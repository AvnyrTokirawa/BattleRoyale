package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.core.util.configurable.Configurable;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link GUIPageModel} content.
 *
 * @author AdrianSR / 09/08/2021 / Time: 12:13 p. m.
 */
public class GUIPageModelContent implements Configurable {
	
	public static final String SLOT_BASE_NAME = "base";
	
	protected       GUIPageModelSlotBase              base;
	protected final Map < Integer, GUIPageModelSlot > slots = new LinkedHashMap <> ( );
	
	public GUIPageModelContent ( GUIPageModelSlotBase base , Collection < GUIPageModelSlot > slots ) {
		this.base = base;
		
		for ( GUIPageModelSlot slot : slots ) {
			this.slots.put ( slot.getIndex ( ) , slot );
		}
	}
	
	public GUIPageModelContent ( Collection < GUIPageModelSlot > slots ) {
		this ( null , slots );
	}
	
	public GUIPageModelContent ( GUIPageModelSlotBase base , GUIPageModelSlot... slots ) {
		this ( base , Arrays.asList ( slots ) );
	}
	
	public GUIPageModelContent ( GUIPageModelSlot... slots ) {
		this ( null , slots );
	}
	
	public GUIPageModelContent ( ) {
		// to be loaded
	}
	
	public GUIPageModelSlotBase getSlotBase ( ) {
		return base;
	}
	
	public Map < Integer, GUIPageModelSlot > getSlots ( ) {
		return slots;
	}
	
	public GUIPageModelSlot getSlotByIndex ( int index ) {
		return slots.get ( index );
	}
	
	@Override
	public GUIPageModelContent load ( ConfigurationSection section ) {
		this.slots.clear ( );
		
		for ( String key : section.getKeys ( false ) ) {
			if ( section.isConfigurationSection ( key ) ) {
				GUIPageModelSlot slot = new GUIPageModelSlot ( ).load ( section.getConfigurationSection ( key ) );
				
				if ( slot.isValid ( ) ) {
					// slot base
					if ( SLOT_BASE_NAME.equals ( slot.getName ( ).trim ( ) ) ) {
						this.base = new GUIPageModelSlotBase ( slot );
						continue;
					}
					
					// ordinary slot
					this.slots.put ( slot.getIndex ( ) , slot );
				}
			}
		}
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = 0;
		
		// saving slot base
		if ( base != null && base.isValid ( ) ) {
			save += base.save ( section.createSection ( base.getName ( ).trim ( ) ) );
		}
		
		// saving slots
		if ( slots.size ( ) > 0 ) {
			for ( GUIPageModelSlot slot : this.slots.values ( ) ) {
				if ( slot.isValid ( ) ) {
					save += slot.save ( section.createSection ( slot.getName ( ).trim ( ) ) );
				}
			}
		}
		return save;
	}
	
	@Override
	public boolean isValid ( ) {
		return true;
	}
}
