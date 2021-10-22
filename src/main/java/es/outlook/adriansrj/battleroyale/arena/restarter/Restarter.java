package es.outlook.adriansrj.battleroyale.arena.restarter;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.core.util.Duration;

/**
 * @author AdrianSR / 21/10/2021 / 09:13 p. m.
 */
public class Restarter {
	
	protected final BattleRoyaleArena arena;
	
	public Restarter ( BattleRoyaleArena arena ) {
		this.arena = arena;
	}
	
	public boolean start ( Duration countdown_duration ) {
		return false;
	}
}