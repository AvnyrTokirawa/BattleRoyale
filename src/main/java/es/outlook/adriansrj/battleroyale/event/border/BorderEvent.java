package es.outlook.adriansrj.battleroyale.event.border;

import es.outlook.adriansrj.battleroyale.arena.border.BattleRoyaleArenaBorder;
import es.outlook.adriansrj.core.events.CustomEvent;

/**
 * Base class for battle royale arena border-related events.
 *
 * @author AdrianSR / 07/09/2021 / 04:49 p. m.
 */
public abstract class BorderEvent extends CustomEvent {
	
	protected final BattleRoyaleArenaBorder border;
	
	public BorderEvent ( BattleRoyaleArenaBorder border , boolean async ) {
		super ( async );
		this.border = border;
	}
	
	public BorderEvent ( BattleRoyaleArenaBorder border ) {
		this ( border , false );
	}
	
	public BattleRoyaleArenaBorder getBorder ( ) {
		return border;
	}
}