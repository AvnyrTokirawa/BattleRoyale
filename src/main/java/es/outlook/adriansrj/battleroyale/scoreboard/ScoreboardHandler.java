package es.outlook.adriansrj.battleroyale.scoreboard;

import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaLeaveEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaSetEvent;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

/**
 * Class responsible for handling {@link Scoreboard}s.
 *
 * @author AdrianSR / 08/10/2021 / 05:17 p. m.
 */
public final class ScoreboardHandler extends PluginHandler {
	
	public static ScoreboardHandler getInstance ( ) {
		return getPluginHandler ( ScoreboardHandler.class );
	}
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public ScoreboardHandler ( BattleRoyale plugin ) {
		super ( plugin ); register ( );
		
		// task responsible for updating the scoreboards
		Bukkit.getScheduler ( ).runTaskTimer ( plugin , ( )
				-> Player.getPlayers ( ).stream ( ).map ( Player :: getBRScoreboard )
				.filter ( Objects :: nonNull ).filter ( Scoreboard :: isVisible )
				.forEach ( Scoreboard :: update ) , 0L , 15L );
	}
	
	// this event handler is responsible for showing
	// the scoreboard when a player joins an arena.
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onJoinArena ( PlayerArenaSetEvent event ) {
		setScoreboardVisible ( event.getPlayer ( ) , true );
	}
	
	// this event handler is responsible for hiding
	// the scoreboard when a player leaves an arena.
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onLeaveArena ( PlayerArenaLeaveEvent event ) {
		setScoreboardVisible ( event.getPlayer ( ) , false );
	}
	
	private void setScoreboardVisible ( Player player , boolean visible ) {
		Scoreboard scoreboard = player.getBRScoreboard ( );
		
		if ( scoreboard != null ) {
			scoreboard.setVisible ( visible );
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
