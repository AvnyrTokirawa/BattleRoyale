package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Page model slot.
 *
 * @author AdrianSR / 09/08/2021 / Time: 12:14 p. m.
 */
public class GUIPageModelSlot implements Configurable {
	
	protected static final String INDEX_KEY = "index";
	protected static final String ICON_KEY  = "icon";
	
	protected String name;
	@ConfigurableEntry ( key = INDEX_KEY )
	protected int    index;
	protected String icon_name;
	
	public GUIPageModelSlot ( String name , int index , String icon_name ) {
		this.name      = name;
		this.index     = index;
		this.icon_name = icon_name;
	}
	
	public GUIPageModelSlot ( int index , String icon_name ) {
		this ( "slot-" + index , index , icon_name );
	}
	
	public GUIPageModelSlot ( String name , int index , GUIIcon icon ) {
		this ( name , index , icon.getName ( ) );
	}
	
	public GUIPageModelSlot ( int index , GUIIcon icon ) {
		this ( "slot-" + index , index , icon );
	}
	
	public GUIPageModelSlot ( ) {
		// to be loaded
	}
	
	public String getName ( ) {
		return name;
	}
	
	public int getIndex ( ) {
		return index;
	}
	
	public String getIconName ( ) {
		return icon_name;
	}
	
	@Override
	public GUIPageModelSlot load ( ConfigurationSection section ) {
		// this will load the index
		loadEntries ( section );
		
		// loading name
		this.name = section.getName ( );
		// loading icon name
		this.icon_name = section.getString ( ICON_KEY , StringUtil.EMPTY ).trim ( );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		// this will save the index
		int save = saveEntries ( section );
		
		// saving type
		save += YamlUtil.setNotEqual ( section , ICON_KEY , icon_name ) ? 1 : 0;
		
		return save;
	}
	
	@Override
	public boolean isValid ( ) {
		return StringUtil.isNotBlank ( name ) && index >= 0 && StringUtil.isNotBlank ( icon_name );
	}
}
