package es.outlook.adriansrj.battleroyale.configuration.gui.settings;

import es.outlook.adriansrj.battleroyale.configuration.EnumConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.enums.EnumSettingsGUIsConfiguration;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

import java.io.File;

/**
 * @author AdrianSR / 03/10/2021 / 06:17 p. m.
 */
public final class SettingsGUIsConfigHandler
		extends EnumConfigurationHandler < EnumSettingsGUIsConfiguration > {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public SettingsGUIsConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.SETTINGS_GUIS_CONFIGURATION.getFile ( );
	}
	
	@Override
	public Class < EnumSettingsGUIsConfiguration > getEnumClass ( ) {
		return EnumSettingsGUIsConfiguration.class;
	}
}