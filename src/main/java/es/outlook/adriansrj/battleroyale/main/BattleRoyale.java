package es.outlook.adriansrj.battleroyale.main;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.configuration.ConfigurationHandler;
import es.outlook.adriansrj.battleroyale.data.DataStorageHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.enums.EnumPluginHandler;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobby;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobbyHandler;
import es.outlook.adriansrj.battleroyale.metrics.Metrics;
import es.outlook.adriansrj.battleroyale.schedule.ScheduledExecutorPool;
import es.outlook.adriansrj.battleroyale.world.chunk.EmptyChunkGenerator;
import es.outlook.adriansrj.core.dependency.MavenDependency;
import es.outlook.adriansrj.core.dependency.MavenDependencyRepository;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.plugin.Plugin;
import es.outlook.adriansrj.core.plugin.PluginAdapter;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import org.apache.commons.io.FileDeleteStrategy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.Objects;

/**
 * Battle royale plugin main class.
 *
 * @author AdrianSR / 22/08/2021 / Time: 05:53 p. m.
 */
public final class BattleRoyale extends PluginAdapter {
	
	public static BattleRoyale getInstance ( ) {
		return Plugin.getPlugin ( BattleRoyale.class );
	}
	
	@Override
	public MavenDependencyRepository[] getLibraryRepositories ( ) {
		return new MavenDependencyRepository[] {
				new MavenDependencyRepository ( "jitpack" , "https://jitpack.io/" )
		};
	}
	
	@Override
	public MavenDependency[] getLibraries ( ) {
		return new MavenDependency[] {
				new MavenDependency ( "com.zaxxer:HikariCP:5.0.0" ) ,
				new MavenDependency ( "org.mongodb:mongodb-driver-sync:4.2.1" ) ,
				new MavenDependency ( "net.kyori:adventure-nbt:4.9.2" ) ,
				new MavenDependency ( "com.github.Querz:NBT:6.1" ) ,
				new MavenDependency ( "xyz.xenondevs:particle:1.6.4" ) ,
		};
	}
	
	@Override
	protected boolean setUp ( ) {
		ConsoleUtil.sendPluginMessage (
				ChatColor.GREEN , "BattleRoyale enabled!" , this );
		
		// cleaning temp folder
		deleteTempFolder ( );
		return true;
	}
	
	@Override
	protected boolean setUpHandlers ( ) {
		for ( EnumPluginHandler handler : EnumPluginHandler.values ( ) ) {
			if ( handler.canInitialize ( ) && !initialize ( handler ) ) {
				return false;
			}
		}
		
		// metrics
		if ( EnumMainConfiguration.METRICS_ENABLE.getAsBoolean ( ) ) {
			new Metrics ( this , 4054 );
		}
		
		return true;
	}
	
	private boolean initialize ( EnumPluginHandler handler ) {
		try {
			PluginHandler instance = handler.getHandlerClass ( )
					.getConstructor ( BattleRoyale.class ).newInstance ( this );
			
			// required by ConfigurationHandler handlers.
			if ( instance instanceof ConfigurationHandler ) {
				( ( ConfigurationHandler ) instance ).initialize ( );
			}
			
			return true;
		} catch ( Exception ex ) {
			ex.printStackTrace ( );
			return false;
		}
	}
	
	@Override
	protected boolean setUpConfig ( ) {
		// making sure required directories exists
		for ( EnumDirectory folder : EnumDirectory.values ( ) ) {
			if ( folder.isRequired ( ) ) {
				folder.getDirectory ( ).mkdirs ( );
			}
		}
		return true;
	}
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator ( String world_name , String id ) {
		// returns an EmptyChunkGenerator if the world was
		// generated from the arena world generator.
		World world = Bukkit.getWorld ( world_name );
		
		if ( world != null && Objects.equals ( world.getWorldFolder ( ).getParentFile ( ) ,
											   EnumDirectory.BATTLEFIELD_TEMP_DIRECTORY.getDirectory ( ) ) ) {
			return new EmptyChunkGenerator ( );
		} else {
			return super.getDefaultWorldGenerator ( world_name , id );
		}
	}
	
	@Override
	public void onDisable ( ) {
		// stopping arenas
		BattleRoyaleLobby lobby = BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( );
		Location          spawn = lobby.getCustomSpawn ( );
		
		if ( spawn == null ) {
			spawn = lobby.getWorld ( ).getSpawnLocation ( );
		}
		
		for ( BattleRoyaleArena arena : BattleRoyaleArenaHandler.getInstance ( ).getArenas ( ) ) {
			for ( org.bukkit.entity.Player player : arena.getWorld ( ).getPlayers ( ) ) {
				// sending back to lobby
				player.teleport ( spawn );
			}
			
			if ( arena.getState ( ) != EnumArenaState.STOPPED ) {
				arena.stop ( );
			}
			
			Bukkit.unloadWorld ( arena.getWorld ( ) , false );
		}
		
		// closing database connection.
		try {
			DataStorageHandler.getInstance ( ).dispose ( );
		} catch ( Exception ex ) {
			ex.printStackTrace ( );
		}
		
		// shutting down scheduled executors
		ScheduledExecutorPool.getInstance ( ).clear ( );
		
		// cleaning temp folder
		deleteTempFolder ( );
	}
	
	private void deleteTempFolder ( ) {
		File folder = EnumDirectory.BATTLEFIELD_TEMP_DIRECTORY.getDirectory ( );
		
		try {
			if ( folder.exists ( ) ) {
				FileDeleteStrategy.FORCE.delete ( folder );
			}
		} catch ( Exception ex ) {
			// ignored exception
		}
	}
}