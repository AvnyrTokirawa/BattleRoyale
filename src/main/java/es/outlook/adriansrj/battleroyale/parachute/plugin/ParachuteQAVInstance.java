package es.outlook.adriansrj.battleroyale.parachute.plugin;

import es.outlook.adriansrj.battleroyale.parachute.ParachuteInstance;
import es.outlook.adriansrj.battleroyale.player.Player;
import org.apache.commons.lang.Validate;

/**
 * {@link ParachuteQAV} instance.
 *
 * @author AdrianSR / 12/09/2021 / 10:12 a. m.
 */
public class ParachuteQAVInstance extends ParachuteInstance {
	
	/** the current handle */
	protected ParachuteQAVInstanceHandle handle;
	
	public ParachuteQAVInstance ( Player player , ParachuteQAV configuration ) {
		super ( player , configuration );
	}
	
	@Override
	public ParachuteQAV getConfiguration ( ) {
		return ( ParachuteQAV ) super.getConfiguration ( );
	}
	
	@Override
	public boolean isOpen ( ) {
		return handle != null && handle.started && !handle.destroyed;
	}
	
	@Override
	public void open ( ) {
		Validate.notNull ( player.getArena ( ) , "player must be in an arena" );
		
		if ( handle == null || handle.destroyed ) {
			this.handle = new ParachuteQAVInstanceHandle ( this );
			this.handle.start ( );
		}
	}
	
	@Override
	public void close ( ) {
		if ( handle != null ) {
			handle.destroy ( );
			handle = null;
		}
	}
}
