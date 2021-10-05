package es.outlook.adriansrj.battleroyale.exception;

import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShapePart;

/**
 * Thrown whenever a file that is supposed to store a {@link BattlefieldShapePart}
 * has an invalid name.
 *
 * @author AdrianSR / 25/09/2021 / 09:45 p. m.
 */
public class InvalidShapePartFileName extends IllegalStateException {
	
	public InvalidShapePartFileName ( ) {
		// no message
	}
	
	public InvalidShapePartFileName ( String s ) {
		super ( s );
	}
	
	public InvalidShapePartFileName ( String message , Throwable cause ) {
		super ( message , cause );
	}
	
	public InvalidShapePartFileName ( Throwable cause ) {
		super ( cause );
	}
}
