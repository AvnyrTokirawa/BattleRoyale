package es.outlook.adriansrj.battleroyale.arena;

import es.outlook.adriansrj.battleroyale.battlefield.bus.BusSpawn;
import es.outlook.adriansrj.battleroyale.bus.BusInstance;
import es.outlook.adriansrj.battleroyale.bus.dragon.BusDragon;
import es.outlook.adriansrj.battleroyale.bus.dragon.BusDragonInstance;
import es.outlook.adriansrj.battleroyale.enums.EnumBusConfiguration;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.util.math.RandomUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Class responsible for keeping track of the {@link BusInstance}es in a {@link BattleRoyaleArena}.
 *
 * @author AdrianSR / 08/09/2021 / 05:45 p. m.
 */
public final class BattleRoyaleArenaBusRegistry implements Iterable < BusInstance < ? > > {
	
	final Map < UUID, BusInstance < ? > > handle = new ConcurrentHashMap <> ( );
	final BattleRoyaleArena               arena;
	final BusSpawn                        spawn;
	final BusDragonInstance               dragon_bus;
	
	BattleRoyaleArenaBusRegistry ( BattleRoyaleArena arena , boolean dragon_bus ) {
		this.arena = arena;
		
		// finding out random bus spawn
		Set < BusSpawn > spawns = arena.getBattlefield ( ).getConfiguration ( ).getBusSpawns ( );
		BusSpawn         spawn  = null;
		
		if ( spawns.size ( ) > 0 && spawns.stream ( ).anyMatch ( BusSpawn :: isValid ) ) {
			while ( spawn == null ) {
				spawn = RandomUtil.getRandomElement ( spawns );
				spawn = spawn != null && spawn.isValid ( ) ? spawn : null;
			}
		}
		
		if ( spawn == null ) {
			throw new IllegalStateException ( "couldn't find a valid bus spawn" );
		}
		
		this.spawn = spawn;
		
		// dragon bus
		if ( dragon_bus ) {
			this.dragon_bus = new BusDragon ( ).createInstance ( );
		} else {
			this.dragon_bus = null;
		}
	}
	
	BattleRoyaleArenaBusRegistry ( BattleRoyaleArena arena ) {
		this ( arena , EnumBusConfiguration.ENABLE_DRAGON_BUS.getAsBoolean ( ) );
	}
	
	/**
	 * Gets the {@link BattleRoyaleArena} that owns this registry.
	 *
	 * @return owner of this registry.
	 */
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	/**
	 * Gets whether the dragon bus is enabled.
	 * <br>
	 * <b>Note that is the dragon bus is enabled,
	 * the players will not be able to choose any
	 * other kind of bus.</b>
	 *
	 * @return whether the dragon bus is enabled, or not.
	 */
	public boolean isDragonBusEnabled ( ) {
		return dragon_bus != null;
	}
	
	/**
	 * Gets the dragon bus.
	 * <br>
	 * <b>Note that {@link #isDragonBusEnabled()} should be used to check
	 * if the dragon bus is enabled, as, if it is not, this will return null.</b>
	 *
	 * @return the dragon bus, or <b>null</b> if not enabled.
	 */
	public BusDragonInstance getDragonBus ( ) {
		return dragon_bus;
	}
	
	/**
	 * Gets the number of registered buses.
	 * <br>
	 * Note that <b>zero</b> will be returned
	 * if the dragon bus is enabled.
	 *
	 * @return the number of registered buses.
	 */
	public int getCount ( ) {
		return handle.size ( );
	}
	
	//	/**
	//	 * Creates and registers a new {@link BusInstance} for the specified player.
	//	 * <br>
	//	 * Note that the <b>dragon bus</b> will be returned if enabled, instead
	//	 * of a new bus.
	//	 *
	//	 * @param owner the owner.
	//	 * @return the created bus, or the <b>dragon bus</b> if enabled.
	//	 */
	//	BusInstance < ? > createAndRegisterBus ( Player owner ) {
	//		if ( dragon_bus == null ) {
	//			// finishing current
	//			BusInstance < ? > current = handle.get ( owner.getUniqueId ( ) );
	//
	//			if ( current != null && !current.isFinished ( ) ) {
	//				current.finish ( );
	//			}
	//
	//			// then creating
	//			// TODO: create bus. of course this depends on the type of bus the player is using
	//		} else {
	//			return dragon_bus;
	//		}
	//		return null;
	//	}
	
	//	/**
	//	 * Creates and registers a new {@link BusInstance} for the player
	//	 * identified by the provided {@link UUID}.
	//	 * <br>
	//	 * Note that the <b>dragon bus</b> will be returned if enabled, instead
	//	 * of a new bus.
	//	 *
	//	 * @param owner_uid the owner uuid.
	//	 * @return the created bus, or the <b>dragon bus</b> if enabled.
	//	 */
	//	BusInstance < ? > createAndRegisterBus ( UUID owner_uid ) {
	//		if ( dragon_bus == null ) {
	//			// finishing current
	//			BusInstance < ? > current = handle.get ( owner_uid );
	//
	//			if ( current != null && !current.isFinished ( ) ) {
	//				current.finish ( );
	//			}
	//
	//			// then creating
	//
	//			// TODO: create bus. of course this depends on the type of bus the player is using
	//		} else {
	//			return dragon_bus;
	//		}
	//		return null;
	//	}
	
	/**
	 * Gets the bus that carries the player identified by the provided {@link UUID}.
	 * <br>
	 * Note that the <b>dragon bus</b> will be returned if enabled.
	 *
	 * @return the bus that carries the player (if any), or the <b>dragon bus</b> if enabled.
	 */
	public BusInstance < ? > getBus ( UUID owner_uid ) {
		if ( dragon_bus == null ) {
			return handle.get ( owner_uid );
		} else {
			return dragon_bus;
		}
	}
	
	/**
	 * Gets the bus that carries the provided player.
	 * <br>
	 * Note that the <b>dragon bus</b> will be returned if enabled.
	 *
	 * @return the bus that carries the player (if any), or the <b>dragon bus</b> if enabled.
	 */
	public BusInstance < ? > getBus ( Player owner ) {
		return getBus ( owner.getUniqueId ( ) );
	}
	
	/**
	 * Gets the bus that carries the provided player.
	 * <br>
	 * Note that the <b>dragon bus</b> will be returned if enabled.
	 *
	 * @return the bus that carries the player (if any), or the <b>dragon bus</b> if enabled.
	 */
	public BusInstance < ? > getBus ( org.bukkit.entity.Player owner ) {
		return getBus ( owner.getUniqueId ( ) );
	}
	
	/**
	 * Gets an unmodifiable view of the handle {@link Map} that holds the buses in this registry.
	 * <br>
	 * <b>Note that any attempt to modify the returned map will result in a
	 * exception as it is unmodifiable.</b>
	 *
	 * @return the {@link Map} that holds the buses in this registry,
	 * wrapped in a <b>unmodifiable</b> view.
	 */
	public Map < UUID, BusInstance < ? > > getHandle ( ) {
		return Collections.unmodifiableMap ( handle );
	}
	
	public void start ( ) {
		if ( dragon_bus != null ) {
			dragon_bus.start ( arena , spawn );
		} else {
			// starting individual buses
			arena.getPlayers ( false ).stream ( ).filter ( Player :: hasTeam ).filter (
					Player :: isOnline ).forEach ( player -> {
				BusInstance < ? > bus = player.getBus ( );
				
				if ( bus.isStarted ( ) ) {
					bus.restart ( );
				}
				
				bus.start ( this.arena , this.spawn );
				
				// mapping to keep track of it
				this.handle.put ( player.getUniqueId ( ) , bus );
			} );
		}
	}
	
	public void finish ( ) {
		if ( dragon_bus != null && dragon_bus.isStarted ( ) && !dragon_bus.isFinished ( ) ) {
			dragon_bus.finish ( );
		}
		
		handle.values ( ).stream ( )
				.filter ( BusInstance :: isStarted )
				.filter ( bus -> !bus.isFinished ( ) )
				.forEach ( BusInstance :: finish );
	}
	
	/**
	 * Restarts this bus registry.
	 */
	public void restart ( ) {
		if ( dragon_bus != null ) {
			dragon_bus.restart ( );
		}
		
		handle.values ( ).forEach ( BusInstance :: restart );
		handle.clear ( );
	}
	
	@NotNull
	@Override
	public Iterator < BusInstance < ? > > iterator ( ) {
		// getHandle() for a immutable iterator
		return getHandle ( ).values ( ).iterator ( );
	}
	
	/**
	 * Returns a sequential {@code Stream} with this registry as its source.
	 *
	 * @return a sequential {@code Stream} over the teams in this registry.
	 */
	public Stream < BusInstance < ? > > stream ( ) {
		return getHandle ( ).values ( ).stream ( );
	}
}