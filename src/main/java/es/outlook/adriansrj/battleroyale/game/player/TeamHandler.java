package es.outlook.adriansrj.battleroyale.game.player;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.event.player.PlayerChangeTeamEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerJoinTeamEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerLeaveTeamEvent;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;

import java.util.Objects;

/**
 * Class responsible for {@link Team}s.
 *
 * @author AdrianSR / 06/09/2021 / 10:57 p. m.
 */
public final class TeamHandler extends PluginHandler {
	
	public static TeamHandler getInstance ( ) {
		return getPluginHandler ( TeamHandler.class );
	}
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public TeamHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	/**
	 * Sets the provided {@link Player} on the specified team.
	 * <br>
	 * Note that passing a <b>null</b> team is the equivalent of calling {@link #leaveTeam(Player)}.
	 * <br>
	 * <b>Note that the following conditions must be met in order to successfully set the team:</b>
	 * <ul>
	 *     <li>The team and the player must be in the same arena.</li>
	 *     <li>The arena must be waiting: {@link BattleRoyaleArena#getState()} == {@link EnumArenaState#WAITING}</li>
	 * </ul>
	 *
	 * @param player the player to set.
	 * @param team the team to set.
	 * @return whether the player was set on the team or not.
	 */
	public synchronized boolean setTeam ( Player player , Team team ) {
		if ( team == null ) {
			return leaveTeam ( player );
		} else if ( !Objects.equals ( team , player.getTeam ( ) ) ) {
			if ( team.getArena ( ).getState ( ) != EnumArenaState.WAITING ) {
				// team arena must be in waiting state in order to accept members
				return false;
			}
			
			if ( Objects.equals ( player.getArena ( ) , team.getArena ( ) ) ) {
				Team previous_team = player.team;
				
				player.team = team;
				if ( !player.team.players.contains ( player ) ) {
					player.team.players.add ( player );
				}
				
				if ( previous_team == null ) {
					new PlayerJoinTeamEvent ( player , team ).call ( );
				} else {
					// player list
					previous_team.players.remove ( player );
					
					// firing event
					new PlayerChangeTeamEvent ( player , previous_team , team ).call ( );
				}
				return true;
			} else {
				// player and team must be in the same arena
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Removes the provided {@link Player} from the specified team.
	 * <br>
	 * <b>Note that if the arena is running ({@link BattleRoyaleArena#getState()} == {@link EnumArenaState#RUNNING}),
	 * the player will also leave the arena, and sent back to the lobby</b>.
	 *
	 * @param player the player that leaves.
	 * @return whether the player leave the team or not.
	 */
	public synchronized boolean leaveTeam ( Player player ) {
		Team team = player.team;
		
		if ( team != null ) {
			team.players.remove ( player );
			
			// empty teams will automatically be removed
			if ( team.players.isEmpty ( ) ) {
				team.getArena ( ).getTeamRegistry ( ).unregisterTeam ( team );
			}
			
			player.team = null;
			
			// firing event
			new PlayerLeaveTeamEvent ( player , team ).call ( );
			
			// sending back to lobby in case the game is running
			if ( team.getArena ( ).getState ( ) == EnumArenaState.RUNNING ) {
				BattleRoyaleArenaHandler.getInstance ( ).leaveArena ( player );
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}