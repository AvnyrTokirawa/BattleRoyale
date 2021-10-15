package es.outlook.adriansrj.battleroyale.battlefield.border;

import es.outlook.adriansrj.battleroyale.util.math.Location2D;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.loadable.LoadableEntry;
import es.outlook.adriansrj.core.util.math.Vector2D;
import es.outlook.adriansrj.core.util.saveable.SavableEntry;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Border random shrinking succession.
 * <br>
 * <b>Note that this class will only generate shrinking successions.</b>
 * This means that it will not generate points that will make the border
 * grow, but instead, will generate points that will make the border shrink.
 *
 * @author AdrianSR / 06/09/2021 / 11:09 a. m.
 */
public class BattlefieldBorderSuccessionRandom extends BattlefieldBorderSuccession {
	
	public static final int MINIMUM_DIVISIONS = 2;
	
	protected static final String MINIMUM_RADIUS_KEY = "minimum-radius";
	protected static final String FULL_TIME_KEY      = "full-time";
	protected static final String DIVISIONS_KEY      = "divisions";
	protected static final String MINIMUM_DAMAGE_KEY = "minimum-damage";
	protected static final String MAXIMUM_DAMAGE_KEY = "maximum-damage";
	
	protected double maximum_radius;
	
	@SavableEntry ( key = MINIMUM_RADIUS_KEY )
	@LoadableEntry ( key = MINIMUM_RADIUS_KEY )
	protected double minimum_radius;
	
	@SavableEntry ( key = FULL_TIME_KEY )
	@LoadableEntry ( key = FULL_TIME_KEY )
	protected long full_time;
	
	@SavableEntry ( key = DIVISIONS_KEY )
	@LoadableEntry ( key = DIVISIONS_KEY )
	protected int divisions;
	
	@SavableEntry ( key = MINIMUM_DAMAGE_KEY )
	@LoadableEntry ( key = MINIMUM_DAMAGE_KEY )
	protected double minimum_damage;
	
	@SavableEntry ( key = MAXIMUM_DAMAGE_KEY )
	@LoadableEntry ( key = MAXIMUM_DAMAGE_KEY )
	protected double maximum_damage;
	
	/* calculation data */
	// stores how much each division will shrink
	protected double[]     shrink_factors;
	// stores the idle time of each division (in milliseconds)
	protected long[]       idle_times;
	// stores the time of each division (in milliseconds)
	protected long[]       times;
	// stores the damage each division does (per second)
	protected double[]     damages;
	// stores the eye-location of each division
	protected Location2D[] eye_locations;
	
	/**
	 *
	 * @param minimum_radius
	 * @param full_time full time in milliseconds
	 * @param divisions
	 * @param minimum_damage
	 * @param maximum_damage
	 */
	public BattlefieldBorderSuccessionRandom ( double minimum_radius ,
			long full_time , int divisions , double minimum_damage , double maximum_damage ) {
		Validate.isTrue ( divisions >= MINIMUM_DIVISIONS , "at least "
				+ MINIMUM_DIVISIONS + " divisions are required!" );
		
		this.minimum_radius = minimum_radius;
		this.full_time      = full_time;
		this.divisions      = divisions;
		this.minimum_damage = minimum_damage;
		this.maximum_damage = maximum_damage;
	}
	
	public BattlefieldBorderSuccessionRandom ( ConfigurationSection section ) {
		this.load ( section );
	}
	
	public BattlefieldBorderSuccessionRandom ( ) {
		// to be loaded
	}
	
	/**
	 * Randomly recalculates this succession based on the data.
	 * <p>
	 * @param maximum_radius the maximum radius of the border. usually the radius of the battlefild.
	 */
	public void recalculate ( double maximum_radius ) {
		this.maximum_radius = maximum_radius;
		
		this.points.clear ( );
		
		this.calculateRadius ( );
		this.calculateTimes ( );
		this.calculateDamages ( );
		this.calculateEyeDisplacements ( );
		
		double current_radius = maximum_radius;
		
		for ( int i = 0 ; i < divisions ; i++ ) {
			double radius    = current_radius -= shrink_factors[ i ];
			double damage    = damages[ i ];
			long   time      = times[ i ];
			long   idle_time = idle_times[ i ];
			
			this.points.add ( new BattlefieldBorderResize (
					eye_locations[ i ] , radius , damage ,
					Duration.ofMilliseconds ( time ) ,
					Duration.ofMilliseconds ( idle_time ) ) );
		}
	}
	
	/**
	 * Randomly recalculates this succession based on the data.
	 * <p>
	 * @param bounds the bounds that will provide the maximum radius.
	 */
	public void recalculate ( ZoneBounds bounds ) {
		recalculate ( bounds.getSize ( ) );
	}
	
	/**
	 * Calculates the radius for each division.
	 */
	protected void calculateRadius ( ) {
		this.shrink_factors = new double[ divisions ];
		
		double radius_left = maximum_radius;
		
		for ( int i = 0 ; i < shrink_factors.length ; i++ ) {
			int    divisions_left = divisions - i;
			double base_radius    = radius_left / divisions_left;
			double add_sub = ( base_radius / Math.max (
					Math.round ( ( divisions_left * Math.random ( ) ) ) , 1L ) );
			
			//-------------- DIFFERENT RANDOMIZERS --------------
			double value = base_radius + ( add_sub * ( Math.random ( ) + 0.5D ) );
			//			double       value = base_radius + ( add_sub * ( Math.random ( ) ) );
			//			double       value = base_radius * ( Math.random ( ) + 0.7 );
			//			double       value = base_radius + add_sub;
			//			double       value = base_radius;
			//---------------------------------------------------
			
			if ( value > radius_left ) {
				if ( i == shrink_factors.length - 1 ) {
					value = radius_left;
					
					double previous_value = shrink_factors[ i - 1 ];
					
					if ( previous_value > value ) {
						double dff = previous_value - value;
						double dft = 1.0D - ( dff / shrink_factors[ i - 1 ] );
						
						if ( dft < 0.23D ) {
							double new_pv = previous_value - ( ( previous_value / 2 ) - value );
							double diff   = previous_value - new_pv;
							
							shrink_factors[ i - 1 ] = new_pv;
							
							value += diff;
						}
					}
				} else {
					double dd = value - radius_left;
					double hf = radius_left / 2;
					
					value = hf - dd;
				}
			}
			
			radius_left -= value;
			
			this.shrink_factors[ i ] = value;
		}
	}
	
	/**
	 * Calculates the time of each division.
	 */
	protected void calculateTimes ( ) {
		this.idle_times = new long[ divisions ];
		this.times      = new long[ divisions ];
		
		long   time_left   = full_time;
		double radius_left = maximum_radius;
		
		for ( int i = 0 ; i < divisions ; i++ ) {
			int    divisions_left = divisions - i;
			long   base_time      = time_left / divisions_left;
			double radius         = radius_left -= shrink_factors[ i ];
			double factor         = radius / maximum_radius;
			
			long value          = base_time + Math.round ( base_time * ( factor ) );
			long time_in_shrink = Math.round ( value - ( value / 2.5D ) ); // time (lower)
			long time_to_start  = value - time_in_shrink;                  // idle time (higher)
			
			if ( value > time_left ) {
				value = time_left;
			}
			
			time_left -= value;
			
			this.idle_times[ i ] = time_to_start;
			this.times[ i ]      = time_in_shrink;
		}
	}
	
	/**
	 * Calculates the damage each division does.
	 */
	protected void calculateDamages ( ) {
		this.damages = new double[ divisions ];
		
		double radius_left = maximum_radius;
		
		for ( int i = 0 ; i < damages.length ; i++ ) {
			double radius = radius_left -= shrink_factors[ i ];
			double value = Math.min ( Math.max (
					maximum_damage * ( 1.0D - ( radius / ( maximum_radius - minimum_radius ) ) ) ,
					minimum_damage ) , maximum_damage );
			
			this.damages[ i ] = value;
		}
	}
	
	/**
	 * Calculates the displacement of the eye of each division.
	 */
	protected void calculateEyeDisplacements ( ) {
		this.eye_locations = new Location2D[ divisions ];
		
		for ( int i = 0 ; i < eye_locations.length ; i++ ) {
			// here we're generating a random direction
			Vector2D direction = new Vector2D (
					Math.random ( ) < 0.5D ? Math.random ( ) : -Math.random ( ) ,
					Math.random ( ) < 0.5D ? Math.random ( ) : -Math.random ( ) );
			
			if ( direction.getX ( ) == 0.0D && direction.getY ( ) == 0.0D ) {
				// as the direction is zero, the eye will not move anywhere.
				if ( i > 0 ) {
					// the center of the previous division.
					this.eye_locations[ i ] = eye_locations[ i - 1 ];
				} else {
					// the center of the actual zone.
					this.eye_locations[ i ] = new Location2D ( maximum_radius / 2 , maximum_radius / 2 );
				}
			} else {
				double max_movement = shrink_factors[ i ] / 2;
				double movement     = max_movement * Math.min ( 1.0D , 0.3D + Math.random ( ) );
				double previous_x   = 0.0D;
				double previous_y   = 0.0D;
				
				if ( i > 0 ) {
					Location2D previous_center = eye_locations[ i - 1 ];
					
					previous_x = previous_center.getX ( );
					previous_y = previous_center.getZ ( );
				} else {
					// center of the arena
					previous_x = maximum_radius / 2;
					previous_y = maximum_radius / 2;
					//					previous_x = bounds.getCenter ( ).getX ( );
					//					previous_y = bounds.getCenter ( ).getZ ( );
				}
				
				this.eye_locations[ i ] = new Location2D (
						direction.multiply ( movement ).add ( previous_x , previous_y ) );
			}
		}
	}
	
	@Override
	public BattlefieldBorderSuccessionRandom load ( ConfigurationSection section ) {
		this.loadEntries ( section , false );
		
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return saveEntries ( section , false );
	}
	
	@Override
	public boolean isValid ( ) {
		return full_time > 0 && divisions >= MINIMUM_DIVISIONS;
	}
}
