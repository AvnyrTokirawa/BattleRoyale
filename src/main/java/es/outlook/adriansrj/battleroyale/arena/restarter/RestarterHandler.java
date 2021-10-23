package es.outlook.adriansrj.battleroyale.arena.restarter;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaLeaveEvent;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * @author AdrianSR / 21/10/2021 / 11:31 p. m.
 */
public final class RestarterHandler extends PluginHandler {
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public RestarterHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	// this event handler will start the restarter
	// of an arena when is running and all the
	// players have quit
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onLeave ( PlayerArenaLeaveEvent event ) {
		BattleRoyaleArena arena = event.getArena ( );
		
		if ( arena.getState ( ) == EnumArenaState.RUNNING && arena.isEmpty ( ) ) {
			arena.restart ( false );
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
