package es.outlook.adriansrj.battleroyale.enums;

/**
 * @author AdrianSR / 03/09/2021 / 12:23 p. m.
 */
public enum EnumArenaRestartPolicy {
	
	/**
	 * The world of the arena will not be reloaded, but instead only the battlefield.
	 */
	BATTLEFIELD,
	
	/**
	 * The arena will need the world to be reloaded in order to start again.
	 *
	 * In case there are more than one arena in a single world, the world will
	 * not be reloaded until all the arenas are ready to restart.
	 */
	WORLD,
	
	/**
	 * If there is more than one arena in a single world, only the battlefield of the arena
	 * will be restarted.
	 *
	 * If there is only one arena in the world, then the world will be reloaded.
	 */
	ANY,
	
	;
}