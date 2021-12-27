package es.outlook.adriansrj.battleroyale.world.region.provider;

import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.world.region.Region;

import java.io.IOException;

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
	 * @throws IOException if there was a problem loading the region.
	 * @throws IllegalArgumentException if there was a problem processing the region.
	 */
	Region getRegion ( int x , int z ) throws IOException, IllegalArgumentException;
	
	/**
	 * Gets the region at the specified location.
	 *
	 * @param location the region location.
	 * @return the region at the specified location.
	 */
	default Region getRegion ( Location2I location ) throws IOException, IllegalArgumentException {
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
	Region getRegionAtChunkCoordinates ( int x , int z ) throws IOException, IllegalArgumentException;
	
	/**
	 * Gets the region at the specified block coordinates.
	 *
	 * @param x world x coordinate.
	 * @param z world z coordinate.
	 *
	 * @return the region at the specified block coordinates.
	 */
	Region getRegionAtBlockCoordinates ( int x , int z ) throws IOException, IllegalArgumentException;
}