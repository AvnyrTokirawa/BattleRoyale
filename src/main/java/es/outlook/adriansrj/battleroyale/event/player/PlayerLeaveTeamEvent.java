package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.Team;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a player leaves a team.
 *
 * @author AdrianSR / 06/09/2021 / 11:11 p. m.
 */
public class PlayerLeaveTeamEvent extends PlayerEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final Team team;
	
	public PlayerLeaveTeamEvent ( Player player , Team team ) {
		super ( player );
		this.team = team;
	}
	
	public Team getTeam ( ) {
		return team;
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}
