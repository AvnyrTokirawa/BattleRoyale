package es.outlook.adriansrj.battleroyale.compass;

import es.outlook.adriansrj.battleroyale.enums.EnumCompassConfiguration;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import org.bukkit.block.BlockFace;

import java.util.Arrays;

/**
 * Compass cardinal points.
 *
 * @author AdrianSR / 09/09/2021 / 02:43 p. m.
 */
public enum CompassCardinalPoint {
	
	NORTH ( 180 , "N" , EnumCompassConfiguration.NORTH_WORD ),
	EAST ( 270 , "E" , EnumCompassConfiguration.EAST_WORD ),
	SOUTH ( 0 , "S" , EnumCompassConfiguration.SOUTH_WORD ),
	WEST ( 90 , "W" , EnumCompassConfiguration.WEST_WORD ),
	
	NORTH_EAST ( 225 , "NE" , EnumCompassConfiguration.NORTH_EAST_WORD ),
	NORTH_WEST ( 135 , "NW" , EnumCompassConfiguration.NORTH_WEST_WORD ),
	SOUTH_EAST ( 315 , "SE" , EnumCompassConfiguration.SOUTH_EAST_WORD ),
	SOUTH_WEST ( 45 , "SW" , EnumCompassConfiguration.SOUTH_WEST_WORD );
	
	public static CompassCardinalPoint of ( float yaw ) {
		return CompassCardinalPoint.valueOf (
				DirectionUtil.getBlockFace45 ( DirectionUtil.normalize ( yaw ) ).name ( ) );
	}
	
	public static CompassCardinalPoint ofValue ( int value ) {
		return Arrays.stream ( values ( ) ).filter ( point -> point.getValue ( ) == value )
				.findAny ( ).orElse ( null );
	}
	
	private final int                      value;
	private final String                   default_abbreviation;
	private final EnumCompassConfiguration abbreviation;
	
	CompassCardinalPoint ( int value , String default_abbreviation , EnumCompassConfiguration abbreviation ) {
		this.value                = value;
		this.default_abbreviation = default_abbreviation;
		this.abbreviation         = abbreviation;
	}
	
	public int getValue ( ) {
		return value;
	}
	
	public String getDefaultAbbreviation ( ) {
		return default_abbreviation;
	}
	
	public EnumCompassConfiguration getAbbreviation ( ) {
		return abbreviation;
	}
	
	public boolean is90 ( ) {
		switch ( this ) {
			case EAST:
			case NORTH:
			case WEST:
			case SOUTH:
				return true;
			
			default:
				return false;
		}
	}
	
	public boolean is45 ( ) {
		return !is90 ( );
	}
	
	/**
	 * Gets the equivalent {@link BlockFace}.
	 *
	 * @return the equivalent {@link BlockFace}.
	 */
	public BlockFace getBlockFace ( ) {
		return BlockFace.valueOf ( name ( ) );
	}
}
