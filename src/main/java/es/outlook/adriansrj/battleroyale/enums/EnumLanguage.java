package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationEntry;
import es.outlook.adriansrj.battleroyale.placeholder.PlaceholderHandler;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import es.outlook.adriansrj.core.util.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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
	
	BUS_JUMP_TITLE ( "bus.jump.title" , "bus jump title" , "" ),
	
	BUS_JUMP_SUBTITLE ( "bus.jump.subtitle" , "bus jump subtitle" ,
						ChatColor.BLUE + "PRESS " + ChatColor.AQUA + "SNEAK" + ChatColor.BLUE + " TO JUMP" ),
	
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
	
	RESPAWN_COUNTDOWN_TITLE ( "respawn.countdown.title" ,
							  "respawn countdown title" ,
							  StringUtil.EMPTY ),
	
	RESPAWN_COUNTDOWN_SUBTITLE ( "respawn.countdown.subtitle" ,
								 "respawn countdown subtitle" ,
								 ChatColor.GOLD + "Respawning in %s" ),
	
	RANK_WINNER_TITLE ( "rank.winner.title" ,
						"winner rank title" ,
						ChatColor.GREEN + "#1 WINNER" ),
	
	RANK_WINNER_SUBTITLE ( "rank.winner.subtitle" ,
						   "winner rank subtitle" ,
						   ChatColor.GOLD + "Battle Royale" ),
	
	RANK_GAME_OVER_TITLE ( "rank.game-over.title" ,
						   "game-over rank title" ,
						   ChatColor.DARK_RED + "#%d GAME OVER" ),
	
	RANK_GAME_OVER_SUBTITLE ( "rank.game-over.subtitle" ,
							  "game-over rank subtitle" ,
							  ChatColor.GOLD + "Battle Royale" ),
	
	// -------- killed phrases
	
	KILLED_PHRASE_GENERAL ( "killed-phrase.general" ,
							"general killed phrase" ,
							ChatColor.WHITE + "%s" + ChatColor.GOLD + " killed " + ChatColor.WHITE + "%s." ),
	
	KILLED_PHRASE_SHOT ( "killed-phrase.shot" ,
						 "phrase when a player is shot" ,
						 ChatColor.WHITE + "%s" + ChatColor.GOLD + " shot " + ChatColor.WHITE + "%s" ),
	
	KILLED_PHRASE_PUSH ( "killed-phrase.push" ,
						 "phrase when a player dies from a fall after being pushed" ,
						 ChatColor.WHITE + "%s" + ChatColor.GOLD + " pushed " + ChatColor.WHITE + "%s" ),
	
	KILLED_PHRASE_VOID ( "killed-phrase.void" ,
						 "phrase when a player is thrown into the void" ,
						 ChatColor.WHITE + "%s" + ChatColor.GOLD + " threw "
								 + ChatColor.WHITE + "%s " + ChatColor.GOLD + " into the void." ),
	
	KILLED_PHRASE_BLEEDING_OUT ( "killed-phrase.bleeding-out" ,
								 "phrase when a player dies from bleeding out, after being knocked by a player" ,
								 ChatColor.WHITE + "%s" + ChatColor.GOLD + " killed "
										 + ChatColor.WHITE + "%s " + ChatColor.GOLD + " (bleeding out)." ),
	
	// -------- death phrases
	
	DEATH_PHRASE_GENERAL ( "death-phrase.general" ,
						   "general death phrase" ,
						   ChatColor.GOLD + "%s " + ChatColor.WHITE + "died." ),
	
	DEATH_PHRASE_FALL ( "death-phrase.fall" ,
						"phrase when a player dies from falling from a high place" ,
						ChatColor.GOLD + "%s " + ChatColor.WHITE + "fell from a high place." ),
	
	DEATH_PHRASE_VOID ( "death-phrase.void" ,
						"phrase when a player dies from falling into the void" ,
						ChatColor.GOLD + "%s " + ChatColor.WHITE + "fell into the void." ),
	
	DEATH_PHRASE_EXPLOSION ( "death-phrase.explosion" ,
							 "phrase when a player dies in an explosion" ,
							 ChatColor.GOLD + "%s " + ChatColor.WHITE + "died in an explosion." ),
	
	DEATH_PHRASE_BLEEDING_OUT ( "death-phrase.bleeding-out" ,
								"phrase when a player dies from bleeding out" ,
								ChatColor.GOLD + "%s " + ChatColor.WHITE + "died bleeding." ),
	
	DEATH_PHRASE_OUT_OF_BOUNDS ( "death-phrase.out-of-bounds" ,
								 "phrase when a player dies from being out of bounds" ,
								 ChatColor.GOLD + "%s " + ChatColor.WHITE + "died in the storm." ),
	
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
	
	// -------- auto starter
	
	AUTO_STARTER_STATE_WAITING_BATTLEFIELD ( "auto-start.state.waiting.loading-battlefield" ,
											 "auto-starter waiting state text format (when the battlefield is being prepared)" ,
											 ChatColor.GOLD + "Preparing Battlefield..." ),
	
	AUTO_STARTER_STATE_WAITING_PLAYER ( "auto-start.state.waiting.required-players" ,
										"auto-starter waiting state text format (one-player teams)" ,
										"Players: " + ChatColor.GOLD + "%s" ),
	
	AUTO_STARTER_STATE_WAITING_TEAM ( "auto-start.state.waiting.required-teams" ,
									  "auto-starter waiting state text format (teams with more than one player)" ,
									  "Teams: " + ChatColor.GOLD + "%s" ),
	
	AUTO_STARTER_STATE_STARTING ( "auto-start.state.starting" ,
								  "auto-starter starting state text format" ,
								  "Starting in: " + ChatColor.GREEN + "%s" ),
	
	AUTO_STARTER_COUNTDOWN_TITLE ( "auto-start.countdown.title" ,
								   "auto-starter countdown title" ,
								   ChatColor.GOLD + "%d" ),
	
	AUTO_STARTER_COUNTDOWN_SUBTITLE ( "auto-start.countdown.subtitle" ,
									  "auto-starter countdown subtitle" ,
									  "" ),
	
	// -------- restarter
	
	RESTARTER_COUNTDOWN_ACTIONBAR ( "restart.countdown.actionbar" ,
									"restarter countdown actionbar" ,
									ChatColor.DARK_RED + "Restarting in " + ChatColor.BOLD + "%s" ),
	
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
		return getAsString ( null );
	}
	
	public String getAsString ( Player player ) {
		return PlaceholderHandler.getInstance ( ).setPlaceholders ( player , StringEscapeUtils.unescapeJava (
				StringUtil.translateAlternateColorCodes ( ConfigurationEntry.super.getAsString ( ) ) ) );
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