package es.outlook.adriansrj.battleroyale.bus.dragon;

import es.outlook.adriansrj.battleroyale.bus.BusInstanceBase;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.event.player.PlayerJumpOffBusEvent;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.entity.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.potion.PotionEffect;
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
	
	private static final double DRAGON_HEIGHT = 2.85D;
	
	private final Map < UUID, Chicken > seat_map = new ConcurrentHashMap <> ( );
	private final Stack < UUID >        queue    = new Stack <> ( );
	
	private Vector      velocity;
	// bus dragon shape
	private EnderDragon shape;
	private Chicken     shape_seat;
	
	BusDragonInstance ( BusDragon configuration ) {
		super ( configuration );
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
		Chicken seat = seat_map.get ( player.getUniqueId ( ) );
		
		return ( seat != null && seat.isValid ( ) ) || queue.contains ( player.getUniqueId ( ) );
	}
	
	private void putPlayer ( org.bukkit.entity.Player player ) {
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
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onEnterWorld ( PlayerChangedWorldEvent event ) {
		org.bukkit.entity.Player player = event.getPlayer ( );
		
		// then putting
		if ( queue.remove ( player.getUniqueId ( ) ) ) {
			Bukkit.getScheduler ( ).scheduleSyncDelayedTask (
					BattleRoyale.getInstance ( ) , ( ) -> putPlayer0 ( player ) );
		}
	}
	
	// avoid any entity of this bus to be damaged.
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onDamage ( EntityDamageEvent event ) {
		Entity entity = event.getEntity ( );
		
		if ( Objects.equals ( entity , shape ) || Objects.equals ( entity , shape_seat )
				|| seat_map.values ( ).stream ( ).anyMatch ( seat -> Objects.equals ( seat , entity ) ) ) {
			event.setCancelled ( true );
		}
	}
	
	// avoid the dragon from damaging other entities.
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onDragonAttack ( EntityDamageByEntityEvent event ) {
		if ( Objects.equals ( event.getDamager ( ) , shape ) ) {
			event.setCancelled ( true );
		}
	}
	
	@SuppressWarnings ( "deprecation" )
	private void putPlayer0 ( org.bukkit.entity.Player player ) {
		// as the player is going to spawn in the bus
		// flying should be enabled.
		player.setAllowFlight ( true );
		player.setFlying ( true );
		
		Chicken seat = arena.getWorld ( ).spawn (
				shape_seat.getLocation ( ).clone ( ).add ( 0.0D , DRAGON_HEIGHT , 0.0D ) ,
				Chicken.class );
		
		seat.setRemoveWhenFarAway ( false );
		seat.setAdult ( );
		seat.addPotionEffect ( new PotionEffect (
				PotionEffectType.INVISIBILITY , Integer.MAX_VALUE , 0 ) );
		
		try {
			seat.addPassenger ( player );
		} catch ( NoSuchMethodError ex ) {
			// legacy versions
			seat.setPassenger ( player );
		}
		
		seat_map.put ( player.getUniqueId ( ) , seat );
		
		// hiding player for others
		arena.getPlayers ( false ).stream ( ).map ( Player :: getBukkitPlayer )
				.filter ( Objects :: nonNull )
				.filter ( other -> !Objects.equals ( other.getUniqueId ( ) , player.getUniqueId ( ) )
						&& other.canSee ( player ) )
				.forEach ( other -> Player.getPlayer ( other ).hidePlayer ( player ) );
		
		// blindness effect
		EntityUtil.addPotionEffectForcing (
				player , PotionEffectType.SLOW , Duration.ofSeconds ( 3 ) , 10 );
		EntityUtil.addPotionEffectForcing (
				player , PotionEffectType.BLINDNESS , Duration.ofSeconds ( 3 ) , 0 );
	}
	
	@SuppressWarnings ( "deprecation" )
	@Override
	protected void start ( ) {
		super.start ( );
		
		// velocity can be calculated only once.
		velocity = direction.clone ( ).multiply ( spawn.getSpeed ( ) );
		
		// we must listen for PlayerChangeWorldEvent
		Bukkit.getPluginManager ( ).registerEvents ( this , BattleRoyale.getInstance ( ) );
		
		// putting players
		arena.getPlayers ( false ).stream ( ).filter ( Player :: hasTeam ).map (
				Player :: getBukkitPlayer ).filter ( Objects :: nonNull ).forEach ( this :: putPlayer );
		
		// spawning dragon
		shape_seat = arena.getWorld ( ).spawn ( getLocation ( ).toLocation (
				arena.getWorld ( ) , spawn.getYaw ( ) , 0.0F ) , Chicken.class );
		shape_seat.setRemoveWhenFarAway ( false );
		shape_seat.setAdult ( );
		shape_seat.addPotionEffect ( new PotionEffect (
				PotionEffectType.INVISIBILITY , Integer.MAX_VALUE , 0 ) );
		
		shape = arena.getWorld ( ).spawn ( getLocation ( ).toLocation (
				arena.getWorld ( ) , spawn.getYaw ( ) + 180.0F , 0.0F ) , EnderDragon.class );
		shape.setRemoveWhenFarAway ( false );
		
		try {
			shape_seat.addPassenger ( shape );
		} catch ( NoSuchMethodError ex ) {
			// legacy versions
			shape_seat.setPassenger ( shape );
		}
	}
	
	@Override
	protected void lifeLoop ( ) {
		// updating location
		location.add ( velocity );
		
		// displacing dragon
		shape_seat.setVelocity ( velocity );
		
		// displacing seats
		Iterator < Map.Entry < UUID, Chicken > > iterator = seat_map.entrySet ( ).iterator ( );
		
		while ( iterator.hasNext ( ) ) {
			Map.Entry < UUID, Chicken > entry  = iterator.next ( );
			org.bukkit.entity.Player    player = Bukkit.getPlayer ( entry.getKey ( ) );
			Chicken                     seat   = entry.getValue ( );
			
			if ( player != null && player.isOnline ( ) && seat != null ) {
				if ( seat.getLocation ( ).distance ( shape_seat.getLocation ( ) ) < 1.0D ) {
					seat.setVelocity ( velocity );
				} else {
					EntityReflection.setPositionDirty ( seat , shape_seat.getLocation ( ).add (
							0.0D , DRAGON_HEIGHT , 0.0D ).toVector ( ) );
				}
			} else {
				iterator.remove ( );
				
				// disposing seat
				if ( seat != null ) {
					seat.eject ( );
					seat.remove ( );
				}
			}
		}
		
		// then life loop
		super.lifeLoop ( );
	}
	
	@Override
	protected void jumpTutorial ( ) {
		for ( Map.Entry < UUID, Chicken > entry : seat_map.entrySet ( ) ) {
			Player player = Player.getPlayer ( entry.getKey ( ) );
			
			if ( player != null ) {
				player.sendTitle ( EnumLanguage.BUS_JUMP_TITLE.getAsString ( ) ,
								   EnumLanguage.BUS_JUMP_SUBTITLE.getAsString ( ) ,
								   0 , 10 , 0 );
			}
		}
	}
	
	@SuppressWarnings ( "deprecation" )
	public synchronized void ejectPlayer ( Player br_player ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			queue.remove ( br_player.getUniqueId ( ) );
			
			// then disposing
			Chicken seat = seat_map.remove ( br_player.getUniqueId ( ) );
			
			if ( seat != null ) {
				seat.eject ( );
				seat.remove ( );
			}
			
			br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
				// we will update the last location in the server
				EntityReflection.setLocation (
						player , location.getX ( ) , location.getY ( ) , location.getZ ( ) ,
						player.getLocation ( ).getYaw ( ) , player.getLocation ( ).getPitch ( ) );
				
				// disabling fly
				player.setAllowFlight ( player.getGameMode ( ) == GameMode.CREATIVE );
				player.setFlying ( false );
				
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
		
		if ( shape_seat != null ) {
			shape_seat.remove ( );
			shape_seat = null;
		}
		
		if ( shape != null ) {
			shape.remove ( );
			shape = null;
		}
		
		seat_map.values ( ).stream ( ).filter ( Objects :: nonNull ).forEach ( Entity :: remove );
		seat_map.clear ( );
	}
}