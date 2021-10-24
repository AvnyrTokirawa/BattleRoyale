package es.outlook.adriansrj.battleroyale.configuration.arena;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaConfiguration;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.battlefield.Battlefield;
import es.outlook.adriansrj.battleroyale.configuration.ConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.game.mode.simple.SimpleBattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.schedule.ScheduledExecutorPool;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.battleroyale.util.mode.BattleRoyaleModeUtil;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.file.filter.YamlFileFilter;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * @author AdrianSR / 03/09/2021 / 02:30 p. m.
 */
public final class BattleRoyaleArenasConfigHandler extends ConfigurationHandler {
	
	private static final ExecutorService                EXECUTOR_SERVICE;
	private static final BattleRoyaleArenaConfiguration EXAMPLE_ARENA_CONFIGURATION;
	
	static {
		EXECUTOR_SERVICE            = ScheduledExecutorPool.getInstance ( ).getWorkStealingPool ( );
		EXAMPLE_ARENA_CONFIGURATION = new BattleRoyaleArenaConfiguration (
				"battlefield name here" ,
				"world-1" ,
				"Duos.yml" ,
				Constants.DEFAULT_YAML_FILE_NAME ,
				null ,
				true ,
				// auto-start
				15 ,
				2 ,
				5 ,
				Duration.ofSeconds ( 15 ) ,
				// restart
				Duration.ofSeconds ( 15 )
		);
	}
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public BattleRoyaleArenasConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public void initialize ( ) {
		File folder       = EnumDirectory.ARENA_DIRECTORY.getDirectory ( );
		File example_file = new File ( folder , "example.yml" );
		
		// saving example configuration
		if ( !folder.exists ( ) ) {
			folder.mkdirs ( );
			
			try {
				if ( example_file.createNewFile ( ) ) {
					EXAMPLE_ARENA_CONFIGURATION.save ( example_file );
				} else {
					throw new IllegalStateException ( "couldn't save default arena configuration file" );
				}
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
		
		// saving default modes
		if ( !EnumDirectory.MODE_DIRECTORY.getDirectory ( ).exists ( ) ) {
			saveDefaultMode ( "Solo" , new SimpleBattleRoyaleMode.Builder ( )
					.maximumPlayerPerTeam ( 1 )
					.maximumTeams ( 0 ) // no team limit
					.reviving ( false )
					// team creation/selection must be disabled
					.teamCreation ( false )
					.teamSelection ( false )
					.build ( ) );
			
			saveDefaultMode ( "Duos" , new SimpleBattleRoyaleMode.Builder ( )
					.maximumPlayerPerTeam ( 2 )
					.maximumTeams ( 0 ) // no team limit
					.reviving ( true ) // reviving
					.teamCreation ( true )
					.teamSelection ( true )
					.autofill ( true )
					.build ( ) );
			
			saveDefaultMode ( "Squads" , new SimpleBattleRoyaleMode.Builder ( )
					.maximumPlayerPerTeam ( 4 )
					.maximumTeams ( 0 ) // no team limit
					.reviving ( true ) // reviving
					.teamCreation ( true )
					.teamSelection ( true )
					.autofill ( true )
					.build ( ) );
			
			saveDefaultMode ( "50vs50" , new SimpleBattleRoyaleMode.Builder ( )
					// 50 vs 50 (two teams of 50 members each)
					.maximumTeams ( 2 )
					.maximumPlayerPerTeam ( 50 )
					// reviving not enabled, but respawn instead
					.reviving ( false )
					.respawn ( true )
					// team creation must be disabled.
					.teamCreation ( false )
					.teamSelection ( true )
					.autofill ( true )
					.redeploy ( true )
					.build ( ) );
		}
		
		// then loading configurations
		loadConfiguration ( );
	}
	
	@Override
	public void loadConfiguration ( ) {
		File folder = EnumDirectory.ARENA_DIRECTORY.getDirectoryMkdirs ( );
		
		for ( File file : Objects.requireNonNull ( folder.listFiles ( new YamlFileFilter ( ) ) ) ) {
			String                         name          = FilenameUtil.getBaseName ( file ).trim ( );
			BattleRoyaleArenaConfiguration configuration = BattleRoyaleArenaConfiguration.of ( file );
			Battlefield                    battlefield   = configuration.getBattlefield ( );
			BattleRoyaleMode               mode          = configuration.getMode ( );
			
			// logging invalid configurations
			if ( configuration.isInvalid ( ) ) {
				if ( battlefield == null ) {
					if ( StringUtil.isBlank ( configuration.getBattlefieldName ( ) ) ) {
						logInvalidConfiguration ( name , "Battlefield not specified" );
					} else {
						logInvalidConfiguration (
								name , "Unknown battlefield '" + configuration.getBattlefieldName ( ) + "'" );
					}
				} else if ( mode == null || mode.isInvalid ( ) ) {
					if ( StringUtil.isBlank ( configuration.getModeFilename ( ) ) ) {
						logInvalidConfiguration (
								name , "Mode file couldn't be found, or has an invalid configuration " +
										"(" + configuration.getModeFilename ( ) + ")" );
					} else {
						logInvalidConfiguration (
								name , "Mode not specified" );
					}
				} else if ( StringUtil.isBlank ( configuration.getWorldName ( ) ) ) {
					logInvalidConfiguration ( name , "World is invalid or not specified" );
				}
				
				continue;
			}
			
			// in case respawn is enabled, but there is not a
			// kill limit, the arena will not end, so let's
			// print a warning message.
			if ( mode.isRespawnEnabled ( )
					&& !BattleRoyaleModeUtil.isDeterminedByKills ( mode ) ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , "It seems that the arena '" + name
						+ "' will never end: respawning is enabled, but there is not a kill limit." , plugin );
			}
			
			// must load arenas asynchronously as the server could crash
			// since the shapes of the arenas could probably be huge.
			ConsoleUtil.sendPluginMessage ( ChatColor.YELLOW , "Loading arena '" + name
					+ "' ..." , plugin );
			
			EXECUTOR_SERVICE.execute ( ( ) -> BattleRoyaleArenaHandler.getInstance ( ).createArena (
					name , configuration , arena -> {
						ConsoleUtil.sendPluginMessage ( ChatColor.GREEN , "Arena '"
								+ arena.getName ( ) + "' successfully loaded." , plugin );
						
						// preparing
						if ( !arena.isPrepared ( ) ) {
							arena.prepare ( );
						}
					} ) );
		}
	}
	
	private void saveDefaultMode ( String name , SimpleBattleRoyaleMode mode ) {
		File file = new File ( EnumDirectory.MODE_DIRECTORY.getDirectoryMkdirs ( ) , name + ".yml" );
		
		if ( !file.exists ( ) ) {
			try {
				file.createNewFile ( );
				
				YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( file );
				mode.save ( yaml );
				yaml.save ( file );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	private void logInvalidConfiguration ( String arena , String message ) {
		ConsoleUtil.sendPluginMessage (
				ChatColor.RED , "The configuration of the arena '"
						+ arena + "' is invalid: " + message , plugin );
	}
	
	@Override
	public void save ( ) {
		// nothing to do here
	}
}