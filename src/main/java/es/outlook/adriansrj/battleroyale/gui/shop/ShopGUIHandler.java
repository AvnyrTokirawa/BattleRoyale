package es.outlook.adriansrj.battleroyale.gui.shop;

import es.outlook.adriansrj.battleroyale.bus.BusRegistry;
import es.outlook.adriansrj.battleroyale.enums.EnumShopGUIsConfiguration;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.gui.shop.bus.BusShopGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.shop.parachute.ParachuteShopGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.shop.parachute.color.ParachuteColorShopGUIHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteRegistry;
import es.outlook.adriansrj.battleroyale.util.CosmeticUtil;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Shop GUI.
 *
 * @author AdrianSR / 07/11/2021 / 11:43 a. m.
 */
public final class ShopGUIHandler extends PluginHandler {
	
	public static ShopGUIHandler getInstance ( ) {
		return getPluginHandler ( ShopGUIHandler.class );
	}
	
	private final Map < UUID, ItemMenu > handle_map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public ShopGUIHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	public void open ( org.bukkit.entity.Player player ) {
		this.build ( player ).open ( player );
	}
	
	public void open ( Player player ) {
		player.getBukkitPlayerOptional ( ).ifPresent ( this :: open );
	}
	
	public void update ( org.bukkit.entity.Player player ) {
		this.build ( player ).update ( player );
	}
	
	private synchronized ItemMenu build ( org.bukkit.entity.Player player ) {
		ItemMenu handle = this.handle_map.computeIfAbsent ( player.getUniqueId ( ) , k -> {
			ItemMenu result = new ItemMenu (
					EnumShopGUIsConfiguration.MAIN_GUI_TITLE.getAsString ( ) ,
					ItemMenuSize.FOUR_LINE );
			
			result.registerListener ( BattleRoyale.getInstance ( ) );
			return result;
		} );
		
		handle.clear ( );
		
		// bus shop
		if ( BusRegistry.getInstance ( ).getRegisteredBuses ( ).stream ( )
				.anyMatch ( bus -> !CosmeticUtil.isUnlocked ( bus , player ) ) ) {
			handle.setItem (
					EnumShopGUIsConfiguration.MAIN_GUI_BUS_SHOP_BUTTON_POSITION.getAsInteger ( ) ,
					new ActionItem ( EnumShopGUIsConfiguration.MAIN_GUI_BUS_SHOP_BUTTON_TEXT.getAsString ( ) ,
									 new ItemStack ( Objects.requireNonNull (
											 EnumShopGUIsConfiguration.MAIN_GUI_BUS_SHOP_BUTTON_MATERIAL.getAsItemStack ( ) ) )
					).addAction (
							action -> BusShopGUIHandler.getInstance ( ).open ( action.getPlayer ( ) ) ) );
		}
		
		// parachute shop
		if ( ParachuteRegistry.getInstance ( ).getRegisteredParachutes ( ).stream ( )
				.anyMatch ( parachute -> !CosmeticUtil.isUnlocked ( parachute , player ) ) ) {
			handle.setItem (
					EnumShopGUIsConfiguration.MAIN_GUI_PARACHUTE_SHOP_BUTTON_POSITION.getAsInteger ( ) ,
					new ActionItem ( EnumShopGUIsConfiguration.MAIN_GUI_PARACHUTE_SHOP_BUTTON_TEXT.getAsString ( ) ,
									 new ItemStack ( Objects.requireNonNull (
											 EnumShopGUIsConfiguration.MAIN_GUI_PARACHUTE_SHOP_BUTTON_MATERIAL.getAsItemStack ( ) ) )
					).addAction (
							action -> ParachuteShopGUIHandler.getInstance ( ).open ( action.getPlayer ( ) ) ) );
		}
		
		// parachute color shop
		if ( Arrays.stream ( Parachute.Color.values ( ) )
				.anyMatch ( color -> color != Parachute.Color.PLAYER && !CosmeticUtil.isUnlocked ( color , player ) ) ) {
			handle.setItem (
					EnumShopGUIsConfiguration.MAIN_GUI_PARACHUTE_COLOR_SHOP_BUTTON_POSITION.getAsInteger ( ) ,
					new ActionItem ( EnumShopGUIsConfiguration.MAIN_GUI_PARACHUTE_COLOR_SHOP_BUTTON_TEXT.getAsString ( ) ,
									 new ItemStack ( Objects.requireNonNull (
											 EnumShopGUIsConfiguration.MAIN_GUI_PARACHUTE_COLOR_SHOP_BUTTON_MATERIAL.getAsItemStack ( ) ) )
					).addAction (
							action -> ParachuteColorShopGUIHandler.getInstance ( ).open ( action.getPlayer ( ) ) ) );
		}
		
		return handle;
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
