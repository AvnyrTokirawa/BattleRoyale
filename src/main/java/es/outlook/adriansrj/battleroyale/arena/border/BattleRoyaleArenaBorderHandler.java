package es.outlook.adriansrj.battleroyale.arena.border;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderResize;
import es.outlook.adriansrj.battleroyale.bus.BusInstance;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;

/**
 * Class responsible for keeping track of {@link BattleRoyaleArenaBorder}s.
 *
 * @author AdrianSR / 25/09/2021 / 09:19 a. m.
 */
public final class BattleRoyaleArenaBorderHandler extends PluginHandler implements Listener, Runnable {
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BattleRoyaleArenaBorderHandler ( BattleRoyale plugin ) {
		super ( plugin ); register ( );
		
		// this executor will schedule a task that will
		// keep track of the players outside bounds
		Bukkit.getScheduler ( ).runTaskTimerAsynchronously ( plugin , this , 20L , 20L );
	}
	
	@Override
	public void run ( ) {
		for ( BattleRoyaleArena arena : BattleRoyaleArenaHandler.getInstance ( ).getArenas ( ) ) {
			if ( arena.getState ( ) != EnumArenaState.RUNNING || arena.isOver ( ) ) { continue; }
			
			ZoneBounds              bounds         = arena.getBorder ( ).getCurrentBounds ( );
			BattlefieldBorderResize point          = arena.getBorder ( ).getPoint ( );
			BattlefieldBorderResize previous_point = arena.getBorder ( ).getPreviousPoint ( );
			double                  damage         = 0.0D;
			
			if ( point != null ) {
				damage = point.getDamage ( );
			} else if ( previous_point != null ) {
				damage = previous_point.getDamage ( );
			}
			
			if ( damage > 0.0D ) {
				final double final_damage = damage;
				
				for ( Player br_player : arena.getPlayers ( true ) ) {
					BusInstance < ? > bus = br_player.getBus ( );
					
					if ( !br_player.isPlaying ( )
							|| ( bus.isStarted ( ) && !bus.isFinished ( ) && bus.isPassenger ( br_player ) ) ) {
						continue;
					}
					
					br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
						Location location = player.getLocation ( );
						
						if ( !bounds.contains ( location.getX ( ) , location.getZ ( ) ) ) {
							SchedulerUtil.runTask ( ( ) -> player.damage ( final_damage ) );
						}
					} );
				}
			}
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}