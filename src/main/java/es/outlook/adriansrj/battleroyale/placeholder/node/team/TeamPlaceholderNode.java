package es.outlook.adriansrj.battleroyale.placeholder.node.team;

import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNode;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.battleroyale.player.Team;

/**
 *
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
			if ( params.toLowerCase ( ).startsWith ( "count" ) ) { // br_team_count
				return String.valueOf ( team.getCount ( ) );
			} else if ( params.toLowerCase ( ).startsWith ( "member" ) ) { // br_team_member
			
			}
		} else {
			return null;
		}
		return null;
	}
}