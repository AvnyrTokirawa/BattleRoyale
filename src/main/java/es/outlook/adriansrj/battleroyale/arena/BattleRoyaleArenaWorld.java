package es.outlook.adriansrj.battleroyale.arena;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShape;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShapePart;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.enums.EnumWorldGenerator;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.schedule.ScheduledExecutorPool;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.battleroyale.util.file.FileUtil;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.battleroyale.world.arena.ArenaWorldGenerator;
import es.outlook.adriansrj.battleroyale.world.data.WorldData;
import es.outlook.adriansrj.core.util.server.Version;
import es.outlook.adriansrj.core.util.world.GameRuleDisableDaylightCycle;
import es.outlook.adriansrj.core.util.world.GameRuleType;
import es.outlook.adriansrj.core.util.world.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author AdrianSR / 04/09/2021 / 10:51 p. m.
 */
public class BattleRoyaleArenaWorld {
	
	/**
	 * The name of the folder that holds the world
	 * that is already prepared and is to be copied.
	 */
	public static final String PREPARED_WORLD_FOLDER_NAME = "prepared";
	
	// this will lock the prepare method as it would
	// consume all the heap space if multiple arenas
	// prepare at the same time.
	protected static final ReentrantLock PREPARE_LOCK = new ReentrantLock ( );
	
	// this executor will provide as with a thread
	// which we can lock to prevent lacking of heap space.
	protected static final ExecutorService EXECUTOR_SERVICE;
	
	static {
		EXECUTOR_SERVICE = ScheduledExecutorPool.getInstance ( ).getNewSingleThreadScheduledExecutor ( );
		// we will try by using a work stealing pool.
		// if this result in problems, then we will have
		// to use a single thread executor instead.
		//		EXECUTOR_SERVICE = ScheduledExecutorPool.getInstance ( ).getWorkStealingPool ( );
	}
	
	protected final BattleRoyaleArena   arena;
	protected final ZoneBounds          bounds;
	// current world
	protected       World               world;
	protected       boolean             preparing;
	// temporal generator, used when reshaping
	protected       ArenaWorldGenerator generator;
	
	public BattleRoyaleArenaWorld ( BattleRoyaleArena arena ) {
		this.arena = arena;
		
		// calculating bounds
		int size_half = arena.battlefield.getSize ( ) >> 1;
		this.bounds = new ZoneBounds ( -size_half , -size_half , size_half , size_half );
	}
	
	public World getWorld ( ) {
		return world;
	}
	
	public synchronized void restart ( ) {
		this.disposeWorld ( ( ) -> {
			if ( !isPrepared ( ) && !isPreparing ( ) ) {
				this.prepare ( null );
			}
		} );
	}
	
	public synchronized void stop ( ) {
		this.disposeWorld ( null );
	}
	
	public boolean isPreparing ( ) {
		return preparing;
	}
	
	protected synchronized boolean isPrepared ( ) {
		return world != null;
	}
	
	/**
	 * Inserts the shape of the battlefield of the arena in the current region.
	 * <br>
	 * <b>Note that the players within this region will be kicked, unless the
	 * are moved before calling this method.</b>
	 *
	 * @param callback callback to run when shape process is finished.
	 */
	protected synchronized void prepare ( Runnable callback ) {
		Validate.isTrue ( world == null , "already prepared" );
		Validate.isTrue ( !preparing , "already preparing" );
		
		File prepared_folder = new File ( arena.getBattlefield ( ).getFolder ( ) , PREPARED_WORLD_FOLDER_NAME );
		
		// preparing in case is not prepared
		preparing = true;
		
		if ( !prepared_folder.exists ( ) || !WorldUtil.worldFolderCheck ( prepared_folder ) ) {
			prepare0 ( ( ) -> load ( ( ) -> {
				preparing = false;
				
				// callback
				if ( callback != null ) {
					callback.run ( );
				}
			} ) );
		} else {
			load ( ( ) -> {
				preparing = false;
				
				// callback
				if ( callback != null ) {
					callback.run ( );
				}
			} );
		}
	}
	
	/**
	 * Generates a world and inserts the shape of
	 * the battlefield in it.
	 *
	 * @param callback the callback to execute once the process is done.
	 */
	protected synchronized void prepare0 ( Runnable callback ) {
		File prepared_folder = new File ( arena.getBattlefield ( ).getFolder ( ) , PREPARED_WORLD_FOLDER_NAME );
		
		try {
			FileUtil.deleteDirectory ( prepared_folder );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		}
		
		prepared_folder.mkdirs ( );
		
		EXECUTOR_SERVICE.execute ( ( ) -> {
			PREPARE_LOCK.lock ( );
			
			try {
				preparing = true;
				
				// inserting shape
				Location2I       bounds_min = bounds.getMinimum ( );
				BattlefieldShape shape      = arena.battlefield.getShape ( );
				int              index      = 0;
				
				generator = ArenaWorldGenerator.createGenerator ( prepared_folder );
				WorldData generator_data = generator.getWorldData ( );
				
				if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 ) ) {
					generator_data.setGeneratorOptions ( "minecraft:air;minecraft:air" );
				} else {
					generator_data.setGeneratorOptions ( "2;0;1" );
				}
				
				generator_data.setGeneratorType ( EnumWorldGenerator.FLAT );
				generator_data.setGenerateStructures ( false );
				generator_data.setInitialized ( true );
				generator_data.setName ( prepared_folder.getName ( ) );
				generator_data.setSpawnX ( 0 );
				generator_data.setSpawnY ( 0 );
				generator_data.setSpawnZ ( 0 );
				// bukkit world loader must have something to load;
				// in case the shape is empty, we set at least one block.
				// if the region folder of the world is empty, bukkit
				// will not load the world, so we have to give bukkit
				// something to load.
				generator.setBlockAtFromLegacyId ( 0 , 0 , 0 , 1 );
				
				for ( BattlefieldShapePart part : shape.getParts ( ).values ( ) ) {
					Location2I part_location = part.getLocation ( );
					int        part_x        = part_location.getX ( );
					int        part_z        = part_location.getZ ( );
					int        part_block_x  = bounds_min.getX ( ) + ( part_x << 7 );
					int        part_block_z  = bounds_min.getZ ( ) + ( part_z << 7 );
					
					try {
						Clipboard contents = part.loadContent ( arena.battlefield.getFolder ( ) );
						
						try {
							BattleRoyale.getInstance ( ).getLogger ( ).info (
									"Inserting part (" + part_x + ", " + part_z + "). Size: "
											+ Files.size ( new File ( arena.battlefield.getFolder ( )
											, part.getFileName ( ) ).toPath ( ) ) );
						} catch ( IOException e ) {
							e.printStackTrace ( );
						}
						
						generator.insert ( contents ,
										   new Vector ( part_block_x , 0.0D , part_block_z ) ,
										   true );
						
						// disposing, we need that heap space.
						contents = null;
						
						// printing progress
						double progress = ( 100.0D * ( ( double ) ( index + 1 ) / shape.getParts ( ).size ( ) ) );
						
						BattleRoyale.getInstance ( ).getLogger ( ).info (
								"Preparing battlefield world ("
										+ arena.battlefield.getName ( ) + ")... "
										+ String.format ( "%.2f" , progress ) + "%" );
					} catch ( FileNotFoundException ex ) {
						// we want to be able to load incomplete
						// battlefield shapes as we don't want
						// to lose a whole battlefield just for a missing part.
						BattleRoyale.getInstance ( ).getLogger ( ).warning (
								"Missing battlefield shape part file (" + part_x + ", " + part_z + ")" );
					}
					
					// next
					index++;
				}
				
				// saving and disposing, we need that heap space.
				generator.save ( );
				generator = null;
				
				// callback
				if ( callback != null ) {
					callback.run ( );
				}
			} finally {
				PREPARE_LOCK.unlock ( );
			}
		} );
	}
	
	/**
	 * Creates a copy of the prepared world and loads it.
	 *
	 * @param callback the callback to execute once the world is loaded.
	 */
	protected synchronized void load ( Runnable callback ) {
		File prepared_folder = new File ( arena.getBattlefield ( ).getFolder ( ) , PREPARED_WORLD_FOLDER_NAME );
		File world_folder = new File ( EnumDirectory.BATTLEFIELD_TEMP_DIRECTORY.getDirectory ( ) ,
									   UUID.randomUUID ( ).toString ( ) );
		
		// copying into folder of the temp copy
		world_folder.mkdirs ( );
		
		try {
			FileUtil.copyDirectory ( prepared_folder , world_folder );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		}
		
		// then loading
		if ( Bukkit.isPrimaryThread ( ) ) {
			world = loadWorld ( world_folder );
			
			// callback
			if ( callback != null ) {
				callback.run ( );
			}
		} else {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> {
				world = loadWorld ( world_folder );
				
				// callback
				if ( callback != null ) {
					callback.run ( );
				}
			} );
		}
	}
	
	// ------ utils
	
	/**
	 * Gets the <b>region</b> folder within the
	 * world folder of the arena.
	 *
	 * @return region folder of the world of the arena.
	 */
	protected File getRegionsFolder ( ) {
		return new File ( arena.getWorld ( ).getWorldFolder ( ) , WorldUtil.REGION_FOLDER_NAME );
	}
	
	/**
	 * Disposes the {@link #world}.
	 * <br>
	 * <b>Note that the players in the world will be kicked, unless the
	 * are moved before calling this method.</b>
	 *
	 * @param callback callback to execute once the world is unloaded.
	 */
	protected void disposeWorld ( Runnable callback ) {
		if ( world != null ) {
			// must create a final reference as the world field
			// will be set to null before even unloading
			// the world as we're switching thread.
			final World final_reference = this.world;
			
			if ( Bukkit.isPrimaryThread ( ) ) {
				unloadAndRemove ( final_reference );
				world = null;
				
				// callback
				if ( callback != null ) {
					callback.run ( );
				}
			} else {
				Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> {
					unloadAndRemove ( final_reference );
					world = null;
					
					// callback
					if ( callback != null ) {
						callback.run ( );
					}
				} );
			}
		} else {
			// callback
			if ( callback != null ) {
				callback.run ( );
			}
		}
	}
	
	/**
	 * Unloads and removes the provided world.
	 * <br>
	 * <b>Note that the players in the world will be kicked, unless the
	 * are moved before calling this method.</b>
	 *
	 * @param world the world to unload and remove.
	 */
	private void unloadAndRemove ( World world ) {
		Validate.isTrue ( Bukkit.isPrimaryThread ( ) , "must run on server thread" );
		
		// kicking players
		for ( org.bukkit.entity.Player player : world.getPlayers ( ) ) {
			player.kickPlayer ( ChatColor.RED + "Unloading this world" );
		}
		
		// then proceeding
		Bukkit.unloadWorld ( world , false );
		
		try {
			FileUtil.deleteDirectory ( world.getWorldFolder ( ) );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		}
	}
	
	/**
	 * Loads the world in case is not already loaded.
	 *
	 * @param world_folder the folder of the world to load.
	 * @return the loaded world.
	 */
	protected World loadWorld ( File world_folder ) {
		World world = Bukkit.getWorld ( world_folder.getName ( ) );
		
		if ( world == null && ( world = Bukkit.getWorld ( world_folder.getAbsolutePath ( ) ) ) == null ) {
			world = es.outlook.adriansrj.battleroyale.util.world.WorldUtil.loadWorldEmpty ( world_folder );
		}
		
		new GameRuleDisableDaylightCycle ( ).apply ( world );
		
		if ( EnumMainConfiguration.ADVANCEMENTS_ANNOUNCEMENT_DISABLE.getAsBoolean ( ) ) {
			GameRuleType.ANNOUNCE_ADVANCEMENTS.apply ( world , false );
		}
		
		return world;
	}
}