package es.outlook.adriansrj.battleroyale.world.chunk;

import es.outlook.adriansrj.battleroyale.util.ColorUtil;
import es.outlook.adriansrj.battleroyale.world.ScalableHeightmap;
import es.outlook.adriansrj.battleroyale.world.block.BlockColor;
import es.outlook.adriansrj.battleroyale.world.block.BlockColorCustom;
import es.outlook.adriansrj.battleroyale.world.block.BlockColorDefault;
import org.apache.commons.lang3.Validate;

import java.awt.*;
import java.util.Arrays;

/**
 * @author AdrianSR / 31/08/2021 / 10:34 a. m.
 */
public class ChunkSurface {
	
	protected final BlockColor[] colors = new BlockColor[ 16 * 16 ];
	protected final Chunk        chunk;
	
	public ChunkSurface ( Chunk chunk ) {
		this.chunk = chunk;
		
		Arrays.fill ( colors , BlockColorDefault.AIR );
	}
	
	public ChunkSurface ( Chunk chunk , BlockColor[] colors ) {
		Validate.notNull ( colors , "colors cannot be null" );
		Validate.isTrue ( colors.length == this.colors.length ,
						  "colors array doesn't match required size of " + this.colors.length );
		
		this.chunk = chunk;
		
		for ( int i = 0; i < this.colors.length; i++ ) {
			BlockColor color = colors[ i ];
			
			if ( color != null ) {
				this.colors[ i ] = color;
			} else {
				this.colors[ i ] = BlockColorDefault.AIR;
			}
		}
	}
	
	/**
	 * Gets colors of this chunk surface.
	 *
	 * @return the color.
	 */
	public BlockColor[] getColors ( ) {
		return colors;
	}
	
	/**
	 * Gets the chunk.
	 *
	 * @return the chunk.
	 */
	public Chunk getChunk ( ) {
		return chunk;
	}
	
	/**
	 * Gets the surface color at the specified relative coordinates <strong>x, z</strong>.
	 *
	 * @param x the block x relative to chunk ( 0 - 15 )
	 * @param z the block z relative to chunk ( 0 - 15 )
	 *
	 * @return the color at the specified coordinates.
	 */
	public BlockColor getColor ( int x , int z ) {
		return colors[ index ( x , z ) ];
	}
	
	/**
	 * Sets the surface color at the specified relative coordinates <strong>x, z</strong>.
	 *
	 * @param x     the block x relative to chunk ( 0 - 15 )
	 * @param z     the block z relative to chunk ( 0 - 15 )
	 * @param color the color to set.
	 */
	public void setColor ( int x , int z , BlockColor color ) {
		Validate.notNull ( color , "color cannot be null" );
		
		colors[ index ( x , z ) ] = color;
	}
	
	protected int index ( int x , int z ) {
		return ( x & 0xF ) + ( z & 0xF ) * 16;
	}
	
	public void applyGradient ( ScalableHeightmap heightmap ) {
		if ( heightmap == null ) {
			heightmap = new ScalableHeightmap ( );
			heightmap.setHeights ( chunk.getLocation ( ) , chunk.getHeightmap ( ) );
		}
		
		int     cx  = chunk.getLocation ( ).getX ( ) * 16;
		int     cz  = chunk.getLocation ( ).getZ ( ) * 16;
		float[] rgb = new float[ 3 ];
		
		for ( int x = 0; x < 16; x++ ) {
			for ( int z = 0; z < 16; z++ ) {
				BlockColor block_color = getColor ( x , z );
				
				if ( block_color == null || block_color == BlockColorDefault.AIR
						|| block_color.getColor ( ).getRGB ( ) == 0 ) {
					continue;
				}
				
				Color color = block_color.getColor ( );
				ColorUtil.getRGBComponents ( ColorUtil.getArgb (
						( float ) color.getRed ( ) / 255.F ,
						( float ) color.getGreen ( ) / 255.F ,
						( float ) color.getBlue ( ) / 255.F , 1.0F ) , rgb );
				
				float gradient = ( heightmap.getHeight ( cx + x , cz + z )
						+ heightmap.getHeight ( cx + x + 1 , cz + z )
						+ heightmap.getHeight ( cx + x , cz + z + 1 )
						- heightmap.getHeight ( cx + x - 1 , cz + z )
						- heightmap.getHeight ( cx + x , cz + z - 1 )
						- heightmap.getHeight ( cx + x - 1 , cz + z - 1 ) );
				
				gradient = ( float ) ( ( Math.atan ( gradient / 15 ) / ( Math.PI / 1.7 ) ) + 1 );
				
				rgb[ 0 ] *= gradient;
				rgb[ 1 ] *= gradient;
				rgb[ 2 ] *= gradient;
				
				// clip the result
				rgb[ 0 ] = Math.max ( 0.0F , rgb[ 0 ] );
				rgb[ 0 ] = Math.min ( 1.0F , rgb[ 0 ] );
				rgb[ 1 ] = Math.max ( 0.0F , rgb[ 1 ] );
				rgb[ 1 ] = Math.min ( 1.0F , rgb[ 1 ] );
				rgb[ 2 ] = Math.max ( 0.0F , rgb[ 2 ] );
				rgb[ 2 ] = Math.min ( 1.0F , rgb[ 2 ] );
				
				setColor ( x , z , new BlockColorCustom ( ColorUtil.getRGB ( rgb[ 0 ] , rgb[ 1 ] , rgb[ 2 ] ) ) );
			}
		}
	}
	
	public void applyGradient ( ) {
		applyGradient ( null );
	}
}