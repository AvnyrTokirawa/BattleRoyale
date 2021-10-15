package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Class that disables the vanilla food system.
 *
 * @author AdrianSR / 13/10/2021 / 08:48 a. m.
 */
public final class FoodLevelListener extends BattleRoyaleArenaListener {
	
	public FoodLevelListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onChange ( FoodLevelChangeEvent event ) {
		event.setCancelled ( true );
	}
}
