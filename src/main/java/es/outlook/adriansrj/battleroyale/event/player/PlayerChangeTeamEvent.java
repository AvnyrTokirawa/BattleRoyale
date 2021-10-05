package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.battleroyale.player.Team;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a player changes team.
 *
 * @author AdrianSR / 06/09/2021 / 11:11 p. m.
 */
public class PlayerChangeTeamEvent extends PlayerEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final Team previous_team;
	protected final Team team;
	
	public PlayerChangeTeamEvent ( Player player , Team previous_team , Team team ) {
		super ( player );
		
		this.previous_team = previous_team;
		this.team          = team;
	}
	
	public Team getPreviousTeam ( ) {
		return previous_team;
	}
	
	public Team getTeam ( ) {
		return team;
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}
