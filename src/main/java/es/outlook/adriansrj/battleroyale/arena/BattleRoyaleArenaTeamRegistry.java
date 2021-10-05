package es.outlook.adriansrj.battleroyale.arena;

import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.player.Team;
import es.outlook.adriansrj.battleroyale.util.mode.BattleRoyaleModeUtil;
import es.outlook.adriansrj.core.util.function.FunctionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

/**
 * Class responsible for keeping track of the {@link Team}s in a {@link BattleRoyaleArena}.
 *
 * @author AdrianSR / 07/09/2021 / 01:17 p. m.
 */
public final class BattleRoyaleArenaTeamRegistry implements Iterable < Team > {
	
	private final Set < Team >      handle = Collections.synchronizedSet ( new CopyOnWriteArraySet <> ( ) );
	private final BattleRoyaleArena arena;
	
	BattleRoyaleArenaTeamRegistry ( BattleRoyaleArena arena ) {
		this.arena = arena;
	}
	
	/**
	 * Gets the {@link BattleRoyaleArena} that owns this registry.
	 *
	 * @return owner of this registry.
	 */
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	/**
	 * Gets the number of registered teams.
	 *
	 * @return the number of registered teams.
	 */
	public int getCount ( ) {
		return handle.size ( );
	}
	
	/**
	 * Gets whether there are no teams registered.
	 *
	 * @return whether there are no teams registered.
	 */
	public boolean isEmpty ( ) {
		return handle.isEmpty ( );
	}
	
	/**
	 * Gets whether the limit of teams (<b>determined by the mode of the arena</b>) is reached.
	 *
	 * @return whether the limit of teams is reached.
	 */
	public boolean isFull ( ) {
		BattleRoyaleMode mode = arena.getMode ( );
		return BattleRoyaleModeUtil.isLimitedTeams ( mode ) && getCount ( ) >= mode.getMaxTeams ( );
	}
	
	/**
	 * Creates and registers a new {@link Team}.
	 * <br>
	 * Note that <b>null</b> will be returned if for any reason the team couldn't be registered.
	 *
	 * @return the created team, or <b>null</b> if the team couldn't be registered.
	 */
	public Team createAndRegisterTeam ( ) {
		Team team = new Team ( arena );
		
		if ( registerTeam ( team ) ) {
			return team;
		} else {
			return null;
		}
	}
	
	/**
	 * Useful method that finds and returns the next team that is not full.
	 * <br>
	 * @return the next team that is not full,
	 * or <strong>null</strong> if all teams are full.
	 */
	public Team getNextNotFull ( ) {
		return handle.stream ( ).filter ( FunctionUtil.negate ( Team :: isFull ) )
				.findAny ( ).orElse ( null );
	}
	
	/**
	 * Registers the provided {@link Team}.
	 *
	 * @param team the team to register.
	 * @return whether the team was successfully registered or not.
	 */
	public boolean registerTeam ( Team team ) {
		return handle.add ( Objects.requireNonNull ( team , "team cannot be null" ) );
	}
	
	/**
	 * Unregisters the provided {@link Team}.
	 *
	 * @param team the team to unregister.
	 * @return whether the team was successfully unregistered or not.
	 */
	public boolean unregisterTeam ( Team team ) {
		return handle.remove ( team );
	}
	
	/**
	 * Clears this registry.
	 */
	public void clear ( ) {
		handle.clear ( );
	}
	
	/**
	 * Gets an unmodifiable view of the handle {@link Set} that holds the teams in this registry.
	 * <br>
	 * <b>Note that any attempt to modify the returned set will result in a
	 * exception as it is unmodifiable.</b>
	 *
	 * @return the {@link Set} that holds the teams in this registry,
	 * wrapped in a <b>unmodifiable</b> view.
	 */
	public Set < Team > getHandle ( ) {
		return Collections.unmodifiableSet ( handle );
	}
	
	@NotNull
	@Override
	public Iterator < Team > iterator ( ) {
		// getHandle() for a immutable iterator
		return getHandle ( ).iterator ( );
	}
	
	/**
	 * Returns a sequential {@code Stream} with this registry as its source.
	 *
	 * @return a sequential {@code Stream} over the teams in this registry.
	 */
	public Stream < Team > stream ( ) {
		return getHandle ( ).stream ( );
	}
}