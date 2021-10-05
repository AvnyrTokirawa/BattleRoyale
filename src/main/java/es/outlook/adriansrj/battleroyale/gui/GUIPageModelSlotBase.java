package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.core.util.StringUtil;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Page model <b>base</b> slot configuration, which is just a template.
 *
 * @author AdrianSR / 10/08/2021 / Time: 08:09 a. m.
 */
public class GUIPageModelSlotBase extends GUIPageModelSlot {
	
	public GUIPageModelSlotBase ( String icon_name ) {
		super ( GUIPageModelContent.SLOT_BASE_NAME , -1 , icon_name );
	}
	
	public GUIPageModelSlotBase ( GUIIcon icon ) {
		super ( GUIPageModelContent.SLOT_BASE_NAME , -1 , icon );
	}
	
	public GUIPageModelSlotBase ( GUIPageModelSlot copy ) {
		this ( copy.getIconName ( ) );
	}
	
	public GUIPageModelSlotBase ( ) {
		// to be loaded
	}
	
	@Override
	public int getIndex ( ) {
		throw new UnsupportedOperationException ( );
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = super.save ( section );
		
		// making sure the index was not saved
		if ( section.isSet ( INDEX_KEY ) ) {
			section.set ( INDEX_KEY , null );
			save++;
		}
		
		return save;
	}
	
	@Override
	public boolean isValid ( ) {
		return StringUtil.isNotBlank ( name ) && StringUtil.isNotBlank ( icon_name );
	}
}
