package es.outlook.adriansrj.battleroyale.configuration.bus;

import es.outlook.adriansrj.battleroyale.bus.Bus;
import es.outlook.adriansrj.battleroyale.bus.BusRegistry;
import es.outlook.adriansrj.battleroyale.bus.pet.BusPet;
import es.outlook.adriansrj.battleroyale.configuration.ConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.file.filter.YamlFileFilter;
import es.outlook.adriansrj.core.util.permission.Permissions;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.bukkit.ChatColor;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author AdrianSR / 23/09/2021 / 10:14 p. m.
 */
public final class BusesConfigHandler extends ConfigurationHandler {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public BusesConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public void initialize ( ) {
		File folder = EnumDirectory.BUS_DIRECTORY.getDirectory ( );
		
		// saving examples
		if ( !folder.exists ( ) ) {
			for ( EntityType type : EntityType.values ( ) ) {
				if ( type.getEntityClass ( ) != null && Animals.class.isAssignableFrom ( type.getEntityClass ( ) ) ) {
					saveBus ( new BusPet ( 1000 , Permissions.of ( "bus.pet-" + type.name ( ).toLowerCase ( ) ) , type ) ,
							  type.name ( ).toLowerCase ( ).replace ( '_' , '-' ) + ".yml" );
				}
			}
		}
		
		// saving default bus
		saveBus ( new BusPet ( EntityType.CHICKEN ) , Constants.DEFAULT_YAML_FILE_NAME );
		
		// then loading configuration
		loadConfiguration ( );
	}
	
	private void saveBus ( Bus bus , String file_name ) {
		File file = new File ( EnumDirectory.BUS_DIRECTORY.getDirectoryMkdirs ( ) , file_name );
		
		if ( !file.exists ( ) ) {
			try {
				if ( file.createNewFile ( ) ) {
					YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( file );
					
					if ( bus.save ( yaml ) > 0 ) {
						yaml.save ( file );
					}
				} else {
					throw new IllegalStateException ( "couldn't save default bus file" );
				}
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	@Override
	public void loadConfiguration ( ) {
		File folder = EnumDirectory.BUS_DIRECTORY.getDirectoryMkdirs ( );
		
		for ( File file : Objects.requireNonNull ( folder.listFiles ( new YamlFileFilter ( ) ) ) ) {
			try {
				Bus bus = Bus.of ( YamlConfigurationComments.loadConfiguration ( file ) );
				
				if ( bus != null && bus.isValid ( ) ) {
					BusRegistry.getInstance ( )
							.registerBus ( FilenameUtil.getBaseName ( file ) , bus );
				}
			} catch ( Exception ex ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , "Couldn't load bus '"
						+ FilenameUtil.getBaseName ( file ) + "'!" , BattleRoyale.getInstance ( ) );
				ex.printStackTrace ( );
			}
		}
	}
	
	@Override
	public void save ( ) {
		// nothing to do here
	}
}
