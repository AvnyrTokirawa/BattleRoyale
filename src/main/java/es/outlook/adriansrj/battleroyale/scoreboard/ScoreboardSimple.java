package es.outlook.adriansrj.battleroyale.scoreboard;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.placeholder.PlaceholderHandler;
import es.outlook.adriansrj.core.scoreboard.ScoreScoreboard;
import es.outlook.adriansrj.core.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple scoreboard implementation.
 *
 * @author AdrianSR / 08/10/2021 / 09:32 a. m.
 */
public class ScoreboardSimple extends Scoreboard {
	
	protected ScoreScoreboard handle;
	
	public ScoreboardSimple ( Player player ) {
		super ( player );
		
		// initializing handle
		this.handle = new ScoreScoreboard ( StringUtil.EMPTY );
		
		// visible by default
		this.setVisible ( true );
	}
	
	@Override
	public boolean isVisible ( ) {
		return handle != null && handle.isViewer ( player.getUniqueId ( ) );
	}
	
	@Override
	public void setVisible ( boolean visible ) {
		if ( handle != null ) {
			if ( visible ) {
				handle.addViewersByUniqueId ( player.getUniqueId ( ) );
			} else {
				handle.removeViewerByUniqueId ( player.getUniqueId ( ) );
			}
		}
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
			
			handle.update ( );
		}
	}
	
	@Override
	public void destroy ( ) {
		if ( handle != null ) {
			this.handle.clearViewers ( );
			this.handle = null;
		}
	}
	
	private String setPlaceholders ( String text ) {
		return text != null ? PlaceholderHandler.getInstance ( )
				.setPlaceholders ( player.getPlayer ( ) , text ) : null;
	}
}