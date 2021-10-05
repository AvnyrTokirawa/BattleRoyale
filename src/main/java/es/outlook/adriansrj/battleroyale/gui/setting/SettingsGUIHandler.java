package es.outlook.adriansrj.battleroyale.gui.setting;

import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.enums.EnumSettingsGUIsConfiguration;
import es.outlook.adriansrj.battleroyale.gui.setting.bus.BusSettingsGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setting.parachute.ParachuteSettingsGUIHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.item.action.close.CloseMenuActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Player settings GUI.
 *
 * @author AdrianSR / 17/09/2021 / 08:33 p. m.
 */
public final class SettingsGUIHandler extends PluginHandler {
	
	public static SettingsGUIHandler getInstance ( ) {
		return getPluginHandler ( SettingsGUIHandler.class );
	}
	
	private final ItemMenu handle;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public SettingsGUIHandler ( BattleRoyale plugin ) {
		super ( plugin );
		
		this.handle = new ItemMenu ( EnumSettingsGUIsConfiguration.MAIN_GUI_TITLE.getAsString ( ) ,
									 ItemMenuSize.THREE_LINE );
		this.handle.registerListener ( plugin );
	}
	
	public void open ( org.bukkit.entity.Player player ) {
		this.build ( );
		this.handle.open ( player );
	}
	
	public void open ( Player player ) {
		player.getBukkitPlayerOptional ( ).ifPresent ( this :: open );
	}
	
	public void update ( org.bukkit.entity.Player player ) {
		this.build ( );
		this.handle.update ( player );
	}
	
	private void build ( ) {
		this.handle.clear ( );
		
		this.handle.setItem (
				EnumSettingsGUIsConfiguration.MAIN_GUI_BUS_SETTINGS_BUTTON_POSITION.getAsInteger ( ) ,
				new ActionItem ( EnumSettingsGUIsConfiguration.MAIN_GUI_BUS_SETTINGS_BUTTON_TEXT.getAsString ( ) ,
								 new ItemStack ( Objects.requireNonNull (
										 EnumSettingsGUIsConfiguration.MAIN_GUI_BUS_SETTINGS_BUTTON_MATERIAL.getAsItemStack ( ) ) )
				).addAction (
						action -> BusSettingsGUIHandler.getInstance ( ).open ( action.getPlayer ( ) ) ) );
		
		this.handle.setItem (
				EnumSettingsGUIsConfiguration.MAIN_GUI_PARACHUTE_SETTINGS_BUTTON_POSITION.getAsInteger ( ) ,
				new ActionItem ( EnumSettingsGUIsConfiguration.MAIN_GUI_PARACHUTE_SETTINGS_BUTTON_TEXT.getAsString ( ) ,
								 new ItemStack ( Objects.requireNonNull (
										 EnumSettingsGUIsConfiguration.MAIN_GUI_PARACHUTE_SETTINGS_BUTTON_MATERIAL.getAsItemStack ( ) ) )
				).addAction (
						action -> ParachuteSettingsGUIHandler.getInstance ( ).open ( action.getPlayer ( ) ) ) );
		
		// close button
		this.handle.setItem (
				EnumSettingsGUIsConfiguration.MAIN_GUI_CLOSE_BUTTON_POSITION.getAsInteger ( ) ,
				new CloseMenuActionItem ( ChatColor.DARK_RED + EnumLanguage.CLOSE_WORD.getAsStringStripColors ( ) ,
										  EnumSettingsGUIsConfiguration.COMMON_BUTTON_CLOSE_MATERIAL.getAsItemStack ( ) ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}