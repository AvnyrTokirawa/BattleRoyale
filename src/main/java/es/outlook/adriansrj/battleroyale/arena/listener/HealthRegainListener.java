package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;

/**
 * Class that enables the battle royale health-regain system.
 * <br>
 * In Battle Royale, players do not automatically regain health,
 * instead they will need to use a medkit or healing potion to regain health.
 *
 * @author AdrianSR / 13/10/2021 / 08:51 a. m.
 */
public final class HealthRegainListener extends BattleRoyaleArenaListener {
	
	public HealthRegainListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onAutoHealth ( final EntityRegainHealthEvent event ) {
		if ( event.getEntity ( ) instanceof Player
				&& event.getRegainReason ( ) == EntityRegainHealthEvent.RegainReason.SATIATED ) {
			event.setCancelled ( true );
		}
	}
}