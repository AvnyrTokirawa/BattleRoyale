package es.outlook.adriansrj.battleroyale.compass;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

/**
 * Class responsible for handling {@link CompassBar}s.
 *
 * @author AdrianSR / 09/09/2021 / 01:44 p. m.
 */
public final class CompassBarHandler extends PluginHandler {
	
	public static CompassBarHandler getInstance ( ) {
		return getPluginHandler ( CompassBarHandler.class );
	}
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public CompassBarHandler ( BattleRoyale plugin ) {
		super ( plugin );
		register ( );
		
		// task responsible for updating the compasses
		Bukkit.getScheduler ( ).runTaskTimerAsynchronously ( plugin , ( )
				-> Player.getPlayers ( ).stream ( ).map ( Player :: getCompass )
				.filter ( Objects :: nonNull ).filter ( CompassBar :: isVisible )
				.forEach ( CompassBar :: update ) , 0 , 0 );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onPlayerReconnect ( PlayerJoinEvent event ) {
		Player     player = Player.getPlayer ( event.getPlayer ( ) );
		CompassBar bar    = player.getCompass ( );
		
		if ( bar != null ) {
			bar.onPlayerReconnect ( );
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}