package es.outlook.adriansrj.battleroyale.arena.airsupply;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldConfiguration;
import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderResize;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.util.math.RandomUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author AdrianSR / 13/10/2021 / 09:30 a. m.
 */
public class AirSupplyGenerator {
	
	protected final BattleRoyaleArena       arena;
	protected final List < AirSupply >      next;
	protected       BattlefieldBorderResize point;
	
	// configuration
	protected final int min;
	protected final int max;
	protected final int count;
	
	public AirSupplyGenerator ( BattleRoyaleArena arena ) {
		this.arena = arena;
		this.next  = new ArrayList <> ( );
		
		// configuration
		BattlefieldConfiguration configuration = arena.getBattlefield ( ).getConfiguration ( );
		
		this.min   = configuration.getAirSupplyMin ( );
		this.max   = configuration.getAirSupplyMax ( );
		this.count = max > 0 ? Math.max ( min , RandomUtil.nextInt ( max ) ) : 0;
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	public List < AirSupply > current ( ) {
		return Collections.unmodifiableList ( next );
	}
	
	public List < AirSupply > next ( ) {
		BattlefieldBorderResize border_point = arena.getBorder ( ).getPoint ( );
		
		if ( ( next.isEmpty ( ) || !Objects.equals ( border_point , point ) ) && border_point != null ) {
			point = border_point;
			
			// recalculating
			next.clear ( );
			
			// count calculated based on the size of the point
			double     alpha   = point.getRadius ( ) / arena.getBattlefield ( ).getSizeExact ( );
			int        count   = ( int ) Math.floor ( this.count * alpha );
			int        attempt = 0;
			ZoneBounds bounds  = arena.getBorder ( ).getFutureBounds ( );
			
			while ( count > 0 ) {
				AirSupply next = generate ( bounds );
				
				// the method generate() will probably return
				// null as it will probably not find a solid
				// ground to spawn the air supply; so we give
				// it 32 attempts to find a solid ground.
				if ( next == null && attempt < 32 ) {
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
	 *
	 * @param bounds the future bounds of the border. this value is asked to
	 *               improve performance as it would be a little expensive to
	 *               call the getFutureBounds() method every time a new {@link AirSupply}
	 *               needs to be generated.
	 * @return <b>null</b> if the generated air supply will not land on a valid place.
	 */
	protected AirSupply generate ( ZoneBounds bounds ) {
		Location2I min   = bounds.getMinimum ( );
		int        range = bounds.getSize ( );
		
		AirSupply result = new AirSupply ( arena , new Location2I (
				min.getX ( ) + RandomUtil.nextInt ( range + 1 ) ,
				min.getZ ( ) + RandomUtil.nextInt ( range + 1 ) ) );
		
		if ( result.isValidPlace ( ) ) {
			return result;
		} else {
			return null;
		}
	}
}