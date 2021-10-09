package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationEntry;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import es.outlook.adriansrj.core.util.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author AdrianSR / 04/09/2021 / 03:21 p. m.
 */
public enum EnumLanguage implements ConfigurationEntry {
	
	YOU_WORD ( "common.you-word" , "'You' word. colors will be ignored" , "You" ),
	
	BACK_WORD ( "common.back-word" , "'Back' word. colors will be ignored" , "Back" ),
	
	NEXT_WORD ( "common.next-word" , "'Next' word. colors will be ignored" , "Next" ),
	
	CLOSE_WORD ( "common.close-word" , "'Close' word. colors will be ignored" , "Close" ),
	
	TIME_FORMAT_SECONDS ( "time.format-seconds" , "time format that only includes seconds" ,
						  "ss's'" ),
	TIME_FORMAT_MINUTES_SECONDS ( "time.format-minutes-seconds" ,
								  "time format that includes minutes and seconds" ,
								  "mm'm' ss's'" ),
	TIME_FORMAT_HOURS_MINUTES_SECONDS ( "time.format-hours-minutes-seconds" ,
										"time format that includes hours, minutes and seconds" ,
										"hh'h' mm'm' ss's'" ),
	
	ARENA_STATE_WAITING_WORD ( "arena.state.waiting-word" ,
							   "arena waiting state word. colors will be ignored" , "Waiting" ),
	
	ARENA_STATE_RUNNING_WORD ( "arena.state.running-word" ,
							   "arena running state word. colors will be ignored" , "Running" ),
	
	ARENA_STATE_RESTARTING_WORD ( "arena.state.restarting-word" ,
								  "arena restarting state word. colors will be ignored" , "Restarting" ),
	
	ARENA_STATE_STOPPED_WORD ( "arena.state.stopped-word" ,
							   "arena stopped state word. colors will be ignored" , "Stopped" ),
	
	ARENA_BORDER_STATE_IDLE ( "arena.border.state.idle" ,
							  "border idle state word. colors will be ignored" , "Idle" ),
	
	ARENA_SIGN_WAITING_TEXT ( "arena.sign.waiting-text" , "arena signs waiting text format. '\\n' supported" ,
							  ChatColor.DARK_RED + "Battle Royale\n%s\n\n" + ChatColor.DARK_GREEN + "Waiting" ),
	
	ARENA_SIGN_RUNNING_TEXT ( "arena.sign.running-text" , "arena signs running/in-game text format. '\\n' supported" ,
							  ChatColor.DARK_RED + "Battle Royale\n%s\n\n" + ChatColor.DARK_RED + "In Game" ),
	
	ARENA_SIGN_RESTARTING_TEXT ( "arena.sign.restarting-text" ,
								 "arena signs restarting text format. '\\n' supported" ,
								 ChatColor.DARK_RED + "Battle Royale\n%s\n\n" + ChatColor.DARK_RED + "Restarting" ),
	
	ARENA_MESSAGE_RUNNING ( "arena.message.running" ,
							"message players will receive when trying to\njoin an arena that is running/in-game" ,
							ChatColor.DARK_RED + "You cannot join an arena that has already started!" ),
	
	ARENA_MESSAGE_FULL ( "arena.message.full" ,
						 "message players will receive when trying to\njoin an arena that is full" ,
						 ChatColor.DARK_RED + "This arena is full!" ),
	
	ARENA_MESSAGE_RESTARTING ( "arena.message.restarting" ,
							   "message players will receive when trying to\njoin an arena that is being restarted" ,
							   ChatColor.DARK_RED + "This arena is restarting!" ),
	
	ARENA_MESSAGE_STOPPED ( "arena.message.stopped" ,
							"message players will receive when trying to\njoin an arena that is stopped" ,
							ChatColor.DARK_RED + "This arena is unavailable right now." ),
	
	KNOCKED_BLEEDING_OUT ( "knocked.bleeding-out" , "bleeding out action bar message" ,
						   StringUtil.concatenate ( ChatColor.DARK_RED , ChatColor.BOLD ) + "You are bleeding out" ),
	
	KNOCKED_BEING_REVIVED ( "knocked.being-revived" , "being revived action bar message" ,
							StringUtil.concatenate ( ChatColor.GOLD , ChatColor.BOLD ) + "You are being revived" ),
	
	KNOCKED_REVIVING ( "knocked.reviving" , "reviving action bar message" ,
					   StringUtil.concatenate ( ChatColor.GOLD , ChatColor.BOLD ) + "Reviving %s" ),
	
	KNOCKED_REVIVED ( "knocked.revived" , "revived action bar message" ,
					  StringUtil.concatenate ( ChatColor.DARK_GREEN , ChatColor.BOLD ) + "%s Revived" ),
	
	KNOCKED_REVIVING_PROGRESS ( "knocked.reviving-progress" , "reviving progress title" ,
								StringUtil.concatenate ( ChatColor.GOLD , ChatColor.BOLD ) + "%s" ),
	
	BORDER_TIME_SHRINK_ANNOUNCEMENT_TITLE (
			"border.shrink.title" ,
			"announcement title to warn players that the border will shrink soon" ,
			ChatColor.GOLD + "Border Shrinking" ),
	
	BORDER_TIME_SHRINK_ANNOUNCEMENT_SUBTITLE (
			"border.shrink.subtitle" ,
			"announcement subtitle to warn players that the border will shrink soon (intended to show the time)" ,
			ChatColor.GOLD + "%s" ),
	
	;
	
	private final String      key;
	private final String      comment;
	private final Object      default_value;
	private final Class < ? > type;
	private       Object      value;
	
	EnumLanguage ( String key , String comment , String default_value , Class < ? > type ) {
		this.key           = key;
		this.comment       = comment;
		this.default_value = StringEscapeUtils.escapeJava (
				StringUtil.untranslateAlternateColorCodes ( default_value ) );
		this.value         = this.default_value;
		this.type          = type;
	}
	
	EnumLanguage ( String key , String comment , String default_value ) {
		this ( key , comment , default_value , default_value.getClass ( ) );
	}
	
	@Override
	public String getKey ( ) {
		return key;
	}
	
	@Override
	public String getComment ( ) {
		return comment;
	}
	
	@Override
	public Object getDefaultValue ( ) {
		return default_value;
	}
	
	@Override
	public Object getValue ( ) {
		return value;
	}
	
	@Override
	public Class < ? > getValueType ( ) {
		return type;
	}
	
	@Override
	public String getAsString ( ) {
		// TODO: full placeholders plugins support
		return StringEscapeUtils.unescapeJava (
				StringUtil.translateAlternateColorCodes ( ConfigurationEntry.super.getAsString ( ) ) );
	}
	
	public String getAsStringStripColors ( ) {
		return StringUtil.stripColors ( getAsString ( ) );
	}
	
	@Override
	public void load ( ConfigurationSection section ) {
		Object raw = section.get ( getKey ( ) );
		
		if ( raw != null && ClassReflection.compatibleTypes ( this.type , raw ) ) {
			this.value = raw;
		}
	}
}