package es.outlook.adriansrj.battleroyale.gui;

import java.util.*;

/**
 * {@link GUIIconType}s registry.
 *
 * @author AdrianSR / 29/09/2021 / 08:47 p. m.
 */
public final class GUIIconTypeRegistry {
	
	private static final Map < String, GUIIconType > REGISTERED_TYPES = new HashMap <> ( );
	
	static {
		// registering default types
		for ( GUIIconTypeDefault type : GUIIconTypeDefault.values ( ) ) {
			register ( type );
		}
	}
	
	public static Collection < GUIIconType > getRegisteredTypes ( ) {
		return REGISTERED_TYPES.values ( );
	}
	
	public static GUIIconType getByIdentifier ( String identifier ) {
		return REGISTERED_TYPES.get ( Objects.requireNonNull (
				identifier , "identifier cannot be null" ).toUpperCase ( ) );
	}
	
	public static void register ( GUIIconType type ) {
		REGISTERED_TYPES.put ( type.getIdentifier ( ).toUpperCase ( ) , type );
	}
	
	public static void unregister ( GUIIconType type ) {
		Iterator < String > iterator = REGISTERED_TYPES.keySet ( ).iterator ( );
		
		if ( iterator.hasNext ( ) && Objects.equals (
				type , REGISTERED_TYPES.get ( iterator.next ( ) ) ) ) {
			iterator.remove ( );
		}
	}
	
	private GUIIconTypeRegistry ( ) {
		// singleton
	}
}