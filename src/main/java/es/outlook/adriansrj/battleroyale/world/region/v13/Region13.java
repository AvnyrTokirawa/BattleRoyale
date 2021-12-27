package es.outlook.adriansrj.battleroyale.world.region.v13;

import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.world.RegionFile;
import es.outlook.adriansrj.battleroyale.world.chunk.v13.Chunk13;
import es.outlook.adriansrj.battleroyale.world.region.Region;
import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Region for minecraft versions 1.13 - 1.15.
 *
 * @author AdrianSR / 24/08/2021 / Time: 07:13 p. m.
 */
public class Region13 implements Region {
	
	// 32 * 32 chunk area.
	protected final Chunk13[][] chunks = new Chunk13[ 32 ][ 32 ];
	protected final Location2I  location;
	protected       File        file;
	
	public Region13 ( Location2I location ) {
		this.location = location;
	}
	
	public Region13 ( Location2I location , File file ) {
		this ( location );
		
		setFile ( file );
	}
	
	@Override
	public Location2I getLocation ( ) {
		return location;
	}
	
	/**
	 * Gets the file this region loads chunks from.
	 *
	 * @return file this region loads chunks from.
	 */
	@Override
	public File getFile ( ) {
		return file;
	}
	
	@Override
	public Chunk13[][] getChunks ( ) {
		return chunks;
	}
	
	@Override
	public Chunk13 getChunk ( ChunkLocation location ) throws IOException, IllegalArgumentException {
		int     x     = location.getX ( ) & 31;
		int     z     = location.getZ ( ) & 31;
		Chunk13 chunk = chunks[ x ][ z ];
		
		if ( chunk == null ) {
			// reading from file
			//			System.out.println ( ">>>>> " + x + ", " + z + " | file = " + file );
			
			if ( file != null && file.exists ( ) ) {
				// adventure nbt is not working properly, so we will
				// use querz nbt to load the chunk.
				try ( RegionFile handler = new RegionFile ( file , true ) ;
						DataInputStream input = handler.getChunkDataInputStream ( x , z ) ) {
					if ( input != null ) {
						NamedTag tag = new NBTDeserializer ( false ).fromStream ( input );
						
						if ( tag != null && tag.getTag ( ) instanceof CompoundTag ) {
							chunks[ x ][ z ] = ( chunk = new Chunk13 ( ( CompoundTag ) tag.getTag ( ) ) );
						} else {
							throw new IOException (
									"invalid data tag: " + ( tag == null ? "null" : tag.getClass ( ).getName ( ) ) );
						}
						//						chunks[ x ][ z ] = ( chunk = new Chunk13 (
						//								BinaryTagIO.reader ( ).read ( ( DataInput ) input ) ) );
					}
				}
			}
			
			// file doesn't exit, not specified, couldn't read it,
			// or this chunk doesn't exist in the file.
			if ( chunk == null ) {
				chunks[ x ][ z ] = ( chunk = new Chunk13 ( location ) );
			}
		}
		
		return chunk;
	}
	
	@Override
	public boolean containsChunk ( ChunkLocation location ) {
		int x = location.getX ( ) & 31;
		int z = location.getZ ( ) & 31;
		
		return chunks[ x ][ z ] != null;
	}
	
	/**
	 * Sets the file the chunks will be loaded from.
	 *
	 * @param file the file to load chunks from.
	 */
	public void setFile ( File file ) {
		this.file = file;
	}
	
	@Override
	public void save ( File region_folder ) {
		Chunk13[][] chunks = getChunks ( );
		File file = new File ( region_folder , String.format (
				REGION_FILE_NAME_FORMAT , location.getX ( ) , location.getZ ( ) ) );
		
		if ( chunks.length > 0 ) {
			try {
				if ( !file.exists ( ) ) {
					file.getParentFile ( ).mkdirs ( );
					file.createNewFile ( );
				}
				
				// writing chunks
				try ( RegionFile handler = new RegionFile ( file ) ) {
					for ( int x = 0 ; x < chunks.length ; x++ ) {
						for ( int z = 0 ; z < chunks[ 0 ].length ; z++ ) {
							ChunkLocation location = new ChunkLocation ( x , z );
							
							if ( containsChunk ( location ) ) {
								getChunk ( location ).write ( handler );
							}
						}
					}
				}
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass ( ) != o.getClass ( ) ) {
			return false;
		}
		Region13 region = ( Region13 ) o;
		return Objects.equals ( location , region.location );
	}
	
	@Override
	public int hashCode ( ) {
		return Objects.hash ( location );
	}
}