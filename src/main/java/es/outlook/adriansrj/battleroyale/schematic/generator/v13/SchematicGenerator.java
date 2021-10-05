package es.outlook.adriansrj.battleroyale.schematic.generator.v13;

import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.world.Material;
import es.outlook.adriansrj.battleroyale.world.chunk.v13.Chunk13;
import es.outlook.adriansrj.battleroyale.world.chunk.v13.ChunkSection13;
import es.outlook.adriansrj.battleroyale.world.region.Region;
import es.outlook.adriansrj.battleroyale.world.region.v13.Region13;
import es.outlook.adriansrj.core.util.math.Vector3D;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import es.outlook.adriansrj.core.util.world.WorldUtil;
import net.kyori.adventure.nbt.*;
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Schematic creator for versions {@link EnumDataVersion#v1_13}+.
 *
 * @author AdrianSR / 29/08/2021 / 01:50 p. m.
 */
public class SchematicGenerator implements es.outlook.adriansrj.battleroyale.schematic.generator.SchematicGenerator {
	
	/**
	 * @author AdrianSR / 29/08/2021 / 02:36 p. m.
	 */
	protected static class BlockData {
		
		protected final Vector3D          location;
		protected final Material          material;
		protected final CompoundBinaryTag tile_data;
		
		public BlockData ( Vector3D location , Material material , CompoundBinaryTag tile_data ) {
			this.location  = location;
			this.material  = material;
			this.tile_data = tile_data;
		}
		
		public String getKey ( ) {
			Map < String, String > properties = material.getProperties ( );
			
			if ( properties.size ( ) > 0 ) {
				String properties_string = properties.entrySet ( ).stream ( )
						.map ( entry -> entry.getKey ( )
								+ "=" + entry.getValue ( ).toLowerCase ( Locale.ROOT ) )
						.collect ( Collectors.joining ( "," ) );
				
				return ( material.getNamespacedId ( ) + "[" + properties_string + "]" )
						.toLowerCase ( Locale.ROOT ).trim ( );
			} else {
				return material.getNamespacedId ( ).toLowerCase ( Locale.ROOT ).trim ( );
			}
		}
	}
	
	protected final EnumDataVersion data_version;
	
	public SchematicGenerator ( EnumDataVersion data_version ) {
		Validate.isTrue ( data_version.getId ( ) >= EnumDataVersion.v1_13.getId ( ) ,
						  "unsupported data version" );
		
		this.data_version = data_version;
	}
	
	@Override
	public void generate ( World world , BoundingBox bounds , File out ) {
		// we're disabling the auto-saving as we don't want
		// the world to be saved while we're acceding its files
		final boolean autosave = world.isAutoSave ( );
		world.setAutoSave ( false );
		
		// extracting required data
		File            folder = world.getWorldFolder ( );
		Vector          origin = bounds.getMinimum ( );
		int             width  = ( int ) Math.round ( bounds.getWidth ( ) );
		int             height = ( int ) Math.round ( bounds.getHeight ( ) );
		int             depth  = ( int ) Math.round ( bounds.getDepth ( ) );
		BlockData[][][] blocks = new BlockData[ width ][ height ][ depth ];
		
		// TODO: use ChunkProvider instead of manually loading chunks
		Map < Location2I, Region13 >   region_map = new HashMap <> ( );
		Map < ChunkLocation, Chunk13 > chunk_map  = new HashMap <> ( );
		
		for ( int y = 0 ; y < height ; y++ ) {
			int yy = origin.getBlockY ( ) + y;
			for ( int z = 0 ; z < depth ; z++ ) {
				int zz = origin.getBlockZ ( ) + z;
				for ( int x = 0 ; x < width ; x++ ) {
					int xx = origin.getBlockX ( ) + x;
					
					// reading chunk
					ChunkLocation chunk_location = new ChunkLocation ( xx >> 4 , zz >> 4 );
					Chunk13       chunk          = chunk_map.get ( chunk_location );
					
					if ( chunk == null ) {
						Location2I region_location = chunk_location.getRegionLocation ( );
						Region13   region          = region_map.get ( region_location );
						
						if ( region == null ) {
							region_map.put (
									region_location , region = new Region13 ( region_location , new File (
											new File ( folder , WorldUtil.REGION_FOLDER_NAME ) ,
											String.format ( Region.REGION_FILE_NAME_FORMAT ,
															region_location.getX ( ) ,
															region_location.getZ ( ) ) ) ) );
						}
						
						chunk_map.put ( chunk_location , chunk = region.getChunk ( chunk_location ) );
					}
					
					// then extracting block
					ChunkSection13 section = chunk != null ? chunk.getSectionFromYCoordinate ( yy ) : null;
					
					if ( section != null ) {
						Material material = section.getMaterial ( xx & 0xF , yy & 0xF , zz & 0xF );
						
						if ( material != null && !material.isEmpty ( ) ) {
							blocks[ x ][ y ][ z ] = new BlockData (
									new Vector3D ( x , y , z ) , material ,
									null /* tile entities will probably be implemented in the future */ );
						}
					}
				}
			}
		}
		
		// disposing resources
		chunk_map.clear ( );
		region_map.clear ( );
		
		// re-enabling auto-save
		world.setAutoSave ( autosave );
		
		// then generating
		generate ( width , height , depth , blocks , out );
	}
	
	protected void generate ( int width , int height , int depth , BlockData[][][] blocks ,
			File out ) {
		Map < String, BinaryTag > root = new HashMap <> ( );
		
		root.put ( "Version" , IntBinaryTag.of ( 2 ) );
		root.put ( "DataVersion" , IntBinaryTag.of ( data_version.getId ( ) ) );
		root.put ( "Width" , ShortBinaryTag.of ( ( short ) width ) );
		root.put ( "Height" , ShortBinaryTag.of ( ( short ) height ) );
		root.put ( "Length" , ShortBinaryTag.of ( ( short ) depth ) );
		
		int                        palette_max   = 0;
		Map < String, Integer >    palette       = new HashMap <> ( );
		List < CompoundBinaryTag > tile_entities = new ArrayList <> ( );
		ByteArrayOutputStream      buffer        = new ByteArrayOutputStream ( width * height * depth );
		
		for ( int y = 0 ; y < height ; y++ ) {
			for ( int z = 0 ; z < depth ; z++ ) {
				for ( int x = 0 ; x < width ; x++ ) {
					BlockData block = blocks[ x ][ y ][ z ];
					
					if ( block == null ) {
						block = new BlockData ( new Vector3D ( x , y , z ) , Material.AIR , null );
					}
					
					if ( block.tile_data != null ) {
						Map < String, BinaryTag > values = new HashMap <> ( );
						
						for ( Map.Entry < String, ? extends BinaryTag > value : block.tile_data ) {
							switch ( value.getKey ( ) ) {
								// ignore 'id' if it exists. it must be 'Id'
								case "id":
									values.put ( "Id" , value.getValue ( ) );
									break;
								
								// actual positions are kept in NBT, ignoring.
								case "x":
								case "y":
								case "z":
									break;
								
								default: {
									values.put ( value.getKey ( ) , value.getValue ( ) );
									break;
								}
							}
						}
						
						// position within schematic bounds
						values.put ( "Pos" , IntArrayBinaryTag.of ( x , y , z ) );
						
						tile_entities.add ( CompoundBinaryTag.from ( values ) );
					}
					
					String block_key = block.getKey ( );
					int    block_id;
					
					if ( palette.containsKey ( block_key ) ) {
						block_id = palette.get ( block_key );
					} else {
						block_id = palette_max;
						palette.put ( block_key , block_id );
						palette_max++;
					}
					
					while ( ( block_id & -128 ) != 0 ) {
						buffer.write ( block_id & 127 | 128 );
						block_id >>>= 7;
					}
					
					buffer.write ( block_id );
				}
			}
		}
		
		// palette max
		root.put ( "PaletteMax" , IntBinaryTag.of ( palette_max ) );
		
		// palette
		Map < String, BinaryTag > palette_tag = new HashMap <> ( );
		palette.forEach ( ( key , value ) -> palette_tag.put ( key , IntBinaryTag.of ( value ) ) );
		
		root.put ( "Palette" , CompoundBinaryTag.from ( palette_tag ) );
		
		// blocks
		root.put ( "BlockData" , ByteArrayBinaryTag.of ( buffer.toByteArray ( ) ) );
		root.put ( "BlockEntities" , ListBinaryTag.from ( tile_entities ) );
		
		// then writing to output file
		FileOutputStream output = null;
		
		try {
			CompoundBinaryTag compound = CompoundBinaryTag.from ( root );
			Map.Entry < String, CompoundBinaryTag > named = new Map.Entry < String, CompoundBinaryTag > ( ) {
				@Override
				public String getKey ( ) {
					return "Schematic";
				}
				
				@Override
				public CompoundBinaryTag getValue ( ) {
					return compound;
				}
				
				@Override
				public CompoundBinaryTag setValue ( CompoundBinaryTag value ) {
					return compound;
				}
			};
			
			BinaryTagIO.writer ( )
					.writeNamed ( named ,
								  output = new FileOutputStream ( out ) ,
								  BinaryTagIO.Compression.GZIP );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		} finally {
			try {
				if ( output != null ) {
					output.close ( );
				}
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
}
