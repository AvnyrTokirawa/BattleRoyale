package es.outlook.adriansrj.battleroyale.placeholder.node.team;

import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.Team;
import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNode;
import es.outlook.adriansrj.core.util.entity.EntityUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <b>'br_team'</b> placeholder node.
 *
 * @author AdrianSR / 09/10/2021 / 07:44 a. m.
 */
public class TeamPlaceholderNode extends PlaceholderNode {
	
	public static final String IDENTIFIER = "team";
	
	@Override
	public String getSubIdentifier ( ) {
		return IDENTIFIER;
	}
	
	@Override
	protected String onRequest ( org.bukkit.entity.Player player , String params ) {
		Player br_player = Player.getPlayer ( player );
		Team   team      = br_player.getTeam ( );
		
		if ( team != null ) {
			if ( params.toLowerCase ( ).startsWith ( "id" ) ) { // br_team_id
				return String.valueOf ( team.getArena ( ).getTeamRegistry ( ).getNumericId ( team ) );
			} else if ( params.toLowerCase ( ).startsWith ( "count" ) ) { // br_team_count
				return String.valueOf ( team.getCount ( ) );
			} else if ( params.toLowerCase ( ).startsWith ( "members" ) ) {
				// br_team_members           | br_team_members_{limit}
				// br_team_members_formatted | br_team_members_formatted_{limit}
				return members ( player , team , params );
			} else if ( params.toLowerCase ( ).startsWith ( "member" ) ) { // br_team_member_{index}
				try {
					int    index  = Integer.parseInt ( extractIdentifier ( params ) );
					Player member = null;
					
					for ( Player other : team.getPlayers ( ) ) {
						if ( team.getNumericId ( other ) == index ) {
							member = other;
							break;
						}
					}
					
					if ( member != null ) {
						return member.getName ( );
					}
				} catch ( NumberFormatException ex ) {
					// invalid index
				}
			}
		} else {
			return null;
		}
		return null;
	}
	
	protected String members ( org.bukkit.entity.Player player , Team team , String params ) {
		boolean formatted = extractIdentifier ( params.toLowerCase ( ) ).startsWith ( "formatted" );
		
		if ( formatted ) {
			StringBuilder       result   = new StringBuilder ( );
			Iterator < Player > iterator = team.getPlayers ( ).iterator ( );
			Integer             limit    = null;
			
			// list limit
			try {
				limit = Integer.valueOf ( extractIdentifier ( extractIdentifier ( params ) ) );
			} catch ( NumberFormatException ex ) {
				// invalid/not specified limit
			}
			
			while ( iterator.hasNext ( ) ) {
				Player member = iterator.next ( );
				
				if ( member != null && member.isOnline ( ) ) {
					org.bukkit.entity.Player bukkit = member.getBukkitPlayer ( );
					double                   health = bukkit.getHealth ( );
					double                   alpha  = Math.min ( health / EntityUtil.getMaxHealth ( bukkit ) , 1.0D );
					
					if ( member.isSpectator ( ) || alpha <= 0.0D ) { // dead
						result.append ( String.format ( EnumLanguage.TEAM_MEMBER_HEALTH_FORMAT_DEAD.getAsString ( ) ,
														bukkit.getName ( ) ) );
					} else if ( alpha < 0.40D ) { // low health line ( health lower than 40% )
						result.append ( String.format ( EnumLanguage.TEAM_MEMBER_HEALTH_FORMAT_LOW.getAsString ( ) ,
														( int ) health , bukkit.getName ( ) ) );
					} else if ( alpha < 0.75D ) { // normal health line ( health lower than 75% )
						result.append ( String.format ( EnumLanguage.TEAM_MEMBER_HEALTH_FORMAT_NORMAL.getAsString ( ) ,
														( int ) health , bukkit.getName ( ) ) );
					} else if ( alpha <= 1.0D ) { // good health line (near 100%)
						result.append ( String.format ( EnumLanguage.TEAM_MEMBER_HEALTH_FORMAT_FINE.getAsString ( ) ,
														( int ) health , bukkit.getName ( ) ) );
					}
					
					// 'you' suffix
					if ( player != null && Objects.equals (
							player.getUniqueId ( ) , bukkit.getUniqueId ( ) ) ) {
						result.append ( ChatColor.RESET ).append ( ' ' )
								.append ( ChatColor.GRAY + "(" )
								.append ( EnumLanguage.YOU_WORD.getAsStringStripColors ( ) )
								.append ( ")" );
					}
				}
				
				// list limit
				if ( limit != null ) {
					if ( limit > 0 ) {
						limit--;
					} else {
						break;
					}
				}
				
				// there is another one, so let's
				// append the line separator.
				if ( iterator.hasNext ( ) ) {
					result.append ( System.lineSeparator ( ) );
				}
			}
			
			// making sure is unscaped to make sure
			// special characters will work.
			return StringEscapeUtils.unescapeJava ( result.toString ( ) );
		} else {
			Stream < Player > members = team.getPlayers ( ).stream ( );
			
			// list limit
			try {
				int limit = Integer.parseInt ( extractIdentifier ( params ) );
				
				if ( limit > 0 ) {
					members = members.limit ( limit );
				}
			} catch ( NumberFormatException ex ) {
				// invalid/not specified limit
			}
			
			return members.map ( Player :: getName ).collect ( Collectors.joining ( System.lineSeparator ( ) ) );
		}
	}
}