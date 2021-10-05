package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.enums.EnumStat;
import es.outlook.adriansrj.battleroyale.event.player.PlayerDeathEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerKnockedOutEvent;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.player.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * Class responsible for keeping track of the stats
 * of the players on the battlefield.
 *
 * @author AdrianSR / 20/09/2021 / 04:16 p. m.
 */
public final class StatListener extends BattleRoyaleArenaListener {
	
	public StatListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	// event handler responsible for
	// handling the knock-related stats.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onKnocked ( PlayerKnockedOutEvent event ) {
		Player player  = event.getPlayer ( );
		Player knocker = event.getKnocker ( );
		
		if ( knocker != null ) {
			knocker.getDataStorage ( ).incrementStat (
					EnumStat.KNOCKED_ENEMIES , 1 , true );
		}
		
		player.getDataStorage ( ).incrementStat (
				EnumStat.TIMES_KNOCKED , 1 , true );
	}
	
	// event handler responsible for
	// handling the kill-related stats.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onDie ( PlayerDeathEvent event ) {
		Player player = event.getPlayer ( );
		Player killer = event.getKiller ( );
		
		if ( killer != null ) {
			killer.getDataStorage ( ).incrementStat (
					EnumStat.KILLS , 1 , true );
		}
		
		player.getDataStorage ( ).incrementStat (
				EnumStat.DEATHS , 1 , true );
	}
}