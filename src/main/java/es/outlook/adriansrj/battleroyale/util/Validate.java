package es.outlook.adriansrj.battleroyale.util;

import es.outlook.adriansrj.core.util.Validable;

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
	
	public static < T extends Validable > T isValid ( T validable , String message ) {
		isTrue ( validable.isValid ( ) , message );
		return validable;
	}
	
	public static < T extends Validable > T isValid ( T validable ) {
		return isValid ( validable , validable + " cannot be invalid" );
	}
}