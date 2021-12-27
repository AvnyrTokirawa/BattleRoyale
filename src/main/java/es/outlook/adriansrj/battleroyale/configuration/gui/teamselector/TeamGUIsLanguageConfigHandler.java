package es.outlook.adriansrj.battleroyale.configuration.gui.teamselector;

import es.outlook.adriansrj.battleroyale.configuration.LanguageEnumConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.enums.EnumTeamGUIsLanguage;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

import java.io.File;

/**
 * @author AdrianSR / 02/10/2021 / 08:58 a. m.
 */
public final class TeamGUIsLanguageConfigHandler
		extends LanguageEnumConfigurationHandler < EnumTeamGUIsLanguage > {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public TeamGUIsLanguageConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.TEAM_GUIS_LANGUAGE_CONFIGURATION.getFile ( );
	}
	
	@Override
	public Class < EnumTeamGUIsLanguage > getEnumClass ( ) {
		return EnumTeamGUIsLanguage.class;
	}
}