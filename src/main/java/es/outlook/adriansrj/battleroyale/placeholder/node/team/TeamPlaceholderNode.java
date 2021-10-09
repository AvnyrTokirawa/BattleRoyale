package es.outlook.adriansrj.battleroyale.placeholder.node.team;

import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNode;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.Team;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <b>'br_team'</b> placeholder node.
 *
 * @author AdrianSR / 09/10/2021 / 07:44 a. m.
 */
public class TeamPlaceholderNode extends PlaceholderNode {
	
	@Override
	public String getSubIdentifier ( ) {
		return "team";
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
			} else if ( params.toLowerCase ( ).startsWith ( "members" ) ) { // br_team_members | br_team_members_{limit}
				Stream < String > names = team.getPlayers ( ).stream ( ).map ( Player :: getName );
				
				// list limit
				try {
					int limit = Integer.parseInt ( extractIdentifier ( params ) );
					
					if ( limit > 0 ) {
						names = names.limit ( limit );
					}
				} catch ( NumberFormatException ex ) {
					// invalid/not specified limit
				}
				
				return names.collect ( Collectors.joining ( System.lineSeparator ( ) ) );
			}
		} else {
			return null;
		}
		return null;
	}
	
}