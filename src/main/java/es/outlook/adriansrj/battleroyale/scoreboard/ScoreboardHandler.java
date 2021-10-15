package es.outlook.adriansrj.battleroyale.scoreboard;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.bukkit.Bukkit;

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
		super ( plugin );
		
		// task responsible for updating the scoreboards
		Bukkit.getScheduler ( ).runTaskTimerAsynchronously ( plugin , ( )
				-> Player.getPlayers ( ).stream ( ).map ( Player :: getBRScoreboard )
				.filter ( Objects :: nonNull ).filter ( Scoreboard :: isVisible )
				.forEach ( Scoreboard :: update ) , 0L , 15L );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
