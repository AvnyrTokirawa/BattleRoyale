package es.outlook.adriansrj.battleroyale.world.chunk;

import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.world.RegionFile;
import es.outlook.adriansrj.battleroyale.util.nbt.NBTSerializable;

import java.util.Map;

/**
 * @author AdrianSR / 30/08/2021 / 05:31 p. m.
 */
public interface Chunk extends NBTSerializable {
	
	public ChunkLocation getLocation ( );
	
	public ChunkHeightmap getHeightmap ( );
	
	public ChunkSurface getSurface ( );
	
	public void recalculateHeightmap ( );
	
	public void recalculateSurface ( );
	
	public void write ( RegionFile region_file );
}