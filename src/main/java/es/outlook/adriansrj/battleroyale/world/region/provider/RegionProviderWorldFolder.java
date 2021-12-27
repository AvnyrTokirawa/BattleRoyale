package es.outlook.adriansrj.battleroyale.world.region.provider;

import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.world.region.Region;
import es.outlook.adriansrj.core.util.world.WorldUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author AdrianSR / 31/08/2021 / 02:59 p. m.
 */
public class RegionProviderWorldFolder implements RegionProvider {
	
	protected final Map < Location2I, Region > region_map = new HashMap <> ( );
	protected final File                       world_folder;
	protected final File                       region_folder;
	
	public RegionProviderWorldFolder ( File world_folder ) {
		this.world_folder  = world_folder;
		this.region_folder = new File ( world_folder , WorldUtil.REGION_FOLDER_NAME );
	}
	
	public File getWorldFolder ( ) {
		return world_folder;
	}
	
	public File getRegionFolder ( ) {
		return region_folder;
	}
	
	/**
	 * Gets the region at the specified region coordinates.
	 *
	 * @param x region x coordinate.
	 * @param z region z coordinate.
	 *
	 * @return null if the region doesn't exist in the world folder.
	 */
	@Override
	public Region getRegion ( int x , int z ) throws IOException, IllegalArgumentException {
		Location2I location = new Location2I ( x , z );
		Region     region   = region_map.get ( location );
		
		if ( region == null && region_folder.exists ( ) ) {
			File file = new File (
					region_folder , String.format ( Region.REGION_FILE_NAME_FORMAT ,
													location.getX ( ) , location.getZ ( ) ) );
			
			if ( file.exists ( ) ) {
				region = Region.newRegion ( location , file );
				region_map.put ( location , region );
			}
		}
		
		return region;
	}
	
	@Override
	public Region getRegionAtChunkCoordinates ( int x , int z ) throws IOException, IllegalArgumentException {
		return getRegion ( x >> 5 , z >> 5 );
	}
	
	@Override
	public Region getRegionAtBlockCoordinates ( int x , int z ) throws IOException, IllegalArgumentException {
		return getRegionAtChunkCoordinates ( x >> 4 , z >> 4 );
	}
}