package es.outlook.adriansrj.battleroyale.configuration.loot;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.enums.EnumLootContainer;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfiguration;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfigurationContainer;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfigurationEntry;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfigurationRegistry;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.file.filter.YamlFileFilter;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author AdrianSR / 12/09/2021 / 12:56 p. m.
 */
public final class LootConfigHandler extends ConfigurationHandler {
	
	private static final LootConfiguration DEFAULT_LOOT_CONFIGURATION;
	
	static {
		Map < EnumLootContainer, LootConfigurationContainer > containers = new EnumMap <> ( EnumLootContainer.class );
		
		// initial
		Map < String, LootConfigurationEntry > initial = new HashMap <> ( );
		
		initial.put ( "sandstone" , new LootConfigurationEntry ( UniversalMaterial.SANDSTONE , 30 ) );
		
		containers.put ( EnumLootContainer.INITIAL , new LootConfigurationContainer ( initial ) );
		
		// chest
		Map < String, LootConfigurationEntry > chest = new HashMap <> ( );
		
		chest.put ( "sandstone" , new LootConfigurationEntry ( UniversalMaterial.SANDSTONE , 10 ) );
		chest.put ( "diamond-sword" , new LootConfigurationEntry ( UniversalMaterial.DIAMOND_SWORD , 1 ) );
		chest.put ( "stone-sword" , new LootConfigurationEntry ( UniversalMaterial.STONE_SWORD , 1 ) );
		chest.put ( "iron-sword" , new LootConfigurationEntry ( UniversalMaterial.IRON_SWORD , 1 ) );
		
		containers.put ( EnumLootContainer.CHEST , new LootConfigurationContainer ( chest ) );
		
		// air supply
		Map < String, LootConfigurationEntry > air_supply = new HashMap <> ( );
		
		air_supply.put ( "sandstone" , new LootConfigurationEntry ( UniversalMaterial.SANDSTONE , 128 ) );
		air_supply.put ( "diamond-sword" , new LootConfigurationEntry ( UniversalMaterial.DIAMOND_SWORD , 1 ) );
		air_supply.put ( "bow" , new LootConfigurationEntry ( UniversalMaterial.BOW , 1 ) );
		air_supply.put ( "arrow" , new LootConfigurationEntry ( UniversalMaterial.ARROW , 30 ) );
		
		containers.put ( EnumLootContainer.AIR_SUPPLY , new LootConfigurationContainer ( air_supply ) );
		
		DEFAULT_LOOT_CONFIGURATION = new LootConfiguration ( containers );
	}
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public LootConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public void initialize ( ) {
		File folder       = EnumDirectory.LOOT_DIRECTORY.getDirectoryMkdirs ( );
		File default_file = new File ( folder , Constants.DEFAULT_YAML_FILE_NAME );
		
		// saving default configuration
		if ( !default_file.exists ( ) ) {
			try {
				if ( default_file.createNewFile ( ) ) {
					DEFAULT_LOOT_CONFIGURATION.save ( default_file );
				} else {
					throw new IllegalStateException ( "couldn't save default loot configuration file" );
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
		File folder = EnumDirectory.LOOT_DIRECTORY.getDirectoryMkdirs ( );
		
		for ( File file : Objects.requireNonNull ( folder.listFiles ( new YamlFileFilter ( ) ) ) ) {
			LootConfigurationRegistry.getInstance ( ).registerConfiguration (
					FilenameUtil.getBaseName ( file ) , LootConfiguration.of ( file ) );
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