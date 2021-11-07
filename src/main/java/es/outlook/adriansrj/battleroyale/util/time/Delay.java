package es.outlook.adriansrj.battleroyale.util.time;

import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.core.util.Duration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author AdrianSR / 01/11/2021 / 05:20 p. m.
 */
public class Delay {
	
	protected final UUID id;
	protected       long end_millis;
	
	protected Delay ( UUID id ) {
		this.id         = id;
		this.end_millis = -1;
	}
	
	public UUID getID ( ) {
		return id;
	}
	
	public Duration getTimeLeft ( ) {
		if ( isActive ( ) ) {
			return Duration.ofMilliseconds ( end_millis - System.currentTimeMillis ( ) );
		} else {
			return Duration.ZERO;
		}
	}
	
	public void activate ( Duration duration ) {
		Validate.isTrue ( !isActive ( ) , "this delay is already active" );
		
		activate ( duration.getDuration ( ) , duration.getUnit ( ) );
	}
	
	public void activate ( long time , TimeUnit unit ) {
		Validate.isTrue ( !isActive ( ) , "this delay is already active" );
		
		activate0 ( time , unit );
	}
	
	public void reset ( Duration duration ) {
		Validate.isTrue ( !duration.isZero ( ) , "duration cannot be zero" );
		
		reset ( duration.getDuration ( ) , duration.getUnit ( ) );
	}
	
	public void reset ( long time , TimeUnit unit ) {
		Validate.isTrue ( time > 0 , "time must be > 0" );
		Validate.notNull ( unit , "unit cannot be null" );
		Validate.isTrue ( isActive ( ) , "must be active to be reset" );
		
		activate0 ( time , unit );
	}
	
	private void activate0 ( long time , TimeUnit unit ) {
		Validate.isTrue ( time > 0 , "time must be > 0" );
		Validate.notNull ( unit , "unit cannot be null" );
		
		this.end_millis = System.currentTimeMillis ( ) + unit.toMillis ( time );
	}
	
	public void expire ( ) {
		this.end_millis = -1;
	}
	
	public boolean isActive ( ) {
		return System.currentTimeMillis ( ) < end_millis;
	}
	
	public boolean isExpired ( ) {
		return !isActive ( );
	}
}
