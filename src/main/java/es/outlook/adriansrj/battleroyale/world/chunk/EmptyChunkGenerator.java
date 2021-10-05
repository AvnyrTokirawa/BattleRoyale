package es.outlook.adriansrj.battleroyale.world.chunk;

import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * Empty chunk generator.
 *
 * @author AdrianSR / Wednesday 23 December, 2020 / 12:50 PM
 */
public class EmptyChunkGenerator extends ChunkGenerator {
	
	@Override
	public ChunkData generateChunkData ( World world , Random random , int x , int z , BiomeGrid biome ) {
		ChunkData data = createChunkData ( world );
		
		data.setRegion ( 0 , 0 , 0 , 16 , 256 , 16 , UniversalMaterial.AIR.getMaterial ( ) );
		return data;
	}
	
	@Override
	public Location getFixedSpawnLocation ( World world , Random random ) {
		return new Location ( world , 0.0D , 0.0D , 0.0D );
	}
	
	// 1.14 +
	public boolean shouldGenerateMobs ( ) {
		return false;
	}
	
	public boolean shouldGenerateDecorations ( ) {
		return false;
	}
	
	public boolean shouldGenerateStructures ( ) {
		return false;
	}
	
	public boolean shouldGenerateCaves ( ) {
		return false;
	}
	
	// 1.17+
	/*public int getBaseHeight ( WorldInfo worldInfo , Random random , int x , int z , HeightMap heightMap ) {
		return 0;
	}*/
}