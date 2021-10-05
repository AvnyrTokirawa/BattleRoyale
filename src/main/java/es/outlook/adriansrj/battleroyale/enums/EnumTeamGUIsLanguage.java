package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationEntry;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import es.outlook.adriansrj.core.util.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.List;

/**
 * @author AdrianSR / 04/09/2021 / 03:21 p. m.
 */
public enum EnumTeamGUIsLanguage implements ConfigurationEntry {
	
	SELECTOR_TITLE ( "title.selector" , "selector gui title" ,
					 ChatColor.BLACK + "Team Selector" ),
	
	MY_TEAM_TITLE ( "title.my-team" , "my team gui title" ,
					ChatColor.BLACK + "My Team" ),
	
	BUTTON_MY_TEAM ( "button.my-team.display-text" , "'My Team' button display text" ,
					 ChatColor.GOLD + "My Team" ),
	
	BUTTON_CREATE_TEAM ( "button.create-team.display-text" , "'Create Team' button display text" ,
						 StringUtil.concatenate ( ChatColor.GOLD , ChatColor.BOLD ) + "Create Team" ),
	
	BUTTON_CREATE_TEAM_DESCRIPTION ( "button.create-team.description" ,
									 "'Create Team' button display description, use '\n' to create lines" ,
									 "" ,
									 ChatColor.GRAY + "Click to create" ,
									 ChatColor.GRAY + "a new team." ),
	
	BUTTON_JOIN_TEAM ( "button.join-team.display-text" , "join-team button display text" ,
					   ChatColor.GOLD + "Team %d" ),
	
	BUTTON_JOIN_TEAM_DESCRIPTION ( "button.join-team.description" , "join-team button display description" ,
								   "" ,
								   ChatColor.GRAY + "Click to join" ,
								   "" ),
	
	BUTTON_JOIN_TEAM_FULL_DESCRIPTION ( "button.join-team-full.description" ,
										"join-team (that is full) button display description" ,
										"" ,
										ChatColor.RED + "This Team is Full" ,
										"" ),
	
	BUTTON_JOIN_TEAM_MEMBERS ( "button.join-team.members" ,
							   "team member list format, use '\n' to create lines" ,
							   "" ,
							   ChatColor.GRAY + "Members" ,
							   ChatColor.GRAY + "%s" ),
	
	BUTTON_JOIN_TEAM_MEMBERS_ENTRY ( "button.join-team.team.members.entry" ,
									 "team member list entry format" , ChatColor.BLUE + "* %s" ),
	
	BUTTON_JOIN_TEAM_NO_MEMBERS ( "button.join-team.team.no-members" ,
								  "description to show when team is empty" ,
								  ChatColor.YELLOW + "Empty team" ),
	
	BUTTON_SEE_ALL_TEAMS ( "button.see-all-teams.display-text" ,
						   "see all teams button display text" ,
						   ChatColor.BLUE + "See all teams" ),
	
	BUTTON_LEAVE_TEAM ( "button.leave-team.display-text" ,
						"leave team button display text" ,
						ChatColor.RED + "Leave Team" ),
	
	MESSAGE_TEAM_CREATED ( "message.team-created" , "team created message" ,
						   ChatColor.GREEN + "Team created successfully!" ),
	
	MESSAGE_CANNOT_CREATE_TEAM ( "message.cannot-create-team" , "cannot create team message" ,
								 ChatColor.DARK_RED + "Cannot create a new team at this moment!" ),
	
	MESSAGE_TEAM_FULL ( "message.team-full" , "team full message" ,
						ChatColor.DARK_RED + "This team is full!" ),
	
	MESSAGE_CANNOT_JOIN_TEAM ( "message.cannot-join-team" , "cannot-join-team" ,
							   ChatColor.DARK_RED + "Cannot join this team in this moment" ),
	
	;
	
	private final String key;
	private final String comment;
	private final Object default_value;
	private       Object value;
	
	EnumTeamGUIsLanguage ( String key , String comment , String default_value ) {
		this.key           = key;
		this.comment       = comment;
		this.default_value = StringEscapeUtils.escapeJava (
				StringUtil.untranslateAlternateColorCodes ( default_value ) );
		this.value         = this.default_value;
	}
	
	EnumTeamGUIsLanguage ( String key , String comment , String... default_value ) {
		this ( key , comment , StringUtil.join ( default_value , "\n" ) );
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
		return String.class;
	}
	
	@Override
	public String getAsString ( ) {
		// TODO: full placeholders plugins support
		return StringEscapeUtils.unescapeJava (
				StringUtil.translateAlternateColorCodes ( ConfigurationEntry.super.getAsString ( ) ) );
	}
	
	public List < String > getAsStringList ( ) {
		return Arrays.asList ( getAsString ( ).split ( "\n" ) );
	}
	
	@Override
	public void load ( ConfigurationSection section ) {
		Object raw = section.get ( getKey ( ) );
		
		if ( raw != null && ClassReflection.compatibleTypes ( String.class , raw ) ) {
			this.value = raw;
		}
	}
}