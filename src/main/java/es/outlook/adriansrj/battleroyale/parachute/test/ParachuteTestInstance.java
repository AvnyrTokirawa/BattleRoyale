package es.outlook.adriansrj.battleroyale.parachute.test;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteInstance;
import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustomModel;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;

/**
 * {@link ParachuteTest} instance.
 *
 * @author AdrianSR / 09/09/2021 / 08:56 p. m.
 */
public class ParachuteTestInstance extends ParachuteInstanceTest {
	
	/** the current handle */
	protected       ParachuteTestInstanceHandle handle;
	protected final BattleRoyaleArena           arena;
	protected final Location                    spawn;
	
	public ParachuteTestInstance ( ParachuteTest configuration , BattleRoyaleArena arena , Location spawn ) {
		super ( configuration );
		
		this.arena = arena;
		this.spawn = spawn;
	}
	
	public ParachuteTestInstance ( BattleRoyaleArena arena , Location spawn ) {
		this ( new ParachuteTest ( ParachuteCustomModel.DEFAULT_MODEL ) , arena , spawn );
	}
	
	@Override
	public ParachuteTest getConfiguration ( ) {
		return ( ParachuteTest ) configuration;
	}
	
	@Override
	public boolean isOpen ( ) {
		return handle != null && handle.started && !handle.destroyed;
	}
	
	@Override
	public void open ( ) {
		if ( handle == null || handle.destroyed ) {
			handle = new ParachuteTestInstanceHandle ( this );
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