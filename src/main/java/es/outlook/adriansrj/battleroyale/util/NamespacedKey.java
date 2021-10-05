package es.outlook.adriansrj.battleroyale.util;

import org.apache.commons.lang3.Validate;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a String based key which consists of two components - a namespace
 * and a key.
 *
 * Namespaces may only contain lowercase alphanumeric characters, periods,
 * underscores, and hyphens.
 * <p>
 * Keys may only contain lowercase alphanumeric characters, periods,
 * underscores, hyphens, and forward slashes.
 *
 * @author AdrianSR / 18/09/2021 / 12:48 p. m.
 */
public class NamespacedKey {
	
	protected static final Pattern VALID_NAMESPACE = Pattern.compile ( "[a-zA-Z0-9._-]+" );
	protected static final Pattern VALID_KEY       = Pattern.compile ( "[a-zA-Z0-9/._-]+" );
	
	public static NamespacedKey of ( String namespacedkey ) {
		if ( namespacedkey.indexOf ( ':' ) != -1 ) {
			String[] split = namespacedkey.split ( ":" );
			
			return new NamespacedKey ( split[ 0 ] , split[ 1 ] );
		} else {
			throw new IllegalArgumentException ( "unknown namespace for '" + namespacedkey + "'" );
		}
	}
	
	protected final String namespace;
	protected final String key;
	
	public NamespacedKey ( String namespace , String key ) {
		Validate.isTrue ( VALID_NAMESPACE.matcher ( namespace ).matches ( ) , "invalid namespace" );
		Validate.isTrue ( VALID_KEY.matcher ( key ).matches ( ) , "invalid key" );
		
		this.namespace = namespace.toLowerCase ( Locale.ROOT ).trim ( );
		this.key       = key.toLowerCase ( Locale.ROOT ).trim ( );
		
	}
	
	public String getNamespace ( ) {
		return namespace;
	}
	
	public String getKey ( ) {
		return key;
	}
	
	@Override
	public String toString ( ) {
		return this.namespace + ":" + this.key;
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		NamespacedKey that = ( NamespacedKey ) o;
		return namespace.equals ( that.namespace ) && key.equals ( that.key );
	}
	
	@Override
	public int hashCode ( ) {
		return Objects.hash ( namespace , key );
	}
}
