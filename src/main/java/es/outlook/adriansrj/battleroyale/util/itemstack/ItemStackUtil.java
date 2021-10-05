package es.outlook.adriansrj.battleroyale.util.itemstack;

import es.outlook.adriansrj.battleroyale.battlefield.minimap.renderer.MinimapRenderer;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.lang.reflect.InvocationTargetException;

/**
 * Useful class for dealing with {@link org.bukkit.inventory.ItemStack}s.
 *
 * @author AdrianSR / 01/09/2021 / 01:53 p. m.
 */
public class ItemStackUtil extends es.outlook.adriansrj.core.util.itemstack.ItemStackUtil {
	
	/**
	 * Creates a map {@link ItemStack} that renders the provided {@link MapView}.
	 *
	 * @param view the view to render.
	 * @return a map {@link ItemStack} that renders the provided {@link MapView}.
	 */
	public static ItemStack createViewItemStack ( MapView view ) {
		ItemStack item = UniversalMaterial.FILLED_MAP.getItemStack ( );
		MapMeta   meta = ( MapMeta ) item.getItemMeta ( );
		
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R2 ) ) {
			try {
				MapMeta.class.getMethod ( "setMapView" , MapView.class ).invoke ( meta , view );
			} catch ( NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException ex ) {
				ex.printStackTrace ( );
			}
		} else {
			// the method getId is causing problems
			// in 1.12- versions as it used to return short,
			// and the newer bukkit api versions return int.
			try {
				item.setDurability ( ( short ) MapView.class.getMethod ( "getId" ).invoke ( view ) );
			} catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
				e.printStackTrace ( );
			}
		}
		
		item.setItemMeta ( meta );
		
		// MiniMapRenderers will require to be requested for a render
		for ( MapRenderer renderer : view.getRenderers ( ) ) {
			if ( renderer instanceof MinimapRenderer ) {
				( ( MinimapRenderer ) renderer ).requestRendering ( );
			}
		}
		return item;
	}
	
	/**
	 * Gets the corresponding {@link MapView} for the provided map {@link ItemStack}
	 *
	 * @param map_item the map item stack.
	 * @return the corresponding {@link MapView} for the provided map {@link ItemStack
	 */
	public static MapView getMapItemStackView ( ItemStack map_item ) {
		ItemMeta meta = map_item.getItemMeta ( );
		
		if ( meta instanceof MapMeta ) {
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R2 ) ) {
				try {
					return ( MapView ) MapMeta.class.getMethod ( "getMapView" ).invoke ( meta );
				} catch ( NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException ex ) {
					return null;
				}
			} else {
				// the method getMap is causing problems
				// in 1.12- versions as it used to accept a short,
				// as parameter, and the newer bukkit api versions uses int.
				MapView map_view = null;
				
				try {
					map_view = ( MapView ) Server.class.getMethod ( "getMap" , int.class )
							.invoke ( Bukkit.getServer ( ) , ( int ) map_item.getDurability ( ) );
				} catch ( NoSuchMethodException ex ) {
					try {
						map_view = ( MapView ) Server.class.getMethod ( "getMap" , short.class )
								.invoke ( Bukkit.getServer ( ) , ( short ) map_item.getDurability ( ) );
					} catch ( NoSuchMethodException ex_a ) {
						throw new IllegalStateException ( "couldn't find getMap() method" );
					} catch ( IllegalAccessException | InvocationTargetException ex_b ) {
						ex_b.printStackTrace ( );
					}
				} catch ( IllegalAccessException | InvocationTargetException e ) {
					e.printStackTrace ( );
				}
				
				return map_view;
			}
		} else {
			return null;
		}
	}
}
