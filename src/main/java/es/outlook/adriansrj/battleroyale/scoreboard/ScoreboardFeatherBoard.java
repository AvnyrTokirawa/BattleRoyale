package es.outlook.adriansrj.battleroyale.scoreboard;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.compatibility.featherboard.FeatherBoardCompatibilityHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.util.StringUtil;

/**
 * @author AdrianSR / 06/01/2022 / 01:15 p. m.
 */
public class ScoreboardFeatherBoard extends Scoreboard {
	
	protected boolean visible;
	
	public ScoreboardFeatherBoard ( Player player ) {
		super ( player );
		
		// visible by default
		this.setVisible ( true );
	}
	
	@Override
	public boolean isVisible ( ) {
		return visible;
	}
	
	@Override
	public void setVisible ( boolean visible ) {
		this.visible = visible;
		
		// setting visible
		FeatherBoardCompatibilityHandler compatibility_handler = FeatherBoardCompatibilityHandler.getInstance ( );
		String                           name                  = getScoreboardName ( );
		
		if ( compatibility_handler != null && StringUtil.isNotBlank ( name ) ) {
			if ( visible ) {
				compatibility_handler.showScoreboard ( player , name );
			} else {
				compatibility_handler.hideScoreboard ( player , name );
			}
		}
	}
	
	@Override
	public void update ( ) {
		if ( visible ) {
			FeatherBoardCompatibilityHandler compatibility_handler = FeatherBoardCompatibilityHandler.getInstance ( );
			String                           name                  = getScoreboardName ( );
			
			if ( compatibility_handler != null && StringUtil.isNotBlank ( name ) ) {
				compatibility_handler.showScoreboard ( player , name );
			}
		}
	}
	
	@Override
	public void destroy ( ) {
		setVisible ( false );
	}
	
	// ---- utils
	
	protected String getScoreboardName ( ) {
		BattleRoyaleArena       arena         = player.getArena ( );
		ScoreboardConfiguration configuration = getConfiguration ( );
		String                  name;
		
		if ( arena != null ) {
			if ( arena.getState ( ) == EnumArenaState.RUNNING ) {
				if ( player.isPlaying ( ) ) {
					name = configuration.getGameFeatherBoard ( );
				} else {
					name = configuration.getSpectatorFeatherBoard ( );
				}
			} else {
				name = configuration.getLobbyFeatherBoard ( );
			}
		} else {
			return null;
		}
		
		return name;
	}
	
	protected ScoreboardConfiguration getConfiguration ( ) {
		BattleRoyaleArena arena = player.getArena ( );
		
		if ( arena != null ) {
			return arena.getConfiguration ( ).getScoreboardConfiguration ( );
		} else {
			return null;
		}
	}
}
