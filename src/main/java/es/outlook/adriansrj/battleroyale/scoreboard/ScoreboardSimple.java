package es.outlook.adriansrj.battleroyale.scoreboard;

import es.outlook.adriansrj.battleroyale.placeholder.PlaceholderHandler;
import es.outlook.adriansrj.battleroyale.game.player.Player;

/**
 *
 *
 * @author AdrianSR / 08/10/2021 / 09:32 a. m.
 */
public class ScoreboardSimple extends ScoreboardBase {
	
	public ScoreboardSimple ( Player player ) {
		super ( player );
	}
	
	@Override
	public void update ( ) {
		if ( handle != null && isVisible ( ) ) {
			handle.set ( 0 , "Hola" );
			handle.set ( 1 , "" );
			handle.set ( 2 , "Como estas" );
			handle.set ( 3 , "" );
			handle.set ( 4 , setPlaceholders ( "Arena limit: %br_arena_limit%" ) );
			handle.set ( 5 , setPlaceholders ( "Arena count: %br_arena_count%" ) );
			handle.set ( 6 , setPlaceholders ( "Arena state: %br_arena_state%" ) );
			
			super.update ( );
		}
	}
	
	private String setPlaceholders ( String text ) {
		return PlaceholderHandler.getInstance ( ).setPlaceholders ( player.getPlayer ( ) , text );
	}
}