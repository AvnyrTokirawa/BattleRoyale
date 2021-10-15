package es.outlook.adriansrj.battleroyale.placeholder.node.arena;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.border.BattleRoyaleArenaBorder;
import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderResize;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNode;
import es.outlook.adriansrj.battleroyale.util.time.TimeUtil;
import es.outlook.adriansrj.core.util.Duration;

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
			} else if ( params.toLowerCase ( ).startsWith ( "left" ) ) { // br_arena_left
				return String.valueOf (
						br_player.getArena ( ).getTeamRegistry ( ).getHandle ( ).stream ( )
								.map ( team -> team.getPlayers ( ).stream ( )
										.filter ( Player :: isPlaying )
										.count ( ) )
								.reduce ( 0L , Long :: sum ).intValue ( ) );
			} else if ( params.toLowerCase ( ).startsWith ( "border" ) ) { // br_arena_border
				BattleRoyaleArenaBorder border = arena.getBorder ( );
				params = extractIdentifier ( params );
				
				if ( params.toLowerCase ( ).startsWith ( "state" ) ) { // br_arena_border_state
					BattlefieldBorderResize point = border.getPoint ( );
					
					switch ( border.getState ( ) ) {
						case IDLE: {
							if ( point != null ) {
								return String.format (
										EnumLanguage.BORDER_STATE_IDLE.getAsString ( ) ,
										TimeUtil.formatTime ( Duration.ofMilliseconds (
												( border.getPoint ( ).getIdleTime ( ).toMillis ( ) + 1000 )
														- ( System.currentTimeMillis ( ) - border.getStateTime ( ) ) ) ) );
							}
							return EnumLanguage.BORDER_STATE_STOPPED.getAsString ( );
						}
						
						case RESIZING: {
							if ( point != null ) {
								return String.format (
										border.getFutureSize ( ) < border.getCurrentSize ( )
												? EnumLanguage.BORDER_STATE_SHRINKING.getAsString ( )
												: EnumLanguage.BORDER_STATE_GROWING.getAsString ( ) ,
										TimeUtil.formatTime ( Duration.ofMilliseconds (
												( border.getPoint ( ).getTime ( ).toMillis ( ) + 1000 )
														- ( System.currentTimeMillis ( ) - border.getStateTime ( ) ) ) ) );
							}
							return EnumLanguage.BORDER_STATE_STOPPED.getAsString ( );
						}
						
						default:
						case STOPPED: {
							return EnumLanguage.BORDER_STATE_STOPPED.getAsString ( );
						}
					}
				}
				return null;
			}
		}
		
		return null;
	}
}