package es.outlook.adriansrj.battleroyale.battlefield.minimap.renderer;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;

import java.util.Objects;
import java.util.Optional;

/**
 * Arena battlefield minimap renderer.
 *
 * @author AdrianSR / 03/09/2021 / 08:47 p. m.
 */
public class MinimapRendererArena extends MinimapRendererBase {
	
	protected final BattleRoyaleArena arena;
	
	public MinimapRendererArena ( BattleRoyaleArena arena ) {
		super ( arena.getBattlefield ( ).getMinimap ( ) , arena.getFullBounds ( ) );
		this.arena = arena;
	}
	
	@Override
	protected void render ( MapCanvas canvas , ZoneBounds display_bounds , org.bukkit.entity.Player player ) {
		drawPlayer ( player.getLocation ( ) , canvas , MapCursor.Type.WHITE_POINTER , display_bounds );
		
		// drawing teammates
		// TODO: skip spectators, dead teammates
		Player.getPlayer ( player ).getTeam ( ).getPlayers ( ).stream ( )
				.filter ( teammate -> !Objects.equals ( teammate.getUniqueId ( ) , player.getUniqueId ( ) ) )
				.filter ( teammate -> !teammate.isSpectator ( ) )
				.map ( Player :: getBukkitPlayerOptional ).filter ( Optional :: isPresent ).map ( Optional :: get )
				.forEach ( teammate -> drawPlayer (
						teammate.getLocation ( ) , canvas , MapCursor.Type.GREEN_POINTER , display_bounds ) );
		
		/* drawing border */
		// current border bounds
		// we want to wrap it in case it is displaying the initial border.
		drawBorder ( arena , arena.getBorder ( ).getCurrentBounds ( ) ,
					 display_bounds , player , canvas , MapCursor.Type.RED_POINTER , true );
		
		// future border bounds
		ZoneBounds future_border = arena.getBorder ( ).getFutureBounds ( );
		
		if ( future_border != null ) {
			drawBorder ( arena , future_border , display_bounds , player , canvas ,
						 MapCursor.Type.BLUE_POINTER , false );
		}
	}
}
