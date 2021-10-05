package es.outlook.adriansrj.battleroyale.configuration;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;

import java.io.File;
import java.io.IOException;

/**
 * @author AdrianSR / 22/08/2021 / Time: 11:06 p. m.
 */
public abstract class ScalableConfigurationHandler extends SingleFileConfigurationHandler {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public ScalableConfigurationHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	protected abstract void loadConfiguration ( YamlConfigurationComments yaml );
	
	protected abstract int saveDefaultConfiguration ( YamlConfigurationComments yaml );
	
	protected abstract int save ( YamlConfigurationComments yaml );
	
	@Override
	public void initialize ( ) {
		File file = safeGetFile ( );
		
		if ( ! file.exists ( ) ) {
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
	
	@Override
	public void loadConfiguration ( ) {
		loadConfiguration ( YamlConfigurationComments.loadConfiguration ( safeGetFile ( ) ) );
	}
	
	@Override
	public void save ( ) {
		File                      file = safeGetFile ( );
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( file );
		
		if ( save ( yaml ) > 0 ) {
			try {
				yaml.save ( file );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
}