package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.event.border.BorderShrinkChangeEvent;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.configurable.duration.ConfigurableDuration;
import org.apache.commons.lang3.time.DurationFormatUtils;
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
	// a new shrink point.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onShrinkChange ( BorderShrinkChangeEvent event ) {
		ConfigurableDuration idle_time = event.getShrink ( ).getIdleTime ( );
		
		if ( idle_time != null && idle_time.toSeconds ( ) > 0L ) {
			for ( Player player : event.getBorder ( ).getArena ( ).getPlayers ( ) ) {
				String format;
				
				if ( idle_time.toHours ( ) > 0 ) { // hours, minutes and seconds
					format = EnumLanguage.TIME_FORMAT_HOURS_MINUTES_SECONDS.getAsString ( );
				} else if ( idle_time.toMinutes ( ) > 0 ) { // minutes and seconds
					format = EnumLanguage.TIME_FORMAT_MINUTES_SECONDS.getAsString ( );
				} else { // seconds
					format = EnumLanguage.TIME_FORMAT_SECONDS.getAsString ( );
				}
				
				player.sendTitle ( EnumLanguage.BORDER_TIME_SHRINK_ANNOUNCEMENT_TITLE.getAsString ( ) ,
								   String.format (
										   EnumLanguage.BORDER_TIME_SHRINK_ANNOUNCEMENT_SUBTITLE.getAsString ( ) ,
										   formatTime ( format , idle_time ) ) ,
								   10 , 80 , 10 );
			}
		}
	}
	
	private String formatTime ( String format , Duration duration ) {
		return DurationFormatUtils.formatDuration ( duration.toMillis ( ) , format , false );
	}
}