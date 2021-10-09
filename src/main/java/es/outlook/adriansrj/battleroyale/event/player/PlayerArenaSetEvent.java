package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import org.bukkit.event.HandlerList;

/**
 * Called whenever the arena of a player is set.
 *
 * @author AdrianSR / 04/09/2021 / 12:25 a. m.
 */
public class PlayerArenaSetEvent extends PlayerEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final BattleRoyaleArena arena;
	
	public PlayerArenaSetEvent ( Player player , BattleRoyaleArena arena ) {
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