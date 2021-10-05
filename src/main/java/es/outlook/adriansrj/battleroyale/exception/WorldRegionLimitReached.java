package es.outlook.adriansrj.battleroyale.exception;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;

/**
 * Thrown whenever the world of a {@link BattleRoyaleArena}
 * reaches the limit of regions.
 *
 * @author AdrianSR / 14/09/2021 / 03:01 p. m.
 */
public class WorldRegionLimitReached extends IllegalStateException {
	
	public WorldRegionLimitReached ( ) {
		// no message
	}
	
	public WorldRegionLimitReached ( String s ) {
		super ( s );
	}
	
	public WorldRegionLimitReached ( String message , Throwable cause ) {
		super ( message , cause );
	}
	
	public WorldRegionLimitReached ( Throwable cause ) {
		super ( cause );
	}
}
