package es.outlook.adriansrj.battleroyale.arena.border;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.bus.BusInstance;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class responsible for keeping track of {@link BattleRoyaleArenaBorder}s.
 *
 * @author AdrianSR / 25/09/2021 / 09:19 a. m.
 */
public final class BattleRoyaleArenaBorderHandler extends PluginHandler implements Listener, Runnable {
	
	private final ScheduledExecutorService executor;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BattleRoyaleArenaBorderHandler ( BattleRoyale plugin ) {
		super ( plugin ); register ( );
		
		// this executor will schedule a task that will
		// keep track of the players outside bounds
		this.executor = Executors.newSingleThreadScheduledExecutor ( );
		this.executor.scheduleAtFixedRate (
				this , 1000L , 1000L , TimeUnit.MILLISECONDS );
	}
	
	@Override
	public void run ( ) {
		for ( BattleRoyaleArena arena : BattleRoyaleArenaHandler.getInstance ( ).getArenas ( ) ) {
			if ( arena.getState ( ) != EnumArenaState.RUNNING ) { continue; }
			
			ZoneBounds bounds = arena.getBorder ( ).getCurrentBounds ( );
			double     damage = arena.getBorder ( ).getPoint ( ).getDamage ( );
			
			if ( damage > 0.0D ) {
				for ( Player br_player : arena.getPlayers ( true ) ) {
					BusInstance < ? > bus = br_player.getBus ( );
					
					if ( br_player.isSpectator ( )
							|| ( !bus.isFinished ( ) && bus.isPassenger ( br_player ) ) ) {
						continue;
					}
					
					br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
						Location location = player.getLocation ( );
						
						if ( !bounds.contains ( location.getX ( ) , location.getZ ( ) ) ) {
							SchedulerUtil.runTask ( ( ) -> player.damage ( damage ) );
						}
					} );
				}
			}
		}
	}
	
	@EventHandler
	public void onDisable ( PluginDisableEvent event ) {
		// the executor will not shut down even when the plugin
		// is disabled, unless we tell him to do it.
		if ( Objects.equals ( event.getPlugin ( ) , BattleRoyale.getInstance ( ) ) ) {
			executor.shutdownNow ( );
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}