package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.parachute.ParachuteInstance;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import org.bukkit.event.HandlerList;

/**
 * Called whenever a parachute closes.
 *
 * @author AdrianSR / 10/09/2021 / 12:49 p. m.
 */
public class PlayerCloseParachuteEvent extends PlayerEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final ParachuteInstance parachute;
	
	public PlayerCloseParachuteEvent ( Player player , ParachuteInstance parachute ) {
		super ( player , true );
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