package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaStat;
import es.outlook.adriansrj.battleroyale.enums.EnumStat;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaEndEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerDeathEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerKnockedOutEvent;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.Team;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

/**
 * Class responsible for keeping track of the stats of
 * the arenas and the players on the battlefield.
 *
 * @author AdrianSR / 20/09/2021 / 04:16 p. m.
 */
public final class StatListener extends BattleRoyaleArenaListener {
	
	public StatListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	// event handler responsible for
	// handling the knock-related stats.
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onKnocked ( PlayerKnockedOutEvent event ) {
		Player player  = event.getPlayer ( );
		Player knocker = event.getKnocker ( );
		
		if ( knocker != null ) {
			incrementStat ( knocker , EnumStat.KNOCKED_ENEMIES );
		}
		
		incrementStat ( player , EnumStat.TIMES_KNOCKED );
		incrementArenaStat ( player.getArena ( ) , EnumArenaStat.KNOCKS );
	}
	
	// event handler responsible for
	// handling the kill-related stats.
	@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onDie ( PlayerDeathEvent event ) {
		Player            player = event.getPlayer ( );
		Player            killer = event.getKiller ( );
		BattleRoyaleArena arena  = player.getArena ( );
		
		if ( killer != null ) {
			incrementStat ( killer , EnumStat.KILLS );
			
			if ( event.isHeadshot ( ) ) {
				incrementStat ( killer , EnumStat.HEADSHOTS );
			}
		}
		
		incrementStat ( player , EnumStat.DEATHS );
		incrementArenaStat ( arena , EnumArenaStat.KILLS );
		
		if ( event.isHeadshot ( ) ) {
			incrementArenaStat ( arena , EnumArenaStat.HEADSHOTS );
		}
	}
	
	// event handler responsible for
	// handling the wins/losses-related stats.
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onWinLose ( ArenaEndEvent event ) {
		Player winner_player = event.getWinnerPlayer ( );
		Team   winner_team   = event.getWinnerTeam ( );
		
		if ( winner_player != null || winner_team != null ) {
			for ( Team team : event.getArena ( ).getTeamRegistry ( ) ) {
				boolean winner = Objects.equals (
						team , winner_team != null ? winner_team : winner_player.getTeam ( ) );
				
				for ( Player member : team.getPlayers ( ) ) {
					if ( winner && member.isOnline ( ) ) {
						incrementStat ( member , EnumStat.WINS );
					} else {
						incrementStat ( member , EnumStat.LOSSES );
					}
				}
			}
		}
	}
	
	private void incrementStat ( Player player , EnumStat stat ) {
		player.getDataStorage ( ).incrementStat ( stat , 1 , true );
		player.getDataStorage ( ).incrementTempStat ( stat , 1 );
		
		// reward
		int reward = stat.getReward ( );
		
		if ( reward > 0 ) {
			player.getDataStorage ( ).balanceDeposit ( reward , true );
		}
	}
	
	private void incrementArenaStat ( BattleRoyaleArena arena , EnumArenaStat stat ) {
		arena.getStats ( ).increment ( stat , 1 );
	}
}