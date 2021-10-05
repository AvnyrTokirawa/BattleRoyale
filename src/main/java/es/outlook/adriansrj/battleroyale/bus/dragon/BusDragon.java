package es.outlook.adriansrj.battleroyale.bus.dragon;

import es.outlook.adriansrj.battleroyale.bus.Bus;
import es.outlook.adriansrj.battleroyale.player.Player;

/**
 * Bus dragon configuration.
 *
 * @author AdrianSR / 23/09/2021 / 09:10 p. m.
 */
public final class BusDragon extends Bus {
	
	@Override
	public boolean isValid ( ) {
		return true;
	}
	
	public BusDragonInstance createInstance ( ) {
		return new BusDragonInstance ( this );
	}
	
	/**
	 *
	 * @param player
	 * @return
	 * @see #createInstance()
	 */
	@Override
	public BusDragonInstance createInstance ( Player player ) {
		return createInstance ( );
	}
}
