package es.outlook.adriansrj.battleroyale.bus.test;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.permission.Permissions;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;

import java.util.Objects;

/**
 * @author AdrianSR / 23/09/2021 / 07:45 p. m.
 */
public abstract class BusTest implements Configurable, Cloneable {
	
	protected static final String PRICE_KEY      = "price";
	protected static final String PERMISSION_KEY = "permission";
	
	public static BusTest of ( ConfigurationSection section ) {
		BusTest result = null;
		
		if ( ( result = BusPetTest.of ( section ) ).isValid ( ) ) {
			return result;
		}
		
		return null;
	}
	
	protected int        price;
	protected Permission permission;
	
	protected BusTest ( int price , Permission permission ) {
		this.price      = price;
		this.permission = permission;
	}
	
	protected BusTest ( ) {
		// to be loaded
	}
	
	public int getPrice ( ) {
		return price;
	}
	
	public Permission getPermission ( ) {
		return permission;
	}
	
	public abstract BusInstanceTest < ? > createInstance ( BattleRoyaleArena arena , Location spawn );
	
	@Override
	public BusTest load ( ConfigurationSection section ) {
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
	public BusTest clone ( ) {
		try {
			return ( BusTest ) super.clone ( );
		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError ( );
		}
	}
	
	protected BattleRoyaleArena arenaCheck ( Player player ) {
		return Objects.requireNonNull ( player.getArena ( ) , "player must be in an arena" );
	}
	
	protected < T extends BusTest > T configurationCheck ( Class < T > clazz , BusTest configuration ) {
		Validate.isAssignableFrom ( clazz , configuration.getClass ( ) ,
									"only %s configurations are supported" , clazz.getName ( ) );
		
		return clazz.cast ( configuration );
	}
}