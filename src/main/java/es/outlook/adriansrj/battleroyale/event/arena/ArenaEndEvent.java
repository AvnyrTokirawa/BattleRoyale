package es.outlook.adriansrj.battleroyale.event.arena;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.Team;
import org.bukkit.event.HandlerList;

/**
 * Called whenever an arena is prepared.
 *
 * @author AdrianSR / 21/10/2021 / 08:36 a. m.
 */
public class ArenaEndEvent extends ArenaEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final Player winner_player;
	protected final Team   winner_team;
	
	public ArenaEndEvent ( BattleRoyaleArena arena , Player winner ) {
		super ( arena );
		
		this.winner_player = winner;
		this.winner_team   = null;
	}
	
	public ArenaEndEvent ( BattleRoyaleArena arena , Team winner ) {
		super ( arena );
		
		this.winner_team   = winner;
		this.winner_player = null;
	}
	
	public ArenaEndEvent ( BattleRoyaleArena arena ) {
		super ( arena );
		
		this.winner_player = null;
		this.winner_team   = null;
	}
	
	/**
	 * Gets the player who won the match.
	 * <br>
	 * Note that <b>null</b> will be returned
	 * if the mode is not <b>solo</b>, or if
	 * the winning player couldn't be determined.
	 *
	 * @return the player who won the match, or <b>null</b>.
	 */
	public Player getWinnerPlayer ( ) {
		return winner_player;
	}
	
	/**
	 * Gets the team that won the match.
	 * <br>
	 * Note that <b>null</b> will be returned
	 * if the mode is <b>solo</b>, or if
	 * the winning team couldn't be determined.
	 *
	 * @return the team that won the match, or <b>null</b>.
	 */
	public Team getWinnerTeam ( ) {
		return winner_team;
	}
	
	public boolean hasWinner ( ) {
		return winner_player != null || winner_team != null;
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}