package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import org.bukkit.event.HandlerList;

/**
 * Called before a player laves an arena.
 * <br>
 * Once the leave process is completed, the event
 * {@link PlayerArenaLeaveEvent} is fired.
 *
 * @see PlayerArenaLeaveEvent
 * @author AdrianSR / 25/10/2021 / 12:14 p. m.
 */
public class PlayerArenaPreLeaveEvent extends PlayerEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final BattleRoyaleArena arena;
	
	public PlayerArenaPreLeaveEvent ( Player player , BattleRoyaleArena arena ) {
		super ( player );
		this.arena = arena;
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}
