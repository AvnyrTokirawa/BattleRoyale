package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import org.bukkit.event.Cancellable;

/**
 * Base class for {@link Player}-related cancellable events.
 *
 * @author AdrianSR / 06/09/2021 / 11:11 p. m.
 */
public abstract class PlayerEventCancellable extends PlayerEvent implements Cancellable {
	
	protected boolean cancelled;
	
	public PlayerEventCancellable ( Player player ) {
		super ( player );
	}
	
	@Override
	public boolean isCancelled ( ) {
		return cancelled;
	}
	
	@Override
	public void setCancelled ( boolean cancelled ) {
		this.cancelled = cancelled;
	}
}