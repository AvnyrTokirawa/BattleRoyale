package es.outlook.adriansrj.battleroyale.util.math;

import es.outlook.adriansrj.core.util.math.Vector2D;
import es.outlook.adriansrj.core.util.math.Vector2I;

/**
 * Represents a simple an integer two-dimensional immutable location. Unlike the typical vectors, this location will not
 * name the secondary axis as <b>"y"</b>, but instead <b>"z"</b>.
 *
 * @author AdrianSR / 24/08/2021 / Time: 07:26 p. m.
 */
public class Location2I {
	
	protected final int x;
	protected final int z;
	protected final int hash;
	
	public Location2I ( int x , int z ) {
		this.x = x;
		this.z = z;
		
		// we're hashing in construction and caching it to make process faster.
		this.hash = ( 1664525 * this.x + 1013904223 ) ^ ( 1664525 * ( this.z ^ -559038737 ) + 1013904223 );
	}
	
	public Location2I ( Vector2I vector ) {
		this ( vector.getX ( ) , vector.getY ( ) );
	}
	
	public Location2I ( Vector2D vector ) {
		this ( ( int ) vector.getX ( ) , ( int ) vector.getY ( ) );
	}
	
	public int getX ( ) {
		return x;
	}
	
	public int getZ ( ) {
		return z;
	}
	
	public Vector2I toVector2I ( ) {
		return new Vector2I ( x , z );
	}
	
	public int distance ( final Location2I other ) {
		return ( int ) Math.sqrt ( distanceSquared ( other ) );
	}
	
	public int distanceSquared ( Location2I other ) {
		final int x_d = ( this.x - other.x );
		final int y_d = ( this.z - other.z );
		
		return ( ( x_d * x_d ) + ( y_d * y_d ) );
	}
	
	@Override
	public String toString ( ) {
		return "[" + x + ", " + z + "]";
	}
	
	@Override
	public int hashCode ( ) {
		return hash;
	}
	
	@Override
	public boolean equals ( Object obj ) {
		if ( this == obj ) {
			return true;
		} else {
			if ( obj instanceof Location2I ) {
				Location2I other = ( Location2I ) obj;
				return other.x == this.x && other.z == this.z;
			} else {
				return false;
			}
		}
	}
}
