package es.outlook.adriansrj.battleroyale.parachute.creator;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustom;

/**
 * @author AdrianSR / 23/11/2021 / 12:06 p. m.
 */
abstract class ParachuteShapeDisplayer {
	
	static ParachuteShapeDisplayer of ( Player player , Parachute configuration ) {
		if ( configuration instanceof ParachuteCustom ) {
			return new ParachuteShapeDisplayerCustom ( player , ( ParachuteCustom ) configuration );
		}
		
		return null;
	}
	
	protected final Player    player;
	protected final Parachute configuration;
	
	ParachuteShapeDisplayer ( Player player , Parachute configuration ) {
		this.player        = player;
		this.configuration = configuration;
	}
	
	public abstract void show ( );
	
	public abstract void destroy ( );
}
