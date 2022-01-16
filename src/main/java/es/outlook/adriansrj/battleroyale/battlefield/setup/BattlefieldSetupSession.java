package es.outlook.adriansrj.battleroyale.battlefield.setup;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaWorld;
import es.outlook.adriansrj.battleroyale.battlefield.Battlefield;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldConfiguration;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShape;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShapePart;
import es.outlook.adriansrj.battleroyale.battlefield.border.BattlefieldBorderSuccession;
import es.outlook.adriansrj.battleroyale.battlefield.bus.BusSpawn;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.Minimap;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.generator.MinimapGenerator;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.renderer.MinimapRendererSetupSession;
import es.outlook.adriansrj.battleroyale.enums.EnumBattleMapSetupTool;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.enums.EnumWorldGenerator;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfigurationRegistry;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.schedule.ScheduledExecutorPool;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.MiniMapUtil;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.battleroyale.util.WorldEditUtil;
import es.outlook.adriansrj.battleroyale.util.file.FileUtil;
import es.outlook.adriansrj.battleroyale.util.itemstack.ItemStackUtil;
import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.battleroyale.util.schematic.SchematicUtil;
import es.outlook.adriansrj.battleroyale.util.world.WorldUtil;
import es.outlook.adriansrj.battleroyale.vehicle.VehiclesConfiguration;
import es.outlook.adriansrj.battleroyale.vehicle.VehiclesConfigurationRegistry;
import es.outlook.adriansrj.battleroyale.world.arena.ArenaWorldGenerator;
import es.outlook.adriansrj.battleroyale.world.data.WorldData;
import es.outlook.adriansrj.battleroyale.world.region.Region;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.file.filter.FileExtensionFilter;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.math.Vector3D;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import es.outlook.adriansrj.core.util.server.Version;
import es.outlook.adriansrj.core.util.world.GameRuleDisableDaylightCycle;
import es.outlook.adriansrj.core.util.world.GameRuleType;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.lang3.Validate;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * {@link Battlefield} setup session.
 *
 * @author AdrianSR / 23/08/2021 / Time: 10:59 a. m.
 */
public class BattlefieldSetupSession {
	
	// executor service for starting new setup sessions
	protected static final ExecutorService INITIALIZER_EXECUTOR_SERVICE;
	// executor service in a single thread for calculating
	// the battlefield schematic/minimap.
	protected static final ExecutorService SHAPE_EXECUTOR_SERVICE;
	
	static {
		INITIALIZER_EXECUTOR_SERVICE = ScheduledExecutorPool.getInstance ( ).getSingleThreadScheduledExecutor ( );
		SHAPE_EXECUTOR_SERVICE       = ScheduledExecutorPool.getInstance ( ).getSingleThreadScheduledExecutor ( );
	}
	
	// this creator is for cases where the input is a battlefield.
	// the shape of the battlefield will be pasted into the ordinary
	// setup world, and the name, minimap and any other configuration
	// will be recovered.
	protected static void newSetupSession ( Player configurator , Battlefield battlefield ,
			Consumer < BattlefieldSetupSession > callback ) {
		BattlefieldShape shape = battlefield.getShape ( );
		
		if ( shape != null && shape.getSize ( ) > 0 ) {
			// we will load a copy of the prepared world to make
			// this process faster. we will prepare it if
			// never prepared before.
			File prepared = new File ( battlefield.getFolder ( ) ,
									   BattleRoyaleArenaWorld.PREPARED_WORLD_FOLDER_NAME );
			
			if ( prepared.exists ( ) && WorldUtil.worldFolderCheck ( prepared ) ) {
				if ( BattleRoyaleArenaHandler.getInstance ( ).getArenas ( ).stream ( )
						.anyMatch ( arena -> Objects.equals ( arena.getBattlefield ( ) , battlefield ) )
						&& BattleRoyaleArenaWorld.PREPARE_LOCK.isLocked ( ) ) {
					// battlefield world already being prepared. all we
					// have to do as this point is schedule the start of
					// the session from the preparation thread, so it will
					// start the session once the world is prepared.
					BattleRoyaleArenaWorld.PREPARE_EXECUTOR.execute ( ( ) -> newSetupSessionFromBattlefield (
							configurator , battlefield , prepared , callback ) );
				} else {
					// battlefield world already prepared, no need to wait.
					newSetupSessionFromBattlefield ( configurator , battlefield , prepared , callback );
				}
			} else {
				if ( prepared.exists ( ) ) {
					// prepared world exists but is invalid,
					// lets generate it again.
					try {
						FileUtil.deleteDirectory ( prepared );
					} catch ( IOException e ) {
						e.printStackTrace ( );
					}
				}
				
				if ( !prepared.mkdirs ( ) ) {
					throw new IllegalStateException ( "couldn't create battlefield prepared world folder" );
				}
				
				// world preparation. we will schedule the preparation
				// from the arenas world preparation executor as we want
				// to prevent having multiple world preparations at the
				// same time, resulting in leak of heap space.
				BattleRoyaleArenaWorld.PREPARE_EXECUTOR.execute ( ( ) -> {
					BattleRoyaleArenaWorld.PREPARE_LOCK.lock ( );
					
					try {
						ArenaWorldGenerator generator = createGenerator ( prepared );
						
						// inserting and centering shape.
						// the shape will be inserted in the 0,0,0
						// coordinates, so we can save unit-locations
						// that allows us to relocate them later.
						int size_half = shape.getSize ( ) / 2;
						int index     = 0;
						
						for ( BattlefieldShapePart part : shape.getParts ( ).values ( ) ) {
							Location2I part_location = part.getLocation ( );
							int        part_x        = part_location.getX ( );
							int        part_z        = part_location.getZ ( );
							int        part_block_x  = ( part_x << 7 ) - size_half;
							int        part_block_z  = ( part_z << 7 ) - size_half;
							
							try {
								// it will load the schematic of the part,
								// and the garbage collector should dispose it.
								generator.insert ( part.loadContent ( battlefield.getFolder ( ) ) ,
												   new Vector ( part_block_x , 0.0D , part_block_z ) ,
												   true );
								// saving heap space.
								generator.save ( );
								generator.flush ( );
								
								// printing progress
								BattleRoyale.getInstance ( ).getLogger ( ).info (
										"Preparing battlefield for setup... " +
												( 100.0D * ( ( double ) ++index / shape.getParts ( ).size ( ) ) ) + "%" );
							} catch ( FileNotFoundException ex ) {
								// we want to be able to load incomplete
								// battlefield shapes as we don't want
								// to lose a whole battlefield just for a missing part.
								BattleRoyale.getInstance ( ).getLogger ( ).warning (
										"Missing battlefield shape part file (" + part_x + ", " + part_z + ")" );
							}
						}
						
						// generation done
						generator.save ( );
						generator.flush ( );
						
						// setup session stuff done.
						newSetupSessionFromBattlefield ( configurator , battlefield , prepared , callback );
					} finally {
						BattleRoyaleArenaWorld.PREPARE_LOCK.unlock ( );
					}
				} );
			}
		} else {
			throw new IllegalArgumentException ( "invalid battlefield shape" );
		}
	}
	
	private static void newSetupSessionFromBattlefield ( Player configurator , Battlefield battlefield ,
			File prepared , Consumer < BattlefieldSetupSession > callback ) {
		Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> {
			try {
				BattlefieldSetupSession result = newSetupSession (
						configurator , prepared ,
						// copy must be set to true as we
						// don't want the plugin to load the
						// world from the prepared folder.
						true );
				
				// recovering values
				int size_half = battlefield.getShape ( ).getSize ( ) / 2;
				
				result.configuration = battlefield.getConfiguration ( );
				result.name          = battlefield.getName ( );
				result.minimap       = battlefield.getMinimap ( );
				result.result        = null;
				result.setBounds ( new ZoneBounds ( -size_half , -size_half , size_half , size_half ) ,
								   false , false );
				
				callback.accept ( result );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		} );
	}
	
	protected static BattlefieldSetupSession newSetupSession ( Player configurator , File input_world_folder ,
			boolean copy ) throws IOException {
		Validate.isTrue ( WorldUtil.worldFolderCheck ( input_world_folder ) , "invalid world folder" );
		
		File world_folder = input_world_folder;
		
		// a copy of the world will actually
		// be loaded, instead of the original.
		if ( copy ) {
			world_folder = getNewTempWorldFolder ( );
			
			// copying only the files that are actually important
			for ( File file : Objects.requireNonNull ( input_world_folder.listFiles ( ) ) ) {
				switch ( file.getName ( ).toLowerCase ( ) ) {
					case WorldUtil.LEVEL_DATA_FILE_NAME:
					case WorldUtil.LEVEL_DATA_OLD_FILE_NAME:
					case WorldUtil.REGION_FOLDER_NAME: {
						if ( file.isDirectory ( ) ) {
							FileUtil.copyDirectoryToDirectory ( file , world_folder );
						} else {
							FileUtil.copyFileToDirectory ( file , world_folder );
						}
						break;
					}
					
					default:
						break;
				}
			}
		}
		
		World world = WorldUtil.loadWorldEmpty ( world_folder );
		GameRuleType.FIRE_TICK.apply ( world , false );
		GameRuleType.KEEP_INVENTORY.apply ( world , true );
		new GameRuleDisableDaylightCycle ( ).apply ( world );
		
		return new BattlefieldSetupSession ( configurator , world , null );
	}
	
	protected static BattlefieldSetupSession newSetupSession ( Player configurator , File input_world_folder )
			throws IOException {
		return newSetupSession ( configurator , input_world_folder , true );
	}
	
	// this creator is for cases where the input is a schematic.
	// the schematic will be pasted into the ordinary setup world.
	protected static void newSetupSession ( Player configurator , Clipboard input , String name ,
			Consumer < BattlefieldSetupSession > callback ) {
		File world_folder = getNewTempWorldFolder ( );
		
		// let's generate it
		INITIALIZER_EXECUTOR_SERVICE.execute ( ( ) -> {
			/* world generation */
			ArenaWorldGenerator generator = createGenerator ( world_folder );
			
			// inserting and centering input schematic
			// the schematic will be inserted in the 0,0,0
			// coordinates, so we can save unit-locations
			// that allows us to relocate them later.
			Vector3D dimensions = Objects.requireNonNull ( WorldEditUtil.getDimensions ( input ) ,
														   "couldn't determine schematic dimensions" );
			int x_size_half = ( int ) Math.round ( ( dimensions.getX ( ) / 2 ) );
			int z_size_half = ( int ) Math.round ( ( dimensions.getZ ( ) / 2 ) );
			
			generator.insert ( input , new Vector ( -x_size_half , 0 , -z_size_half ) , true );
			generator.save ( );
			generator.flush ( );
			
			// setup session successfully created, let's call
			// the callback from the bukkit thread.
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> {
				try {
					BattlefieldSetupSession result = newSetupSession ( configurator , world_folder , false );
					
					Consumer < Minimap > minimap_callback = minimap -> {
						// we will give the minimap item to any
						// player in the session once the minimap is
						// successfully generated.
						if ( minimap != null ) {
							giveMinimap ( result.getOwner ( ) , result );
							
							for ( Player invited : result.getGuestList ( ) ) {
								giveMinimap ( invited , result );
							}
						}
					};
					
					Consumer < Boolean > schematic_callback = schematic -> {
						// this will let the player know that schematic
						// was successfully exported.
						if ( schematic != null && schematic ) {
							configurator.sendMessage ( ChatColor.GREEN + "Schematic generated successfully!" );
						}
					};
					
					configurator.sendMessage ( ChatColor.GOLD + "Bounds were automatically calculated!" );
					configurator.sendMessage ( ChatColor.GOLD + "* Generating minimap..." );
					configurator.sendMessage ( ChatColor.GOLD + "* Generating schematic..." );
					
					result.name = name;
					result.setBounds ( new ZoneBounds ( -x_size_half , -z_size_half , x_size_half , z_size_half ) ,
									   minimap_callback , schematic_callback );
					
					callback.accept ( result );
				} catch ( IOException e ) {
					e.printStackTrace ( );
				}
			} );
		} );
	}
	
	protected static void giveMinimap ( Player br_player , BattlefieldSetupSession session ) {
		br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
			player.getInventory ( ).addItem (
					ItemStackUtil.createViewItemStack ( MiniMapUtil.createView (
							new MinimapRendererSetupSession ( session ) , session.getWorld ( ) ) ) );
			player.updateInventory ( );
			
			// letting know
			player.sendMessage ( ChatColor.GREEN + "Minimap generated successfully!" );
		} );
	}
	
	protected static File getNewTempWorldFolder ( ) {
		File world_folder = new File ( EnumDirectory.BATTLEFIELD_TEMP_DIRECTORY.getDirectory ( ) ,
									   UUID.randomUUID ( ).toString ( ) );
		world_folder.mkdirs ( );
		return world_folder;
	}
	
	protected static ArenaWorldGenerator createGenerator ( File world_folder ) {
		ArenaWorldGenerator generator      = ArenaWorldGenerator.createGenerator ( world_folder );
		WorldData           generator_data = generator.getWorldData ( );
		
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 ) ) {
			generator_data.setGeneratorOptions ( "minecraft:air;minecraft:air" );
		} else {
			generator_data.setGeneratorOptions ( "2;0;1" );
		}
		
		generator_data.setGeneratorType ( EnumWorldGenerator.FLAT );
		generator_data.setGenerateStructures ( false );
		generator_data.setInitialized ( true );
		generator_data.setName ( "BattleRoyaleArenaWorld" );
		generator_data.setSpawnX ( 0 );
		generator_data.setSpawnY ( 0 );
		generator_data.setSpawnZ ( 0 );
		// bukkit world loader must have something to load;
		// in case the shape is empty, we set at least one block.
		// if the region folder of the world is empty, bukkit
		// will not load the world, so we have to give bukkit
		// something to load.
		generator.setBlockAtFromLegacyId ( 0 , 0 , 0 , 1 );
		
		return generator;
	}
	
	/** the player who started the session */
	protected final Player                             owner;
	/** world in which the setup takes place */
	protected final World                              world;
	/** true if closed */
	protected       boolean                            closed;
	/** invited configurators */
	protected final Set < Player >                     guest_list = new HashSet <> ( );
	/** stores the current tool of each configurator */
	protected final Map < UUID, BattlefieldSetupTool > tool_map   = new HashMap <> ( );
	/** world modification monitor */
	protected final WorldModificationMonitor           worldMonitor;
	
	/**
	 * @author AdrianSR / 16/01/2022 / 07:48 a. m.
	 */
	protected static class WorldModificationMonitor implements Listener {
		
		protected static final ExecutorService EXECUTOR_SERVICE;
		
		static {
			EXECUTOR_SERVICE = ScheduledExecutorPool.getInstance ( ).getNewWorkStealingPool ( );
		}
		
		protected final BattlefieldSetupSession session;
		// stores the location of the modified regions.
		protected final Set < Location2I >      modified;
		
		public WorldModificationMonitor ( BattlefieldSetupSession session ) {
			this.session  = session;
			this.modified = new HashSet <> ( );
			
			Bukkit.getPluginManager ( ).registerEvents ( this , BattleRoyale.getInstance ( ) );
			
			// region files watcher
			EXECUTOR_SERVICE.execute ( ( ) -> {
				boolean poll = BattleRoyale.getInstance ( ).isEnabled ( ) && session.isActive ( );
				
				try {
					WatchService watch_service = FileSystems.getDefault ( ).newWatchService ( );
					Path directory_path = new File ( session.world.getWorldFolder ( ) ,
													 WorldUtil.REGION_FOLDER_NAME ).toPath ( );
					
					directory_path.register ( watch_service , StandardWatchEventKinds.ENTRY_MODIFY ,
											  StandardWatchEventKinds.ENTRY_DELETE ,
											  StandardWatchEventKinds.ENTRY_CREATE );
					
					while ( poll ) {
						WatchKey key  = watch_service.take ( );
						Path     path = ( Path ) key.watchable ( );
						
						for ( WatchEvent < ? > event : key.pollEvents ( ) ) {
							File eventFile = path.resolve ( ( Path ) event.context ( ) ).toFile ( );
							
							// marking as not modified
							synchronized ( modified ) {
								Iterator < Location2I > iterator = modified.iterator ( );
								
								while ( iterator.hasNext ( ) ) {
									Location2I next = iterator.next ( );
									String fileName = String.format (
											Region.REGION_FILE_NAME_FORMAT , next.getX ( ) , next.getZ ( ) );
									
									if ( fileName.equals ( eventFile.getName ( ) ) ) {
										iterator.remove ( );
									}
								}
							}
						}
						
						poll = key.reset ( );
					}
				} catch ( IOException e ) {
					e.printStackTrace ( );
				} catch ( InterruptedException ex ) {
					poll = false;
				}
			} );
		}
		
		public boolean isWorldUpToDate ( ) {
			return modified.size ( ) == 0;
		}
		
		public boolean isWorldOutOfDate ( ) {
			return modified.size ( ) > 0;
		}
		
		public void reset ( ) {
			modified.clear ( );
		}
		
		public void saveChanges ( ) {
			Bukkit.getScheduler ( ).runTask (
					BattleRoyale.getInstance ( ) , session.world :: save );
		}
		
		protected void mark ( Location location ) {
			if ( Objects.equals ( location.getWorld ( ) , session.world )
					&& ( session.bounds == null || session.bounds.contains ( location ) ) ) {
				Chunk chunk = location.getChunk ( );
				
				modified.add ( new ChunkLocation ( chunk.getX ( ) , chunk.getZ ( ) )
									   .getRegionLocation ( ) );
			}
		}
		
		@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
		public void onModify ( BlockPlaceEvent event ) {
			mark ( event.getBlock ( ).getLocation ( ) );
		}
		
		@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
		public void onModify ( BlockBreakEvent event ) {
			mark ( event.getBlock ( ).getLocation ( ) );
		}
		
		@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
		public void onModify ( org.bukkit.event.block.BlockBurnEvent event ) {
			mark ( event.getBlock ( ).getLocation ( ) );
		}
		
		@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
		public void onModify ( org.bukkit.event.block.SignChangeEvent event ) {
			mark ( event.getBlock ( ).getLocation ( ) );
		}
		
		@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
		public void onModify ( org.bukkit.event.block.BlockExplodeEvent event ) {
			mark ( event.getBlock ( ).getLocation ( ) );
		}
		
		@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
		public void onModify ( org.bukkit.event.block.BlockFromToEvent event ) {
			mark ( event.getBlock ( ).getLocation ( ) );
		}
		
		public void dispose ( ) {
			modified.clear ( );
			HandlerList.unregisterAll ( this );
		}
	}
	
	// result
	protected String                   name;
	protected ZoneBounds               bounds;
	protected Minimap                  minimap;
	protected BattlefieldConfiguration configuration;
	protected BattlefieldSetupResult   result;
	
	// this constructor is for cases where the input is an entire world.
	// it will be a different world in a different folder.
	protected BattlefieldSetupSession ( Player configurator , World input , ZoneBounds bounds ) {
		Validate.notNull ( input , "input world cannot be null" );
		
		this.owner        = configurator;
		this.world        = input;
		this.worldMonitor = new WorldModificationMonitor ( this );
		
		if ( bounds != null ) {
			this.setBounds ( bounds , false , false );
		}
	}
	
	/**
	 * Gets the world on which the setup takes place.
	 * <br>
	 * This is a mock world; it is created with the goal
	 * of simulating the actual battlefield.
	 *
	 * @return the world on which the setup takes place.
	 */
	public World getWorld ( ) {
		return world;
	}
	
	/**
	 * Gets the user who started this session.
	 *
	 * @return the user who started this session.
	 */
	public Player getOwner ( ) {
		return owner;
	}
	
	/**
	 * Gets the guest user list.
	 *
	 * @return the guest user list.
	 */
	public Set < Player > getGuestList ( ) {
		return guest_list;
	}
	
	/**
	 * Gets the name of the battlefield.
	 * <br>
	 * Note that <b>null</b> will be returned
	 * if the name has not been set yet.
	 *
	 * @return the name of the battlefield or <b>null</b>
	 * if not set.
	 */
	public String getName ( ) {
		return name;
	}
	
	/**
	 * Gets the bounds of the battlefield.
	 * <br>
	 * Note that <b>null</b> will be returned
	 * if the bounds has not been set yet.
	 *
	 * @return the bounds of the battlefield or <b>null</b>
	 * if not set.
	 */
	public ZoneBounds getBounds ( ) {
		return bounds;
	}
	
	/**
	 * Gets the minimap of the battlefield.
	 * <br>
	 * Note that <b>null</b> will be returned
	 * if the bounds has not been set yet.
	 *
	 * @return the minimap of the battlefield or <b>null</b>
	 * if bounds are not set.
	 */
	public Minimap getMinimap ( ) {
		return minimap;
	}
	
	/**
	 * Gets the configuration of the battlefield.
	 * <br>
	 * Note that <b>null</b> will be returned if there
	 * is nothing set that must be saved.
	 *
	 * @return the configuration of the battlefield or
	 * <b>null</b> if nothing needs to be saved.
	 */
	public BattlefieldConfiguration getConfiguration ( ) {
		return configuration;
	}
	
	/**
	 * Gets the folder that holds the battlefield.
	 *
	 * @return the folder that holds the battlefield.
	 */
	public File getFolder ( ) {
		if ( StringUtil.isNotBlank ( name ) ) {
			return new File ( EnumDirectory.BATTLEFIELD_DIRECTORY.getDirectory ( ) ,
							  StringUtil.replaceFileCharacters ( name , "-" ) );
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the result of this session.
	 * <br>
	 * Note that <b>null</b> will be returned
	 * if the <b>name</b>, <b>bounds</b>, or <b>minimap</b> are not set.
	 *
	 * @return the result of this session, or <b>null</b> if incomplete.
	 */
	public BattlefieldSetupResult getResult ( ) {
		if ( result == null &&
				( name != null && bounds != null && minimap != null ) ) {
			result = new BattlefieldSetupResultBase ( name , bounds , minimap , configuration );
		}
		
		return result;
	}
	
	/**
	 * Gets whether this session is still active.
	 *
	 * @return whether this session is still active.
	 */
	public boolean isActive ( ) {
		return world != null && !closed;
	}
	
	/**
	 * Gets whether the <b>name</b> of the battlefield
	 * has already been set.
	 *
	 * @return whether the <b>name</b> of the battlefield is set.
	 */
	public boolean isNameSet ( ) {
		return StringUtil.isNotBlank ( name );
	}
	
	/**
	 * Gets whether the <b>bounds</b> of the battlefield
	 * has already been set.
	 *
	 * @return whether the <b>bounds</b> of the battlefield is set.
	 */
	public boolean isBoundsSet ( ) {
		return bounds != null && minimap != null;
	}
	
	/**
	 * Sets the name of the battlefield.
	 * <br>
	 * Note that a new folder matching the new
	 * name will be created.
	 *
	 * @param name the name of the battlefield.
	 */
	public void setName ( String name ) {
		Validate.isTrue ( StringUtil.isNotBlank ( name ) , "name cannot be null/blank" );
		
		final File old_folder = getFolder ( );
		
		this.name   = StringUtil.replaceFileCharacters ( name , "-" );
		this.result = null;
		
		// relocating files
		File folder = getFolder ( );
		
		if ( !Objects.equals ( old_folder , folder ) && old_folder != null && old_folder.exists ( ) ) {
			if ( folder != null && ( folder.exists ( ) || folder.mkdirs ( ) ) ) {
				// we must move everything in the old folder, otherwise
				// some files will be lost, for example, the configuration
				// files of a complex mode. we can avoid that by simply
				// moving everything.
				try {
					FileUtil.copyDirectory ( old_folder , folder );
				} catch ( IOException e ) {
					e.printStackTrace ( );
				}
			}
			
			// finally disposing
			try {
				FileUtil.deleteDirectory ( old_folder );
			} catch ( IOException ex ) {
				ex.printStackTrace ( );
				
				try {
					FileDeleteStrategy.FORCE.delete ( old_folder );
				} catch ( IOException ex_b ) {
					// ignoring
				}
			}
		}
	}
	
	// ----- bus spawns
	
	/**
	 * Adds a new bus spawn.
	 *
	 * @param location the location.
	 * @param yaw the horizontal rotation (yaw).
	 * @param door_point_distance the door point distance.
	 * @param speed the speed.
	 * @param make_relocatable whether to make relocatable the provided location.
	 * @return whether the bus was added or not.
	 */
	public boolean addBusSpawn ( Vector location , float yaw , double door_point_distance , double speed ,
			boolean make_relocatable ) {
		return addBusSpawn ( new BusSpawn ( make_relocatable ? bounds.unproject ( location ) : location ,
											yaw , door_point_distance , speed ) );
	}
	
	/**
	 * Adds a new bus spawn.
	 *
	 * @param location the location.
	 * @param yaw the horizontal rotation (yaw).
	 * @param door_point_distance the door point distance.
	 * @param speed the speed.
	 * @return whether the bus was added or not.
	 */
	public boolean addBusSpawn ( Vector location , float yaw , double door_point_distance , double speed ) {
		return addBusSpawn ( location , yaw , door_point_distance , speed , false );
	}
	
	/**
	 * Adds a new bus spawn.
	 *
	 * @param spawn the spawn.
	 * @return whether the bus was added or not.
	 */
	public boolean addBusSpawn ( BusSpawn spawn ) {
		this.configurationCheck ( );
		
		if ( configuration.addBusSpawn ( spawn ) ) {
			saveConfiguration ( );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes the provided spawn.
	 *
	 * @param spawn the spawn to remove.
	 * @return whether the bus spawn was removed or no.t
	 */
	public boolean removeBusSpawn ( BusSpawn spawn ) {
		if ( configuration != null && configuration.removeBusSpawn ( spawn ) ) {
			saveConfiguration ( );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Clear bus spawns.
	 */
	public void clearBusSpawns ( ) {
		if ( configuration != null ) {
			configuration.getBusSpawns ( ).clear ( );
			
			saveConfiguration ( );
		}
	}
	
	// ----- player spawns
	
	/**
	 * Adds a new player spawn at the specified location.
	 *
	 * @param location the location of the spawn.
	 * @return whether the spawn was added.
	 */
	public boolean addPlayerSpawn ( Vector location ) {
		this.configurationCheck ( );
		
		if ( configuration.addPlayerSpawn ( bounds.unproject ( location ) ) ) {
			saveConfiguration ( );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes the player spawn at the specified location.
	 *
	 * @param location the location of the spawn to remove.
	 * @return whether the spawn was removed.
	 */
	public boolean removePlayerSpawn ( Vector location ) {
		if ( configuration != null && configuration.removePlayerSpawn ( bounds.unproject ( location ) ) ) {
			saveConfiguration ( );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Clear player spawns.
	 */
	public void clearPlayerSpawns ( ) {
		if ( configuration != null ) {
			configuration.getPlayerSpawns ( ).clear ( );
			saveConfiguration ( );
		}
	}
	
	// ----- vehicle spawns
	
	/**
	 * Adds a new vehicle spawn at the specified location.
	 *
	 * @param location the location of the spawn.
	 * @return whether the spawn was added.
	 */
	public boolean addVehicleSpawn ( Vector location ) {
		this.configurationCheck ( );
		
		if ( configuration.addVehicleSpawn ( bounds.unproject ( location ) ) ) {
			saveConfiguration ( );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes the player spawn at the specified location.
	 *
	 * @param location the location of the spawn to remove.
	 * @return whether the spawn was removed.
	 */
	public boolean removeVehicleSpawn ( Vector location ) {
		if ( configuration != null && configuration.removeVehicleSpawn ( bounds.unproject ( location ) ) ) {
			saveConfiguration ( );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Clear player spawns.
	 */
	public void clearVehicleSpawns ( ) {
		if ( configuration != null ) {
			configuration.getVehicleSpawns ( ).clear ( );
			saveConfiguration ( );
		}
	}
	
	// ----- loot chests
	
	/**
	 * Adds a new loot chest spawn at the specified location.
	 *
	 * @param location the location of the spawn.
	 * @return whether the spawn was added.
	 */
	public boolean addLootChest ( Vector location ) {
		this.configurationCheck ( );
		
		if ( configuration.addLootChest ( bounds.unproject ( location ) ) ) {
			saveConfiguration ( );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes the loot chest spawn at the specified location.
	 *
	 * @param location the location of the spawn to remove.
	 * @return whether the spawn was removed.
	 */
	public boolean removeLootChest ( Vector location ) {
		if ( configuration != null && configuration.removeLootChest ( bounds.unproject ( location ) ) ) {
			saveConfiguration ( );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Clear loot chest spawns.
	 */
	public void clearLootChests ( ) {
		if ( configuration != null ) {
			configuration.getLootChests ( ).forEach (
					location -> world.getBlockAt ( bounds.project ( location ).toLocation ( world ) )
							.setType ( UniversalMaterial.AIR.getMaterial ( ) ) );
			configuration.getLootChests ( ).clear ( );
			
			saveConfiguration ( );
		}
	}
	
	// ----- bounds
	
	/**
	 * Sets the bounds of the battlefield.
	 * <br>
	 * Note that the bounds {@link BoundingBox} must be squared, the minimum
	 * point height must be 0, and the maximum point height must match with the maximum height of the world. If that is
	 * not the case, this method will automatically recalculate the bounds.
	 *
	 * @param bounds              the bounds of the battlefield.
	 * @param recalculate_minimap whether to recalculate the minimap. Note that an exception will be thrown if the
	 *                               name of the battlefield is not set or is invalid.
	 * @param minimap_callback   a callback which returns the resulting {@link Minimap}, or <b>null</b> if something
	 *                              went wrong.
	 * @param export_schematic whether to export the corresponding. Note that an exception will be thrown if the name
	 *                            of the battlefield is not set or is invalid.
	 * @param  schematic_callback    a callback which returns <b>true</b> if the schematic was successfully generated
	 *                                  and exported.
	 */
	public void setBounds ( ZoneBounds bounds , boolean recalculate_minimap , Consumer < Minimap > minimap_callback ,
			boolean export_schematic , Consumer < Boolean > schematic_callback ) {
		Validate.notNull ( bounds , "bounds cannot be null" );
		
		this.bounds = bounds;
		this.result = null;
		
		this.recalculateShapeChanges ( recalculate_minimap , minimap_callback , export_schematic , schematic_callback );
	}
	
	public void recalculateShapeChanges ( boolean recalculate_minimap , Consumer < Minimap > minimap_callback ,
			boolean export_schematic , Consumer < Boolean > schematic_callback ) {
		//		// both minimap recalculation and schematic exporting
		//		// requires the world to be saved.
		//		if ( recalculate_minimap || export_schematic ) {
		//			if ( Bukkit.isPrimaryThread ( ) ) {
		//				world.save ( );
		//			} else {
		//				BattleRoyale.getInstance ( ).getLogger ( ).warning (
		//						"setBounds() called from a thread other than server thread. the result is not guaranteed to " +
		//								"be completely accurate, as saving the world from the current thread could result in" +
		//								" a concurrency exception." );
		//			}
		//		}
		
		// recalculating minimap
		if ( recalculate_minimap ) {
			recalculateMinimap ( minimap_callback );
		}
		
		// exporting schematic for the battlefield
		if ( export_schematic ) {
			exportShape ( schematic_callback );
		}
	}
	
	/**
	 * Sets the bounds of the battlefield.
	 * <br>
	 * Note that the bounds {@link BoundingBox} must be squared, the minimum
	 * point height must be 0, and the maximum point height must match with the maximum height of the world. If that is
	 * not the case, this method will automatically recalculate the bounds.
	 *
	 * @param bounds              the bounds of the battlefield.
	 * @param recalculate_minimap whether to recalculate the minimap. Note that an exception will be thrown if the
	 *                               name of the battlefield is not set or is invalid.
	 * @param export_schematic whether to export the corresponding. Note that an exception will be thrown if the name
	 *                            of the battlefield is not set or is invalid.
	 */
	public void setBounds ( ZoneBounds bounds , boolean recalculate_minimap , boolean export_schematic ) {
		setBounds ( bounds , recalculate_minimap , null , export_schematic , null );
	}
	
	/**
	 * Sets the bounds of the battlefield.
	 * <br>
	 * Note that the bounds {@link BoundingBox} must be squared, the minimum
	 * point height must be 0, and the maximum point height must match with the maximum height of the world. If that is
	 * not the case, this method will automatically recalculate the bounds.
	 * <br>
	 * <b>Note that if the <code>minimap_callback</code> is <code>null</code>, the minimap will not be
	 * recalculated.</b>
	 * <br>
	 * <b>Note that if the <code>schematic_callback</code> is <code>null</code>, the schematic will not be
	 * exported.</b>
	 *
	 * @param bounds              the bounds of the battlefield.
	 * @param minimap_callback   a callback which returns the resulting {@link Minimap}, or <b>null</b> if something
	 *                              went wrong.
	 * @param  schematic_callback    a callback which returns <b>true</b> if the schematic was successfully generated
	 *                                  and exported.
	 */
	public void setBounds ( ZoneBounds bounds , Consumer < Minimap > minimap_callback ,
			Consumer < Boolean > schematic_callback ) {
		setBounds ( bounds ,
					minimap_callback != null , minimap_callback ,
					schematic_callback != null , schematic_callback );
	}
	
	/**
	 * Sets the bounds of the battlefield.
	 * <br>
	 * Note that the bounds {@link BoundingBox} must be squared, the minimum
	 * point height must be 0, and the maximum point height must match with the maximum height of the world. If
	 * that is
	 * not the case, this method will automatically recalculate the bounds.
	 *
	 * @param bounds              the bounds of the battlefield.
	 */
	public void setBounds ( ZoneBounds bounds ) {
		setBounds ( bounds , !Objects.equals ( bounds , this.bounds ) , !Objects.equals ( bounds , this.bounds ) );
	}
	
	/**
	 * Recalculates the minimap.
	 *
	 * @param callback a callback which returns the resulting {@link Minimap}, or <b>null</b> if something went
	 *                       wrong.
	 */
	public void recalculateMinimap ( Consumer < Minimap > callback ) {
		Validate.isTrue ( StringUtil.isNotBlank ( name ) , "name never set or invalid" );
		Validate.notNull ( bounds , "bounds never set" );
		
		// saving world changes if not saved yet.
		if ( worldMonitor.isWorldOutOfDate ( ) ) {
			worldMonitor.saveChanges ( );
		}
		
		// blocks until done so we must keep it asynchronous.
		SHAPE_EXECUTOR_SERVICE.execute ( ( ) -> {
			// must wait until world is done saving
			while ( worldMonitor.isWorldOutOfDate ( ) ) {
				try {
					Thread.sleep ( 50 );
				} catch ( InterruptedException e ) {
					e.printStackTrace ( );
				}
				
				System.out.println ( ">>> left to be saved: " + worldMonitor.modified.size ( ) );
			}
			
			// then generating
			MinimapGenerator generator = new MinimapGenerator ( world );
			
			generator.generate ( bounds );
			generator.close ( );
			
			this.minimap = generator.getResult ( );
			this.result  = null;
			
			// exporting
			File folder = getFolder ( );
			
			if ( folder != null ) {
				if ( !folder.exists ( ) ) {
					folder.mkdirs ( );
				}
				
				try {
					this.minimap.save ( new File ( folder , Constants.BATTLEFIELD_MINIMAP_FILE_NAME ) );
					
					// callback
					if ( callback != null ) {
						callback.accept ( minimap );
					}
				} catch ( IOException ex ) {
					ConsoleUtil.sendPluginMessage (
							ChatColor.RED , "Something went wrong when generating minimap: " ,
							BattleRoyale.getInstance ( ) );
					ex.printStackTrace ( );
					
					// callback
					if ( callback != null ) {
						callback.accept ( null );
					}
				}
			}
		} );
	}
	
	/**
	 * Recalculates the minimap.
	 * <br>
	 * @see #recalculateMinimap(Consumer)
	 */
	public void recalculateMinimap ( ) {
		recalculateMinimap ( null );
	}
	
	/**
	 * Exports the shape.
	 *
	 * @param callback a callback which returns <b>true</b> if the schematic was successfully generated and
	 *                       exported.
	 */
	public void exportShape ( Consumer < Boolean > callback ) {
		Validate.isTrue ( StringUtil.isNotBlank ( name ) , "name never set or invalid" );
		Validate.notNull ( bounds , "bounds never set" );
		
		File folder = getFolder ( );
		
		if ( folder != null ) {
			if ( !folder.exists ( ) ) {
				folder.mkdirs ( );
			}
			
			// removing old shape
			for ( File file : Objects.requireNonNull (
					folder.listFiles ( FileExtensionFilter.of ( BattlefieldShapePart.PART_FILE_EXTENSION ) ) ) ) {
				try {
					Files.delete ( file.toPath ( ) );
				} catch ( IOException e ) {
					e.printStackTrace ( );
					
					try {
						FileUtil.forceDelete ( file );
					} catch ( IOException ex_b ) {
						ex_b.printStackTrace ( );
					}
				}
			}
			
			// saving world changes if not saved yet.
			if ( worldMonitor.isWorldOutOfDate ( ) ) {
				worldMonitor.saveChanges ( );
			}
			
			// blocks until done so we must keep it asynchronous.
			SHAPE_EXECUTOR_SERVICE.execute ( ( ) -> {
				// must wait until world is done saving
				while ( worldMonitor.isWorldOutOfDate ( ) ) {
					try {
						Thread.sleep ( 50 );
					} catch ( InterruptedException e ) {
						e.printStackTrace ( );
					}
					
					System.out.println ( ">>> left to be saved: " + worldMonitor.modified.size ( ) );
				}
				
				try {
					SchematicUtil.generateBattlefieldShape ( world , bounds.toBoundingBox ( ) , folder );
					
					// callback
					if ( callback != null ) {
						callback.accept ( true );
					}
				} catch ( Exception ex ) {
					ConsoleUtil.sendPluginMessage (
							ChatColor.RED , "Something went wrong when exporting battlefield schematic: " ,
							BattleRoyale.getInstance ( ) );
					ex.printStackTrace ( );
					
					// callback
					if ( callback != null ) {
						callback.accept ( false );
					}
				}
			} );
		}
	}
	
	/**
	 * Exports the shape.
	 *
	 * @see #exportShape(Consumer)
	 */
	public void exportShape ( ) {
		exportShape ( null );
	}
	
	// ----- border resize succession
	
	/**
	 * Sets the border resize succession.
	 *
	 * @param resize_succession the border resize succession.
	 */
	public void setBorderResizeSuccession ( BattlefieldBorderSuccession resize_succession ) {
		this.configurationCheck ( );
		this.configuration.setBorderResizeSuccession ( resize_succession );
		this.saveConfiguration ( );
	}
	
	// ----- air supply
	
	/**
	 * Sets the maximum number of air supplies that
	 * can be dropped during the arena.
	 *
	 * @param air_supply_max maximum number of air supplies that can be dropped.
	 */
	public void setAirSupplyMax ( int air_supply_max ) {
		this.configurationCheck ( );
		this.configuration.setAirSupplyMax ( air_supply_max );
		this.saveConfiguration ( );
	}
	
	/**
	 * Sets the minimum number of air supplies that
	 * can be dropped during the arena.
	 *
	 * @param air_supply_min minimum number of air supplies that can be dropped.
	 */
	public void setAirSupplyMin ( int air_supply_min ) {
		this.configurationCheck ( );
		this.configuration.setAirSupplyMin ( air_supply_min );
		this.saveConfiguration ( );
	}
	
	// ----- bombing zone
	
	/**
	 * Sets the maximum number of bombing zones that
	 * can be generated each border resize-point.
	 *
	 * @param bombing_zone_max
	 * maximum number of bombing zones that can be generated
	 * each border resize-point.
	 */
	public void setBombingZoneMax ( int bombing_zone_max ) {
		this.configurationCheck ( );
		this.configuration.setBombingZoneMax ( bombing_zone_max );
		this.saveConfiguration ( );
	}
	
	/**
	 * Sets the minimum number of bombing zones that
	 * can be generated each border resize-point.
	 *
	 * @param bombing_zone_min
	 * minimum number of bombing zones that can be generated
	 * each border resize-point.
	 */
	public void setBombingZoneMin ( int bombing_zone_min ) {
		this.configurationCheck ( );
		this.configuration.setBombingZoneMin ( bombing_zone_min );
		this.saveConfiguration ( );
	}
	
	// ----- vehicles configuration
	
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
		this.configurationCheck ( );
		this.configuration.setVehiclesConfiguration ( configuration_name );
		this.saveConfiguration ( );
	}
	
	// ----- loot configuration
	
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
		this.configurationCheck ( );
		this.configuration.setLootConfiguration ( loot_configuration_name );
		this.saveConfiguration ( );
	}
	
	/**
	 * Introduces the provided player/user into the session.
	 * <br>
	 * Note that only players with the permission can be introduced.
	 *
	 * @param player the player to introduce.
	 * @return whether the player was introduced or not.
	 */
	public boolean introduce ( org.bukkit.entity.Player player ) {
		if ( !permissionCheck ( player ) ) {
			return false;
		}
		
		player.getInventory ( ).clear ( );
		
		player.setGameMode ( GameMode.CREATIVE );
		player.setFlying ( true );
		player.teleport ( new Location ( world , 0.0D ,
										 world.getHighestBlockYAt ( 0 , 0 ) + 1.0D , 0.0D ) );
		
		if ( bounds != null && minimap != null ) {
			player.getInventory ( ).addItem ( ItemStackUtil.createViewItemStack (
					MiniMapUtil.createView ( new MinimapRendererSetupSession ( this ) , world ) ) );
		}
		
		player.updateInventory ( );
		return true;
	}
	
	/**
	 * Gets whether the provided player/user is busy using a tool.
	 *
	 * @param player the player to check.
	 * @return whether the provided player/user is busy using a tool.
	 */
	public boolean isBusy ( org.bukkit.entity.Player player ) {
		return getCurrentTool ( player ) != null;
	}
	
	/**
	 * Gets the tool the provided player/user is currently using.
	 *
	 * @param player the player to get.
	 * @return the tool the provided player/user is currently using.
	 */
	public BattlefieldSetupTool getCurrentTool ( org.bukkit.entity.Player player ) {
		BattlefieldSetupTool tool = tool_map.get ( player.getUniqueId ( ) );
		
		if ( tool != null && tool.isActive ( ) ) {
			return tool;
		} else {
			tool_map.remove ( player.getUniqueId ( ) );
			return null;
		}
	}
	
	/**
	 * Populates the tool for use.
	 *
	 * @param player the player that will use the tool.
	 * @param tool the tool to populate.
	 * @return whether the tool was populated or not.
	 * @throws IllegalStateException if the provided player is currently using a tool that is <b>modal</b>.
	 */
	public boolean populateTool ( org.bukkit.entity.Player player , EnumBattleMapSetupTool tool )
			throws IllegalStateException {
		return populateTool ( player , tool.getNewInstance ( this , Player.getPlayer ( player ) ) );
	}
	
	/**
	 * Populates the tool for use.
	 *
	 * @param player the player that will use the tool.
	 * @param tool the tool to populate.
	 * @return whether the tool was populated or not.
	 * @throws IllegalStateException if the provided player is currently using a tool that is <b>modal</b>.
	 */
	public boolean populateTool ( org.bukkit.entity.Player player , BattlefieldSetupTool tool ) {
		Validate.notNull ( tool , "tool cannot be null" );
		
		// disposing current if possible.
		BattlefieldSetupTool current = getCurrentTool ( player );
		
		if ( current != null ) {
			if ( current.isModal ( ) ) {
				throw new IllegalStateException (
						"cannot populate tool while player is using a modal tool" );
			} else {
				current.dispose ( );
			}
		}
		
		// then populating
		if ( permissionCheck ( player ) ) {
			tool.initialize ( );
			tool_map.put ( player.getUniqueId ( ) , tool );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Disposes the tool the provided player is currently using.
	 *
	 * @param player the player that is using the tool to dispose.
	 */
	public void disposeTool ( org.bukkit.entity.Player player ) {
		BattlefieldSetupTool tool = getCurrentTool ( player );
		
		if ( tool != null ) {
			tool.dispose ( );
			
			tool_map.remove ( player.getUniqueId ( ) );
		}
	}
	
	/**
	 * Closes this session.
	 */
	protected void close ( ) {
		closed = true;
		
		// disposing world monitor
		worldMonitor.dispose ( );
		
		// unloading world
		world.getPlayers ( ).forEach ( player -> player.kickPlayer ( "Unloading world" ) );
		
		Bukkit.unloadWorld ( world , false );
		
		// disposing tools
		tool_map.values ( ).stream ( ).filter ( BattlefieldSetupTool :: isActive )
				.forEach ( BattlefieldSetupTool :: dispose );
	}
	
	// ----- utils
	
	protected void moveFileToDirectory ( File file , File directory ) {
		if ( file.exists ( ) ) {
			try {
				FileUtil.moveFileToDirectory ( file , directory , true );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	protected boolean permissionCheck ( org.bukkit.entity.Player player ) {
		return Objects.equals ( player.getUniqueId ( ) , owner.getUniqueId ( ) )
				|| BattlefieldSetupHandler.getInstance ( ).invite ( Player.getPlayer ( player ) , this );
	}
	
	protected void configurationCheck ( ) {
		if ( configuration == null ) {
			this.configuration = new BattlefieldConfiguration ( );
		}
	}
	
	protected void saveConfiguration ( ) {
		File folder = getFolder ( );
		
		if ( folder != null && configuration != null ) {
			try {
				configuration.save ( new File (
						folder , Constants.BATTLEFIELD_CONFIGURATION_FILE_NAME ) );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
}