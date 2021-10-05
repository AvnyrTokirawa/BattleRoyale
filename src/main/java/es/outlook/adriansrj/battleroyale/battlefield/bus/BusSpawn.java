package es.outlook.adriansrj.battleroyale.battlefield.bus;

import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.configurable.vector.ConfigurableVector;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.Objects;

/**
 * Battle royale bus spawn.
 *
 * @author AdrianSR / 07/09/2021 / 05:42 p. m.
 */
public class BusSpawn implements Configurable {
	
	/**
	 * Default bus displacement speed.
	 */
	public static final double DEFAULT_BUS_SPEED = 1.25D;
	
	@ConfigurableEntry ( subsection = "start-location" )
	protected ConfigurableVector start_location;
	@ConfigurableEntry ( key = "yaw" )
	protected float              yaw;
	@ConfigurableEntry ( key = "door-point-distance" )
	protected double             door_point_distance;
	@ConfigurableEntry ( key = "speed" )
	protected double             speed;
	
	public BusSpawn ( Vector start_location , float yaw , double door_point_distance , double speed ) {
		this.start_location      = new ConfigurableVector ( start_location );
		this.yaw                 = yaw;
		this.door_point_distance = door_point_distance;
		this.speed               = speed;
	}
	
	public BusSpawn ( ) {
		// to be loaded
	}
	
	public ConfigurableVector getStartLocation ( ) {
		return start_location;
	}
	
	public float getYaw ( ) {
		return yaw;
	}
	
	public double getDoorPointDistance ( ) {
		return Math.max ( door_point_distance , 0.0D );
	}
	
	public double getSpeed ( ) {
		return speed;
	}
	
	@Override
	public BusSpawn load ( ConfigurationSection section ) {
		loadEntries ( section );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return saveEntries ( section );
	}
	
	@Override
	public boolean isValid ( ) {
		return start_location != null && start_location.isValid ( );
	}
	
	@Override
	public boolean isInvalid ( ) {
		return !isValid ( );
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		BusSpawn busSpawn = ( BusSpawn ) o;
		return Float.compare ( busSpawn.yaw , yaw ) == 0
				&& Double.compare ( busSpawn.door_point_distance , door_point_distance ) == 0
				&& Objects.equals ( start_location , busSpawn.start_location );
	}
	
	@Override
	public int hashCode ( ) {
		return Objects.hash ( start_location , yaw , door_point_distance );
	}
}