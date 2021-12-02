package es.outlook.adriansrj.battleroyale.bus.test;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.packet.reader.PacketReaderService;
import es.outlook.adriansrj.battleroyale.packet.sender.PacketSenderService;
import es.outlook.adriansrj.battleroyale.packet.wrapper.out.PacketOutEntityRelativeMove;
import es.outlook.adriansrj.battleroyale.packet.wrapper.out.PacketOutEntityRelativeMoveLook;
import es.outlook.adriansrj.battleroyale.packet.wrapper.out.PacketOutEntityTeleport;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.packet.interceptor.entity.PacketEntityRelativeMoveInterceptorSimple;
import es.outlook.adriansrj.battleroyale.util.packet.interceptor.entity.PacketEntityTeleportInterceptorSimple;
import es.outlook.adriansrj.battleroyale.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.entity.EntityUtil;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * {@link BusPetTest} instance.
 *
 * @author AdrianSR / 23/09/2021 / 09:01 p. m.
 * @see BusPetTest
 */
public class BusPetInstanceTest extends BusInstanceBaseTest < BusPetTest > implements Listener {
	
	protected static final ConcurrentLinkedQueue < BusPetInstanceTest > INSTANCES = new ConcurrentLinkedQueue <> ( );
	
	static {
		// packet interceptors
		// order is important
		PacketEntityTeleportInterceptorSimple     tp_interceptor = new PacketEntityTeleportInterceptorSimple ( );
		PacketEntityRelativeMoveInterceptorSimple mv_interceptor = new PacketEntityRelativeMoveInterceptorSimple ( );
		
		tp_interceptor.register ( );
		mv_interceptor.register ( );
		
		// the system in server versions 1.9 - 1.11 is the following:
		// the seat location will be set through reflection with the
		// setLocation method, which will trigger teleport/relative-move
		// packets that we will intercept; a copy of each intercepted
		// packet will be created, and the id of the shape will be
		// set in the packet copies. in other words, the outgoing
		// packets that teleport/moves the seat will also apply for
		// the shape.
		tp_interceptor.registerAcceptor ( packet -> {
			PacketOutEntityTeleport wrapper = PacketReaderService.getInstance ( )
					.readEntityTeleportPacket ( packet );
			BusPetInstanceTest instance = INSTANCES.stream ( ).filter ( bus -> bus.seat != null
							&& wrapper.getEntityId ( ) == bus.seat.getEntityId ( ) )
					.findAny ( ).orElse ( null );
			
			if ( instance != null ) {
				wrapper.setEntityId ( instance.shape.getEntityId ( ) );
				
				// fixing y
				Vector location = wrapper.getLocation ( );
				
				location.setY ( location.getY ( ) + ( instance.seat.getEyeHeight ( ) -
						EntityReflection.getHeight ( instance.shape ) ) );
				wrapper.setLocation ( location );
				
				// the sending
				instance.arena.getPlayers ( true ).stream ( )
						.map ( Player :: getBukkitPlayerOptional )
						.filter ( Optional :: isPresent )
						.map ( Optional :: get ).forEach ( wrapper :: send );
			}
			return false;
		} );
		
		mv_interceptor.registerAcceptor ( packet -> {
			// move
			if ( Objects.equals ( packet.getClass ( ) , Constants.PACKET_OUT_ENTITY_RELATIVE_MOVE_CLASS ) ) {
				PacketOutEntityRelativeMove wrapper = PacketReaderService.getInstance ( )
						.readEntityRelativeMovePacket ( packet );
				BusPetInstanceTest instance = INSTANCES.stream ( ).filter ( bus -> bus.seat != null
								&& wrapper.getEntityId ( ) == bus.seat.getEntityId ( ) )
						.findAny ( ).orElse ( null );
				
				if ( instance != null ) {
					wrapper.setEntityId ( instance.shape.getEntityId ( ) );
					wrapper.setDeltaY ( 0 ); // must not move in y
					
					// the sending
					instance.arena.getPlayers ( true ).stream ( )
							.map ( Player :: getBukkitPlayerOptional )
							.filter ( Optional :: isPresent )
							.map ( Optional :: get ).forEach ( wrapper :: send );
				}
			}
			
			// move and look
			if ( Objects.equals ( packet.getClass ( ) , Constants.PACKET_OUT_ENTITY_RELATIVE_MOVE_LOOK_CLASS ) ) {
				PacketOutEntityRelativeMoveLook wrapper = PacketReaderService.getInstance ( )
						.readEntityRelativeMoveLookPacket ( packet );
				BusPetInstanceTest instance = INSTANCES.stream ( ).filter ( bus -> bus.seat != null
								&& wrapper.getEntityId ( ) == bus.seat.getEntityId ( ) )
						.findAny ( ).orElse ( null );
				
				if ( instance != null ) {
					wrapper.setEntityId ( instance.shape.getEntityId ( ) );
					wrapper.setDeltaY ( 0 ); // must not move in y
					
					// the sending
					instance.arena.getPlayers ( true ).stream ( )
							.map ( Player :: getBukkitPlayerOptional )
							.filter ( Optional :: isPresent )
							.map ( Optional :: get ).forEach ( wrapper :: send );
				}
			}
			
			return false;
		} );
	}
	
	protected final BattleRoyaleArena arena;
	protected final Location          spawn;
	protected       ArmorStand        seat;
	protected       Entity            shape;
	protected       boolean           in_queue;
	protected       PigZombie         passenger;
	
	protected BusPetInstanceTest ( BusPetTest configuration , BattleRoyaleArena arena , Location spawn ) {
		super ( configuration );
		
		Validate.notNull ( configuration.getShape ( ) , "configuration returned a null shape" );
		
		this.arena = arena;
		this.spawn = spawn;
		
		// registering
		INSTANCES.add ( this );
	}
	
	@Override
	public boolean isPassenger ( Player player ) {
		return false;
	}
	
	@Override
	public void start ( ) {
		super.start ( );
		
		// updating and relocating shape
		EntityReflection.setLocation ( updateShape ( ) , location );
		
		// we must listen for PlayerChangeWorldEvent
		Bukkit.getPluginManager ( ).registerEvents (
				this , BattleRoyale.getInstance ( ) );
		
		// putting passenger
		this.putPassenger ( );
	}
	
	protected void putPassenger ( ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			passenger = arena.getWorld ( ).spawn ( spawn , PigZombie.class );
			passenger.setRemoveWhenFarAway ( false );
			
			// as the displacement task is constantly
			// running even when the player is not in
			// the seat, we have to make sure that the
			// finish() method will not be called before
			// even spawning the seat.
			in_queue = true;
			
			Bukkit.getScheduler ( ).scheduleSyncDelayedTask (
					BattleRoyale.getInstance ( ) , ( ) -> putPassenger0 ( passenger ) );
		} else {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , this :: putPassenger );
		}
	}
	
	@SuppressWarnings ( "deprecation" )
	protected void putPassenger0 ( PigZombie passenger ) {
		seat = arena.getWorld ( ).spawn ( getLocation ( ).toLocation (
				arena.getWorld ( ) , spawn.getYaw ( ) , 0.0F ) , ArmorStand.class );
		
		seat.setGravity ( false );
		seat.setSmall ( false );
		seat.setBasePlate ( false );
		seat.setRemoveWhenFarAway ( false );
		seat.setVisible ( false );
		
		// seat spawned, we can now
		// disable the queue flag
		this.in_queue = false;
		
		//		// as the player is going to spawn in the bus
		//		// flying should be enabled.
		//		if ( player.getGameMode ( ) != GameMode.CREATIVE ) {
		//			player.setAllowFlight ( true );
		//			player.setFlying ( true );
		//		}
		
		// hiding player for others
		//		arena.getPlayers ( false ).stream ( ).map ( Player :: getBukkitPlayer ).filter (
		//				Objects :: nonNull ).forEach (
		//				other -> Player.getPlayer ( other ).hidePlayer ( player ) );
		
		try {
			seat.addPassenger ( passenger );
		} catch ( NoSuchMethodError ex ) {
			// legacy versions
			seat.setPassenger ( passenger );
		}
		
		// blindness effect
		EntityUtil.addPotionEffectForcing (
				passenger , PotionEffectType.SLOW , Duration.ofSeconds ( 3 ) , 10 );
		EntityUtil.addPotionEffectForcing (
				passenger , PotionEffectType.BLINDNESS , Duration.ofSeconds ( 3 ) , 0 );
		
		// showing shape
		PacketSenderService packet_service = PacketSenderService.getInstance ( );
		Entity              shape          = updateShape ( );
		
		// shape and seat positions must match at
		// the moment of spawning, otherwise, the
		// movement packets outgoing from the server
		// will cause the shape to be away from the seat.
		Location seat_position = seat.getLocation ( );
		
		EntityReflection.setLocation ( shape , new Vector (
				seat_position.getX ( ) ,
				seat_position.getY ( ) + ( seat.getEyeHeight ( ) - EntityReflection.getHeight ( shape ) ) ,
				seat_position.getZ ( ) ) );
		EntityReflection.setYawPitch ( shape , spawn.getYaw ( ) , 0.0F );
		
		for ( org.bukkit.entity.Player player : Bukkit.getOnlinePlayers ( ) ) {
			packet_service.sendSpawnEntityPacket ( player , shape );
			packet_service.sendEntityMetadataPacket ( player , shape );
		}
	}
	
	protected void eject ( ) {
		System.out.println ( ">>>>> EJECT" );
		
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
			
			if ( passenger != null ) {
				// we will update the last location in the server
				if ( location != null ) {
					EntityReflection.setLocation (
							passenger , location.getX ( ) , location.getY ( ) , location.getZ ( ) ,
							passenger.getLocation ( ).getYaw ( ) , passenger.getLocation ( ).getPitch ( ) );
				}
				
				// disabling fly
				//				if ( player.getGameMode ( ) != GameMode.CREATIVE ) {
				//					player.setAllowFlight ( false );
				//					player.setFlying ( false );
				//				}
				
				// showing player for others
				//				if ( arena != null ) {
				//					arena.getPlayers ( false ).stream ( ).map ( Player :: getBukkitPlayer ).filter (
				//							Objects :: nonNull ).forEach (
				//							other -> Player.getPlayer ( other ).showPlayer ( player ) );
				//				}
			}
			
			// firing event
			//			new PlayerJumpOffBusEvent ( player , this ).call ( );
		} else {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , this :: eject );
		}
	}
	
	@Override
	protected void displace ( Vector location ) {
		if ( passenger != null ) {
			if ( seat != null ) {
				System.out.println ( ">>> displace: SEAT PASSENGERS: " );
				for ( Entity entity : seat.getPassengers ( ) ) {
					System.out.println ( ">>>>>>> - " + entity );
				}
				
				// displacing seat
				if ( Bukkit.isPrimaryThread ( ) ) {
					EntityReflection.setLocation ( seat , location );
					EntityReflection.setYawPitch ( seat , spawn.getYaw ( ) , 0.0F );
				} else {
					Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> {
						EntityReflection.setLocation ( seat , location );
						EntityReflection.setYawPitch ( seat , spawn.getYaw ( ) , 0.0F );
					} );
				}
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
	protected void jumpTutorial ( ) {
		// don't need to check if the player
		// is actually in the bus as the bus
		// will be immediately disposed when
		// the player ejects.
		//		this.player.sendTitle ( EnumLanguage.BUS_JUMP_TITLE.getAsString ( ) ,
		//								EnumLanguage.BUS_JUMP_SUBTITLE.getAsString ( ) ,
		//								0 , 10 , 0 );
	}
	
	@Override
	public synchronized void finish ( ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			this.eject ( );
			
			// then finishing
			super.finish ( );
			this.dispose ( );
		} else {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> {
				// we will have to make sure that it is not already
				// finished as we are switching threads; this can
				// result in a desynchronization problem.
				if ( !isFinished ( ) ) {
					finish ( );
				}
			} );
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
			for ( org.bukkit.entity.Player player : Bukkit.getOnlinePlayers ( ) ) {
				PacketSenderService.getInstance ( ).sendDestroyEntityPacket ( player , shape );
			}
		}
		
		if ( seat != null ) {
			seat.remove ( );
			seat = null;
		}
	}
}
