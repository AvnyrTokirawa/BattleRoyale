package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.bus.BusInstance;
import es.outlook.adriansrj.battleroyale.player.Player;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a player jumps off of the {@link BusInstance}.
 *
 * @author AdrianSR / 10/09/2021 / 12:49 p. m.
 */
public class PlayerJumpOffBusEvent extends PlayerEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final BusInstance bus;
	
	public PlayerJumpOffBusEvent ( Player player , BusInstance bus ) {
		super ( player );
		this.bus = bus;
	}
	
	public BusInstance getBus ( ) {
		return bus;
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}