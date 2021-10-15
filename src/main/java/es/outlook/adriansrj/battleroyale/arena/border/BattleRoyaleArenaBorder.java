package es.outlook.adriansrj.battleroyale.arena.border;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderResize;
import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderSuccession;
import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderSuccessionRandom;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaBorderState;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.event.border.BorderResizeChangeEvent;
import es.outlook.adriansrj.battleroyale.event.border.BorderStateChangeEvent;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.math.Location2D;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.battleroyale.world.border.WorldBorder;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Battle royale arena border.
 *
 * @author AdrianSR / 06/09/2021 / 01:41 a. m.
 */
public class BattleRoyaleArenaBorder {
	
	/**
	 * @author AdrianSR / 06/09/2021 / 01:52 p. m.
	 */
	protected static class BorderResizeTask implements Runnable {
		
		protected final BattleRoyaleArenaBorder           border;
		protected       ScheduledExecutorService          executor;
		protected       Stack < BattlefieldBorderResize > points;
		
		protected long   idle_time;
		protected long   time;
		protected long   end_time;
		protected long   idle_last;
		protected double start_size;
		protected double start_x;
		protected double start_z;
		
		public BorderResizeTask ( BattleRoyaleArenaBorder border , ScheduledExecutorService executor ) {
			this.border   = border;
			this.executor = executor;
			
			this.points = new Stack <> ( );
			this.points.addAll ( border.points );
		}
		
		@Override
		public void run ( ) {
			try {
				// as we're using a custom scheduler this task will
				// continue even when the plugin is disabled, so we have
				// to stop it once the plugin is disabled.
				if ( !BattleRoyale.getInstance ( ).isEnabled ( ) ) {
					this.dispose ( );
					return;
				}
				
				if ( border.point == null ) {
					if ( points.size ( ) > 0 ) {
						this.border.point = points.remove ( 0 );
						this.setState ( EnumArenaBorderState.IDLE );
						
						this.idle_time = System.currentTimeMillis ( );
						
						// firing change event
						new BorderResizeChangeEvent (
								border , border.previous_point , border.point ).callSafe ( );
					} else {
						this.setState ( EnumArenaBorderState.STOPPED );
						this.dispose ( );
						
						return;
					}
				}
				
				long idle_time = System.currentTimeMillis ( ) - this.idle_time;
				
				if ( idle_time > border.point.getIdleTime ( ).toMillis ( ) ) {
					if ( border.state != EnumArenaBorderState.RESIZING ) {
						this.setState ( EnumArenaBorderState.RESIZING );
						
						this.time       = System.currentTimeMillis ( );
						this.end_time   = time + border.point.getTime ( ).toMillis ( );
						this.start_size = border.handle.getSize ( );
						this.start_x    = border.handle.getCenterX ( );
						this.start_z    = border.handle.getCenterZ ( );
					}
					
					long time = System.currentTimeMillis ( ) - this.time;
					
					if ( time <= border.point.getTime ( ).toMillis ( ) ) {
						double factor = ( float ) time / ( float ) ( this.end_time - this.time );
						
						// resizing
						double size         = border.point.getRadius ( );
						double current_size = border.handle.getSize ( );
						
						if ( Double.compare ( size , current_size ) != 0 ) {
							border.handle.setSize ( lerp ( start_size , size , factor ) );
						}
						
						// displacing
						Vector point_location = getPointLocation ( border.point );
						double x              = point_location.getX ( );
						double z              = point_location.getZ ( );
						
						if ( Double.compare ( x , border.handle.getCenterX ( ) ) != 0
								|| Double.compare ( z , border.handle.getCenterZ ( ) ) != 0 ) {
							border.handle.setCenter ( lerp ( start_x , x , factor ) ,
													  lerp ( start_z , z , factor ) );
						}
					} else {
						border.previous_point = border.point;
						border.point          = null;
						
						setState ( EnumArenaBorderState.IDLE );
					}
				} else {
					// the border is idle, but we need
					// to regularly notify players about it.
					if ( idle_last == 0L || System.currentTimeMillis ( ) - idle_last >= 1000L ) {
						idle_last = System.currentTimeMillis ( );
						
						// notifying each 1000 milliseconds
						border.handle.refresh ( );
					}
				}
			} catch ( Exception ex ) {
				ex.printStackTrace ( );
			}
		}
		
		protected void dispose ( ) {
			points.clear ( );
			points = null;
			
			// shutting down executor
			executor.shutdown ( );
			executor = null;
		}
		
		protected double lerp ( double minimum , double maximum , double normal ) {
			return ( 1.0D - normal ) * minimum + normal * maximum;
		}
		
		protected Vector getPointLocation ( BattlefieldBorderResize point ) {
			Location2D relocatable = point.getLocation ( );
			
			return border.arena.getFullBounds ( ).project ( new Vector (
					relocatable.getX ( ) , 0 , relocatable.getZ ( ) ) );
		}
		
		protected void setState ( EnumArenaBorderState state ) {
			if ( border.state != state ) {
				EnumArenaBorderState previous_state = border.state;
				border.state      = state;
				border.state_time = System.currentTimeMillis ( );
				
				if ( previous_state != null ) {
					new BorderStateChangeEvent ( border , previous_state , border.state ).callSafe ( );
				}
			}
		}
	}
	
	protected final WorldBorder       handle;
	protected final BattleRoyaleArena arena;
	
	// state
	protected ScheduledExecutorService          executor;
	protected Stack < BattlefieldBorderResize > points;
	protected BattlefieldBorderResize           previous_point;
	protected BattlefieldBorderResize           point;
	protected EnumArenaBorderState              state;
	protected long                              state_time;
	protected boolean                           started;
	
	public BattleRoyaleArenaBorder ( BattleRoyaleArena arena ) {
		this.handle     = new WorldBorder ( arena.getWorld ( ) );
		this.arena      = arena;
		this.state      = EnumArenaBorderState.STOPPED;
		this.state_time = System.currentTimeMillis ( );
		
		this.recalculatePoints ( );
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	public Set < Player > getPlayers ( ) {
		return handle.getPlayers ( );
	}
	
	public EnumArenaBorderState getState ( ) {
		return state;
	}
	
	/**
	 * Gets how long the border has been in the state returned by {@link #getState()}.
	 *
	 * @return state time in milliseconds.
	 */
	public long getStateTime ( ) {
		return state_time;
	}
	
	public boolean isStarted ( ) {
		return started;
	}
	
	public BattlefieldBorderResize getPreviousPoint ( ) {
		return previous_point;
	}
	
	public BattlefieldBorderResize getPoint ( ) {
		return point;
	}
	
	public List < BattlefieldBorderResize > getPoints ( ) {
		return Collections.unmodifiableList ( points );
	}
	
	public double getCurrentSize ( ) {
		return handle.getSize ( );
	}
	
	public double getFutureSize ( ) {
		return point != null ? point.getRadius ( ) : handle.getSize ( );
	}
	
	public ZoneBounds getCurrentBounds ( ) {
		return new ZoneBounds ( new Location2I ( ( int ) handle.getCenterX ( ) , ( int ) handle.getCenterZ ( ) ) ,
								( int ) Math.round ( handle.getSize ( ) ) );
	}
	
	public ZoneBounds getFutureBounds ( ) {
		return point != null ? getBounds ( point ) : null;
	}
	
	public ZoneBounds getPreviousBounds ( ) {
		return previous_point != null ? getBounds ( previous_point ) : null;
	}
	
	public void start ( ) {
		if ( started ) {
			throw new IllegalStateException ( "border already started" );
		} else if ( arena.getState ( ) != EnumArenaState.RUNNING ) {
			throw new IllegalStateException ( "border cannot start until arena is started" );
		}
		
		this.started = true;
		
		// initial bounds
		ZoneBounds bounds        = arena.getFullBounds ( );
		Location2I bounds_center = bounds.getCenter ( );
		
		handle.setCenter ( bounds_center.getX ( ) , bounds_center.getZ ( ) );
		handle.setSize ( bounds.getSize ( ) );
		
		// schedule resizing
		executor = Executors.newSingleThreadScheduledExecutor ( );
		
		if ( points != null && points.size ( ) > 0 ) {
			executor.scheduleAtFixedRate ( new BorderResizeTask ( this , executor ) ,
										   70L , 70L , TimeUnit.MILLISECONDS );
		} else {
			// resize succession invalid or not set. we need
			// to regularly notify players about world border,
			// so lets schedule a task for that.
			executor.scheduleAtFixedRate ( handle :: refresh , 1L , 1L , TimeUnit.SECONDS );
		}
	}
	
	public void restart ( ) {
		this.started = false;
		
		// disposing points
		this.previous_point = null;
		this.point          = null;
		
		// shutting down executor
		if ( executor != null ) {
			executor.shutdownNow ( );
			executor = null;
		}
		
		handle.getPlayers ( ).clear ( );
		
		// recalculating succession
		recalculatePoints ( );
	}
	
	public void refresh ( ) {
		handle.refresh ( );
	}
	
	protected void recalculatePoints ( ) {
		// here we're creating a copy of the resize succession, as it could probably
		// be a BattlefieldBorderSuccessionRandom, and the method recalculate() might
		// be called, resulting in a completely different succession.
		BattlefieldBorderSuccession resize_succession = arena.getBattlefield ( )
				.getConfiguration ( ).getBorderResizeSuccession ( );
		
		if ( resize_succession != null ) {
			this.points = new Stack <> ( );
			
			// it's a random succession, so let's recalculate it.
			if ( resize_succession instanceof BattlefieldBorderSuccessionRandom ) {
				( ( BattlefieldBorderSuccessionRandom ) resize_succession )
						.recalculate ( arena.getFullBounds ( ) );
			}
			
			resize_succession.getPoints ( ).stream ( )
					.filter ( BattlefieldBorderResize :: isValid ).forEach ( this.points :: add );
		}
	}
	
	protected ZoneBounds getBounds ( BattlefieldBorderResize point ) {
		Location2D relocatable = point.getLocation ( );
		Vector location = arena.getFullBounds ( ).project ( new Vector (
				relocatable.getX ( ) , 0 , relocatable.getZ ( ) ) );
		
		return new ZoneBounds ( new Location2I ( ( int ) location.getX ( ) , ( int ) location.getZ ( ) ) ,
								( int ) Math.round ( point.getRadius ( ) ) );
	}
}