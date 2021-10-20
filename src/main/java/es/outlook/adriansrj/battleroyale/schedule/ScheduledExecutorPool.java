package es.outlook.adriansrj.battleroyale.schedule;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author AdrianSR / 15/10/2021 / 12:52 p. m.
 */
public final class ScheduledExecutorPool extends PluginHandler {
	
	public static ScheduledExecutorPool getInstance ( ) {
		return getPluginHandler ( ScheduledExecutorPool.class );
	}
	
	/**
	 * @author AdrianSR / 15/10/2021 / 01:01 p. m.
	 */
	enum Kind {
		
		SINGLE_THREAD_SCHEDULED_EXECUTOR,
		WORK_STEALING_POOL,
	}
	
	private final EnumMap < Kind, Set < ExecutorService > > cache = new EnumMap <> ( Kind.class );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public ScheduledExecutorPool ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	public ScheduledExecutorService getNewSingleThreadScheduledExecutor ( ) {
		Set < ExecutorService > cache = cache ( Kind.SINGLE_THREAD_SCHEDULED_EXECUTOR );
		
		ScheduledExecutorService result = Executors.newSingleThreadScheduledExecutor ( );
		
		cache.add ( result );
		return result;
	}
	
	public ScheduledExecutorService getSingleThreadScheduledExecutor ( ) {
		Set < ExecutorService > cache = this.cache.get ( Kind.SINGLE_THREAD_SCHEDULED_EXECUTOR );
		
		return cache == null || cache.isEmpty ( ) ? getNewSingleThreadScheduledExecutor ( ) :
				( ScheduledExecutorService ) cache.iterator ( ).next ( );
	}
	
	public ExecutorService getNewWorkStealingPool ( ) {
		Set < ExecutorService > cache = cache ( Kind.WORK_STEALING_POOL );
		
		ExecutorService result = Executors.newWorkStealingPool ( );
		
		cache.add ( result );
		return result;
	}
	
	public ExecutorService getWorkStealingPool ( ) {
		Set < ExecutorService > cache = this.cache.get ( Kind.WORK_STEALING_POOL );
		
		return cache == null || cache.isEmpty ( )
				? getNewWorkStealingPool ( ) : cache.iterator ( ).next ( );
	}
	
	public void clear ( ) {
		cache.values ( ).forEach ( executors -> executors.forEach ( executor -> {
			try {
				executor.shutdownNow ( );
			} catch ( Exception ex ) {
				ex.printStackTrace ( );
			}
		} ) );
		cache.clear ( );
	}
	
	private Set < ExecutorService > cache ( Kind kind ) {
		return this.cache.computeIfAbsent ( kind , k -> new LinkedHashSet <> ( ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
