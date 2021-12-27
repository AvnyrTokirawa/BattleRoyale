package es.outlook.adriansrj.battleroyale.configuration.lang;

import es.outlook.adriansrj.battleroyale.configuration.LanguageEnumConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

import java.io.File;

/**
 * @author AdrianSR / 04/09/2021 / 03:29 p. m.
 */
public final class BattleRoyaleLanguageConfigHandler extends LanguageEnumConfigurationHandler < EnumLanguage > {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public BattleRoyaleLanguageConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.LANGUAGE_CONFIGURATION.getFile ( );
	}
	
	@Override
	public Class < EnumLanguage > getEnumClass ( ) {
		return EnumLanguage.class;
	}
}