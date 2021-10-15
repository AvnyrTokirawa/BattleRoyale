package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderResize;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaBorderState;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.event.border.BorderResizeChangeEvent;
import es.outlook.adriansrj.battleroyale.event.border.BorderStateChangeEvent;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.time.TimeUtil;
import es.outlook.adriansrj.core.util.configurable.duration.ConfigurableDuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * Class that keep track of the border of the arena.
 *
 * @author AdrianSR / 24/09/2021 / 09:00 p. m.
 */
public final class BorderListener extends BattleRoyaleArenaListener {
	
	public BorderListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	// this event handler will announce the beginning of
	// a new resize point.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onPointChange ( BorderResizeChangeEvent event ) {
		ConfigurableDuration idle_time = event.getResize ( ).getIdleTime ( );
		
		if ( idle_time != null && idle_time.toSeconds ( ) > 0L ) {
			for ( Player player : event.getBorder ( ).getArena ( ).getPlayers ( ) ) {
				player.sendTitle (
						EnumLanguage.BORDER_STATE_RESIZE_IN_ANNOUNCEMENT_TITLE.getAsString ( ) ,
						String.format ( EnumLanguage.BORDER_STATE_RESIZE_IN_ANNOUNCEMENT_SUBTITLE.getAsString ( ) ,
										TimeUtil.formatTime ( idle_time ) ) ,
						10 , 80 , 10 );
			}
		}
	}
	
	// this event handler will send a warning announcement
	// when the border starts resizing.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onStartResizing ( BorderStateChangeEvent event ) {
		BattlefieldBorderResize point = event.getBorder ( ).getPoint ( );
		
		if ( event.getState ( ) == EnumArenaBorderState.RESIZING && point != null ) {
			ConfigurableDuration resizing_time = point.getTime ( );
			
			if ( resizing_time != null && resizing_time.toSeconds ( ) > 0L ) {
				for ( Player player : event.getBorder ( ).getArena ( ).getPlayers ( ) ) {
					player.sendTitle (
							EnumLanguage.BORDER_STATE_RESIZING_ANNOUNCEMENT_TITLE.getAsString ( ) ,
							String.format ( EnumLanguage.BORDER_STATE_RESIZING_ANNOUNCEMENT_SUBTITLE.getAsString ( ) ,
											TimeUtil.formatTime ( resizing_time ) ) ,
							10 , 80 , 10 );
				}
			}
		}
	}
}