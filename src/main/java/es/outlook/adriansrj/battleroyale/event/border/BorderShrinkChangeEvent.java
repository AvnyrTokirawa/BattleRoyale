package es.outlook.adriansrj.battleroyale.event.border;

import es.outlook.adriansrj.battleroyale.arena.border.BattleRoyaleArenaBorder;
import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderShrink;
import org.bukkit.event.HandlerList;

/**
 * Thrown whenever a {@link BattleRoyaleArenaBorder} starts a new shrinking point.
 *
 * @author AdrianSR / 06/09/2021 / 06:06 p. m.
 */
public class BorderShrinkChangeEvent extends BorderEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final BattlefieldBorderShrink previous_shrink;
	protected final BattlefieldBorderShrink shrink;
	
	public BorderShrinkChangeEvent ( BattleRoyaleArenaBorder border ,
			BattlefieldBorderShrink previous_shrink , BattlefieldBorderShrink shrink ) {
		super ( border );
		
		this.previous_shrink = previous_shrink;
		this.shrink          = shrink;
	}
	
	public BattlefieldBorderShrink getPreviousShrink ( ) {
		return previous_shrink;
	}
	
	public BattlefieldBorderShrink getShrink ( ) {
		return shrink;
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}