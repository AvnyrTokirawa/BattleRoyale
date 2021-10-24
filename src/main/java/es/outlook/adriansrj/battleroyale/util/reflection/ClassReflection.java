package es.outlook.adriansrj.battleroyale.util.reflection;

import es.outlook.adriansrj.core.util.reflection.DataType;

import java.util.Objects;

/**
 * Useful class for dealing with classes.
 *
 * @author AdrianSR / 22/08/2021 / Time: 09:06 p. m.
 */
public class ClassReflection extends es.outlook.adriansrj.core.util.reflection.general.ClassReflection {
	
	public static boolean isPrimitiveWrapper ( Object input ) {
		return input instanceof Integer || input instanceof Boolean || input instanceof Character
				|| input instanceof Byte || input instanceof Short || input instanceof Double || input instanceof Long
				|| input instanceof Float;
	}
	
	public static boolean compatibleTypes ( Class < ? > class_a , Class < ? > class_b ) {
		if ( primitiveTypeCheck ( class_a , class_b ) ) {
			return true;
		} else {
			return ( isNumericType ( class_a ) && isNumericType ( class_b ) )
					|| ( class_a.isAssignableFrom ( class_b ) || class_b.isAssignableFrom ( class_a ) );
		}
	}
	
	public static boolean compatibleTypes ( Class < ? > clazz , Object to_check ) {
		if ( primitiveTypeCheck ( clazz , ( to_check != null ? to_check.getClass ( ) : null ) ) ) {
			return true;
		} else {
			return to_check != null && ( ( isNumericType ( clazz ) && to_check instanceof Number )
					|| clazz.isAssignableFrom ( to_check.getClass ( ) ) );
		}
	}
	
	protected static boolean primitiveTypeCheck ( Class < ? > class_a , Class < ? > class_b ) {
		DataType type_a = DataType.fromClass ( class_a );
		DataType type_b = DataType.fromClass ( class_b );
		
		return type_a != null && type_b != null && Objects.equals ( type_a , type_b );
	}
	
	public static boolean isNumericType ( Class < ? > clazz ) {
		if ( Number.class.isAssignableFrom ( clazz ) ) {
			return true;
		} else {
			DataType type = DataType.fromClass ( clazz );
			
			if ( type != null ) {
				switch ( type ) {
					case BYTE:
					case DOUBLE:
					case FLOAT:
					case INTEGER:
					case LONG:
					case SHORT:
						return true;
					
					default:
						break;
				}
			}
		}
		return false;
	}
}
