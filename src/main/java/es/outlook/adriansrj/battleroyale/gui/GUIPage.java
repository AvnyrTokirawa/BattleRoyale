package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Team GUI page configuration.
 *
 * @author AdrianSR / 09/08/2021 / Time: 12:11 p. m.
 */
public class GUIPage implements Configurable {
	
	protected static final String NAME_KEY  = "name";
	protected static final String INDEX_KEY = "index";
	protected static final String TITLE_KEY = "title";
	protected static final String MODEL_KEY = "model";
	
	public static GUIPage of ( ConfigurationSection section ) {
		return new GUIPage ( ).load ( section );
	}
	
	@ConfigurableEntry ( key = NAME_KEY, comment = "name of this page." +
			"\nthe name of the section" +
			"\nwill be used if not specified." )
	protected String name;
	@ConfigurableEntry ( key = INDEX_KEY, comment = "index of this page" )
	protected int    index = -1;
	@ConfigurableEntry ( key = TITLE_KEY, comment = "title of this page." )
	protected String title;
	@ConfigurableEntry ( key = MODEL_KEY, comment = "mode of this page." +
			"\nthe model determines how" +
			"\nthis page will look." )
	protected String model;
	
	public GUIPage ( String name , int index , String title , String model ) {
		this.name  = name;
		this.index = index;
		this.title = StringUtil.untranslateAlternateColorCodes ( title );
		this.model = model;
	}
	
	public GUIPage ( String name , String title , String model ) {
		this ( name , -1 , title , model );
	}
	
	public GUIPage ( String name , int index , String title ,
			GUIPageModel model ) {
		this ( name , index , title , model.getName ( ) );
	}
	
	public GUIPage ( String name , String title ,
			GUIPageModel model ) {
		this ( name , -1 , title , model );
	}
	
	public GUIPage ( ) {
		// to be loaded
	}
	
	public String getName ( ) {
		return name;
	}
	
	public int getIndex ( ) {
		return index;
	}
	
	public boolean isIndexable ( ) {
		return index >= 0;
	}
	
	public String getTitle ( ) {
		return StringUtil.translateAlternateColorCodes ( title );
	}
	
	public String getModelName ( ) {
		return model;
	}
	
	@Override
	public GUIPage load ( ConfigurationSection section ) {
		// this will load the name, index, title, and the model.
		loadEntries ( section );
		
		// if the name is not explicitly specified,
		// then we will take the name of the section
		// as the name of this page.
		if ( !section.isString ( NAME_KEY ) ) {
			this.name = section.getName ( ).trim ( );
		}
		
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		// this will save the name, index, title, and the model.
		int save = saveEntries ( section );
		
		// we will exclude the index if this is not an
		// indexable page
		if ( !isIndexable ( ) ) {
			section.set ( INDEX_KEY , null );
		}
		
		return save;
	}
	
	@Override
	public boolean isValid ( ) {
		return ( StringUtil.isNotBlank ( name ) || index > 0 ) && StringUtil.isNotBlank ( model );
	}
}