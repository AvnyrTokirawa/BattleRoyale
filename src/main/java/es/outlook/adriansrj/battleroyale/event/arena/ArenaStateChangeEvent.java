package es.outlook.adriansrj.battleroyale.event.arena;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import org.bukkit.event.HandlerList;

/**
 * Called whenever the state of an {@link BattleRoyaleArena} changes.
 *
 * @author AdrianSR / 06/09/2021 / 10:05 a. m.
 */
public class ArenaStateChangeEvent extends ArenaEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final EnumArenaState previous_state;
	protected final EnumArenaState state;
	
	public ArenaStateChangeEvent ( BattleRoyaleArena arena ,
			EnumArenaState previous_state , EnumArenaState state ) {
		super ( arena , true );
		
		this.previous_state = previous_state;
		this.state          = state;
	}
	
	public EnumArenaState getPreviousState ( ) {
		return previous_state;
	}
	
	public EnumArenaState getState ( ) {
		return state;
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}