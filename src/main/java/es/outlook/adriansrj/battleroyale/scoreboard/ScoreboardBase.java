package es.outlook.adriansrj.battleroyale.scoreboard;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.PluginUtil;
import es.outlook.adriansrj.battleroyale.util.StringUtil;

/**
 * Scoreboard that will enable the compatibility
 * with scoreboard plugins if possible; otherwise
 * it will use the {@link ScoreboardSimple} instead.
 *
 * @author AdrianSR / 06/01/2022 / 01:35 p. m.
 */
public class ScoreboardBase extends Scoreboard {
	
	protected ScoreboardSimple       simple;
	protected ScoreboardFeatherBoard featherboard;
	protected Scoreboard             active;
	
	public ScoreboardBase ( Player player ) {
		super ( player );
		
		this.simple       = new ScoreboardSimple ( player );
		this.featherboard = new ScoreboardFeatherBoard ( player );
		this.active       = simple;
		
		// visible by default
		this.setVisible ( true );
	}
	
	@Override
	public boolean isVisible ( ) {
		return active != null && active.isVisible ( );
	}
	
	@Override
	public void setVisible ( boolean visible ) {
		// finding out the scoreboard to activate
		BattleRoyaleArena       arena         = player.getArena ( );
		ScoreboardConfiguration configuration = getConfiguration ( );
		
		if ( configuration != null ) {
			boolean plugin = false;
			
			if ( PluginUtil.isFeatherBoardEnabled ( ) ) {
				String name;
				
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
					name = null;
				}
				
				if ( StringUtil.isNotBlank ( name ) ) {
					plugin = true;
					active = featherboard;
				}
			}
			
			if ( plugin ) {
				// making sure simple is not visible
				simple.setVisible ( false );
			} else {
				// couldn't enable compatibility
				// with any scoreboard plugin.
				active = simple;
			}
		}
		
		// then setting
		if ( active != null ) {
			active.setVisible ( visible );
		}
	}
	
	@Override
	public void update ( ) {
		if ( isVisible ( ) ) {
			active.update ( );
		}
	}
	
	@Override
	public void destroy ( ) {
		if ( simple != null ) {
			simple.destroy ( );
			simple = null;
		}
		
		if ( featherboard != null ) {
			featherboard.destroy ( );
			featherboard = null;
		}
		
		active = null;
	}
	
	// ---- utils
	
	protected ScoreboardConfiguration getConfiguration ( ) {
		BattleRoyaleArena arena = player.getArena ( );
		
		if ( arena != null ) {
			return arena.getConfiguration ( ).getScoreboardConfiguration ( );
		} else {
			return null;
		}
	}
}
