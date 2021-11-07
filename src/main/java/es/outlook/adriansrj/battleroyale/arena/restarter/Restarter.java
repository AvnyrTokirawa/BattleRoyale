package es.outlook.adriansrj.battleroyale.arena.restarter;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.battleroyale.util.task.BukkitRunnableWrapper;
import es.outlook.adriansrj.battleroyale.util.time.TimeUtil;
import es.outlook.adriansrj.core.enums.EnumMessageType;
import es.outlook.adriansrj.core.util.Duration;

/**
 * Class responsible for restarting a certain arena
 * in a provided period of time.
 *
 * @author AdrianSR / 21/10/2021 / 09:13 p. m.
 */
public class Restarter {
	
	/**
	 * @author AdrianSR / 21/10/2021 / 09:38 p. m.
	 */
	protected static class RestartTask extends BukkitRunnableWrapper {
		
		protected final Restarter restarter;
		protected final Duration  countdown_duration;
		protected       long      timestamp;
		
		public RestartTask ( Restarter restarter , Duration countdown_duration ) {
			this.restarter          = restarter;
			this.countdown_duration = countdown_duration;
			this.timestamp          = -1;
		}
		
		public Duration getTimeLeft ( ) {
			return Duration.ofMilliseconds ( Math.max (
					countdown_duration.toMillis ( ) - ( System.currentTimeMillis ( ) - timestamp ) , 0L ) );
		}
		
		public Duration getCountdownDuration ( ) {
			return countdown_duration;
		}
		
		@Override
		public void run ( ) {
			if ( timestamp == -1 ) {
				timestamp = System.currentTimeMillis ( );
			}
			
			long time      = System.currentTimeMillis ( ) - timestamp;
			long full_time = countdown_duration.toMillis ( );
			
			if ( time < full_time ) {
				// action bar
				restarter.arena.getPlayers ( false ).forEach ( player -> player.sendMessage (
						EnumMessageType.ACTION_BAR ,
						String.format ( EnumLanguage.RESTARTER_COUNTDOWN_ACTIONBAR.getAsString ( ) ,
										TimeUtil.formatTime ( Duration.ofMilliseconds (
												// human count starts from 1 ;)
												getTimeLeft ( ).toMillis ( ) + 1000L ) ) ) ) );
			} else {
				// cleaning action bar
				restarter.arena.getPlayers ( false ).forEach (
						player -> player.sendMessage ( EnumMessageType.ACTION_BAR , StringUtil.EMPTY ) );
				
				// set finished
				restarter.stop ( );
				restarter.finished = true;
				
				// then restarting
				restarter.arena.restart ( );
			}
		}
	}
	
	protected final BattleRoyaleArena arena;
	protected       boolean           finished;
	
	// current restart task
	protected RestartTask restart_task;
	
	public Restarter ( BattleRoyaleArena arena ) {
		this.arena = arena;
	}
	
	public boolean start ( Duration countdown_duration ) {
		if ( restart_task == null || restart_task.isCancelled ( ) ) {
			this.restart_task = new RestartTask ( this , countdown_duration != null ? countdown_duration
					: arena.getConfiguration ( ).getRestartCountdownDuration ( ) );
			this.restart_task.runTaskTimerAsynchronously (
					BattleRoyale.getInstance ( ) , 10L , 10L );
			return true;
		} else {
			return false;
		}
	}
	
	public boolean start ( ) {
		return start ( null );
	}
	
	public boolean stop ( ) {
		if ( restart_task != null && !restart_task.isCancelled ( ) ) {
			restart_task.cancel ( );
			return true;
		} else {
			return false;
		}
	}
	
	public boolean restart ( ) {
		if ( isStarted ( ) ) {
			stop ( );
			
			this.restart_task = null;
			this.finished     = false;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isStarted ( ) {
		return ( restart_task != null && !restart_task.isCancelled ( ) ) || finished;
	}
	
	public Duration getTimeLeft ( ) {
		return ( restart_task != null && !restart_task.isCancelled ( ) ) ? restart_task.getTimeLeft ( ) : null;
	}
	
	public boolean isFinished ( ) {
		return finished;
	}
}