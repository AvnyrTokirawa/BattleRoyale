package es.outlook.adriansrj.battleroyale.configuration.vehicle;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.PluginUtil;
import es.outlook.adriansrj.battleroyale.util.qualityarmory.QualityArmoryVehiclesUtil;
import es.outlook.adriansrj.battleroyale.vehicle.VehiclesConfiguration;
import es.outlook.adriansrj.battleroyale.vehicle.VehiclesConfigurationEntry;
import es.outlook.adriansrj.battleroyale.vehicle.VehiclesConfigurationRegistry;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.file.filter.YamlFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author AdrianSR / 14/09/2021 / 07:41 p. m.
 */
public final class BattleRoyaleVehiclesConfigHandler extends ConfigurationHandler {
	
	private static final VehiclesConfiguration DEFAULT_VEHICLES_CONFIGURATION;
	
	static {
		Set < VehiclesConfigurationEntry > entries = new HashSet <> ( );
		
		// quality armory vehicles
		VehiclesConfiguration quality_armory = null;
		
		if ( PluginUtil.isQualityArmoryVehiclesEnabled ( ) ) {
			quality_armory = QualityArmoryVehiclesUtil.generateVehiclesConfiguration ( );
		}
		
		if ( quality_armory != null ) {
			entries.addAll ( quality_armory.getEntries ( ) );
		}
		
		// TODO: include any other vehicle plugin here
		
		DEFAULT_VEHICLES_CONFIGURATION = new VehiclesConfiguration ( entries );
	}
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public BattleRoyaleVehiclesConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public void initialize ( ) {
		File folder       = EnumDirectory.VEHICLE_DIRECTORY.getDirectoryMkdirs ( );
		File default_file = new File ( folder , Constants.DEFAULT_YAML_FILE_NAME );
		
		// saving default configuration
		if ( !default_file.exists ( ) ) {
			try {
				if ( default_file.createNewFile ( ) ) {
					DEFAULT_VEHICLES_CONFIGURATION.save ( default_file );
				} else {
					throw new IllegalStateException ( "couldn't save default vehicles configuration file" );
				}
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
		
		// then loading
		loadConfiguration ( );
	}
	
	@Override
	public void loadConfiguration ( ) {
		File folder = EnumDirectory.VEHICLE_DIRECTORY.getDirectoryMkdirs ( );
		
		for ( File file : Objects.requireNonNull ( folder.listFiles ( new YamlFileFilter ( ) ) ) ) {
			VehiclesConfigurationRegistry.getInstance ( ).registerConfiguration (
					FilenameUtil.getBaseName ( file ) , VehiclesConfiguration.of ( file ) );
		}
	}
	
	@Override
	public void save ( ) {
		// nothing to do here
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}