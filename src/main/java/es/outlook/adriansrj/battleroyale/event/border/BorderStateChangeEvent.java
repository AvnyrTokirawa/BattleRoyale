package es.outlook.adriansrj.battleroyale.event.border;

import es.outlook.adriansrj.battleroyale.arena.border.BattleRoyaleArenaBorder;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaBorderState;
import org.bukkit.event.HandlerList;

/**
 * Thrown whenever the state of a {@link BattleRoyaleArenaBorder} changes.
 *
 * @author AdrianSR / 06/09/2021 / 06:06 p. m.
 */
public class BorderStateChangeEvent extends BorderEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final EnumArenaBorderState previous_state;
	protected final EnumArenaBorderState state;
	
	public BorderStateChangeEvent ( BattleRoyaleArenaBorder border ,
			EnumArenaBorderState previous_state , EnumArenaBorderState state ) {
		super ( border , true );
		
		this.previous_state = previous_state;
		this.state          = state;
	}
	
	public EnumArenaBorderState getPreviousState ( ) {
		return previous_state;
	}
	
	public EnumArenaBorderState getState ( ) {
		return state;
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}