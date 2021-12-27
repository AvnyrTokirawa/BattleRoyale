package es.outlook.adriansrj.battleroyale.schematic.generator.v12;

import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.schematic.generator.SchematicGeneratorBase;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.world.block.BlockTileEntity;
import es.outlook.adriansrj.battleroyale.world.chunk.v12.Chunk12;
import es.outlook.adriansrj.battleroyale.world.chunk.v12.ChunkSection12;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import net.kyori.adventure.nbt.*;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Schematic creator for versions pre-{@link EnumDataVersion#v1_13}.
 *
 * @author AdrianSR / 29/08/2021 / 01:50 p. m.
 */
public class SchematicGenerator_v12 extends SchematicGeneratorBase {
	
	protected final EnumDataVersion       data_version;
	protected final Set < ChunkLocation > corrupted        = new HashSet <> ( );
	protected       boolean               corrupted_logged = false;
	
	public SchematicGenerator_v12 ( File world_folder , EnumDataVersion data_version ) {
		super ( world_folder );
		Validate.isTrue ( data_version.getId ( ) <= EnumDataVersion.v1_12.getId ( ) ,
						  "unsupported data version" );
		
		this.data_version = data_version;
	}
	
	public SchematicGenerator_v12 ( World world , EnumDataVersion data_version ) {
		super ( world );
		Validate.isTrue ( data_version.getId ( ) <= EnumDataVersion.v1_12.getId ( ) ,
						  "unsupported data version" );
		
		this.data_version = data_version;
	}
	
	@Override
	public void generatePart ( BoundingBox bounds , File out ) {
		Vector origin = bounds.getMinimum ( );
		int    width  = ( int ) Math.round ( bounds.getWidth ( ) );
		int    height = ( int ) Math.round ( bounds.getHeight ( ) );
		int    depth  = ( int ) Math.round ( bounds.getDepth ( ) );
		
		// extracting required data
		byte[]                   blocks        = new byte[ width * height * depth ];
		byte[]                   add_blocks    = null;
		byte[]                   block_data    = new byte[ width * height * depth ];
		List < BlockTileEntity > tile_entities = new ArrayList <> ( );
		
		for ( int y = 0 ; y < height ; y++ ) {
			int yy = origin.getBlockY ( ) + y;
			for ( int z = 0 ; z < depth ; z++ ) {
				int zz = origin.getBlockZ ( ) + z;
				for ( int x = 0 ; x < width ; x++ ) {
					int xx = origin.getBlockX ( ) + x;
					
					// reading chunk
					ChunkLocation chunk_location = new ChunkLocation ( xx >> 4 , zz >> 4 );
					Chunk12       chunk          = null;
					
					if ( corrupted.contains ( chunk_location ) ) {
						// corrupted chunks may slow the process as they
						// will not be cached by the provider, so
						// let's skip them.
						continue;
					}
					
					try {
						chunk = ( Chunk12 ) chunk_provider.getChunk ( chunk_location );
					} catch ( IOException | IllegalArgumentException e ) {
						corrupted.add ( chunk_location );
						
						if ( !corrupted_logged ) {
							ConsoleUtil.sendPluginMessage (
									ChatColor.RED , "Schematic generation may take longer than " +
											"expected as there are some chunks that appear to be corrupted." ,
									BattleRoyale.getInstance ( ) );
							
							corrupted_logged = true;
						}
						continue;
					}
					
					// extracting block id & data
					ChunkSection12 section = chunk != null ? chunk.getSectionFromYCoordinate ( yy ) : null;
					
					if ( section != null ) {
						int  index = y * width * depth + z * width + x;
						int  id    = section.getBlockId ( xx & 15 , yy & 15 , zz & 15 );
						byte data  = section.getBlockData ( xx & 15 , yy & 15 , zz & 15 );
						
						if ( id > 255 ) {
							if ( add_blocks == null ) {
								add_blocks = new byte[ ( blocks.length >> 1 ) + 1 ];
							}
							
							add_blocks[ index >> 1 ] = ( byte ) ( ( ( index & 1 ) == 0 ) ?
									add_blocks[ index >> 1 ] & 0xF0 | ( id >> 8 ) & 0xF
									: add_blocks[ index >> 1 ] & 0xF | ( ( id >> 8 ) & 0xF ) << 4 );
						}
						
						blocks[ index ]     = ( byte ) id;
						block_data[ index ] = data;
					}
					
					// tile entity block
					BlockTileEntity tile_entity = chunk != null ? chunk.getTileEntity ( xx , yy , zz ) : null;
					
					if ( tile_entity != null ) {
						// relocating
						BlockTileEntity relocated = new BlockTileEntity ( tile_entity );
						
						relocated.setX ( x );
						relocated.setY ( y );
						relocated.setZ ( z );
						
						// done
						if ( !tile_entities.contains ( relocated ) ) {
							tile_entities.add ( relocated );
						}
					}
				}
			}
		}
		
		// then generating
		generate ( width , height , depth , blocks , add_blocks , block_data , tile_entities , out );
	}
	
	protected void generate ( int width , int height , int depth , byte[] blocks , byte[] add_blocks ,
			byte[] block_data , List < BlockTileEntity > tile_entities , File out ) {
		Map < String, BinaryTag > root = new HashMap <> ( );
		
		root.put ( "Width" , ShortBinaryTag.of ( ( short ) width ) );
		root.put ( "Height" , ShortBinaryTag.of ( ( short ) height ) );
		root.put ( "Length" , ShortBinaryTag.of ( ( short ) depth ) );
		root.put ( "Materials" , StringBinaryTag.of ( "Alpha" ) );
		
		root.put ( "Blocks" , ByteArrayBinaryTag.of ( blocks ) );
		root.put ( "Data" , ByteArrayBinaryTag.of ( block_data ) );
		
		if ( add_blocks != null ) {
			root.put ( "AddBlocks" , ByteArrayBinaryTag.of ( add_blocks ) );
		}
		
		root.put ( "TileEntities" , ListBinaryTag.from (
				tile_entities.stream ( )
						.filter ( Objects :: nonNull )
						.map ( BlockTileEntity :: toNBT )
						.collect ( Collectors.toList ( ) ) ) );
		
		// then writing to output file
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
		
		try ( FileOutputStream output = new FileOutputStream ( out ) ) {
			BinaryTagIO.writer ( ).writeNamed ( named , output , BinaryTagIO.Compression.GZIP );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		}
	}
	
	@Override
	public void dispose ( ) {
		super.dispose ( );
		
		// disposing corrupted chunks
		corrupted.clear ( );
	}
}