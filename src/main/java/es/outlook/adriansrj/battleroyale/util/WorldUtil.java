package es.outlook.adriansrj.battleroyale.util;

import es.outlook.adriansrj.battleroyale.world.chunk.EmptyChunkGenerator;
import es.outlook.adriansrj.core.util.world.GameRuleType;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;

import java.io.File;
import java.util.function.Predicate;

/**
 * @author AdrianSR / 03/09/2021 / 02:11 p. m.
 */
public class WorldUtil extends es.outlook.adriansrj.core.util.world.WorldUtil {
	
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
	 *
	 * @param world
	 * @param x
	 * @param z
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
}