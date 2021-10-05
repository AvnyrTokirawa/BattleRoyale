package es.outlook.adriansrj.battleroyale.world;

import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.world.chunk.ChunkHeightmap;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe scalable heightmap.
 *
 * @author AdrianSR / 31/08/2021 / 09:35 p. m.
 */
public class ScalableHeightmap {
	
	protected final Map < ChunkLocation, ChunkHeightmap > heightmaps = new ConcurrentHashMap <> ( );
	
	public synchronized int getHeight ( int x , int z ) {
		return getHeightmap ( x >> 4 , z >> 4 ).getHeight ( x & 0xF , z & 0xF );
	}
	
	public synchronized void setHeight ( int x , int z , int height ) {
		getHeightmap ( x >> 4 , z >> 4 ).setHeight ( x & 0xF , z & 0xF , height );
	}
	
	public synchronized void setHeights ( ChunkLocation chunk_location , ChunkHeightmap chunk_heightmap ) {
		Validate.notNull ( chunk_location , "chunk location cannot be null" );
		Validate.notNull ( chunk_heightmap , "chunk heightmap cannot be null" );
		
		heightmaps.put ( chunk_location , chunk_heightmap );
	}
	
	protected ChunkHeightmap getHeightmap ( int x , int z ) {
		return getHeightmap ( new ChunkLocation ( x , z ) );
	}
	
	protected ChunkHeightmap getHeightmap ( ChunkLocation chunk_location ) {
		ChunkHeightmap heightmap = heightmaps.get ( chunk_location );
		
		if ( heightmap == null ) {
			heightmaps.put ( chunk_location , heightmap = new ChunkHeightmap ( ) );
		}
		
		return heightmap;
	}
}