package es.outlook.adriansrj.battleroyale.util;

import es.outlook.adriansrj.battleroyale.world.chunk.EmptyChunkGenerator;
import es.outlook.adriansrj.core.util.world.GameRuleType;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;

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
			String path = world_folder.getPath ( ).replace ( '\\' , '/' );
			World world = new WorldCreator ( path ).environment ( World.Environment.NORMAL )
					.generator ( new EmptyChunkGenerator ( ) )
					.generateStructures ( false ).createWorld ( );
			
			world.setAutoSave ( false );
			GameRuleType.MOB_SPAWNING.apply ( world , false );
			
			return world;
		} else {
			return null;
		}
	}
}