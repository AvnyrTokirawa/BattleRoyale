package es.outlook.adriansrj.battleroyale.battlefield.minimap;

import es.outlook.adriansrj.battleroyale.battlefield.minimap.renderer.MinimapRenderer;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.battleroyale.util.itemstack.ItemStackUtil;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class responsible for handling minimaps.
 *
 * @author AdrianSR / 02/09/2021 / 11:15 a. m.
 */
public final class MinimapHandler extends PluginHandler {
	
	public static MinimapHandler getInstance ( ) {
		return PluginHandler.getPluginHandler ( MinimapHandler.class );
	}
	
	/** maps the minimap settings of the players */
	private final Map < UUID, MinimapSettings > settings_map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public MinimapHandler ( BattleRoyale plugin ) {
		super ( plugin );
		register ( );
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onToggleZoom ( PlayerInteractEvent event ) {
		Player    player = Player.getPlayer ( event.getPlayer ( ) );
		ItemStack item   = event.getItem ( );
		MapView   view   = item != null ? ItemStackUtil.getMapItemStackView ( item ) : null;
		
		if ( view != null && view.getRenderers ( ).stream ( )
				.anyMatch ( renderer -> renderer instanceof MinimapRenderer ) ) {
			// toggling zoom
			getSettings ( player ).toggleZoom ( );
			
			// must force rendering
			for ( MapRenderer renderer : view.getRenderers ( ) ) {
				if ( renderer instanceof MinimapRenderer ) {
					( ( MinimapRenderer ) renderer ).requestRendering ( );
				}
			}
			
			event.setCancelled ( true );
		}
	}
	
	public MinimapSettings getSettings ( UUID id ) {
		if ( settings_map.containsKey ( id ) ) {
			return settings_map.get ( id );
		} else {
			MinimapSettings preferences = new MinimapSettings ( id );
			settings_map.put ( id , preferences );
			
			return preferences;
		}
	}
	
	public MinimapSettings getSettings ( org.bukkit.entity.Player player ) {
		return getSettings ( player.getUniqueId ( ) );
	}
	
	public MinimapSettings getSettings ( Player player ) {
		return getSettings ( player.getUniqueId ( ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
