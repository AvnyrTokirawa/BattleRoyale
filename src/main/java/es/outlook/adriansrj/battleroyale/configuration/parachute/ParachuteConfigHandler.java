package es.outlook.adriansrj.battleroyale.configuration.parachute;

import es.outlook.adriansrj.battleroyale.configuration.EnumConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.enums.EnumParachuteConfiguration;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;

import java.io.File;

/**
 * Class responsible for handling the configuration of the {@link Parachute}s.
 *
 * @author AdrianSR / 11/09/2021 / 11:31 a. m.
 */
public final class ParachuteConfigHandler extends EnumConfigurationHandler < EnumParachuteConfiguration > {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public ParachuteConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.PARACHUTE_CONFIGURATION.getFile ( );
	}
	
	@Override
	public Class < EnumParachuteConfiguration > getEnumClass ( ) {
		return EnumParachuteConfiguration.class;
	}
}