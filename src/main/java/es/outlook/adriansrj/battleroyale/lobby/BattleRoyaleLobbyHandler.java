package es.outlook.adriansrj.battleroyale.lobby;

import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.enums.EnumLobbyConfiguration;
import es.outlook.adriansrj.battleroyale.enums.EnumWorldGenerator;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.WorldUtil;
import es.outlook.adriansrj.battleroyale.world.arena.ArenaWorldGenerator;
import es.outlook.adriansrj.battleroyale.world.data.WorldData;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;

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
		File   lobby_folder = EnumDirectory.LOBBY_DIRECTORY.getDirectoryMkdirs ( );
		String world_name   = EnumLobbyConfiguration.WORLD_NAME.getAsString ( );
		File   world_folder;
		
		File  relative = new File ( lobby_folder , world_name );
		World world    = Bukkit.getWorld ( world_name );
		
		if ( ( relative.exists ( ) && relative.isDirectory ( ) ) || world == null ) {
			world_folder = relative;
			
			if ( !world_folder.exists ( ) ) {
				world_folder.mkdirs ( );
			}
			
			if ( !WorldUtil.worldFolderCheck ( world_folder ) ) {
				// not-existing world.
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
			}
			
			// then loading
			world = WorldUtil.loadWorldEmpty ( world_folder );
			
			if ( world == null ) {
				throw new IllegalStateException ( "couldn't load lobby world" );
			}
		}
		
		this.lobby = new BattleRoyaleLobby ( world );
		this.lobby.register ( plugin );
	}
	
	public BattleRoyaleLobby getLobby ( ) {
		return lobby;
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
