package es.outlook.adriansrj.battleroyale.parachute.custom;

import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustomPartPosition;
import es.outlook.adriansrj.core.util.math.Vector2D;

/**
 * @author AdrianSR / Tuesday 20 October, 2020 / 11:40 AM
 */
public class PositionData {
	
	/**
	 * The towards Z ( represents the rotation 0 ).
	 */
	protected static final Vector2D TOWARD_Z = new Vector2D ( 0F , 1F );
	
	/** the angle from rotation 0 to part position */
	protected final float angle;
	/** the distance excluding the component Y */
	protected final float xz_distance;
	/** the distance excluding the components X and Z */
	protected final float y_distance;
	
	/**
	 * Construct the position data from the provided {@link ParachuteCustomPartPosition}.
	 * <p>
	 * @param position the position.
	 */
	protected PositionData ( final ParachuteCustomPartPosition position ) {
		this.angle       = ( float ) new Vector2D ( position.getX ( ) , position.getZ ( ) ).normalize ( )
				.angle ( TOWARD_Z );
		this.xz_distance = ( float ) Math.sqrt (
				( position.getX ( ) * position.getX ( ) ) + ( position.getZ ( ) * position.getZ ( ) ) );
		this.y_distance  = position.getY ( );
	}
}