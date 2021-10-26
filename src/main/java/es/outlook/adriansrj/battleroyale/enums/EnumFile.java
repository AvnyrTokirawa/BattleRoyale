package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

import java.io.File;
import java.io.IOException;

/**
 * @author AdrianSR / 22/08/2021 / Time: 10:14 p. m.
 */
public enum EnumFile {
	
	/**
	 * Plugin main configuration file.
	 */
	MAIN_CONFIGURATION ( "BattleRoyaleConfiguration.yml" ),
	
	/**
	 * Plugin language configuration file.
	 */
	LANGUAGE_CONFIGURATION ( "BattleRoyaleLanguage.yml" ),
	
	/**
	 * Lobby configuration file.
	 */
	LOBBY_CONFIGURATION ( "BattleRoyaleLobby.yml" ),
	
	/**
	 * Arenas configuration file.
	 */
	ARENAS_CONFIGURATION ( "BattleRoyaleArenas.yml" ),
	
	/**
	 * Arena signs configuration file.
	 */
	ARENA_SIGNS_CONFIGURATION ( "BattleRoyaleSigns.yml" ),
	
	/**
	 * File responsible for storing the signs.
	 */
	ARENA_SIGNS_CONTAINER ( EnumDirectory.LOBBY_DIRECTORY , "SignContainer.yml" ),
	
	/**
	 * Compass bar configuration file.
	 */
	COMPASS_BAR_CONFIGURATION ( "BattleRoyaleCompass.yml" ),
	
	/**
	 * Bus configuration file.
	 */
	BUS_CONFIGURATION ( "BattleRoyaleBus.yml" ),
	
	/**
	 * Parachute configuration file.
	 */
	PARACHUTE_CONFIGURATION ( "BattleRoyaleParachute.yml" ),
	
	/**
	 * Battle royale item configuration file.
	 */
	ITEM_CONFIGURATION ( "BattleRoyaleItems.yml" ) ,
	
	/**
	 * Arena selector GUI configuration file.
	 */
	ARENA_SELECTOR_GUI ( "BattleRoyaleArenaSelectorGUI.yml" ) ,
	
	/**
	 * Team GUIs language configuration file.
	 */
	TEAM_GUIS_LANGUAGE_CONFIGURATION ( "BattleRoyaleTeamGUIsLanguage.yml" ),
	
	/**
	 * Settings GUIs configuration file.
	 */
	SETTINGS_GUIS_CONFIGURATION ( "BattleRoyaleSettingsGUIs.yml" ),
	
	;
	
	private final String        name;
	private final EnumDirectory directory;
	
	private EnumFile ( EnumDirectory directory , String name ) {
		this.directory = directory;
		this.name      = name;
	}
	
	private EnumFile ( String name ) {
		this ( null , name );
	}
	
	public String getName ( ) {
		return name;
	}
	
	public File getFile ( ) {
		return new File ( directory != null ? directory.getDirectory ( )
								  : BattleRoyale.getInstance ( ).getDataFolder ( ) , name );
	}
	
	/**
	 * Gets the file, and creates it if not exists.
	 *
	 * @return the file.
	 */
	public File safeGetFile ( ) {
		File file = getFile ( );
		
		if ( !file.getParentFile ( ).exists ( ) ) {
			file.getParentFile ( ).mkdirs ( );
		}
		
		if ( !file.exists ( ) ) {
			try {
				file.createNewFile ( );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
		return file;
	}
}
