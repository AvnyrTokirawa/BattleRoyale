package es.outlook.adriansrj.battleroyale.placeholder.node;

/**
 * @author AdrianSR / 27/11/2021 / 03:39 p. m.
 */
public abstract class PlaceholderTypableNode < T > extends PlaceholderNode {
	
	protected final Class < T > type_class;
	
	protected PlaceholderTypableNode ( Class < T > type_class ) {
		this.type_class = type_class;
	}
	
	public Class < T > getType ( ) {
		return type_class;
	}
	
	protected abstract String onRequest ( T context , String params );
	
	public String onPlaceholderRequest ( Object context , String params ) {
		if ( context != null && type_class.isAssignableFrom ( context.getClass ( ) ) ) {
			String identifier = getSubIdentifier ( ).toLowerCase ( );
			
			if ( params.toLowerCase ( ).startsWith ( identifier ) ) {
				return onRequest ( type_class.cast ( context ) , extractIdentifier ( params ) );
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}