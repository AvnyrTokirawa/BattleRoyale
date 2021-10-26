package es.outlook.adriansrj.battleroyale.lobby;

import es.outlook.adriansrj.battleroyale.enums.EnumLobbyConfiguration;
import es.outlook.adriansrj.battleroyale.enums.EnumWorldGenerator;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.WorldUtil;
import es.outlook.adriansrj.battleroyale.world.arena.ArenaWorldGenerator;
import es.outlook.adriansrj.battleroyale.world.chunk.EmptyChunkGenerator;
import es.outlook.adriansrj.battleroyale.world.data.WorldData;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.Objects;

/**
 * Battle royale lobby handler.
 *
 * @author AdrianSR / 03/09/2021 / 09:32 p. m.
 */
public final class BattleRoyaleLobbyHandler extends PluginHandler {
	
	public static BattleRoyaleLobbyHandler getInstance ( ) {
		return getPluginHandler ( BattleRoyaleLobbyHandler.class );
	}
	
	private final BattleRoyaleLobby lobby;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BattleRoyaleLobbyHandler ( BattleRoyale plugin ) {
		super ( plugin );
		
		// loading lobby
		BattleRoyaleLobby lobby = null;
		//		String world_path = FilenameUtil.filePathSubPlugins (
		//				EnumLobbyConfiguration.WORLD_PATH.getAsString ( ).replace ( '\\' , '/' ) );
		String world_path   = EnumLobbyConfiguration.WORLD_PATH.getAsString ( ).replace ( '\\' , '/' );
		File   world_folder = new File ( world_path );
		
		if ( !world_folder.exists ( ) ) {
			world_folder.mkdirs ( );
		}
		
		System.out.println ( ">>>>>>>>> BattleRoyaleLobbyHandler: world_folder = " + world_folder.getAbsolutePath ( ) );
		
		// generating
		World world = null;
		
		if ( !WorldUtil.worldFolderCheck ( world_folder ) ) {
			System.out.println ( ">>>>>>> Don't exist, generating world...." );
			
			// we can use the arena world generator to generate an empty world
			ArenaWorldGenerator generator = ArenaWorldGenerator.createGenerator ( world_folder );
			WorldData           data      = generator.getWorldData ( );
			
			data.setGenerateStructures ( false );
			data.setGeneratorType ( EnumWorldGenerator.FLAT );
			
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 ) ) {
				data.setGeneratorOptions ( "minecraft:air;minecraft:air" );
			} else {
				data.setGeneratorOptions ( "2;0;1" );
			}
			
			data.setInitialized ( true );
			data.setName ( world_folder.getName ( ) );
			
			// bukkit world loader must have something to load!
			// if the region folder of the world is empty, bukkit
			// will not load the world, so we have to give bukkit
			// something to load.
			generator.setBlockAtFromLegacyId ( 0 , 0 , 0 , 1 );
			generator.save ( );
			
			// then loading
			world = WorldUtil.loadWorldEmpty ( world_folder );
		} else {
			if ( ( world = Bukkit.getWorld ( world_path ) ) == null && ( world = Bukkit.getWorlds ( ).stream ( )
					.filter ( other -> same ( other.getWorldFolder ( ) , world_folder ) )
					.findAny ( ).orElse ( null ) ) == null ) {
				System.out.println ( ">>>>>>> Loading existing world...." );
				// loading existing world
				world = new WorldCreator ( world_path ).environment ( World.Environment.NORMAL )
						.generator ( new EmptyChunkGenerator ( ) )
						.generateStructures ( false ).createWorld ( );
				world.setAutoSave ( false );
			} else {
				System.out.println ( ">>>>>>> World already loaded...." );
			}
		}
		
		this.lobby = new BattleRoyaleLobby ( world );
		this.lobby.register ( plugin );
	}
	
	public BattleRoyaleLobby getLobby ( ) {
		return lobby;
	}
	
	// ------- utils
	
	private boolean same ( File folder_a , File folder_b ) {
		return Objects.equals ( folder_a , folder_b )
				|| folder_a.getAbsolutePath ( ).equals ( folder_b.getAbsolutePath ( ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
