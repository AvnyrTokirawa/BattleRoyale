package es.outlook.adriansrj.battleroyale.scoreboard.handle;

import es.outlook.adriansrj.battleroyale.scoreboard.Scoreboard;

/**
 * @author AdrianSR / 06/01/2022 / 01:07 p. m.
 */
public abstract class ScoreboardHandleBase implements ScoreboardHandle {
	
	protected final Scoreboard scoreboard;
	
	public ScoreboardHandleBase ( Scoreboard scoreboard ) {
		this.scoreboard = scoreboard;
	}
}
