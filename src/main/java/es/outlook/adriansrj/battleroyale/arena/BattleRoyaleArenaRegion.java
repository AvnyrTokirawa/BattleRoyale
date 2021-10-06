package es.outlook.adriansrj.battleroyale.arena;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShape;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShapePart;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.exception.WorldRegionLimitReached;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.battleroyale.world.arena.ArenaWorldGenerator;
import es.outlook.adriansrj.battleroyale.world.region.Region;
import es.outlook.adriansrj.core.util.world.WorldUtil;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

/**
 * @author AdrianSR / 04/09/2021 / 10:51 p. m.
 */
public class BattleRoyaleArenaRegion {
	
	// this will lock the shape method as it would
	// consume all the heap space if multiple arenas
	// shape at the same time.
	protected static final ReentrantLock SHAPE_LOCK = new ReentrantLock ( );
	
	// this executor will provide as with a thread
	// which we can lock to prevent lacking of heap space.
	protected static final ExecutorService EXECUTOR_SERVICE;
	
	static {
		EXECUTOR_SERVICE = Executors.newSingleThreadExecutor ( );
	}
	
	/**
	 * Class responsible for assigning a region within a world to an arena.
	 *
	 * @author AdrianSR / 03/09/2021 / 11:16 a. m.
	 */
	protected static class RegionFileAssigner {
		
		protected static final Map < File, Set < Location2I > > RESERVED_MAP = new ConcurrentHashMap <> ( );
		
		// the -32 <-> 32 range for 1024 regions
		protected static final int RANGE_START = -16;
		protected static final int RANGE_END   = 16;
		
		private RegionFileAssigner ( ) {
			// singleton
		}
		
		protected static synchronized Location2I assign ( BattleRoyaleArenaRegion region ) {
			File world_folder  = region.arena.world.getWorldFolder ( );
			int  size          = region.arena.battlefield.getSize ( );
			int  required_size = ( int ) Math.round ( ( size / 16.0D ) / 32.0D ); // measured in regions
			
			Set < Location2I > reserved = RESERVED_MAP.computeIfAbsent ( world_folder , k ->
					Collections.synchronizedSet ( new HashSet <> ( ) ) );
			
			for ( int x = RANGE_START ; x <= RANGE_END ; x++ ) {
				outer_z:
				for ( int z = RANGE_START ; z <= RANGE_END ; z++ ) {
					Location2I location = new Location2I ( x , z );
					
					if ( reserved.contains ( location ) || getRegionFile ( world_folder , location ).exists ( ) ) {
						reserved.add ( location ); // already existing region
						continue;
					}
					
					for ( Location2I other : reserved ) {
						if ( location.distance ( other ) < required_size ) {
							// not enough space, as there is another region
							// being used that is too close.
							continue outer_z;
						}
					}
					
					// reserving
					reserved.add ( location );
					return location;
				}
			}
			
			return null;
		}
		
		protected static synchronized File getRegionFile ( File world_folder , int region_x , int region_z ) {
			return new File ( new File ( world_folder , WorldUtil.REGION_FOLDER_NAME ) ,
							  String.format ( Region.REGION_FILE_NAME_FORMAT , region_x , region_z ) );
		}
		
		protected static synchronized File getRegionFile ( File world_folder , Location2I location ) {
			return getRegionFile ( world_folder , location.getX ( ) , location.getZ ( ) );
		}
		
		protected static synchronized void clear ( File world_Folder ) {
			Set < Location2I > reserved = RESERVED_MAP.get ( world_Folder );
			
			if ( reserved != null ) {
				for ( Location2I location : reserved ) {
					File file = getRegionFile ( world_Folder , location );
					
					if ( file.exists ( ) ) {
						try {
							FileDeleteStrategy.FORCE.delete ( file );
						} catch ( IOException e ) {
							e.printStackTrace ( );
						}
					}
				}
				
				reserved.clear ( );
			}
			
			RESERVED_MAP.remove ( world_Folder );
		}
	}
	
	protected final BattleRoyaleArena arena;
	
	// current region. changes each time
	// the arena is restarted. bounds are calculated
	// based on this location, but that doesn't mean
	// that the arena will use only one region.
	protected Location2I          base_location;
	protected ZoneBounds          bounds;
	// temporal generator, used when reshaping
	protected ArenaWorldGenerator generator;
	
	public BattleRoyaleArenaRegion ( BattleRoyaleArena arena ) {
		this.arena = arena;
		this.reassignRegion ( );
	}
	
	protected void reassignRegion ( ) throws WorldRegionLimitReached {
		Validate.isTrue ( Bukkit.isPrimaryThread ( ) , "must run on server thread" );
		
		// players within the region will be kicked, unless
		// they are moved before calling this method.
		disposeCurrentRegion ( null );
		
		// bounds are calculated based on
		// the size of the battlefield,
		// and then relocated within the world
		// of the arena.
		if ( ( this.base_location = RegionFileAssigner.assign ( this ) ) == null ) {
			throw new WorldRegionLimitReached ( "world needs to be restarted" );
		}
		
		// recalculation bounds
		int region_min_chunk_x       = base_location.getX ( ) << 5;
		int region_min_chunk_z       = base_location.getZ ( ) << 5;
		int region_max_chunk_x       = ( ( base_location.getX ( ) + 1 ) << 5 ) - 1;
		int region_max_chunk_z       = ( ( base_location.getZ ( ) + 1 ) << 5 ) - 1;
		int region_center_chunk_x    = ( region_min_chunk_x + region_max_chunk_x ) >> 1;
		int region_center_chunk_z    = ( region_min_chunk_z + region_max_chunk_z ) >> 1;
		int center_chunk_min_block_x = region_center_chunk_x << 4;
		int center_chunk_min_block_z = region_center_chunk_z << 4;
		int size_half                = arena.battlefield.getSizeExact ( ) >> 1;
		
		this.bounds = new ZoneBounds ( center_chunk_min_block_x - size_half ,
									   center_chunk_min_block_z - size_half ,
									   center_chunk_min_block_x + size_half ,
									   center_chunk_min_block_z + size_half );
	}
	
	/**
	 * Assign and recalculates a new region.
	 *
	 * @see #reassignRegion()
	 * @see #shape(Runnable)
	 */
	protected void recalculate ( ) {
		// reassigning region
		reassignRegion ( );
		
		// then reshaping
		shape ( null );
	}
	
	/**
	 * Inserts the shape of the battlefield of the arena in the current region.
	 * <br>
	 * <b>Note that the players within this region will be kicked, unless the
	 * are moved before calling this method.</b>
	 *
	 * @param callback callback to run when shape process is finished.
	 */
	protected synchronized void shape ( Runnable callback ) {
		Validate.isTrue ( Bukkit.isPrimaryThread ( ) , "must run on server thread" );
		Validate.notNull ( base_location , "region never assign" );
		
		EXECUTOR_SERVICE.execute ( ( ) -> {
			SHAPE_LOCK.lock ( );
			
			try {
				// inserting shape
				Location2I         bounds_min           = bounds.getMinimum ( );
				BattlefieldShape   shape                = arena.battlefield.getShape ( );
				Set < Location2I > part_block_locations = new HashSet <> ( );
				int                index                = 0;
				
				generator = ArenaWorldGenerator.createGenerator ( arena.world.getWorldFolder ( ) );
				
				for ( BattlefieldShapePart part : shape.getParts ( ).values ( ) ) {
					Location2I part_location = part.getLocation ( );
					int        part_x        = part_location.getX ( );
					int        part_z        = part_location.getZ ( );
					int        part_block_x  = bounds_min.getX ( ) + ( part_x << 7 );
					int        part_block_z  = bounds_min.getZ ( ) + ( part_z << 7 );
					
					// we store the block location at which
					// the part is being inserted, so we can
					// keep track of the region and save it later.
					part_block_locations.add ( new Location2I ( part_block_x , part_block_z ) );
					
					try {
						Clipboard contents = part.loadContent ( arena.battlefield.getFolder ( ) );
						
						generator.insert ( contents ,
										   new Vector ( part_block_x , 0.0D , part_block_z ) ,
										   true );
						
						// disposing, we need that heap space.
						contents = null;
						
						// printing progress
						double progress = ( 100.0D * ( ( double ) ( index + 1 ) / shape.getParts ( ).size ( ) ) );
						
						BattleRoyale.getInstance ( ).getLogger ( ).info (
								"Preparing battlefield ("
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
				
				try {
					// saving region files with the
					// shape of the battlefield.
					Set < Region > saved = new HashSet <> ( );
					
					for ( Location2I loc : part_block_locations ) {
						Region region = generator.getRegion ( new Location2I (
								( loc.getX ( ) >> 4 ) >> 5 ,
								( loc.getZ ( ) >> 4 ) >> 5 ) );
						
						if ( saved.add ( region ) ) {
							region.save ( getRegionsFolder ( ) );
						}
					}
					
					// progress completed
					BattleRoyale.getInstance ( ).getLogger ( ).info ( "Battlefield prepared successfully." );
				} catch ( Exception ex ) {
					generator   = null;
					arena.state = EnumArenaState.STOPPED;
					
					throw new IllegalStateException ( "couldn't save region file: " , ex );
				}
				
				// disposing, we need that heap space.
				generator = null;
				
				// callback
				if ( callback != null ) {
					callback.run ( );
				}
			} finally {
				SHAPE_LOCK.unlock ( );
			}
		} );
	}
	
	/**
	 * Disposes the current region.
	 * <br>
	 * <b>Note that there should not be players within the region,
	 * otherwise this will fail unloading the region.</b>
	 *
	 * @param player_processor the function responsible for processing the players within the region. if the result
	 * is <b>false</b>, then the player will be <b>kicked</b>.
	 */
	protected synchronized void disposeCurrentRegion ( Predicate < Player > player_processor ) {
		Validate.isTrue ( Bukkit.isPrimaryThread ( ) , "must run on server thread" );
		
		if ( base_location == null ) {
			return; // region never assign
		}
		
		// unloading chunks
		int region_min_chunk_x = base_location.getX ( ) << 5;
		int region_min_chunk_z = base_location.getZ ( ) << 5;
		int region_max_chunk_x = ( ( base_location.getX ( ) + 1 ) << 5 ) - 1;
		int region_max_chunk_z = ( ( base_location.getZ ( ) + 1 ) << 5 ) - 1;
		
		// processing players within
		for ( org.bukkit.entity.Player player : arena.world.getPlayers ( ) ) {
			if ( contains ( player.getLocation ( ) ) ) {
				Boolean result = player_processor != null ? player_processor.test ( player ) : null;
				
				if ( result == null || !result ) {
					player.kickPlayer ( ChatColor.RED + "Disposing region" );
				}
			}
		}
		
		// removing entities
		for ( Entity entity : arena.world.getEntities ( ) ) {
			if ( !( entity instanceof org.bukkit.entity.Player ) && contains ( entity.getLocation ( ) ) ) {
				entity.remove ( );
			}
		}
		
		for ( int x = region_min_chunk_x ; x <= region_max_chunk_x ; x++ ) {
			for ( int z = region_min_chunk_z ; z <= region_max_chunk_z ; z++ ) {
				if ( !( arena.world.unloadChunk ( x , z , false ) ) ) {
					arena.world.unloadChunkRequest ( x , z );
				}
			}
		}
	}
	
	/**
	 * Checks whether the provided location is within this region.
	 *
	 * @param location the location to check.
	 * @return whether the provided location is within this region or not.
	 */
	protected boolean contains ( Location location ) {
		int chunk_x  = location.getBlockX ( ) >> 4;
		int chunk_z  = location.getBlockZ ( ) >> 4;
		int region_x = chunk_x >> 5;
		int region_z = chunk_z >> 5;
		
		return region_x == this.base_location.getX ( ) && region_z == this.base_location.getZ ( );
	}
	
	/**
	 * Gets the <b>region</b> folder within the
	 * world folder of the arena.
	 *
	 * @return region folder of the world of the arena.
	 */
	protected File getRegionsFolder ( ) {
		return new File ( arena.getWorld ( ).getWorldFolder ( ) , WorldUtil.REGION_FOLDER_NAME );
	}
}