package es.outlook.adriansrj.battleroyale.world.chunk.provider;

import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.world.chunk.Chunk;
import es.outlook.adriansrj.battleroyale.world.region.Region;
import es.outlook.adriansrj.battleroyale.world.region.provider.RegionProviderWorldFolder;

import java.io.File;

/**
 * @author AdrianSR / 31/08/2021 / 02:56 p. m.
 */
public class ChunkProviderWorldFolder implements ChunkProvider {
	
	protected final RegionProviderWorldFolder region_provider;
	protected final File                      world_folder;
	
	public ChunkProviderWorldFolder ( File world_folder ) {
		this.world_folder    = world_folder;
		this.region_provider = new RegionProviderWorldFolder ( world_folder );
	}
	
	/**
	 * Gets the chunk at the specified chunk coordinates.
	 *
	 * @param x the x chunk coordinate.
	 * @param z the x chunk coordinate.
	 *
	 * @return null if the chunk doesn't exist in the world folder.
	 */
	@Override
	public Chunk getChunk ( int x , int z ) {
		Region region = region_provider.getRegionAtChunkCoordinates ( x , z );
		
		return region != null ? region.getChunk ( new ChunkLocation ( x , z ) ) : null;
	}
	
	@Override
	public Chunk getChunkAtBlockCoordinates ( int x , int z ) {
		return getChunk ( x >> 4 , z >> 4 );
	}
}
