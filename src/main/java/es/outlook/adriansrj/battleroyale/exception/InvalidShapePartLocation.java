package es.outlook.adriansrj.battleroyale.exception;

import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShapePart;

/**
 * Thrown whenever a location that is supposed to locate
 * a certain {@link BattlefieldShapePart} is invalid.
 *
 * @author AdrianSR / 25/09/2021 / 09:45 p. m.
 */
public class InvalidShapePartLocation extends IllegalStateException {
	
	public InvalidShapePartLocation ( ) {
		// no message
	}
	
	public InvalidShapePartLocation ( String s ) {
		super ( s );
	}
	
	public InvalidShapePartLocation ( String message , Throwable cause ) {
		super ( message , cause );
	}
	
	public InvalidShapePartLocation ( Throwable cause ) {
		super ( cause );
	}
}
