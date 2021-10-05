package es.outlook.adriansrj.battleroyale.util.qualityarmory;

import me.zombie_striker.customitemmanager.CustomBaseObject;
import me.zombie_striker.qg.api.QualityArmory;
import org.bukkit.inventory.ItemStack;

/**
 * Useful class for dealing with the <b>Quality Armory</b> plugin.
 *
 * @author AdrianSR / 12/09/2021 / 03:40 p. m.
 */
public class QualityArmoryUtil {
	
	public static CustomBaseObject getCustomItemByName ( String name ) {
		return QualityArmory.getCustomItemByName ( name );
	}
	
	public static ItemStack getCustomItemAsItemStack ( CustomBaseObject object ) {
		return QualityArmory.getCustomItemAsItemStack ( object );
	}
	
	public static ItemStack getCustomItemAsItemStackByName ( String name ) {
		CustomBaseObject object = getCustomItemByName ( name );
		
		if ( object != null ) {
			return getCustomItemAsItemStack ( object );
		} else {
			return null;
		}
	}
}