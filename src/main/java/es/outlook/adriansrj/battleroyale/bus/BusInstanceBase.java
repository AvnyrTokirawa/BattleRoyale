package es.outlook.adriansrj.battleroyale.bus;

import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Base battle royale bus implementation.
 *
 * @author AdrianSR / 08/09/2021 / 01:50 p. m.
 */
public abstract class BusInstanceBase < C extends Bus > extends BusInstance < C > {
	
	/**
	 * @author AdrianSR / 08/09/2021 / 02:05 p. m.
	 */
	protected static class BusDisplacementTask implements Runnable {
		
		protected final BusInstanceBase < ? >    bus;
		protected       ScheduledExecutorService executor;
		
		protected final Vector     direction;
		protected final ZoneBounds bounds;
		protected       boolean    entered_bounds;
		
		public BusDisplacementTask ( BusInstanceBase < ? > bus , ScheduledExecutorService executor ) {
			this.bus       = bus;
			this.executor  = executor;
			this.direction = DirectionUtil.getDirection ( bus.getSpawn ( ).getYaw ( ) , 0.0F );
			this.bounds    = bus.getArena ( ).getFullBounds ( );
			
			// start location
			Vector start_location = bounds.project ( bus.getSpawn ( ).getStartLocation ( ) );
			
			this.bus.setLocation ( start_location );
			this.entered_bounds = bounds.contains ( start_location );
		}
		
		@Override
		public void run ( ) {
			double  x_to       = bus.location.getX ( ) + ( direction.getX ( ) * bus.spawn.getSpeed ( ) );
			double  z_to       = bus.location.getZ ( ) + ( direction.getZ ( ) * bus.spawn.getSpeed ( ) );
			boolean out_bounds = !bounds.contains ( x_to , z_to );
			
			// route finished
			if ( out_bounds && entered_bounds ) {
				if ( !this.bus.isFinished ( ) ) {
					this.bus.finish ( );
				}
				
				this.dispose ( );
				return;
			}
			
			if ( !entered_bounds && !out_bounds ) {
				entered_bounds = true;
			}
			
			// displacing
			bus.location.setX ( x_to );
			bus.location.setZ ( z_to );
			
			bus.setLocation ( bus.location );
			bus.displace ( bus.location );
		}
		
		protected void dispose ( ) {
			// shutting down executor
			executor.shutdown ( );
			executor = null;
		}
	}
	
	protected final C                        configuration;
	protected       boolean                  started;
	protected       ScheduledExecutorService executor;
	protected       Vector                   location;
	protected       boolean                  finished;
	
	protected BusInstanceBase ( C configuration ) {
		this.configuration = configuration;
	}
	
	@Override
	public C getConfiguration ( ) {
		return configuration;
	}
	
	@Override
	public Vector getCurrentLocation ( ) {
		return getLocation ( ).clone ( );
	}
	
	@Override
	public boolean isStarted ( ) {
		return started;
	}
	
	@Override
	public boolean isFinished ( ) {
		return finished;
	}
	
	protected Vector getLocation ( ) {
		if ( isStarted ( ) ) {
			if ( location == null ) {
				// starting point location
				location = getArena ( ).getFullBounds ( )
						.project ( getSpawn ( ).getStartLocation ( ) );
			}
			
			return location;
		} else {
			throw new IllegalStateException ( "bus must be started to get its current location" );
		}
	}
	
	protected void setLocation ( Vector location ) {
		if ( isStarted ( ) ) {
			this.location = Objects.requireNonNull ( location , "location cannot be null" );
		} else {
			throw new IllegalStateException ( "bus must be started to set its current location" );
		}
	}
	
	protected abstract void displace ( Vector location );
	
	@Override
	protected void start ( ) {
		if ( started ) {
			throw new IllegalStateException ( "bus already started" );
		} else if ( getArena ( ).getState ( ) != EnumArenaState.RUNNING ) {
			throw new IllegalStateException ( "bus cannot start until arena is started" );
		}
		
		this.started = true;
		
		// starting point location
		this.location = getArena ( ).getFullBounds ( ).project ( getSpawn ( ).getStartLocation ( ) );
		
		// scheduling displacement task
		executor = Executors.newSingleThreadScheduledExecutor ( );
		executor.scheduleAtFixedRate ( new BusDisplacementTask ( this , executor ) ,
									   Constants.BUS_DISPLACEMENT_EXECUTOR_PERIOD ,
									   Constants.BUS_DISPLACEMENT_EXECUTOR_PERIOD , TimeUnit.MILLISECONDS );
	}
	
	@Override
	public void finish ( ) {
		if ( finished ) {
			throw new IllegalStateException ( "bus already finished" );
		} else if ( !started ) {
			throw new IllegalStateException ( "bus not started" );
		}
		
		this.finished = true;
		
		// disposing executor
		if ( executor != null ) {
			executor.shutdownNow ( );
			executor = null;
		}
	}
	
	@Override
	public void restart ( ) {
		this.started  = false;
		this.finished = false;
		
		if ( executor != null ) {
			executor.shutdownNow ( );
			executor = null;
		}
	}
}