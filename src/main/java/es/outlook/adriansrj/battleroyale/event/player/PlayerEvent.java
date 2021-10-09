package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.events.CustomEvent;

/**
 * Base class for {@link Player}-related events.
 *
 * @author AdrianSR / 06/09/2021 / 11:11 p. m.
 */
public abstract class PlayerEvent extends CustomEvent {
	
	protected final Player player;
	
	public PlayerEvent ( Player player , boolean async ) {
		super ( async );
		this.player = player;
	}
	
	public PlayerEvent ( Player player ) {
		this ( player , false );
	}
	
	public Player getPlayer ( ) {
		return player;
	}
}