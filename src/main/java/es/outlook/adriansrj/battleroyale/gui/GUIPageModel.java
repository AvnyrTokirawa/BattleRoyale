package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.configuration.ConfigurationSection;

/**
 * {@link GUIPage} model configuration.
 *
 * @author AdrianSR / 09/08/2021 / Time: 12:12 p. m.
 */
public class GUIPageModel implements Configurable {
	
	public static final    String PAGE_MODEL_BASE_NAME = "base";
	protected static final String SIZE_KEY             = "size";
	protected static final String CONTENT_KEY          = "contents";
	
	protected String              name;
	protected ItemMenuSize        size;
	@ConfigurableEntry ( key = CONTENT_KEY )
	protected GUIPageModelContent content;
	
	public GUIPageModel ( String name , ItemMenuSize size , GUIPageModelContent content ) {
		this.name    = name;
		this.size    = size;
		this.content = content;
	}
	
	public GUIPageModel ( ) {
		// to be loaded
	}
	
	public String getName ( ) {
		return name;
	}
	
	public ItemMenuSize getSize ( ) {
		return size;
	}
	
	public GUIPageModelContent getContent ( ) {
		return content;
	}
	
	@Override
	public GUIPageModel load ( ConfigurationSection section ) {
		// this will load the content
		loadEntries ( section );
		
		// loading name
		this.name = section.getName ( );
		
		// loading size
		this.size = EnumReflection.getEnumConstant (
				ItemMenuSize.class , section.getString ( SIZE_KEY , StringUtil.EMPTY ) );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		// saving size
		int save = YamlUtil.setNotEqual ( section , SIZE_KEY , size.name ( ) ) ? 1 : 0;
		
		// this will save the content
		return save + saveEntries ( section );
	}
	
	@Override
	public boolean isValid ( ) {
		return StringUtil.isNotBlank ( name ) && size != null && content != null;
	}
	
	@Override
	public boolean isInvalid ( ) {
		return !isValid ( );
	}
}