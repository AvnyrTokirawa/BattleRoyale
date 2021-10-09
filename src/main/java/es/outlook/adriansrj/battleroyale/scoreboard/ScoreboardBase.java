package es.outlook.adriansrj.battleroyale.scoreboard;

import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.scoreboard.ScoreScoreboard;
import es.outlook.adriansrj.core.util.StringUtil;

/**
 * Base implementation of {@link Scoreboard}.
 *
 * @author AdrianSR / 08/10/2021 / 08:41 a. m.
 */
public abstract class ScoreboardBase extends Scoreboard {
	
	protected ScoreScoreboard handle;
	
	protected ScoreboardBase ( Player player ) {
		super ( player );
		
		// initializing handle
		this.handle = new ScoreScoreboard ( StringUtil.EMPTY );
		this.handle.addViewersByUniqueId ( player.getUniqueId ( ) );
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
}
