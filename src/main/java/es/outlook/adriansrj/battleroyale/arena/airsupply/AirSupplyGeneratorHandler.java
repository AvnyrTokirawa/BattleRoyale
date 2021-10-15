package es.outlook.adriansrj.battleroyale.arena.airsupply;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.event.border.BorderResizeChangeEvent;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/**
 * @author AdrianSR / 13/10/2021 / 11:46 a. m.
 */
public final class AirSupplyGeneratorHandler extends PluginHandler {
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public AirSupplyGeneratorHandler ( BattleRoyale plugin ) {
		super ( plugin ); register ( );
	}
	
	// this event handler will spawn the air
	// supply drops when a new border resize
	// point begins.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onPointChange ( BorderResizeChangeEvent event ) {
		BattleRoyaleArena  arena = event.getBorder ( ).getArena ( );
		List < AirSupply > list  = arena.getAirSupplyGenerator ( ).next ( );
		
		for ( AirSupply next : list ) {
			if ( next.isValidPlace ( ) ) {
				next.start ( );
			}
		}
	}
	
	// this event handler will open the
	// supply drops when a player clicks on them.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onInteract ( PlayerInteractEvent event ) {
		Block                    clicked = event.getClickedBlock ( );
		org.bukkit.entity.Player player  = event.getPlayer ( );
		
		if ( clicked != null && ( clicked.hasMetadata ( AirSupply.AIR_SUPPLY_META_KEY )
				|| clicked.hasMetadata ( AirSupply.AIR_SUPPLY_INSTANCE_META_KEY ) ) ) {
			event.setCancelled ( true );
			
			// air supply loot chest clicked
			if ( player.getGameMode ( ) != GameMode.SPECTATOR
					&& Player.getPlayer ( player ).isPlaying ( )
					&& clicked.getState ( ) instanceof Chest
					&& clicked.hasMetadata ( AirSupply.AIR_SUPPLY_INSTANCE_META_KEY ) ) {
				AirSupply instance = ( AirSupply ) clicked.getMetadata (
						AirSupply.AIR_SUPPLY_INSTANCE_META_KEY ).get ( 0 ).value ( );
				
				if ( instance != null ) {
					instance.open ( player );
					
					// open sound
					player.playSound ( clicked.getLocation ( ) , Sound.ENTITY_SHULKER_OPEN , 4F , 1F );
					player.playSound ( clicked.getLocation ( ) , Sound.ENTITY_WITCH_DRINK , 4F , 1F );
					player.playSound ( clicked.getLocation ( ) , Sound.BLOCK_LAVA_EXTINGUISH , 4F , 1F );
				}
			}
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}