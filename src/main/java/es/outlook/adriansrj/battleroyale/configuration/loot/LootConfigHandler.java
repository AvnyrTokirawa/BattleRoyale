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
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
		
		chest.put ( "sandstone" , new LootConfigurationEntry (
				UniversalMaterial.SANDSTONE , 0 , 10 , 20.0D ) );
		chest.put ( "diamond-sword" , new LootConfigurationEntry (
				UniversalMaterial.DIAMOND_SWORD , 0 , 1 , 8.0D ) );
		chest.put ( "stone-sword" , new LootConfigurationEntry (
				UniversalMaterial.STONE_SWORD , 0 , 1 , 12.0D ) );
		chest.put ( "iron-sword" , new LootConfigurationEntry (
				UniversalMaterial.IRON_SWORD , 0 , 1 , 10.0D ) );
		chest.put ( "ender-pearl" , new LootConfigurationEntry (
				UniversalMaterial.ENDER_PEARL , 0 , 1 , 5.0D ) );
		chest.put ( "first-aid" , new LootConfigurationEntry (
				ChatColor.DARK_GREEN + "First Aid" ,
				UniversalMaterial.POTION_HEALING_2 , 1 , 0 , 10.0D ,
				Collections.emptyList ( ) ) );
		
		containers.put ( EnumLootContainer.CHEST , new LootConfigurationContainer ( chest ) );
		
		// air supply
		Map < String, LootConfigurationEntry > air_supply = new HashMap <> ( );
		
		air_supply.put ( "sandstone" , new LootConfigurationEntry (
				UniversalMaterial.SANDSTONE , 0 , 128 , 30.0D ) );
		air_supply.put ( "diamond-sword" , new LootConfigurationEntry (
				UniversalMaterial.DIAMOND_SWORD , 0 , 1 , 20.0D ) );
		air_supply.put ( "bow" , new LootConfigurationEntry (
				UniversalMaterial.BOW , 0 , 1 , 17.0D ) );
		air_supply.put ( "arrow" , new LootConfigurationEntry (
				UniversalMaterial.ARROW , 0 , 30 , 15.0D ) );
		air_supply.put ( "gold-apple" , new LootConfigurationEntry (
				UniversalMaterial.GOLDEN_APPLE , 0 , 3 , 8.0D ) );
		
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