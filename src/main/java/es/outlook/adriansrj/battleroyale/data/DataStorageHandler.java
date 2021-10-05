package es.outlook.adriansrj.battleroyale.data;

import es.outlook.adriansrj.battleroyale.enums.EnumDataStorage;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import org.bukkit.ChatColor;

/**
 * Class responsible for handling the storage of the data.
 *
 * @author AdrianSR / 15/09/2021 / 06:16 p. m.
 */
public final class DataStorageHandler extends PluginHandler {
	
	public static DataStorageHandler getInstance ( ) {
		return ( DataStorageHandler ) PluginHandler.getPluginHandler ( DataStorageHandler.class );
	}
	
	private final DataStorage data_storage;
	
	/**
	 * Constructs the plugin handler.
	 * <br>
	 * @param plugin the plugin to handle.
	 */
	public DataStorageHandler ( BattleRoyale plugin ) throws Exception {
		super ( plugin );
		
		/* connecting to database */
		// this will throw an exception that
		// will disable the plugin in case
		// it cannot connect to the database.
		// trying to connect to database
		if ( EnumMainConfiguration.ENABLE_DATABASE.getAsBoolean ( ) ) {
			String type_name = EnumMainConfiguration.DATABASE_TYPE.getAsString ( );
			EnumDataStorage type = EnumReflection.getEnumConstant ( EnumDataStorage.class ,
																	type_name.toUpperCase ( ) );
			
			if ( type != null ) {
				this.data_storage = type.getImplementationClass ( ).getConstructor (
						BattleRoyale.class ).newInstance ( plugin );
			} else {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED ,
												"Unknown database type '" + type_name + "'!" , plugin );
				throw new IllegalArgumentException ( );
			}
			
			if ( this.data_storage.setUp ( ) ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.GREEN ,
												"Connected to database" , plugin );
			} else {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED ,
												"Couldn't connect to database!" , plugin );
				throw new IllegalStateException ( );
			}
		} else {
			this.data_storage = null;
		}
	}
	
	public DataStorage getDataStorage ( ) {
		return data_storage;
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
