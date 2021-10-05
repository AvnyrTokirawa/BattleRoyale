package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.player.Player;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a player laves an arena.
 *
 * @author AdrianSR / 04/09/2021 / 12:25 a. m.
 */
public class PlayerArenaLeaveEvent extends PlayerEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final BattleRoyaleArena arena;
	
	public PlayerArenaLeaveEvent ( Player player , BattleRoyaleArena arena ) {
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
