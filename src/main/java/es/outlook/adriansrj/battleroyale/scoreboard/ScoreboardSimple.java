package es.outlook.adriansrj.battleroyale.scoreboard;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.placeholder.PlaceholderHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple scoreboard implementation.
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
			BattleRoyaleArena arena = player.getArena ( );
			ScoreboardConfiguration configuration =
					arena != null ? arena.getConfiguration ( ).getScoreboardConfiguration ( ) : null;
			
			if ( configuration != null ) {
				String          title;
				List < String > elements;
				
				if ( arena.getState ( ) == EnumArenaState.RUNNING ) {
					if ( player.isPlaying ( ) ) {
						title    = configuration.getGameTitle ( );
						elements = configuration.getGameElements ( );
					} else {
						title    = configuration.getSpectatorTitle ( );
						elements = configuration.getSpectatorElements ( );
					}
				} else {
					title    = configuration.getLobbyTitle ( );
					elements = configuration.getLobbyElements ( );
				}
				
				// setting title
				handle.setTitle ( setPlaceholders ( title ) );
				
				// inserting elements
				List < String > resulting_elements = new ArrayList <> ( );
				
				for ( String element : elements ) {
					// placeholders
					element = setPlaceholders ( element );
					
					// line separator
					if ( element.contains ( System.lineSeparator ( ) ) ) {
						resulting_elements.addAll ( Arrays.asList ( element.split ( System.lineSeparator ( ) ) ) );
					} else {
						resulting_elements.add ( element );
					}
				}
				
				handle.clear ( );
				handle.addAll ( resulting_elements.toArray ( new String[ 0 ] ) );
			}
			
			super.update ( );
		}
	}
	
	private String setPlaceholders ( String text ) {
		return text != null ? PlaceholderHandler.getInstance ( )
				.setPlaceholders ( player.getPlayer ( ) , text ) : null;
	}
}