package es.outlook.adriansrj.battleroyale.scoreboard;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.StringUtil;
import org.apache.commons.lang.Validate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link Scoreboard} configurations registry.
 *
 * @author AdrianSR / 09/10/2021 / 09:26 a. m.
 */
public final class ScoreboardConfigurationRegistry extends PluginHandler {
	
	public static ScoreboardConfigurationRegistry getInstance ( ) {
		return getPluginHandler ( ScoreboardConfigurationRegistry.class );
	}
	
	private final Map < String, ScoreboardConfiguration > map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public ScoreboardConfigurationRegistry ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	public Map < String, ScoreboardConfiguration > getConfigurations ( ) {
		return Collections.unmodifiableMap ( map );
	}
	
	public ScoreboardConfiguration getConfiguration ( String name ) {
		return map.get ( name );
	}
	
	public void registerConfiguration ( String name , ScoreboardConfiguration configuration ) {
		Validate.isTrue ( StringUtil.isNotBlank ( name ) , "name cannot be null/blank" );
		Validate.notNull ( configuration , "configuration cannot be null" );
		
		map.put ( name.trim ( ) , configuration );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
