package es.outlook.adriansrj.battleroyale.arena.bombing;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.event.border.BorderResizeChangeEvent;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.List;

/**
 * @author AdrianSR / 14/10/2021 / 07:15 p. m.
 */
public final class BombingZoneGeneratorHandler extends PluginHandler {
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BombingZoneGeneratorHandler ( BattleRoyale plugin ) {
		super ( plugin ); register ( );
	}
	
	// this event handler will start bombing zones
	// when a new border resize point begins.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onPointChange ( BorderResizeChangeEvent event ) {
		BattleRoyaleArena    arena = event.getBorder ( ).getArena ( );
		List < BombingZone > list  = arena.getBombingZoneGenerator ( ).next ( );
		
		for ( BombingZone next : list ) {
			if ( next.isValidPlace ( ) ) {
				next.start ( );
			}
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}