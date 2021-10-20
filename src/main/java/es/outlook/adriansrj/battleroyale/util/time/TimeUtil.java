package es.outlook.adriansrj.battleroyale.util.time;

import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.core.util.Duration;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.concurrent.TimeUnit;

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
	
	/**
	 * Matches a {@link TimeUnit} from its name or an alias.
	 *
	 * @param input the name.
	 * @return the resulting time unit.
	 */
	public static TimeUnit matchTimeUnit ( String input ) {
		switch ( input.toLowerCase ( ) ) {
			case "s":
			case "sec":
			case "secs":
			case "second":
			case "seconds":
				return TimeUnit.SECONDS;
			
			case "m":
			case "min":
			case "mins":
			case "minute":
			case "minutes":
				return TimeUnit.MINUTES;
			
			case "h":
			case "hr":
			case "hrs":
			case "hour":
			case "hours":
				return TimeUnit.HOURS;
			
			case "d":
			case "day":
			case "days":
				return TimeUnit.DAYS;
			
			default:
				return null;
		}
	}
}
