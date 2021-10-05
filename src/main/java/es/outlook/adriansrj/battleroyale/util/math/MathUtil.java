package es.outlook.adriansrj.battleroyale.util.math;

import es.outlook.adriansrj.battleroyale.battlefield.bus.BusSpawn;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import es.outlook.adriansrj.core.util.math.IntersectionUtil;
import es.outlook.adriansrj.core.util.math.collision.Ray;
import org.bukkit.util.Vector;

/**
 * Useful math class.
 *
 * @author AdrianSR / 08/09/2021 / 03:57 p. m.
 */
public class MathUtil {
	
	/**
	 * Approximates the end point location of the provided {@link BusSpawn} within the specified {@link ZoneBounds}.
	 *
	 * @param spawn the spawn to approximate.
	 * @param bounds the bounds to approximate.
	 * @return the resulting approximation, or <b>null</b> if the bus will never be
	 * within the bounds when displacing on this route.
	 */
	public static Vector approximateEndPointLocation ( BusSpawn spawn , ZoneBounds bounds ) {
		Vector direction      = DirectionUtil.getDirection ( spawn.getYaw ( ) , 0.0F );
		Vector start_location = bounds.project ( spawn.getStartLocation ( ) );
		
		if ( bounds.contains ( start_location ) ) {
			Vector point = start_location.clone ( );
			
			while ( bounds.contains ( point ) ) {
				point.add ( direction.clone ( ).multiply ( 1.0F ) );
			}
			
			return point;
		} else {
			Ray    ray          = new Ray ( start_location , direction );
			Vector intersection = new Vector ( );
			
			if ( IntersectionUtil.intersectRayBounds ( ray , bounds.toBoundingBox ( ) , intersection ) ) {
				Vector point = intersection.clone ( );
				
				while ( bounds.contains ( point ) ) {
					point.add ( direction.clone ( ).multiply ( 1.0F ) );
				}
				
				return point;
			} else {
				// bus will never be in the battlefield
				return null;
			}
		}
	}
}
