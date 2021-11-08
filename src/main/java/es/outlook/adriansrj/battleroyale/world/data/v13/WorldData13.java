package es.outlook.adriansrj.battleroyale.world.data.v13;

import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.enums.EnumWorldGenerator;
import es.outlook.adriansrj.battleroyale.world.Material;
import es.outlook.adriansrj.battleroyale.world.arena.ArenaWorldGeneratorMainv1_9_v1_12;
import es.outlook.adriansrj.battleroyale.world.data.WorldData;
import net.kyori.adventure.nbt.*;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 1.13+ {@link WorldData}.
 *
 * @author AdrianSR / 25/08/2021 / Time: 09:40 a. m.
 */
public class WorldData13 implements WorldData {
	
	protected final EnumDataVersion    data_version;
	protected       String             name                = "world";
	protected       boolean            initialized         = false;
	protected       EnumWorldGenerator generator_type      = EnumWorldGenerator.DEFAULT;
	protected       String             generator_options   = "";
	protected       long               seed                = new Random ( ).nextLong ( );
	protected       boolean            generate_structures = false;
	protected       long               last_time_played    = System.currentTimeMillis ( );
	protected       int                spawn_x             = 0;
	protected       int                spawn_y             = 0;
	protected       int                spawn_z             = 0;
	
	public WorldData13 ( EnumDataVersion data_version ) {
		Validate.isTrue ( data_version.getId ( ) >= EnumDataVersion.v1_13.getId ( ) ,
						  "data version " + data_version.name ( ) + " not supported." );
		
		this.data_version = data_version;
	}
	
	@Override
	public EnumDataVersion getDataVersion ( ) {
		return data_version;
	}
	
	@Override
	public String getName ( ) {
		return name;
	}
	
	@Override
	public void setName ( String name ) {
		this.name = name;
	}
	
	@Override
	public boolean isInitialized ( ) {
		return initialized;
	}
	
	@Override
	public void setInitialized ( boolean initialized ) {
		this.initialized = initialized;
	}
	
	@Override
	public EnumWorldGenerator getGeneratorType ( ) {
		return generator_type;
	}
	
	@Override
	public void setGeneratorType ( EnumWorldGenerator generator_type ) {
		this.generator_type = generator_type;
	}
	
	@Override
	public String getGeneratorOptions ( ) {
		return generator_options;
	}
	
	@Override
	public void setGeneratorOptions ( String generator_options ) {
		this.generator_options = generator_options;
	}
	
	@Override
	public long getSeed ( ) {
		return seed;
	}
	
	@Override
	public void setSeed ( long seed ) {
		this.seed = seed;
	}
	
	@Override
	public boolean isGenerateStructures ( ) {
		return generate_structures;
	}
	
	@Override
	public void setGenerateStructures ( boolean generate_structures ) {
		this.generate_structures = generate_structures;
	}
	
	@Override
	public long getLastTimePlayed ( ) {
		return last_time_played;
	}
	
	@Override
	public void setLastTimePlayed ( long last_time_played ) {
		this.last_time_played = last_time_played;
	}
	
	@Override
	public int getSpawnX ( ) {
		return spawn_x;
	}
	
	@Override
	public int getSpawnY ( ) {
		return spawn_y;
	}
	
	@Override
	public int getSpawnZ ( ) {
		return spawn_z;
	}
	
	@Override
	public void setSpawnX ( int spawn_x ) {
		this.spawn_x = spawn_x;
	}
	
	@Override
	public void setSpawnY ( int spawn_y ) {
		this.spawn_y = spawn_y;
	}
	
	@Override
	public void setSpawnZ ( int spawn_z ) {
		this.spawn_z = spawn_z;
	}
	
	@Override
	public CompoundBinaryTag toNBT ( ) {
		Map < String, BinaryTag > root    = new HashMap <> ( );
		Map < String, BinaryTag > data    = new HashMap <> ( );
		Map < String, BinaryTag > version = new HashMap <> ( );
		
		// mapping values
		version.put ( "Id" , IntBinaryTag.of ( data_version.getId ( ) ) );
		version.put ( "Name" , StringBinaryTag.of ( data_version.getName ( ) ) );
		version.put ( "Snapshot" , ByteBinaryTag.of ( ( byte ) ( data_version.isSnapshot ( ) ? 1 : 0 ) ) );
		
		data.put ( "Version" , CompoundBinaryTag.from ( version ) );
		data.put ( "LevelName" , StringBinaryTag.of ( name ) );
		data.put ( "DataVersion" , IntBinaryTag.of ( data_version.getId ( ) ) );
		data.put ( "version" , IntBinaryTag.of ( ArenaWorldGeneratorMainv1_9_v1_12.NBT_VERSION ) );
		data.put ( "initialized" , IntBinaryTag.of ( initialized ? 1 : 0 ) );
		data.put ( "LastPlayed" , LongBinaryTag.of ( last_time_played ) );
		data.put ( "SpawnX" , IntBinaryTag.of ( spawn_x ) );
		data.put ( "SpawnY" , IntBinaryTag.of ( spawn_y ) );
		data.put ( "SpawnZ" , IntBinaryTag.of ( spawn_z ) );
		
		// generation
		Map < String, BinaryTag > generator_settings = new HashMap <> ( );
		
		// generator settings 1.13 - 1.15
		data.put ( "generatorName" , StringBinaryTag.of ( generator_type.getName ( ) ) );
		data.put ( "generatorVersion" , IntBinaryTag.of ( 1 ) );
		
		if ( generator_options != null ) {
			Map < String, BinaryTag > generator_compound = new HashMap <> ( );
			
			generator_compound.put ( "flat_world_options" , StringBinaryTag.of ( generator_options ) );
			// void biome (https://minecraft.fandom.com/wiki/Java_Edition_data_values#Biomes)
			generator_compound.put ( "biome" , StringBinaryTag.of ( "the_void" ) );
			
			// layers
			List < CompoundBinaryTag > layers = new ArrayList <> ( );
			
			if ( generator_options.indexOf ( ';' ) != -1 ) {
				// several layers
				String[] layer_ids = generator_options.split ( ";" );
				
				for ( String layer_block_id : layer_ids ) {
					layers.add ( CompoundBinaryTag.builder ( )
										 .put ( "block" ,
												StringBinaryTag.of ( layer_block_id.toLowerCase ( ).trim ( ) ) )
										 .put ( "height" , ByteBinaryTag.of ( ( byte ) 1 ) )
										 .build ( ) );
				}
			} else if ( StringUtils.isBlank ( generator_options ) ) {
				// empty
				layers.add ( CompoundBinaryTag.builder ( )
									 .put ( "block" , StringBinaryTag.of ( Material.AIR.getNamespacedId ( ) ) )
									 .put ( "height" , ByteBinaryTag.of ( ( byte ) 127 ) )
									 .build ( ) );
			} else {
				// single layer
				layers.add ( CompoundBinaryTag.builder ( )
									 .put ( "block" , StringBinaryTag.of ( generator_options ) )
									 .put ( "height" , ByteBinaryTag.of ( ( byte ) 1 ) )
									 .build ( ) );
			}
			
			generator_compound.put ( "layers" , ListBinaryTag.from ( layers ) );
			
			data.put ( "generatorOptions" , CompoundBinaryTag.from ( generator_compound ) );
		}
		
		data.put ( "RandomSeed" , LongBinaryTag.of ( seed ) );
		data.put ( "MapFeatures" , IntBinaryTag.of ( generate_structures ? 1 : 0 ) );
		
		// generator settings 1.16 +
		generator_settings.put ( "bonus_chest" , IntBinaryTag.of ( 0 ) );
		generator_settings.put ( "seed" , LongBinaryTag.of ( seed ) );
		generator_settings.put ( "generate_features" , IntBinaryTag.of ( generate_structures ? 1 : 0 ) );
		data.put ( "WorldGenSettings" , CompoundBinaryTag.from ( generator_settings ) );
		
		root.put ( "Data" , CompoundBinaryTag.from ( data ) );
		
		return CompoundBinaryTag.from ( root );
	}
}
