package es.outlook.adriansrj.battleroyale.configuration.arena;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.battlefield.Battlefield;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldRegistry;
import es.outlook.adriansrj.battleroyale.configuration.ScalableConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleModeManager;
import es.outlook.adriansrj.battleroyale.game.mode.simple.SimpleBattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author AdrianSR / 03/09/2021 / 02:30 p. m.
 */
public final class BattleRoyaleArenasConfigHandler extends ScalableConfigurationHandler {
	
	private static final ExecutorService EXECUTOR_SERVICE;
	
	static {
		EXECUTOR_SERVICE = Executors.newWorkStealingPool ( );
	}
	
	private final BattleRoyaleModeManager mode_manager;
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public BattleRoyaleArenasConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
		
		this.mode_manager = new BattleRoyaleModeManager ( );
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.ARENAS_CONFIGURATION.getFile ( );
	}
	
	@Override
	public void initialize ( ) {
		super.initialize ( );
		
		// saving default modes
		saveDefaultMode ( "Solo" , new SimpleBattleRoyaleMode.Builder ( )
				.maximumPlayerPerTeam ( 1 )
				.maximumTeams ( 0 ) // no team limit
				.reviving ( false )
				.teamSelection ( false ) // team selection must be disabled
				.build ( ) );
		
		saveDefaultMode ( "Duos" , new SimpleBattleRoyaleMode.Builder ( )
				.maximumPlayerPerTeam ( 2 )
				.maximumTeams ( 0 ) // no team limit
				.reviving ( true ) // reviving
				.teamSelection ( true )
				.autofill ( true )
				.build ( ) );
		
		saveDefaultMode ( "Squads" , new SimpleBattleRoyaleMode.Builder ( )
				.maximumPlayerPerTeam ( 4 )
				.maximumTeams ( 0 ) // no team limit
				.reviving ( true ) // reviving
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
				.teamSelection ( true )
				.autofill ( true )
				.redeploy ( true )
				.build ( ) );
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
	
	@Override
	protected void loadConfiguration ( YamlConfigurationComments yaml ) {
		ConfigurationSection section = yaml.getConfigurationSection ( Constants.ARENAS_KEY );
		
		if ( section != null ) {
			for ( String key : section.getKeys ( false ) ) {
				if ( section.isConfigurationSection ( key ) ) {
					ConfigurationSection arena_section = section.getConfigurationSection ( key );
					
					// mode
					String           mode_file_name = arena_section.getString ( Constants.MODE_KEY );
					BattleRoyaleMode mode           = null;
					
					try {
						if ( StringUtil.isNotBlank ( mode_file_name ) ) {
							mode = mode_manager.load (
									new File ( EnumDirectory.MODE_DIRECTORY.getDirectory ( ) , mode_file_name ) );
						} else {
							logInvalidConfiguration (
									key , "Mode not specified" );
						}
					} catch ( FileNotFoundException e ) {
						logInvalidConfiguration (
								key , "Unknown mode file '" + mode_file_name + "'" );
						continue;
					}
					
					// battlefield
					String battlefield_name = arena_section.getString ( Constants.BATTLEFIELD_KEY );
					Battlefield battlefield = battlefield_name != null ? BattlefieldRegistry.getInstance ( )
							.getBattlefield ( battlefield_name ) : null;
					
					if ( battlefield == null ) {
						if ( StringUtil.isBlank ( battlefield_name ) ) {
							logInvalidConfiguration ( key , "Battlefield not specified" );
						} else {
							logInvalidConfiguration (
									key , "Unknown battlefield '" + battlefield_name + "'" );
						}
						continue;
					}
					
					// world
					String world_name = arena_section.getString ( Constants.WORLD_KEY );
					world_name = world_name != null
							? StringUtil.replaceFileCharacters ( world_name , "-" ) : null;
					
					if ( StringUtil.isBlank ( world_name ) ) {
						logInvalidConfiguration ( key , "World is invalid or not specified" );
						continue;
					}
					
					// must load arenas asynchronously as the server could crash
					// since the shapes of the arenas could probably be huge.
					final String           resulting_name        = key.trim ( );
					final Battlefield      resulting_battlefield = battlefield;
					final BattleRoyaleMode resulting_mode        = mode;
					final String           resulting_world_name  = world_name;
					
					ConsoleUtil.sendPluginMessage ( ChatColor.YELLOW , "Loading arena '" + resulting_name
							+ "' ..." , plugin );
					
					EXECUTOR_SERVICE.execute ( ( ) -> BattleRoyaleArenaHandler.getInstance ( ).createArena (
							resulting_name , resulting_world_name ,
							resulting_battlefield , resulting_mode ,
							arena -> {
								ConsoleUtil.sendPluginMessage ( ChatColor.GREEN , "Arena '"
										+ arena.getName ( ) + "' successfully loaded." , plugin );
								
								// preparing
								if ( !arena.isPrepared ( ) ) {
									arena.prepare ( );
								}
							} ) );
				}
			}
		}
	}
	
	protected void logInvalidConfiguration ( String arena , String message ) {
		ConsoleUtil.sendPluginMessage (
				ChatColor.RED , "The configuration of the arena '"
						+ arena + "' is invalid: " + message , plugin );
	}
	
	@Override
	protected int saveDefaultConfiguration ( YamlConfigurationComments yaml ) {
		if ( yaml.getConfigurationSection ( Constants.ARENAS_KEY ) == null ) {
			yaml.createSection ( Constants.ARENAS_KEY );
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	protected int save ( YamlConfigurationComments yaml ) {
		return 0;
	}
}