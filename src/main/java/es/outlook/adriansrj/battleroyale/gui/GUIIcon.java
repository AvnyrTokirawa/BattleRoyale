package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GUI icon configuration.
 *
 * @author AdrianSR / 09/08/2021 / Time: 11:59 a. m.
 */
public abstract class GUIIcon implements Configurable {
	
	public static GUIIcon of ( ConfigurationSection section ) {
		GUIIconType type = GUIIconTypeRegistry.getByIdentifier (
				section.getString ( TYPE_KEY , StringUtil.EMPTY ) );
		
		return type != null ? type.load ( section ) : null;
	}
	
	protected static final String NAME_KEY                = "name";
	protected static final String TYPE_KEY                = "type";
	protected static final String DISPLAY_NAME_FORMAT_KEY = "display-name-format";
	protected static final String DESCRIPTION_FORMAT_KEY  = "description-format";
	
	@ConfigurableEntry ( key = NAME_KEY, comment = "name of this icon." +
			"\nthe name of the section" +
			"\nwill be used if not specified." )
	protected       String          name;
	protected       GUIIconType     type;
	protected       String          display_name_format;
	protected final List < String > description_format = new ArrayList <> ( );
	
	protected GUIIcon ( String name , GUIIconType type , String display_name_format ,
			List < String > description_format ) {
		this.name                = name;
		this.type                = type;
		this.display_name_format = StringUtil.translateAlternateColorCodes ( display_name_format );
		
		this.description_format.clear ( );
		this.description_format.addAll (
				StringUtil.translateAlternateColorCodes ( new ArrayList <> ( description_format ) ) );
	}
	
	protected GUIIcon ( String name , GUIIconType type , String display_name_format ,
			String... description_format ) {
		this ( name , type , display_name_format , Arrays.asList ( description_format ) );
	}
	
	protected GUIIcon ( GUIIconType type ) {
		this ( StringUtil.EMPTY , type , StringUtil.EMPTY );
	}
	
	protected GUIIcon ( ) {
		// to be loaded
	}
	
	public String getName ( ) {
		return name;
	}
	
	public GUIIconType getType ( ) {
		return type;
	}
	
	public String getDisplayNameFormat ( ) {
		return display_name_format;
	}
	
	public List < String > getDescriptionFormat ( ) {
		return description_format;
	}
	
	public GUIIconInstance createInstance ( GUIInstance gui , Player player ) {
		// TODO: set placeholders to both display name and description
		return createInstance (
				gui , player ,
				// display name
				StringUtil.defaultIfBlank ( display_name_format , StringUtil.EMPTY ) ,
				// description
				description_format );
	}
	
	protected abstract GUIIconInstance createInstance ( GUIInstance gui , Player player ,
			String display_name_format , List < String > description_format );
	
	@Override
	public GUIIcon load ( ConfigurationSection section ) {
		// this will load the name
		loadEntries ( section );
		// if the name is not explicitly specified,
		// then we will take the name of the section
		// as the name of this icon.
		this.name = section.getName ( ).trim ( );
		
		// loading type
		this.type = GUIIconTypeRegistry.getByIdentifier ( section.getString ( TYPE_KEY , StringUtil.EMPTY ) );
		
		this.display_name_format = StringUtil.translateAlternateColorCodes (
				section.getString ( DISPLAY_NAME_FORMAT_KEY , StringUtil.EMPTY ) );
		
		this.description_format.clear ( );
		this.description_format.addAll (
				StringUtil.translateAlternateColorCodes ( section.getStringList ( DESCRIPTION_FORMAT_KEY ) ) );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = saveEntries ( section );
		
		save += YamlUtil.setNotEqual ( section , TYPE_KEY , type.getIdentifier ( ) ) ? 1 : 0;
		save += YamlUtil.setNotEqual ( section , DISPLAY_NAME_FORMAT_KEY ,
									   StringUtil.untranslateAlternateColorCodes ( display_name_format ) ) ? 1 : 0;
		save += YamlUtil.setNotEqual ( section , DESCRIPTION_FORMAT_KEY ,
									   StringUtil.untranslateAlternateColorCodes ( description_format ) ) ? 1 : 0;
		return save;
	}
	
	@Override
	public boolean isValid ( ) {
		return StringUtil.isNotBlank ( name ) && type != null;
	}
	
	@Override
	public boolean isInvalid ( ) {
		return !isValid ( );
	}
}