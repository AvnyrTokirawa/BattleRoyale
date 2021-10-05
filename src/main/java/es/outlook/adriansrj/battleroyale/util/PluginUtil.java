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
	
	public static boolean isQualityArmoryVehiclesEnabled ( ) {
		return isPluginEnabled ( Constants.QUALITY_ARMORY_VEHICLES_PLUGIN_NAME );
	}
	
	public static boolean isPluginEnabled ( String plugin_name ) {
		return Bukkit.getPluginManager ( ).isPluginEnabled ( plugin_name );
	}
}