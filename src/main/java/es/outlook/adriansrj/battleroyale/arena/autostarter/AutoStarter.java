package es.outlook.adriansrj.battleroyale.arena.autostarter;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaTeamRegistry;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.util.Duration;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

/**
 *
 *
 * @author AdrianSR / 20/10/2021 / 12:00 p. m.
 */
public class AutoStarter implements Listener {
	
	/**
	 * @author AdrianSR / 20/10/2021 / 12:07 p. m.
	 */
	protected static class StartTask extends BukkitRunnable {
		
		protected final AutoStarter starter;
		protected       long        timestamp;
		protected       long        last_second;
		
		public StartTask ( AutoStarter starter ) {
			this.starter     = starter;
			this.timestamp   = -1;
			this.last_second = -1;
		}
		
		public Duration getTimeLeft ( ) {
			return Duration.ofMilliseconds ( Math.max (
					starter.countdown_duration.toMillis ( ) - ( System.currentTimeMillis ( ) - timestamp ) , 0L ) );
		}
		
		@Override
		public void run ( ) {
			if ( timestamp == -1 ) {
				timestamp = System.currentTimeMillis ( );
			}
			
			if ( starter.canStart ( ) ) {
				long time      = System.currentTimeMillis ( ) - timestamp;
				long full_time = starter.countdown_duration.toMillis ( );
				
				if ( time < full_time ) {
					long second = TimeUnit.MILLISECONDS.toSeconds ( full_time - time );
					
					if ( last_second != second ) {
					
					}
				} else {
					// set finished
					starter.stop ( );
					starter.finished = true;
					
					// then starting
					starter.arena.start ( );
				}
			} else {
				starter.stop ( );
			}
		}
	}
	
	protected final BattleRoyaleArena arena;
	protected final int               required;
	protected final Duration          countdown_duration;
	protected       boolean           finished;
	
	// start task
	protected StartTask start_task;
	
	public AutoStarter ( BattleRoyaleArena arena ) {
		this.arena = arena;
		
		// configuration
		this.required           = arena.getConfiguration ( ).getAutostartRequired ( );
		this.countdown_duration = arena.getConfiguration ( ).getAutostartCountdown ( );
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	public boolean start ( ) {
		if ( start_task == null || start_task.isCancelled ( ) ) {
			this.start_task = new StartTask ( this );
			this.start_task.runTaskTimerAsynchronously (
					BattleRoyale.getInstance ( ) , 15L , 15L );
			return true;
		} else {
			return false;
		}
	}
	
	public boolean stop ( ) {
		if ( start_task != null && !start_task.isCancelled ( ) ) {
			start_task.cancel ( );
			return true;
		} else {
			return false;
		}
	}
	
	public boolean restart ( ) {
		if ( isStarted ( ) ) {
			stop ( );
			
			this.start_task = null;
			this.finished   = false;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isStarted ( ) {
		return ( start_task != null && !start_task.isCancelled ( ) ) || finished;
	}
	
	public Duration getTimeLeft ( ) {
		return ( start_task != null && !start_task.isCancelled ( ) ) ? start_task.getTimeLeft ( ) : null;
	}
	
	public boolean isFinished ( ) {
		return finished;
	}
	
	public boolean canStart ( ) {
		if ( arena.getState ( ) == EnumArenaState.WAITING && arena.isPrepared ( ) ) {
			BattleRoyaleMode mode = arena.getMode ( );
			
			if ( mode.isSolo ( ) ) {
				return arena.getCount ( false ) >= required;
			} else {
				BattleRoyaleArenaTeamRegistry team_registry = arena.getTeamRegistry ( );
				int count = ( int ) team_registry.stream ( ).filter (
						team -> !team.isEmpty ( ) ).count ( );
				
				if ( count >= required ) {
					// there are enough teams registered
					// to start the arena.
					return true;
				} else {
					// there are no enough teams registered
					// to start the arena; we will check
					// if there are enough players to create
					// new teams and fill them (in case the auto-fill is enabled).
					if ( mode.isAutoFillEnabled ( ) ) {
						int needed = required - count;
						int player_count = ( int ) arena.getPlayers ( false ).stream ( )
								.filter ( player -> !player.hasTeam ( ) ).count ( );
						
						return player_count >= ( mode.getMaxPlayersPerTeam ( ) * needed );
					} else {
						return false;
					}
				}
			}
		} else {
			return false;
		}
	}
}