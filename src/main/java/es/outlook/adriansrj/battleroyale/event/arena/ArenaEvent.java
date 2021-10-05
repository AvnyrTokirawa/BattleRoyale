package es.outlook.adriansrj.battleroyale.event.arena;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.core.events.CustomEvent;

/**
 * Base class for battle royale arena-related events.
 *
 * @author AdrianSR / 07/09/2021 / 04:46 p. m.
 */
public abstract class ArenaEvent extends CustomEvent {
	
	protected final BattleRoyaleArena arena;
	
	public ArenaEvent ( BattleRoyaleArena arena , boolean async ) {
		super ( async );
		this.arena = arena;
	}
	
	public ArenaEvent ( BattleRoyaleArena arena ) {
		this.arena = arena;
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
}