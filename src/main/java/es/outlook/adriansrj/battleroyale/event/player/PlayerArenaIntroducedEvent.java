package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.Validate;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Called whenever a player is introduced in an arena.
 *
 * @author AdrianSR / 10/11/2021 / 11:42 a. m.
 */
public class PlayerArenaIntroducedEvent extends PlayerEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final BattleRoyaleArena arena;
	protected final boolean           player_spawn;
	protected       Location          spawn;
	protected final boolean           spectator;
	
	public PlayerArenaIntroducedEvent ( Player player , BattleRoyaleArena arena , Location spawn , boolean spectator ) {
		super ( player );
		
		this.arena        = arena;
		this.spawn        = spawn;
		this.player_spawn = spawn != null && !spectator;
		this.spectator    = spectator;
	}
	
	public PlayerArenaIntroducedEvent ( Player player , BattleRoyaleArena arena , boolean spectator ) {
		this ( player , arena , null , spectator );
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	/**
	 * Gets where the player will be spawned.
	 * <br>
	 * Note that <b>null</b> will be returned if the
	 * player will be spawned in the bus, or introduced
	 * as spectator.
	 *
	 * @return where the player will be spawned , or <n>null</n>.
	 */
	public Location getSpawn ( ) {
		return isPlayerSpawn ( ) ? spawn : null;
	}
	
	/**
	 * Sets where the player will be spawned.
	 * <br>
	 * Note that this will not have any effect if the
	 * player will be spawned in the bus, or introduced
	 * as spectator.
	 *
	 * @param spawn where the player will be spawned.
	 */
	public void setSpawn ( Location spawn ) {
		Validate.notNull ( spawn , "spawn cannot be null" );
		Validate.isTrue ( Objects.equals ( spawn.getWorld ( ) , arena.getWorld ( ) ) ,
						  "spawn must be in the world of the arena." );
		
		this.spawn = spawn;
	}
	
	/**
	 * Gets whether the player will be spawned
	 * in a player spawn. This means that the
	 * player will not spawn in the bus, but
	 * instead in a specific player spawn.
	 *
	 * @return whether the player will be spawned in a player spawn.
	 */
	public boolean isPlayerSpawn ( ) {
		return player_spawn;
	}
	
	/**
	 * Gets whether the player will be introduced
	 * as a spectator.
	 * <br>
	 * Players are usually introduced as spectators when
	 * a non-full team couldn't be found; or when for any
	 * reason the mode of the arena did not allow the player
	 * to be introduced.
	 *
	 * @return whether the player will be introduced as a spectator.
	 */
	public boolean isSpectator ( ) {
		return spectator;
	}
	
	@NotNull
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}
