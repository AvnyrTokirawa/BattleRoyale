package es.outlook.adriansrj.battleroyale.util;

import es.outlook.adriansrj.battleroyale.bus.Bus;
import es.outlook.adriansrj.battleroyale.bus.BusRegistry;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.PlayerDataStorage;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteRegistry;

import java.util.Objects;

/**
 * Useful class for dealing with cosmetics.
 *
 * @author AdrianSR / 07/11/2021 / 12:40 p. m.
 */
public class CosmeticUtil {
	
	/**
	 * Checks whether the provided {@link Bus} is unlocked
	 * for the provided {@link Player}.
	 *
	 * @param bus the bus to check.
	 * @param player the player to check.
	 * @return whether the provided bus is unlocked for the provided player.
	 */
	public static boolean isUnlocked ( Bus bus , org.bukkit.entity.Player player ) {
		if ( bus.getPermission ( ) == null || player.hasPermission ( bus.getPermission ( ) ) ) {
			return true;
		} else {
			NamespacedKey     key          = BusRegistry.getInstance ( ).getRegistrationKey ( bus );
			PlayerDataStorage data_storage = Player.getPlayer ( player ).getDataStorage ( );
			
			return data_storage.getCosmeticsByReturnType ( Bus.class ).stream ( )
					.anyMatch ( bus_cosmetic -> Objects.equals ( bus_cosmetic.getKey ( ) , key ) );
		}
	}
	
	/**
	 * Checks whether the provided {@link Parachute} is unlocked
	 * for the provided {@link Player}.
	 *
	 * @param parachute the parachute to check.
	 * @param player the player to check.
	 * @return whether the provided parachute is unlocked for the provided player.
	 */
	public static boolean isUnlocked ( Parachute parachute , org.bukkit.entity.Player player ) {
		if ( parachute.getPermission ( ) == null || player.hasPermission ( parachute.getPermission ( ) ) ) {
			return true;
		} else {
			NamespacedKey     key          = ParachuteRegistry.getInstance ( ).getRegistrationKey ( parachute );
			PlayerDataStorage data_storage = Player.getPlayer ( player ).getDataStorage ( );
			
			return data_storage.getCosmeticsByReturnType ( Parachute.class ).stream ( )
					.anyMatch ( parachute_cosmetic -> Objects.equals ( parachute_cosmetic.getKey ( ) , key ) );
		}
	}
	
	/**
	 * Checks whether the provided {@link Parachute.Color} is unlocked
	 * for the provided {@link Player}.
	 *
	 * @param color the color to check.
	 * @param player the player to check.
	 * @return whether the provided parachute color is unlocked for the provided player.
	 */
	public static boolean isUnlocked ( Parachute.Color color , org.bukkit.entity.Player player ) {
		if ( color.getPermission ( ) == null || player.hasPermission ( color.getPermission ( ) ) ) {
			return true;
		} else {
			NamespacedKey     key          = color.getKey ( );
			PlayerDataStorage data_storage = Player.getPlayer ( player ).getDataStorage ( );
			
			return data_storage.getCosmeticsByReturnType ( Parachute.Color.class ).stream ( )
					.anyMatch ( color_cosmetic -> Objects.equals ( color_cosmetic.getKey ( ) , key ) );
		}
	}
}