package es.outlook.adriansrj.battleroyale.arena.drop;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumLootContainer;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfiguration;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfigurationContainer;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfigurationEntry;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.configurable.vector.ConfigurableVector;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.math.RandomUtil;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Set;

/**
 * @author AdrianSR / 15/10/2021 / 07:42 p. m.
 */
public final class ItemDropHandler extends PluginHandler implements Runnable {
	
	private static final double CLOSE_ENOUGH_DISTANCE = 150.0D;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public ItemDropHandler ( BattleRoyale plugin ) {
		super ( plugin );
		
		// we've decided to register listeners only if the
		// enhanced drops are actually enabled, as some events
		// handled here might be fired too frequently.
		if ( EnumMainConfiguration.GAME_ENHANCED_DROPS_ENABLE.getAsBoolean ( ) ) {
			register ( );
			
			// this task is responsible dropping the
			// contents of a loot chest on the ground when a
			// player is close enough.
			Bukkit.getScheduler ( ).runTaskTimerAsynchronously ( plugin , this , 20L , 20L );
		}
	}
	
	// this event handler is responsible for detecting
	// and registering items that spawn in the arenas.
	@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onSpawn ( ItemSpawnEvent event ) {
		Item handle = event.getEntity ( );
		
		// then registering
		if ( !EnumMainConfiguration.GAME_ENHANCED_DROPS_LOOT_CONTAINER_ONLY.getAsBoolean ( ) ) {
			BattleRoyaleArenaHandler.getInstance ( ).getArenas ( ).stream ( )
					.filter ( other -> Objects.equals ( other.getWorld ( ) , handle.getWorld ( ) ) )
					.filter ( other -> other.getFullBounds ( ).contains ( handle.getLocation ( ) ) )
					.findAny ( ).ifPresent (
							arena -> arena.getDropManager ( ).register ( new ItemDrop ( handle , arena ) ) );
		}
	}
	
	@Override
	public void run ( ) {
		for ( BattleRoyaleArena arena : BattleRoyaleArenaHandler.getInstance ( ).getArenas ( ) ) {
			if ( arena.getState ( ) != EnumArenaState.RUNNING ) { continue; }
			
			for ( Player br_player : arena.getPlayers ( true ) ) {
				Location player_location = br_player.getLocation ( );
				
				for ( ConfigurableVector loot_chest_vector : arena.getBattlefield ( )
						.getConfiguration ( ).getLootChests ( ) ) {
					Location chest_location = loot_chest_vector != null ? arena.getFullBounds ( ).project (
							loot_chest_vector ).toLocation ( arena.getWorld ( ) ) : null;
					Block block = chest_location != null ? chest_location.getBlock ( ) : null;
					
					if ( block != null && block.getType ( ) == UniversalMaterial.CHEST.getMaterial ( )
							&& chest_location.distance ( player_location ) <= CLOSE_ENOUGH_DISTANCE ) {
						Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> {
							if ( block.getState ( ) instanceof Chest ) {
								drop ( block , arena , br_player );
							}
						} );
					}
				}
			}
		}
	}
	
	private void drop ( Block block , BattleRoyaleArena arena , Player br_player ) {
		Chest chest = ( Chest ) block.getState ( );
		
		// dropping contents
		LootConfiguration loot_configuration = arena.getBattlefield ( ).getConfiguration ( )
				.getLootConfiguration ( );
		LootConfigurationContainer container = loot_configuration != null
				? loot_configuration.getContainer ( EnumLootContainer.CHEST ) : null;
		
		if ( container != null ) {
			Set < LootConfigurationEntry > contents = container.getRandomEntries ( Math.max (
					RandomUtil.nextInt ( ( container.getMaximum ( ) *
							( chest.getInventory ( ).getSize ( ) / 9 ) ) + 1 ) , 1 ) );
			
			// dropping
			contents.stream ( ).map ( entry -> entry.toItemStack ( br_player.getPlayer ( ) ) ).filter (
					Objects :: nonNull ).forEach ( item -> drop ( arena , block , item ) );
		}
		
		// removing loot chest
		chest.getInventory ( ).clear ( );
		
		block.setType ( UniversalMaterial.AIR.getMaterial ( ) );
		block.getState ( ).update ( true , false );
		// we don't need the metadata anymore.
		block.removeMetadata ( Constants.LOOT_CHEST_METADATA_KEY , BattleRoyale.getInstance ( ) );
	}
	
	private void drop ( BattleRoyaleArena arena , Block block , ItemStack item ) {
		SchedulerUtil.runTask ( ( ) -> {
			Item instance = arena.getWorld ( ).dropItem (
					block.getLocation ( ).add ( 0.5 , 0.5 , 0.5 ) , item );
			
			// enhanced drop
			if ( EnumMainConfiguration.GAME_ENHANCED_DROPS_ENABLE.getAsBoolean ( )
					&& EnumMainConfiguration.GAME_ENHANCED_DROPS_LOOT_CONTAINER_ONLY.getAsBoolean ( ) ) {
				arena.getDropManager ( ).register ( new ItemDrop ( instance , arena ) );
			}
		} );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}