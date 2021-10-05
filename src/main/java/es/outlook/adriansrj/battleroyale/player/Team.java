package es.outlook.adriansrj.battleroyale.player;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Represents battle royale team.
 *
 * @author AdrianSR / 04/09/2021 / 10:48 p. m.
 */
public final class Team {
	
	final Set < Player > players = Collections.synchronizedSet ( new CopyOnWriteArraySet <> ( ) );
	
	final UUID              id;
	final BattleRoyaleArena arena;
	
	public Team ( BattleRoyaleArena arena ) {
		this.id    = UUID.randomUUID ( );
		this.arena = arena;
	}
	
	public Set < Player > getPlayers ( ) {
		return Collections.unmodifiableSet ( players );
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