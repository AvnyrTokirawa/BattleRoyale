package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.player.Player;
import org.bukkit.event.HandlerList;

/**
 * Thrown whenever a player is knocked out.
 *
 * @author AdrianSR / 06/09/2021 / 11:11 p. m.
 */
public class PlayerKnockedOutEvent extends PlayerEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final Player knocker;
	
	public PlayerKnockedOutEvent ( Player player , Player knocker ) {
		super ( player );
		this.knocker = knocker;
	}
	
	public PlayerKnockedOutEvent ( Player player ) {
		this ( player , null );
	}
	
	public Player getKnocker ( ) {
		return knocker;
	}
	
	/**
	 * Gets whether the player was knocked out by
	 * another player.
	 * <br>
	 * This is the equivalent of using: <b><code>getKnocker() != null</code></b>.
	 *
	 * @return whether the player was knocked out by another player.
	 */
	public boolean hasKnocker ( ) {
		return knocker != null;
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}
