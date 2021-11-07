package es.outlook.adriansrj.battleroyale.game.loot;

import es.outlook.adriansrj.battleroyale.enums.EnumItem;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.battleroyale.util.crackshot.CrackShotPlusUtil;
import es.outlook.adriansrj.battleroyale.util.crackshot.CrackShotUtil;
import es.outlook.adriansrj.battleroyale.util.itemstack.ItemStackUtil;
import es.outlook.adriansrj.battleroyale.util.qualityarmory.QualityArmoryUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.loadable.LoadableEntry;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.saveable.SavableCollectionEntry;
import es.outlook.adriansrj.core.util.server.Version;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

/**
 * @author AdrianSR / 12/09/2021 / 01:07 p. m.
 */
public class LootConfigurationEntry implements Configurable, Cloneable {
	
	protected static final String REQUIRED_KEY                     = "required";
	protected static final char   INLINE_REQUIRED_FORMAT_SEPARATOR = ':';
	protected static final String INLINE_REQUIRED_FORMAT           = "%s" + INLINE_REQUIRED_FORMAT_SEPARATOR + "%d";
	
	public static LootConfigurationEntry of ( ConfigurationSection section ) {
		return new LootConfigurationEntry ( ).load ( section );
	}
	
	@LoadableEntry ( key = Constants.NAME_KEY )
	protected String            display_name;
	@ConfigurableEntry ( key = "material" )
	protected UniversalMaterial material;
	@ConfigurableEntry ( key = "amount" )
	protected int               amount;
	@ConfigurableEntry ( key = "data" )
	protected int               data;
	@ConfigurableEntry ( key = "plugin-object" )
	protected String            plugin_object;
	@ConfigurableEntry ( key = "lore" )
	protected List < String >   lore;
	@ConfigurableEntry ( key = "chance" )
	protected double            chance;
	
	@SavableCollectionEntry ( subsection = REQUIRED_KEY, subsectionprefix = "required-" )
	protected final Set < LootConfigurationEntry > required = new HashSet <> ( );
	protected       Object                         required_raw;
	
	public LootConfigurationEntry ( String display_name , UniversalMaterial material , int amount , int data ,
			String plugin_object , double chance , List < String > lore , Set < LootConfigurationEntry > required ) {
		this.display_name  = display_name;
		this.material      = material;
		this.amount        = amount;
		this.data          = data;
		this.plugin_object = plugin_object;
		this.chance        = chance;
		this.lore          = lore != null ? new ArrayList <> ( lore ) : lore;
		
		if ( required != null ) {
			for ( LootConfigurationEntry entry : required ) {
				if ( entry != null ) {
					this.required.add ( entry.clone ( ) );
				}
			}
		}
	}
	
	public LootConfigurationEntry ( String display_name , UniversalMaterial material , int amount , int data ,
			String plugin_object , List < String > lore , Set < LootConfigurationEntry > required ) {
		this ( display_name , material , amount , data , plugin_object , 0.0D , lore , required );
	}
	
	public LootConfigurationEntry ( String display_name , UniversalMaterial material , int amount , int data ,
			String plugin_object , double chance , List < String > lore ) {
		this ( display_name , material , amount , data , plugin_object , chance , lore , null );
	}
	
	public LootConfigurationEntry ( String display_name , UniversalMaterial material , int amount , int data ,
			String plugin_object , List < String > lore ) {
		this ( display_name , material , amount , data , plugin_object , 0.0D , lore );
	}
	
	public LootConfigurationEntry ( String display_name , UniversalMaterial material , int amount ,
			int data , double chance , List < String > lore ) {
		this ( display_name , material , amount , data , null , chance , lore );
	}
	
	public LootConfigurationEntry ( String display_name , UniversalMaterial material , int amount ,
			int data , List < String > lore ) {
		this ( display_name , material , amount , data , 0.0D , lore );
	}
	
	public LootConfigurationEntry ( UniversalMaterial material , int data , int amount , double chance ) {
		this.material = material;
		this.amount   = amount;
		this.data     = data;
		this.chance   = chance;
	}
	
	public LootConfigurationEntry ( UniversalMaterial material , int data , int amount ) {
		this ( material , data , amount , 0.0D );
	}
	
	public LootConfigurationEntry ( UniversalMaterial material , int amount , double chance ) {
		this ( material , 0 , amount , chance );
	}
	
	public LootConfigurationEntry ( UniversalMaterial material , int amount ) {
		this ( material , amount , 0.0D );
	}
	
	public LootConfigurationEntry ( String plugin_object , int amount , double chance ,
			Set < LootConfigurationEntry > required ) {
		this.plugin_object = plugin_object;
		this.amount        = amount;
		this.chance        = chance;
		
		if ( required != null ) {
			for ( LootConfigurationEntry entry : required ) {
				if ( entry != null ) {
					this.required.add ( entry.clone ( ) );
				}
			}
		}
	}
	
	public LootConfigurationEntry ( String plugin_object , int amount , Set < LootConfigurationEntry > required ) {
		this ( plugin_object , amount , 0.0D , required );
	}
	
	public LootConfigurationEntry ( String plugin_object , int amount , double chance ) {
		this ( plugin_object , amount , chance , null );
	}
	
	public LootConfigurationEntry ( String plugin_object , int amount ) {
		this ( plugin_object , amount , 0.0D );
	}
	
	public LootConfigurationEntry ( String plugin_object , double chance ) {
		this ( plugin_object , 1 , chance );
	}
	
	public LootConfigurationEntry ( String plugin_object ) {
		this ( plugin_object , 0.0D );
	}
	
	public LootConfigurationEntry ( ) {
		// to be loaded
	}
	
	public String getDisplayName ( ) {
		return display_name != null ? StringUtil.translateAlternateColorCodes ( display_name ) : null;
	}
	
	public UniversalMaterial getMaterial ( ) {
		return material;
	}
	
	public int getAmount ( ) {
		return amount;
	}
	
	public int getData ( ) {
		return data;
	}
	
	public String getPluginObject ( ) {
		return plugin_object;
	}
	
	public List < String > getLore ( ) {
		return lore != null ? StringUtil.translateAlternateColorCodes ( new ArrayList <> ( lore ) ) : null;
	}
	
	public double getChance ( ) {
		return chance;
	}
	
	public Set < LootConfigurationEntry > getRequired ( ) {
		return required;
	}
	
	@SuppressWarnings ( "deprecation" )
	public ItemStack toItemStack ( org.bukkit.entity.Player player ) {
		int amount = Math.max ( 1 , this.amount );
		
		// copying plugin object
		// order is important
		if ( StringUtil.isNotBlank ( plugin_object ) ) {
			// battle royale object
			for ( EnumItem br_item : EnumItem.values ( ) ) {
				if ( br_item.name ( ).equalsIgnoreCase ( plugin_object ) ) {
					return br_item.toItemStack ( amount );
				}
			}
			
			// quality armory object
			if ( Bukkit.getPluginManager ( ).isPluginEnabled ( "QualityArmory" ) ) {
				ItemStack result = QualityArmoryUtil.getCustomItemAsItemStackByName ( plugin_object );
				
				if ( result != null ) {
					result.setAmount ( amount );
					return result;
				}
			}
			
			// crack shot plus object
			if ( Bukkit.getPluginManager ( ).isPluginEnabled ( "CrackShotPlus" ) ) {
				ItemStack result = player != null
						? CrackShotPlusUtil.updateItemStackFeatures ( player , plugin_object )
						: CrackShotPlusUtil.updateItemStackFeaturesNonPlayer ( plugin_object );
				
				if ( result != null ) {
					result.setAmount ( amount );
					return result;
				}
			}
			
			// crack shot object
			if ( Bukkit.getPluginManager ( ).isPluginEnabled ( "CrackShot" ) ) {
				ItemStack result = CrackShotUtil.generateWeapon ( plugin_object );
				
				if ( result != null ) {
					result.setAmount ( amount );
					return result;
				}
			}
		}
		
		// ordinary item stack
		if ( material != null ) {
			ItemStack result = material.getItemStack ( );
			result.setAmount ( amount );
			
			// data
			if ( data > 0 ) {
				try {
					result.setData ( result.getType ( ).getNewData ( ( byte ) data ) );
				} catch ( Exception ex ) {
					// ignored
				}
				
				if ( Version.getServerVersion ( ).isOlder ( Version.v1_13_R1 ) ) {
					result.setDurability ( ( short ) data );
				}
			}
			
			// display name
			if ( StringUtil.isNotBlank ( display_name ) ) {
				ItemStackUtil.setName ( result , display_name );
			}
			
			// lore
			if ( lore != null ) {
				ItemStackUtil.setLore ( result , lore );
			}
			return result;
		}
		return null;
	}
	
	public ItemStack toItemStack ( Player br_player ) {
		org.bukkit.entity.Player bukkit = br_player.getBukkitPlayer ( );
		return toItemStack ( bukkit != null ? bukkit : br_player.getLastHandle ( ) );
	}
	
	public ItemStack toItemStack ( ) {
		return toItemStack ( ( org.bukkit.entity.Player ) null );
	}
	
	public void addToInventory ( Inventory inventory , boolean add_required ) {
		ItemStack item = inventory instanceof PlayerInventory
				? toItemStack ( ( org.bukkit.entity.Player ) ( ( PlayerInventory ) inventory ).getHolder ( ) )
				: toItemStack ( );
		
		if ( item != null ) {
			inventory.addItem ( item );
		}
		
		// adding required
		if ( add_required ) {
			this.required.forEach ( entry -> entry.addToInventory ( inventory , false ) );
		}
	}
	
	public void addToInventory ( Inventory inventory ) {
		addToInventory ( inventory , true );
	}
	
	@Override
	public LootConfigurationEntry load ( ConfigurationSection section ) {
		loadEntries ( section );
		
		// required
		this.required_raw = section.get ( REQUIRED_KEY );
		return this;
	}
	
	public LootConfigurationEntry finishLoadingRequired ( LootConfigurationContainer container ) {
		this.required.clear ( );
		
		// inline required: [loot entry/plugin-object]:[amount]
		if ( required_raw instanceof List ) {
			for ( Object uncast : ( List < ? > ) required_raw ) {
				if ( uncast instanceof String ) {
					String inline    = ( String ) uncast;
					int    separator = inline.indexOf ( INLINE_REQUIRED_FORMAT_SEPARATOR );
					String name      = null;
					int    amount    = 1;
					
					if ( separator != -1 ) {
						String[] split = inline.split ( String.valueOf ( INLINE_REQUIRED_FORMAT_SEPARATOR ) );
						
						try {
							name   = split[ 0 ].trim ( );
							amount = Math.max ( Integer.parseInt ( split[ 1 ] ) , 1 );
						} catch ( NumberFormatException ex ) {
							// ignored
						}
					} else {
						name = inline.trim ( );
					}
					
					if ( StringUtil.isNotBlank ( name ) ) {
						LootConfigurationEntry loot_entry = null;
						
						for ( Map.Entry < String, LootConfigurationEntry > entry : container
								.getContent ( ).entrySet ( ) ) {
							if ( Objects.equals ( entry.getKey ( ) , name ) ) {
								loot_entry        = entry.getValue ( ).clone ( );
								loot_entry.amount = amount;
								// required sub-entries will not have chance or required sub-entries
								loot_entry.chance       = 0.0D;
								loot_entry.required_raw = null;
								loot_entry.required.clear ( );
								
								break;
							}
						}
						
						if ( loot_entry == null ) {
							// trying to resolve as plugin-object
							loot_entry = new LootConfigurationEntry ( name , amount );
						}
						
						if ( loot_entry.isValid ( ) ) {
							required.add ( loot_entry );
						}
					}
				}
			}
		}
		// just an ordinary section
		else if ( required_raw instanceof ConfigurationSection ) {
			ConfigurationSection section = ( ConfigurationSection ) required_raw;
			
			for ( String key : section.getKeys ( false ) ) {
				if ( section.isConfigurationSection ( key ) ) {
					LootConfigurationEntry entry = LootConfigurationEntry.of (
							section.getConfigurationSection ( key ) );
					
					if ( entry.isValid ( ) ) {
						// required sub-entries will not have chance or required sub-entries
						entry.chance       = 0.0D;
						entry.required_raw = null;
						entry.required.clear ( );
						
						required.add ( entry );
					}
				}
			}
		}
		
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = saveEntries ( section );
		
		// saving display name
		if ( display_name != null ) {
			save += YamlUtil.setNotEqual ( section , Constants.NAME_KEY ,
										   StringUtil.untranslateAlternateColorCodes ( display_name ) ) ? 1 : 0;
		}
		
		return save;
	}
	
	@Override
	public boolean isValid ( ) {
		return toItemStack ( ) != null;
	}
	
	@Override
	public LootConfigurationEntry clone ( ) {
		try {
			return ( LootConfigurationEntry ) super.clone ( );
		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError ( );
		}
	}
}