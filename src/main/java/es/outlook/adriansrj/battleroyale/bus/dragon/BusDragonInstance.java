package es.outlook.adriansrj.battleroyale.bus.dragon;

import es.outlook.adriansrj.battleroyale.bus.BusInstanceBase;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.event.player.PlayerJumpOffBusEvent;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.packet.sender.PacketSenderService;
import es.outlook.adriansrj.battleroyale.util.PluginUtil;
import es.outlook.adriansrj.battleroyale.util.VehicleUtil;
import es.outlook.adriansrj.battleroyale.util.packet.interceptor.entity.PacketEntityTeleportInterceptorProtocolLib;
import es.outlook.adriansrj.battleroyale.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.entity.EntityUtil;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A battle royale bus that carries an unlimited number of players.
 *
 * @author AdrianSR / 08/09/2021 / 05:36 p. m.
 */
public final class BusDragonInstance extends BusInstanceBase < BusDragon > implements Listener {
	
	private final Map < UUID, ArmorStand > seat_map = new ConcurrentHashMap <> ( );
	private final Stack < UUID >           queue    = new Stack <> ( );
	
	// bus dragon shape
	private       EnderDragon          shape;
	// last location of the seat in the server
	private final Map < UUID, Vector > last_locations_server = new ConcurrentHashMap <> ( );
	
	BusDragonInstance ( BusDragon configuration ) {
		super ( configuration );
		
		// registering packet interceptor
		if ( PluginUtil.isProtocolLibEnabled ( ) ) {
			final PacketEntityTeleportInterceptorProtocolLib interceptor = new PacketEntityTeleportInterceptorProtocolLib ( );
			
			interceptor.register ( );
			interceptor.registerAcceptor ( packet -> {
				int entity_id = VehicleUtil.getEntityId ( packet );
				
				if ( seat_map.values ( ).stream ( )
						.anyMatch ( entity -> entity_id == EntityReflection.getEntityID ( entity ) ) ) {
					boolean from_server = true;
					
					// checking packet comes from PacketSenderService, otherwise will be cancelled.
					for ( StackTraceElement element : Thread.currentThread ( ).getStackTrace ( ) ) {
						if ( element.getClassName ( ).equals ( PacketSenderService.class.getName ( ) ) ) {
							from_server = false;
							break;
						}
					}
					
					return from_server;
				} else {
					return false;
				}
			} );
		}
	}
	
	public Set < Player > getPlayers ( ) {
		Set < Player > result = seat_map.keySet ( ).stream ( ).map ( Player :: getPlayer )
				.collect ( Collectors.toSet ( ) );
		
		// including players in queue
		queue.stream ( ).map ( Player :: getPlayer )
				.filter ( Objects :: nonNull ).forEach ( result :: add );
		
		return result;
	}
	
	@Override
	public boolean isPassenger ( Player player ) {
		ArmorStand seat = seat_map.get ( player.getUniqueId ( ) );
		
		return ( seat != null && seat.isValid ( ) ) || queue.contains ( player.getUniqueId ( ) );
	}
	
	protected void putPlayer ( org.bukkit.entity.Player player ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			// player must be in the world of the arena, otherwise
			// we will need to teleport them, and wait until the teleportation
			// process is finished to actually put the player on the dragon.
			if ( Objects.equals ( player.getWorld ( ) , arena.getWorld ( ) ) ) {
				Bukkit.getScheduler ( ).scheduleSyncDelayedTask (
						BattleRoyale.getInstance ( ) , ( ) -> putPlayer0 ( player ) );
			} else {
				if ( !queue.contains ( player.getUniqueId ( ) ) ) {
					queue.add ( player.getUniqueId ( ) );
				}
				
				player.teleport ( getLocation ( ).toLocation ( arena.getWorld ( ) ) );
			}
		} else {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> putPlayer ( player ) );
		}
	}
	
	// event handler responsible for putting players on their respective seat
	// when they are done changing of world.
	@EventHandler ( priority = EventPriority.LOWEST, ignoreCancelled = false )
	public void onEnterWorld ( PlayerChangedWorldEvent event ) {
		org.bukkit.entity.Player player = event.getPlayer ( );
		
		// then putting
		if ( Objects.equals ( player.getWorld ( ).getWorldFolder ( ) , arena.getWorld ( ).getWorldFolder ( ) )
				&& queue.remove ( player.getUniqueId ( ) ) ) {
			Bukkit.getScheduler ( ).scheduleSyncDelayedTask (
					BattleRoyale.getInstance ( ) , ( ) -> putPlayer0 ( player ) );
		}
	}
	
	private void putPlayer0 ( org.bukkit.entity.Player player ) {
		ArmorStand seat = arena.getWorld ( ).spawn ( getLocation ( ).toLocation (
				arena.getWorld ( ) , spawn.getYaw ( ) , 0.0F ) , ArmorStand.class );
		
		seat.setGravity ( false );
		seat.setSmall ( false );
		seat.setBasePlate ( false );
		seat.setRemoveWhenFarAway ( false );
		seat.setVisible ( false );
		
		// as the player is going to spawn in the bus
		// flying should be enabled.
		if ( player.getGameMode ( ) != GameMode.CREATIVE ) {
			player.setAllowFlight ( true );
			player.setFlying ( true );
		}
		
		// hiding player for others
		arena.getPlayers ( false ).stream ( ).map ( Player :: getBukkitPlayer ).filter (
				Objects :: nonNull ).forEach ( other -> {
			try {
				other.hidePlayer ( BattleRoyale.getInstance ( ) , player );
			} catch ( NoSuchMethodError ex ) {
				// legacy versions
				other.hidePlayer ( player );
			}
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
		
		seat_map.put ( player.getUniqueId ( ) , seat );
		
		// showing dragon
		SchedulerUtil.scheduleSyncDelayedTask ( ( ) -> {
			PacketSenderService packet_service = PacketSenderService.getInstance ( );
			
			packet_service.sendSpawnEntityPacket ( player , shape );
			packet_service.sendEntityMetadataPacket ( player , shape );
		} , 30L );
	}
	
	@Override
	protected void start ( ) {
		super.start ( );
		
		// we must listen for PlayerChangeWorldEvent
		Bukkit.getPluginManager ( ).registerEvents ( this , BattleRoyale.getInstance ( ) );
		
		// spawning shape
		PacketSenderService packet_service = PacketSenderService.getInstance ( );
		
		this.shape = ( EnderDragon ) packet_service.spawnEntity (
				EntityType.ENDER_DRAGON , getLocation ( ).getX ( ) , getLocation ( ).getY ( ) ,
				getLocation ( ).getZ ( ) , spawn.getYaw ( ) + 180.0F /* inverted yaw */ , 0.0F , entity -> {
					EntityReflection.setInvulnerable ( entity , true );
					EntityReflection.setSilent ( entity , true );
				} );
		
		// putting players
		arena.getPlayers ( false ).stream ( ).filter ( Player :: hasTeam ).map (
				Player :: getBukkitPlayer ).filter ( Objects :: nonNull ).forEach ( this :: putPlayer );
	}
	
	@Override
	protected synchronized void displace ( Vector location ) {
		PacketSenderService packet_service = PacketSenderService.getInstance ( );
		
		// displacing players
		displacePlayers ( location.clone ( ).add ( new Vector ( 0.0D , 2.0D , 0.0D ) ) );
		
		// displacing dragon
		arena.getPlayers ( ).stream ( ).map ( Player :: getBukkitPlayer ).filter (
				Objects :: nonNull ).forEach ( player -> {
			// dragon yaw seems to be inverted
			packet_service.sendEntityTeleportPacket (
					player , EntityReflection.getEntityID ( shape ) , false ,
					location.getX ( ) , location.getY ( ) , location.getZ ( ) ,
					spawn.getYaw ( ) + 180.0F , 0.0F );
		} );
	}
	
	private void displacePlayers ( Vector location ) {
		PacketSenderService packet_service = PacketSenderService.getInstance ( );
		Set < UUID >        dispose        = new HashSet <> ( );
		
		for ( Map.Entry < UUID, ArmorStand > entry : seat_map.entrySet ( ) ) {
			org.bukkit.entity.Player player = Bukkit.getPlayer ( entry.getKey ( ) );
			
			if ( player != null && player.isOnline ( ) ) {
				ArmorStand seat = entry.getValue ( );
				
				if ( seat != null ) {
					// location packet
					packet_service.sendEntityTeleportPacket (
							player , EntityReflection.getEntityID ( seat ) , false ,
							location.getX ( ) , location.getY ( ) , location.getZ ( ) ,
							spawn.getYaw ( ) , 0.0F );
					
					// updating last location of the player in the server
					EntityReflection.directLocationUpdate ( player , location );
					
					// updating last location of the seat in the server
					Vector last_location_server = last_locations_server.get ( entry.getKey ( ) );
					
					if ( last_location_server == null || location.distance ( last_location_server ) >= 10.0D ) {
						EntityReflection.directLocationUpdate ( seat , last_location_server = location );
						
						// mapping
						last_locations_server.put ( entry.getKey ( ) , last_location_server );
					}
				} else {
					dispose.add ( entry.getKey ( ) );
				}
			} else {
				dispose.add ( entry.getKey ( ) );
			}
		}
		
		dispose.forEach ( seat_map :: remove );
	}
	
	@Override
	protected void jumpTutorial ( ) {
		for ( Map.Entry < UUID, ArmorStand > entry : seat_map.entrySet ( ) ) {
			Player player = Player.getPlayer ( entry.getKey ( ) );
			
			if ( player != null ) {
				player.sendTitle ( EnumLanguage.BUS_JUMP_TITLE.getAsString ( ) ,
								   EnumLanguage.BUS_JUMP_SUBTITLE.getAsString ( ) ,
								   0 , 10 , 0 );
			}
		}
	}
	
	public synchronized void ejectPlayer ( Player br_player ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			queue.remove ( br_player.getUniqueId ( ) );
			
			/* disposing seat */
			ArmorStand seat = seat_map.remove ( br_player.getUniqueId ( ) );
			
			if ( seat != null ) {
				// we will update the last location in the server
				EntityReflection.setLocation (
						seat , location.getX ( ) , location.getY ( ) , location.getZ ( ) ,
						spawn.getYaw ( ) , 0.0F );
				
				// then disposing
				seat.eject ( );
				seat.remove ( );
			}
			
			br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
				// we will update the last location in the server
				EntityReflection.setLocation (
						player , location.getX ( ) , location.getY ( ) , location.getZ ( ) ,
						player.getLocation ( ).getYaw ( ) , player.getLocation ( ).getPitch ( ) );
				
				// disabling fly
				if ( player.getGameMode ( ) != GameMode.CREATIVE ) {
					player.setAllowFlight ( false );
					player.setFlying ( false );
				}
				
				// showing player for others
				arena.getPlayers ( false ).stream ( ).map ( Player :: getBukkitPlayer ).filter (
						Objects :: nonNull ).forEach ( other -> {
					try {
						other.showPlayer ( BattleRoyale.getInstance ( ) , player );
					} catch ( NoSuchMethodError ex ) {
						// legacy versions
						other.showPlayer ( player );
					}
				} );
			} );
			
			// firing event
			new PlayerJumpOffBusEvent ( br_player , this ).call ( );
		} else {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> ejectPlayer ( br_player ) );
		}
	}
	
	private void ejectPlayers ( ) {
		seat_map.keySet ( ).stream ( ).map ( Player :: getPlayer ).forEach ( this :: ejectPlayer );
	}
	
	@Override
	public synchronized void finish ( ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			// ejecting
			this.ejectPlayers ( );
			
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
		// ejecting
		this.ejectPlayers ( );
		
		super.restart ( );
		this.dispose ( );
	}
	
	private void dispose ( ) {
		HandlerList.unregisterAll ( this );
		
		if ( shape != null ) {
			Bukkit.getOnlinePlayers ( ).forEach (
					player -> PacketSenderService.getInstance ( ).sendDestroyEntityPacket ( player , shape ) );
			
			shape = null;
		}
		
		seat_map.values ( ).stream ( ).filter ( Objects :: nonNull ).forEach ( Entity :: remove );
		seat_map.clear ( );
		last_locations_server.clear ( );
	}
}