package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * This class is responsible from stopping players
 * from move or interact once the game is over.
 * <br>
 * This effect is very common in the battle royale
 * games (Pubg, Fortnite...).
 *
 * @author AdrianSR / 23/10/2021 / 10:30 a. m.
 */
public final class MoveInteractListener extends BattleRoyaleArenaListener {
	
	public MoveInteractListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onMove ( PlayerMoveEvent event ) {
	
	}
}
