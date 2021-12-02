package es.outlook.adriansrj.battleroyale.gui.setting.parachute.color;

import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.enums.EnumPlayerSetting;
import es.outlook.adriansrj.battleroyale.enums.EnumSettingsGUIsConfiguration;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.gui.shop.parachute.color.ParachuteColorShopGUIHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.util.CosmeticUtil;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.Item;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.menu.custom.book.BookItemMenu;
import es.outlook.adriansrj.core.menu.custom.book.item.AlternateBookPageActionItem;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.item.action.close.CloseMenuActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.itemstack.wool.WoolItemStack;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author AdrianSR / 04/10/2021 / 05:54 p. m.
 */
public final class ParachuteColorSettingsGUIHandler extends PluginHandler {
	
	public static ParachuteColorSettingsGUIHandler getInstance ( ) {
		return getPluginHandler ( ParachuteColorSettingsGUIHandler.class );
	}
	
	private final Map < UUID, BookItemMenu > handle_map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public ParachuteColorSettingsGUIHandler ( BattleRoyale plugin ) {
		super ( plugin );
		
		// this task is responsible for updating
		// the guis so players can actually
		// see changes in real-time.
		Bukkit.getScheduler ( ).runTaskTimer ( plugin , ( ) -> {
			for ( Map.Entry < UUID, BookItemMenu > next : handle_map.entrySet ( ) ) {
				org.bukkit.entity.Player player = Bukkit.getPlayer ( next.getKey ( ) );
				
				if ( player != null && player.isOnline ( ) ) {
					BookItemMenu handle = next.getValue ( );
					
					if ( handle.isMenuOpen ( player ) ) {
						update ( player );
					}
				}
			}
		} , 20L , 20L );
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
					EnumSettingsGUIsConfiguration.PARACHUTE_COLOR_GUI_TITLE.getAsString ( ) ,
					EnumSettingsGUIsConfiguration.PARACHUTE_COLOR_GUI_SIZE.getAsEnum ( ItemMenuSize.class ) );
			
			result.registerListener ( BattleRoyale.getInstance ( ) );
			return result;
		} );
		
		handle.clear ( );
		
		// inserting items
		for ( Parachute.Color color : Parachute.Color.values ( ) ) {
			if ( color != Parachute.Color.PLAYER ) {
				handle.addItem ( buildParachuteColorItem ( player , color ) );
			}
		}
		
		// shop button
		if ( Arrays.stream ( Parachute.Color.values ( ) )
				.anyMatch ( color -> !CosmeticUtil.isUnlocked ( color , player ) ) ) {
			handle.setBarButton ( 0 , new Item (
					ChatColor.DARK_GREEN + EnumLanguage.SHOP_WORD.getAsString ( ) ,
					UniversalMaterial.EMERALD.getItemStack ( ) ) {
				@Override
				public void onClick ( ItemClickAction action ) {
					ParachuteColorShopGUIHandler.getInstance ( ).open ( action.getPlayer ( ) );
				}
			} );
		}
		
		// back button
		handle.setBarButton ( 3 , new AlternateBookPageActionItem (
				ChatColor.GREEN + EnumLanguage.BACK_WORD.getAsStringStripColors ( ) ,
				EnumSettingsGUIsConfiguration.COMMON_BUTTON_BACK_MATERIAL.getAsItemStack ( ) )
				// go next disabled to go to the previous
				.setGoNext ( false ) );
		
		// next button
		handle.setBarButton ( 5 , new AlternateBookPageActionItem (
				ChatColor.GREEN + EnumLanguage.NEXT_WORD.getAsStringStripColors ( ) ,
				EnumSettingsGUIsConfiguration.COMMON_BUTTON_NEXT_MATERIAL.getAsItemStack ( ) )
				// go next disabled to go to the next
				.setGoNext ( true ) );
		
		// close button
		handle.setBarButton ( 8 , new CloseMenuActionItem (
				ChatColor.DARK_RED + EnumLanguage.CLOSE_WORD.getAsStringStripColors ( ) ,
				EnumSettingsGUIsConfiguration.COMMON_BUTTON_CLOSE_MATERIAL.getAsItemStack ( ) ) );
		
		return handle;
	}
	
	private synchronized Item buildParachuteColorItem ( org.bukkit.entity.Player player , Parachute.Color color ) {
		NamespacedKey key      = color.getKey ( );
		final boolean unlocked = CosmeticUtil.isUnlocked ( color , player );
		
		if ( color == Parachute.Color.PLAYER ) {
			throw new UnsupportedOperationException ( color.name ( ) + " not supported" );
		}
		
		if ( key != null ) {
			return new ActionItem (
					// display text
					String.format (
							( unlocked
									? EnumSettingsGUIsConfiguration.PARACHUTE_COLOR_GUI_ITEM_UNLOCKED_TEXT_FORMAT :
									EnumSettingsGUIsConfiguration.PARACHUTE_COLOR_GUI_ITEM_LOCKED_TEXT_FORMAT ).getAsString ( ) ,
							// actual color
							color.getAsChatColor ( ).toString ( ) ,
							// color name
							StringUtil.capitalize ( key.getKey ( )
															.replace ( "_" , " " )
															.replace ( "-" , " " ) ) ) ,
					// icon
					new WoolItemStack ( color.getAsWoolColor ( ) ) ,
					// description
					( unlocked ?
							EnumSettingsGUIsConfiguration.PARACHUTE_COLOR_GUI_ITEM_UNLOCKED_DESCRIPTION_FORMAT :
							EnumSettingsGUIsConfiguration.PARACHUTE_COLOR_GUI_ITEM_LOCKED_DESCRIPTION_FORMAT )
							.getAsStringList ( )
			).addAction ( action -> {
				action.setClose ( true );
				
				// setting and immediately uploading
				if ( unlocked ) {
					Player.getPlayer ( action.getPlayer ( ) ).getDataStorage ( )
							.setSetting ( EnumPlayerSetting.PARACHUTE_COLOR , key , true );
					// message
					action.getPlayer ( ).sendMessage (
							EnumSettingsGUIsConfiguration.PARACHUTE_COLOR_GUI_ITEM_SELECTED_MESSAGE.getAsString ( ) );
				} else {
					action.getPlayer ( ).sendMessage (
							EnumSettingsGUIsConfiguration.PARACHUTE_COLOR_GUI_ITEM_LOCKED_MESSAGE.getAsString ( ) );
				}
			} );
		} else {
			return null;
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}