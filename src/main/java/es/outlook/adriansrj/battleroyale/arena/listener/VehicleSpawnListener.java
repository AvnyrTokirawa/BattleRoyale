package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaStateChangeEvent;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.qualityarmory.QualityArmoryVehiclesUtil;
import es.outlook.adriansrj.battleroyale.vehicle.VehiclesConfiguration;
import es.outlook.adriansrj.battleroyale.vehicle.VehiclesConfigurationEntry;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class responsible for spawning vehicles when players
 * are close to a vehicle spawn.
 *
 * @author AdrianSR / 14/09/2021 / 06:16 p. m.
 */
public class VehicleSpawnListener extends BattleRoyaleArenaListener implements Runnable {
	
	protected final Map < BattleRoyaleArena, Set < Vector > > spawn_map = new ConcurrentHashMap <> ( );
	
	public VehicleSpawnListener ( BattleRoyale plugin ) {
		super ( plugin );
		
		// scheduling
		Bukkit.getScheduler ( ).runTaskTimerAsynchronously (
				plugin , this , 20L , 20L );
	}
	
	// event handler responsible for loading
	// vehicles spawns for each arena
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onStart ( ArenaStateChangeEvent event ) {
		BattleRoyaleArena arena     = event.getArena ( );
		Set < Vector >    spawn_set = Collections.synchronizedSet ( new HashSet <> ( ) );
		
		if ( event.getState ( ) == EnumArenaState.RUNNING ) {
			arena.getBattlefield ( ).getConfiguration ( ).getVehicleSpawns ( ).stream ( )
					.map ( location -> arena.getFullBounds ( ).project ( location ) )
					.forEach ( spawn_set :: add );
		}
		
		spawn_map.put ( arena , spawn_set );
	}
	
	@Override
	public void run ( ) {
		if ( Bukkit.getOnlinePlayers ( ).isEmpty ( ) ) {
			return;
		}
		
		synchronized ( spawn_map ) {
			for ( Map.Entry < BattleRoyaleArena, Set < Vector > > entry : spawn_map.entrySet ( ) ) {
				BattleRoyaleArena   arena    = entry.getKey ( );
				Iterator < Vector > iterator = entry.getValue ( ).iterator ( );
				
				while ( iterator.hasNext ( ) ) {
					Vector next = iterator.next ( );
					
					if ( canSpawn ( arena , next ) ) {
						iterator.remove ( );
						
						// spawning
						spawnVehicle ( arena , next );
					}
				}
			}
		}
	}
	
	protected boolean canSpawn ( BattleRoyaleArena arena , Vector location ) {
		Location2I chunk_location = toChunkLocation ( location );
		
		// must be within 3x3 chunk around to spawn the vehicle
		return arena.getWorld ( ).getPlayers ( ).stream ( ).anyMatch (
				player -> chunk_location.distance ( toChunkLocation ( player.getLocation ( ).toVector ( ) ) ) <= 3 );
	}
	
	protected Location2I toChunkLocation ( Vector location ) {
		return new Location2I ( location.getBlockX ( ) >> 4 , location.getBlockZ ( ) >> 4 );
	}
	
	protected void spawnVehicle ( BattleRoyaleArena arena , Vector location ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			VehiclesConfiguration configuration = arena.getBattlefield ( ).getConfiguration ( )
					.getVehiclesConfiguration ( );
			VehiclesConfigurationEntry entry = configuration != null ? configuration.getRandomEntry ( ) : null;
			
			if ( entry != null ) {
				QualityArmoryVehiclesUtil.spawnVehicle ( location.toLocation ( arena.getWorld ( ) ) , entry );
			}
		} else {
			Bukkit.getScheduler ( ).runTask (
					BattleRoyale.getInstance ( ) , ( ) -> spawnVehicle ( arena , location ) );
		}
	}
}