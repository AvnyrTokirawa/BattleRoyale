package es.outlook.adriansrj.battleroyale.battlefield.minimap.renderer;

import es.outlook.adriansrj.battleroyale.battlefield.minimap.Minimap;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.MinimapHandler;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.MinimapZoom;
import es.outlook.adriansrj.battleroyale.util.math.ColorMatrix;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import es.outlook.adriansrj.core.util.math.Vector2I;
import es.outlook.adriansrj.core.util.math.Vector3I;
import es.outlook.adriansrj.core.util.server.Version;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.*;
import org.bukkit.util.Vector;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author AdrianSR / 01/09/2021 / 12:04 p. m.
 */
public abstract class MinimapRenderer extends MapRenderer {
	
	/**
	 * Distance between border cursors measured in pixels.
	 */
	protected static final int DISTANCE_BETWEEN_BORDER_CURSORS = 10;
	
	/**
	 * Represents a location within a map.
	 *
	 * @author AdrianSR / Wednesday 02 September, 2020 / 02:42 PM
	 */
	protected static class MapLocation {
		
		protected final int  x;
		protected final int  y;
		protected final byte direction;
		
		protected MapLocation ( int x , int y , byte direction ) {
			this.x         = x;
			this.y         = y;
			this.direction = ( byte ) ( direction & 15 );
		}
		
		protected boolean isOutOfBounds ( ) {
			return x <= -128 || x >= 127 || y <= -128 || y >= 127;
		}
	}
	
	protected final Map < UUID, MinimapZoom > zoom_cache     = new HashMap <> ( );
	protected final Map < UUID, Vector >      location_cache = new HashMap <> ( );
	
	protected final ColorMatrix colors;
	protected final ZoneBounds  bounds;
	protected       boolean     force_rendering;
	
	protected MinimapRenderer ( ColorMatrix colors , ZoneBounds bounds ) {
		super ( true );
		
		Validate.notNull ( bounds , "bounds cannot be null" );
		
		this.colors          = colors;
		this.bounds          = bounds;
		this.force_rendering = false;
	}
	
	protected MinimapRenderer ( Minimap minimap , ZoneBounds bounds ) {
		this ( Objects.requireNonNull ( minimap.getColors ( ) , "minimap cannot be null" ) , bounds );
	}
	
	/**
	 * Request rendering.
	 */
	public void requestRendering ( ) {
		this.force_rendering = true;
	}
	
	@Override
	public void render ( MapView view , MapCanvas canvas , Player player ) {
		ZoneBounds  display_bounds = bounds;
		MinimapZoom zoom           = MinimapHandler.getInstance ( ).getSettings ( player ).getZoom ( );
		MinimapZoom last_zoom      = zoom_cache.get ( player.getUniqueId ( ) );
		
		// here we're drawing the minimap
		if ( zoom == MinimapZoom.NORMAL ) {
			if ( last_zoom != zoom || force_rendering ) {
				for ( int x = 0 ; x < 128 ; x++ ) {
					for ( int y = 0 ; y < 128 ; y++ ) {
						double d0    = x / 128.0D;
						double d1    = y / 128.0D;
						int    range = colors.capacity;
						Color  color = colors.get ( ( int ) ( range * d0 ) , ( int ) ( range * d1 ) );
						
						if ( color.getTransparency ( ) == Color.TRANSLUCENT ) {
							canvas.setPixel ( x , y , MapPalette.TRANSPARENT );
						} else {
							canvas.setPixel ( x , y , MapPalette.matchColor ( color ) );
						}
					}
				}
				
				force_rendering = false;
			}
		} else {
			Vector   last_location      = location_cache.get ( player.getUniqueId ( ) );
			Vector3I location           = toVector3i ( player.getLocation ( ) );
			int      display_range      = zoom.getDisplayRange ( );
			int      display_range_half = display_range >> 1;
			
			display_bounds = new ZoneBounds (
					toVector2I ( location.subtract ( display_range_half , 0 , display_range_half ) ) ,
					toVector2I ( location.add ( display_range_half , 0 , display_range_half ) ) );
			
			// must be within bounds to render in this zoom level
			if ( bounds.contains ( player.getLocation ( ).getX ( ) ,
								   player.getLocation ( ).getZ ( ) ) &&
					( last_zoom != zoom
							|| !Objects.equals ( player.getLocation ( ).toVector ( ) , last_location )
							|| force_rendering ) ) {
				int x_offset = display_bounds.getMinimum ( ).getX ( ) - bounds.getMinimum ( ).getX ( );
				int y_offset = display_bounds.getMinimum ( ).getZ ( ) - bounds.getMinimum ( ).getZ ( );
				
				for ( int x = 0 ; x < 128 ; x++ ) {
					for ( int y = 0 ; y < 128 ; y++ ) {
						double d0 = x / 128.0D;
						double d1 = y / 128.0D;
						
						int xx = x_offset + ( int ) ( display_range * d0 );
						int yy = y_offset + ( int ) ( display_range * d1 );
						
						if ( xx >= 0 && yy >= 0 && xx < colors.capacity && yy < colors.capacity ) {
							Color color = colors.get ( xx , yy );
							
							if ( color.getTransparency ( ) == Transparency.TRANSLUCENT ) {
								canvas.setPixel ( x , y , MapPalette.TRANSPARENT );
							} else {
								canvas.setPixel ( x , y , MapPalette.matchColor ( color ) );
							}
						} else {
							canvas.setPixel ( x , y , MapPalette.TRANSPARENT );
						}
					}
				}
				
				force_rendering = false;
				
				// caching player location as vector (as vector to ignore yaw and pitch rotations)
				location_cache.put ( player.getUniqueId ( ) , player.getLocation ( ).toVector ( ) );
			}
		}
		
		// caching zoom
		zoom_cache.put ( player.getUniqueId ( ) , zoom );
		
		// here we're going to add cursors, but first we have to clear
		// the cursors of the last render.
		clearCursors ( canvas );
		render ( canvas , display_bounds , player );
		
		// here we're implementing our unlimited tracking
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_11_R1 ) ) {
			unlimitedTrackingCheck ( canvas );
		}
	}
	
	/**
	 * Called from {@link #render(MapView , MapCanvas , Player)} which ask for render.
	 * <br>
	 * At this point, the zone is already rendered, so this method is actually called to render stuff like cursors;
	 * canvas.setPixel() is still valid though.
	 *
	 * @param canvas         the canvas for the render.
	 * @param display_bounds the bounds of the zone that is being rendered.
	 * @param player         the player that holds the map.
	 */
	protected abstract void render ( MapCanvas canvas , ZoneBounds display_bounds , Player player );
	
	/**
	 * Projects the given {@link Location} to a {@link MapLocation} between the specified {@link ZoneBounds}.
	 *
	 * @param location           the bukkit location.
	 * @param zone_bounds        the zone.
	 * @param clip_out_of_bounds whether to clip locations out of bounds.
	 *
	 * @return the corresponding map location, or null if off of limits and <strong>clip_off_limits</strong> is false.
	 */
	protected MapLocation project ( Location location , ZoneBounds zone_bounds , boolean clip_out_of_bounds ) {
		Vector3I vector = toVector3i ( location );
		Vector3I relative = vector.subtract (
				new Vector3I ( zone_bounds.getMinimum ( ).getX ( ) , 0 , zone_bounds.getMinimum ( ).getZ ( ) ) );
		
		double d0 = ( double ) relative.getX ( ) / ( double ) zone_bounds.getSize ( );
		double d1 = ( double ) relative.getZ ( ) / ( double ) zone_bounds.getSize ( );
		
		int x = -128 + ( int ) ( 256 * d0 );
		int y = -128 + ( int ) ( 256 * d1 );
		
		// clipping
		boolean out_bounds = false;
		
		if ( d0 <= 0.0D || d0 >= 1.0D ) {
			x          = d0 <= 0.0D ? -128 : 127;
			out_bounds = true;
		}
		
		if ( d1 <= 0.0D || d1 >= 1.0D ) {
			y          = d1 <= 0.0D ? -128 : 127;
			out_bounds = true;
		}
		
		if ( !out_bounds || clip_out_of_bounds ) {
			return new MapLocation ( x , y , ( byte ) ( 1 + ( DirectionUtil.normalize (
					location.getYaw ( ) ) / 360.0F ) * 15 ) );
		} else {
			return null;
		}
	}
	
	/**
	 * Projects the given location {@link Vector} to a {@link MapLocation} between the specified {@link ZoneBounds}.
	 *
	 * @param location           the location vector.
	 * @param yaw                the horizontal orientation.
	 * @param zone_bounds        the zone.
	 * @param clip_out_of_bounds whether to clip locations out of bounds.
	 *
	 * @return the corresponding map location, or null if off of limits and <strong>clip_off_limits</strong> is false.
	 */
	protected MapLocation project ( Vector location , float yaw , ZoneBounds zone_bounds , boolean clip_out_of_bounds ) {
		return project ( location.toLocation (
				Bukkit.getWorlds ( ).get ( 0 ) , yaw , 0.0F ) , zone_bounds , clip_out_of_bounds );
	}
	
	/**
	 * Projects the given {@link Location} to a {@link MapLocation} between a specified {@link ZoneBounds}.
	 * <p>
	 *
	 * @param location    the bukkit location.
	 * @param zone_bounds the zone.
	 *
	 * @return the corresponding map location.
	 */
	protected MapLocation project ( Location location , ZoneBounds zone_bounds ) {
		return project ( location , zone_bounds , false );
	}
	
	/**
	 * Gets the equivalent {@link Vector3I} for the provided {@link Location}.
	 *
	 * @param location the location to convert.
	 *
	 * @return the equivalent {@link Vector3I}.
	 */
	protected Vector3I toVector3i ( Location location ) {
		return new Vector3I ( location.getX ( ) , location.getY ( ) , location.getZ ( ) );
	}
	
	protected Vector2I toVector2I ( Vector3I vector ) {
		return new Vector2I ( vector.getX ( ) , vector.getZ ( ) );
	}
	
	/**
	 * Remove all cursors from specified {@link MapCanvas}.
	 *
	 * @param canvas the canvas to remove from.
	 */
	protected void clearCursors ( MapCanvas canvas ) {
		MapCursorCollection cursors = canvas.getCursors ( );
		
		while ( cursors.size ( ) > 0 ) {
			cursors.removeCursor ( cursors.getCursor ( 0 ) );
		}
	}
	
	/**
	 * Checks the unlimited tracking. <strong>Note that this feature is available since 1.11.</strong>
	 *
	 * @throws IllegalStateException if the version of the running server is
	 *                               <strong><code>< 1.11</code></strong>.
	 */
	protected void unlimitedTrackingCheck ( MapCanvas canvas ) {
		try {
			// this will throw an IllegalArgumentException on unsupported server versions.
			MapCursor.Type      off_limits_type = MapCursor.Type.valueOf ( "SMALL_WHITE_CIRCLE" );
			MapCursorCollection cursors         = canvas.getCursors ( );
			
			for ( int i = 0 ; i < cursors.size ( ) ; i++ ) {
				MapCursor cursor = cursors.getCursor ( i );
				if ( cursor.getType ( ) != MapCursor.Type.GREEN_POINTER && cursor.getType ( ) != MapCursor.Type.WHITE_POINTER
						&& cursor.getType ( ) != off_limits_type ) {
					// we're ignoring cursors that doesn't represent a player.
					continue;
				}
				
				byte    x          = cursor.getX ( );
				byte    y          = cursor.getY ( );
				boolean off_limits = false;
				
				if ( x <= -128 || x >= 127 ) {
					x          = ( byte ) ( x <= -128 ? -128 : 127 );
					off_limits = true;
				}
				
				if ( y <= -128 || y >= 127 ) {
					y          = ( byte ) ( y <= -128 ? -128 : 127 );
					off_limits = true;
				}
				
				if ( off_limits ) {
					cursor.setX ( x );
					cursor.setY ( y );
					cursor.setType ( MapCursor.Type.valueOf ( "SMALL_WHITE_CIRCLE" ) );
				}
			}
		} catch ( IllegalArgumentException ex ) {
			throw new IllegalStateException ( "unsupported server version!" );
		}
	}
}
