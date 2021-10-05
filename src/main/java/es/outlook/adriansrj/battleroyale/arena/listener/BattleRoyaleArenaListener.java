package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * A {@link BattleRoyaleArena} listener.
 *
 * @author AdrianSR / 13/09/2021 / 04:16 p. m.
 */
public abstract class BattleRoyaleArenaListener implements Listener {
	
	public BattleRoyaleArenaListener ( BattleRoyale plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
}