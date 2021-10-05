package es.outlook.adriansrj.battleroyale.configuration;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

import java.io.File;

/**
 * @author AdrianSR / 11/09/2021 / 11:53 a. m.
 */
public abstract class SingleFileConfigurationHandler extends ConfigurationHandler {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public SingleFileConfigurationHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	/**
	 * Gets the configuration file that is being handled for this configuration handler.
	 *
	 * @return handling configuration file.
	 */
	public abstract File getFile ( );
	
	/**
	 * @return handling configuration file.
	 *
	 * @throws IllegalArgumentException if returned null.
	 */
	protected final File safeGetFile ( ) {
		File file = getFile ( );
		
		if ( file != null ) {
			return file;
		} else {
			throw new IllegalArgumentException ( "getFile() returned null" );
		}
	}
}
