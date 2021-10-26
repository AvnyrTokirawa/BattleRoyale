package es.outlook.adriansrj.battleroyale.world.chunk;

import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.nbt.NBTSerializable;
import es.outlook.adriansrj.battleroyale.world.RegionFile;
import es.outlook.adriansrj.battleroyale.world.block.BlockTileEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AdrianSR / 30/08/2021 / 05:31 p. m.
 */
public interface Chunk extends NBTSerializable {
	
	default List < BlockTileEntity > getTileEntities ( ) {
		return new ArrayList <> ( );
	}
	
	default BlockTileEntity getTileEntity ( int x , int y , int z ) {
		return getTileEntities ( ).stream ( )
				.filter ( tile -> tile.getX ( ) == x && tile.getY ( ) == y && tile.getZ ( ) == z )
				.findAny ( ).orElse ( null );
	}
	
	ChunkLocation getLocation ( );
	
	ChunkHeightmap getHeightmap ( );
	
	ChunkSurface getSurface ( );
	
	void recalculateHeightmap ( );
	
	void recalculateSurface ( );
	
	void write ( RegionFile region_file );
}