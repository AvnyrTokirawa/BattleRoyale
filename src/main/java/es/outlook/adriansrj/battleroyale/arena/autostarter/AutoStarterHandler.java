package es.outlook.adriansrj.battleroyale.arena.autostarter;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaLeaveEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaSetEvent;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * @author AdrianSR / 20/10/2021 / 12:09 p. m.
 */
public final class AutoStarterHandler extends PluginHandler {
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public AutoStarterHandler ( BattleRoyale plugin ) {
		super ( plugin ); register ( );
	}
	
	/**
	 * Starts/stops the auto-starter of the arena
	 * based on the number of required players/teams.
	 *
	 * @param arena the arena to check.
	 */
	private void check ( BattleRoyaleArena arena ) {
		AutoStarter starter = arena.getAutoStarter ( );
		
		if ( arena.getState ( ) == EnumArenaState.WAITING && starter != null ) {
			// stopping and restarting
			if ( starter.isStarted ( ) && !starter.isFinished ( )
					&& !starter.canStart ( ) ) {
				starter.stop ( );
				starter.restart ( );
			}
			// starting
			else if ( !starter.isStarted ( ) && starter.canStart ( ) ) {
				starter.start ( );
			}
		}
	}
	
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onJoin ( PlayerArenaSetEvent event ) {
		check ( event.getArena ( ) );
	}
	
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onLeave ( PlayerArenaLeaveEvent event ) {
		check ( event.getArena ( ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
