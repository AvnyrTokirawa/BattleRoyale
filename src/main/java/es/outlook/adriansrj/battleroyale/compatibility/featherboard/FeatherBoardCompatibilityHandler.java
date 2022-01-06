package es.outlook.adriansrj.battleroyale.compatibility.featherboard;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import es.outlook.adriansrj.battleroyale.compatibility.PluginCompatibilityHandler;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

/**
 * @author AdrianSR / 06/01/2022 / 12:57 p. m.
 */
public final class FeatherBoardCompatibilityHandler extends PluginCompatibilityHandler {
	
	public static FeatherBoardCompatibilityHandler getInstance ( ) {
		return getPluginHandler ( FeatherBoardCompatibilityHandler.class );
	}
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public FeatherBoardCompatibilityHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	public void showScoreboard ( org.bukkit.entity.Player player , String scoreboard_name ) {
		FeatherBoardAPI.showScoreboard ( player , scoreboard_name );
	}
	
	public void showScoreboard ( Player player , String scoreboard_name ) {
		player.getBukkitPlayerOptional ( ).ifPresent ( bukkit -> showScoreboard ( bukkit , scoreboard_name ) );
	}
	
	public void hideScoreboard ( org.bukkit.entity.Player player , String scoreboard_name ) {
		FeatherBoardAPI.hideScoreboard ( player , scoreboard_name );
	}
	
	public void hideScoreboard ( Player player , String scoreboard_name ) {
		player.getBukkitPlayerOptional ( ).ifPresent ( bukkit -> hideScoreboard ( bukkit , scoreboard_name ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
