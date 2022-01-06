package es.outlook.adriansrj.battleroyale.scoreboard;

import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Scoreboard} configuration.
 *
 * @author AdrianSR / 09/10/2021 / 09:08 a. m.
 */
public class ScoreboardConfiguration implements Configurable {
	
	protected static final String LOBBY_TITLE_KEY        = "lobby.title";
	protected static final String LOBBY_ELEMENTS_KEY     = "lobby.elements";
	protected static final String GAME_TITLE_KEY         = "game.title";
	protected static final String GAME_ELEMENTS_KEY      = "game.elements";
	protected static final String SPECTATOR_TITLE_KEY    = "spectator.title";
	protected static final String SPECTATOR_ELEMENTS_KEY = "spectator.elements";
	protected static final String PLUGIN_SUPPORT_KEY     = "scoreboard-plugin-support";
	
	public static ScoreboardConfiguration of ( ConfigurationSection section ) {
		return new ScoreboardConfiguration ( ).load ( section );
	}
	
	public static ScoreboardConfiguration of ( File file ) {
		return of ( YamlConfigurationComments.loadConfiguration ( file ) );
	}
	
	protected       String          lobby_title;
	protected       String          game_title;
	protected       String          spectator_title;
	protected final List < String > lobby_elements     = new ArrayList <> ( );
	protected final List < String > game_elements      = new ArrayList <> ( );
	protected final List < String > spectator_elements = new ArrayList <> ( );
	
	// featherboard compatibility
	@ConfigurableEntry ( key = PLUGIN_SUPPORT_KEY + ".featherboard.lobby-scoreboard" )
	protected String featherboard_lobby;
	@ConfigurableEntry ( key = PLUGIN_SUPPORT_KEY + ".featherboard.game-scoreboard" )
	protected String featherboard_game;
	@ConfigurableEntry ( key = PLUGIN_SUPPORT_KEY + ".featherboard.spectator-scoreboard" )
	protected String featherboard_spectator;
	
	public ScoreboardConfiguration (
			String lobby_title , List < String > lobby_elements ,
			String game_title , List < String > game_elements ,
			String spectator_title , List < String > spectator_elements ) {
		this.lobby_title     = StringUtil.translateAlternateColorCodes ( lobby_title );
		this.game_title      = StringUtil.translateAlternateColorCodes ( game_title );
		this.spectator_title = StringUtil.translateAlternateColorCodes ( spectator_title );
		
		this.lobby_elements.addAll ( StringUtil.translateAlternateColorCodes ( lobby_elements ) );
		this.game_elements.addAll ( StringUtil.translateAlternateColorCodes ( game_elements ) );
		this.spectator_elements.addAll ( StringUtil.translateAlternateColorCodes ( spectator_elements ) );
	}
	
	public ScoreboardConfiguration ( String lobby_title , String game_title , String spectator_title ) {
		this.lobby_title     = StringUtil.translateAlternateColorCodes ( lobby_title );
		this.game_title      = StringUtil.translateAlternateColorCodes ( game_title );
		this.spectator_title = StringUtil.translateAlternateColorCodes ( spectator_title );
	}
	
	public ScoreboardConfiguration ( List < String > lobby_elements , List < String > game_elements ,
			List < String > spectator_elements ) {
		this.lobby_elements.addAll ( StringUtil.translateAlternateColorCodes ( lobby_elements ) );
		this.game_elements.addAll ( StringUtil.translateAlternateColorCodes ( game_elements ) );
		this.spectator_elements.addAll ( StringUtil.translateAlternateColorCodes ( spectator_elements ) );
	}
	
	public ScoreboardConfiguration ( ) {
		// to be loaded
	}
	
	public String getLobbyTitle ( ) {
		return lobby_title;
	}
	
	public String getGameTitle ( ) {
		return game_title;
	}
	
	public String getSpectatorTitle ( ) {
		return spectator_title;
	}
	
	public List < String > getLobbyElements ( ) {
		return lobby_elements;
	}
	
	public List < String > getGameElements ( ) {
		return game_elements;
	}
	
	public List < String > getSpectatorElements ( ) {
		return spectator_elements;
	}
	
	public String getLobbyFeatherBoard ( ) {
		return featherboard_lobby;
	}
	
	public String getGameFeatherBoard ( ) {
		return featherboard_game;
	}
	
	public String getSpectatorFeatherBoard ( ) {
		return featherboard_spectator;
	}
	
	@Override
	public ScoreboardConfiguration load ( ConfigurationSection section ) {
		loadEntries ( section );
		
		// lobby
		this.lobby_title = loadTitle ( section , LOBBY_TITLE_KEY );
		this.lobby_elements.clear ( );
		this.lobby_elements.addAll ( loadElements ( section , LOBBY_ELEMENTS_KEY ) );
		
		// game
		this.game_title = loadTitle ( section , GAME_TITLE_KEY );
		this.game_elements.clear ( );
		this.game_elements.addAll ( loadElements ( section , GAME_ELEMENTS_KEY ) );
		
		// spectator
		this.spectator_title = loadTitle ( section , SPECTATOR_TITLE_KEY );
		this.spectator_elements.clear ( );
		this.spectator_elements.addAll ( loadElements ( section , SPECTATOR_ELEMENTS_KEY ) );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = saveEntries ( section );
		
		// lobby
		save += saveTitle ( section , LOBBY_TITLE_KEY , lobby_title );
		save += saveElements ( section , LOBBY_ELEMENTS_KEY , lobby_elements );
		
		// game
		save += saveTitle ( section , GAME_TITLE_KEY , game_title );
		save += saveElements ( section , GAME_ELEMENTS_KEY , game_elements );
		
		// spectator
		save += saveTitle ( section , SPECTATOR_TITLE_KEY , spectator_title );
		save += saveElements ( section , SPECTATOR_ELEMENTS_KEY , spectator_elements );
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
		return true;
	}
	
	protected String loadTitle ( ConfigurationSection section , String key ) {
		return StringUtil.translateAlternateColorCodes ( section.getString ( key , StringUtil.EMPTY ) );
	}
	
	protected List < String > loadElements ( ConfigurationSection section , String key ) {
		return StringUtil.translateAlternateColorCodes ( section.getStringList ( key ) );
	}
	
	protected int saveTitle ( ConfigurationSection section , String key , String value ) {
		return ( value != null && YamlUtil.setNotEqual (
				section , key , StringUtil.untranslateAlternateColorCodes ( value ) ) ) ? 1 : 0;
	}
	
	protected int saveElements ( ConfigurationSection section , String key , List < String > value ) {
		return ( value.size ( ) > 0 && YamlUtil.setNotEqual (
				section , key , StringUtil.untranslateAlternateColorCodes ( value ) ) ) ? 1 : 0;
	}
}