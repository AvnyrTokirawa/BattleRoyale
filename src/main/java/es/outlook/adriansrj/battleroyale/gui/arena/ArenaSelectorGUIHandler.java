package es.outlook.adriansrj.battleroyale.gui.arena;

import es.outlook.adriansrj.battleroyale.configuration.gui.arenaselector.ArenaSelectorGUIConfigHandler;
import es.outlook.adriansrj.battleroyale.gui.GUIConfiguration;
import es.outlook.adriansrj.battleroyale.gui.GUIIconTypeRegistry;
import es.outlook.adriansrj.battleroyale.gui.GUIInstance;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author AdrianSR / 04/09/2021 / 12:12 a. m.
 */
public final class ArenaSelectorGUIHandler extends PluginHandler {
	
	static {
		// registering icon types
		for ( ArenaSelectorGUIIconType type : ArenaSelectorGUIIconType.values ( ) ) {
			GUIIconTypeRegistry.register ( type );
		}
	}
	
	public static ArenaSelectorGUIHandler getInstance ( ) {
		return getPluginHandler ( ArenaSelectorGUIHandler.class );
	}
	
	private       GUIConfiguration                       configuration;
	private final Map < UUID, ArenaSelectorGUIInstance > handle_map;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public ArenaSelectorGUIHandler ( BattleRoyale plugin ) {
		super ( plugin );
		
		this.configuration = ArenaSelectorGUIConfigHandler.DEFAULT_TEAM_GUI_CONFIGURATION;
		this.handle_map    = new HashMap <> ( );
		
		// this task is responsible for updating
		// the team selector guis so players can
		// actually see changes in real-time.
		// cannot schedule it asynchronously as it
		// will result in an exception in the 1.17.
		Bukkit.getScheduler ( ).runTaskTimer ( plugin , ( ) -> {
			for ( org.bukkit.entity.Player player : Bukkit.getOnlinePlayers ( ) ) {
				ArenaSelectorGUIInstance instance = handle_map.get ( player.getUniqueId ( ) );
				
				if ( instance != null ) {
					instance.update ( player );
				}
			}
		} , 20L , 20L );
	}
	
	public synchronized GUIConfiguration getConfiguration ( ) {
		return configuration;
	}
	
	public synchronized void setConfiguration ( GUIConfiguration configuration ) {
		Validate.notNull ( configuration , "configuration cannot be null" );
		Validate.isTrue ( configuration.isValid ( ) , "configuration must be valid" );
		
		if ( !Objects.equals ( this.configuration , configuration ) ) {
			this.configuration = configuration;
			
			// disposing old handles
			this.handle_map.values ( ).forEach ( GUIInstance :: dispose );
			this.handle_map.clear ( );
		}
	}
	
	public void open ( org.bukkit.entity.Player player ) {
		get ( player ).open ( player );
	}
	
	public void update ( org.bukkit.entity.Player player ) {
		get ( player ).build ( player );
	}
	
	private ArenaSelectorGUIInstance get ( org.bukkit.entity.Player player ) {
		return this.handle_map.computeIfAbsent (
				player.getUniqueId ( ) , k -> new ArenaSelectorGUIInstance ( configuration ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}