package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.gui.arena.ArenaSelectorGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setting.SettingsGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.team.TeamSelectorGUIHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.battleroyale.util.itemstack.ItemStackUtil;
import es.outlook.adriansrj.core.util.EventUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.reflection.DataType;
import es.outlook.adriansrj.core.util.server.Version;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

/**
 * Battle royale items.
 *
 * @author AdrianSR / 25/10/2021 / 04:43 p. m.
 */
public enum EnumItem implements Consumer < PlayerInteractEvent >, Configurable {
	
	ARENA_SELECTOR ( true , 0 , ChatColor.GOLD + "Arena Selector" ,
					 UniversalMaterial.SAND , ( byte ) 0 ,
					 true , EventPriority.HIGH , null ) {
		@Override
		public void accept ( PlayerInteractEvent event ) {
			if ( EventUtil.isRightClick ( event.getAction ( ) ) ) {
				event.setCancelled ( true );
				
				ArenaSelectorGUIHandler.getInstance ( ).open ( event.getPlayer ( ) );
			}
		}
	},
	
	LEAVE_ARENA ( true , 8 , ChatColor.DARK_RED + "Leave Arena" ,
				  UniversalMaterial.BLACK_WOOL , ( byte ) 0 ,
				  true , EventPriority.HIGH , null ) {
		@Override
		public void accept ( PlayerInteractEvent event ) {
			if ( EventUtil.isRightClick ( event.getAction ( ) ) ) {
				event.setCancelled ( true );
				
				BattleRoyaleArenaHandler.getInstance ( )
						.leaveArena ( Player.getPlayer ( event.getPlayer ( ) ) );
			}
		}
	},
	
	TEAM_SELECTOR ( true , 4 , ChatColor.GOLD + "Team Selector" ,
					UniversalMaterial.WHITE_WOOL , ( byte ) 0 ,
					true , EventPriority.HIGH , null ) {
		@Override
		public void accept ( PlayerInteractEvent event ) {
			if ( EventUtil.isRightClick ( event.getAction ( ) ) ) {
				event.setCancelled ( true );
				
				TeamSelectorGUIHandler.getInstance ( ).open ( event.getPlayer ( ) );
			}
		}
	},
	
	SETTINGS ( true , 2 , ChatColor.GOLD + "Settings" ,
			   UniversalMaterial.LEVER , ( byte ) 0 ,
			   true , EventPriority.HIGH , null ) {
		@Override
		public void accept ( PlayerInteractEvent event ) {
			if ( EventUtil.isRightClick ( event.getAction ( ) ) ) {
				event.setCancelled ( true );
				
				SettingsGUIHandler.getInstance ( ).open ( event.getPlayer ( ) );
			}
		}
	},
	
	;
	
	private static final String INDEX_KEY        = "index";
	private static final String DISPLAY_NAME_KEY = "display-name";
	private static final String LORE_KEY         = "lore";
	private static final String MATERIAL_KEY     = "material";
	private static final String DATA_KEY         = "data";
	
	/**
	 * Configuration entry.
	 *
	 * @author AdrianSR / 08/08/2021 / Time: 11:49 a. m.
	 */
	protected static class Entry implements Configurable {
		
		protected final String      key;
		protected final String      comment;
		protected final Class < ? > type;
		protected final Object      default_value;
		protected       Object      value;
		
		public Entry ( String key , String comment , Class < ? > type , Object default_value ) {
			this.key           = key;
			this.comment       = comment;
			this.type          = type;
			this.default_value = default_value;
			this.value         = default_value;
			
			Validate.isTrue ( typeCheck ( type , default_value ) ,
							  "type mismatch: " + type.getName ( ) + " is not assignable from "
									  + default_value.getClass ( ).getName ( ) );
		}
		
		public Object getDefaultValue ( ) {
			return default_value;
		}
		
		public Object getValue ( ) {
			return value;
		}
		
		@Override
		public Entry load ( ConfigurationSection section ) {
			Object raw = section.get ( key );
			
			if ( raw != null && typeCheck ( type , raw ) ) {
				this.value = raw;
			}
			return this;
		}
		
		@Override
		public int save ( ConfigurationSection section ) {
			if ( StringUtil.isNotBlank ( comment ) ) {
				YamlUtil.comment ( section , key , comment );
			}
			
			return YamlUtil.setNotSet ( section , key , default_value ) ? 1 : 0;
		}
		
		@Override
		public boolean isValid ( ) {
			return StringUtil.isNotBlank ( key ) && default_value != null;
		}
		
		@Override
		public boolean isInvalid ( ) {
			return !isValid ( );
		}
		
		protected boolean typeCheck ( Class < ? > clazz , Object to_check ) {
			if ( primitiveTypeCheck ( clazz , ( to_check != null ? to_check.getClass ( ) : null ) ) ) {
				return true;
			} else {
				return to_check != null && ( ( isNumericType ( clazz ) && to_check instanceof Number )
						|| clazz.isAssignableFrom ( to_check.getClass ( ) ) );
			}
		}
		
		protected boolean primitiveTypeCheck ( Class < ? > class_a , Class < ? > class_b ) {
			DataType type_a = DataType.fromClass ( class_a );
			DataType type_b = DataType.fromClass ( class_b );
			
			return type_a != null && type_b != null && Objects.equals ( type_a , type_b );
		}
		
		protected boolean isNumericType ( Class < ? > clazz ) {
			if ( Number.class.isAssignableFrom ( clazz ) ) {
				return true;
			} else {
				DataType type = DataType.fromClass ( clazz );
				
				if ( type != null ) {
					switch ( type ) {
						case BYTE:
						case DOUBLE:
						case FLOAT:
						case INTEGER:
						case LONG:
						case SHORT:
							return true;
						
						default:
							break;
					}
				}
			}
			return false;
		}
	}
	
	public static boolean isCustomItem ( ItemStack item ) {
		if ( item == null ) {
			return false;
		} else {
			for ( EnumItem custom_item : EnumItem.values ( ) ) {
				if ( ItemStackUtil.fastEqual ( item , custom_item.toItemStack ( ) ) ) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private final boolean           configurable;
	private final String            default_display_name;
	private       String            display_name;
	private final List < String >   default_lore;
	private       List < String >   lore;
	private final UniversalMaterial default_material;
	private       UniversalMaterial material;
	private final byte              default_data;
	private       byte              data;
	private final boolean           droppable;
	private final int               default_index;
	private       int               index;
	
	/** extra configuration entries */
	private final Set < Entry > extra_entries = new HashSet <> ( );
	
	EnumItem ( boolean configurable , int index , String display_name ,
			UniversalMaterial material , byte data , boolean droppable , EventPriority action_priority , String[] lore ) {
		this.configurable         = configurable;
		this.default_index        = index;
		this.index                = index;
		this.default_display_name = display_name;
		this.display_name         = display_name;
		this.default_material     = material;
		this.material             = material;
		this.default_data         = data;
		this.data                 = data;
		this.droppable            = droppable;
		this.default_lore         = lore != null ? Arrays.asList ( lore ) : new ArrayList <> ( );
		this.lore                 = new ArrayList <> ( default_lore );
		
		if ( action_priority != null ) {
			registerEvent ( action_priority );
		}
	}
	
	private void registerEvent ( EventPriority action_priority ) {
		Bukkit.getPluginManager ( ).registerEvent (
				PlayerInteractEvent.class , new Listener ( ) { } ,
				action_priority , ( listener , uncast_event ) -> {
					if ( uncast_event instanceof PlayerInteractEvent ) {
						PlayerInteractEvent event = ( PlayerInteractEvent ) uncast_event;
						ItemStack           item  = event.getItem ( );
						
						if ( event.getAction ( ) != Action.PHYSICAL && isThis ( item ) ) {
							accept ( event );
							
							event.setCancelled ( true );
						}
					}
				} , BattleRoyale.getInstance ( ) , false );
	}
	
	@SuppressWarnings ( "deprecation" )
	public ItemStack toItemStack ( int amount ) {
		ItemStack result = material.getItemStack ( );
		result.setAmount ( amount );
		
		// data
		if ( data > 0 ) {
			try {
				result.setData ( result.getType ( ).getNewData ( data ) );
			} catch ( Exception ex ) {
				// ignored
			}
			
			if ( Version.getServerVersion ( ).isOlder ( Version.v1_13_R1 ) ) {
				result.setDurability ( data );
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
	
	public ItemStack toItemStack ( ) {
		return toItemStack ( 1 );
	}
	
	public boolean isConfigurable ( ) {
		return configurable;
	}
	
	public int getIndex ( ) {
		return index;
	}
	
	public boolean isIndexable ( ) {
		return default_index != -1;
	}
	
	public boolean isDroppable ( ) {
		return droppable;
	}
	
	public String getDisplayName ( ) {
		return display_name;
	}
	
	public boolean isThis ( ItemStack item ) {
		return ItemStackUtil.fastEqual ( item , toItemStack ( ) );
	}
	
	@Override
	public String toString ( ) {
		return this.getDisplayName ( );
	}
	
	public void give ( org.bukkit.entity.Player player ) {
		give ( player , this.index );
	}
	
	public void give ( org.bukkit.entity.Player player , int index ) {
		if ( index != -1 ) {
			player.getInventory ( ).setItem ( index , toItemStack ( ) );
		}
		
		player.updateInventory ( );
	}
	
	public void give ( Player br_player , int index ) {
		br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> give ( player , index ) );
	}
	
	public void give ( Player br_player ) {
		give ( br_player , this.index );
	}
	
	@Override
	public void accept ( PlayerInteractEvent playerInteractEvent ) {
		// nothing by default
	}
	
	@Override
	public EnumItem load ( ConfigurationSection section ) {
		if ( configurable ) {
			// loading display name
			this.display_name = StringUtil.translateAlternateColorCodes (
					section.getString ( DISPLAY_NAME_KEY , default_display_name ) );
			
			// loading data
			this.data = ( byte ) section.getInt ( DATA_KEY , default_data );
			
			// loading indexes
			if ( default_index != -1 ) {
				this.index = section.getInt ( INDEX_KEY , default_index );
			}
			
			// loading lore
			this.lore = StringUtil.translateAlternateColorCodes ( section.getStringList ( LORE_KEY ) );
			
			// loading material
			this.material = UniversalMaterial.match ( section.getString ( MATERIAL_KEY , "" ) );
			
			// loading extra entries
			for ( Entry entry : extra_entries ) {
				entry.load ( section );
			}
		}
		
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		if ( configurable ) {
			YamlUtil.comment ( section , DISPLAY_NAME_KEY , "display name of the item" );
			int save = YamlUtil.setNotSet ( section , DISPLAY_NAME_KEY ,
											StringUtil.untranslateAlternateColorCodes ( default_display_name ) ) ?
					1 :
					0;
			
			// saving data
			YamlUtil.comment ( section , DATA_KEY , "item special data" );
			save += YamlUtil.setNotSet ( section , DATA_KEY , data ) ? 1 : 0;
			
			// saving indexes
			if ( default_index != -1 ) {
				YamlUtil.comment ( section , INDEX_KEY , "the position of this item in the player's inventory." );
				save += YamlUtil.setNotSet ( section , INDEX_KEY , default_index ) ? 1 : 0;
			}
			
			// saving lore
			if ( lore.size ( ) > 0 ) {
				YamlUtil.comment ( section , LORE_KEY , "the lore/description of this item." );
				save += YamlUtil.setNotSet ( section , LORE_KEY ,
											 StringUtil.untranslateAlternateColorCodes ( default_lore ) ) ? 1 : 0;
			}
			
			// saving material
			YamlUtil.comment ( section , MATERIAL_KEY , "the material/type of this item." );
			save += YamlUtil.setNotSet ( section , MATERIAL_KEY , default_material.name ( ) ) ? 1 : 0;
			
			// saving extra entries
			for ( Entry entry : extra_entries ) {
				save += entry.save ( section );
			}
			
			return save;
		} else {
			return 0;
		}
	}
	
	@Override
	public boolean isValid ( ) {
		return false;
	}
}
