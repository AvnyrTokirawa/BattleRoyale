package es.outlook.adriansrj.battleroyale.bus.dragon;

import es.outlook.adriansrj.battleroyale.bus.BusInstanceBase;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.event.player.PlayerJumpOffBusEvent;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.entity.EntityUtil;
import es.outlook.adriansrj.core.util.math.Vector2D;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
	
	//	private static final double DRAGON_HEIGHT = 2.85D;
	//	private static final double DRAGON_HEIGHT = 1.0D;
	
	private final Map < UUID, Chicken > seat_map = new ConcurrentHashMap <> ( );
	private final Stack < UUID >        queue    = new Stack <> ( );
	
	private Vector      start_location;
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
		syncCheck ( );
		
		// player must be in the world of the arena, otherwise
		// we will need to teleport them, and wait until the teleportation
		// process is finished to actually put the player on the dragon.
		if ( Objects.equals ( player.getWorld ( ) , arena.getWorld ( ) ) ) {
			Bukkit.getScheduler ( ).scheduleSyncDelayedTask (
					BattleRoyale.getInstance ( ) , ( ) -> putPlayer0 ( player ) );
		} else {
			// adding to put queue
			if ( !queue.contains ( player.getUniqueId ( ) ) ) {
				queue.add ( player.getUniqueId ( ) );
			}
			
			// making sure chunk is loaded
			Location location = getLocation ( ).toLocation (
					arena.getWorld ( ) , getSpawn ( ).getYaw ( ) , 0.0F );
			Chunk chunk = location.getChunk ( );
			
			if ( !chunk.isLoaded ( ) ) {
				chunk.load ( true );
			}
			
			// changing world
			player.teleport ( location );
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
					BattleRoyale.getInstance ( ) , ( ) -> putPlayer0 ( player ) , 2L );
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
		
		//		Chicken seat = arena.getWorld ( ).spawn (
		//				shape_seat.getLocation ( ).clone ( ).add ( 0.0D , DRAGON_HEIGHT , 0.0D ) ,
		//				Chicken.class );
		
		Chicken seat = arena.getWorld ( ).spawn ( getLocation ( ).toLocation (
				arena.getWorld ( ) , spawn.getYaw ( ) , 0.0F ) , Chicken.class );
		
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
		syncCheck ( );
		super.start ( );
		
		start_location = location.clone ( );
		// velocity can be calculated only once.
		velocity = direction.clone ( ).multiply ( spawn.getSpeed ( ) ).setY ( 0.0D );
		
		// we must listen for PlayerChangeWorldEvent
		Bukkit.getPluginManager ( ).registerEvents (
				this , BattleRoyale.getInstance ( ) );
		
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
			shape_seat.setCollidable ( false );
			shape.setCollidable ( false );
		} catch ( NoSuchMethodError ex ) {
			// legacy version
		}
		
		try {
			shape_seat.addPassenger ( shape );
		} catch ( NoSuchMethodError ex ) {
			// legacy versions
			shape_seat.setPassenger ( shape );
		}
		
		// putting players
		arena.getPlayers ( false ).stream ( ).filter ( Player :: hasTeam ).map (
				Player :: getBukkitPlayer ).filter ( Objects :: nonNull ).forEach ( this :: putPlayer );
	}
	
	@Override
	protected void lifeLoop ( ) {
		syncCheck ( );
		
		// updating location
		location.add ( velocity );
		
		// displacing seats
		Iterator < Map.Entry < UUID, Chicken > > iterator = seat_map.entrySet ( ).iterator ( );
		
		while ( iterator.hasNext ( ) ) {
			Map.Entry < UUID, Chicken > entry  = iterator.next ( );
			org.bukkit.entity.Player    player = Bukkit.getPlayer ( entry.getKey ( ) );
			Chicken                     seat   = entry.getValue ( );
			
			if ( player != null && player.isOnline ( ) && seat != null ) {
				Location seat_location       = seat.getLocation ( );
				Location shape_seat_location = shape_seat.getLocation ( );
				double distance = new Vector2D ( seat_location.getX ( ) , seat_location.getZ ( ) ).distance (
						new Vector2D ( shape_seat_location.getX ( ) , shape_seat_location.getZ ( ) ) );
				
				Vector velocity = this.velocity.clone ( );
				
				if ( distance > 0.5D ) {
					if ( seat_location.toVector ( ).distance ( start_location )
							< shape_seat_location.toVector ( ).distance ( start_location ) ) {
						velocity = direction.clone ( ).multiply ( spawn.getSpeed ( ) + 0.05D );
					} else {
						velocity = direction.clone ( ).multiply ( spawn.getSpeed ( ) / 2.0D );
					}
				}
				
				seat.setVelocity ( velocity.setY ( 0.0D ) );
			} else {
				iterator.remove ( );
				
				// disposing seat
				if ( seat != null ) {
					seat.eject ( );
					seat.remove ( );
				}
				
				// showing player for others
				if ( player != null ) {
					arena.getPlayers ( false ).stream ( ).map ( Player :: getBukkitPlayer )
							.filter ( Objects :: nonNull )
							.filter ( other -> !Objects.equals ( player.getUniqueId ( ) , other.getUniqueId ( ) ) )
							.forEach ( other -> Player.getPlayer ( other ).showPlayer ( player ) );
				}
			}
		}
		
		// displacing dragon
		shape_seat.setVelocity ( velocity );
		
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
	
	public synchronized void ejectPlayer ( Player br_player ) {
		syncCheck ( );
		
		// player is probably in the put queue
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
		} );
		
		// showing player for others
		br_player.getBukkitPlayerOptional ( ).ifPresent (
				player -> arena.getPlayers ( false ).stream ( ).map ( Player :: getBukkitPlayer )
						.filter ( Objects :: nonNull )
						.filter ( other -> !Objects.equals ( player.getUniqueId ( ) , other.getUniqueId ( ) ) )
						.forEach ( other -> Player.getPlayer ( other ).showPlayer ( player ) ) );
		
		// firing event
		new PlayerJumpOffBusEvent ( br_player , this ).call ( );
	}
	
	private void ejectPlayers ( ) {
		getPlayers ( ).forEach ( this :: ejectPlayer );
	}
	
	@Override
	public synchronized void finish ( ) {
		syncCheck ( );
		
		this.ejectPlayers ( );
		super.finish ( );
		this.dispose ( );
	}
	
	@Override
	public synchronized void restart ( ) {
		syncCheck ( );
		
		this.ejectPlayers ( );
		super.restart ( );
		this.dispose ( );
	}
	
	private void dispose ( ) {
		syncCheck ( );
		HandlerList.unregisterAll ( this );
		
		if ( shape_seat != null ) {
			shape_seat.remove ( );
			shape_seat = null;
		}
		
		if ( shape != null ) {
			shape.remove ( );
			shape = null;
		}
		
		queue.clear ( );
		seat_map.values ( ).stream ( ).filter ( Objects :: nonNull ).forEach ( Entity :: remove );
		seat_map.clear ( );
	}
}