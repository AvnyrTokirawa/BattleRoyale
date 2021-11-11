package es.outlook.adriansrj.battleroyale.compatibility;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;

/**
 * @author AdrianSR / 13/09/2021 / 12:20 a. m.
 */
public abstract class PluginCompatibilityHandler extends PluginHandler {
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	protected PluginCompatibilityHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}