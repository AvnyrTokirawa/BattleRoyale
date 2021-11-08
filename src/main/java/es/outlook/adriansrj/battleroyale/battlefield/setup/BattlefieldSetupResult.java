package es.outlook.adriansrj.battleroyale.battlefield.setup;

import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldConfiguration;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.Minimap;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;

/**
 * Wraps the resulting values of the setup of a battlefield.
 *
 * @author AdrianSR / 28/08/2021 / 10:36 a. m.
 */
public interface BattlefieldSetupResult {
	
	/**
	 * Gets the name of the battlefield.
	 *
	 * @return the name of the battlefield.
	 */
	String getName ( );
	
	ZoneBounds getBounds ( );
	
	Minimap getMinimap ( );
	
	BattlefieldConfiguration getConfiguration ( );
}