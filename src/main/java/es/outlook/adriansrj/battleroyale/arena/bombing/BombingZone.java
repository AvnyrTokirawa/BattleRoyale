package es.outlook.adriansrj.battleroyale.arena.bombing;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.WorldUtil;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.math.RandomUtil;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author AdrianSR / 14/10/2021 / 07:14 p. m.
 */
public class BombingZone {
	
	/**
	 * @author AdrianSR / 14/10/2021 / 07:25 p. m.
	 */
	protected static class ThrowingBombsTask extends BukkitRunnable {
		
		protected final BombingZone zone;
		protected       long        timestamp;
		
		public ThrowingBombsTask ( BombingZone zone ) {
			this.zone      = zone;
			this.timestamp = -1;
		}
		
		@Override
		public void run ( ) {
			if ( timestamp == -1 ) {
				timestamp = System.currentTimeMillis ( );
			}
			
			if ( ( System.currentTimeMillis ( ) - timestamp ) < zone.duration.toMillis ( ) ) {
				int range = zone.bounds.getSize ( );
				int x     = zone.bounds.getMinimum ( ).getX ( ) + RandomUtil.nextInt ( range + 1 );
				int z     = zone.bounds.getMinimum ( ).getZ ( ) + RandomUtil.nextInt ( range + 1 );
				
				Block land = WorldUtil.getHighestBlockAt ( zone.arena.getWorld ( ) , x , z , block ->
						block.getType ( ).isSolid ( ) || ( !block.isLiquid ( ) && !block.isEmpty ( ) ) );
				
				if ( land != null ) {
					SchedulerUtil.runTask ( ( ) -> zone.arena.getWorld ( ).createExplosion (
							land.getLocation ( ).clone ( ).add ( 0.0D , 1.0D , 0.0D ) ,
							5.0F , false , false ) );
				}
			} else {
				zone.stop ( );
			}
		}
	}
	
	protected final BattleRoyaleArena arena;
	protected final ZoneBounds        bounds;
	protected final Duration          duration;
	protected final Location2I        center;
	
	// bombing task
	protected ThrowingBombsTask bomb_task;
	
	public BombingZone ( BattleRoyaleArena arena , ZoneBounds border_bounds ,
			Duration border_idle_time , Location2I center ) {
		this.arena  = arena;
		this.center = center;
		
		// calculating bounds.
		// the size of the bombing zone will be between
		// a 25% and 55% of the size of the bounds.
		double alpha = Math.min ( 0.25D + Math.random ( ) , 0.55D );
		int    size  = ( int ) Math.floor ( border_bounds.getSize ( ) * alpha );
		
		this.bounds = new ZoneBounds ( center , size );
		
		// calculating duration.
		// the duration of the bombing zone will be between
		// a 25% and 40% of the duration of the border idle-time.
		alpha = Math.min ( 0.30D + Math.random ( ) , 0.60D );
		
		this.duration = Duration.ofMilliseconds (
				( long ) ( border_idle_time.toMillis ( ) * alpha ) );
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	public ZoneBounds getBounds ( ) {
		return bounds;
	}
	
	public Duration getDuration ( ) {
		return duration;
	}
	
	public Location2I getCenter ( ) {
		return center;
	}
	
	public boolean isStarted ( ) {
		return bomb_task != null;
	}
	
	public boolean isActive ( ) {
		return bomb_task != null && !bomb_task.isCancelled ( );
	}
	
	public boolean isFinished ( ) {
		return !isActive ( );
	}
	
	/**
	 * Gets whether this bombing zone will throw bombs on a valid place.
	 * <br>
	 * The place is considered as <b>'valid'</b> if there is at least a <b>15%</b>
	 * of solid blocks on which bombs can land.
	 *
	 * @return whether this bombing zone will throw bombs on a valid place.
	 */
	public boolean isValidPlace ( ) {
		int size  = bounds.getSize ( ) >> 1;
		int left  = size;
		int count = 0;
		
		while ( left > 0 ) {
			int rand_x = RandomUtil.nextInt ( size );
			int rand_z = RandomUtil.nextInt ( size );
			int x      = center.getX ( ) + ( RandomUtil.nextBoolean ( ) ? rand_x : -rand_x );
			int z      = center.getZ ( ) + ( RandomUtil.nextBoolean ( ) ? rand_z : -rand_z );
			
			for ( int y = arena.getWorld ( ).getMaxHeight ( ) - 1 ; y >= 0 ; y-- ) {
				Block block = arena.getWorld ( ).getBlockAt ( x , y , z );
				
				if ( block.getType ( ).isSolid ( ) || ( !block.isLiquid ( ) && !block.isEmpty ( ) ) ) {
					count++;
					break;
				}
			}
			
			if ( ( count / ( double ) size ) >= 0.15D ) {
				return true;
			}
			
			left--;
		}
		return false;
	}
	
	public void start ( ) {
		if ( bomb_task != null ) {
			throw new IllegalStateException ( "already started" );
		} else if ( arena.getState ( ) != EnumArenaState.RUNNING ) {
			throw new IllegalStateException ( "cannot start until arena is started" );
		}
		
		if ( isValidPlace ( ) ) {
			this.bomb_task = new ThrowingBombsTask ( this );
			this.bomb_task.runTaskTimerAsynchronously ( BattleRoyale.getInstance ( ) , 5L , 5L );
		} else {
			throw new IllegalStateException ( "not valid place" );
		}
	}
	
	public void stop ( ) {
		if ( bomb_task != null ) {
			bomb_task.cancel ( );
			bomb_task = null;
		} else {
			throw new IllegalStateException ( "never started" );
		}
	}
}
