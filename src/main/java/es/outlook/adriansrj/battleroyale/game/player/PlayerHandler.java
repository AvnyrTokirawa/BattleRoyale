package es.outlook.adriansrj.battleroyale.game.player;

import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author AdrianSR / 28/08/2021 / 10:55 a. m.
 */
public final class PlayerHandler extends PluginHandler {
	
	private static final Map < UUID, Player > PLAYER_MAP = new ConcurrentHashMap <> ( );
	
	/**
	 * Get all the battle royale players that have been loaded so far.
	 *
	 * @return loaded battle royale players.
	 */
	public static Collection < Player > getPlayers ( ) {
		return Collections.unmodifiableCollection ( PLAYER_MAP.values ( ) );
	}
	
	/**
	 * Gets the corresponding battle royale player from a given {@link UUID}. <br> Note that the {@link UUID} should be
	 * the <b>same</b> as would be used to get a certain <b>bukkit player</b>.
	 *
	 * @param id the unique {@link UUID} of the player to get.
	 *
	 * @return the battle royale player that corresponds to the given bukkit player.
	 */
	public static Player getPlayer ( UUID id ) {
		Player br_player = PLAYER_MAP.get ( id );
		
		if ( br_player == null ) {
			org.bukkit.entity.Player player         = Bukkit.getPlayer ( id );
			OfflinePlayer            offline_player = Bukkit.getOfflinePlayer ( id );
			
			if ( player != null || offline_player != null ) {
				PLAYER_MAP.put ( id , br_player = new Player (
						( player != null ? player : offline_player ).getName ( ) ,
						id , player ) );
			}
			
			if ( br_player != null && EnumMainConfiguration.ENABLE_DATABASE.getAsBoolean ( ) ) {
				br_player.getDataStorage ( ).fetch ( );
			}
		}
		return br_player;
	}
	
	/**
	 * Gets the corresponding battle royale player from a given bukkit {@link org.bukkit.entity.Player}
	 *
	 * @param player the bukkit player.
	 *
	 * @return the battle royale player that corresponds to the given bukkit player.
	 */
	public static Player getPlayer ( org.bukkit.entity.Player player ) {
		return player != null ? getPlayer ( player.getUniqueId ( ) ) : null;
	}
	
	//	public static PlayerHandler getInstance ( ) {
	//		return PluginHandler.getPluginHandler ( PlayerHandler.class );
	//	}
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public PlayerHandler ( BattleRoyale plugin ) {
		super ( plugin );
		register ( );
		
		// this will load the players that are online right now
		Bukkit.getOnlinePlayers ( ).forEach ( this :: load );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onJoin ( PlayerJoinEvent event ) {
		// loads the player that joins the server.
		load ( event.getPlayer ( ) );
	}
	
	private void load ( org.bukkit.entity.Player player ) {
		if ( !PLAYER_MAP.containsKey ( player.getUniqueId ( ) ) ) {
			getPlayer ( player ); // this will automatically load the player.
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}