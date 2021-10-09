package es.outlook.adriansrj.battleroyale.game.loot;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.StringUtil;
import org.apache.commons.lang.Validate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Loot configurations registry.
 *
 * @author AdrianSR / 12/09/2021 / 04:21 p. m.
 */
public final class LootConfigurationRegistry extends PluginHandler {
	
	public static LootConfigurationRegistry getInstance ( ) {
		return getPluginHandler ( LootConfigurationRegistry.class );
	}
	
	private final Map < String, LootConfiguration > map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public LootConfigurationRegistry ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	public Map < String, LootConfiguration > getConfigurations ( ) {
		return Collections.unmodifiableMap ( map );
	}
	
	public LootConfiguration getConfiguration ( String name ) {
		return map.get ( name );
	}
	
	public void registerConfiguration ( String name , LootConfiguration configuration ) {
		Validate.isTrue ( StringUtil.isNotBlank ( name ) , "name cannot be null/blank" );
		Validate.notNull ( configuration , "configuration cannot be null" );
		
		map.put ( name.trim ( ) , configuration );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
