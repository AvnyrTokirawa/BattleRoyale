package es.outlook.adriansrj.battleroyale.util.time;

import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.core.util.Duration;
import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * Useful class for dealing with time.
 *
 * @author AdrianSR / 12/10/2021 / 02:30 p. m.
 */
public class TimeUtil {
	
	public static String formatTime ( String format , Duration duration ) {
		return DurationFormatUtils.formatDuration ( duration.toMillis ( ) , format , false );
	}
	
	public static String formatTime ( Duration duration ) {
		String format;
		
		if ( duration.toHours ( ) > 0 ) { // hours, minutes and seconds
			format = EnumLanguage.TIME_FORMAT_HOURS_MINUTES_SECONDS.getAsString ( );
		} else if ( duration.toMinutes ( ) > 0 ) { // minutes and seconds
			format = EnumLanguage.TIME_FORMAT_MINUTES_SECONDS.getAsString ( );
		} else { // seconds
			format = EnumLanguage.TIME_FORMAT_SECONDS.getAsString ( );
		}
		
		return formatTime ( format , duration );
	}
}
