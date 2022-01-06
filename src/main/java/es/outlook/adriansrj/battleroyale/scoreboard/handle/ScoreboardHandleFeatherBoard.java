package es.outlook.adriansrj.battleroyale.scoreboard.handle;

import es.outlook.adriansrj.battleroyale.scoreboard.Scoreboard;
import es.outlook.adriansrj.core.scoreboard.ScoreScoreboard;
import es.outlook.adriansrj.core.util.StringUtil;

/**
 * @author AdrianSR / 06/01/2022 / 01:06 p. m.
 */
public class ScoreboardHandleFeatherBoard extends ScoreboardHandleBase {
	
	protected ScoreScoreboard handle;
	
	public ScoreboardHandleFeatherBoard ( Scoreboard scoreboard ) {
		super ( scoreboard );
		this.handle = new ScoreScoreboard ( StringUtil.EMPTY );
	}
	
	@Override
	public boolean isVisible ( ) {
		return handle != null && handle.isViewer ( scoreboard.getPlayer ( ).getUniqueId ( ) );
	}
	
	@Override
	public void setVisible ( boolean visible ) {
		if ( handle != null ) {
			if ( visible ) {
				handle.addViewersByUniqueId ( scoreboard.getPlayer ( ).getUniqueId ( ) );
			} else {
				handle.removeViewerByUniqueId ( scoreboard.getPlayer ( ).getUniqueId ( ) );
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
