package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;

/**
 * Enumerate the different modes in which the plugin can run.
 *
 * @author AdrianSR / 06/11/2021 / 02:26 p. m.
 */
public enum EnumMode {
	
	BUNGEE {
		@Override
		public String getArenaName ( ) {
			return EnumMainConfiguration.MODE_BUNGEE_ARENA.getAsString ( ).trim ( );
		}
		
//		@Override
//		public String getRestartCommand ( ) {
//			return EnumMainConfiguration.MODE_BUNGEE_RESTART_COMMAND.getAsString ( )
//					.trim ( ).replace ( "/" , "" );
//		}
	},
	
	MULTIARENA,
	
	SHARED {
		
		@Override
		public String getCommand ( ) {
			return EnumMainConfiguration.MODE_SHARED_COMMAND.getAsString ( )
					.trim ( ).replace ( "/" , "" );
		}
	} ,
	
	/**
	 * The plugin will only enable the required
	 * modules to replace placeholders and to
	 * hub-stuff.
	 */
	LOBBY ;
	
	// ----- bungee
	
	/**
	 * Gets the name of the arena that is to
	 * be played in this server.
	 * <br>
	 * <b>Note that {@link #BUNGEE} is the only mode
	 * that supports this value.</b>
	 *
	 * @return the name of the arena that is to be played in this server.
	 */
	public String getArenaName ( ) {
		throw new UnsupportedOperationException ( );
	}
	
	/**
	 * Gets the arena that is to
	 * be played in this server.
	 * <br>
	 * <b>Note that {@link #BUNGEE} is the only mode
	 * that supports this value.</b>
	 *
	 * @return the arena that is to be played in this server.
	 */
	public BattleRoyaleArena getArena ( ) {
		return BattleRoyaleArenaHandler.getInstance ( ).getArena (
				getArenaName ( ) ).orElse ( null );
	}
	
//	/**
//	 * Gets the command that is to be
//	 * executed to restart the server after
//	 * the arena ends.
//	 * <br>
//	 * <b>Note that {@link #BUNGEE} is the only mode
//	 * that supports this value.</b>
//	 *
//	 * @return the name of the arena that is to be played in this server.
//	 */
//	public String getRestartCommand ( ) {
//		throw new UnsupportedOperationException ( );
//	}
	
	// ----- shared
	
	/**
	 * Gets the command that is to be
	 * executed to send a given player
	 * to the battle royale lobby.
	 * <br>
	 * <b>Note that {@link #SHARED} is the only mode
	 * that supports this value.</b>
	 *
	 * @return the name of the arena that is to be played in this server.
	 */
	public String getCommand ( ) {
		throw new UnsupportedOperationException ( );
	}
}