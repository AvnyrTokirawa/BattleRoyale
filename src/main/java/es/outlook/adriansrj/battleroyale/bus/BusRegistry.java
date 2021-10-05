package es.outlook.adriansrj.battleroyale.bus;

import es.outlook.adriansrj.battleroyale.bus.pet.BusPet;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;
import es.outlook.adriansrj.battleroyale.util.NamespacedKeyUtil;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bus configuration registry.
 *
 * @author AdrianSR / 23/09/2021 / 10:08 p. m.
 */
public final class BusRegistry extends PluginHandler {
	
	public static final NamespacedKey DEFAULT_BUS_REGISTRATION_KEY = NamespacedKeyUtil.ofBus ( "default" );
	
	public static BusRegistry getInstance ( ) {
		return getPluginHandler ( BusRegistry.class );
	}
	
	private final Map < NamespacedKey, Bus > map = new ConcurrentHashMap <> ( );
	
	// default bus that any player is allowed to use
	private Bus default_bus = new BusPet ( EntityType.CHICKEN );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BusRegistry ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	public Collection < Bus > getRegisteredBuses ( ) {
		return map.values ( );
	}
	
	public Bus getDefaultBus ( ) {
		return default_bus.clone ( );
	}
	
	public Bus getBus ( NamespacedKey key ) {
		if ( Objects.equals ( DEFAULT_BUS_REGISTRATION_KEY , key ) ) {
			return default_bus;
		} else {
			return getBus ( Validate.namespace ( Constants.BUS_NAMESPACE , key ).getKey ( ) );
		}
	}
	
	public Bus getBus ( String name ) {
		return map.get ( NamespacedKeyUtil.ofBus ( Objects.requireNonNull (
				name , "name cannot be null" ) ) );
	}
	
	public NamespacedKey getRegistrationKey ( Bus bus ) {
		if ( Objects.equals ( bus , default_bus ) ) {
			return DEFAULT_BUS_REGISTRATION_KEY;
		}
		
		for ( Map.Entry < NamespacedKey, Bus > entry : map.entrySet ( ) ) {
			if ( Objects.equals ( bus , entry.getValue ( ) ) ) {
				return entry.getKey ( );
			}
		}
		return null;
	}
	
	public void registerBus ( NamespacedKey key , Bus bus ) {
		Validate.notNull ( bus , "bus cannot be null" );
		Validate.isTrue ( bus.isValid ( ) , "bus cannot be invalid" );
		
		if ( Objects.equals ( DEFAULT_BUS_REGISTRATION_KEY , key ) ) {
			setDefaultBus ( bus );
		} else {
			map.put ( Validate.namespace ( Constants.BUS_NAMESPACE , key ) , bus );
		}
	}
	
	public void registerBus ( String name , Bus bus ) {
		registerBus ( NamespacedKeyUtil.ofBus ( name ) , bus );
	}
	
	/**
	 * Sets the default bus players will use when they don't have another to use.
	 * <br>
	 * <b>Note that the price and the permission will be ignored as it is the
	 * default bus and anyone should be able to use it.</b>
	 *
	 * @param bus the default bus.
	 */
	public void setDefaultBus ( Bus bus ) {
		Validate.notNull ( bus , "bus cannot be null" );
		Validate.isTrue ( bus.isValid ( ) , "bus cannot be invalid" );
		
		// the price and the permission will be ignored
		// as this is the default bus and anyone
		// should be able to use it.
		this.default_bus = bus.clone ( );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}