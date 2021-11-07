package es.outlook.adriansrj.battleroyale.util.time;

import java.util.*;

/**
 * @author AdrianSR / 01/11/2021 / 05:20 p. m.
 */
public class DelayPool implements Iterable < Delay > {
	
	protected static final Map < UUID, DelayPool > DELAY_POOL_MAP = new HashMap <> ( );
	
	public static DelayPool createDelayPool ( ) {
		DelayPool pool = new DelayPool ( );
		
		DELAY_POOL_MAP.put ( pool.id , pool );
		return pool;
	}
	
	public static DelayPool getDelayPool ( UUID id ) {
		return DELAY_POOL_MAP.get ( id );
	}
	
	protected final Map < UUID, Delay > delay_map = new HashMap <> ( );
	protected final UUID                id        = UUID.randomUUID ( );
	
	public UUID getID ( ) {
		return id;
	}
	
	public Collection < Delay > getDelays ( ) {
		return delay_map.values ( );
	}
	
	public Delay createDelay ( UUID id ) {
		if ( delay_map.containsKey ( id ) ) {
			throw new IllegalArgumentException ( "there is a delay already registered with that id" );
		}
		
		Delay delay = new Delay ( id );
		
		delay_map.put ( id , delay );
		return delay;
	}
	
	public Delay createDelay ( ) {
		return createDelay ( UUID.randomUUID ( ) );
	}
	
	public Delay getDelay ( UUID id ) {
		if ( delay_map.containsKey ( id ) ) {
			return delay_map.get ( id );
		} else {
			throw new IllegalArgumentException ( "there is no delay with that id" );
		}
	}
	
	public Delay getOrCreateDelay ( UUID id ) {
		if ( containsDelay ( id ) ) {
			return getDelay ( id );
		} else {
			return createDelay ( id );
		}
	}
	
	public boolean containsDelay ( UUID id ) {
		return delay_map.containsKey ( id );
	}
	
	public void disposeDelay ( UUID id ) {
		// if there is not delay with the provided id
		// it will throw an exception
		getDelay ( id );
		
		// then removing
		delay_map.remove ( id );
	}
	
	@Override
	public Iterator < Delay > iterator ( ) {
		return delay_map.values ( ).iterator ( );
	}
}