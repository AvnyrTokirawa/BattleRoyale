package es.outlook.adriansrj.battleroyale.util;

import java.util.Objects;

/**
 * Custom fork of {@link org.apache.commons.lang3.Validate}.
 *
 * @author AdrianSR / 18/09/2021 / 01:35 p. m.
 */
public class Validate extends org.apache.commons.lang3.Validate {
	
	public static NamespacedKey namespace ( String namespace , NamespacedKey namespacedkey ) {
		isTrue ( Objects.equals ( namespace , namespacedkey.getNamespace ( ) ) ,
				 "namespace mismatch: " + namespace + " != " + namespacedkey.getNamespace ( ) );
		
		return namespacedkey;
	}
}