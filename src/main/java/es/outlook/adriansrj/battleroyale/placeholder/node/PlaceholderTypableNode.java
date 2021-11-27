package es.outlook.adriansrj.battleroyale.placeholder.node;

import org.bukkit.entity.Player;

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
	
	public String set ( T context , String contents ) {
		
		return null;
	}
	
	@Override
	protected String onRequest ( Player player , String params ) {
		// not supported by default
		return null;
	}
	
	protected abstract String onRequest ( T context , String params );
}