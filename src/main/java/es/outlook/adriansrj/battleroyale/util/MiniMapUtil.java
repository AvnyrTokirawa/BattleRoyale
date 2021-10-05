package es.outlook.adriansrj.battleroyale.util;

import es.outlook.adriansrj.battleroyale.battlefield.minimap.Minimap;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.generator.MinimapGenerator;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.renderer.MinimapRenderer;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.io.File;

/**
 * Useful class for dealing with minimaps.
 *
 * @author AdrianSR / 01/09/2021 / 12:27 p. m.
 */
public class MiniMapUtil {
	
	public static Minimap generate ( File world_folder , ZoneBounds bounds ) {
		Validate.notNull ( world_folder , "world folder cannot be null" );
		Validate.notNull ( bounds , "bounds cannot be null" );
		
		MinimapGenerator generator = new MinimapGenerator ( world_folder );
		
		generator.generate ( bounds );
		generator.close ( );
		
		return generator.getResult ( );
	}
	
	public static Minimap generate ( File world_folder , BoundingBox bounds ) {
		return generate ( world_folder , new ZoneBounds ( bounds ) );
	}
	
	public static Minimap generate ( World world , ZoneBounds bounds ) {
		return generate ( world.getWorldFolder ( ) , bounds );
	}
	
	public static Minimap generate ( World world , BoundingBox bounds ) {
		return generate ( world.getWorldFolder ( ) , new ZoneBounds ( bounds ) );
	}
	
	public static MapView createView ( MinimapRenderer renderer , World world ) {
		MapView view = Bukkit.createMap ( world );
		
		// view.gerRenderers().clear() will not work since
		// bukkit API has severe problems just like always.
		for ( MapRenderer other : view.getRenderers ( ) ) {
			view.removeRenderer ( other );
		}
		
		// then we can add our custom renderer
		view.addRenderer ( renderer );
		return view;
	}
	
	public static MapView createView ( MinimapRenderer renderer ) {
		return createView ( renderer , Bukkit.getWorlds ( ).get ( 0 ) );
	}
}