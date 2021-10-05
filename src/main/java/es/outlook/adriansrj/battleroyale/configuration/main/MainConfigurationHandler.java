package es.outlook.adriansrj.battleroyale.configuration.main;

import es.outlook.adriansrj.battleroyale.configuration.EnumConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

import java.io.File;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 22/08/2021 / Time: 09:04 p. m.
 */
public final class MainConfigurationHandler extends EnumConfigurationHandler < EnumMainConfiguration > {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public MainConfigurationHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.MAIN_CONFIGURATION.getFile ( );
	}
	
	@Override
	public Class < EnumMainConfiguration > getEnumClass ( ) {
		return EnumMainConfiguration.class;
	}
}