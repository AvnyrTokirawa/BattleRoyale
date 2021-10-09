package es.outlook.adriansrj.battleroyale.gui.setting.bus;

import es.outlook.adriansrj.battleroyale.bus.Bus;
import es.outlook.adriansrj.battleroyale.bus.BusRegistry;
import es.outlook.adriansrj.battleroyale.bus.dragon.BusDragon;
import es.outlook.adriansrj.battleroyale.bus.pet.BusPet;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.enums.EnumPlayerSetting;
import es.outlook.adriansrj.battleroyale.enums.EnumSettingsGUIsConfiguration;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.game.player.Player;
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
 * @author AdrianSR / 25/09/2021 / 10:14 a. m.
 */
public final class BusSettingsGUIHandler extends PluginHandler {
	
	public static BusSettingsGUIHandler getInstance ( ) {
		return getPluginHandler ( BusSettingsGUIHandler.class );
	}
	
	private final Map < UUID, BookItemMenu > handle_map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BusSettingsGUIHandler ( BattleRoyale plugin ) {
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
					EnumSettingsGUIsConfiguration.BUS_GUI_TITLE.getAsString ( ) ,
					EnumSettingsGUIsConfiguration.BUS_GUI_SIZE.getAsEnum ( ItemMenuSize.class ) );
			
			result.registerListener ( BattleRoyale.getInstance ( ) );
			return result;
		} );
		
		handle.clear ( );
		
		// bus items
		for ( Bus bus : BusRegistry.getInstance ( ).getRegisteredBuses ( ) ) {
			handle.addItem ( buildBusItem ( player , bus ) );
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
	
	private synchronized Item buildBusItem ( org.bukkit.entity.Player player , Bus bus ) {
		NamespacedKey     key      = BusRegistry.getInstance ( ).getRegistrationKey ( bus );
		UniversalMaterial material = UniversalMaterial.MINECART;
		final boolean     unlocked = bus.getPermission ( ) == null || player.hasPermission ( bus.getPermission ( ) );
		
		if ( bus instanceof BusDragon ) {
			throw new UnsupportedOperationException ( BusDragon.class.getSimpleName ( ) + " not supported" );
		} else if ( bus instanceof BusPet ) {
			material = UniversalMaterial.PUFFERFISH;
		}
		
		// bus must be in the registry
		if ( key != null ) {
			String display_text = String.format ( ( unlocked
					? EnumSettingsGUIsConfiguration.BUS_GUI_ITEM_UNLOCKED_TEXT_FORMAT
					: EnumSettingsGUIsConfiguration.BUS_GUI_ITEM_LOCKED_TEXT_FORMAT )
														  .getAsString ( ) , StringUtil.capitalize (
					key.getKey ( ).replace ( "_" , "" ).replace ( "-" , "" ) ) );
			
			List < String > display_description = ( unlocked
					? EnumSettingsGUIsConfiguration.BUS_GUI_ITEM_UNLOCKED_DESCRIPTION_FORMAT
					: EnumSettingsGUIsConfiguration.BUS_GUI_ITEM_LOCKED_DESCRIPTION_FORMAT ).getAsStringList ( );
			
			return new ActionItem ( display_text , material.getItemStack ( ) , display_description ).addAction ( action -> {
				action.setClose ( true );
				
				// setting and immediately uploading
				if ( unlocked ) {
					Player.getPlayer ( action.getPlayer ( ) ).getDataStorage ( )
							.setSetting ( EnumPlayerSetting.BUS , key , true );
					
					// message
					action.getPlayer ( ).sendMessage (
							EnumSettingsGUIsConfiguration.BUS_GUI_ITEM_SELECTED_MESSAGE.getAsString ( ) );
				} else {
					action.getPlayer ( ).sendMessage (
							EnumSettingsGUIsConfiguration.BUS_GUI_ITEM_LOCKED_MESSAGE.getAsString ( ) );
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