package es.outlook.adriansrj.battleroyale.battlefield.minimap;

import es.outlook.adriansrj.battleroyale.util.math.ColorMatrix;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Battle map minimap.
 *
 * @author AdrianSR / Monday 31 August, 2020 / 05:15 PM
 */
public class Minimap {
	
	/** color matrix */
	protected ColorMatrix colors;
	/** the last file this minimap was loaded from */
	protected File        file;
	
	/**
	 * Construct the minimap providing colors matrix.
	 *
	 * @param colors the colors matrix.
	 */
	public Minimap ( ColorMatrix colors ) {
		this.colors = colors;
	}
	
	/**
	 * Construct the minimap providing the file to load colors from.
	 *
	 * @param file the file to load from.
	 *
	 * @throws IOException if an error occurs during loading.
	 */
	public Minimap ( File file ) throws IOException {
		load ( file );
	}
	
	/**
	 * Gets the minimap colors.
	 *
	 * @return minimap colors, or null if not loaded.
	 */
	@Nullable
	public ColorMatrix getColors ( ) {
		return colors;
	}
	
	/**
	 * Gets the last file this minimap was loaded from.
	 *
	 * @return last load file.
	 */
	@Nullable
	public File getLastFile ( ) {
		return file;
	}
	
	/**
	 * Loads the colors from the provided {@link File}.
	 *
	 * @param file the file to load from.
	 *
	 * @throws IOException              if an error occurs during loading.
	 * @throws IllegalArgumentException if the image is not squared or if the image is too small (< 2).
	 */
	public void load ( @Nonnull File file ) throws IOException {
		BufferedImage image = ImageIO.read ( file );
		
		if ( image.getWidth ( ) == image.getHeight ( ) ) {
			if ( image.getWidth ( ) < 2 ) {
				throw new IllegalArgumentException ( "image is too small!" );
			}
			
			this.colors = new ColorMatrix ( image.getWidth ( ) /* width and height are the same for us */ );
			
			for ( int x = 0 ; x < colors.capacity ; x++ ) {
				for ( int y = 0 ; y < colors.capacity ; y++ ) {
					this.colors.setRGB ( x , y , image.getRGB ( x , y ) );
				}
			}
			
			// yeah, this confirms it was loaded successfully.
			this.file = file;
		} else {
			throw new IllegalArgumentException ( "image must be squared!" );
		}
	}
	
	/**
	 * Exports this minimap to the desired .png {@link File}.
	 *
	 * @param file the .png file to save.
	 *
	 * @throws IOException if an error occurs during saving.
	 */
	public void save ( @Nonnull File file ) throws IOException {
		if ( colors == null ) {
			throw new IllegalStateException ( "incomplete minimap!" );
		}
		
		int           size  = colors.capacity;
		BufferedImage image = new BufferedImage ( size , size , BufferedImage.TYPE_INT_ARGB );
		
		for ( int x = 0 ; x < size ; x++ ) {
			for ( int y = 0 ; y < size ; y++ ) {
				image.setRGB ( x , y , colors.getRGB ( x , y ) );
			}
		}
		
		ImageIO.write ( image , "png" , file );
	}
}