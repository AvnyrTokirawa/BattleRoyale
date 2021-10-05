package es.outlook.adriansrj.battleroyale.util.qualityarmory;

import es.outlook.adriansrj.battleroyale.vehicle.VehiclesConfiguration;
import es.outlook.adriansrj.battleroyale.vehicle.VehiclesConfigurationEntry;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.vehicles.AbstractCar;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Useful class for dealing with the QualityArmoryVehicles2 plugin.
 *
 * @author AdrianSR / 14/09/2021 / 07:55 p. m.
 */
public class QualityArmoryVehiclesUtil {
	
	public static VehiclesConfiguration generateVehiclesConfiguration ( ) {
		Set < VehiclesConfigurationEntry > entries = new HashSet <> ( );
		
		for ( AbstractVehicle vehicle : Main.vehicleTypes ) {
			if ( vehicle instanceof AbstractCar ) {
				entries.add ( new VehiclesConfigurationEntry (
						( int ) ( Math.random ( ) * 100.0D ) , vehicle.getName ( ) ,
						false , 0 ) );
			}
		}
		
		return new VehiclesConfiguration ( entries );
	}
	
	public static boolean isValidVehicle ( String vehicle_name ) {
		return QualityArmoryVehicles.getVehicle ( vehicle_name ) != null;
	}
	
	public static void spawnVehicle ( Location location , VehiclesConfigurationEntry entry ) {
		List < Player > online = new ArrayList <> ( Bukkit.getOnlinePlayers ( ) );
		
		if ( online.isEmpty ( ) ) {
			throw new IllegalStateException ( "cannot spawn vehicle as there are not online players" );
		}
		
		AbstractVehicle vehicle = QualityArmoryVehicles.getVehicle ( entry.getVehicleName ( ) );
		
		if ( vehicle != null ) {
			VehicleEntity instance = QualityArmoryVehicles.spawnVehicle (
					vehicle , location , online.get ( 0 ) );
			
			// fuel
			if ( entry.isEnableFuel ( ) ) {
				instance.setFuel ( Math.max ( entry.getInitialFuel ( ) , 0 ) );
			} else {
				instance.setFuel ( Integer.MAX_VALUE );
			}
			
			// allowing any player to enter the vehicle
			online.forEach ( player -> instance.addToWhitelist ( player.getUniqueId ( ) ) );
		}
	}
}