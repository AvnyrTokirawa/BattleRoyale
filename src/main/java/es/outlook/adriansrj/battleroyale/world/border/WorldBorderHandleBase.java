package es.outlook.adriansrj.battleroyale.world.border;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import org.bukkit.World;

import java.util.*;

/**
 * @author AdrianSR / 05/09/2021 / 11:55 p. m.
 */
abstract class WorldBorderHandleBase implements WorldBorderHandle {
	
	protected final Set < Player > players0 = Collections.synchronizedSet ( new HashSet <> ( ) );
	protected final Set < Player > players  = Collections.synchronizedSet ( new HashSet <> ( ) );
	
	// the world players must be in to see this border.
	protected final World world;
	
	WorldBorderHandleBase ( World world ) {
		this.world = Objects.requireNonNull ( world , "world cannot be null" );
	}
	
	@Override
	public synchronized Set < Player > getPlayers ( ) {
		return safeGetPlayers ( );
	}
	
	protected abstract void resetBorder ( Player player );
	
	protected abstract void refresh ( Player player );
	
	@Override
	public synchronized void refresh ( ) {
		safeGetPlayers ( ).forEach ( this :: refresh );
	}
	
	protected synchronized Set < Player > safeGetPlayers ( ) {
		this.players0.addAll ( players );
		
		// resetting border for excluded players
		Iterator < Player > iterator = players0.iterator ( );
		
		while ( iterator.hasNext ( ) ) {
			Player next = iterator.next ( );
			
			if ( next != null && !players.contains ( next ) ) {
				iterator.remove ( );
				
				resetBorder ( next );
			}
		}
		
		return players;
	}
}