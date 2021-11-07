package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.WorldUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Iterator;
import java.util.Objects;

/**
 * This listener is responsible for stopping non-player-blocks
 * to be destroyed.
 *
 * @author AdrianSR / 01/11/2021 / 04:57 p. m.
 */
public final class BlockListener extends BattleRoyaleArenaListener {
	
	public BlockListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	// this event handler will mark blocks
	// placed by players as player blocks.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onPlace ( BlockPlaceEvent event ) {
		Block             block = event.getBlock ( );
		BattleRoyaleArena arena = matchArena ( block.getLocation ( ) );
		
		if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING && !arena.isOver ( ) ) {
			WorldUtil.setPlayerBlock ( block , true );
		}
	}
	
	// this event will stop non-player blocks
	// from breaking if the arena is running.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onBreak ( BlockBreakEvent event ) {
		Block             block = event.getBlock ( );
		BattleRoyaleArena arena = matchArena ( block.getLocation ( ) );
		
		if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING && !arena.isOver ( ) ) {
			if ( WorldUtil.isPlayerBlock ( block ) ) {
				WorldUtil.setPlayerBlock ( block , false );
			} else {
				event.setCancelled ( true );
			}
		}
	}
	
	// this event will stop non-player blocks
	// from exploding if the arena is running.
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onExplode ( EntityExplodeEvent event ) {
		BattleRoyaleArena arena = matchArena ( event.getEntity ( ).getLocation ( ) );
		
		if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING && !arena.isOver ( ) ) {
			Iterator < Block > iterator = event.blockList ( ).iterator ( );
			
			while ( iterator.hasNext ( ) ) {
				Block next = iterator.next ( );
				
				if ( WorldUtil.isPlayerBlock ( next ) ) {
					WorldUtil.setPlayerBlock ( next , false );
				} else {
					iterator.remove ( );
				}
			}
		}
	}
	
	// ------ utils
	
	private BattleRoyaleArena matchArena ( Location location ) {
		return BattleRoyaleArenaHandler.getInstance ( ).getArenas ( ).stream ( )
				.filter ( other -> Objects.equals ( location.getWorld ( ) , other.getWorld ( ) ) )
				.filter ( other -> other.getFullBounds ( ).contains ( location.getX ( ) , location.getZ ( ) ) )
				.findAny ( ).orElse ( null );
	}
}
