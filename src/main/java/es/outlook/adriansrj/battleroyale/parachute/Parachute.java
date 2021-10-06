package es.outlook.adriansrj.battleroyale.parachute;

import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustom;
import es.outlook.adriansrj.battleroyale.parachute.plugin.ParachuteQAV;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.itemstack.banner.BannerColor;
import es.outlook.adriansrj.core.util.itemstack.wool.WoolColor;
import es.outlook.adriansrj.core.util.permission.Permissions;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;

import java.util.Locale;

/**
 * @author AdrianSR / 11/09/2021 / 01:29 p. m.
 */
public abstract class Parachute implements Configurable, Cloneable {
	
	protected static final String PRICE_KEY      = "price";
	protected static final String PERMISSION_KEY = "permission";
	
	public static Parachute of ( ConfigurationSection section ) {
		Parachute result = null;
		
		if ( ( result = ParachuteCustom.of ( section ) ).isValid ( ) ) {
			return result;
		}
		
		if ( Bukkit.getPluginManager ( ).isPluginEnabled ( Constants.QUALITY_ARMORY_VEHICLES_PLUGIN_NAME ) ) {
			if ( ( result = ParachuteQAV.of ( section ) ).isValid ( ) ) {
				return result;
			}
		}
		
		return null;
	}
	
	/**
	 * @author AdrianSR / 10/09/2021 / 08:15 p. m.
	 */
	public enum Color {
		
		PLAYER ( null , null , null ),
		WHITE ( ),
		ORANGE ( ChatColor.GOLD ),
		MAGENTA ( ChatColor.LIGHT_PURPLE ),
		LIGHT_BLUE ( ChatColor.BLUE ),
		YELLOW ( ),
		LIME ( ChatColor.GREEN ),
		PINK ( ChatColor.LIGHT_PURPLE ),
		GRAY ( ChatColor.DARK_GRAY ),
		LIGHT_GRAY ( ChatColor.GRAY ),
		CYAN ( ChatColor.AQUA ),
		PURPLE ( ChatColor.DARK_PURPLE ),
		BLUE ( ChatColor.DARK_BLUE ),
		BROWN ( ChatColor.DARK_RED ),
		GREEN ( ChatColor.DARK_GREEN ),
		RED ( ),
		BLACK ( ),
		;
		
		public static Color of ( NamespacedKey key ) {
			return valueOf ( Validate.namespace ( Constants.PARACHUTE_COLOR_NAMESPACE , key )
									 .getKey ( ).toUpperCase ( Locale.ROOT ) );
		}
		
		private final WoolColor     wool;
		private final BannerColor   banner;
		private final ChatColor     chat_color;
		private       NamespacedKey key;
		private       Permission    permission;
		
		Color ( WoolColor wool , BannerColor banner , ChatColor chat_color ) {
			this.wool       = wool;
			this.banner     = banner;
			this.chat_color = chat_color;
		}
		
		Color ( ChatColor chat_color ) {
			this.wool       = WoolColor.valueOf ( name ( ) );
			this.banner     = BannerColor.valueOf ( name ( ) );
			this.chat_color = chat_color;
		}
		
		Color ( ) {
			this.wool       = WoolColor.valueOf ( name ( ) );
			this.banner     = BannerColor.valueOf ( name ( ) );
			this.chat_color = ChatColor.valueOf ( name ( ) );
		}
		
		public Permission getPermission ( ) {
			if ( permission == null ) {
				this.permission = Permissions.of ( "parachute-color." + name ( ).toLowerCase ( ) );
				Permissions.register ( permission );
			}
			return permission;
		}
		
		public WoolColor getAsWoolColor ( ) {
			return wool;
		}
		
		public BannerColor getAsBannerColor ( ) {
			return banner;
		}
		
		public ChatColor getAsChatColor ( ) {
			return chat_color;
		}
		
		public NamespacedKey getKey ( ) {
			if ( key == null ) {
				key = new NamespacedKey ( Constants.PARACHUTE_COLOR_NAMESPACE , name ( ) );
			}
			return key;
		}
	}
	
	protected int        price;
	protected Permission permission;
	
	protected Parachute ( int price , Permission permission ) {
		this.price      = price;
		this.permission = permission;
	}
	
	protected Parachute ( ) {
		// to be loaded
	}
	
	public int getPrice ( ) {
		return price;
	}
	
	public Permission getPermission ( ) {
		return permission;
	}
	
	public abstract ParachuteInstance createInstance ( Player player );
	
	@Override
	public Parachute load ( ConfigurationSection section ) {
		// price
		this.price = Math.max ( section.getInt ( PRICE_KEY ) , 0 );
		
		// permission
		String permission_name = section.getString ( PERMISSION_KEY );
		
		if ( StringUtil.isNotBlank ( permission_name ) ) {
			this.permission = Permissions.of ( permission_name.toLowerCase ( ).trim ( ) );
			Permissions.register ( permission );
		}
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = 0;
		
		// price
		if ( price > 0 ) {
			save += YamlUtil.setNotEqual ( section , PRICE_KEY , price ) ? 1 : 0;
		}
		
		// permission
		if ( permission != null ) {
			save += YamlUtil.setNotEqual ( section , PERMISSION_KEY , permission.getName ( ) ) ? 1 : 0;
		}
		
		return save;
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		
		Parachute parachute = ( Parachute ) o;
		
		return new EqualsBuilder ( ).append ( price , parachute.price )
				.append ( permission , parachute.permission ).isEquals ( );
	}
	
	@Override
	public int hashCode ( ) {
		return new HashCodeBuilder ( 17 , 37 )
				.append ( price ).append ( permission ).toHashCode ( );
	}
	
	@Override
	public Parachute clone ( ) {
		try {
			return ( Parachute ) super.clone ( );
		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError ( );
		}
	}
}
