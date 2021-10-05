package es.outlook.adriansrj.battleroyale.util;

/**
 * Useful class for dealing with {@link NamespacedKey}s.
 *
 * @author AdrianSR / 18/09/2021 / 01:45 p. m.
 */
public class NamespacedKeyUtil {
	
	public static NamespacedKey ofBus ( String registration_name ) {
		return new NamespacedKey ( Constants.BUS_NAMESPACE , registration_name );
	}
	
	public static NamespacedKey ofParachute ( String registration_name ) {
		return new NamespacedKey ( Constants.PARACHUTE_NAMESPACE , registration_name );
	}
	
	public static NamespacedKey ofParachuteColor ( String name ) {
		return new NamespacedKey ( Constants.PARACHUTE_COLOR_NAMESPACE , name );
	}
}