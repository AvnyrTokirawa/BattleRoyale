package es.outlook.adriansrj.battleroyale.gui.setting.parachute;

import es.outlook.adriansrj.battleroyale.enums.EnumSettingsGUIsConfiguration;
import es.outlook.adriansrj.battleroyale.gui.setting.parachute.color.ParachuteColorSettingsGUIHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.itemstack.wool.WoolColor;
import es.outlook.adriansrj.core.util.itemstack.wool.WoolItemStack;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.math.RandomUtil;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author AdrianSR / 17/09/2021 / 08:40 p. m.
 */
public final class ParachuteSettingsMainGUIHandler extends PluginHandler {
	
	public static ParachuteSettingsMainGUIHandler getInstance ( ) {
		return getPluginHandler ( ParachuteSettingsMainGUIHandler.class );
	}
	
	private final Map < UUID, ItemMenu > main_handle_map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public ParachuteSettingsMainGUIHandler ( BattleRoyale plugin ) {
		super ( plugin );
		
		// this task is responsible for updating
		// the guis so players can actually
		// see changes in real-time.
		Bukkit.getScheduler ( ).runTaskTimerAsynchronously ( plugin , ( ) -> {
			for ( Map.Entry < UUID, ItemMenu > next : main_handle_map.entrySet ( ) ) {
				org.bukkit.entity.Player player = Bukkit.getPlayer ( next.getKey ( ) );
				
				if ( player != null && player.isOnline ( ) ) {
					ItemMenu handle = next.getValue ( );
					
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
		ItemMenu handle = this.main_handle_map.computeIfAbsent ( player.getUniqueId ( ) , k -> {
			ItemMenu result = new ItemMenu (
					EnumSettingsGUIsConfiguration.PARACHUTE_MAIN_GUI_TITLE.getAsString ( ) ,
					EnumSettingsGUIsConfiguration.PARACHUTE_MAIN_GUI_SIZE.getAsEnum ( ItemMenuSize.class ) );
			
			result.registerListener ( BattleRoyale.getInstance ( ) );
			return result;
		} );
		
		handle.clear ( );
		
		// parachute settings button
		handle.setItem (
				EnumSettingsGUIsConfiguration.PARACHUTE_MAIN_GUI_PARACHUTE_BUTTON_POSITION.getAsInteger ( ) ,
				new ActionItem ( EnumSettingsGUIsConfiguration.PARACHUTE_MAIN_GUI_PARACHUTE_BUTTON_TEXT.getAsString ( ) ,
								 UniversalMaterial.SADDLE.getItemStack ( )
				).addAction (
						action -> ParachuteSettingsGUIHandler.getInstance ( ).open ( action.getPlayer ( ) ) ) );
		
		// color settings button
		handle.setItem (
				EnumSettingsGUIsConfiguration.PARACHUTE_MAIN_GUI_PARACHUTE_COLOR_BUTTON_POSITION.getAsInteger ( ) ,
				new ActionItem ( EnumSettingsGUIsConfiguration.PARACHUTE_MAIN_GUI_PARACHUTE_COLOR_BUTTON_TEXT.getAsString ( ) ,
								 // this will build a random-color wool item stack,
								 // so every time it updates the icon will change its
								 // material, enabling an interesting effect.
								 new WoolItemStack ( RandomUtil.getRandomElement ( WoolColor.values ( ) ) )
				).addAction (
						action -> ParachuteColorSettingsGUIHandler.getInstance ( ).open ( action.getPlayer ( ) ) ) );
		
		return handle;
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
