package es.outlook.adriansrj.battleroyale.util.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.inventory.ItemStack;

/**
 * Useful class for with the <b>MMOItems</b> plugin.
 *
 * @author AdrianSR / 12/11/2021 / 10:06 a. m.
 */
public class MMOItemsUtil {
	
	public static ItemStack getItemInstance ( String type_name , String id ) {
		return MMOItems.plugin.getItem ( type_name , id );
	}
}