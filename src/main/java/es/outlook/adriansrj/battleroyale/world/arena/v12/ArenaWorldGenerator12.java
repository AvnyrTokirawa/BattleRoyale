package es.outlook.adriansrj.battleroyale.world.arena.v12;

import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.util.WorldEditUtil;
import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.world.arena.ArenaWorldGenerator;
import es.outlook.adriansrj.battleroyale.world.chunk.Chunk;
import es.outlook.adriansrj.battleroyale.world.chunk.v12.Chunk12;
import es.outlook.adriansrj.battleroyale.world.data.v12.WorldData12;
import es.outlook.adriansrj.battleroyale.world.region.v12.Region12;
import es.outlook.adriansrj.core.util.math.Vector3D;
import es.outlook.adriansrj.core.util.world.WorldUtil;
import net.kyori.adventure.nbt.BinaryTagIO;
import org.apache.commons.lang.Validate;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * World generator for versions pre-{@link EnumDataVersion#v1_13}.
 * </p>
 *
 * @author AdrianSR / 25/08/2021 / Time: 09:19 a. m.
 */
public class ArenaWorldGenerator12 implements ArenaWorldGenerator {
	
	protected final Map < Location2I, Region12 > region_map = new ConcurrentHashMap <> ( );
	protected final WorldData12                  world_data;
	protected final File                         world_folder;
	
	public ArenaWorldGenerator12 ( File world_folder , EnumDataVersion data_version ) {
		Validate.notNull ( world_folder , "world folder cannot be null" );
		Validate.notNull ( data_version , "data version cannot be null" );
		Validate.isTrue ( data_version.getId ( ) < EnumDataVersion.v1_13.getId ( ) ,
						  "unsupported data version" );
		
		this.world_data   = new WorldData12 ( data_version );
		this.world_folder = world_folder;
	}
	
	public ArenaWorldGenerator12 ( File world_folder ) {
		this ( world_folder , EnumDataVersion.v1_12 );
	}
	
	@Override
	public File getWorldFolder ( ) {
		return world_folder;
	}
	
	@Override
	public WorldData12 getWorldData ( ) {
		return world_data;
	}
	
	@Override
	public Region12 getRegion ( Location2I location ) {
		Region12 region = region_map.get ( location );
		
		if ( region == null ) {
			region_map.put ( location , region = new Region12 ( location ) );
		}
		
		return region;
	}
	
	@Override
	public Chunk12 getChunk ( ChunkLocation location ) {
		return getRegion ( new Location2I ( location.getRegionX ( ) , location.getRegionZ ( ) ) ).getChunk ( location );
	}
	
	@Override
	public Chunk getChunkAt ( Vector vector ) {
		return getChunk ( new ChunkLocation ( vector.getBlockX ( ) >> 4 , vector.getBlockZ ( ) >> 4 ) );
	}
	
	@Override
	public void setBlockAtFromLegacyId ( int x , int y , int z , int id ) {
		setBlockIdAt ( x , y , z , id );
	}
	
	public byte getBlockAt ( int x , int y , int z ) {
		return getChunk ( new ChunkLocation ( x >> 4 , z >> 4 ) ).getBlock ( x & 15 , y , z & 15 );
	}
	
	public byte getBlockAddAt ( int x , int y , int z ) {
		return getChunk ( new ChunkLocation ( x >> 4 , z >> 4 ) ).getBlockAdd ( x & 15 , y , z & 15 );
	}
	
	public int getBlockIdAt ( int x , int y , int z ) {
		return getChunk ( new ChunkLocation ( x >> 4 , z >> 4 ) ).getBlockId ( x & 15 , y , z & 15 );
	}
	
	public int getBlockDataAt ( int x , int y , int z ) {
		return getChunk ( new ChunkLocation ( x >> 4 , z >> 4 ) ).getBlockData ( x & 15 , y , z & 15 );
	}
	
	public void setBlockAt ( int x , int y , int z , byte block ) {
		getChunk ( new ChunkLocation ( x >> 4 , z >> 4 ) ).setBlock ( x & 15 , y , z & 15 , block );
	}
	
	public void setBlockAt ( int x , int y , int z , int id , byte data ) {
		setBlockIdAt ( x , y , z , id );
		setBlockDataAt ( x , y , z , data );
	}
	
	public void setBlockIdAt ( int x , int y , int z , int id ) {
		getChunk ( new ChunkLocation ( x >> 4 , z >> 4 ) ).setBlockId ( x & 15 , y , z & 15 , id );
	}
	
	public void setBlockAddAt ( int x , int y , int z , byte add ) {
		getChunk ( new ChunkLocation ( x >> 4 , z >> 4 ) ).setBlockAdd ( x & 15 , y , z & 15 , add );
	}
	
	public void setBlockDataAt ( int x , int y , int z , byte data ) {
		getChunk ( new ChunkLocation ( x >> 4 , z >> 4 ) ).setBlockData ( x & 15 , y , z & 15 , data );
	}
	
	@Override
	public void insert ( Clipboard schematic , Vector location , boolean ignore_air_blocks ) {
		// we are assuming that it is an instance of BlockArrayClipboard clipboard,
		// since we are not expecting to have to include here new implementations
		// of Clipboard for this world edit version.
		BlockArrayClipboard array      = ( BlockArrayClipboard ) schematic;
		Vector3D            dimensions = WorldEditUtil.getDimensions ( schematic );
		
		try {
			Class < ? > legacy_block_class = Class.forName ( "com.sk89q.worldedit.foundation.Block" );
			Method      id_getter          = legacy_block_class.getMethod ( "getId" );
			Method      data_getter        = legacy_block_class.getMethod ( "getData" );
			Field       blocks_field       = BlockArrayClipboard.class.getDeclaredField ( "blocks" );
			
			blocks_field.setAccessible ( true );
			
			Object[][][] uncast = ( Object[][][] ) blocks_field.get ( array );
			
			for ( int x = 0 ; x < dimensions.getX ( ) ; x++ ) {
				for ( int y = 0 ; y < dimensions.getY ( ) ; y++ ) {
					for ( int z = 0 ; z < dimensions.getZ ( ) ; z++ ) {
						Object uncast_block = uncast[ x ][ y ][ z ];
						
						if ( legacy_block_class.isAssignableFrom ( uncast_block.getClass ( ) ) ) {
							int id   = ( int ) id_getter.invoke ( legacy_block_class.cast ( uncast_block ) );
							int data = ( int ) data_getter.invoke ( legacy_block_class.cast ( uncast_block ) );
							
							if ( ignore_air_blocks && id <= 0 ) {
								continue;
							}
							
							setBlockAt (
									( int ) Math.floor ( location.getX ( ) + x ) ,
									( int ) Math.floor ( location.getY ( ) + y ) ,
									( int ) Math.floor ( location.getZ ( ) + z ) ,
									
									id , ( byte ) data );
						}
					}
				}
			}
		} catch ( NoSuchFieldException | IllegalAccessException
				| NoSuchMethodException | InvocationTargetException e ) {
			throw new IllegalArgumentException ( "invalid schematic" , e );
		} catch ( ClassNotFoundException ex ) {
			ex.printStackTrace ( );
		}
	}
	
	@Override
	public void save ( ) {
		// saving world data
		File level_data_file = new File ( world_folder , WorldUtil.LEVEL_DATA_FILE_NAME );
		
		try {
			if ( ! level_data_file.exists ( ) ) {
				level_data_file.getParentFile ( ).mkdirs ( );
				level_data_file.createNewFile ( );
			}
			
			BinaryTagIO.writer ( ).write ( world_data.toNBT ( ) , level_data_file.toPath ( ) ,
										   BinaryTagIO.Compression.GZIP );
		} catch ( IOException ex ) {
			ex.printStackTrace ( );
		}
		
		// saving chunks
		File region_folder = new File ( world_folder , WorldUtil.REGION_FOLDER_NAME );
		
		if ( ! region_folder.exists ( ) ) {
			region_folder.mkdirs ( );
		}
		
		for ( Region12 region : region_map.values ( ) ) {
			region.save ( region_folder );
		}
	}
}