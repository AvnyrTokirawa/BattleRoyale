package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumLootContainer;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfiguration;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfigurationContainer;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

/**
 * Class that handles the loot chests around an arena.
 *
 * @author AdrianSR / 13/09/2021 / 04:16 p. m.
 */
public final class LootChestListener extends BattleRoyaleArenaListener {
	
	public LootChestListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onOpen ( PlayerInteractEvent event ) {
		Block clicked = event.getClickedBlock ( );
		
		if ( clicked != null && clicked.getState ( ) instanceof Chest
				&& clicked.hasMetadata ( Constants.LOOT_CHEST_METADATA_KEY ) ) {
			Chest     chest     = ( Chest ) clicked.getState ( );
			Inventory inventory = chest.getBlockInventory ( );
			BattleRoyaleArena arena = ( BattleRoyaleArena ) clicked.getMetadata (
					Constants.LOOT_CHEST_METADATA_KEY ).get ( 0 ).value ( );
			
			// filling
			if ( arena != null ) {
				LootConfiguration loot_configuration = arena.getBattlefield ( ).getConfiguration ( )
						.getLootConfiguration ( );
				LootConfigurationContainer container = loot_configuration != null
						? loot_configuration.getContainer ( EnumLootContainer.CHEST ) : null;
				
				if ( container != null ) {
					container.fill ( inventory , event.getPlayer ( ) );
				} else {
					ConsoleUtil.sendPluginMessage ( ChatColor.RED , "A loot chest could not be filled because " +
							"the loot configuration is unknown." , BattleRoyale.getInstance ( ) );
				}
				
				// filled, we don't need the metadata anymore.
				clicked.removeMetadata ( Constants.LOOT_CHEST_METADATA_KEY , BattleRoyale.getInstance ( ) );
			}
		}
	}
}