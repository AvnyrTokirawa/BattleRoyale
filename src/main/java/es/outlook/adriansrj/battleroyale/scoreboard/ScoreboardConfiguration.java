package es.outlook.adriansrj.battleroyale.scoreboard;

import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
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
	
	protected static final String LOBBY_TITLE_KEY    = "lobby.title";
	protected static final String LOBBY_ELEMENTS_KEY = "lobby.elements";
	protected static final String GAME_TITLE_KEY     = "game.title";
	protected static final String GAME_ELEMENTS_KEY  = "game.elements";
	
	public static ScoreboardConfiguration of ( ConfigurationSection section ) {
		return new ScoreboardConfiguration ( ).load ( section );
	}
	
	public static ScoreboardConfiguration of ( File file ) {
		return of ( YamlConfigurationComments.loadConfiguration ( file ) );
	}
	
	protected       String          lobby_title;
	protected       String          game_title;
	protected final List < String > lobby_elements = new ArrayList <> ( );
	protected final List < String > game_elements  = new ArrayList <> ( );
	
	public ScoreboardConfiguration ( String lobby_title , List < String > lobby_elements ,
			String game_title , List < String > game_elements ) {
		this.lobby_title = StringUtil.translateAlternateColorCodes ( lobby_title );
		this.game_title  = StringUtil.translateAlternateColorCodes ( game_title );
		
		this.lobby_elements.addAll ( StringUtil.translateAlternateColorCodes ( lobby_elements ) );
		this.game_elements.addAll ( StringUtil.translateAlternateColorCodes ( game_elements ) );
	}
	
	public ScoreboardConfiguration ( String lobby_title , String game_title ) {
		this.lobby_title = StringUtil.translateAlternateColorCodes ( lobby_title );
		this.game_title  = StringUtil.translateAlternateColorCodes ( game_title );
	}
	
	public ScoreboardConfiguration ( List < String > lobby_elements , List < String > game_elements ) {
		this.lobby_elements.addAll ( StringUtil.translateAlternateColorCodes ( lobby_elements ) );
		this.game_elements.addAll ( StringUtil.translateAlternateColorCodes ( game_elements ) );
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
	
	public List < String > getLobbyElements ( ) {
		return lobby_elements;
	}
	
	public List < String > getGameElements ( ) {
		return game_elements;
	}
	
	@Override
	public ScoreboardConfiguration load ( ConfigurationSection section ) {
		// lobby
		this.lobby_title = loadTitle ( section , LOBBY_TITLE_KEY );
		this.lobby_elements.clear ( );
		this.lobby_elements.addAll ( loadElements ( section , LOBBY_ELEMENTS_KEY ) );
		
		// game
		this.game_title = loadTitle ( section , GAME_TITLE_KEY );
		this.game_elements.clear ( );
		this.game_elements.addAll ( loadElements ( section , GAME_ELEMENTS_KEY ) );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = 0;
		
		// lobby
		save += saveTitle ( section , LOBBY_TITLE_KEY , lobby_title );
		save += saveElements ( section , LOBBY_ELEMENTS_KEY , lobby_elements );
		
		// game
		save += saveTitle ( section , GAME_TITLE_KEY , game_title );
		save += saveElements ( section , GAME_ELEMENTS_KEY , game_elements );
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