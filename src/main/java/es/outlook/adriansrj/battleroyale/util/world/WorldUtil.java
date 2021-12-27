package es.outlook.adriansrj.battleroyale.util.world;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.file.FileUtil;
import es.outlook.adriansrj.battleroyale.world.chunk.EmptyChunkGenerator;
import es.outlook.adriansrj.core.util.world.GameRuleType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * @author AdrianSR / 03/09/2021 / 02:11 p. m.
 */
public class WorldUtil extends es.outlook.adriansrj.core.util.world.WorldUtil {
	
	public static final String PLAYER_BLOCK_METADATA_KEY = UUID.randomUUID ( ).toString ( );
	
	/**
	 * Useful method for creating a copy of the region
	 * files of a certain world.
	 * <br>
	 * +1.13 world region files are accessed asynchronously,
	 * resulting in concurrent modification exceptions while
	 * trying to read the files. This method will create a copy
	 * of each region file whenever is possible (whenever the
	 * file is not locked anymore); <b>so it will block the
	 * current thread until the copy-process is done.</b>
	 *
	 * @param region_folder the source region folder.
	 * @param destiny_folder the destiny region folder.
	 * @param filter the file filter. <b>null</b> if no filtering must be applied.
	 */
	public static List < File > copyWorldRegionFolder ( File region_folder , File destiny_folder , FileFilter filter ) {
		List < File > copied_files = new ArrayList <> ( );
		File[] source = filter == null ? region_folder.listFiles ( )
				: region_folder.listFiles ( filter );
		
		if ( source != null && source.length > 0 ) {
			List < File > queue = new ArrayList <> ( Arrays.asList ( source ) );
			
			while ( queue.size ( ) > 0 ) {
				File next = queue.get ( 0 );
				
				if ( !FileUtil.isFileLocked ( next ) ) {
					File target = new File ( destiny_folder , next.getName ( ) );
					
					try {
						if ( destiny_folder.exists ( ) || destiny_folder.mkdirs ( ) ) {
							Files.copy ( next.toPath ( ) , target.toPath ( ) );
						} else {
							throw new IllegalStateException (
									"couldn't create a copy of the region file " + next.getName ( ) );
						}
						
						// successfully copied
						queue.remove ( 0 );
						copied_files.add ( next );
					} catch ( IOException e ) {
						e.printStackTrace ( );
					}
				}
			}
		}
		return copied_files;
	}
	
	/**
	 * This method will load the world that is stored in the provided folder, but making sure that:
	 * <ul>
	 *     <li>The environment is {@link org.bukkit.World.Environment#NORMAL}</li>
	 *     <li>The chunk generator is {@link EmptyChunkGenerator}</li>
	 *     <li>The structures generation is disabled</li>
	 *     <li>The auto-save is disabled</li>
	 *     <li>The mob-spawning is disabled</li>
	 * </ul>
	 *
	 * If there is not a valid world within the
	 * provided folder, <b>null</b> will be returned.
	 *
	 * @param world_folder the folder of the world to load.
	 * @return the loaded world, or null if there is not a valid world in the provided folder.
	 */
	public static World loadWorldEmpty ( File world_folder ) {
		if ( worldFolderCheck ( world_folder ) ) {
			World world = new WorldCreator ( world_folder.getPath ( ).replace ( '\\' , '/' ) )
					.environment ( World.Environment.NORMAL )
					.generator ( new EmptyChunkGenerator ( ) )
					.generateStructures ( false ).createWorld ( );
			
			if ( world != null ) {
				world.setAutoSave ( false );
				GameRuleType.MOB_SPAWNING.apply ( world , false );
			}
			
			return world;
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the highest block at the provided location.
	 *
	 * @param world the world.
	 * @param x the x coordinate.
	 * @param z the z coordinate.
	 * @return the highest or null.
	 */
	public static Block getHighestBlockAt ( World world , int x , int z , Predicate < Block > filter ) {
		for ( int y = world.getMaxHeight ( ) - 1 ; y >= 0 ; y-- ) {
			Block block = world.getBlockAt ( x , y , z );
			
			if ( filter.test ( block ) ) {
				return block;
			}
		}
		return null;
	}
	
	/**
	 * Gets whether the provided block is a player-block.
	 * <br>
	 * In a battle royale match, only the player-blocks
	 * will be able to be broken/exploded.
	 *
	 * @param block the block to check.
	 * @return whether the provided block is a player-block.
	 */
	public static boolean isPlayerBlock ( Block block ) {
		return block.hasMetadata ( PLAYER_BLOCK_METADATA_KEY );
	}
	
	/**
	 * Sets whether the provided block is a player-block.
	 * <br>
	 * In a battle royale match, only the player-blocks
	 * will be able to be broken/exploded.
	 *
	 * @param block the block to set.
	 * @param set whether to set as a <b>player-block</b>, or not.
	 */
	public static void setPlayerBlock ( Block block , boolean set ) {
		if ( set && !isPlayerBlock ( block ) ) {
			block.setMetadata ( PLAYER_BLOCK_METADATA_KEY , new FixedMetadataValue (
					BattleRoyale.getInstance ( ) , true ) );
		} else if ( !set ) {
			for ( Plugin plugin : Bukkit.getPluginManager ( ).getPlugins ( ) ) {
				block.removeMetadata ( PLAYER_BLOCK_METADATA_KEY , plugin );
			}
		}
	}
}