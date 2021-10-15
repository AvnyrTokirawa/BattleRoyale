package es.outlook.adriansrj.battleroyale.event.border;

import es.outlook.adriansrj.battleroyale.arena.border.BattleRoyaleArenaBorder;
import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderResize;
import org.bukkit.event.HandlerList;

/**
 * Thrown whenever a {@link BattleRoyaleArenaBorder} starts a new resizing point.
 *
 * @author AdrianSR / 06/09/2021 / 06:06 p. m.
 */
public class BorderResizeChangeEvent extends BorderEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final BattlefieldBorderResize previous_resize;
	protected final BattlefieldBorderResize resize;
	
	public BorderResizeChangeEvent ( BattleRoyaleArenaBorder border ,
			BattlefieldBorderResize previous_resize , BattlefieldBorderResize resize ) {
		super ( border );
		
		this.previous_resize = previous_resize;
		this.resize          = resize;
	}
	
	public BattlefieldBorderResize getPreviousResize ( ) {
		return previous_resize;
	}
	
	public BattlefieldBorderResize getResize ( ) {
		return resize;
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}