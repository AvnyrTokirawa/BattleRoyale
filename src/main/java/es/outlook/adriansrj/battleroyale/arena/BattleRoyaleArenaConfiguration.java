package es.outlook.adriansrj.battleroyale.arena;

import es.outlook.adriansrj.battleroyale.battlefield.Battlefield;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldRegistry;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.enums.EnumScoreboardPlugin;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleModeManager;
import es.outlook.adriansrj.battleroyale.scoreboard.ScoreboardConfiguration;
import es.outlook.adriansrj.battleroyale.scoreboard.ScoreboardConfigurationRegistry;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.configurable.duration.ConfigurableDuration;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/**
 * {@link BattleRoyaleArena} configuration.
 *
 * @author AdrianSR / 09/10/2021 / 06:54 p. m.
 */
public class BattleRoyaleArenaConfiguration implements Configurable {
	
	protected static final String DESCRIPTION_KEY                   = "description";
	protected static final String BATTLEFIELD_KEY                   = "battlefield";
	protected static final String MODE_KEY                          = "mode";
	protected static final String SCOREBOARD_KEY                    = "scoreboard.name";
	protected static final String SCOREBOARD_PLUGIN_KEY             = "scoreboard.plugin";
	protected static final String AUTO_START_ENABLE_KEY             = "auto-start.enable";
	protected static final String AUTO_START_REQUIRED_PLAYERS_KEY   = "auto-start.required.players";
	protected static final String AUTO_START_REQUIRED_TEAMS_KEY     = "auto-start.required.teams";
	protected static final String AUTO_START_COUNTDOWN_DISPLAY_KEY  = "auto-start.countdown.display";
	protected static final String AUTO_START_COUNTDOWN_DURATION_KEY = "auto-start.countdown.duration";
	protected static final String RESTART_COUNTDOWN_DURATION_KEY    = "restart.countdown.duration";
	protected static final String RESTART_SERVER_ENABLE_KEY         = "restart.restart-server.enable";
	protected static final String RESTART_SERVER_COMMAND_KEY        = "restart.restart-server.command";
	
	protected static final BattleRoyaleModeManager MODE_MANAGER = new BattleRoyaleModeManager ( );
	
	public static BattleRoyaleArenaConfiguration of ( ConfigurationSection section ) {
		return new BattleRoyaleArenaConfiguration ( ).load ( section );
	}
	
	public static BattleRoyaleArenaConfiguration of ( File file ) {
		return of ( YamlConfigurationComments.loadConfiguration ( file ) );
	}
	
	// description
	@ConfigurableEntry ( key = DESCRIPTION_KEY,
						 comment = "description of this arena." )
	protected String description;
	
	// battlefield
	@ConfigurableEntry ( key = BATTLEFIELD_KEY,
						 comment = "battlefield on which this arena will take place." )
	protected String      battlefield_name;
	protected Battlefield battlefield;
	
	// mode
	@ConfigurableEntry ( key = MODE_KEY,
						 comment = "file name of the mode to be played in this arena" )
	protected String           mode_filename;
	protected BattleRoyaleMode mode;
	
	// scoreboard
	@ConfigurableEntry ( key = SCOREBOARD_KEY,
						 comment = "file name of the scoreboard to be displayed in this arena." )
	protected String               scoreboard;
	protected EnumScoreboardPlugin scoreboard_plugin;
	
	// auto-start
	@ConfigurableEntry ( key = AUTO_START_ENABLE_KEY,
						 comment = "if enabled, the arena will start automatically when the\n" +
								 "minimum number of players/teams is reached." )
	protected boolean autostart_enable;
	
	@ConfigurableEntry ( key = AUTO_START_REQUIRED_PLAYERS_KEY,
						 comment = "the required number of players to start arena." )
	protected int autostart_required_players;
	
	@ConfigurableEntry ( key = AUTO_START_REQUIRED_TEAMS_KEY,
						 comment = "the required number of teams to start arena." )
	protected int autostart_required_teams;
	
	@ConfigurableEntry ( key = AUTO_START_COUNTDOWN_DISPLAY_KEY,
						 comment = "the count from which titles start to show." )
	protected int autostart_countdown_display;
	
	@ConfigurableEntry ( key = AUTO_START_COUNTDOWN_DURATION_KEY,
						 comment = "the duration of the countdown when automatically starting." )
	protected ConfigurableDuration autostart_countdown_duration;
	
	// restart
	@ConfigurableEntry ( key = RESTART_COUNTDOWN_DURATION_KEY,
						 comment = "the duration of the countdown when restarting this arena." )
	protected ConfigurableDuration restart_countdown_duration;
	
	@ConfigurableEntry ( key = RESTART_SERVER_ENABLE_KEY,
						 comment = "if true, the server will be restarted when this arena ends.\n" +
								 "this option is useful if you only want one\n" +
								 "arena in the server (bungeemode)" )
	protected boolean restart_server;
	
	@ConfigurableEntry ( key = RESTART_SERVER_COMMAND_KEY,
						 comment = "the command to restart the server" )
	protected String restart_server_command;
	
	public BattleRoyaleArenaConfiguration ( String description , String battlefield_name , String mode_filename ,
			String scoreboard , EnumScoreboardPlugin scoreboard_plugin , boolean autostart_enable ,
			int autostart_required_players , int autostart_required_teams ,
			int autostart_countdown_display , Duration autostart_countdown_duration ,
			Duration restart_countdown_duration , boolean restart_server , String restart_server_command ) {
		this.description                  = description;
		this.battlefield_name             = battlefield_name;
		this.mode_filename                = mode_filename;
		this.scoreboard                   = scoreboard;
		this.scoreboard_plugin            = scoreboard_plugin;
		this.autostart_enable             = autostart_enable;
		this.autostart_required_players   = autostart_required_players;
		this.autostart_required_teams     = autostart_required_teams;
		this.autostart_countdown_display  = autostart_countdown_display;
		this.autostart_countdown_duration = new ConfigurableDuration ( autostart_countdown_duration );
		this.restart_countdown_duration   = new ConfigurableDuration ( restart_countdown_duration );
		this.restart_server               = restart_server;
		this.restart_server_command       = restart_server_command;
	}
	
	public BattleRoyaleArenaConfiguration ( BattleRoyaleArenaConfiguration copy ) {
		this.description                  = copy.description;
		this.battlefield_name             = copy.battlefield_name;
		this.battlefield                  = copy.battlefield;
		this.mode_filename                = copy.mode_filename;
		this.mode                         = copy.mode;
		this.scoreboard                   = copy.scoreboard;
		this.scoreboard_plugin            = copy.scoreboard_plugin;
		this.autostart_enable             = copy.autostart_enable;
		this.autostart_required_players   = copy.autostart_required_players;
		this.autostart_required_teams     = copy.autostart_required_teams;
		this.autostart_countdown_display  = copy.autostart_countdown_display;
		this.autostart_countdown_duration = copy.autostart_countdown_duration;
		this.restart_countdown_duration   = copy.restart_countdown_duration;
		this.restart_server               = copy.restart_server;
		this.restart_server_command       = copy.restart_server_command;
	}
	
	public BattleRoyaleArenaConfiguration ( ) {
		// to be loaded
	}
	
	// -------- description configuration
	
	public String getDescription ( ) {
		return description != null ? StringUtil.translateAlternateColorCodes ( description ) : StringUtil.EMPTY;
	}
	
	// -------- battlefield configuration
	
	public String getBattlefieldName ( ) {
		return battlefield != null ? battlefield.getName ( ) : battlefield_name;
	}
	
	public Battlefield getBattlefield ( ) {
		if ( battlefield == null && StringUtil.isNotBlank ( battlefield_name ) ) {
			this.battlefield = BattlefieldRegistry.getInstance ( ).getBattlefield ( battlefield_name );
		}
		return battlefield;
	}
	
	public void setBattlefield ( String battlefield_name ) {
		this.battlefield_name = Validate.notBlank (
				battlefield_name , "battlefield name cannot be null/empty" ).trim ( );
		this.battlefield      = null;
	}
	
	public void setBattlefield ( Battlefield battlefield ) {
		this.battlefield      = battlefield;
		this.battlefield_name = null;
	}
	
	// -------- mode configuration
	
	/**
	 * Gets the file name of the mode to be played in the arena.
	 * <br>
	 * Note that <b>null</b> will be returned if the mode was not
	 * loaded from a file.
	 *
	 * @return the file name of the mode to be played in the arena.
	 */
	public String getModeFilename ( ) {
		return mode != null ? null : mode_filename;
	}
	
	/**
	 * Gets the mode to be played in the arena.
	 *
	 * @return the mode to be played in the arena.
	 */
	public BattleRoyaleMode getMode ( ) throws FileNotFoundException {
		if ( mode == null && StringUtil.isNotBlank ( mode_filename ) ) {
			this.mode = MODE_MANAGER.load ( new File (
					EnumDirectory.MODE_DIRECTORY.getDirectory ( ) , mode_filename ) );
		}
		return mode;
	}
	
	public void setMode ( BattleRoyaleMode mode ) {
		this.mode          = mode;
		this.mode_filename = null;
	}
	
	public void setMode ( String mode_filename ) {
		this.mode_filename = mode_filename;
		this.mode          = null;
	}
	
	// -------- scoreboard configuration
	
	/**
	 * Gets the configuration file name of the scoreboard that is to
	 * be displayed to the players in the arena.
	 * <br>
	 * If the scoreboard plugin compatibility is enabled, then what this
	 * method will actually return is a <b>reference to the scoreboard provided
	 * by the respective scoreboard plugin</b>.
	 *
	 * @return configuration file name of the scoreboard to be displayed.
	 */
	public String getScoreboard ( ) {
		return scoreboard;
	}
	
	/**
	 * Gets the scoreboard of the configuration that is to be displayed to the players in the arena.
	 * <br>
	 * If {@link #scoreboard} is <b>blank</b>, or there is no scoreboard configurations
	 * registered with that name, then the next valid scoreboard configuration registered in
	 * the {@link ScoreboardConfigurationRegistry} will be used. In case there is no valid loot
	 * configurations, then <b>null</b> will be returned.
	 *
	 * @return scoreboard of the configuration that is to be displayed, or <b>null</b>.
	 */
	public ScoreboardConfiguration getScoreboardConfiguration ( ) {
		ScoreboardConfiguration configuration = this.scoreboard != null ? ScoreboardConfigurationRegistry
				.getInstance ( ).getConfiguration ( FilenameUtil.getBaseName ( this.scoreboard ) ) : null;
		
		// finding out the next valid
		if ( configuration == null ) {
			configuration = ScoreboardConfigurationRegistry.getInstance ( ).getConfigurations ( ).values ( ).stream ( )
					.filter ( Objects :: nonNull ).filter ( ScoreboardConfiguration :: isValid )
					.findAny ( ).orElse ( null );
		}
		
		return configuration;
	}
	
	public void setScoreboard ( String scoreboard ) {
		this.scoreboard = scoreboard;
	}
	
	/**
	 * Gets the scoreboard plugin to enable compatibility
	 * with.
	 * <br>
	 * Note that <b>null</b> will be returned if no scoreboard
	 * compatibility is to be enabled.
	 *
	 * @return scoreboard plugin to enable compatibility with.
	 */
	public EnumScoreboardPlugin getScoreboardPlugin ( ) {
		return scoreboard_plugin;
	}
	
	public void setScoreboardPlugin ( EnumScoreboardPlugin scoreboard_plugin ) {
		this.scoreboard_plugin = scoreboard_plugin;
	}
	
	// -------- auto-start configuration
	
	public boolean isAutostartEnabled ( ) {
		return autostart_enable;
	}
	
	public void setAutostart ( boolean autostart ) {
		this.autostart_enable = autostart;
	}
	
	/**
	 * Gets the required number of players
	 * to start the arena.
	 *
	 * @return required number of players to start.
	 */
	public int getAutostartRequiredPlayers ( ) {
		return autostart_required_players;
	}
	
	public void setAutostartRequiredPlayers ( int required_players ) {
		this.autostart_required_players = required_players;
	}
	
	/**
	 * Gets the required number of teams
	 * to start the arena.
	 *
	 * @return required number of teams to start.
	 */
	public int getAutostartRequiredTeams ( ) {
		return autostart_required_teams;
	}
	
	public void setAutostartRequiredTeams ( int required_teams ) {
		this.autostart_required_teams = required_teams;
	}
	
	/**
	 * Gets the count from which titles start to show.
	 * <br>
	 * In other words, this value is actually an offset
	 * for the countdown titles.
	 *
	 * @return the count from which titles start to show.
	 */
	public int getAutostartCountdownDisplay ( ) {
		return autostart_countdown_display;
	}
	
	public void setAutostartCountdownDisplay ( int countdown_display ) {
		this.autostart_countdown_display = countdown_display;
	}
	
	public ConfigurableDuration getAutostartCountdownDuration ( ) {
		return autostart_countdown_duration != null
				? new ConfigurableDuration ( autostart_countdown_duration )
				: new ConfigurableDuration ( Duration.ZERO );
	}
	
	public void setAutostartCountdownDuration ( ConfigurableDuration countdown_duration ) {
		this.autostart_countdown_duration = countdown_duration;
	}
	
	// -------- restart configuration
	
	public ConfigurableDuration getRestartCountdownDuration ( ) {
		return restart_countdown_duration != null
				? new ConfigurableDuration ( restart_countdown_duration )
				: new ConfigurableDuration ( Duration.ZERO );
	}
	
	public void setRestartCountdownDuration ( ConfigurableDuration countdown_duration ) {
		this.restart_countdown_duration = countdown_duration;
	}
	
	public boolean isRestartServer ( ) {
		return restart_server;
	}
	
	public void setRestartServer ( boolean restart_server ) {
		this.restart_server = restart_server;
	}
	
	public String getRestartServerCommand ( ) {
		return restart_server_command;
	}
	
	public void setRestartServerCommand ( String restart_command ) {
		this.restart_server_command = restart_command;
	}
	
	@Override
	public BattleRoyaleArenaConfiguration load ( ConfigurationSection section ) {
		loadEntries ( section );
		
		// we're using the setters to clear the instances so its
		// respective getters will recalculate them.
		setBattlefield ( battlefield_name );
		setMode ( mode_filename );
		setScoreboard ( scoreboard );
		
		// loading scoreboard plugin
		this.scoreboard_plugin = EnumReflection.getEnumConstant (
				EnumScoreboardPlugin.class , section.getString ( SCOREBOARD_PLUGIN_KEY , StringUtil.EMPTY ) );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = saveEntries ( section );
		
		// saving scoreboard plugin
		if ( scoreboard_plugin != null ) {
			save += YamlUtil.setNotEqual (
					section , SCOREBOARD_PLUGIN_KEY , scoreboard_plugin.name ( ) ) ? 1 : 0;
		}
		
		return save;
	}
	
	public void save ( File file ) {
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( file );
		
		if ( save ( yaml ) > 0 ) {
			try {
				yaml.save ( file );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	@Override
	public boolean isValid ( ) {
		Battlefield      battlefield = getBattlefield ( );
		BattleRoyaleMode mode;
		
		try {
			mode = getMode ( );
		} catch ( FileNotFoundException ex ) {
			return false;
		}
		
		return battlefield != null && mode != null && mode.isValid ( );
	}
}