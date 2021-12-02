package es.outlook.adriansrj.battleroyale.bus;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.battlefield.bus.BusSpawn;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

/**
 * Battle royale bus.
 *
 * @author AdrianSR / 07/09/2021 / 05:24 p. m.
 * @param <C> configuration.
 */
public abstract class BusInstance < C extends Bus > {
	
	protected BattleRoyaleArena arena;
	protected BusSpawn          spawn;
	
	protected BusInstance ( ) {
		// just protected
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	public BusSpawn getSpawn ( ) {
		return spawn;
	}
	
	public abstract C getConfiguration ( );
	
	public abstract Vector getCurrentLocation ( );
	
	/**
	 * Gets whether the door of the bus is open (<b>whether a player can jump or not.</b>).
	 *
	 * @return whether the door of the bus is open.
	 */
	public boolean isDoorOpen ( ) {
		return isStarted ( ) && arena.getFullBounds ( ).unproject ( getCurrentLocation ( ) )
				.distance ( spawn.getStartLocation ( ) ) >= spawn.getDoorPointDistance ( );
	}
	
	/**
	 * Gets whether this bus has started its rout.
	 *
	 * @return whether this bus has started its rout.
	 */
	public abstract boolean isStarted ( );
	
	/**
	 * Gets whether this bus finished its route.
	 *
	 * @return whether this bus finished its route.
	 */
	public abstract boolean isFinished ( );
	
	/**
	 * Gets whether the provided {@link Player} is a passenger of this bus.
	 *
	 * @param player the player to check.
	 * @return whether the provided player is a passenger of this bus.
	 */
	public abstract boolean isPassenger ( Player player );
	
	public void start ( BattleRoyaleArena arena , BusSpawn spawn ) {
		Validate.notNull ( arena , "arena cannot be null" );
		Validate.isTrue ( !isStarted ( ) , "bus already started" );
		Validate.isTrue ( !isFinished ( ) , "bus already finished; call restart() first" );
		Validate.notNull ( spawn , "spawn cannot be null" );
		Validate.isTrue ( spawn.isValid ( ) , "spawn must be valid" );
		
		this.arena = arena;
		this.spawn = spawn;
		
		// then starting
		this.start ( );
	}
	
	protected abstract void start ( );
	
	public abstract void finish ( );
	
	public abstract void restart ( );
	
	// ----- utils
	
	protected void syncCheck ( ) {
		Validate.isTrue ( Bukkit.isPrimaryThread ( ) , "must run on server thread" );
	}
}