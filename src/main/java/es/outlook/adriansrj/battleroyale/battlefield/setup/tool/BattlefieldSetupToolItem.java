package es.outlook.adriansrj.battleroyale.battlefield.setup.tool;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupTool;
import es.outlook.adriansrj.battleroyale.enums.EnumInternalLanguage;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.item.ActionItem;
import es.outlook.adriansrj.core.item.ActionItemBase;
import es.outlook.adriansrj.core.item.ActionItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author AdrianSR / 28/08/2021 / 11:23 a. m.
 */
public abstract class BattlefieldSetupToolItem extends BattlefieldSetupTool {
	
	protected final ActionItemBase     item;
	protected final Set < ActionItem > extra_items       = new HashSet <> ( );
	protected final Set < ActionItem > undroppable_items = new HashSet <> ( );
	
	protected BattlefieldSetupToolItem ( BattlefieldSetupSession session , Player configurator ) {
		super ( session , configurator );
		
		// building description
		List < String > description = new ArrayList <> ( );
		
		description.addAll ( getItemDescription ( ) );
		description.add ( "" );
		
		if ( isCancellable ( ) ) {
			description.add ( ChatColor.YELLOW + "Press the drop key to cancel." );
		} else {
			description.add ( ChatColor.YELLOW + "Press the drop key to finish." );
		}
		
		this.item = new ActionItemBase ( getItemDisplayName ( ) , description , getItemMaterial ( ) ) {
			
			@Override
			public void onActionPerform ( org.bukkit.entity.Player player , EnumAction action ,
					PlayerInteractEvent event ) {
				if ( Objects.equals ( player.getUniqueId ( ) , getConfigurator ( ).getUniqueId ( ) ) ) {
					BattlefieldSetupToolItem.this.onActionPerform ( player , action , event );
				}
			}
			
			@Override
			public EventPriority getPriority ( ) {
				return EventPriority.HIGHEST;
			}
		};
		
		ActionItemHandler.register ( item );
	}
	
	public ActionItemBase getItem ( ) {
		return item;
	}
	
	protected void registerExtraItem ( ActionItem item , boolean undroppable ) {
		if ( extra_items.add ( item ) ) {
			ActionItemHandler.register ( item );
			
			if ( undroppable ) {
				undroppable_items.add ( item );
			}
		}
	}
	
	protected void registerExtraItem ( ActionItem item ) {
		registerExtraItem ( item , true );
	}
	
	protected abstract Material getItemMaterial ( );
	
	protected abstract String getItemDisplayName ( );
	
	protected abstract List < String > getItemDescription ( );
	
	protected abstract void onActionPerform ( org.bukkit.entity.Player player , ActionItem.EnumAction action ,
			PlayerInteractEvent event );
	
	@Override
	public void initialize ( ) {
		register ( );
		
		// here we're adding the item to the inventory of the configurator.
		configurator.getBukkitPlayerOptional ( ).ifPresent ( player -> {
			boolean add = true;
			
			for ( ItemStack item : player.getInventory ( ) ) {
				if ( this.item.isThis ( item ) ) {
					add = false;
					break;
				}
			}
			
			if ( add ) {
				player.getInventory ( ).addItem ( getItem ( ).toItemStack ( ) );
				player.updateInventory ( );
			}
		} );
	}
	
	@Override
	protected void dispose ( ) {
		super.dispose ( );
		
		// unregistering items
		ActionItemHandler.unregister ( item );
		extra_items.forEach ( ActionItemHandler :: unregister );
		undroppable_items.clear ( );
		
		// here we're removing the item from the inventory of the configurator.
		org.bukkit.entity.Player player = configurator.getBukkitPlayerOptional ( )
				.orElse ( configurator.getLastHandle ( ) );
		
		if ( player != null ) {
			Bukkit.getScheduler ( ).scheduleSyncDelayedTask ( BattleRoyale.getInstance ( ) , ( ) -> {
				player.getInventory ( ).remove ( item.toItemStack ( ) );
				player.updateInventory ( );
			} );
		}
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public final void onCancel ( PlayerDropItemEvent event ) {
		// the player can cancel this tool by dropping it
		org.bukkit.entity.Player player = event.getPlayer ( );
		
		if ( Objects.equals ( configurator.getUniqueId ( ) , player.getUniqueId ( ) ) ) {
			if ( item.isThis ( event.getItemDrop ( ).getItemStack ( ) ) ) {
				dispose ( );
				
				// letting player know
				if ( isCancellable ( ) ) {
					player.sendMessage ( EnumInternalLanguage.TOOL_CANCELLED_MESSAGE.toString ( ) );
				} else {
					player.sendMessage ( EnumInternalLanguage.TOOL_FINISHED_MESSAGE.toString ( ) );
				}
				
				// we don't want the item to be actually dropped to the ground
				event.getItemDrop ( ).remove ( );
			} else {
				// there are some extra items that are undroppable
				ActionItem extra_item = extra_items.stream ( ).filter (
								extra -> extra.isThis ( event.getItemDrop ( ).getItemStack ( ) ) )
						.findAny ( ).orElse ( null );
				
				if ( extra_item != null && undroppable_items.contains ( extra_item ) ) {
					event.setCancelled ( true );
				}
			}
		}
	}
}