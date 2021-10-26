package es.outlook.adriansrj.battleroyale.data;

import es.outlook.adriansrj.battleroyale.enums.EnumDataStorage;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import org.bukkit.ChatColor;

import java.util.Optional;

/**
 * Class responsible for handling the storage of the data.
 *
 * @author AdrianSR / 15/09/2021 / 06:16 p. m.
 */
public final class DataStorageHandler extends PluginHandler {
	
	public static DataStorageHandler getInstance ( ) {
		return PluginHandler.getPluginHandler ( DataStorageHandler.class );
	}
	
	private final DataStorage data_storage;
	
	/**
	 * Constructs the plugin handler.
	 * <br>
	 * @param plugin the plugin to handle.
	 */
	public DataStorageHandler ( BattleRoyale plugin ) {
		super ( plugin );
		
		/* connecting to database */
		// this will throw an exception that
		// will disable the plugin in case
		// it cannot connect to the database.
		// trying to connect to database
		DataStorage data_storage = null;
		
		if ( EnumMainConfiguration.ENABLE_DATABASE.getAsBoolean ( ) ) {
			Exception error         = null;
			String    error_message = null;
			String    type_name     = EnumMainConfiguration.DATABASE_TYPE.getAsString ( );
			EnumDataStorage type = EnumReflection.getEnumConstant (
					EnumDataStorage.class , type_name.toUpperCase ( ) );
			
			if ( type != null ) {
				boolean successfully;
				
				try {
					data_storage = type.getImplementationClass ( ).getConstructor (
							BattleRoyale.class ).newInstance ( plugin );
					
					if ( data_storage.setUp ( ) ) {
						successfully = true;
						ConsoleUtil.sendPluginMessage (
								ChatColor.GREEN , "Connected to database" , plugin );
					} else {
						successfully  = false;
						error_message = "Couldn't connect to database!";
					}
				} catch ( Exception ex ) {
					error         = ex;
					successfully  = false;
					error_message = "Couldn't connect to database!";
				}
				
				if ( !successfully && data_storage != null ) {
					data_storage.dispose ( );
					data_storage = null;
				}
			} else {
				error_message = "Unknown database type '" + type_name + "'!";
			}
			
			if ( error_message != null ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , error_message , plugin );
			}
			
			if ( error != null ) {
				error.printStackTrace ( );
			}
		}
		
		this.data_storage = data_storage;
	}
	
	public DataStorage getDataStorage ( ) {
		return data_storage;
	}
	
	public Optional < DataStorage > getDataStorageOptional ( ) {
		return Optional.ofNullable ( getDataStorage ( ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
	
	public void dispose ( ) {
		if ( data_storage != null ) {
			data_storage.dispose ( );
		}
	}
}
