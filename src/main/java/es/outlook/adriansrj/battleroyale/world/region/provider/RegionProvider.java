package es.outlook.adriansrj.battleroyale.world.region.provider;

import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.world.region.Region;

/**
 * @author AdrianSR / 31/08/2021 / 02:59 p. m.
 */
public interface RegionProvider {
	
	/**
	 * Gets the region at the specified region coordinates.
	 *
	 * @param x region x coordinate.
	 * @param z region z coordinate.
	 *
	 * @return the region at the specified region coordinates.
	 */
	public Region getRegion ( int x , int z );
	
	/**
	 * Gets the region at the specified location.
	 *
	 * @param location the region location.
	 * @return the region at the specified location.
	 */
	default Region getRegion ( Location2I location ) {
		return getRegion ( location.getX ( ) , location.getZ ( ) );
	}
	
	/**
	 * Gets the region at the specified chunk coordinates.
	 *
	 * @param x chunk x coordinate.
	 * @param z chunk z coordinate.
	 *
	 * @return the region at the specified chunk coordinates.
	 */
	public Region getRegionAtChunkCoordinates ( int x , int z );
	
	/**
	 * Gets the region at the specified block coordinates.
	 *
	 * @param x world x coordinate.
	 * @param z world z coordinate.
	 *
	 * @return the region at the specified block coordinates.
	 */
	public Region getRegionAtBlockCoordinates ( int x , int z );
}