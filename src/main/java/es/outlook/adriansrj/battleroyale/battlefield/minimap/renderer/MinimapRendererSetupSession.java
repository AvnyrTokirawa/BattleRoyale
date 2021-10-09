package es.outlook.adriansrj.battleroyale.battlefield.minimap.renderer;

import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldConfiguration;
import es.outlook.adriansrj.battleroyale.battlefield.bus.BusSpawn;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupResult;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import org.bukkit.World;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.util.Vector;

/**
 * Battlefield setup session minimap renderer.
 *
 * @author AdrianSR / 01/09/2021 / 03:06 p. m.
 */
public class MinimapRendererSetupSession extends MinimapRendererBase {
	
	protected final BattlefieldSetupSession session;
	
	public MinimapRendererSetupSession ( BattlefieldSetupSession session ) {
		super ( session.getResult ( ).getMinimap ( ) , session.getResult ( ).getBounds ( ) );
		this.session = session;
	}
	
	@Override
	protected void render ( MapCanvas canvas , ZoneBounds display_bounds , org.bukkit.entity.Player player ) {
		drawPlayer ( player.getLocation ( ) , canvas , MapCursor.Type.WHITE_POINTER , display_bounds );
		
		World                    world         = player.getWorld ( );
		BattlefieldSetupResult   result        = session.getResult ( );
		BattlefieldConfiguration configuration = result != null ? result.getConfiguration ( ) : null;
		
		// drawing players in the session
		session.getGuestList ( ).stream ( ).map ( Player :: getBukkitPlayerOptional ).forEach (
				invited -> invited.ifPresent (
						handle -> drawPlayer ( handle.getLocation ( ) , canvas ,
											   MapCursor.Type.GREEN_POINTER , display_bounds ) ) );
		
		// drawing bus spawns travel path
		if ( configuration != null ) {
			for ( BusSpawn spawn : configuration.getBusSpawns ( ) ) {
				if ( spawn.isInvalid ( ) ) { continue; }
				
				Vector start_location = result.getBounds ( ).project ( spawn.getStartLocation ( ) );
				float  yaw            = spawn.getYaw ( );
				Vector direction      = DirectionUtil.getDirection ( yaw , 0.0F );
				
				Vector door_point_location = start_location.clone ( ).add (
						direction.clone ( ).multiply ( spawn.getDoorPointDistance ( ) ) );
				Vector end_point_location = door_point_location.clone ( ).add ( direction.clone ( ).multiply (
						session.getResult ( ).getBounds ( ).getSize ( ) ) );
				
				for ( MapLocation point : connect (
						project ( start_location.toLocation ( world , yaw , 0.0F ) ,
								  display_bounds , true ) ,
						project ( door_point_location.toLocation ( world , yaw , 0.0F ) ,
								  display_bounds , true ) ,
						display_bounds ) ) {
					if ( !point.isOutOfBounds ( ) ) {
						canvas.getCursors ( ).addCursor ( point.x , point.y ,
														  point.direction , MapCursor.Type.RED_POINTER.getValue ( ) );
					}
				}
				
				for ( MapLocation point : connect (
						project ( door_point_location.toLocation ( world , yaw , 0.0F ) ,
								  display_bounds , true ) ,
						project ( end_point_location.toLocation ( world , yaw , 0.0F ) ,
								  display_bounds , true ) ,
						display_bounds ) ) {
					if ( !point.isOutOfBounds ( ) ) {
						canvas.getCursors ( ).addCursor ( point.x , point.y ,
														  point.direction ,
														  MapCursor.Type.GREEN_POINTER.getValue ( ) );
					}
				}
			}
		}
		
		// TODO: draw configuration, like border, loot chests, etc...
	}
}