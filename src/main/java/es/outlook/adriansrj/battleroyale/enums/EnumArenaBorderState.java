package es.outlook.adriansrj.battleroyale.enums;

/**
 * Enumerates the different states of the border of an arena.
 *
 * @author AdrianSR / 06/09/2021 / 10:50 a. m.
 */
public enum EnumArenaBorderState {
	
	/**
	 * The border is temporarily stopped until the next shrinking point begins.
	 */
	IDLE,
	
	/**
	 * The border is resizing.
	 */
	RESIZING,
	
	/**
	 * The border is stopped since the shrinking succession has finished.
	 */
	STOPPED;
}