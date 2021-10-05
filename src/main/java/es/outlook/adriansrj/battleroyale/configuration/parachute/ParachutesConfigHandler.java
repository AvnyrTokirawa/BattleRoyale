package es.outlook.adriansrj.battleroyale.configuration.parachute;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustom;
import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustomModel;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteRegistry;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.file.filter.YamlFileFilter;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author AdrianSR / 11/09/2021 / 11:50 a. m.
 */
public final class ParachutesConfigHandler extends ConfigurationHandler {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public ParachutesConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public void initialize ( ) {
		File folder       = EnumDirectory.PARACHUTE_DIRECTORY.getDirectoryMkdirs ( );
		File default_file = new File ( folder , Constants.DEFAULT_YAML_FILE_NAME );
		
		// saving default parachute
		if ( !default_file.exists ( ) ) {
			try {
				if ( default_file.createNewFile ( ) ) {
					YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( default_file );
					
					if ( new ParachuteCustom ( 0 , null ,
											   ParachuteCustomModel.DEFAULT_MODEL ).save ( yaml ) > 0 ) {
						yaml.save ( default_file );
					}
				} else {
					throw new IllegalStateException ( "couldn't save default parachute file" );
				}
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
		
		// then loading configuration
		loadConfiguration ( );
	}
	
	@Override
	public void loadConfiguration ( ) {
		File folder = EnumDirectory.PARACHUTE_DIRECTORY.getDirectoryMkdirs ( );
		
		for ( File file : Objects.requireNonNull ( folder.listFiles ( new YamlFileFilter ( ) ) ) ) {
			try {
				Parachute parachute = Parachute.of (
						YamlConfigurationComments.loadConfiguration ( file ) );
				
				if ( parachute != null && parachute.isValid ( ) ) {
					ParachuteRegistry.getInstance ( )
							.registerParachute ( FilenameUtil.getBaseName ( file ) , parachute );
				}
			} catch ( Exception ex ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , "Couldn't load parachute '"
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
