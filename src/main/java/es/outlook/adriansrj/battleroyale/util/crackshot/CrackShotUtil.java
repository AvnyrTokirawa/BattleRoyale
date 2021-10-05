package es.outlook.adriansrj.battleroyale.util.crackshot;

import com.shampaggon.crackshot.CSUtility;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Useful class for dealing with the <b>Crack Shot</b> plugin.
 *
 * @author AdrianSR / 12/09/2021 / 03:52 p. m.
 */
public class CrackShotUtil {
	
	protected static final CSUtility HANDLE = new CSUtility ( );
	
	public static boolean giveWeapon ( Player player , String weapon_title , int amount ) {
		return HANDLE.giveWeapon ( player , weapon_title , amount );
	}
	
	public static ItemStack generateWeapon ( String weapon_title ) {
		return HANDLE.generateWeapon ( weapon_title );
	}
}