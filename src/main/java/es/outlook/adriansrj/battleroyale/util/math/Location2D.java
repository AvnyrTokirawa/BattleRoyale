package es.outlook.adriansrj.battleroyale.util.math;

import es.outlook.adriansrj.core.util.math.Vector2D;
import es.outlook.adriansrj.core.util.math.Vector2I;

import java.util.Objects;

/**
 * Represents a simple Double two-dimensional immutable location. Unlike the typical vectors, this location will not
 * name the secondary axis as <b>"y"</b>, but instead <b>"z"</b>.
 *
 * @author AdrianSR / 24/08/2021 / Time: 07:26 p. m.
 */
public class Location2D {
	
	protected final double x;
	protected final double z;
	protected final int    hash;
	
	public Location2D ( double x , double z ) {
		this.x = x;
		this.z = z;
		
		// we're hashing in construction and caching it to make process faster.
		this.hash = Objects.hash ( x , z );
	}
	
	public Location2D ( Vector2I vector ) {
		this ( vector.getX ( ) , vector.getY ( ) );
	}
	
	public Location2D ( Vector2D vector ) {
		this ( ( int ) vector.getX ( ) , ( int ) vector.getY ( ) );
	}
	
	public double getX ( ) {
		return x;
	}
	
	public double getZ ( ) {
		return z;
	}
	
	public Vector2I toVector2I ( ) {
		return new Vector2I ( x , z );
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
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		Location2D that = ( Location2D ) o;
		return Double.compare ( that.x , x ) == 0 && Double.compare ( that.z , z ) == 0;
	}
}
