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
	WAITING ( EnumLanguage.ARENA_STATE_WAITING_WORD ),
	
	/**
	 * The arena is running.
	 */
	RUNNING ( EnumLanguage.ARENA_STATE_RUNNING_WORD ),
	
	/**
	 * The arena is restarting.
	 */
	RESTARTING ( EnumLanguage.ARENA_STATE_RESTARTING_WORD ),
	
	/**
	 * The arena was stopped by an admin or by the plugin
	 * as result of an error.
	 */
	STOPPED ( EnumLanguage.ARENA_STATE_STOPPED_WORD );
	
	private final EnumLanguage language;
	
	EnumArenaState ( EnumLanguage language ) {
		this.language = language;
	}
	
	public EnumLanguage getLanguage ( ) {
		return language;
	}
}