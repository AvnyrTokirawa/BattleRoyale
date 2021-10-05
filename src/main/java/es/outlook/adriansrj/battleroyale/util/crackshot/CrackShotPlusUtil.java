package es.outlook.adriansrj.battleroyale.util.crackshot;

import me.DeeCaaD.CrackShotPlus.API;
import me.DeeCaaD.CrackShotPlus.CSPapi;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Useful class for dealing with the <b>Crack Shot Plus</b> plugin.
 *
 * @author AdrianSR / 12/09/2021 / 03:52 p. m.
 */
public class CrackShotPlusUtil {
	
	public static ItemStack generateWeapon ( String weapon_name ) {
		return API.getCSUtility ( ).generateWeapon ( weapon_name );
	}
	
	public static ItemStack updateItemStackFeatures ( Player player , String weapon_name ) {
		ItemStack weapon_item = generateWeapon ( weapon_name );
		
		return weapon_item != null ? CSPapi.updateItemStackFeatures ( weapon_name , weapon_item , player ) : null;
	}
	
	public static ItemStack updateItemStackFeaturesNonPlayer ( String weapon_name ) {
		ItemStack weapon_item = generateWeapon ( weapon_name );
		
		return weapon_item != null ? CSPapi.updateItemStackFeaturesNonPlayer ( weapon_name , weapon_item ) : null;
	}
}