package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.itemstack.ItemStackUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.server.Version;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Icon that acts as a button which performs an action when
 * clicking on it.
 *
 * @author AdrianSR / 29/09/2021 / 08:32 p. m.
 */
public abstract class GUIButton extends GUIIcon {
	
	protected static final String MATERIAL_KEY = "material";
	protected static final String AMOUNT_KEY   = "amount";
	protected static final String DATA_KEY     = "data";
	
	protected Material material;
	@ConfigurableEntry ( key = AMOUNT_KEY, comment = "amount. 1 by default" )
	protected int      amount;
	@ConfigurableEntry ( key = DATA_KEY, comment = "material special data" )
	protected int      data;
	
	protected GUIButton ( String name , GUIIconType type , Material material , int amount , int data ,
			List < String > description_format , String display_name_format ) {
		super ( name , type , display_name_format , description_format );
		
		this.material = material;
		this.amount   = amount;
		this.data     = data;
	}
	
	protected GUIButton ( String name , GUIIconType type , Material material , int amount , int data ,
			String display_name_format , String... description_format ) {
		super ( name , type , display_name_format , description_format );
		
		this.material = material;
		this.amount   = amount;
		this.data     = data;
	}
	
	protected GUIButton ( String name , GUIIconType type , Material material , int amount , int data ,
			String display_name_format ) {
		super ( name , type , display_name_format );
		this.material = material;
		this.amount   = amount;
		this.data     = data;
	}
	
	protected GUIButton ( GUIIconType type ) {
		super ( StringUtil.EMPTY , type , StringUtil.EMPTY );
	}
	
	protected GUIButton ( ) {
		// to be loaded
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
	protected GUIIconInstance createInstance ( GUIInstance gui , Player player , String display_name ,
			List < String > description ) {
		ItemStack icon = new ItemStack ( material , Math.max ( amount , 0 ) );
		
		// legacy versions data
		if ( Version.getServerVersion ( ).isOlder ( Version.v1_13_R1 ) ) {
			ItemStackUtil.setData ( icon , data );
		}
		
		return createInstance (
				gui , player ,
				// display name
				StringUtil.translateAlternateColorCodes (
						StringUtil.defaultIfBlank ( display_name_format , StringUtil.EMPTY ) ) ,
				// icon
				icon ,
				// description
				StringUtil.translateAlternateColorCodes (
						description_format != null ? new ArrayList <> ( description_format ) : new ArrayList <> ( ) ) );
	}
	
	protected abstract GUIIconInstance createInstance ( GUIInstance gui , Player player ,
			String display_name , ItemStack icon , List < String > description );
	
	@Override
	public GUIButton load ( ConfigurationSection section ) {
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
		return super.isValid ( ) && material != null && amount > 0;
	}
}