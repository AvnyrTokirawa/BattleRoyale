package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.bus.BusRegistry;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteRegistry;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;

/**
 * Enumerates the kind of settings for the {@link Player}s.
 *
 * @author AdrianSR / 17/09/2021 / 02:41 p. m.
 */
public enum EnumPlayerSetting {
	
	BUS {
		@Override
		public Object getValue ( NamespacedKey key ) {
			return BusRegistry.getInstance ( ).getBus ( key );
		}
		
		@Override
		public NamespacedKey getDefaultValue ( ) {
			return BusRegistry.DEFAULT_BUS_REGISTRATION_KEY;
		}
	},
	
	PARACHUTE {
		@Override
		public Object getValue ( NamespacedKey key ) {
			return ParachuteRegistry.getInstance ( ).getParachute ( key );
		}
		
		@Override
		public NamespacedKey getDefaultValue ( ) {
			return ParachuteRegistry.DEFAULT_PARACHUTE_REGISTRATION_KEY;
		}
	},
	
	PARACHUTE_COLOR {
		@Override
		public Object getValue ( NamespacedKey key ) {
			return Parachute.Color.of ( key );
		}
		
		@Override
		public NamespacedKey getDefaultValue ( ) {
			return Parachute.Color.WHITE.getKey ( );
		}
	},
	
	;
	
	public < T > T getValue ( Class < T > clazz , NamespacedKey key ) {
		return clazz.cast ( getValue ( key ) );
	}
	
	public abstract Object getValue ( NamespacedKey key );
	
	public abstract NamespacedKey getDefaultValue ( );
}
