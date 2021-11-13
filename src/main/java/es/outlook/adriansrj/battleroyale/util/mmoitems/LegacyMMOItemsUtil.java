package es.outlook.adriansrj.battleroyale.util.mmoitems;

import es.outlook.adriansrj.core.util.reflection.general.MethodReflection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

/**
 * Useful class for with the legacy <b>MMOItems</b> plugin.
 *
 * @author AdrianSR / 12/11/2021 / 08:46 a. m.
 */
public class LegacyMMOItemsUtil {
	
	public static ItemStack getItemInstance ( String type_name , String id ) {
		try {
			Class < ? extends JavaPlugin > plugin_class = Class.forName (
					"net.Indyuce.mmoitems.MMOItems" ).asSubclass ( JavaPlugin.class );
			Class < ? > type_class = Class.forName ( "net.Indyuce.mmoitems.api.Type" );
			
			JavaPlugin plugin_instance = JavaPlugin.getPlugin ( plugin_class );
			Object types = MethodReflection.getAccessible (
					plugin_class , "getTypes" ).invoke ( plugin_instance );
			Object type = MethodReflection.getAccessible (
					types.getClass ( ) , "get" , String.class ).invoke ( types , type_name );
			
			if ( type != null ) {
				return ( ItemStack ) MethodReflection.getAccessible (
						plugin_class , "getItem" , type_class , String.class ).invoke ( null , type , id );
			} else {
				return null;
			}
		} catch ( ClassNotFoundException | NoSuchMethodException
				| InvocationTargetException | IllegalAccessException e ) {
			throw new IllegalStateException ( e );
		}
		
		//		MMOItems mmoitems = JavaPlugin.getPlugin ( MMOItems.class );
		//		Type     type     = mmoitems.getTypes ( ).get ( type_name );
		//
		//		if ( type != null ) {
		//			return MMOItems.getItem ( type , id );
		//		} else {
		//			return null;
		//		}
	}
}