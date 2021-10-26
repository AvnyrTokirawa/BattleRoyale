package es.outlook.adriansrj.battleroyale.game.item;

import es.outlook.adriansrj.battleroyale.enums.EnumItem;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;

/**
 * @author AdrianSR / 25/10/2021 / 05:15 p. m.
 */
public final class BattleRoyaleItemHandler extends PluginHandler {
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BattleRoyaleItemHandler ( BattleRoyale plugin ) {
		super ( plugin );
		loadConfiguration ( );
	}
	
	private void loadConfiguration ( ) {
		File                      file = EnumFile.ITEM_CONFIGURATION.safeGetFile ( );
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( file );
		
		// this will save the defaults
		defaultConfigurationCheck ( file , yaml );
		
		// then loading
		for ( EnumItem item : EnumItem.values ( ) ) {
			if ( item.isConfigurable ( ) ) {
				ConfigurationSection section = yaml.getConfigurationSection ( formatName ( item ) );
				
				if ( section != null ) {
					item.load ( section );
				}
			}
		}
	}
	
	private void defaultConfigurationCheck ( File file , YamlConfigurationComments yaml ) {
		int save = 0;
		
		for ( EnumItem item : EnumItem.values ( ) ) {
			if ( item.isConfigurable ( ) ) {
				save += item.save ( YamlUtil.createNotExisting ( yaml , formatName ( item ) ) );
			}
		}
		
		if ( save > 0 ) {
			try {
				yaml.save ( file );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	private String formatName ( EnumItem item ) {
		return item.name ( ).toLowerCase ( ).replace ( '_' , '-' );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}