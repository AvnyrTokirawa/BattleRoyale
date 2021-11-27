package es.outlook.adriansrj.battleroyale.parachute.custom;

import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created for locating the parts of a parachute, represents an immutable cached
 * 3-dimensional position, with a facing direction.
 * <p>
 * The values of {@link #length()}, {@link #lengthSquared()} and
 * {@link #hashCode()} are cached for a better performance.
 * <p>
 * <ul>
 * <li>The X representing LEFT.
 * <li>The Y representing UP.
 * <li>The Z representing FORWARD.
 * <li>The Pitch represents rotation around the axis X.
 * <li>The Yaw represents rotation around the axis Y.
 * <li>The Roll represents rotation around the axis Z.
 * </ul>
 * <p>
 * @author AdrianSR / Wednesday 26 February, 2020 / 11:22 AM
 */
public class ParachuteCustomPartPosition implements Configurable, ConfigurationSerializable, Cloneable {
	
	public static final ParachuteCustomPartPosition ZERO = new ParachuteCustomPartPosition ( 0F , 0F , 0F );
	
	public static ParachuteCustomPartPosition of ( ConfigurationSection section ) {
		return new ParachuteCustomPartPosition ( ).load ( section );
	}
	
	/**
	 * Random for generating random positions.
	 */
	protected static final Random RANDOM = new Random ( );
	
	/* left/up/forward */
	@ConfigurableEntry ( key = Constants.X_KEY )
	protected float x;
	@ConfigurableEntry ( key = Constants.Y_KEY )
	protected float y;
	@ConfigurableEntry ( key = Constants.Z_KEY )
	protected float z;
	
	/* rotation */
	@ConfigurableEntry ( key = Constants.PITCH_KEY )
	protected float pitch;
	@ConfigurableEntry ( key = Constants.YAW_KEY )
	protected float yaw;
	@ConfigurableEntry ( key = Constants.ROLL_KEY )
	protected float roll;
	
	/* cached values */
	protected float length_squared = Float.NaN;
	protected float length         = Float.NaN;
	
	protected int     hashcode;
	protected boolean hashed;
	
	protected int     x_bits;
	protected int     y_bits;
	protected int     z_bits;
	protected int     pitch_bits;
	protected int     yaw_bits;
	protected int     roll_bits;
	protected boolean bitset;
	
	/**
	 * Construct the position with provided float components, and with provided
	 * facing directions.
	 * <p>
	 * @param x     X component ( Left ).
	 * @param y     Y component ( Up ).
	 * @param z     Z component ( Forward ).
	 * @param yaw   the Yaw, that is the rotation around the axis Y.
	 * @param pitch the Pitch, that is the rotation around the axis X.
	 * @param roll  the Roll, that is the rotation around the axis Z.
	 */
	public ParachuteCustomPartPosition ( float x , float y , float z , float yaw , float pitch , float roll ) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.yaw   = yaw;
		this.pitch = pitch;
		this.roll  = roll;
	}
	
	/**
	 * Construct the position with provided float components, with facing directions as 0.
	 * <p>
	 * @param x X component ( Left ).
	 * @param y Y component ( Up ).
	 * @param z Z component ( Forward ).
	 */
	public ParachuteCustomPartPosition ( float x , float y , float z ) {
		this ( x , y , z , 0F , 0F , 0F );
	}
	
	/**
	 * Construct the position with provided integer components, and with provided facing directions.
	 * <p>
	 * @param x X component ( Left ).
	 * @param y Y component ( Up ).
	 * @param z Z component ( Forward ).
	 * @param yaw the Pitch, that is the rotation around the axis X.
	 * @param pitch the Yaw, that is the rotation around the axis Y.
	 * @param roll the Roll, that is the rotation around the axis Z.
	 */
	public ParachuteCustomPartPosition ( int x , int y , int z , float yaw , float pitch , float roll ) {
		this ( ( float ) x , ( float ) y , ( float ) z , yaw , pitch , roll );
	}
	
	/**
	 * Construct the position with provided integer components, with facing directions as 0.
	 * <p>
	 * @param x X component ( Left ).
	 * @param y Y component ( Up ).
	 * @param z Z component ( Forward ).
	 */
	public ParachuteCustomPartPosition ( int x , int y , int z ) {
		this ( x , y , z , 0F , 0F , 0F );
	}
	
	/**
	 * Construct the position with provided double components, and with provided facing directions.
	 * <p>
	 * @param x X component ( Left ).
	 * @param y Y component ( Up ).
	 * @param z Z component ( Forward ).
	 * @param yaw the Pitch, that is the rotation around the axis X.
	 * @param pitch the Yaw, that is the rotation around the axis Y.
	 * @param roll the Roll, that is the rotation around the axis Z.
	 */
	public ParachuteCustomPartPosition ( double x , double y , double z , float yaw , float pitch , float roll ) {
		this ( ( float ) x , ( float ) y , ( float ) z , yaw , pitch , roll );
	}
	
	/**
	 * Construct the position with provided double components, with facing directions as 0.
	 * <p>
	 * @param x X component ( Left ).
	 * @param y Y component ( Up ).
	 * @param z Z component ( Forward ).
	 */
	public ParachuteCustomPartPosition ( double x , double y , double z ) {
		this ( x , y , z , 0F , 0F , 0F );
	}
	
	/**
	 * Constructs an uninitialized position for initializing it via the
	 * de-serialization.
	 */
	public ParachuteCustomPartPosition ( ) {
	}
	
	/**
	 * Gets the x (Left) component.
	 * <p>
	 * @return the x component.
	 */
	public float getX ( ) {
		return x;
	}
	
	/**
	 * Gets the floored value of the X component, indicating the block that
	 * this position is contained with.
	 * <p>
	 * @return the block X.
	 */
	public int getBlockX ( ) {
		return NumberConversions.floor ( x );
	}
	
	/**
	 * Gets the y (Up) component.
	 * <p>
	 * @return the y component.
	 */
	public float getY ( ) {
		return y;
	}
	
	/**
	 * Gets the floored value of the Y component, indicating the block that
	 * this position is contained with.
	 * <p>
	 * @return the block Y.
	 */
	public int getBlockY ( ) {
		return NumberConversions.floor ( y );
	}
	
	/**
	 * Gets the z (Forward) component.
	 * <p>
	 * @return the z component.
	 */
	public float getZ ( ) {
		return z;
	}
	
	/**
	 * Gets the floored value of the Z component, indicating the block that
	 * this position is contained with.
	 * <p>
	 * @return the block Z.
	 */
	public int getBlockZ ( ) {
		return NumberConversions.floor ( z );
	}
	
	/**
	 * Gets the Yaw euler angle, which represents the rotation around the axis Y.
	 * <p>
	 * @return the rotation measured in degrees.
	 */
	public float getYaw ( ) {
		return yaw;
	}
	
	/**
	 * Gets the Pitch euler angle, which represents the rotation around the axis X.
	 * <p>
	 * @return the rotation measured in degrees.
	 */
	public float getPitch ( ) {
		return pitch;
	}
	
	/**
	 * Gets the Pitch euler angle, which represents the rotation around the axis Z.
	 * <p>
	 * @return the rotation measured in degrees.
	 */
	public float getRoll ( ) {
		return roll;
	}
	
	/**
	 * Adds a position to this one.
	 * <p>
	 * @param other the other position.
	 * @return a new position containing the addition result.
	 */
	public ParachuteCustomPartPosition add ( final ParachuteCustomPartPosition other ) {
		return new ParachuteCustomPartPosition ( ( x + other.getX ( ) ) , ( y + other.getY ( ) ) , ( z + other.getZ ( ) ) ,
												 yaw , pitch , roll );
	}
	
	/**
	 * Subtracts a position from this one.
	 * <p>
	 * @param other the other position.
	 * @return a new position containing the subtraction result.
	 */
	public ParachuteCustomPartPosition subtract ( final ParachuteCustomPartPosition other ) {
		return new ParachuteCustomPartPosition ( ( x - other.getX ( ) ) , ( y - other.getY ( ) ) , ( z - other.getZ ( ) ) ,
												 yaw , pitch , roll );
	}
	
	/**
	 * Multiplies the position by another.
	 * <p>
	 * @param other the other position.
	 * @return a new position containing the multiplication result.
	 */
	public ParachuteCustomPartPosition multiply ( final ParachuteCustomPartPosition other ) {
		return new ParachuteCustomPartPosition ( ( x * other.getX ( ) ) , ( y * other.getY ( ) ) , ( z * other.getZ ( ) ) ,
												 yaw , pitch , roll );
	}
	
	/**
	 * Divides the position by another.
	 * <p>
	 * @param other the other position.
	 * @return a new position containing the division result.
	 */
	public ParachuteCustomPartPosition divide ( final ParachuteCustomPartPosition other ) {
		return new ParachuteCustomPartPosition ( ( x / other.getX ( ) ) , ( y / other.getY ( ) ) , ( z / other.getZ ( ) ) ,
												 yaw , pitch , roll );
	}
	
	/**
	 * Gets the magnitude of the position, defined as sqrt(x^2 + y^2 + z^2). The
	 * value of this method is cached. NaN will be returned if the inner result of
	 * the sqrt() function overflows, which will be caused if the length is too
	 * long.
	 * <p>
	 * @return the magnitude.
	 */
	public float length ( ) {
		if ( Float.isNaN ( length ) ) {
			this.length = ( float ) Math.sqrt ( lengthSquared ( ) );
		}
		return this.length;
	}
	
	/**
	 * Gets the magnitude of the position squared.
	 * <p>
	 * @return the magnitude.
	 */
	public float lengthSquared ( ) {
		if ( Float.isNaN ( length_squared ) ) {
			this.length_squared = ( x * x ) + ( y * y ) + ( z * z );
		}
		return this.length_squared;
	}
	
	/**
	 * Get the distance between this position and another. The value of this
	 * method is not cached and uses a costly square-root function, so do not
	 * repeatedly call this method to get the position's magnitude. NaN will be
	 * returned if the inner result of the sqrt() function overflows, which
	 * will be caused if the distance is too long.
	 * <p>
	 * @param other the other position.
	 * @return the distance.
	 */
	public double distance ( ParachuteCustomPartPosition other ) {
		return Math.sqrt (
				NumberConversions.square ( x - other.x )
						+ NumberConversions.square ( y - other.y )
						+ NumberConversions.square ( z - other.z ) );
	}
	
	/**
	 * Get the squared distance between this position and another.
	 * <p>
	 * @param other the other position.
	 * @return the distance.
	 */
	public double distanceSquared ( ParachuteCustomPartPosition other ) {
		return ( NumberConversions.square ( x - other.x ) + NumberConversions.square (
				y - other.y ) + NumberConversions.square ( z - other.z ) );
	}
	
	/**
	 * Performs scalar multiplication, multiplying all components with a
	 * scalar.
	 * <p>
	 * @param factor the factor.
	 * @return a new position containing the result.
	 */
	public ParachuteCustomPartPosition multiply ( int factor ) {
		return new ParachuteCustomPartPosition ( ( x * factor ) , ( y * factor ) , ( z * factor ) , yaw , pitch , roll );
	}
	
	/**
	 * Performs scalar multiplication, multiplying all components with a
	 * scalar.
	 * <p>
	 * @param factor the factor.
	 * @return a new position containing the result.
	 */
	public ParachuteCustomPartPosition multiply ( double factor ) {
		return new ParachuteCustomPartPosition ( ( x * factor ) , ( y * factor ) , ( z * factor ) , yaw , pitch , roll );
	}
	
	/**
	 * Performs scalar multiplication, multiplying all components with a
	 * scalar.
	 * <p>
	 * @param factor the factor.
	 * @return a new position containing the result.
	 */
	public ParachuteCustomPartPosition multiply ( float factor ) {
		return new ParachuteCustomPartPosition ( ( x * factor ) , ( y * factor ) , ( z * factor ) , yaw , pitch , roll );
	}
	
	@Override
	public boolean equals ( final Object obj ) {
		if ( obj == this ) {
			return true;
		} else {
			if ( obj instanceof ParachuteCustomPartPosition ) {
				ParachuteCustomPartPosition other = ( ParachuteCustomPartPosition ) obj;
				this.bitset ( ); other.bitset ( );
				return this.x_bits == other.x_bits
						&& this.y_bits == other.y_bits
						&& this.z_bits == other.z_bits
						&& this.yaw_bits == other.yaw_bits
						&& this.pitch_bits == other.pitch_bits
						&& this.roll_bits == other.roll_bits;
			} else {
				return false;
			}
		}
	}
	
	protected void bitset ( ) {
		if ( !bitset ) {
			this.x_bits     = Float.floatToIntBits ( this.x );
			this.y_bits     = Float.floatToIntBits ( this.y );
			this.z_bits     = Float.floatToIntBits ( this.z );
			this.yaw_bits   = Float.floatToIntBits ( this.yaw );
			this.pitch_bits = Float.floatToIntBits ( this.pitch );
			this.roll_bits  = Float.floatToIntBits ( this.roll );
			
			this.bitset = true;
		}
	}
	
	/**
	 * Returns the hash code of this position.
	 * <p>
	 * @return the hash code.
	 */
	@Override
	public int hashCode ( ) {
		if ( !hashed ) {
			this.hashcode = 3;
			this.hashcode = 19 * hashcode + ( int ) Float.floatToIntBits ( this.x );
			this.hashcode = 19 * hashcode + ( int ) Float.floatToIntBits ( this.y );
			this.hashcode = 19 * hashcode + ( int ) Float.floatToIntBits ( this.z );
			this.hashcode = 19 * hashcode + ( int ) Float.floatToIntBits ( this.yaw );
			this.hashcode = 19 * hashcode + ( int ) Float.floatToIntBits ( this.pitch );
			this.hashcode = 19 * hashcode + ( int ) Float.floatToIntBits ( this.roll );
			this.hashed   = true;
		}
		return hashcode;
	}
	
	@Override
	public String toString ( ) {
		return ( x + ", " + y + ", " + z + ", " + yaw + ", " + pitch + ", " + roll );
	}
	
	/**
	 * Gets a Location version of this position, with the horizontal rotation as yaw
	 * and the vertical rotation as pitch.
	 * <p>
	 * @param world the world to link the location to.
	 * @return the location result.
	 */
	public Location toLocation ( World world ) {
		return new Location ( world , x , y , z , yaw , pitch );
	}
	
	/**
	 * Gets a Location version of this position, with the provided yaw and pitch.
	 * <p>
	 * @param world the world to link the location to.
	 * @param yaw the desired yaw.
	 * @param pitch the desired pitch.
	 * @return the location result.
	 */
	public Location toLocation ( World world , float yaw , float pitch ) {
		return new Location ( world , x , y , z , yaw , pitch );
	}
	
	/**
	 * Gets the minimum components of two positions.
	 * <p>
	 * @param p1 the first position.
	 * @param p2 the second position.
	 * @return minimum.
	 */
	public static ParachuteCustomPartPosition getMinimum ( ParachuteCustomPartPosition p1 , ParachuteCustomPartPosition p2 ) {
		return new ParachuteCustomPartPosition ( Math.min ( p1.x , p2.x ) , Math.min ( p1.y , p2.y ) ,
												 Math.min ( p1.z , p2.z ) );
	}
	
	/**
	 * Gets the maximum components of two positions.
	 * <p>
	 * @param p1 the first position.
	 * @param p2 the second position.
	 * @return maximum.
	 */
	public static ParachuteCustomPartPosition getMaximum ( ParachuteCustomPartPosition p1 , ParachuteCustomPartPosition p2 ) {
		return new ParachuteCustomPartPosition ( Math.max ( p1.x , p2.x ) , Math.max ( p1.y , p2.y ) ,
												 Math.max ( p1.z , p2.z ) );
	}
	
	@Override
	public Map < String, Object > serialize ( ) {
		final Map < String, Object > result = new LinkedHashMap < String, Object > ( );
		
		result.put ( "x" , getX ( ) );
		result.put ( "y" , getY ( ) );
		result.put ( "z" , getZ ( ) );
		result.put ( "yaw" , getYaw ( ) );
		result.put ( "pitch" , getPitch ( ) );
		result.put ( "roll" , getRoll ( ) );
		return result;
	}
	
	public static ParachuteCustomPartPosition deserialize ( Map < String, Object > args ) {
		double x     = 0; // left
		double y     = 0; // up
		double z     = 0; // forward
		float  pitch = 0;
		float  yaw   = 0;
		float  roll  = 0;
		
		if ( args.containsKey ( "x" ) ) {
			x = ( Double ) args.get ( "x" );
		}
		
		if ( args.containsKey ( "y" ) ) {
			y = ( Double ) args.get ( "y" );
		}
		
		if ( args.containsKey ( "z" ) ) {
			z = ( Double ) args.get ( "z" );
		}
		
		if ( args.containsKey ( "yaw" ) ) {
			pitch = ( Float ) args.get ( "yaw" );
		}
		
		if ( args.containsKey ( "pitch" ) ) {
			pitch = ( Float ) args.get ( "pitch" );
		}
		
		if ( args.containsKey ( "roll" ) ) {
			pitch = ( Float ) args.get ( "roll" );
		}
		return new ParachuteCustomPartPosition ( x , y , z , pitch , yaw , roll );
	}
	
	@Override
	public ParachuteCustomPartPosition clone ( ) {
		try {
			return ( ParachuteCustomPartPosition ) super.clone ( );
		} catch ( CloneNotSupportedException ex ) {
			throw new Error ( ex );
		}
	}
	
	@Override
	public boolean isValid ( ) {
		return true;
	}
	
	@Override
	public ParachuteCustomPartPosition load ( ConfigurationSection section ) {
		loadEntries ( section );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return saveEntries ( section );
	}
}