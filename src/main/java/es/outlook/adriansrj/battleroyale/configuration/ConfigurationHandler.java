package es.outlook.adriansrj.battleroyale.configuration;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;

/**
 * @author AdrianSR / 22/08/2021 / Time: 09:05 p. m.
 */
public abstract class ConfigurationHandler extends PluginHandler {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public ConfigurationHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	/**
	 * This method is called just after this configuration handler is constructed. Should handle all the initial logic,
	 * such as, saving the default configuration.
	 */
	public abstract void initialize ( );
	
	/**
	 * Loads the configuration from the corresponding configuration file/files this handler handles.
	 */
	public abstract void loadConfiguration ( );
	
	/**
	 * Saves the configuration in the corresponding configuration file/files this handler handles.
	 */
	public abstract void save ( );
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}