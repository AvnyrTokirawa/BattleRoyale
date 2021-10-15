package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationEntry;
import es.outlook.adriansrj.battleroyale.util.Constants;
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
	
	CLOSE_WORD ( "common.close-word" , "'Close' word. colors will be ignored" , "Close" ),
	
	NEXT_WORD ( "common.next-word" , "'Next' word. colors will be ignored" , "Next" ),
	
	SAFE_WORD ( "common.safe-word" , "'Safe' word. colors will be ignored" , "Safe" ),
	
	METERS_WORD ( "common.meters-word" , "'Meters' word. colors will be ignored" , "m" ),
	
	TIME_FORMAT_HOURS_MINUTES_SECONDS ( "time.format-hours-minutes-seconds" ,
										"time format that includes hours, minutes and seconds" ,
										"hh'h' mm'm' ss's'" ),
	
	TIME_FORMAT_MINUTES_SECONDS ( "time.format-minutes-seconds" ,
								  "time format that includes minutes and seconds" ,
								  "mm'm' ss's'" ),
	
	TIME_FORMAT_SECONDS ( "time.format-seconds" , "time format that only includes seconds" ,
						  "ss's'" ),
	
	ARENA_MESSAGE_FULL ( "arena.message.full" ,
						 "message players will receive when trying to\njoin an arena that is full" ,
						 ChatColor.DARK_RED + "This arena is full!" ),
	
	ARENA_MESSAGE_RESTARTING ( "arena.message.restarting" ,
							   "message players will receive when trying to\njoin an arena that is being restarted" ,
							   ChatColor.DARK_RED + "This arena is restarting!" ),
	
	ARENA_MESSAGE_RUNNING ( "arena.message.running" ,
							"message players will receive when trying to\njoin an arena that is running/in-game" ,
							ChatColor.DARK_RED + "You cannot join an arena that has already started!" ),
	
	ARENA_MESSAGE_STOPPED ( "arena.message.stopped" ,
							"message players will receive when trying to\njoin an arena that is stopped" ,
							ChatColor.DARK_RED + "This arena is unavailable right now." ),
	ARENA_SIGN_RESTARTING_TEXT ( "arena.sign.restarting-text" ,
								 "arena signs restarting text format. '\\n' supported" ,
								 ChatColor.DARK_RED + "Battle Royale\n%s\n\n" + ChatColor.DARK_RED + "Restarting" ),
	ARENA_SIGN_RUNNING_TEXT ( "arena.sign.running-text" , "arena signs running/in-game text format. '\\n' supported" ,
							  ChatColor.DARK_RED + "Battle Royale\n%s\n\n" + ChatColor.DARK_RED + "In Game" ),
	
	ARENA_SIGN_WAITING_TEXT ( "arena.sign.waiting-text" , "arena signs waiting text format. '\\n' supported" ,
							  ChatColor.DARK_RED + "Battle Royale\n%s\n\n" + ChatColor.DARK_GREEN + "Waiting" ),
	
	ARENA_STATE_RESTARTING_WORD ( "arena.state.restarting-word" ,
								  "arena restarting state word. colors will be ignored" , "Restarting" ),
	
	ARENA_STATE_RUNNING_WORD ( "arena.state.running-word" ,
							   "arena running state word. colors will be ignored" , "Running" ),
	
	ARENA_STATE_STOPPED_WORD ( "arena.state.stopped-word" ,
							   "arena stopped state word. colors will be ignored" , "Stopped" ),
	
	ARENA_STATE_WAITING_WORD ( "arena.state.waiting-word" ,
							   "arena waiting state word. colors will be ignored" , "Waiting" ),
	
	BORDER_STATE_IDLE ( "border.state.idle" ,
						"border idle state text format" ,
						"Border Resizing in: " + ChatColor.GREEN + "%s" ),
	
	BORDER_STATE_SHRINKING ( "border.state.shrinking" ,
							 "border shrinking state text format" ,
							 "Border Shrinking: " + ChatColor.GREEN + "%s" ),
	
	BORDER_STATE_GROWING ( "border.state.growing" ,
						   "border growing state text format" ,
						   "Border Growing: " + ChatColor.GREEN + "%s" ),
	
	BORDER_STATE_STOPPED ( "border.state.stopped" ,
						   "border stopped state text format" ,
						   ChatColor.GREEN + "Border Stopped" ),
	
	BORDER_STATE_RESIZE_IN_ANNOUNCEMENT_TITLE (
			"border.screen-announcement.state.resizing-in.title" ,
			"announcement title to warn players that the border will resize soon" ,
			ChatColor.GOLD + "Border Resizing In" ),
	
	BORDER_STATE_RESIZE_IN_ANNOUNCEMENT_SUBTITLE (
			"border.screen-announcement.state.resizing-in.subtitle" ,
			"announcement subtitle to warn players that the border will resize soon (intended to show the time)" ,
			ChatColor.GOLD + "%s" ),
	
	BORDER_STATE_RESIZING_ANNOUNCEMENT_TITLE (
			"border.screen-announcement.state.start-resizing.title" ,
			"announcement title to warn players that the border is resizing" ,
			ChatColor.GOLD + "Border is Resizing" ),
	
	BORDER_STATE_RESIZING_ANNOUNCEMENT_SUBTITLE (
			"border.screen-announcement.state.start-resizing.subtitle" ,
			"announcement subtitle to warn players that the border is resizing (intended to show the time)" ,
			ChatColor.GOLD + "%s" ),
	
	KNOCKED_BEING_REVIVED ( "knocked.being-revived" , "being revived action bar message" ,
							StringUtil.concatenate ( ChatColor.GOLD , ChatColor.BOLD ) + "You are being revived" ),
	
	KNOCKED_BLEEDING_OUT ( "knocked.bleeding-out" , "bleeding out action bar message" ,
						   StringUtil.concatenate ( ChatColor.DARK_RED , ChatColor.BOLD ) + "You are bleeding out" ),
	
	KNOCKED_REVIVED ( "knocked.revived" , "revived action bar message" ,
					  StringUtil.concatenate ( ChatColor.DARK_GREEN , ChatColor.BOLD ) + "%s Revived" ),
	
	KNOCKED_REVIVING ( "knocked.reviving" , "reviving action bar message" ,
					   StringUtil.concatenate ( ChatColor.GOLD , ChatColor.BOLD ) + "Reviving %s" ),
	
	KNOCKED_REVIVING_PROGRESS ( "knocked.reviving-progress" , "reviving progress title" ,
								StringUtil.concatenate ( ChatColor.GOLD , ChatColor.BOLD ) + "%s" ),
	
	TEAM_MEMBER_HEALTH_FORMAT_FINE ( "team.member.health.fine" ,
									 "team member health text (when health is fine/full)" ,
									 ChatColor.GREEN + Constants.HEART_TEXT + "%d %s" ),
	
	TEAM_MEMBER_HEALTH_FORMAT_NORMAL ( "team.member.health.normal" ,
									   "team member health text (when health is normal)" ,
									   ChatColor.YELLOW + Constants.HEART_TEXT + "%d %s" ),
	
	TEAM_MEMBER_HEALTH_FORMAT_LOW ( "team.member.health.low" ,
									"team member health text (when health is low)" ,
									ChatColor.DARK_RED + Constants.HEART_TEXT + "%d %s" ),
	
	TEAM_MEMBER_HEALTH_FORMAT_DEAD ( "team.member.health.dead" ,
									 "team member health text when dead" ,
									 ChatColor.GRAY.toString ( ) + ChatColor.STRIKETHROUGH + "%s" ),
	
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
		
		if ( this == TEAM_MEMBER_HEALTH_FORMAT_DEAD ) {
			System.out.println ( "ConfigurationEntry.super.getAsString ( ): " + ConfigurationEntry.super.getAsString ( ) );
			System.out.println ( "StringUtil.translateAlternateColorCodes ( ConfigurationEntry.super.getAsString ( ) ): "
										 + StringUtil.translateAlternateColorCodes ( ConfigurationEntry.super.getAsString ( ) ) );
		}
		
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