package es.outlook.adriansrj.battleroyale.event.arena;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import org.bukkit.event.HandlerList;

/**
 * Called whenever an arena is prepared.
 *
 * @author AdrianSR / 21/10/2021 / 08:36 a. m.
 */
public class ArenaPreparedEvent extends ArenaEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	public ArenaPreparedEvent ( BattleRoyaleArena arena ) {
		super ( arena , true );
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}