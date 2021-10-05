package es.outlook.adriansrj.battleroyale.gui;

import org.bukkit.configuration.ConfigurationSection;

/**
 * GUI icon type.
 *
 * @author AdrianSR / 29/09/2021 / 08:36 p. m.
 */
public interface GUIIconType {
	
	default String getIdentifier ( ) {
		// default identifier
		Class < ? > clazz = getClass ( );
		
		if ( this instanceof Enum ) {
			return ( ( Enum < ? > ) this ).name ( );
		} else {
			String        class_name       = clazz.getSimpleName ( );
			String        class_name_upper = class_name.toUpperCase ( );
			StringBuilder builder          = new StringBuilder ( );
			
			for ( int i = 0 ; i < class_name.length ( ) ; i++ ) {
				char c       = class_name.charAt ( i );
				char c_upper = class_name_upper.charAt ( i );
				
				// separator
				if ( i > 0 && Character.isUpperCase ( c )
						// previous character must not be upper case
						&& !Character.isUpperCase ( class_name.charAt ( i - 1 ) )
						// next character must not be upper case
						&& ( i + 1 >= class_name.length ( )
						|| !Character.isUpperCase ( class_name.charAt ( i + 1 ) ) ) ) {
					builder.append ( '_' );
				}
				
				builder.append ( c_upper );
			}
			
			return builder.toString ( );
		}
	}
	
	GUIIcon load ( ConfigurationSection section );
}