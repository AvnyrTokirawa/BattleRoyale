package es.outlook.adriansrj.battleroyale.placeholder.node.arena;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNode;
import es.outlook.adriansrj.battleroyale.player.Player;

/**
 * <b>'br_arena'</b> placeholder node.
 *
 * @author AdrianSR / 06/10/2021 / 12:26 p. m.
 */
public class ArenaPlaceholderNode extends PlaceholderNode {
	
	protected static final String COUNT_IDENTIFIER = "count";
	protected static final String LIMIT_IDENTIFIER = "limit";
	protected static final String STATE_IDENTIFIER = "state";
	
	@Override
	public String getSubIdentifier ( ) {
		return "arena";
	}
	
	@Override
	protected String onRequest ( org.bukkit.entity.Player player , String params ) {
		Player            br_player = player != null ? Player.getPlayer ( player ) : null;
		BattleRoyaleArena arena     = br_player != null ? br_player.getArena ( ) : null;
		
		if ( arena != null ) {
			if ( params.toLowerCase ( ).startsWith ( COUNT_IDENTIFIER ) ) {
				return String.valueOf ( arena.getCount ( ) );
			} else if ( params.toLowerCase ( ).startsWith ( LIMIT_IDENTIFIER ) ) {
				return String.valueOf ( arena.getMode ( ).getMaxPlayers ( ) );
			} else if ( params.toLowerCase ( ).startsWith ( STATE_IDENTIFIER ) ) {
				return arena.getState ( ).getLanguage ( ).getAsStringStripColors ( );
			}
		}
		
		return null;
	}
}