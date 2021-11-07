package es.outlook.adriansrj.battleroyale.vehicle;

import es.outlook.adriansrj.battleroyale.util.PluginUtil;
import es.outlook.adriansrj.battleroyale.util.qualityarmory.QualityArmoryVehiclesUtil;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author AdrianSR / 14/09/2021 / 07:31 p. m.
 */
public class VehiclesConfigurationEntry implements Configurable, Cloneable {
	
	public static VehiclesConfigurationEntry of ( ConfigurationSection section ) {
		return new VehiclesConfigurationEntry ( ).load ( section );
	}
	
	@ConfigurableEntry ( key = "chance" )
	protected double  chance;
	@ConfigurableEntry ( key = "vehicle-name" )
	protected String  vehicle_name;
	@ConfigurableEntry ( key = "fuel.enable" )
	protected boolean enable_fuel;
	@ConfigurableEntry ( key = "fuel.initial-fuel" )
	protected int     initial_fuel;
	
	public VehiclesConfigurationEntry ( double chance , String vehicle_name ,
			boolean enable_fuel , int initial_fuel ) {
		this.chance       = chance;
		this.vehicle_name = vehicle_name;
		this.enable_fuel  = enable_fuel;
		this.initial_fuel = initial_fuel;
	}
	
	public VehiclesConfigurationEntry ( double chance , String vehicle_name ) {
		this.chance       = chance;
		this.vehicle_name = vehicle_name;
	}
	
	public VehiclesConfigurationEntry ( ) {
		// to be loaded
	}
	
	public double getChance ( ) {
		return chance;
	}
	
	public String getVehicleName ( ) {
		return vehicle_name;
	}
	
	public boolean isEnableFuel ( ) {
		return enable_fuel;
	}
	
	/**
	 * Gets the initial fuel for this entry.
	 *
	 * @return the initial fuel.
	 */
	public int getInitialFuel ( ) {
		return initial_fuel;
	}
	
	@Override
	public VehiclesConfigurationEntry load ( ConfigurationSection section ) {
		loadEntries ( section );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return saveEntries ( section );
	}
	
	@Override
	public boolean isValid ( ) {
		if ( StringUtil.isNotBlank ( vehicle_name ) ) {
			return PluginUtil.isQualityArmoryVehiclesEnabled ( )
					&& QualityArmoryVehiclesUtil.isValidVehicle ( vehicle_name );
		}
		
		return false;
	}
	
	@Override
	public VehiclesConfigurationEntry clone ( ) {
		try {
			return ( VehiclesConfigurationEntry ) super.clone ( );
		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError ( );
		}
	}
}
