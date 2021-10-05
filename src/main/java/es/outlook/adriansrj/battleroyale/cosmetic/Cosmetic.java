package es.outlook.adriansrj.battleroyale.cosmetic;

import es.outlook.adriansrj.battleroyale.cosmetic.bus.BusCosmetic;
import es.outlook.adriansrj.battleroyale.cosmetic.parachute.ParachuteCosmetic;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;

import java.util.Objects;

/**
 * Represents a Battle Royale cosmetic.
 *
 * @author AdrianSR / 18/09/2021 / 12:44 p. m.
 */
public abstract class Cosmetic < T > {
	
	public static Cosmetic < ? > of ( NamespacedKey key ) {
		// as bus
		if ( Constants.BUS_NAMESPACE.equals ( key.getNamespace ( ) ) ) {
			return BusCosmetic.of ( key );
		}
		
		// as parachute
		if ( Constants.PARACHUTE_NAMESPACE.equals ( key.getNamespace ( ) ) ) {
			return ParachuteCosmetic.of ( key );
		}
		
		throw new UnsupportedOperationException ( "unsupported namespace '" + key.getNamespace ( ) + "'" );
	}
	
	public abstract T getValue ( );
	
	public abstract NamespacedKey getKey ( );
	
	@Override
	public boolean equals ( Object obj ) {
		if ( obj instanceof Cosmetic ) {
			return Objects.equals ( getKey ( ) , ( ( Cosmetic < ? > ) obj ).getKey ( ) );
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode ( ) {
		return getKey ( ).hashCode ( );
	}
}