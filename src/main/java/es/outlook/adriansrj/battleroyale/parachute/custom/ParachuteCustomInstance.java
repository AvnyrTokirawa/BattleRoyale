package es.outlook.adriansrj.battleroyale.parachute.custom;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteInstance;
import org.apache.commons.lang.Validate;

/**
 * {@link ParachuteCustom} instance.
 *
 * @author AdrianSR / 09/09/2021 / 08:56 p. m.
 */
public class ParachuteCustomInstance extends ParachuteInstance {
	
	/** the current handle */
	protected ParachuteCustomInstanceHandle handle;
	
	public ParachuteCustomInstance ( Player player , ParachuteCustom configuration ) {
		super ( player , configuration );
	}
	
	public ParachuteCustomInstance ( Player player ) {
		this ( player , new ParachuteCustom ( ParachuteCustomModel.DEFAULT_MODEL ) );
	}
	
	@Override
	public ParachuteCustom getConfiguration ( ) {
		return ( ParachuteCustom ) configuration;
	}
	
	@Override
	public boolean isOpen ( ) {
		return handle != null && handle.started && !handle.destroyed;
	}
	
	@Override
	public void open ( ) {
		Validate.notNull ( player.getArena ( ) , "player must be in an arena" );
		
		if ( handle == null || handle.destroyed ) {
			handle = new ParachuteCustomInstanceHandle ( this );
			handle.start ( );
		}
	}
	
	@Override
	public void close ( ) {
		if ( handle != null && !handle.destroyed ) {
			handle.destroy ( );
		}
		
		handle = null;
	}
}