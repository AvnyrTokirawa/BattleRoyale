package es.outlook.adriansrj.battleroyale.game.player;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents battle royale team.
 *
 * @author AdrianSR / 04/09/2021 / 10:48 p. m.
 */
public final class Team {
	
	final ConcurrentLinkedQueue < Player > players = new ConcurrentLinkedQueue <> ( );
	
	final UUID              id;
	final BattleRoyaleArena arena;
	
	public Team ( BattleRoyaleArena arena ) {
		this.id    = UUID.randomUUID ( );
		this.arena = arena;
	}
	
	public Collection < Player > getPlayers ( ) {
		return Collections.unmodifiableCollection ( players );
	}
	
	public int getCount ( ) {
		return players.size ( );
	}
	
	public boolean isEmpty ( ) {
		return players.isEmpty ( );
	}
	
	public boolean isFull ( ) {
		return getCount ( ) >= arena.getMode ( ).getMaxPlayersPerTeam ( );
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	/**
	 * Sets the provided {@link Player} on this team.
	 * <br>
	 * <b>Note that the following conditions must be met in order to successfully set the team:</b>
	 * <ul>
	 *     <li>The team and the player must be in the same arena.</li>
	 *     <li>The arena must be waiting: {@link BattleRoyaleArena#getState()} == {@link EnumArenaState#WAITING}</li>
	 * </ul>
	 *
	 * @param player the player to set.
	 * @return whether the player was set on this team or not.
	 */
	public boolean join ( Player player ) {
		return player.setTeam ( this );
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
	public boolean leave ( Player player ) {
		return Objects.equals ( this , player.getTeam ( ) ) && player.leaveTeam ( );
	}
	
	/**
	 * Gets the numeric id of the provided {@link Player} within this team.
	 * <br>
	 * This id is useful for differentiating players, as it is actually
	 * the index of the player within this team.
	 *
	 * @param player the player to get.
	 * @return the numeric id (index within this team) of the provided player.
	 */
	public int getNumericId ( Player player ) {
		Validate.notNull ( player , "player cannot be null" );
		int id = 0;
		
		for ( Player other : players ) {
			if ( Objects.equals ( player , other ) ) {
				return id;
			} else {
				id++;
			}
		}
		return id;
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		Team team = ( Team ) o;
		return id.equals ( team.id );
	}
	
	@Override
	public int hashCode ( ) {
		return Objects.hash ( id );
	}
}