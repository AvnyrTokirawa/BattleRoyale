package es.outlook.adriansrj.battleroyale.configuration.bus;

import es.outlook.adriansrj.battleroyale.bus.Bus;
import es.outlook.adriansrj.battleroyale.configuration.EnumConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumBusConfiguration;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

import java.io.File;

/**
 * Class responsible for handling the configuration of the {@link Bus}s.
 *
 * @author AdrianSR / 23/09/2021 / 10:10 p. m.
 */
public final class BusConfigHandler extends EnumConfigurationHandler < EnumBusConfiguration > {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public BusConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.BUS_CONFIGURATION.getFile ( );
	}
	
	@Override
	public Class < EnumBusConfiguration > getEnumClass ( ) {
		return EnumBusConfiguration.class;
	}
}