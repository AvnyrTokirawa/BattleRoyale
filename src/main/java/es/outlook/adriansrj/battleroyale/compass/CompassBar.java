package es.outlook.adriansrj.battleroyale.compass;

import es.outlook.adriansrj.battleroyale.player.Player;

/**
 * Battle royale <b>RPG-compass bar</b> like.
 *
 * @author AdrianSR / 09/09/2021 / 12:42 p. m.
 */
public abstract class CompassBar {
	
	protected final Player player;
	
	protected CompassBar ( Player player ) {
		this.player = player;
	}
	
	public Player getPlayer ( ) {
		return player;
	}
	
	public abstract boolean isVisible ( );
	
	public abstract void setVisible ( boolean visible );
	
	public abstract void update ( );
	
	public abstract void destroy ( );
	
	/**
	 * Called whenever the player reconnects.
	 * <br>
	 * The player should be re-added to the handle in case it is an instance
	 * of {@link org.bukkit.boss.BossBar} as the bukkit boss bar system has severe
	 * problems, just like always.
	 */
	protected abstract void onPlayerReconnect ( );
}