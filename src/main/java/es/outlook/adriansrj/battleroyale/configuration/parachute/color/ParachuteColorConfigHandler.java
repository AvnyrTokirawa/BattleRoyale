package es.outlook.adriansrj.battleroyale.configuration.parachute.color;

import es.outlook.adriansrj.battleroyale.configuration.SingleFileConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;

/**
 * @author AdrianSR / 08/11/2021 / 07:18 a. m.
 */
public final class ParachuteColorConfigHandler extends SingleFileConfigurationHandler {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public ParachuteColorConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public void initialize ( ) {
		File file = safeGetFile ( );
		
		if ( !file.exists ( ) ) {
			try {
				file.getParentFile ( ).mkdirs ( );
				file.createNewFile ( );
			} catch ( IOException ex ) {
				throw new IllegalStateException (
						"couldn't generate configuration file: " + file.getName ( ) , ex );
			}
		}
		
		// saving defaults
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( file );
		
		if ( saveDefaultConfiguration ( yaml ) > 0 ) {
			try {
				yaml.save ( file );
			} catch ( IOException ex ) {
				ex.printStackTrace ( );
			}
		}
		
		// then loading configuration
		loadConfiguration ( );
	}
	
	/**
	 * Saves the entries that not set right now in the configuration file.
	 *
	 * @param yaml the configuration yaml.
	 *
	 * @return the number of changes performed.
	 */
	private int saveDefaultConfiguration ( YamlConfigurationComments yaml ) {
		int save = 0;
		
		for ( Parachute.Color color : Parachute.Color.values ( ) ) {
			String section_name = color.name ( ).toLowerCase ( );
			
			if ( color != Parachute.Color.PLAYER && !yaml.isConfigurationSection ( section_name ) ) {
				save += color.save ( yaml.createSection ( section_name ) );
			}
		}
		return save;
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.PARACHUTE_COLOR_CONFIGURATION.getFile ( );
	}
	
	@Override
	public void loadConfiguration ( ) {
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( safeGetFile ( ) );
		
		for ( Parachute.Color color : Parachute.Color.values ( ) ) {
			if ( color != Parachute.Color.PLAYER ) {
				ConfigurationSection section = yaml.getConfigurationSection ( color.name ( ).toLowerCase ( ) );
				
				if ( section != null ) {
					color.load ( section );
				}
			}
		}
	}
	
	@Override
	public void save ( ) {
		// nothing to do here
	}
}
