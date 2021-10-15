package es.outlook.adriansrj.battleroyale.placeholder.node;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.placeholder.node.arena.ArenaPlaceholderNode;
import es.outlook.adriansrj.battleroyale.placeholder.node.date.DatePlaceholderNode;
import es.outlook.adriansrj.battleroyale.placeholder.node.player.PlayerPlaceholderNode;
import es.outlook.adriansrj.battleroyale.placeholder.node.team.TeamPlaceholderNode;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link PlaceholderNode} registry.
 *
 * @author AdrianSR / 06/10/2021 / 11:02 a. m.
 */
public final class PlaceholderNodeRegistry extends PluginHandler {
	
	public static PlaceholderNodeRegistry getInstance ( ) {
		return getPluginHandler ( PlaceholderNodeRegistry.class );
	}
	
	private final Map < String, PlaceholderNode > node_map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public PlaceholderNodeRegistry ( BattleRoyale plugin ) {
		super ( plugin );
		
		// registering default nodes
		registerNode ( new PlayerPlaceholderNode ( ) );
		registerNode ( new TeamPlaceholderNode ( ) );
		registerNode ( new ArenaPlaceholderNode ( ) );
		registerNode ( new DatePlaceholderNode ( ) );
	}
	
	public Collection < PlaceholderNode > getRegisteredNodes ( ) {
		return Collections.unmodifiableMap ( node_map ).values ( );
	}
	
	public boolean isRegistered ( PlaceholderNode node ) {
		return node_map.containsKey ( identifierCheck ( node ) );
	}
	
	public boolean isRegistered ( String node_sub_identifier ) {
		return node_map.containsKey ( identifierCheck ( node_sub_identifier ) );
	}
	
	public void registerNode ( PlaceholderNode node ) {
		node_map.put ( identifierCheck ( node ) , node );
	}
	
	public boolean unregisterNode ( PlaceholderNode node ) {
		return node_map.remove ( identifierCheck ( node ) ) != null;
	}
	
	public boolean unregisterNode ( String node_sub_identifier ) {
		return node_map.remove ( identifierCheck ( node_sub_identifier ) ) != null;
	}
	
	private String identifierCheck ( PlaceholderNode node ) {
		return Validate.notBlank ( node.getSubIdentifier ( ) ,
								   "node returned an invalid sub-identifier" )
				.trim ( ).toLowerCase ( );
	}
	
	private String identifierCheck ( String identifier ) {
		return Validate.notBlank ( identifier , "invalid sub-identifier" )
				.trim ( ).toLowerCase ( );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
