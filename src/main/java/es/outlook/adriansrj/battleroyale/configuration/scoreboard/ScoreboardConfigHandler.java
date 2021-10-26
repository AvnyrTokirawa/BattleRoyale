package es.outlook.adriansrj.battleroyale.configuration.scoreboard;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.scoreboard.ScoreboardConfiguration;
import es.outlook.adriansrj.battleroyale.scoreboard.ScoreboardConfigurationRegistry;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.file.filter.YamlFileFilter;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author AdrianSR / 12/09/2021 / 12:56 p. m.
 */
public final class ScoreboardConfigHandler extends ConfigurationHandler {
	
	private static final ScoreboardConfiguration DEFAULT_SCOREBOARD_CONFIGURATION = new ScoreboardConfiguration (
			// lobby
			StringUtil.concatenate ( ChatColor.GOLD , ChatColor.BOLD ) + "Arena %br_arena_name%" ,
			Arrays.asList (
					"" ,
					ChatColor.WHITE + "%br_arena_autostart_state%" ,
					"" ,
					ChatColor.WHITE + "Server: " + ChatColor.GREEN + "My Server" ,
					"" ,
					ChatColor.YELLOW + "www.SpigotMC.org"
			) ,
			
			// game
			StringUtil.concatenate ( ChatColor.GOLD , ChatColor.BOLD ) + "Arena %br_arena_name%" ,
			Arrays.asList (
					ChatColor.GRAY + "%br_date%" ,
					"" ,
					"%br_arena_border_state%" ,
					"" ,
					"%br_arena_left%/%br_arena_limit%" ,
					"" ,
					"%br_player_safecompass_0%" ,
					"%br_player_safecompass_1%" ,
					"%br_player_safecompass_2% " + ChatColor.RESET + ChatColor.GREEN + "%br_player_safe%" ,
					"" ,
					"%br_team_members_formatted%" ,
					"" ,
					ChatColor.YELLOW + "www.SpigotMC.org"
			)
	);
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public ScoreboardConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public void initialize ( ) {
		File folder       = EnumDirectory.SCOREBOARD_DIRECTORY.getDirectoryMkdirs ( );
		File default_file = new File ( folder , Constants.DEFAULT_YAML_FILE_NAME );
		
		// saving default configuration
		if ( !default_file.exists ( ) ) {
			try {
				if ( default_file.createNewFile ( ) ) {
					DEFAULT_SCOREBOARD_CONFIGURATION.save ( default_file );
				} else {
					throw new IllegalStateException ( "couldn't save default scoreboard configuration file" );
				}
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
		
		// then loading
		loadConfiguration ( );
	}
	
	@Override
	public void loadConfiguration ( ) {
		File folder = EnumDirectory.SCOREBOARD_DIRECTORY.getDirectoryMkdirs ( );
		
		for ( File file : Objects.requireNonNull ( folder.listFiles ( new YamlFileFilter ( ) ) ) ) {
			ScoreboardConfigurationRegistry.getInstance ( ).registerConfiguration (
					FilenameUtil.getBaseName ( file ) , ScoreboardConfiguration.of ( file ) );
		}
	}
	
	@Override
	public void save ( ) {
		// nothing to do here
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}