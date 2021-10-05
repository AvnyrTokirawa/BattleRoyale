package es.outlook.adriansrj.battleroyale.vehicle;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.StringUtil;
import org.apache.commons.lang.Validate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *Vehicles configuration registry.
 *
 * @author AdrianSR / 14/09/2021 / 07:39 p. m.
 */
public final class VehiclesConfigurationRegistry extends PluginHandler {
	
	public static VehiclesConfigurationRegistry getInstance ( ) {
		return getPluginHandler ( VehiclesConfigurationRegistry.class );
	}
	
	private final Map < String, VehiclesConfiguration > map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public VehiclesConfigurationRegistry ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	public Map < String, VehiclesConfiguration > getConfigurations ( ) {
		return Collections.unmodifiableMap ( map );
	}
	
	public VehiclesConfiguration getConfiguration ( String name ) {
		return map.get ( name );
	}
	
	public void registerConfiguration ( String name , VehiclesConfiguration configuration ) {
		Validate.isTrue ( StringUtil.isNotBlank ( name ) , "name cannot be null/blank" );
		Validate.notNull ( configuration , "configuration cannot be null" );
		
		map.put ( name.trim ( ) , configuration );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
