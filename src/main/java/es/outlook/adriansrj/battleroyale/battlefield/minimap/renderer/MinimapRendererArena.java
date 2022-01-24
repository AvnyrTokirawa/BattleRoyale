package es.outlook.adriansrj.battleroyale.battlefield.minimap.renderer;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.bombing.BombingZone;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.Location;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Arena battlefield minimap renderer.
 *
 * @author AdrianSR / 03/09/2021 / 08:47 p. m.
 */
public class MinimapRendererArena extends MinimapRendererBase {
	
	protected final BattleRoyaleArena arena;
	
	public MinimapRendererArena ( BattleRoyaleArena arena ) {
		super ( arena.getBattlefield ( ).getMinimap ( ) , arena.getFullBounds ( ) );
		this.arena = arena;
	}
	
	@Override
	protected void render ( MapCanvas canvas , ZoneBounds display_bounds , org.bukkit.entity.Player player ) {
		drawPlayer ( player.getLocation ( ) , canvas , MapCursor.Type.WHITE_POINTER , display_bounds );
		
		// drawing teammates
		Player br_player = Player.getPlayer ( player );
		
		if ( br_player.hasTeam ( ) ) {
			br_player.getTeam ( ).getPlayers ( ).stream ( )
					.filter ( teammate -> !Objects.equals ( teammate.getUniqueId ( ) , player.getUniqueId ( ) ) )
					.filter ( Player :: isPlaying )
					.filter ( teammate -> !teammate.isSpectator ( ) )
					.map ( Player :: getBukkitPlayerOptional ).filter ( Optional :: isPresent ).map ( Optional :: get )
					.forEach ( teammate -> drawPlayer (
							teammate.getLocation ( ) , canvas , MapCursor.Type.GREEN_POINTER , display_bounds ) );
		}
		
		/* drawing border */
		if ( arena.getState ( ) == EnumArenaState.RUNNING ) {
			// current border bounds
			// we want to wrap it in case it is displaying the initial border.
			drawBorder ( arena , arena.getBorder ( ).getCurrentBounds ( ) ,
						 display_bounds , player , canvas , MapCursor.Type.RED_POINTER , true );
			
			// future border bounds
			ZoneBounds future_border = arena.getBorder ( ).getFutureBounds ( );
			
			if ( future_border != null ) {
				drawBorder ( arena , future_border , display_bounds , player , canvas ,
							 MapCursor.Type.BLUE_POINTER , false );
			}
			
			// bombing zones
			List < BombingZone > bombing_zones = arena.getBombingZoneGenerator ( ).current ( );
			
			for ( BombingZone zone : bombing_zones ) {
				if ( zone.isActive ( ) ) {
					drawBombingZone ( arena , zone , display_bounds , player , canvas , false );
				}
			}
		}
	}
	
	@SuppressWarnings ( "deprecation" )
	protected void drawBombingZone ( BattleRoyaleArena arena , BombingZone zone , ZoneBounds display_bounds ,
			org.bukkit.entity.Player player , MapCanvas canvas , boolean wrap ) {
		ZoneBounds bounds = zone.getBounds ( );
		
		if ( bounds.getSize ( ) > 0 ) {
			boolean    full   = bounds.getSize ( ) >= arena.getFullBounds ( ).getSize ( );
			int        size   = full && wrap ? bounds.getSize ( ) - 2 : bounds.getSize ( );
			Location2I center = bounds.getCenter ( );
			MapLocation projected_center = project ( new Vector ( center.getX ( ) , 0 , center.getZ ( ) ) ,
													 0.0F , display_bounds , true );
			
			for ( MapLocation location : drawCuboid (
					new Location ( player.getWorld ( ) , center.getX ( ) , 0 , center.getZ ( ) ) ,
					size , display_bounds ) ) {
				if ( location.isOutOfBounds ( ) ) { continue; }
				
				int            dx        = projected_center.x - location.x;
				int            dy        = projected_center.y - location.y;
				float          dd        = DirectionUtil.getEulerAngles ( new Vector ( dx , 0 , dy ).normalize ( ) )[ 0 ];
				byte           direction = ( byte ) ( 1 + ( DirectionUtil.normalize ( dd ) / 360.0F ) * 15 );
				MapCursor.Type type      = MapCursor.Type.RED_POINTER;
				
				// newer versions
				if ( EnumReflection.getEnumConstant ( MapCursor.Type.class , "RED_MARKER" ) != null ) {
					// direction for RED_MARKER is inverted
					type      = MapCursor.Type.RED_MARKER;
					direction = ( byte ) ( ( direction + ( 15 / 2 ) ) % 15 );
				}
				
				if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_12_R1 ) ) {
					canvas.getCursors ( ).addCursor ( new MapCursor (
							( byte ) location.x , ( byte ) location.y ,
							direction , type , true ) );
				} else {
					canvas.getCursors ( ).addCursor ( new MapCursor (
							( byte ) location.x , ( byte ) location.y ,
							direction , type.getValue ( ) , true ) );
				}
			}
		}
	}
}
