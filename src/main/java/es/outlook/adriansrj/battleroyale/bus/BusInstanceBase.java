package es.outlook.adriansrj.battleroyale.bus;

import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.battleroyale.util.task.BukkitRunnableWrapper;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import org.bukkit.util.Vector;

/**
 * Base battle royale bus implementation.
 *
 * @author AdrianSR / 08/09/2021 / 01:50 p. m.
 */
public abstract class BusInstanceBase < C extends Bus > extends BusInstance < C > {
	//
	//	/**
	//	 * @author AdrianSR / 08/09/2021 / 02:05 p. m.
	//	 */
	//	protected static class BusDisplacementTask extends BukkitRunnableWrapper {
	//
	//		protected final BusInstanceBase < ? > bus;
	//		protected final Vector                direction;
	//		protected final ZoneBounds            bounds;
	//		protected       boolean               entered_bounds;
	//		protected       long                  jump_tutorial;
	//
	//		public BusDisplacementTask ( BusInstanceBase < ? > bus ) {
	//			this.bus       = bus;
	//			this.direction = DirectionUtil.getDirection ( bus.getSpawn ( ).getYaw ( ) , 0.0F );
	//			this.bounds    = bus.getArena ( ).getFullBounds ( );
	//
	//			// start location
	//			Vector start_location = bounds.project ( bus.getSpawn ( ).getStartLocation ( ) );
	//
	//			this.bus.setLocation ( start_location );
	//			this.entered_bounds = bounds.contains ( start_location );
	//		}
	//
	//		@Override
	//		public void run ( ) {
	//			double  x_to       = bus.location.getX ( ) + ( direction.getX ( ) * bus.spawn.getSpeed ( ) );
	//			double  z_to       = bus.location.getZ ( ) + ( direction.getZ ( ) * bus.spawn.getSpeed ( ) );
	//			boolean out_bounds = !bounds.contains ( x_to , z_to );
	//
	//			// route finished
	//			if ( out_bounds && entered_bounds ) {
	//				if ( !bus.isFinished ( ) ) {
	//					bus.finish ( );
	//				}
	//
	//				this.dispose ( );
	//				return;
	//			}
	//
	//			if ( !entered_bounds && !out_bounds ) {
	//				entered_bounds = true;
	//			}
	//
	//			// displacing
	//			bus.location.setX ( x_to );
	//			bus.location.setZ ( z_to );
	//
	//			bus.setLocation ( bus.location );
	//			bus.displace ( bus.location );
	//
	//			// jump tutorial
	//			if ( bus.isDoorOpen ( ) && ( jump_tutorial == 0L
	//					|| System.currentTimeMillis ( ) - jump_tutorial > 1000L ) ) {
	//				jump_tutorial = System.currentTimeMillis ( );
	//				bus.jumpTutorial ( );
	//			}
	//		}
	//
	//		protected void dispose ( ) {
	//			// shutting down
	//			cancel ( );
	//		}
	//	}
	
	/**
	 * @author AdrianSR / 29/11/2021 / 12:27 p. m.
	 */
	protected static class BusLifeLoopTask extends BukkitRunnableWrapper {
		
		protected final BusInstanceBase < ? > bus;
		
		public BusLifeLoopTask ( BusInstanceBase < ? > bus ) {
			this.bus = bus;
		}
		
		@Override
		public void run ( ) {
			bus.lifeLoop ( );
		}
	}
	
	protected final C               configuration;
	protected       boolean         started;
	//	protected       BusDisplacementTask displacement_task;
	protected       BusLifeLoopTask life_loop_task;
	protected       Vector          location;
	protected       Vector          direction;
	protected       ZoneBounds      bounds;
	protected       boolean         entered_bounds;
	protected       boolean         finished;
	protected       long            jump_tutorial;
	
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
	
	//	protected void setLocation ( Vector location ) {
	//		if ( isStarted ( ) ) {
	//			this.location = Objects.requireNonNull ( location , "location cannot be null" );
	//		} else {
	//			throw new IllegalStateException ( "bus must be started to set its current location" );
	//		}
	//	}
	
	protected void lifeLoop ( ) {
		boolean out_bounds = !bounds.contains ( location.getX ( ) , location.getZ ( ) );
		
		// route finished
		if ( out_bounds && entered_bounds ) {
			if ( !isFinished ( ) ) {
				finish ( );
			}
			return;
		}
		
		if ( !entered_bounds && !out_bounds ) {
			entered_bounds = true;
		}
		
		// jump tutorial
		if ( isDoorOpen ( ) && ( jump_tutorial == 0L
				|| System.currentTimeMillis ( ) - jump_tutorial > 1000L ) ) {
			jump_tutorial = System.currentTimeMillis ( );
			jumpTutorial ( );
		}
	}
	
	/**
	 * Sends the jump tutorial (titles, messages, whatever)
	 * to the passengers once the door is open.
	 */
	protected void jumpTutorial ( ) {
		// nothing by default
	}
	
	@Override
	protected void start ( ) {
		if ( started ) {
			throw new IllegalStateException ( "bus already started" );
		} else if ( getArena ( ).getState ( ) != EnumArenaState.RUNNING ) {
			throw new IllegalStateException ( "bus cannot start until arena is started" );
		}
		
		// initializing values
		this.bounds         = getArena ( ).getFullBounds ( );
		this.location       = bounds.project ( getSpawn ( ).getStartLocation ( ) );
		this.direction      = DirectionUtil.getDirection ( getSpawn ( ).getYaw ( ) , 0.0F );
		this.entered_bounds = bounds.contains ( location );
		
		// then marking as started
		this.started  = true;
		this.finished = false;
		
		// scheduling life loop task
		this.life_loop_task = new BusLifeLoopTask ( this );
		this.life_loop_task.runTaskTimer ( BattleRoyale.getInstance ( ) , 0L , 0L );
		// scheduling displacement task
		//		this.displacement_task = new BusDisplacementTask ( this );
		//		this.displacement_task.runTaskTimer ( BattleRoyale.getInstance ( ) , 0L , 0L );
	}
	
	@Override
	public void finish ( ) {
		if ( finished ) {
			throw new IllegalStateException ( "bus already finished" );
		} else if ( !started ) {
			throw new IllegalStateException ( "bus not started" );
		}
		
		this.finished = true;
		
		// shutting down life loop task
		if ( life_loop_task != null ) {
			life_loop_task.cancel ( );
			life_loop_task = null;
		}
		
		//		// shutting down displacement task
		//		if ( displacement_task != null ) {
		//			displacement_task.cancel ( );
		//			displacement_task = null;
		//		}
	}
	
	@Override
	public void restart ( ) {
		this.started  = false;
		this.finished = false;
		
		if ( life_loop_task != null ) {
			life_loop_task.cancel ( );
			life_loop_task = null;
		}
		
		//		if ( displacement_task != null ) {
		//			displacement_task.cancel ( );
		//			displacement_task = null;
		//		}
	}
}