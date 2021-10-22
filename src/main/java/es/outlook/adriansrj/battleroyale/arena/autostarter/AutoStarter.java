package es.outlook.adriansrj.battleroyale.arena.autostarter;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaTeamRegistry;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.sound.UniversalSound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author AdrianSR / 20/10/2021 / 12:00 p. m.
 */
public class AutoStarter {
	
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
						if ( second > 0L && second <= starter.countdown_display ) {
							starter.arena.getPlayers ( false ).forEach ( player -> {
								// titles
								player.sendTitle (
										String.format ( EnumLanguage.AUTO_STARTER_COUNTDOWN_TITLE.getAsString ( ) , second ) ,
										String.format ( EnumLanguage.AUTO_STARTER_COUNTDOWN_SUBTITLE.getAsString ( ) ,
														second ) );
								
								// sound
								player.playSound ( player.getLocation ( ) , ( second > 1
										? UniversalSound.ORB_PICKUP
										: UniversalSound.LEVEL_UP )
										.asBukkit ( ) , 2.0F , 0.0F );
							} );
						}
						
						last_second = second;
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
				starter.finished = false;
			}
		}
	}
	
	protected final BattleRoyaleArena arena;
	protected final int               required_players;
	protected final int               required_teams;
	protected final int               countdown_display;
	protected final Duration          countdown_duration;
	protected       boolean           finished;
	
	// start task
	protected StartTask start_task;
	
	public AutoStarter ( BattleRoyaleArena arena ) {
		this.arena = arena;
		
		// configuration
		this.required_players   = arena.getConfiguration ( ).getAutostartRequiredPlayers ( );
		this.required_teams     = arena.getConfiguration ( ).getAutostartRequiredTeams ( );
		this.countdown_display  = arena.getConfiguration ( ).getAutostartCountdownDisplay ( );
		this.countdown_duration = arena.getConfiguration ( ).getAutostartCountdownDuration ( );
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	public boolean start ( ) {
		if ( start_task == null || start_task.isCancelled ( ) ) {
			this.start_task = new StartTask ( this );
			this.start_task.runTaskTimerAsynchronously (
					BattleRoyale.getInstance ( ) , 10L , 10L );
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
				// teams are the same as players in a solo-mode.
				return arena.getCount ( false ) >= required_players;
			} else {
				// checking required teams
				BattleRoyaleArenaTeamRegistry team_registry = arena.getTeamRegistry ( );
				int team_count = ( int ) team_registry.stream ( ).filter (
						team -> !team.isEmpty ( ) ).count ( );
				
				if ( team_count < required_teams ) {
					// there are no enough teams registered
					// to start the arena; we will check
					// if there are enough players to create
					// new teams and fill them (in case the auto-fill is enabled).
					if ( mode.isAutoFillEnabled ( ) ) {
						int needed = required_teams - team_count;
						int player_count = ( int ) arena.getPlayers ( false ).stream ( )
								.filter ( player -> !player.hasTeam ( ) ).count ( );
						
						if ( player_count < ( mode.getMaxPlayersPerTeam ( ) * needed ) ) {
							return false;
						}
					} else {
						return false;
					}
				}
				
				// checking required players
				Stream < Player > player_stream = arena.getPlayers ( false ).stream ( );
				
				if ( !mode.isAutoFillEnabled ( ) ) {
					// only players on a team will be considered if auto-fill is disabled,
					// as they will not be part of the game unless they join a team.
					player_stream = player_stream.filter ( Player :: hasTeam );
				}
				
				return player_stream.count ( ) >= required_players;
			}
		}
		
		return false;
	}
}