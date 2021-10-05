package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.parachute.ParachuteInstance;
import es.outlook.adriansrj.battleroyale.player.Player;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a player opens a parachute from a <b>toggle-flight</b>.
 *
 * @author AdrianSR / 10/09/2021 / 12:49 p. m.
 */
public class PlayerOpenParachuteEvent extends PlayerEventCancellable {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final ParachuteInstance parachute;
	
	public PlayerOpenParachuteEvent ( Player player , ParachuteInstance parachute ) {
		super ( player );
		this.parachute = parachute;
	}
	
	public ParachuteInstance getParachute ( ) {
		return parachute;
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}