package es.outlook.adriansrj.battleroyale.util.math;

import es.outlook.adriansrj.core.util.math.Vector2D;
import es.outlook.adriansrj.core.util.math.Vector2I;
import es.outlook.adriansrj.core.util.math.Vector3D;
import es.outlook.adriansrj.core.util.math.Vector3I;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import es.outlook.adriansrj.core.util.math.collision.Ray;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.awt.geom.Rectangle2D;
import java.util.Objects;

/**
 * Represents the boundaries of a two-dimensional square zone/area.
 *
 * @author AdrianSR / 01/09/2021 / 10:36 a. m.
 */
public class ZoneBounds implements Cloneable {
	
	public static final ZoneBounds ZERO = new ZoneBounds (
			new Location2I ( 0 , 0 ) , new Location2I ( 0 , 0 ) );
	
	protected final int                size;
	protected final Location2I         minimum;
	protected final Location2I         maximum;
	protected final Location2I         center;
	protected final Rectangle2D.Double bounds;
	
	public ZoneBounds ( Location2I minimum , Location2I maximum ) {
		int min_x     = Math.min ( minimum.getX ( ) , maximum.getX ( ) );
		int max_x     = Math.max ( minimum.getX ( ) , maximum.getX ( ) );
		int min_z     = Math.min ( minimum.getZ ( ) , maximum.getZ ( ) );
		int max_z     = Math.max ( minimum.getZ ( ) , maximum.getZ ( ) );
		int size_half = ( size = Math.max ( ( max_x - min_x ) + 1 , ( max_z - min_z ) + 1 ) ) >> 1;
		
		this.center  = new Location2I ( ( min_x + max_x ) >> 1 , ( min_z + max_z ) >> 1 );
		this.minimum = new Location2I ( center.getX ( ) - size_half , center.getZ ( ) - size_half );
		this.maximum = new Location2I ( center.getX ( ) + size_half , center.getZ ( ) + size_half );
		this.bounds  = new Rectangle2D.Double ( minimum.getX ( ) , minimum.getZ ( ) , size , size );
	}
	
	public ZoneBounds ( int minimum_x , int minimum_z , int maximum_x , int maximum_z ) {
		this ( new Location2I ( minimum_x , minimum_z ) , new Location2I ( maximum_x , maximum_z ) );
	}
	
	public ZoneBounds ( Vector2I minimum , Vector2I maximum ) {
		this ( new Location2I ( minimum ) , new Location2I ( maximum ) );
	}
	
	public ZoneBounds ( Location2I center , int size ) {
		this ( center.getX ( ) - ( size >> 1 ) ,
			   center.getZ ( ) - ( size >> 1 ) ,
			   center.getX ( ) + ( size >> 1 ) ,
			   center.getZ ( ) + ( size >> 1 ) );
	}
	
	public ZoneBounds ( BoundingBox bounding_box ) {
		this ( new Location2I ( bounding_box.getMinimum ( ).getBlockX ( ) ,
								bounding_box.getMinimum ( ).getBlockZ ( ) ) ,
			   new Location2I ( bounding_box.getMaximum ( ).getBlockX ( ) ,
								bounding_box.getMaximum ( ).getBlockZ ( ) ) );
	}
	
	/**
	 * Gets the size of this zone/area.
	 *
	 * @return the size of this zone/area.
	 */
	public int getSize ( ) {
		return size;
	}
	
	/**
	 * Gets the minimum corner of this zone/area.
	 *
	 * @return the minimum corner of this zone/area.
	 */
	public Location2I getMinimum ( ) {
		return minimum;
	}
	
	/**
	 * Gets the maximum corner of this zone/area.
	 *
	 * @return the maximum corner of this zone/area.
	 */
	public Location2I getMaximum ( ) {
		return maximum;
	}
	
	/**
	 * Gets the center of this zone/area.
	 *
	 * @return the center of this zone/area.
	 */
	public Location2I getCenter ( ) {
		return center;
	}
	
	public Rectangle2D.Double getRectangleBounds ( ) {
		// it seems that bounds is not immutable
		return ( Rectangle2D.Double ) bounds.clone ( );
	}
	
	public BoundingBox toBoundingBox ( ) {
		return new BoundingBox ( minimum.getX ( ) , 0.0D , minimum.getZ ( ) ,
								 maximum.getX ( ) , 255.0D , maximum.getZ ( ) );
	}
	
	public Location2I getCorner00 ( ) {
		return new Location2I ( minimum.getX ( ) , minimum.getZ ( ) );
	}
	
	public Location2I getCorner01 ( ) {
		return new Location2I ( minimum.getX ( ) , maximum.getZ ( ) );
	}
	
	public Location2I getCorner10 ( ) {
		return new Location2I ( maximum.getX ( ) , minimum.getZ ( ) );
	}
	
	public Location2I getCorner11 ( ) {
		return new Location2I ( maximum.getX ( ) , maximum.getZ ( ) );
	}
	
	public Location2I[] getCorners ( ) {
		return new Location2I[] {
				getCorner00 ( ) ,
				getCorner01 ( ) ,
				getCorner10 ( ) ,
				getCorner11 ( ) ,
		};
	}
	
	public boolean contains ( int x , int z ) {
		return ( minimum.getX ( ) <= x && maximum.getX ( ) >= x &&
				minimum.getZ ( ) <= z && maximum.getZ ( ) >= z ) || bounds.contains ( x , z );
	}
	
	public boolean contains ( double x , double z ) {
		return contains ( ( int ) x , ( int ) z );
	}
	
	public boolean contains ( Location2I point ) {
		return contains ( point.getX ( ) , point.getZ ( ) );
	}
	
	public boolean contains ( Vector2I point ) {
		return contains ( point.getX ( ) , point.getY ( ) );
	}
	
	public boolean contains ( Location point ) {
		return contains ( point.getX ( ) , point.getZ ( ) );
	}
	
	public boolean contains ( Vector point ) {
		return contains ( point.getX ( ) , point.getZ ( ) );
	}
	
	public boolean intersects ( double direction_x , double direction_z , double origin_x , double origin_z ) {
		return bounds.intersectsLine (
				direction_x + origin_x , direction_z + origin_z ,
				( direction_x * 10000 ) + origin_x , ( direction_z * 10000 ) + origin_z );
	}
	
	public boolean intersects ( Location2I direction , Location2I origin ) {
		return intersects ( direction.getX ( ) , direction.getZ ( ) , origin.getX ( ) , origin.getZ ( ) );
	}
	
	public boolean intersects ( Vector2I direction , Vector2I origin ) {
		return intersects ( direction.getX ( ) , direction.getY ( ) , origin.getX ( ) , origin.getY ( ) );
	}
	
	public boolean intersects ( Vector3I direction , Vector3I origin ) {
		return intersects ( direction.getX ( ) , direction.getZ ( ) , origin.getX ( ) , origin.getZ ( ) );
	}
	
	public boolean intersects ( Vector3D direction , Vector3D origin ) {
		return intersects ( direction.getX ( ) , direction.getZ ( ) , origin.getX ( ) , origin.getZ ( ) );
	}
	
	public boolean intersects ( Vector direction , Vector origin ) {
		return intersects ( direction.getX ( ) , direction.getZ ( ) , origin.getX ( ) , origin.getZ ( ) );
	}
	
	public boolean intersects ( Ray ray ) {
		return intersects ( ray.getDirection ( ) , ray.getOrigin ( ) );
	}
	
	public boolean intersects ( Location direction , Location origin ) {
		return intersects ( direction.getX ( ) , direction.getZ ( ) , origin.getX ( ) , origin.getZ ( ) );
	}
	
	/**
	 * Projects/relocates the provided relocatable-location within these bounds.
	 *
	 * @param location the location to project/relocate.
	 * @return a new {@link Location2I} with the result.
	 */
	public Location2I project ( Location2I location ) {
		return new Location2I ( location.getX ( ) + minimum.getX ( ) ,
								location.getZ ( ) + minimum.getZ ( ) );
	}
	
	/**
	 * Projects/relocates the provided relocatable-vector within these bounds.
	 *
	 * @param vector the vector to project/relocate.
	 * @return a new {@link Vector2D} with the result.
	 */
	public Vector2D project ( Vector2D vector ) {
		return vector.add ( minimum.getX ( ) , minimum.getZ ( ) );
	}
	
	/**
	 * Projects/relocates the provided relocatable-vector within these bounds.
	 *
	 * @param vector the vector to project/relocate.
	 * @return a new {@link Vector2I} with the result.
	 */
	public Vector2I project ( Vector2I vector ) {
		return vector.add ( minimum.getX ( ) , minimum.getZ ( ) );
	}
	
	/**
	 * Projects/relocates the provided relocatable-vector within these bounds.
	 *
	 * @param vector the vector to project/relocate.
	 * @return a new {@link Vector3D} with the result.
	 */
	public Vector3D project ( Vector3D vector ) {
		return vector.add ( minimum.getX ( ) , 0.0D , minimum.getZ ( ) );
	}
	
	/**
	 * Projects/relocates the provided relocatable-vector within these bounds.
	 *
	 * @param vector the vector to project/relocate.
	 * @return a new {@link Vector} with the result.
	 */
	public Vector project ( Vector vector ) {
		return vector.clone ( ).add ( new Vector ( minimum.getX ( ) , 0.0D , minimum.getZ ( ) ) );
	}
	
	/**
	 * Make relocatable/unproject the provided location that is within these bounds.
	 *
	 * @param location the location to make relocatable
	 * @return a new {@link Location2I} with the result.
	 */
	public Location2I unproject ( Location2I location ) {
		return new Location2I ( location.getX ( ) - minimum.getX ( ) ,
								location.getZ ( ) - minimum.getZ ( ) );
	}
	
	/**
	 * Make relocatable/unproject the provided vector that is within these bounds.
	 *
	 * @param vector the location to make relocatable
	 * @return a new {@link Vector2D} with the result.
	 */
	public Vector2D unproject ( Vector2D vector ) {
		return vector.subtract ( minimum.getX ( ) , minimum.getZ ( ) );
	}
	
	/**
	 * Make relocatable/unproject the provided vector that is within these bounds.
	 *
	 * @param vector the location to make relocatable
	 * @return a new {@link Vector2I} with the result.
	 */
	public Vector2I unproject ( Vector2I vector ) {
		return vector.subtract ( minimum.getX ( ) , minimum.getZ ( ) );
	}
	
	/**
	 * Make relocatable/unproject the provided vector that is within these bounds.
	 *
	 * @param vector the location to make relocatable
	 * @return a new {@link Vector3D} with the result.
	 */
	public Vector3D unproject ( Vector3D vector ) {
		return vector.subtract ( minimum.getX ( ) , 0.0D , minimum.getZ ( ) );
	}
	
	/**
	 * Make relocatable/unproject the provided vector that is within these bounds.
	 *
	 * @param vector the location to make relocatable
	 * @return a new {@link Vector} with the result.
	 */
	public Vector unproject ( Vector vector ) {
		return vector.clone ( ).subtract ( new Vector ( minimum.getX ( ) , 0.0D , minimum.getZ ( ) ) );
	}
	
	/**
	 * Relocates these bounds to the provided location.
	 *
	 * @param location the location to relocate these bounds.
	 * @return a new {@link ZoneBounds} with the result.
	 */
	public ZoneBounds relocate ( Location2I location ) {
		int size_half = size >> 1;
		
		return new ZoneBounds (
				new Location2I ( location.getX ( ) - size_half , location.getZ ( ) - size_half ) ,
				new Location2I ( location.getX ( ) + size_half , location.getZ ( ) + size_half ) );
	}
	
	/**
	 * Relocates these bounds to the provided location.
	 *
	 * @param vector the location to relocate these bounds.
	 * @return a new {@link ZoneBounds} with the result.
	 */
	public ZoneBounds relocate ( Vector2D vector ) {
		int size_half = size >> 1;
		
		return new ZoneBounds (
				new Location2I ( ( int ) vector.getX ( ) - size_half ,
								 ( int ) vector.getY ( ) - size_half ) ,
				new Location2I ( ( int ) vector.getX ( ) + size_half ,
								 ( int ) vector.getY ( ) + size_half ) );
	}
	
	/**
	 * Relocates these bounds to the provided location.
	 *
	 * @param vector the location to relocate these bounds.
	 * @return a new {@link ZoneBounds} with the result.
	 */
	public ZoneBounds relocate ( Vector2I vector ) {
		int size_half = size >> 1;
		
		return new ZoneBounds (
				new Location2I ( ( int ) vector.getX ( ) - size_half ,
								 ( int ) vector.getY ( ) - size_half ) ,
				new Location2I ( ( int ) vector.getX ( ) + size_half ,
								 ( int ) vector.getY ( ) + size_half ) );
	}
	
	/**
	 * Relocates these bounds to the provided location.
	 *
	 * @param vector the location to relocate these bounds.
	 * @return a new {@link ZoneBounds} with the result.
	 */
	public ZoneBounds relocate ( Vector3D vector ) {
		int size_half = size >> 1;
		
		return new ZoneBounds (
				new Location2I ( ( int ) vector.getX ( ) - size_half ,
								 ( int ) vector.getZ ( ) - size_half ) ,
				new Location2I ( ( int ) vector.getX ( ) + size_half ,
								 ( int ) vector.getZ ( ) + size_half ) );
	}
	
	/**
	 * Relocates these bounds to the provided location.
	 *
	 * @param vector the location to relocate these bounds.
	 * @return a new {@link ZoneBounds} with the result.
	 */
	public ZoneBounds relocate ( Vector vector ) {
		int size_half = size >> 1;
		
		return new ZoneBounds (
				new Location2I ( ( int ) vector.getX ( ) - size_half ,
								 ( int ) vector.getZ ( ) - size_half ) ,
				new Location2I ( ( int ) vector.getX ( ) + size_half ,
								 ( int ) vector.getZ ( ) + size_half ) );
	}
	
	@Override
	public String toString ( ) {
		return "ZoneBounds{" +
				"size=" + size +
				", minimum=" + minimum +
				", maximum=" + maximum +
				", center=" + center +
				", bounds=" + bounds +
				'}';
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass ( ) != o.getClass ( ) ) {
			return false;
		}
		ZoneBounds that = ( ZoneBounds ) o;
		return minimum.equals ( that.minimum ) && maximum.equals ( that.maximum );
	}
	
	@Override
	public int hashCode ( ) {
		return Objects.hash ( minimum , maximum );
	}
	
	@Override
	public ZoneBounds clone ( ) {
		try {
			return ( ZoneBounds ) super.clone ( );
		} catch ( CloneNotSupportedException ex ) {
			throw new Error ( ex );
		}
	}
}