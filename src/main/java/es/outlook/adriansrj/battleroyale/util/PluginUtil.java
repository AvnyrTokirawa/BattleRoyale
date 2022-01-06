package es.outlook.adriansrj.battleroyale.util;

import org.bukkit.Bukkit;

/**
 * Useful class for dealing with plugins.
 *
 * @author AdrianSR / 14/09/2021 / 08:30 p. m.
 */
public class PluginUtil {
	
	public static boolean isProtocolLibEnabled ( ) {
		return isPluginEnabled ( Constants.PROTOCOL_LIB_PLUGIN_NAME );
	}
	
	public static boolean isPlaceholderAPIEnabled ( ) {
		return isPluginEnabled ( Constants.PLACEHOLDER_API_PLUGIN_NAME );
	}
	
	public static boolean isVaultEnabled ( ) {
		return isPluginEnabled ( Constants.VAULT_PLUGIN_NAME );
	}
	
	public static boolean isFeatherBoardEnabled ( ) {
		return isPluginEnabled ( Constants.FEATHERBOARD_PLUGIN_NAME );
	}
	
	public static boolean isQualityArmoryEnabled ( ) {
		return isPluginEnabled ( Constants.QUALITY_ARMORY_PLUGIN_NAME );
	}
	
	public static boolean isQualityArmoryVehiclesEnabled ( ) {
		return isPluginEnabled ( Constants.QUALITY_ARMORY_VEHICLES_PLUGIN_NAME );
	}
	
	public static boolean isCrackShotEnabled ( ) {
		return isPluginEnabled ( Constants.CRACK_SHOT_PLUGIN_NAME );
	}
	
	public static boolean isCrackShotPlusEnabled ( ) {
		return isPluginEnabled ( Constants.CRACK_SHOT_PLUS_PLUGIN_NAME );
	}
	
	public static boolean isMMOItemsEnabled ( ) {
		return isPluginEnabled ( Constants.MMO_ITEMS_PLUGIN_NAME );
		//		try {
		//			Class.forName ( "net.Indyuce.mmoitems.MMOItems" );
		//			return true;
		//		} catch ( ClassNotFoundException ex ) {
		//			return false;
		//		}
	}
	
	public static boolean isPluginEnabled ( String plugin_name ) {
		return Bukkit.getPluginManager ( ).isPluginEnabled ( plugin_name );
	}
}