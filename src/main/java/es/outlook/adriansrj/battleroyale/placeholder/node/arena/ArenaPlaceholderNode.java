package es.outlook.adriansrj.battleroyale.placeholder.node.arena;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNode;

/**
 * <b>'br_arena'</b> placeholder node.
 *
 * @author AdrianSR / 06/10/2021 / 12:26 p. m.
 */
public class ArenaPlaceholderNode extends PlaceholderNode {
	
	@Override
	public String getSubIdentifier ( ) {
		return "arena";
	}
	
	@Override
	protected String onRequest ( org.bukkit.entity.Player player , String params ) {
		Player            br_player = player != null ? Player.getPlayer ( player ) : null;
		BattleRoyaleArena arena     = br_player != null ? br_player.getArena ( ) : null;
		
		if ( arena != null ) {
			if ( params.toLowerCase ( ).startsWith ( "name" ) ) { // br_arena_name
				return arena.getName ( );
			} else if ( params.toLowerCase ( ).startsWith ( "count" ) ) { // br_arena_count
				return String.valueOf ( arena.getCount ( ) );
			} else if ( params.toLowerCase ( ).startsWith ( "limit" ) ) { // br_arena_limit
				return String.valueOf ( arena.getMode ( ).getMaxPlayers ( ) );
			} else if ( params.toLowerCase ( ).startsWith ( "state" ) ) { // br_arena_state
				return arena.getState ( ).getLanguage ( ).getAsStringStripColors ( );
			}
		}
		
		return null;
	}
}