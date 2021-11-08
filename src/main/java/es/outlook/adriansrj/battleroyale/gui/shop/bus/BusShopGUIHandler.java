package es.outlook.adriansrj.battleroyale.gui.shop.bus;

import es.outlook.adriansrj.battleroyale.bus.Bus;
import es.outlook.adriansrj.battleroyale.bus.BusRegistry;
import es.outlook.adriansrj.battleroyale.bus.dragon.BusDragon;
import es.outlook.adriansrj.battleroyale.bus.pet.BusPet;
import es.outlook.adriansrj.battleroyale.cosmetic.bus.BusCosmetic;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.enums.EnumShopGUIsConfiguration;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.PlayerDataStorage;
import es.outlook.adriansrj.battleroyale.gui.shop.ShopConfirmationGUIHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.CosmeticUtil;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.Item;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.custom.book.BookItemMenu;
import es.outlook.adriansrj.core.menu.custom.book.item.AlternateBookPageActionItem;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.item.action.close.CloseMenuActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author AdrianSR / 07/11/2021 / 11:45 a. m.
 */
public final class BusShopGUIHandler extends PluginHandler {
	
	public static BusShopGUIHandler getInstance ( ) {
		return getPluginHandler ( BusShopGUIHandler.class );
	}
	
	private final Map < UUID, BookItemMenu > handle_map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BusShopGUIHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	public synchronized void open ( org.bukkit.entity.Player player ) {
		this.build ( player ).open ( player );
	}
	
	public synchronized void open ( Player player ) {
		player.getBukkitPlayerOptional ( ).ifPresent ( this :: open );
	}
	
	public synchronized void update ( org.bukkit.entity.Player player ) {
		this.build ( player ).update ( player );
	}
	
	private synchronized ItemMenu build ( org.bukkit.entity.Player player ) {
		BookItemMenu handle = this.handle_map.computeIfAbsent ( player.getUniqueId ( ) , k -> {
			BookItemMenu result = new BookItemMenu (
					EnumShopGUIsConfiguration.BUS_GUI_TITLE.getAsString ( ) ,
					EnumShopGUIsConfiguration.BUS_GUI_SIZE.getAsEnum ( ItemMenuSize.class ) );
			
			result.registerListener ( BattleRoyale.getInstance ( ) );
			return result;
		} );
		
		handle.clear ( );
		
		// buses
		BusRegistry.getInstance ( ).getRegisteredBuses ( ).stream ( )
				.filter ( bus -> !CosmeticUtil.isUnlocked ( bus , player ) )
				.filter ( bus -> bus.getPrice ( ) > 0 ) // purchasables only
				.forEach ( bus -> handle.addItem ( buildBusItem ( player , bus ) ) );
		
		// back button
		handle.setBarButton ( 3 , new AlternateBookPageActionItem (
				ChatColor.GREEN + EnumLanguage.BACK_WORD.getAsStringStripColors ( ) ,
				EnumShopGUIsConfiguration.COMMON_BUTTON_BACK_MATERIAL.getAsItemStack ( ) )
				// go next disabled to go to the previous
				.setGoNext ( false ) );
		
		// next button
		handle.setBarButton ( 5 , new AlternateBookPageActionItem (
				ChatColor.GREEN + EnumLanguage.NEXT_WORD.getAsStringStripColors ( ) ,
				EnumShopGUIsConfiguration.COMMON_BUTTON_NEXT_MATERIAL.getAsItemStack ( ) )
				// go next disabled to go to the next
				.setGoNext ( true ) );
		
		// close button
		handle.setBarButton ( 8 , new CloseMenuActionItem (
				ChatColor.DARK_RED + EnumLanguage.CLOSE_WORD.getAsStringStripColors ( ) ,
				EnumShopGUIsConfiguration.COMMON_BUTTON_CLOSE_MATERIAL.getAsItemStack ( ) ) );
		return handle;
	}
	
	private synchronized Item buildBusItem ( org.bukkit.entity.Player player , Bus bus ) {
		NamespacedKey     key      = BusRegistry.getInstance ( ).getRegistrationKey ( bus );
		UniversalMaterial material = UniversalMaterial.MINECART;
		
		if ( bus instanceof BusDragon ) {
			throw new UnsupportedOperationException ( BusDragon.class.getSimpleName ( ) + " not supported" );
		} else if ( bus instanceof BusPet ) {
			material = UniversalMaterial.PUFFERFISH;
		}
		
		// bus must be in the registry
		if ( key != null ) {
			int price = bus.getPrice ( );
			
			// display text
			String display_text = String.format (
					EnumShopGUIsConfiguration.BUS_GUI_ITEM_TEXT_FORMAT
							.getAsString ( ) , StringUtil.capitalize (
							key.getKey ( ).replace ( "_" , "" )
									.replace ( "-" , "" ) ) );
			
			// display description
			List < String > display_description = EnumShopGUIsConfiguration.BUS_GUI_ITEM_DESCRIPTION_FORMAT.getAsStringList ( );
			
			for ( int i = 0 ; i < display_description.size ( ) ; i++ ) {
				String line = display_description.get ( i );
				
				// we have to dirtily replace the %d as it could be
				// present in any line with more placeholders.
				if ( line != null ) {
					display_description.set ( i , line.replace (
							"%d" , String.valueOf ( price ) ) );
				}
			}
			
			return new ActionItem ( display_text , material.getItemStack ( ) , display_description ).addAction ( action -> {
				action.setClose ( true );
				
				Player            br_player    = Player.getPlayer ( action.getPlayer ( ) );
				PlayerDataStorage data_storage = br_player.getDataStorage ( );
				int               balance      = data_storage.getBalance ( );
				
				if ( balance >= price ) {
					ShopConfirmationGUIHandler.getInstance ( ).open ( br_player , ( p , confirm ) -> {
						if ( confirm ) {
							// adding
							data_storage.addCosmetic ( new BusCosmetic ( bus ) , true );
							// withdrawing balance
							data_storage.balanceWithdraw ( price , true );
							
							// message
							action.getPlayer ( ).sendMessage (
									EnumShopGUIsConfiguration.BUS_GUI_ITEM_PURCHASED_MESSAGE.getAsString ( ) );
						} else {
							BusShopGUIHandler.this.open ( action.getPlayer ( ) );
						}
					} );
				} else {
					action.getPlayer ( ).sendMessage (
							EnumShopGUIsConfiguration.BUS_GUI_ITEM_NOT_PURCHASED_MESSAGE.getAsString ( ) );
				}
			} );
		}
		return null;
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}