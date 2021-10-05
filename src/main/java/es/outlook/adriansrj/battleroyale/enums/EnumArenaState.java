package es.outlook.adriansrj.battleroyale.enums;

/**
 * Enumerates the different states of the arenas.
 *
 * @author AdrianSR / 03/09/2021 / 12:03 p. m.
 */
public enum EnumArenaState {
	
	/**
	 * The arena is waiting for players to start.
	 */
	WAITING,
	
	/**
	 * The arena is running.
	 */
	RUNNING,
	
	/**
	 * The arena is restarting.
	 */
	RESTARTING,
	
	/**
	 * The arena cannot be restarted until the world is reset.
	 */
	STOPPED;
}