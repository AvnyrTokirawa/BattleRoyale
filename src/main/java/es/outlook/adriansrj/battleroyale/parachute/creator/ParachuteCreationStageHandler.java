package es.outlook.adriansrj.battleroyale.parachute.creator;

import es.outlook.adriansrj.battleroyale.enums.EnumWorldGenerator;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.battleroyale.util.world.WorldUtil;
import es.outlook.adriansrj.battleroyale.util.file.FileUtil;
import es.outlook.adriansrj.battleroyale.world.arena.ArenaWorldGenerator;
import es.outlook.adriansrj.battleroyale.world.data.WorldData;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author AdrianSR / 22/11/2021 / 08:57 p. m.
 */
public final class ParachuteCreationStageHandler extends PluginHandler {
	
	private static final String TEMP_WORLD_FOLDER_NAME = "parachute-creation";
	
	public static ParachuteCreationStageHandler getInstance ( ) {
		return getPluginHandler ( ParachuteCreationStageHandler.class );
	}
	
	private final Map < UUID, ParachuteCreationStage > stage_map = new HashMap <> ( );
	private       World                                stages_world;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public ParachuteCreationStageHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	public ParachuteCreationStage getStage ( Player player ) {
		return stage_map.get ( player.getUniqueId ( ) );
	}
	
	public ParachuteCreationStage getStage ( org.bukkit.entity.Player player ) {
		return stage_map.get ( player.getUniqueId ( ) );
	}
	
	public ParachuteCreationStage startStage ( Player player , File file ) {
		Validate.isTrue ( Bukkit.isPrimaryThread ( ) , "must run on server thread" );
		
		// stopping current
		stopStage ( player );
		
		// then starting a new one
		if ( stages_world == null ) {
			File world_folder = new File ( Bukkit.getWorldContainer ( ) , TEMP_WORLD_FOLDER_NAME );
			
			if ( world_folder.exists ( ) ) {
				try {
					FileUtil.deleteDirectory ( world_folder );
				} catch ( IOException e ) {
					e.printStackTrace ( );
					
					try {
						FileUtil.forceDelete ( world_folder );
					} catch ( IOException ex ) {
						ex.printStackTrace ( );
					}
				}
			}
			
			world_folder.mkdirs ( );
			
			ArenaWorldGenerator generator      = ArenaWorldGenerator.createGenerator ( world_folder );
			WorldData           generator_data = generator.getWorldData ( );
			
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 ) ) {
				generator_data.setGeneratorOptions ( "minecraft:air;minecraft:air" );
			} else {
				generator_data.setGeneratorOptions ( "2;0;1" );
			}
			
			generator_data.setGeneratorType ( EnumWorldGenerator.FLAT );
			generator_data.setGenerateStructures ( false );
			generator_data.setInitialized ( true );
			generator_data.setName ( TEMP_WORLD_FOLDER_NAME );
			generator_data.setSpawnX ( 0 );
			generator_data.setSpawnY ( 0 );
			generator_data.setSpawnZ ( 0 );
			
			// bukkit world loader must have something to load!
			// if the region folder of the world is empty, bukkit
			// will not load the world, so we have to give bukkit
			// something to load.
			generator.setBlockAtFromLegacyId ( 0 , 0 , 0 , 1 );
			generator.save ( );
			
			// loading
			stages_world = WorldUtil.loadWorldEmpty ( world_folder );
		}
		
		ParachuteCreationStage stage = new ParachuteCreationStage ( player , file , stages_world );
		stage.start ( );
		
		stage_map.put ( player.getUniqueId ( ) , stage );
		
		return stage;
	}
	
	public ParachuteCreationStage startStage ( org.bukkit.entity.Player player , File file ) {
		return startStage ( Player.getPlayer ( player ) , file );
	}
	
	public boolean stopStage ( ParachuteCreationStage stage ) {
		// unmapping
		stage_map.entrySet ( ).removeIf (
				entry -> Objects.equals ( stage , entry.getValue ( ) ) );
		
		// then stopping
		if ( stage.isActive ( ) ) {
			stage.stop ( );
			return true;
		} else {
			return false;
		}
	}
	
	public boolean stopStage ( Player player ) {
		ParachuteCreationStage stage = getStage ( player );
		
		if ( stage != null ) {
			return stopStage ( stage );
		} else {
			return false;
		}
	}
	
	public boolean stopStage ( org.bukkit.entity.Player player ) {
		ParachuteCreationStage stage = getStage ( player );
		
		if ( stage != null ) {
			return stopStage ( stage );
		} else {
			return false;
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}