package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.itemstack.ItemStackUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.server.Version;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * A custom icon.
 *
 * @author AdrianSR / 09/08/2021 / Time: 09:45 p. m.
 */
public class GUIIconCustom extends GUIIcon {
	
	public static GUIIconCustom of ( ConfigurationSection section ) {
		return new GUIIconCustom ( ).load ( section );
	}
	
	protected static final String MATERIAL_KEY = "material";
	protected static final String AMOUNT_KEY   = "amount";
	protected static final String DATA_KEY     = "data";
	
	protected Material material;
	@ConfigurableEntry ( key = AMOUNT_KEY, comment = "amount. 1 by default" )
	protected int      amount;
	@ConfigurableEntry ( key = DATA_KEY, comment = "material special data" )
	protected int      data;
	
	public GUIIconCustom ( String name , Material material , int amount , int data ,
			String display_name_format , List < String > description_format ) {
		super ( name , GUIIconTypeDefault.CUSTOM , display_name_format , description_format );
		
		this.amount   = amount;
		this.material = material;
		this.data     = data;
	}
	
	public GUIIconCustom ( String name , Material material , int amount , int data ,
			String display_name_format , String... description_format ) {
		super ( name , GUIIconTypeDefault.CUSTOM , display_name_format , description_format );
		
		this.amount   = amount;
		this.material = material;
		this.data     = data;
	}
	
	public GUIIconCustom ( ) {
		// to be loaded
		super ( StringUtil.EMPTY , GUIIconTypeDefault.CUSTOM , StringUtil.EMPTY );
	}
	
	@Override
	protected GUIIconInstance createInstance ( GUIInstance gui , Player player , String display_name ,
			List < String > description ) {
		ItemStack icon = new ItemStack ( material , Math.max ( amount , 0 ) );
		
		// legacy versions data
		if ( Version.getServerVersion ( ).isOlder ( Version.v1_13_R1 ) ) {
			ItemStackUtil.setData ( icon , data );
		}
		
		return new GUIIconInstance ( gui , display_name , icon , description ) {
			@Override
			public void onClick ( ItemClickAction action ) {
				// nothing to do
			}
		};
	}
	
	public Material getMaterial ( ) {
		return material;
	}
	
	public int getAmount ( ) {
		return amount;
	}
	
	public int getData ( ) {
		return data;
	}
	
	@Override
	public GUIIconCustom load ( ConfigurationSection section ) {
		// loads the amount, data, and inherited values
		super.load ( section );
		
		// loading material
		UniversalMaterial wrapper = UniversalMaterial.match ( section.getString (
				MATERIAL_KEY , StringUtil.EMPTY ) );
		
		this.material = wrapper != null ? wrapper.getMaterial ( ) : null;
		
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		// saves the amount, data, and inherited values
		int save = super.save ( section );
		
		// saving material
		if ( material != null ) {
			save += YamlUtil.setNotEqual ( section , MATERIAL_KEY , material.name ( ) ) ? 1 : 0;
		}
		
		return save;
	}
	
	@Override
	public boolean isValid ( ) {
		return super.isValid ( ) && material != null && data >= 0;
	}
}
