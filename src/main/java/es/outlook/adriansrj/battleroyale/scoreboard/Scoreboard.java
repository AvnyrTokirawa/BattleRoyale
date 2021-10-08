package es.outlook.adriansrj.battleroyale.scoreboard;

import es.outlook.adriansrj.battleroyale.player.Player;

/**
 * Battle royale contextual scoreboard.
 *
 * @author AdrianSR / 08/10/2021 / 08:38 a. m.
 */
public abstract class Scoreboard {
	
	protected final Player player;
	
	protected Scoreboard ( Player player ) {
		this.player = player;
	}
	
	public Player getPlayer ( ) {
		return player;
	}
	
	public abstract boolean isVisible ( );
	
	public abstract void setVisible ( boolean visible );
	
	public abstract void update ( );
	
	public abstract void destroy ( );
	
//	/**
	//	 * Called whenever the player reconnects.
	//	 * <br>
	//	 * The player should be re-added to the handle viewers list in case it is an instance
	//	 * of {@link es.outlook.adriansrj.core.scoreboard.ScoreScoreboard} as the bukkit
	//	 * scoreboard system has severe problems, just like always.
	//	 */
	//	protected abstract void onPlayerReconnect ( );
}
