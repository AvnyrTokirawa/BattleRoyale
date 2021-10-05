package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

import java.io.File;

/**
 * Enumerates the directories the plugin handles.
 *
 * @author AdrianSR / 23/08/2021 / Time: 10:12 a. m.
 */
public enum EnumDirectory {
	
	/**
	 * Directory that stores the modalities.
	 */
	MODE_DIRECTORY ( "mode" , true ),
	
	/**
	 * Directory that stores the battlefields.
	 */
	BATTLEFIELD_DIRECTORY ( "battlefield" , true ),
	
	/**
	 * Directory that stores the worlds/schematics that will be loaded for the setup of the battle maps.
	 */
	BATTLEFIELD_INPUT_DIRECTORY ( BATTLEFIELD_DIRECTORY.name + "/input" , BATTLEFIELD_DIRECTORY.required ),
	
	/**
	 * Directory that stores temporal files.
	 */
	BATTLEFIELD_TEMP_DIRECTORY ( BATTLEFIELD_DIRECTORY.name + "/temp" , true ),
	
	/**
	 * Directory that stores the lobby stuff.
	 */
	LOBBY_DIRECTORY ( "lobby" , true ),
	
	/**
	 * Directory that stores the buses.
	 */
	BUS_DIRECTORY ( "bus" , false ),
	
	/**
	 * Directory that stores the parachutes.
	 */
	PARACHUTE_DIRECTORY ( "parachute" , true ),
	
	/**
	 * Directory that stores the loot configurations.
	 */
	LOOT_DIRECTORY ( "loot" , true ),
	
	/**
	 * Directory that stores the vehicles configurations.
	 */
	VEHICLE_DIRECTORY ( "vehicle" , true ),
	
	;
	
	private final String  name;
	private final boolean required;
	
	private EnumDirectory ( String name , boolean required ) {
		this.name     = name;
		this.required = required;
	}
	
	private EnumDirectory ( String name ) {
		this ( name , false );
	}
	
	public boolean isRequired ( ) {
		return required;
	}
	
	public String getName ( ) {
		return name;
	}
	
	public File getDirectory ( ) {
		return new File ( BattleRoyale.getInstance ( ).getDataFolder ( ) , name );
	}
	
	public File getDirectoryMkdirs ( ) {
		File directory = getDirectory ( );
		
		if ( !directory.exists ( ) ) {
			directory.mkdirs ( );
		}
		return directory;
	}
}