package es.outlook.adriansrj.battleroyale.battlefield.minimap.generator;

import es.outlook.adriansrj.battleroyale.battlefield.minimap.Minimap;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.file.FileUtil;
import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.math.ColorMatrix;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.battleroyale.util.world.WorldUtil;
import es.outlook.adriansrj.battleroyale.world.ScalableHeightmap;
import es.outlook.adriansrj.battleroyale.world.block.BlockColor;
import es.outlook.adriansrj.battleroyale.world.block.BlockColorDefault;
import es.outlook.adriansrj.battleroyale.world.chunk.Chunk;
import es.outlook.adriansrj.battleroyale.world.chunk.provider.ChunkProviderWorldFolder;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author AdrianSR / 31/08/2021 / 02:36 p. m.
 */
public class MinimapGenerator implements Closeable {
	
	protected final File                     world_folder;
	protected       File                     copy_folder;
	protected       ChunkProviderWorldFolder chunk_provider;
	protected       ScalableHeightmap        heightmap;
	
	// result
	protected Minimap result;
	protected boolean closed;
	
	// corrupted chunks
	protected final Set < ChunkLocation > corrupted        = new HashSet <> ( );
	protected       boolean               corrupted_logged = false;
	
	public MinimapGenerator ( File world_folder ) {
		this.world_folder = world_folder;
		this.heightmap    = new ScalableHeightmap ( );
	}
	
	public MinimapGenerator ( World world ) {
		this ( world.getWorldFolder ( ) );
	}
	
	/**
	 * Blocks until done
	 *
	 * @param bounds
	 */
	public void generate ( ZoneBounds bounds ) {
		Validate.isTrue ( !closed , "this generator is already closed" );
		Validate.notNull ( bounds , "bounds cannot be null" );
		
		Location2I origin = bounds.getMinimum ( );
		int        size   = bounds.getSize ( );
		
		// generating copy folder
		this.copy_folder    = new File ( EnumDirectory.BATTLEFIELD_TEMP_DIRECTORY.getDirectory ( ) ,
										 UUID.randomUUID ( ).toString ( ) );
		this.chunk_provider = new ChunkProviderWorldFolder ( copy_folder );
		
		if ( copy_folder.mkdirs ( ) ) {
			// finding out region files that are actually required.
			final Set < File > required = new HashSet <> ( );
			
			for ( int x = 0 ; x < size ; x++ ) {
				int xx = origin.getX ( ) + x;
				for ( int z = 0 ; z < size ; z++ ) {
					int zz = origin.getZ ( ) + z;
					
					required.add ( FileUtil.getRegionFileWorldFolder (
							world_folder ,
							new ChunkLocation ( xx >> 4 , zz >> 4 ).getRegionLocation ( ) ) );
				}
			}
			
			// then creating a safe copy.
			WorldUtil.copyWorldRegionFolder (
					FileUtil.getRegionFolder ( world_folder ) ,
					FileUtil.getRegionFolder ( copy_folder ) ,
					required :: contains );
		} else {
			throw new IllegalStateException ( "couldn't generate copy folder" );
		}
		
		// generating
		ColorMatrix   matrix       = new ColorMatrix ( size );
		Set < Chunk > recalculated = new HashSet <> ( );
		
		// we will first create the chunks and calculate
		// the heightmaps and surfaces.
		for ( int x = 0 ; x < size ; x++ ) {
			int xx = origin.getX ( ) + x;
			for ( int z = 0 ; z < size ; z++ ) {
				int zz = origin.getZ ( ) + z;
				
				// reading chunk
				ChunkLocation chunk_location = new ChunkLocation ( xx >> 4 , zz >> 4 );
				Chunk         chunk          = null;
				
				if ( corrupted.contains ( chunk_location ) ) {
					// corrupted chunks may slow the process as they
					// will not be cached by the provider, so
					// let's skip them.
					continue;
				}
				
				try {
					chunk = chunk_provider.getChunk ( chunk_location );
				} catch ( IOException | IllegalArgumentException e ) {
					corrupted.add ( chunk_location );
					
					if ( !corrupted_logged ) {
						ConsoleUtil.sendPluginMessage (
								ChatColor.RED , "Minimap generation may take longer than " +
										"expected as there are some chunks that appear to be corrupted." ,
								BattleRoyale.getInstance ( ) );
						
						corrupted_logged = true;
					}
					continue;
				}
				
				// calculating heightmap & surface
				if ( chunk != null && recalculated.add ( chunk ) ) {
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
		recalculated.stream ( ).map ( Chunk :: getSurface )
				.forEach ( surface -> surface.applyGradient ( this.heightmap ) );
		
		for ( int x = 0 ; x < size ; x++ ) {
			int xx = origin.getX ( ) + x;
			for ( int z = 0 ; z < size ; z++ ) {
				int           zz             = origin.getZ ( ) + z;
				ChunkLocation chunk_location = new ChunkLocation ( xx >> 4 , zz >> 4 );
				Chunk         chunk          = null;
				
				if ( corrupted.contains ( chunk_location ) ) {
					// corrupted chunks may slow the process as they
					// will not be cached by the provider, so
					// let's skip them.
					continue;
				}
				
				try {
					chunk = chunk_provider.getChunk ( chunk_location );
				} catch ( IOException | IllegalArgumentException ignored ) {
					continue;
				}
				
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
		this.corrupted.clear ( );
		
		// disposing copy folder
		try {
			FileUtil.deleteDirectory ( copy_folder );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		}
	}
}