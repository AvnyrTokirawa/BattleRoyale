package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.enums.EnumStat;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Called whenever a stat of a player is set.
 *
 * @author AdrianSR / 22/10/2021 / 01:31 p. m.
 */
public class PlayerStatSetEvent extends PlayerEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final UUID     uuid;
	protected final EnumStat stat_type;
	protected final int      previous_value;
	protected final int      value;
	
	public PlayerStatSetEvent ( UUID uuid , EnumStat stat_type , int previous_value , int value ) {
		super ( Player.getPlayer ( uuid ) );
		
		this.uuid           = uuid;
		this.stat_type      = stat_type;
		this.previous_value = previous_value;
		this.value          = value;
	}
	
	public PlayerStatSetEvent ( Player player , EnumStat stat_type , int previous_value , int value ) {
		this ( player.getUniqueId ( ) , stat_type , previous_value , value );
	}
	
	/**
	 * Gets the unique {@link UUID} of the player
	 * who owns the stat being set.
	 *
	 * @return Gets the unique id of the player
	 * who owns the stat being set.
	 */
	public UUID getUniqueId ( ) {
		return uuid;
	}
	
	/**
	 * Gets the player who owns the stat being set.
	 * <br>
	 * Note that there might be players fetched from
	 * the database (using the respective {@link UUID}),
	 * that have not joined the server; in that case,
	 * <b>null</b> will be returned.
	 *
	 * @return the player who owns the stat being set, or <b>null</b>.
	 * @see #getUniqueId()
	 */
	@Override
	public Player getPlayer ( ) {
		return super.getPlayer ( );
	}
	
	public EnumStat getStatType ( ) {
		return stat_type;
	}
	
	public int getPreviousValue ( ) {
		return previous_value;
	}
	
	public int getValue ( ) {
		return value;
	}
	
	public boolean isIncrementing ( ) {
		return value > previous_value;
	}
	
	public boolean isDecrementing ( ) {
		return value < previous_value;
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}