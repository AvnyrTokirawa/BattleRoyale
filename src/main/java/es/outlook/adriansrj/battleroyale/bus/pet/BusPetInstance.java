package es.outlook.adriansrj.battleroyale.bus.pet;

import es.outlook.adriansrj.battleroyale.bus.BusInstanceBase;
import es.outlook.adriansrj.battleroyale.event.player.PlayerJumpOffBusEvent;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.packet.reader.PacketReaderService;
import es.outlook.adriansrj.battleroyale.packet.sender.PacketSenderService;
import es.outlook.adriansrj.battleroyale.packet.wrapper.out.PacketOutEntityTeleport;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.packet.interceptor.PacketInterceptorAcceptor;
import es.outlook.adriansrj.battleroyale.util.packet.interceptor.PacketInterceptorInjector;
import es.outlook.adriansrj.battleroyale.util.packet.interceptor.entity.PacketEntityRelativeMoveInterceptorSimple;
import es.outlook.adriansrj.battleroyale.util.packet.interceptor.entity.PacketEntityTeleportInterceptorSimple;
import es.outlook.adriansrj.battleroyale.util.packet.reader.PacketReader;
import es.outlook.adriansrj.battleroyale.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.entity.EntityUtil;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * {@link BusPet} instance.
 *
 * @author AdrianSR / 23/09/2021 / 09:01 p. m.
 * @see BusPet
 */
public class BusPetInstance extends BusInstanceBase < BusPet > implements Listener {
	
	protected static final Set < BusPetInstance > INSTANCES = Collections.synchronizedSet ( new HashSet <> ( ) );
	
	static {
		// packet interceptors
		// order is important
		PacketEntityTeleportInterceptorSimple     tp_interceptor = new PacketEntityTeleportInterceptorSimple ( );
		PacketEntityRelativeMoveInterceptorSimple mv_interceptor = new PacketEntityRelativeMoveInterceptorSimple ( );
		
		tp_interceptor.register ( );
		mv_interceptor.register ( );
		
		// this acceptor will cancel the packets coming from the server.
		PacketInterceptorAcceptor canceller = packet -> {
			PacketReader reader = new PacketReader ( packet );
			// first var int is the entity id
			int entity_id = reader.readVarInt ( );
			
			synchronized ( INSTANCES ) {
				if ( INSTANCES.stream ( ).anyMatch ( instance -> instance.seat != null
						&& entity_id == instance.seat.getEntityId ( ) ) ) {
					// coming from the server, we must cancel it; otherwise
					// it would cause client-side flickering.
					return true;
				}
			}
			return false;
		};
		
		tp_interceptor.registerAcceptor ( canceller );
		mv_interceptor.registerAcceptor ( canceller );
		
		// this acceptor will inject the outgoing teleport packet (coming from this class)
		tp_interceptor.registerAcceptor ( ( PacketInterceptorInjector ) packet -> {
			// this class will send teleport packets to teleport
			// the seat; the thing is that the id of the seat is masked,
			// so we can identify if the packet is coming from the server or from this class.
			PacketOutEntityTeleport wrapper = PacketReaderService.getInstance ( )
					.readEntityTeleportPacket ( packet );
			
			synchronized ( INSTANCES ) {
				BusPetInstance instance = INSTANCES.stream ( ).filter ( bus -> bus.seat != null
								&& wrapper.getEntityId ( ) == bus.seat.getEntityId ( ) << 4 )
						.findAny ( ).orElse ( null );
				
				if ( instance != null ) {
					// then we can pass the actual entity id
					wrapper.setEntityId ( instance.seat.getEntityId ( ) );
					
					return wrapper.createInstance ( );
				}
			}
			
			return packet;
		} );
	}
	
	protected final Player     player;
	protected       ArmorStand seat;
	protected       Entity     shape;
	protected       boolean    in_queue;
	
	protected BusPetInstance ( BusPet configuration , Player player ) {
		super ( configuration );
		
		Validate.notNull ( configuration.getShape ( ) , "configuration returned a null shape" );
		Validate.notNull ( player , "player cannot be null" );
		
		this.player = player;
		
		// registering
		synchronized ( INSTANCES ) {
			INSTANCES.add ( this );
		}
	}
	
	public Player getPlayer ( ) {
		return player;
	}
	
	@Override
	public boolean isPassenger ( Player player ) {
		return Objects.equals ( player , this.player );
	}
	
	@Override
	public void start ( ) {
		super.start ( );
		
		// updating and relocating shape
		EntityReflection.setLocation ( updateShape ( ) , location );
		
		// we must listen for PlayerChangeWorldEvent
		Bukkit.getPluginManager ( ).registerEvents (
				this , BattleRoyale.getInstance ( ) );
		
		// putting player
		this.putPlayer ( );
	}
	
	protected void putPlayer ( ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			org.bukkit.entity.Player player = this.player.getBukkitPlayer ( );
			
			if ( player != null ) {
				// as the displacement task is constantly
				// running even when the player is not in
				// the seat, we have to make sure that the
				// finish() method will not be called before
				// even spawning the seat.
				in_queue = true;
				
				// player must be in the world of the arena, otherwise
				// we will need to teleport them, and wait until the teleportation
				// process is finished to actually put the player.
				if ( Objects.equals ( player.getWorld ( ) , arena.getWorld ( ) ) ) {
					Bukkit.getScheduler ( ).scheduleSyncDelayedTask (
							BattleRoyale.getInstance ( ) , ( ) -> putPlayer0 ( player ) );
				} else {
					// changing world
					player.teleport ( getLocation ( ).toLocation ( arena.getWorld ( ) ) );
				}
			}
		} else {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , this :: putPlayer );
		}
	}
	
	// event handler responsible for putting the player on the seat
	// when done changing of world.
	@EventHandler ( priority = EventPriority.LOWEST, ignoreCancelled = false )
	public void onEnterWorld ( PlayerChangedWorldEvent event ) {
		org.bukkit.entity.Player player = event.getPlayer ( );
		
		// then putting
		if ( Objects.equals ( player.getWorld ( ).getWorldFolder ( ) , arena.getWorld ( ).getWorldFolder ( ) )
				&& in_queue && Objects.equals ( player.getUniqueId ( ) , this.player.getUniqueId ( ) ) ) {
			Bukkit.getScheduler ( ).scheduleSyncDelayedTask (
					BattleRoyale.getInstance ( ) , ( ) -> putPlayer0 ( player ) );
		}
	}
	
	protected void putPlayer0 ( org.bukkit.entity.Player player ) {
		seat = arena.getWorld ( ).spawn ( getLocation ( ).toLocation (
				arena.getWorld ( ) , spawn.getYaw ( ) , 0.0F ) , ArmorStand.class );
		
		seat.setGravity ( false );
		seat.setSmall ( false );
		seat.setBasePlate ( false );
		seat.setRemoveWhenFarAway ( false );
		seat.setVisible ( true );
		
		// seat spawned, we can now
		// disable the queue flag
		this.in_queue = false;
		
		// as the player is going to spawn in the bus
		// flying should be enabled.
		if ( player.getGameMode ( ) != GameMode.CREATIVE ) {
			player.setAllowFlight ( true );
			player.setFlying ( true );
		}
		
		// hiding player for others
		arena.getPlayers ( false ).stream ( ).map ( Player :: getBukkitPlayer ).filter (
				Objects :: nonNull ).forEach ( other -> {
			Player.getPlayer ( other ).hidePlayer ( player );
		} );
		
		try {
			seat.addPassenger ( player );
		} catch ( NoSuchMethodError ex ) {
			// legacy versions
			seat.setPassenger ( player );
		}
		
		// blindness effect
		EntityUtil.addPotionEffectForcing (
				player , PotionEffectType.SLOW , Duration.ofSeconds ( 3 ) , 10 );
		EntityUtil.addPotionEffectForcing (
				player , PotionEffectType.BLINDNESS , Duration.ofSeconds ( 3 ) , 0 );
		
		// showing shape
		SchedulerUtil.scheduleSyncDelayedTask ( ( ) -> {
			PacketSenderService packet_service = PacketSenderService.getInstance ( );
			Entity              shape          = updateShape ( );
			
			packet_service.sendSpawnEntityPacket ( player , shape );
			packet_service.sendEntityMetadataPacket ( player , shape );
		} , 20L );
	}
	
	protected void eject ( ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			this.in_queue = false;
			
			// disposing seat
			if ( seat != null ) {
				// we will update the last location in the server
				if ( location != null ) {
					EntityReflection.setLocation (
							seat , location.getX ( ) , location.getY ( ) , location.getZ ( ) ,
							spawn.getYaw ( ) , 0.0F );
				}
				
				// then disposing
				seat.eject ( );
				seat.remove ( );
			}
			
			player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
				// we will update the last location in the server
				if ( location != null ) {
					EntityReflection.setLocation (
							player , location.getX ( ) , location.getY ( ) , location.getZ ( ) ,
							player.getLocation ( ).getYaw ( ) , player.getLocation ( ).getPitch ( ) );
				}
				
				// disabling fly
				if ( player.getGameMode ( ) != GameMode.CREATIVE ) {
					player.setAllowFlight ( false );
					player.setFlying ( false );
				}
				
				// showing player for others
				if ( arena != null ) {
					arena.getPlayers ( false ).stream ( ).map ( Player :: getBukkitPlayer ).filter (
							Objects :: nonNull ).forEach ( other -> {
						Player.getPlayer ( other ).showPlayer ( player );
					} );
				}
			} );
			
			// firing event
			new PlayerJumpOffBusEvent ( player , this ).call ( );
		} else {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , this :: eject );
		}
	}
	
	@Override
	protected void displace ( Vector location ) {
		org.bukkit.entity.Player player = this.player.getBukkitPlayer ( );
		
		if ( player != null && player.isOnline ( ) ) {
			if ( seat != null ) {
				// displacing seat
				PacketSenderService.getInstance ( ).sendEntityTeleportPacket (
						player ,
						// masking id
						( seat.getEntityId ( ) << 4 ) ,
						false ,
						location.getX ( ) ,
						location.getY ( ) ,
						location.getZ ( ) ,
						spawn.getYaw ( ) ,
						0
				);
				
				EntityReflection.setPositionDirty ( seat , location );
				
				// displacing shape
				PacketSenderService.getInstance ( ).sendEntityTeleportPacket (
						player ,
						shape.getEntityId ( ) ,
						false ,
						location.getX ( ) ,
						// including shape height
						location.getY ( ) + ( seat.getEyeHeight ( ) - shape.getHeight ( ) ) ,
						location.getZ ( ) ,
						spawn.getYaw ( ) ,
						0
				);
			} else {
				// it seems that seat was
				// removed for any reason.
				if ( !in_queue ) {
					this.finish ( );
				}
			}
		} else {
			// player seems to be offline.
			this.finish ( );
		}
	}
	
	@Override
	public synchronized void finish ( ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			this.eject ( );
			
			// then finishing
			super.finish ( );
			this.dispose ( );
		} else {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , this :: finish );
		}
	}
	
	@Override
	public synchronized void restart ( ) {
		this.eject ( );
		super.restart ( );
		this.dispose ( );
	}
	
	protected Entity updateShape ( ) {
		// spawning shape
		if ( shape == null ) {
			this.shape = PacketSenderService.getInstance ( ).spawnEntity (
					configuration.getShape ( ) ,
					getLocation ( ).getX ( ) ,
					getLocation ( ).getY ( ) ,
					getLocation ( ).getZ ( ) , spawn.getYaw ( ) , 0.0F , entity -> {
						EntityReflection.setInvulnerable ( entity , true );
						EntityReflection.setSilent ( entity , true );
					} );
		}
		
		return shape;
	}
	
	protected void dispose ( ) {
		HandlerList.unregisterAll ( this );
		
		// de-spawning shape
		if ( shape != null ) {
			player.getBukkitPlayerOptional ( ).ifPresent ( player -> PacketSenderService.getInstance ( )
					.sendDestroyEntityPacket ( player , shape ) );
		}
		
		if ( seat != null ) {
			seat.remove ( );
			seat = null;
		}
	}
}
