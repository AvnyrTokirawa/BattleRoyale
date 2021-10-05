package es.outlook.adriansrj.battleroyale.battlefield.border;

import es.outlook.adriansrj.battleroyale.util.math.Location2D;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.configurable.duration.ConfigurableDuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.Objects;

/**
 * Border shrinking point.
 *
 * @author AdrianSR / 06/09/2021 / 11:03 a. m.
 */
public class BattlefieldBorderShrink implements Configurable {
	
	public static final double MIN_BORDERS_RADIUS = 0D;
	
	@ConfigurableEntry ( subsection = "location" )
	protected Location2D           location;
	@ConfigurableEntry ( key = "radius" )
	protected double               radius;
	@ConfigurableEntry ( key = "damage" )
	protected double               damage;
	@ConfigurableEntry ( subsection = "time" )
	protected ConfigurableDuration time;
	@ConfigurableEntry ( subsection = "idle-time" )
	protected ConfigurableDuration idle_time;
	
	public BattlefieldBorderShrink ( Location2D location , double radius , double damage ,
			Duration time , Duration idle_time ) {
		this.location  = location;
		this.radius    = Math.max ( radius , 0.0D );
		this.damage    = Math.max ( damage , 0.0D );
		this.time      = new ConfigurableDuration ( time );
		this.idle_time = new ConfigurableDuration ( idle_time );
	}
	
	public BattlefieldBorderShrink ( Vector location , double radius , double damage ,
			Duration time , Duration idle_time ) {
		this ( new Location2D ( location.getX ( ) , location.getZ ( ) ) , radius , damage , time , idle_time );
	}
	
	public BattlefieldBorderShrink ( ) {
		// to be loaded
	}
	
	public Location2D getLocation ( ) {
		return location;
	}
	
	public double getRadius ( ) {
		return radius;
	}
	
	public double getDamage ( ) {
		return damage;
	}
	
	public ConfigurableDuration getTime ( ) {
		return time;
	}
	
	public ConfigurableDuration getIdleTime ( ) {
		return idle_time;
	}
	
	@Override
	public boolean isValid ( ) {
		return location != null && radius >= 0.0D && time != null
				&& time.isValid ( ) && idle_time != null && idle_time.isValid ( );
	}
	
	@Override
	public boolean isInvalid ( ) {
		return !isValid ( );
	}
	
	@Override
	public BattlefieldBorderShrink load ( ConfigurationSection section ) {
		loadEntries ( section );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return saveEntries ( section );
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		BattlefieldBorderShrink that = ( BattlefieldBorderShrink ) o;
		return Double.compare ( that.radius , radius ) == 0 && Double.compare ( that.damage ,
																				damage ) == 0 && Objects.equals (
				location , that.location ) && Objects.equals ( time , that.time ) && Objects.equals (
				idle_time , that.idle_time );
	}
	
	@Override
	public int hashCode ( ) {
		return Objects.hash ( location , radius , damage , time , idle_time );
	}
}
