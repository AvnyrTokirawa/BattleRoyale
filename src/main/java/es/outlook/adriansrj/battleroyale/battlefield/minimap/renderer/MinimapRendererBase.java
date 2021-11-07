package es.outlook.adriansrj.battleroyale.battlefield.minimap.renderer;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.Minimap;
import es.outlook.adriansrj.battleroyale.util.math.ColorMatrix;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import es.outlook.adriansrj.core.util.math.Vector2I;
import org.bukkit.Location;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * @author AdrianSR / 08/09/2021 / 10:02 a. m.
 */
public abstract class MinimapRendererBase extends MinimapRenderer {
	
	protected MinimapRendererBase ( ColorMatrix colors , ZoneBounds bounds ) {
		super ( colors , bounds );
	}
	
	protected MinimapRendererBase ( Minimap minimap , ZoneBounds bounds ) {
		super ( minimap , bounds );
	}
	
	protected void drawPlayer ( Location location , MapCanvas canvas ,
			MapCursor.Type type , ZoneBounds display_bounds ) {
		MapLocation player_location = project ( location , display_bounds , true );
		
		canvas.getCursors ( ).addCursor ( player_location.x , player_location.y ,
										  player_location.direction , type.getValue ( ) );
	}
	
	protected void drawBorder ( BattleRoyaleArena arena , ZoneBounds border_bounds , ZoneBounds display_bounds ,
			org.bukkit.entity.Player player , MapCanvas canvas , MapCursor.Type type , boolean wrap ) {
		if ( border_bounds.getSize ( ) > 0 ) {
			boolean    full   = border_bounds.getSize ( ) >= arena.getFullBounds ( ).getSize ( );
			int        size   = full && wrap ? border_bounds.getSize ( ) - 2 : border_bounds.getSize ( );
			Location2I center = border_bounds.getCenter ( );
			
			for ( MapLocation location : drawCuboid (
					new Location ( player.getWorld ( ) , center.getX ( ) , 0 , center.getZ ( ) ) ,
					size , display_bounds ) ) {
				if ( location.isOutOfBounds ( ) ) { continue; }
				
				canvas.getCursors ( ).addCursor ( location.x , location.y , location.direction , type.getValue ( ) );
			}
		}
	}
	
	protected Set < MinimapRenderer.MapLocation > drawCuboid ( Location center , double radius ,
			ZoneBounds display_bounds ) {
		Set < MinimapRenderer.MapLocation > result = new HashSet <> ( );
		
		double half_radius = radius / 2;
		
		MinimapRenderer.MapLocation corner_a = project ( center.clone ( ).add ( -half_radius , 0.0D , -half_radius ) ,
														 display_bounds ,
														 true ); // top-left corner
		MinimapRenderer.MapLocation corner_b = project ( center.clone ( ).add ( half_radius , 0.0D , -half_radius ) ,
														 display_bounds ,
														 true );  // top-right corner
		MinimapRenderer.MapLocation corner_c = project ( center.clone ( ).add ( -half_radius , 0.0D , half_radius ) ,
														 display_bounds ,
														 true );  // bottom-left corner
		MinimapRenderer.MapLocation corner_d = project ( center.clone ( ).add ( half_radius , 0.0D , half_radius ) ,
														 display_bounds ,
														 true );   // bottom-right corner
		
		result.addAll ( connect ( corner_a , corner_b , display_bounds ) );
		result.addAll ( connect ( corner_c , corner_d , display_bounds ) );
		result.addAll ( connect ( corner_a , corner_c , display_bounds ) );
		result.addAll ( connect ( corner_b , corner_d , display_bounds ) );
		
		return result;
	}
	
	protected Set < MapLocation > connect ( MapLocation corner_a , MapLocation corner_b , ZoneBounds display_bounds ) {
		Set < MapLocation > result = new HashSet <> ( );
		
		int x_min = Math.min ( corner_a.x , corner_b.x );
		int x_max = Math.max ( corner_a.x , corner_b.x );
		int y_min = Math.min ( corner_a.y , corner_b.y );
		int y_max = Math.max ( corner_a.y , corner_b.y );
		
		double distance  = new Vector2I ( x_max , y_max ).distance ( new Vector2I ( x_min , y_min ) );
		Vector direction = new Vector ( ( double ) x_max - x_min , 0.0D , ( double ) y_max - y_min ).normalize ( );
		
		if ( direction.getX ( ) == 0.0D && direction.getZ ( ) == 0.0D ) {
			return result;
		}
		
		int count = ( int ) distance / DISTANCE_BETWEEN_BORDER_CURSORS;
		
		for ( int i = 0 ; i <= count ; i++ ) {
			Vector point = new Vector ( x_min , 0.0D , y_min )
					.add ( direction.clone ( ).multiply ( i * DISTANCE_BETWEEN_BORDER_CURSORS ) );
			byte rotation = ( byte ) ( int ) Math.round ( ( int ) DirectionUtil.normalize (
					DirectionUtil.getEulerAngles ( direction )[ 0 ] ) / 22.5 );
			
			if ( ( int ) point.getX ( ) == 0 && ( int ) point.getZ ( ) == 0 ) {
				// something very weird happens if we don't check this.
				continue;
			}
			
			result.add ( new MapLocation ( ( int ) point.getX ( ) , ( int ) point.getZ ( ) , rotation ) );
		}
		return result;
	}
}
