package es.outlook.adriansrj.battleroyale.cosmetic.parachute;

import es.outlook.adriansrj.battleroyale.cosmetic.Cosmetic;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteRegistry;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;

import java.util.Objects;

/**
 * Battle Royale {@link Parachute} cosmetic.
 *
 * @author AdrianSR / 18/09/2021 / 12:45 p. m.
 */
public class ParachuteCosmetic extends Cosmetic < Parachute > {
	
	public static ParachuteCosmetic of ( NamespacedKey key ) {
		return new ParachuteCosmetic ( Objects.requireNonNull (
				ParachuteRegistry.getInstance ( ).getParachute ( key ) ,
				"couldn't find any parachute registered with this key" ) );
	}
	
	protected final Parachute     parachute;
	protected final NamespacedKey key;
	
	public ParachuteCosmetic ( Parachute parachute ) {
		ParachuteRegistry registry = ParachuteRegistry.getInstance ( );
		
		if ( !registry.getRegisteredParachutes ( ).contains ( parachute ) ) {
			throw new IllegalArgumentException ( "the parachute is not registered in the registry" );
		}
		
		this.parachute = parachute;
		this.key       = registry.getRegistrationKey ( parachute );
	}
	
	@Override
	public Parachute getValue ( ) {
		return parachute;
	}
	
	@Override
	public NamespacedKey getKey ( ) {
		return key;
	}
}
