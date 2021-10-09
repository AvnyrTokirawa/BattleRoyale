package es.outlook.adriansrj.battleroyale.parachute.custom;

import es.outlook.adriansrj.battleroyale.enums.EnumPlayerSetting;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.material.MaterialUtil;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.itemstack.banner.BannerColor;
import es.outlook.adriansrj.core.util.itemstack.banner.BannerItemStack;
import es.outlook.adriansrj.core.util.itemstack.wool.WoolColor;
import es.outlook.adriansrj.core.util.itemstack.wool.WoolItemStack;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.server.Version;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author AdrianSR / 10/09/2021 / 08:13 p. m.
 */
public class ParachuteCustomModelPartShape implements Configurable, Cloneable {
	
	protected static final String                            MATERIAL_KEY         = "material";
	protected static final Map < String, UniversalMaterial > EASY_MATERIALS       = new HashMap <> ( );
	protected static final String                            EASY_MATERIAL_BANNER = "BANNER";
	protected static final String                            EASY_MATERIAL_WOOL   = "WOOL";
	
	static {
		EASY_MATERIALS.put ( EASY_MATERIAL_BANNER , UniversalMaterial.WHITE_BANNER );
		EASY_MATERIALS.put ( EASY_MATERIAL_WOOL , UniversalMaterial.WHITE_WOOL );
	}
	
	public static ParachuteCustomModelPartShape of ( ConfigurationSection section ) {
		return new ParachuteCustomModelPartShape ( ).load ( section );
	}
	
	@ConfigurableEntry ( key = MATERIAL_KEY )
	protected UniversalMaterial     material;
	@ConfigurableEntry ( key = "color" )
	protected ParachuteCustom.Color color;
	@ConfigurableEntry ( key = "data" )
	protected int                   data;
	
	public ParachuteCustomModelPartShape ( UniversalMaterial material , ParachuteCustom.Color color , int data ) {
		this.material = material;
		this.color    = color;
		this.data     = data;
	}
	
	public ParachuteCustomModelPartShape ( UniversalMaterial material , ParachuteCustom.Color color ) {
		this ( material , color , 0 );
	}
	
	public ParachuteCustomModelPartShape ( UniversalMaterial material , int data ) {
		this ( material , null , data );
	}
	
	public ParachuteCustomModelPartShape ( UniversalMaterial material ) {
		this ( material , null , 0 );
	}
	
	public ParachuteCustomModelPartShape ( ) {
		// to be loaded
	}
	
	public UniversalMaterial getMaterial ( ) {
		return material;
	}
	
	public ParachuteCustom.Color getColor ( ) {
		return color;
	}
	
	public int getData ( ) {
		return data;
	}
	
	public ItemStack toItemStack ( ParachuteCustom.Color color ) {
		if ( material != null ) {
			ItemStack result = material.getItemStack ( );
			
			if ( color != null && color != Parachute.Color.PLAYER ) {
				if ( MaterialUtil.isWool ( material ) ) { // wool
					WoolColor wool_color = color != null ? color.getAsWoolColor ( ) : null;
					result = new WoolItemStack ( wool_color != null ? wool_color : WoolColor.WHITE );
				} else if ( MaterialUtil.isBanner ( material ) ) { // banner
					BannerColor banner_color = color != null ? color.getAsBannerColor ( ) : null;
					result = new BannerItemStack ( banner_color != null ? banner_color : BannerColor.WHITE );
				}
			} else { // any other
				if ( data > 0 ) {
					try {
						result.setData ( result.getType ( ).getNewData ( ( byte ) data ) );
					} catch ( Throwable ex ) {
						// ignored
					}
					
					if ( Version.getServerVersion ( ).isOlder ( Version.v1_13_R1 ) ) {
						result.setDurability ( ( short ) data );
					}
				}
			}
			
			return result;
		} else {
			return null;
		}
	}
	
	public ItemStack toItemStack ( Player player ) {
		// color from player settings
		return toItemStack ( player.getDataStorage ( ).getSetting (
				Parachute.Color.class , EnumPlayerSetting.PARACHUTE_COLOR ) );
	}
	
	public ItemStack toItemStack ( ) {
		return toItemStack ( this.color );
	}
	
	@Override
	public boolean isValid ( ) {
		return material != null;
	}
	
	@Override
	public boolean isInvalid ( ) {
		return !isValid ( );
	}
	
	@Override
	public ParachuteCustomModelPartShape load ( ConfigurationSection section ) {
		loadEntries ( section );
		
		// easy material (easy names)
		if ( material == null ) {
			String material_name = section.getString ( MATERIAL_KEY );
			
			if ( StringUtil.isNotBlank ( material_name ) ) {
				material = EASY_MATERIALS.get ( material_name.trim ( ).toUpperCase ( ) );
			}
		}
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = saveEntries ( section );
		
		// easy material (easy names)
		if ( material != null ) {
			if ( MaterialUtil.isBanner ( material ) ) {
				save += YamlUtil.setNotEqual ( section , MATERIAL_KEY , EASY_MATERIAL_BANNER ) ? 1 : 0;
			} else if ( MaterialUtil.isWool ( material ) ) {
				save += YamlUtil.setNotEqual ( section , MATERIAL_KEY , EASY_MATERIAL_WOOL ) ? 1 : 0;
			}
		}
		
		return saveEntries ( section );
	}
	
	@Override
	public ParachuteCustomModelPartShape clone ( ) {
		try {
			return ( ParachuteCustomModelPartShape ) super.clone ( );
		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError ( );
		}
	}
}
