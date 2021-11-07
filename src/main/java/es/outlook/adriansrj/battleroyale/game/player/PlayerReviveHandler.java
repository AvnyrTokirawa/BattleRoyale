package es.outlook.adriansrj.battleroyale.game.player;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.task.BukkitRunnableWrapper;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.actionbar.ActionBarUtil;
import es.outlook.adriansrj.core.util.math.target.TargetUtil;
import es.outlook.adriansrj.core.util.titles.TitlesUtil;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class responsible for handling players being revived.
 *
 * @author AdrianSR / 16/09/2021 / 12:15 p. m.
 */
public final class PlayerReviveHandler extends PluginHandler {
	
	static PlayerReviveHandler getInstance ( ) {
		return getPluginHandler ( PlayerReviveHandler.class );
	}
	
	/**
	 * @author AdrianSR / 16/09/2021 / 01:31 p. m.
	 */
	private static class ReviveTask extends BukkitRunnableWrapper {
		
		protected final BattleRoyaleArena arena;
		protected final Player            br_reviver;
		protected final Player            br_target;
		protected final long              time;
		protected       long              timestamp = -1L;
		
		public ReviveTask ( Player br_reviver , Player target ) {
			this.arena      = br_reviver.getArena ( );
			this.br_reviver = br_reviver;
			this.br_target  = target;
			this.time       = arena.getMode ( ).getRevivingTime ( ).toMillis ( );
		}
		
		@Override
		public void run ( ) {
			org.bukkit.entity.Player reviver = br_reviver.getBukkitPlayer ( );
			org.bukkit.entity.Player target  = br_target.getBukkitPlayer ( );
			long                     now     = System.currentTimeMillis ( );
			
			if ( reviver == null || target == null
					|| br_reviver.isKnocked ( ) || br_reviver.isSpectator ( ) ) {
				cancel ( ); return;
			}
			
			if ( timestamp == -1 ) {
				timestamp = now;
			}
			
			long time_since = now - timestamp;
			
			if ( time_since < time ) {
				// reanimation in progress.
				// the progress is shown in the format: %.1f
				double progress        = ( time - time_since ) / 1000.0D;
				String progress_string = String.format ( "%.1f" , progress ).replace ( ',' , '.' );
				String progress_lang = String.format ( EnumLanguage.KNOCKED_REVIVING_PROGRESS.getAsString ( ) ,
													   progress_string );
				
				TitlesUtil.send ( reviver , StringUtil.EMPTY , progress_lang , 0 , 3 , 0 );
				TitlesUtil.send ( target , StringUtil.EMPTY , progress_lang , 0 , 3 , 0 );
				
				ActionBarUtil.send ( reviver , String.format ( EnumLanguage.KNOCKED_REVIVING.getAsString ( ) ,
															   target.getName ( ) ) );
				ActionBarUtil.send ( target , EnumLanguage.KNOCKED_BEING_REVIVED.getAsString ( ) );
			} else {
				// reanimation finished successfully
				cancel ( );
				
				ActionBarUtil.send ( reviver , String.format ( EnumLanguage.KNOCKED_REVIVED.getAsString ( ) ,
															   target.getName ( ) ) );
				
				// this will clear the bar of the target player
				ActionBarUtil.send ( target , StringUtil.EMPTY );
				// this will clear the titles
				TitlesUtil.send ( reviver , StringUtil.EMPTY , StringUtil.EMPTY );
				TitlesUtil.send ( target , StringUtil.EMPTY , StringUtil.EMPTY );
				
				// un-knocking
				br_target.setKnocked ( false );
				// health after reviving
				target.setHealth ( Math.max ( arena.getMode ( ).getHealthAfterReviving ( ) , 1.0D ) );
			}
		}
	}
	
	private final Map < Player, ReviveTask > reviving_map         = new ConcurrentHashMap <> ( );
	private final Map < Player, ReviveTask > reviving_reviver_map = new ConcurrentHashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public PlayerReviveHandler ( BattleRoyale plugin ) {
		super ( plugin ); register ( );
	}
	
	/**
	 * @return whether the provided player is being revived.
	 */
	boolean reviving ( Player player ) {
		ReviveTask task = reviving_map.get ( player );
		
		return task != null && !task.isCancelled ( );
	}
	
	// handler that schedules the reanimation task of a player
	// that is knocked when a teammate right-clicks the knocked player.
	@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onStart ( PlayerInteractAtEntityEvent event ) {
		Entity                   uncast     = event.getRightClicked ( );
		org.bukkit.entity.Player player     = event.getPlayer ( );
		Player                   br_reviver = Player.getPlayer ( player );
		BattleRoyaleArena        arena      = br_reviver.getArena ( );
		
		if ( arena != null && arena.getMode ( ).isRevivingEnabled ( )
				&& uncast instanceof org.bukkit.entity.Player
				&& !br_reviver.isKnocked ( ) && !br_reviver.isSpectator ( ) ) {
			org.bukkit.entity.Player knocked    = ( org.bukkit.entity.Player ) uncast;
			Player                   br_knocked = Player.getPlayer ( knocked );
			
			if ( /*Objects.equals ( br_reviver.getTeam ( ) , br_knocked.getTeam ( ) )
					&&*/ br_knocked.isKnocked ( ) && !reviving ( br_knocked ) ) {
				ReviveTask task = new ReviveTask ( br_reviver , br_knocked );
				task.runTaskTimer ( BattleRoyale.getInstance ( ) , 0L , 0L );
				
				reviving_map.put ( br_knocked , task );
				reviving_reviver_map.put ( br_reviver , task );
			}
		}
	}
	
	// handler that cancels the players from being revived when
	// the reviver stops looking at them.
	@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onReanimation ( PlayerMoveEvent event ) {
		org.bukkit.entity.Player player     = event.getPlayer ( );
		Player                   br_reviver = Player.getPlayer ( player );
		ReviveTask               task       = reviving_reviver_map.get ( br_reviver );
		
		if ( task != null && task.timestamp != -1L && !task.isCancelled ( ) ) {
			boolean cancel = br_reviver.isKnocked ( );
			
			// checking target is still in front
			if ( !Objects.equals ( getTarget ( player ) , task.br_target ) ) {
				cancel = true;
			}
			
			// then cancelling
			if ( cancel ) {
				try {
					task.cancel ( );
				} catch ( IllegalStateException ex ) {
					// ignored exception
				}
			}
		}
	}
	
	private Player getTarget ( org.bukkit.entity.Player player ) {
		org.bukkit.entity.Player target = TargetUtil.getTarget (
				player , 3.0D , org.bukkit.entity.Player.class );
		
		return target != null ? Player.getPlayer ( target ) : null;
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
