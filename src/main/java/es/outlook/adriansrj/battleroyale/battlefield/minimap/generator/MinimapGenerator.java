package es.outlook.adriansrj.battleroyale.battlefield.minimap.generator;

import es.outlook.adriansrj.battleroyale.battlefield.minimap.Minimap;
import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.math.ColorMatrix;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.battleroyale.world.ScalableHeightmap;
import es.outlook.adriansrj.battleroyale.world.block.BlockColor;
import es.outlook.adriansrj.battleroyale.world.block.BlockColorDefault;
import es.outlook.adriansrj.battleroyale.world.chunk.Chunk;
import es.outlook.adriansrj.battleroyale.world.chunk.provider.ChunkProviderWorldFolder;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import org.apache.commons.lang3.Validate;
import org.bukkit.World;

import java.io.Closeable;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author AdrianSR / 31/08/2021 / 02:36 p. m.
 */
public class MinimapGenerator implements Closeable {
	
	protected final File                     world_folder;
	protected       ChunkProviderWorldFolder chunk_provider;
	protected       ScalableHeightmap        heightmap;
	protected       World                    world;
	
	// result
	protected Minimap result;
	protected boolean closed;
	
	public MinimapGenerator ( File world_folder ) {
		this.world_folder   = world_folder;
		this.chunk_provider = new ChunkProviderWorldFolder ( world_folder );
		this.heightmap      = new ScalableHeightmap ( );
	}
	
	public MinimapGenerator ( World world ) {
		this ( world.getWorldFolder ( ) );
		this.world = world;
	}
	
	public void generate ( ZoneBounds bounds ) {
		Validate.isTrue ( !closed , "this generator is already closed" );
		Validate.notNull ( bounds , "bounds cannot be null" );
		
		// we're disabling the auto-saving as we don't want
		// the world to be saved while we're acceding its files
		final boolean autosave = world != null && world.isAutoSave ( );
		
		if ( world != null ) {
			world.setAutoSave ( false );
		}
		
		// generating
		Location2I            origin       = bounds.getMinimum ( );
		int                   size         = bounds.getSize ( );
		ColorMatrix           matrix       = new ColorMatrix ( size );
		Set < ChunkLocation > recalculated = new HashSet <> ( );
		
		// we will first create the chunks and calculate
		// the heightmaps and surfaces.
		for ( int x = 0 ; x < size ; x++ ) {
			int xx = origin.getX ( ) + x;
			for ( int z = 0 ; z < size ; z++ ) {
				int           zz             = origin.getZ ( ) + z;
				ChunkLocation chunk_location = new ChunkLocation ( xx >> 4 , zz >> 4 );
				Chunk         chunk          = chunk_provider.getChunk ( chunk_location );
				
				if ( chunk != null && recalculated.add ( chunk_location ) ) {
					try {
						chunk.recalculateHeightmap ( );
						chunk.recalculateSurface ( );
						
						heightmap.setHeights ( chunk_location , chunk.getHeightmap ( ) );
					} catch ( Exception ex ) {
						// we don't want the process to
						// stop if a chunk cannot be loaded.
						ex.printStackTrace ( );
					}
				}
			}
		}
		
		// and now that the heightmap is done, we can apply the gradient,
		// and fill our color matrix.
		recalculated.stream ( ).map ( location -> chunk_provider.getChunk ( location ) ).map ( Chunk :: getSurface )
				.forEach ( surface -> surface.applyGradient ( this.heightmap ) );
		
		for ( int x = 0 ; x < size ; x++ ) {
			int xx = origin.getX ( ) + x;
			for ( int z = 0 ; z < size ; z++ ) {
				int   zz    = origin.getZ ( ) + z;
				Chunk chunk = chunk_provider.getChunk ( xx >> 4 , zz >> 4 );
				
				if ( chunk != null ) {
					BlockColor color = chunk.getSurface ( ).getColor ( xx & 0xF , zz & 0xF );
					
					if ( color != BlockColorDefault.AIR ) {
						matrix.set ( x , z , color.getColor ( ) );
					} else {
						matrix.set ( x , z , ColorMatrix.TRANSPARENT );
					}
				}
			}
		}
		
		this.result = new Minimap ( matrix );
		
		// re-enabling autosave
		if ( world != null ) {
			world.setAutoSave ( autosave );
		}
		
		recalculated.clear ( );
	}
	
	public void generate ( BoundingBox bounds ) {
		generate ( new ZoneBounds ( bounds ) );
	}
	
	public Minimap getResult ( ) {
		if ( result != null && closed ) {
			return result;
		} else {
			if ( result == null ) {
				throw new IllegalStateException ( "call generate() first " );
			} else {
				throw new IllegalStateException ( "must call close() to get a result" );
			}
		}
	}
	
	@Override
	public void close ( ) throws IllegalStateException {
		if ( closed ) {
			throw new IllegalStateException ( "generator already closed" );
		}
		
		// hey garbage collector, do your work!
		this.chunk_provider = null;
		this.heightmap      = null;
		this.closed         = true;
	}
}