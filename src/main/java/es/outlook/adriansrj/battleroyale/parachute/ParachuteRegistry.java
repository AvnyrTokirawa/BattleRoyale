package es.outlook.adriansrj.battleroyale.parachute;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustom;
import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustomModel;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;
import es.outlook.adriansrj.battleroyale.util.NamespacedKeyUtil;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.core.handler.PluginHandler;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Parachute configuration registry.
 *
 * @author AdrianSR / 12/09/2021 / 12:15 a. m.
 */
public final class ParachuteRegistry extends PluginHandler {
	
	public static final NamespacedKey DEFAULT_PARACHUTE_REGISTRATION_KEY = NamespacedKeyUtil.ofParachute ( "default" );
	
	public static ParachuteRegistry getInstance ( ) {
		return getPluginHandler ( ParachuteRegistry.class );
	}
	
	private final Map < NamespacedKey, Parachute > map = new ConcurrentHashMap <> ( );
	
	// default parachute that any player is allowed to use
	private Parachute default_parachute = new ParachuteCustom ( ParachuteCustomModel.DEFAULT_MODEL );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public ParachuteRegistry ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	public Collection < Parachute > getRegisteredParachutes ( ) {
		return map.values ( );
	}
	
	public Parachute getDefaultParachute ( ) {
		return default_parachute.clone ( );
	}
	
	public Parachute getParachute ( NamespacedKey key ) {
		if ( Objects.equals ( DEFAULT_PARACHUTE_REGISTRATION_KEY , key ) ) {
			return default_parachute;
		} else {
			return getParachute ( Validate.namespace ( Constants.PARACHUTE_NAMESPACE , key ).getKey ( ) );
		}
	}
	
	public Parachute getParachute ( String name ) {
		return map.get ( NamespacedKeyUtil.ofParachute ( Objects.requireNonNull (
				name , "name cannot be null" ) ) );
	}
	
	public NamespacedKey getRegistrationKey ( Parachute parachute ) {
		if ( Objects.equals ( parachute , default_parachute ) ) {
			return DEFAULT_PARACHUTE_REGISTRATION_KEY;
		}
		
		for ( Map.Entry < NamespacedKey, Parachute > entry : map.entrySet ( ) ) {
			if ( Objects.equals ( parachute , entry.getValue ( ) ) ) {
				return entry.getKey ( );
			}
		}
		return null;
	}
	
	public void registerParachute ( NamespacedKey key , Parachute parachute ) {
		Validate.notNull ( parachute , "parachute cannot be null" );
		Validate.isTrue ( parachute.isValid ( ) , "parachute cannot be invalid" );
		
		if ( Objects.equals ( DEFAULT_PARACHUTE_REGISTRATION_KEY , key ) ) {
			setDefaultParachute ( parachute );
		} else {
			map.put ( Validate.namespace ( Constants.PARACHUTE_NAMESPACE , key ) , parachute );
		}
	}
	
	public void registerParachute ( String name , Parachute parachute ) {
		registerParachute ( NamespacedKeyUtil.ofParachute ( name ) , parachute );
	}
	
	/**
	 * Sets the default parachute players will use when they don't have another to use.
	 * <br>
	 * <b>Note that the price and the permission will be ignored as it is the
	 * default parachute and anyone should be able to use it.</b>
	 *
	 * @param parachute the default parachute.
	 */
	public void setDefaultParachute ( Parachute parachute ) {
		Validate.notNull ( parachute , "parachute cannot be null" );
		Validate.isTrue ( parachute.isValid ( ) , "parachute cannot be invalid" );
		
		// the price and the permission will be ignored
		// as this is the default parachute and anyone
		// should be able to use it.
		this.default_parachute = parachute.clone ( );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}