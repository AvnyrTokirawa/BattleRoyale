package es.outlook.adriansrj.battleroyale.arena.bombing;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldConfiguration;
import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderResize;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.math.RandomUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author AdrianSR / 14/10/2021 / 07:15 p. m.
 */
public class BombingZoneGenerator {
	
	protected final BattleRoyaleArena       arena;
	protected final List < BombingZone >    next;
	protected       BattlefieldBorderResize point;
	
	// configuration
	protected final int min;
	protected final int max;
	
	public BombingZoneGenerator ( BattleRoyaleArena arena ) {
		this.arena = arena;
		this.next  = new ArrayList <> ( );
		
		// configuration
		BattlefieldConfiguration configuration = arena.getBattlefield ( ).getConfiguration ( );
		
		this.min = configuration.getBombingZoneMin ( );
		this.max = configuration.getBombingZoneMax ( );
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	public List < BombingZone > current ( ) {
		return Collections.unmodifiableList ( next );
	}
	
	public List < BombingZone > next ( ) {
		BattlefieldBorderResize previous_point = arena.getBorder ( ).getPreviousPoint ( );
		BattlefieldBorderResize current_point  = arena.getBorder ( ).getPoint ( );
		
		// we must make sure that a new point has actually begin
		if ( current_point != null && ( next.isEmpty ( ) || !Objects.equals ( previous_point , point ) ) ) {
			point = previous_point;
			
			// recalculating
			next.clear ( );
			
			int count   = max > 0 ? Math.min ( Math.max ( min , RandomUtil.nextInt ( max + 1 ) ) , max ) : 0;
			int attempt = 0;
			
			ZoneBounds bounds = previous_point != null
					? arena.getBorder ( ).getPreviousBounds ( ) : arena.getFullBounds ( );
			
			while ( count > 0 ) {
				BombingZone next = generate ( bounds , current_point.getIdleTime ( ) );
				
				// the method generate() will probably return
				// null as it will probably not find a valid
				// place to throw bombs; so we give
				// it 3 attempts to find a valid place.
				if ( next == null && attempt < 3 ) {
					attempt++;
					continue;
				}
				
				if ( next != null ) {
					this.next.add ( next );
				}
				
				count--;
				attempt = 0;
			}
		}
		return Collections.unmodifiableList ( next );
	}
	
	public void restart ( ) {
		this.point = null;
		this.next.clear ( );
	}
	
	/**
	 * @param bounds the future bounds of the border. this value is asked to
	 *               improve performance as it would be a little expensive to
	 *               call the getFutureBounds() method every time a new {@link BombingZone}
	 *               needs to be generated.
	 * @return <b>null</b> if the generated bombing zone cannot take place.
	 */
	protected BombingZone generate ( ZoneBounds bounds , Duration border_idle_time ) {
		Location2I min   = bounds.getMinimum ( );
		int        range = bounds.getSize ( );
		
		BombingZone result = new BombingZone ( arena , bounds , border_idle_time , new Location2I (
				min.getX ( ) + RandomUtil.nextInt ( range + 1 ) ,
				min.getZ ( ) + RandomUtil.nextInt ( range + 1 ) ) );
		
		if ( result.isValidPlace ( ) ) {
			return result;
		} else {
			return null;
		}
	}
}
