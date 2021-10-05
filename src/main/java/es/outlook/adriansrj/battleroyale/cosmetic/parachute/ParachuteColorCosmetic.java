package es.outlook.adriansrj.battleroyale.cosmetic.parachute;

import es.outlook.adriansrj.battleroyale.cosmetic.Cosmetic;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;

import java.util.Objects;

/**
 * Battle Royale {@link Parachute.Color} cosmetic.
 *
 * @author AdrianSR / 18/09/2021 / 06:44 p. m.
 */
public class ParachuteColorCosmetic extends Cosmetic < Parachute.Color > {
	
	public static ParachuteColorCosmetic of ( NamespacedKey key ) {
		return new ParachuteColorCosmetic ( Objects.requireNonNull (
				Parachute.Color.of ( key ) , "couldn't find any color with this key" ) );
	}
	
	protected final Parachute.Color color;
	
	public ParachuteColorCosmetic ( Parachute.Color color ) {
		this.color = color;
	}
	
	@Override
	public Parachute.Color getValue ( ) {
		return color;
	}
	
	@Override
	public NamespacedKey getKey ( ) {
		return color.getKey ( );
	}
}
