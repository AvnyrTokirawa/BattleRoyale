package es.outlook.adriansrj.battleroyale.placeholder.node.player;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.enums.EnumStat;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNode;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import es.outlook.adriansrj.core.util.math.IntersectionUtil;
import es.outlook.adriansrj.core.util.math.collision.Ray;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * <b>'br_player'</b> placeholder node.
 *
 * @author AdrianSR / 06/10/2021 / 11:00 a. m.
 */
public class PlayerPlaceholderNode extends PlaceholderNode {
	
	public static final String IDENTIFIER = "player";
	
	/**
	 * @author AdrianSR / 10/10/2021 / 06:42 p. m.
	 */
	protected enum SafeZoneDirection {
		
		FRONT ( 0.0F , ( char ) 8673 ),
		BACK ( 180.0F , ( char ) 8675 ),
		RIGHT ( 90.0F , ( char ) 8674 ),
		LEFT ( -90.0F , ( char ) 8672 ),
		
		FRONT_RIGHT ( 45.0F , ( char ) 8599 ),
		FRONT_LEFT ( -45.0F , ( char ) 8598 ),
		BACK_RIGHT ( 135.0F , ( char ) 8600 ),
		BACK_LEFT ( -135.0F , ( char ) 8601 );
		
		final float add;
		final char  character;
		
		SafeZoneDirection ( float add , char character ) {
			this.add       = add;
			this.character = character;
		}
	}
	
	protected static final SafeZoneDirection[] FIRST_LINE_DIRECTIONS = {
			SafeZoneDirection.FRONT_LEFT ,
			SafeZoneDirection.FRONT ,
			SafeZoneDirection.FRONT_RIGHT
	};
	
	protected static final SafeZoneDirection[] SECOND_LINE_DIRECTIONS = {
			SafeZoneDirection.LEFT ,
			null ,
			SafeZoneDirection.RIGHT
	};
	
	protected static final SafeZoneDirection[] THIRD_LINE_DIRECTIONS = {
			SafeZoneDirection.BACK_LEFT ,
			SafeZoneDirection.BACK ,
			SafeZoneDirection.BACK_RIGHT
	};
	
	@Override
	public String getSubIdentifier ( ) {
		return IDENTIFIER;
	}
	
	@Override
	protected String onRequest ( org.bukkit.entity.Player player , String params ) {
		Player br_player = Player.getPlayer ( player );
		
		// temporal stats
		for ( EnumStat stat_type : EnumStat.values ( ) ) { // br_player_{stat}
			if ( params.toLowerCase ( ).startsWith ( stat_type.name ( ).toLowerCase ( ) ) ) {
				return String.valueOf ( br_player.getDataStorage ( ).getTempStat ( stat_type ) );
			}
		}
		
		// stats
		if ( params.toLowerCase ( ).startsWith ( "stat" ) ) {
			params = extractIdentifier ( params );
			
			for ( EnumStat stat_type : EnumStat.values ( ) ) { // br_player_stat_{stat}
				if ( params.toLowerCase ( ).startsWith ( stat_type.name ( ).toLowerCase ( ) ) ) {
					return String.valueOf ( br_player.getDataStorage ( ).getStat ( stat_type ) );
				}
			}
		}
		
		// requires the player to actually be in an arena
		if ( br_player.isInArena ( ) ) {
			if ( params.toLowerCase ( ).startsWith ( "safecompass" ) ) { // br_player_safecompass_{index}
				return safeZoneCompass ( player , br_player , params );
			} else if ( params.toLowerCase ( ).startsWith ( "safe" ) ) { // br_player_safe
				return safe ( player , br_player , params );
			}
		}
		
		return null;
	}
	
	protected String safeZoneCompass ( org.bukkit.entity.Player player , Player br_player , String params ) {
		BattleRoyaleArena arena = br_player.getArena ( );
		int               index = -1;
		
		try {
			// between 0 and 2
			index = Math.max ( Math.min ( Integer.parseInt ( extractIdentifier ( params ) ) , 2 ) , 0 );
		} catch ( NumberFormatException ex ) {
			// invalid/not specified limit
		}
		
		if ( index != -1 && arena != null ) {
			Location          player_location = player.getLocation ( );
			ZoneBounds        future_bounds   = arena.getBorder ( ).getFutureBounds ( );
			SafeZoneDirection right_direction = null;
			
			if ( br_player.isPlaying ( ) && future_bounds != null && !future_bounds.contains (
					player_location.getBlockX ( ) , player_location.getBlockZ ( ) ) ) {
				Location2I center     = future_bounds.getCenter ( );
				float      player_dir = DirectionUtil.normalize ( player_location.getYaw ( ) );
				float right_dir = DirectionUtil.normalize ( DirectionUtil.lookAt (
						player_location.toVector ( ) , new Vector ( center.getX ( ) , 0 , center.getZ ( ) ) )[ 0 ] );
				
				SafeZoneDirection closest_face = null;
				float             closest      = Float.MAX_VALUE;
				
				for ( SafeZoneDirection direction : SafeZoneDirection.values ( ) ) {
					float rotated  = DirectionUtil.normalize ( player_dir + direction.add );
					float distance = Math.abs ( right_dir - rotated );
					
					if ( closest_face == null || distance < closest ) {
						closest_face = direction;
						closest      = distance;
					}
				}
				
				right_direction = closest_face;
			}
			
			// building line
			StringBuilder       result = new StringBuilder ( );
			SafeZoneDirection[] directions;
			
			switch ( index ) {
				case 0: {
					directions = FIRST_LINE_DIRECTIONS;
					break;
				}
				
				case 1: {
					directions = SECOND_LINE_DIRECTIONS;
					break;
				}
				
				default:
				case 2: {
					directions = THIRD_LINE_DIRECTIONS;
					break;
				}
			}
			
			for ( SafeZoneDirection other : directions ) {
				if ( other != null ) {
					result.append ( ( right_direction == null || other == right_direction )
											? ChatColor.GREEN : ChatColor.GRAY );
					result.append ( other.character );
					result.append ( ' ' );
				} else {
					result.append ( ' ' );
					result.append ( ' ' );
				}
			}
			
			return result.toString ( );
		}
		
		return null;
	}
	
	protected String safe ( org.bukkit.entity.Player player , Player br_player , String params ) {
		BattleRoyaleArena arena           = br_player.getArena ( );
		ZoneBounds        future_bounds   = arena != null ? arena.getBorder ( ).getFutureBounds ( ) : null;
		boolean           upper_case      = extractIdentifier ( params ).equalsIgnoreCase ( "upper" );
		Location          player_location = player.getLocation ( );
		
		if ( !br_player.isPlaying ( ) || future_bounds == null
				|| future_bounds.contains ( player_location.getX ( ) , player_location.getZ ( ) ) ) {
			String word = EnumLanguage.SAFE_WORD.getAsStringStripColors ( );
			return upper_case ? word.toUpperCase ( ) : word;
		} else {
			Location2I center        = future_bounds.getCenter ( );
			Vector     intersection  = new Vector ( );
			Vector     player_vector = player_location.toVector ( );
			
			IntersectionUtil.intersectRayBounds (
					new Ray ( player_vector , DirectionUtil.getDirection ( DirectionUtil.lookAt (
							player_vector ,
							new Vector ( center.getX ( ) , 0 , center.getZ ( ) ) )[ 0 ] , 0.0F ) ) ,
					future_bounds.toBoundingBox ( ) ,
					intersection );
			
			return ( int ) ( intersection.distance ( player_vector ) + 1.0F ) +
					EnumLanguage.METERS_WORD.getAsStringStripColors ( );
		}
	}
}
