package es.outlook.adriansrj.battleroyale.compatibility.qualityarmory;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaBusRegistry;
import es.outlook.adriansrj.battleroyale.bus.BusInstance;
import es.outlook.adriansrj.battleroyale.compatibility.PluginCompatibilityHandler;
import es.outlook.adriansrj.battleroyale.event.player.PlayerCloseParachuteEvent;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteInstance;
import es.outlook.adriansrj.battleroyale.player.Player;
import me.zombie_striker.qg.api.QACustomItemInteractEvent;
import me.zombie_striker.qg.api.QAWeaponDamageEntityEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Class responsible for the compatibility with the <b>Quality Armory</b> plugin.
 *
 * @author AdrianSR / 13/09/2021 / 12:21 a. m.
 */
public final class QualityArmoryCompatibilityHandler extends PluginCompatibilityHandler {
	
	private static final String LAND_DATA_KEY    = UUID.randomUUID ( ).toString ( );
	private static final long   LAND_SHOOT_DELAY = 1500;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public QualityArmoryCompatibilityHandler ( BattleRoyale plugin ) {
		super ( plugin );
		register ( );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST, ignoreCancelled = true )
	public void onShoot ( QACustomItemInteractEvent event ) {
		org.bukkit.entity.Player player    = event.getPlayer ( );
		Player                   br_player = Player.getPlayer ( player );
		
		// players cannot shoot from the bus or the parachute
		BattleRoyaleArena            arena     = br_player.getArena ( );
		BattleRoyaleArenaBusRegistry registry  = arena != null ? arena.getBusRegistry ( ) : null;
		BusInstance                  bus       = registry != null ? registry.getBus ( player ) : null;
		ParachuteInstance            parachute = br_player.getParachute ( );
		
		if ( ( bus != null && bus.isStarted ( ) && !bus.isFinished ( ) && bus.isPassenger ( br_player ) )
				|| ( parachute != null && parachute.isOpen ( ) ) ) {
			event.setCancelled ( true );
		}
		
		// players that recently landed from parachuting cannot shoot
		// as the QualityArmory will teleport them back to the air.
		Optional < Object > optional_land_millis = br_player.getData ( LAND_DATA_KEY );
		
		if ( optional_land_millis.isPresent ( ) ) {
			Object uncast = optional_land_millis.get ( );
			
			if ( uncast instanceof Long && ( System.currentTimeMillis ( ) - ( Long ) uncast ) < LAND_SHOOT_DELAY ) {
				event.setCancelled ( true );
			}
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onLand ( PlayerCloseParachuteEvent event ) {
		event.getPlayer ( ).setData ( LAND_DATA_KEY , System.currentTimeMillis ( ) );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST, ignoreCancelled = true )
	public void onShootTeammate ( QAWeaponDamageEntityEvent event ) {
		// stopping friendly-fire
		org.bukkit.entity.Player player    = event.getPlayer ( );
		Player                   br_player = Player.getPlayer ( player );
		Entity                   entity    = event.getDamaged ( );
		
		if ( entity instanceof org.bukkit.entity.Player ) {
			org.bukkit.entity.Player damaged    = ( org.bukkit.entity.Player ) entity;
			Player                   br_damaged = Player.getPlayer ( damaged );
			
			if ( Objects.equals ( br_player.getTeam ( ) , br_damaged.getTeam ( ) ) ) {
				event.setCancelled ( true );
			}
		}
	}
}