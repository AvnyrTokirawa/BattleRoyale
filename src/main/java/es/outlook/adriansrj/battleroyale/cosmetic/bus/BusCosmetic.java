package es.outlook.adriansrj.battleroyale.cosmetic.bus;

import es.outlook.adriansrj.battleroyale.bus.Bus;
import es.outlook.adriansrj.battleroyale.bus.BusRegistry;
import es.outlook.adriansrj.battleroyale.cosmetic.Cosmetic;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;

import java.util.Objects;

/**
 * Battle Royale {@link Bus} cosmetic.
 *
 * @author AdrianSR / 24/09/2021 / 08:53 p. m.
 */
public class BusCosmetic extends Cosmetic < Bus > {
	
	public static BusCosmetic of ( NamespacedKey key ) {
		return new BusCosmetic ( Objects.requireNonNull (
				BusRegistry.getInstance ( ).getBus ( key ) ,
				"couldn't find any bus registered with this key" ) );
	}
	
	protected final Bus           bus;
	protected final NamespacedKey key;
	
	public BusCosmetic ( Bus bus ) {
		BusRegistry registry = BusRegistry.getInstance ( );
		
		if ( !registry.getRegisteredBuses ( ).contains ( bus ) ) {
			throw new IllegalArgumentException ( "the bus is not registered in the registry" );
		}
		
		this.bus = bus;
		this.key = registry.getRegistrationKey ( bus );
	}
	
	@Override
	public Bus getValue ( ) {
		return bus;
	}
	
	@Override
	public NamespacedKey getKey ( ) {
		return key;
	}
}
