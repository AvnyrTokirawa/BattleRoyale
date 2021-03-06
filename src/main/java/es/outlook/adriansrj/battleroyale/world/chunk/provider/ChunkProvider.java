package es.outlook.adriansrj.battleroyale.world.chunk.provider;

import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.world.chunk.Chunk;

import java.io.IOException;

/**
 * @author AdrianSR / 31/08/2021 / 02:55 p. m.
 */
public interface ChunkProvider {
	
	/**
	 * Gets the chunk at the specified chunk coordinates.
	 *
	 * @param x the x chunk coordinate.
	 * @param z the x chunk coordinate.
	 *
	 * @return the chunk at the specified chunk coordinates.
	 */
	Chunk getChunk ( int x , int z ) throws IOException, IllegalArgumentException;
	
	/**
	 * Gets the chunk at the specified chunk location.
	 *
	 * @param chunk_location the chunk_location chunk location.
	 *
	 * @return the chunk at the specified chunk location.
	 */
	default Chunk getChunk ( ChunkLocation chunk_location ) throws IOException, IllegalArgumentException {
		return getChunk ( chunk_location.getX ( ) , chunk_location.getZ ( ) );
	}
	
	/**
	 * Gets the world at the specified block coordinates.
	 *
	 * @param x the x world coordinate.
	 * @param z the x world coordinate.
	 *
	 * @return the chunk at the specified block coordinates.
	 */
	Chunk getChunkAtBlockCoordinates ( int x , int z ) throws IOException, IllegalArgumentException;
}