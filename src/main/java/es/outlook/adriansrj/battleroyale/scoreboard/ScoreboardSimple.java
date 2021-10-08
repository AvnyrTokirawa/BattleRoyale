package es.outlook.adriansrj.battleroyale.scoreboard;

import es.outlook.adriansrj.battleroyale.player.Player;

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
			
			super.update ( );
		}
	}
}