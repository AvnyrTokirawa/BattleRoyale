package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumLootContainer;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfiguration;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfigurationContainer;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

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
		
		if ( clicked != null && clicked.getState ( ) instanceof Chest ) {
			Chest     chest     = ( Chest ) clicked.getState ( );
			Inventory inventory = chest.getBlockInventory ( );
			BattleRoyaleArena arena = BattleRoyaleArenaHandler.getInstance ( ).getArenas ( ).stream ( )
					.filter ( other -> Objects.equals ( other.getWorld ( ) , clicked.getWorld ( ) ) )
					.filter ( other -> other.getFullBounds ( ).contains ( chest.getLocation ( ) ) )
					.findAny ( ).orElse ( null );
			
			// filling
			if ( arena != null && ( clicked.hasMetadata ( Constants.LOOT_CHEST_METADATA_KEY )
					|| EnumMainConfiguration.GAME_LAZY_LOOT_CHESTS.getAsBoolean ( ) ) ) {
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
				
				// in case it is a loot chest from the setup,
				// the metadata must be removed.
				clicked.removeMetadata ( Constants.LOOT_CHEST_METADATA_KEY , BattleRoyale.getInstance ( ) );
				
				// in case lazy loot-chests is enabled the
				// chest must be removed, otherwise the plugin
				// will fill it.
				if ( EnumMainConfiguration.GAME_LAZY_LOOT_CHESTS.getAsBoolean ( ) ) {
					// it was enough to set the block to air, but not anymore as
					// the server will not drop the stuff inside the chest.
					for ( ItemStack item : inventory ) {
						if ( item != null ) {
							arena.getWorld ( ).dropItem ( clicked.getLocation ( ).add (
									0.5 , 0.5 , 0.5 ) , item );
						}
					}
					
					inventory.clear ( );
					clicked.setType ( UniversalMaterial.AIR.getMaterial ( ) );
					clicked.getState ( ).update ( );
				}
			}
		}
	}
}