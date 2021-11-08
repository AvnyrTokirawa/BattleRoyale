package es.outlook.adriansrj.battleroyale.gui.shop;

import es.outlook.adriansrj.battleroyale.enums.EnumShopGUIsConfiguration;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.Item;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * @author AdrianSR / 07/11/2021 / 01:20 p. m.
 */
public final class ShopConfirmationGUIHandler extends PluginHandler {
	
	public static ShopConfirmationGUIHandler getInstance ( ) {
		return getPluginHandler ( ShopConfirmationGUIHandler.class );
	}
	
	private final Map < UUID, ItemMenu > handle_map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public ShopConfirmationGUIHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	public synchronized void open ( org.bukkit.entity.Player player ,
			final BiConsumer < org.bukkit.entity.Player, Boolean > callback ) {
		this.build ( player , callback ).open ( player );
	}
	
	public synchronized void open ( Player br_player , final BiConsumer < Player, Boolean > callback ) {
		BiConsumer < org.bukkit.entity.Player, Boolean > callback_wrapper =
				( player , flag ) -> callback.accept ( Player.getPlayer ( player ) , flag );
		
		br_player.getBukkitPlayerOptional ( ).ifPresent (
				player -> open ( player , callback_wrapper ) );
	}
	
	private synchronized ItemMenu build ( org.bukkit.entity.Player player ,
			final BiConsumer < org.bukkit.entity.Player, Boolean > callback ) {
		ItemMenu handle = this.handle_map.computeIfAbsent ( player.getUniqueId ( ) , k -> {
			ItemMenu result = new ItemMenu (
					EnumShopGUIsConfiguration.CONFIRMATION_GUI_TITLE.getAsString ( ) ,
					EnumShopGUIsConfiguration.CONFIRMATION_GUI_SIZE.getAsEnum ( ItemMenuSize.class ) );
			
			result.registerListener ( BattleRoyale.getInstance ( ) );
			return result;
		} );
		
		handle.clear ( );
		
		// cancel button
		handle.setItem (
				EnumShopGUIsConfiguration.CONFIRMATION_GUI_ITEM_CANCEL_POSITION.getAsInteger ( ) ,
				new Item ( EnumShopGUIsConfiguration.CONFIRMATION_GUI_ITEM_CANCEL_TEXT.getAsString ( ) ,
						   UniversalMaterial.RED_WOOL.getItemStack ( ) ) {
					@Override
					public void onClick ( ItemClickAction action ) {
						action.setClose ( true );
						
						callback.accept ( action.getPlayer ( ) , false );
					}
				} );
		
		// confirm button
		handle.setItem (
				EnumShopGUIsConfiguration.CONFIRMATION_GUI_ITEM_CONFIRM_POSITION.getAsInteger ( ) ,
				new Item ( EnumShopGUIsConfiguration.CONFIRMATION_GUI_ITEM_CONFIRM_TEXT.getAsString ( ) ,
						   UniversalMaterial.GREEN_WOOL.getItemStack ( ) ) {
					@Override
					public void onClick ( ItemClickAction action ) {
						action.setClose ( true );
						
						callback.accept ( action.getPlayer ( ) , true );
					}
				} );
		
		return handle;
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
