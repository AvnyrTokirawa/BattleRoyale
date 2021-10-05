package es.outlook.adriansrj.battleroyale.battlefield;

import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderSuccession;
import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderSuccessionRandom;
import es.outlook.adriansrj.battleroyale.battlefield.bus.BusSpawn;
import es.outlook.adriansrj.battleroyale.loot.LootConfiguration;
import es.outlook.adriansrj.battleroyale.loot.LootConfigurationRegistry;
import es.outlook.adriansrj.battleroyale.vehicle.VehiclesConfiguration;
import es.outlook.adriansrj.battleroyale.vehicle.VehiclesConfigurationRegistry;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableCollectionEntry;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.configurable.vector.ConfigurableVector;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Class responsible for handling the configuration of a {@link Battlefield}.
 *
 * @author AdrianSR / 04/09/2021 / 11:09 p. m.
 */
public class BattlefieldConfiguration implements Configurable {
	
	protected static final String BORDER_SHRINK_KEY = "border-shrink";
	
	/**
	 * Loads the {@link BattlefieldConfiguration} in the provided {@link ConfigurationSection}.
	 *
	 * @param section the configuration section to load from.
	 * @return the resulting configuration.
	 */
	public static BattlefieldConfiguration of ( ConfigurationSection section ) {
		return new BattlefieldConfiguration ( ).load ( section );
	}
	
	/** bus random spawns */
	@ConfigurableCollectionEntry ( subsection = "bus-spawns", subsectionprefix = "spawn-" )
	protected final Set < BusSpawn > bus_spawns = new HashSet <> ( );
	
	/** player random spawns */
	@ConfigurableCollectionEntry ( subsection = "player-spawns", subsectionprefix = "spawn-" )
	protected final Set < ConfigurableVector > player_spawns = new HashSet <> ( );
	
	/** vehicles configuration */
	@ConfigurableEntry ( key = "vehicles-configuration" )
	protected String vehicles_configuration;
	
	/** vehicle random spawns */
	@ConfigurableCollectionEntry ( subsection = "vehicle-spawns", subsectionprefix = "spawn-" )
	protected final Set < ConfigurableVector > vehicle_spawns = new HashSet <> ( );
	
	/** border shrink succession */
	protected BattlefieldBorderSuccession border_shrink;
	
	/** loot configuration */
	@ConfigurableEntry ( key = "loot-configuration" )
	protected String loot_configuration;
	
	/** loot chests */
	@ConfigurableCollectionEntry ( subsection = "loot-chests", subsectionprefix = "loot-chest-" )
	protected final Set < ConfigurableVector > loot_chests = new HashSet <> ( );
	
	public BattlefieldConfiguration ( ) {
		// to be loaded
	}
	
	// ----- bus spawns
	
	public Set < BusSpawn > getBusSpawns ( ) {
		return bus_spawns;
	}
	
	public boolean addBusSpawn ( BusSpawn spawn ) {
		return this.bus_spawns.add ( Objects.requireNonNull ( spawn , "spawn cannot be null" ) );
	}
	
	public boolean removeBusSpawn ( BusSpawn spawn ) {
		return this.bus_spawns.remove ( spawn );
	}
	
	// ----- player spawns
	
	public Set < ConfigurableVector > getPlayerSpawns ( ) {
		return player_spawns;
	}
	
	public boolean addPlayerSpawn ( Vector location ) {
		return this.player_spawns.add ( new ConfigurableVector (
				Objects.requireNonNull ( location , "spawn location cannot be null" ) ) );
	}
	
	public boolean removePlayerSpawn ( Vector location ) {
		return this.player_spawns.remove ( location )
				|| this.player_spawns.remove ( new ConfigurableVector ( location ) );
	}
	
	// ----- vehicle configuration
	
	public String getVehiclesConfigurationName ( ) {
		return vehicles_configuration;
	}
	
	/**
	 * Gets the configuration of the vehicles the battlefield will use to spawn vehicles around.
	 * <br>
	 * If {@link #vehicles_configuration} is <b>blank</b>, or there is no vehicles configurations
	 * registered with that name, then the next valid configuration of vehicles registered in
	 * the {@link VehiclesConfigurationRegistry} will be used. In case there is no valid
	 * configurations of vehicles, then <b>null</b> will be returned.
	 *
	 * @return configuration of vehicles the battlefield will use to spawn vehicles around the world,
	 * or <b>null</b>.
	 */
	public VehiclesConfiguration getVehiclesConfiguration ( ) {
		VehiclesConfiguration configuration = this.vehicles_configuration != null ? VehiclesConfigurationRegistry
				.getInstance ( ).getConfiguration ( FilenameUtil.getBaseName ( this.vehicles_configuration ) ) : null;
		
		// finding out the next valid
		if ( configuration == null ) {
			configuration = VehiclesConfigurationRegistry.getInstance ( ).getConfigurations ( ).values ( ).stream ( )
					.filter ( Objects :: nonNull ).filter ( VehiclesConfiguration :: isValid )
					.findAny ( ).orElse ( null );
		}
		
		return configuration;
	}
	
	/**
	 * Sets the configuration of the vehicles the battlefield will use to spawn
	 * vehicles around.
	 * <br>
	 * Note that the provided name must refer to a {@link VehiclesConfiguration} registered
	 * in the {@link VehiclesConfigurationRegistry}.
	 *
	 * @param configuration_name the name of the configuration of the vehicles.
	 */
	public void setVehiclesConfiguration ( String configuration_name ) {
		this.vehicles_configuration = configuration_name;
	}
	
	// ----- vehicle spawns
	
	public Set < ConfigurableVector > getVehicleSpawns ( ) {
		return vehicle_spawns;
	}
	
	public boolean addVehicleSpawn ( Vector location ) {
		return this.vehicle_spawns.add ( new ConfigurableVector (
				Objects.requireNonNull ( location , "spawn location cannot be null" ) ) );
	}
	
	public boolean removeVehicleSpawn ( Vector location ) {
		return this.vehicle_spawns.remove ( location )
				|| this.vehicle_spawns.remove ( new ConfigurableVector ( location ) );
	}
	
	// ----- loot configuration
	
	public String getLootConfigurationName ( ) {
		return loot_configuration;
	}
	
	/**
	 * Gets the loot configuration the battlefield will use to fill the containers around.
	 * <br>
	 * If {@link #loot_configuration} is <b>blank</b>, or there is no loot configurations
	 * registered with that name, then the next valid loot configuration registered in
	 * the {@link LootConfigurationRegistry} will be used. In case there is no valid loot
	 * configurations, then <b>null</b> will be returned.
	 *
	 * @return loot configuration the battlefield will use to fill the containers around the world,
	 * or <b>null</b>.
	 */
	public LootConfiguration getLootConfiguration ( ) {
		LootConfiguration loot_configuration = this.loot_configuration != null ? LootConfigurationRegistry
				.getInstance ( ).getConfiguration ( FilenameUtil.getBaseName ( this.loot_configuration ) ) : null;
		
		// finding out the next valid
		if ( loot_configuration == null ) {
			loot_configuration = LootConfigurationRegistry.getInstance ( ).getConfigurations ( ).values ( ).stream ( )
					.filter ( Objects :: nonNull ).filter ( LootConfiguration :: isValid )
					.findAny ( ).orElse ( null );
		}
		
		return loot_configuration;
	}
	
	/**
	 * Sets the loot configuration the battlefield will use to fill loot
	 * containers around.
	 * <br>
	 * Note that the provided name must refer to a loot configuration registered
	 * in the {@link LootConfigurationRegistry}.
	 *
	 * @param loot_configuration_name the name of the loot configuration.
	 */
	public void setLootConfiguration ( String loot_configuration_name ) {
		this.loot_configuration = loot_configuration_name;
	}
	
	// ----- loot chests
	
	public Set < ConfigurableVector > getLootChests ( ) {
		return loot_chests;
	}
	
	public boolean addLootChest ( Vector location ) {
		return this.loot_chests.add ( new ConfigurableVector (
				Objects.requireNonNull ( location , "chest location cannot be null" ) ) );
	}
	
	public boolean removeLootChest ( Vector location ) {
		return this.loot_chests.remove ( location )
				|| this.loot_chests.remove ( new ConfigurableVector ( location ) );
	}
	
	// ----- border shrink succession
	
	public BattlefieldBorderSuccession getBorderShrinkSuccession ( ) {
		return border_shrink;
	}
	
	public void setBorderShrinkSuccession ( BattlefieldBorderSuccession shrink_succession ) {
		this.border_shrink = shrink_succession;
	}
	
	@Override
	public BattlefieldConfiguration load ( ConfigurationSection section ) {
		loadEntries ( section );
		
		// border shrink
		if ( section.isConfigurationSection ( BORDER_SHRINK_KEY ) ) {
			ConfigurationSection border_shrink_section = section.getConfigurationSection ( BORDER_SHRINK_KEY );
			
			if ( ( border_shrink = new BattlefieldBorderSuccessionRandom ( border_shrink_section ) ).isInvalid ( ) ) {
				this.border_shrink = new BattlefieldBorderSuccession ( ).load ( border_shrink_section );
			}
		}
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return saveEntries ( section );
	}
	
	/**
	 * Saves this configuration in the specified file.
	 *
	 * @param file the file to save.
	 * @throws IOException something went wrong.
	 */
	public void save ( File file ) throws IOException {
		if ( file.exists ( ) ) {
			file.delete ( );
		}
		
		file.getParentFile ( ).mkdirs ( );
		file.createNewFile ( );
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration ( file );
		
		if ( save ( yaml ) > 0 ) {
			yaml.save ( file );
		}
	}
	
	@Override
	public boolean isValid ( ) {
		// at least one bus-spawn/player-spawn must be valid
		return bus_spawns.stream ( ).anyMatch ( BusSpawn :: isValid )
				|| player_spawns.stream ( ).anyMatch ( ConfigurableVector :: isValid );
	}
	
	@Override
	public boolean isInvalid ( ) {
		return !isValid ( );
	}
}
