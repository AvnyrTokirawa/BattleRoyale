package es.outlook.adriansrj.battleroyale.placeholder.node.arena;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.autostarter.AutoStarter;
import es.outlook.adriansrj.battleroyale.arena.border.BattleRoyaleArenaBorder;
import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderResize;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaStat;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderTypableNode;
import es.outlook.adriansrj.battleroyale.util.time.TimeUtil;
import es.outlook.adriansrj.core.util.Duration;

import java.util.stream.Stream;

/**
 * <b>'br_arena'</b> placeholder node.
 *
 * @author AdrianSR / 06/10/2021 / 12:26 p. m.
 */
public class ArenaPlaceholderNode extends PlaceholderTypableNode < BattleRoyaleArena > {
	
	public static final String IDENTIFIER = "arena";
	
	public ArenaPlaceholderNode ( ) {
		super ( BattleRoyaleArena.class );
	}
	
	@Override
	public String getSubIdentifier ( ) {
		return IDENTIFIER;
	}
	
	@Override
	protected String onRequest ( org.bukkit.entity.Player player , String params ) {
		Player            br_player = player != null ? Player.getPlayer ( player ) : null;
		BattleRoyaleArena arena     = br_player != null ? br_player.getArena ( ) : null;
		
		if ( arena != null ) {
			return onRequest ( arena , params );
		} else {
			return null;
		}
	}
	
	@Override
	protected String onRequest ( BattleRoyaleArena arena , String params ) {
		if ( arena != null ) {
			if ( params.toLowerCase ( ).startsWith ( "name" ) ) { // br_arena_name
				return arena.getName ( );
			} else if ( params.toLowerCase ( ).startsWith ( "description" ) ) { // br_arena_description
				return arena.getDescription ( );
			} else if ( params.toLowerCase ( ).startsWith ( "count" ) ) { // br_arena_count
				return String.valueOf ( arena.getCount ( ) );
			} else if ( params.toLowerCase ( ).startsWith ( "limit" ) ) { // br_arena_limit
				return String.valueOf ( arena.getMode ( ).getMaxPlayers ( ) );
			} else if ( params.toLowerCase ( ).startsWith ( "state" ) ) { // br_arena_state
				return arena.getState ( ).getLanguage ( ).getAsStringStripColors ( );
			} else if ( params.toLowerCase ( ).startsWith ( "left" ) ) { // br_arena_left
				return String.valueOf (
						arena.getTeamRegistry ( ).getHandle ( ).stream ( )
								.map ( team -> team.getPlayers ( ).stream ( )
										.filter ( Player :: isPlaying )
										.count ( ) )
								.reduce ( 0L , Long :: sum ).intValue ( ) );
			} else if ( params.toLowerCase ( ).startsWith ( "border" ) ) { // br_arena_border
				params = extractIdentifier ( params );
				
				if ( arena.getState ( ) == EnumArenaState.RUNNING ) {
					BattleRoyaleArenaBorder border = arena.getBorder ( );
					
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
				}
				
				return null;
			} else if ( params.toLowerCase ( ).startsWith ( "autostart" ) ) { // br_arena_autostart
				params = extractIdentifier ( params );
				AutoStarter starter = arena.getAutoStarter ( );
				
				if ( starter != null ) {
					if ( params.toLowerCase ( ).startsWith ( "required" ) ) {  // br_arena_autostart_required_players/teams
						params = extractIdentifier ( params );
						
						if ( params.toLowerCase ( ).startsWith ( "players" ) ) {
							return String.valueOf ( arena.getConfiguration ( ).getAutostartRequiredPlayers ( ) );
						} else if ( params.toLowerCase ( ).startsWith ( "teams" ) ) {
							return String.valueOf ( arena.getConfiguration ( ).getAutostartRequiredTeams ( ) );
						}
					} else if ( params.toLowerCase ( ).startsWith ( "count" ) ) {  // br_arena_autostart_count
						if ( arena.getMode ( ).isSolo ( ) ) {
							return String.valueOf ( arena.getCount ( false ) );
						} else {
							return String.valueOf ( arena.getTeamRegistry ( ).stream ( ).filter (
									team -> !team.isEmpty ( ) ).count ( ) );
						}
					} else if ( params.toLowerCase ( ).startsWith ( "state" ) ) {  // br_arena_autostart_state
						if ( starter.isStarted ( ) ) {
							Duration time_left = starter.getTimeLeft ( );
							
							return time_left != null ? String.format (
									EnumLanguage.AUTO_STARTER_STATE_STARTING.getAsString ( ) ,
									TimeUtil.formatTime ( time_left ) ) : null;
						} else {
							if ( arena.isPrepared ( ) ) {
								if ( arena.getMode ( ).isSolo ( ) ) {
									// teams are the same as players in a solo-mode.
									return String.format (
											EnumLanguage.AUTO_STARTER_STATE_WAITING_PLAYER.getAsString ( ) ,
											arena.getCount ( false ) + "/" + arena.getConfiguration ( )
													.getAutostartRequiredPlayers ( ) );
								} else {
									int required_players = arena.getConfiguration ( ).getAutostartRequiredPlayers ( );
									int required_teams   = arena.getConfiguration ( ).getAutostartRequiredTeams ( );
									
									// in case there are no enough teams we will
									// return the required number of teams.
									int team_count = ( int ) arena.getTeamRegistry ( ).stream ( )
											.filter ( team -> !team.isEmpty ( ) ).count ( );
									
									if ( team_count < required_teams ) {
										return String.format (
												EnumLanguage.AUTO_STARTER_STATE_WAITING_TEAM.getAsString ( ) ,
												team_count + "/" + required_teams );
									}
									
									// in case there are enough teams, but no enough players
									// we will return the required number of players.
									int               player_count;
									Stream < Player > player_stream = arena.getPlayers ( false ).stream ( );
									
									if ( !arena.getMode ( ).isAutoFillEnabled ( ) ) {
										// only players on a team will be considered if auto-fill is disabled,
										// as they will not be part of the game unless they join a team.
										player_stream = player_stream.filter ( Player :: hasTeam );
									}
									
									player_count = ( int ) player_stream.count ( );
									
									if ( player_count < required_players ) {
										return String.format (
												EnumLanguage.AUTO_STARTER_STATE_WAITING_PLAYER.getAsString ( ) ,
												player_count + "/" + required_players );
									}
								}
							} else {
								return EnumLanguage.AUTO_STARTER_STATE_WAITING_BATTLEFIELD.getAsString ( );
							}
						}
					}
				} else {
					return null;
				}
			}
			
			// arena/match temporal stats
			for ( EnumArenaStat stat_type : EnumArenaStat.values ( ) ) { // br_arena_{stat}
				if ( params.toLowerCase ( ).startsWith ( stat_type.name ( ).toLowerCase ( ) ) ) {
					return String.valueOf ( arena.getStats ( ).get ( stat_type ) );
				}
			}
		}
		return null;
	}
}