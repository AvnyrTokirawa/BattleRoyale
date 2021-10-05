package es.outlook.adriansrj.battleroyale.configuration.compass;

import es.outlook.adriansrj.battleroyale.compass.CompassBar;
import es.outlook.adriansrj.battleroyale.configuration.EnumConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumCompassConfiguration;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

import java.io.File;

/**
 * Class responsible for handling the configuration of the {@link CompassBar}s.
 *
 * @author AdrianSR / 09/09/2021 / 05:02 p. m.
 */
public final class CompassBarConfigHandler extends EnumConfigurationHandler < EnumCompassConfiguration > {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public CompassBarConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.COMPASS_BAR_CONFIGURATION.getFile ( );
	}
	
	@Override
	public Class < EnumCompassConfiguration > getEnumClass ( ) {
		return EnumCompassConfiguration.class;
	}
}