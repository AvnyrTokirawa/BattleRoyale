package es.outlook.adriansrj.battleroyale.util;

import java.awt.*;

/**
 * @author Jesper <jesper@llbit.se>
 * @author AdrianSR / 31/08/2021 / 10:37 a. m.
 */
public class ColorUtil extends es.outlook.adriansrj.core.util.ColorUtil {
	
	public static Color getColorFromRGB ( int rgb ) {
		int r = ( rgb & 16711680 ) >> 16;
		int g = ( rgb & '\uff00' ) >> 8;
		int b = ( rgb & 255 ) >> 0;
		
		return new Color ( ( float ) r / 255.0F , ( float ) g / 255.0F , ( float ) b / 255.0F );
	}
	
	/**
	 * Blend the two argb colors a and b. Result is stored in the array a.
	 */
	public static float[] blend ( float[] src , float[] dst ) {
		float[] out = new float[ 4 ];
		out[ 3 ] = src[ 3 ] + dst[ 3 ] * ( 1 - src[ 3 ] );
		out[ 0 ] = ( src[ 0 ] * src[ 3 ] + dst[ 0 ] * dst[ 3 ] * ( 1 - src[ 3 ] ) ) / out[ 3 ];
		out[ 1 ] = ( src[ 1 ] * src[ 3 ] + dst[ 1 ] * dst[ 3 ] * ( 1 - src[ 3 ] ) ) / out[ 3 ];
		out[ 2 ] = ( src[ 2 ] * src[ 3 ] + dst[ 2 ] * dst[ 3 ] * ( 1 - src[ 3 ] ) ) / out[ 3 ];
		return out;
	}
	
	/**
	 * @return INT RGB value corresponding to the given color
	 */
	public static int getRGB ( float r , float g , float b ) {
		return 0xFF000000 | ( ( int ) ( 255 * r + .5f ) << 16 ) | ( ( int ) ( 255 * g + .5f ) << 8 ) | ( int ) ( 255 * b + .5f );
	}
	
	/**
	 * @return INT RGB value corresponding to the given color
	 */
	public static int getRGB ( double r , double g , double b ) {
		return 0xFF000000 | ( ( int ) ( 255 * r + .5 ) << 16 ) | ( ( int ) ( 255 * g + .5 ) << 8 ) | ( int ) ( 255 * b + .5 );
	}
	
	/**
	 * @return INT ARGB value corresponding to the given color
	 */
	public static int getArgb ( float r , float g , float b , float a ) {
		return ( ( int ) ( 255 * a + .5f ) << 24 ) | ( ( int ) ( 255 * r + .5f ) << 16 ) | ( ( int ) ( 255 * g + .5f ) << 8 )
				| ( int ) ( 255 * b + .5f );
	}
	
	/**
	 * @return INT ARGB value corresponding to the given color
	 */
	public static int getArgb ( double r , double g , double b , double a ) {
		return ( ( int ) ( 255 * a + .5 ) << 24 ) | ( ( int ) ( 255 * r + .5 ) << 16 ) | ( ( int ) ( 255 * g + .5 ) << 8 )
				| ( int ) ( 255 * b + .5 );
	}
	
	/**
	 * Get the RGB color components from an INT RGB value.
	 */
	public static void getRGBComponents ( int irgb , float[] frgb ) {
		frgb[ 0 ] = ( 0xFF & ( irgb >> 16 ) ) / 255.f;
		frgb[ 1 ] = ( 0xFF & ( irgb >> 8 ) ) / 255.f;
		frgb[ 2 ] = ( 0xFF & irgb ) / 255.f;
	}
	
	/**
	 * Get the RGB color components from an INT RGB value.
	 */
	public static void getRGBComponents ( int irgb , double[] frgb ) {
		frgb[ 0 ] = ( 0xFF & ( irgb >> 16 ) ) / 255.0;
		frgb[ 1 ] = ( 0xFF & ( irgb >> 8 ) ) / 255.0;
		frgb[ 2 ] = ( 0xFF & irgb ) / 255.0;
	}
	
	/**
	 * Get the RGBA color components from an INT ARGB value.
	 */
	public static void getRGBAComponents ( int irgb , float[] frgb ) {
		frgb[ 3 ] = ( irgb >>> 24 ) / 255.f;
		frgb[ 0 ] = ( 0xFF & ( irgb >> 16 ) ) / 255.f;
		frgb[ 1 ] = ( 0xFF & ( irgb >> 8 ) ) / 255.f;
		frgb[ 2 ] = ( 0xFF & irgb ) / 255.f;
	}
	
	/**
	 * Get the RGBA color components from an INT ARGB value.
	 */
	public static void getRGBAComponents ( int irgb , double[] frgb ) {
		frgb[ 3 ] = ( irgb >>> 24 ) / 255.0;
		frgb[ 0 ] = ( 0xFF & ( irgb >> 16 ) ) / 255.0;
		frgb[ 1 ] = ( 0xFF & ( irgb >> 8 ) ) / 255.0;
		frgb[ 2 ] = ( 0xFF & irgb ) / 255.0;
	}
	
	/**
	 * @return Get INT RGB value corresponding to the given color
	 */
	public static int getRGB ( float[] frgb ) {
		return 0xFF000000 | ( ( int ) ( 255 * frgb[ 0 ] + .5f ) << 16 ) | ( ( int ) ( 255 * frgb[ 1 ] + .5f ) << 8 )
				| ( int ) ( 255 * frgb[ 2 ] + .5f );
	}
	
	/**
	 * @return Get INT RGB value corresponding to the given color
	 */
	public static int getRGB ( double[] frgb ) {
		return 0xFF000000 | ( ( int ) ( 255 * frgb[ 0 ] + .5 ) << 16 ) | ( ( int ) ( 255 * frgb[ 1 ] + .5 ) << 8 )
				| ( int ) ( 255 * frgb[ 2 ] + .5 );
	}
}