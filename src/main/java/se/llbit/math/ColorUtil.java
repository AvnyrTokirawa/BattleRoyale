/* Copyright (c) 2012-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012-2021 Chunky contributors
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.math;

/**
 * Collection of utility methods for converting between different color representations.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public final class ColorUtil {
	
	/**
	 * @return INT RGB value corresponding to the given color
	 */
	public static int getRGB ( float r , float g , float b ) {
		return 0xFF000000 |
				( ( int ) ( 255 * r + .5f ) << 16 ) |
				( ( int ) ( 255 * g + .5f ) << 8 ) |
				( int ) ( 255 * b + .5f );
	}
	
	/**
	 * @return INT RGB value corresponding to the given color
	 */
	public static int getRGB ( double r , double g , double b ) {
		return 0xFF000000 |
				( ( int ) ( 255 * r + .5 ) << 16 ) |
				( ( int ) ( 255 * g + .5 ) << 8 ) |
				( int ) ( 255 * b + .5 );
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
}
