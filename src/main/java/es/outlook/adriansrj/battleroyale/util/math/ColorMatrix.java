package es.outlook.adriansrj.battleroyale.util.math;

import java.awt.Color;
import java.util.Arrays;

/**
 * Represents a 2-dimensions matrix of custom capacity for storing colors in sRGB color model.
 *
 * @author AdrianSR / Tuesday 01 September, 2020 / 12:14 PM
 */
public class ColorMatrix {
	
	public static final Color TRANSPARENT = new Color ( 0 , 0 , 0 , 0 );
	
	public final    int     capacity;
	protected final Color[] values;
	
	public ColorMatrix ( int capacity ) {
		this.capacity = capacity;
		this.values   = new Color[ capacity * capacity ];
		
		// filling with transparency by default
		Arrays.fill ( values , TRANSPARENT );
	}
	
	public Color[] getValues ( ) {
		return values;
	}
	
	public Color get ( int column , int row ) {
		Color color = values[ column + row * capacity ];
		
		return color != null ? color : TRANSPARENT;
	}
	
	public int getRGB ( int column , int row ) {
		return get ( column , row ).getRGB ( );
	}
	
	public void set ( int column , int row , Color color ) {
		if ( color == null ) {
			color = TRANSPARENT;
		}
		
		values[ column + row * capacity ] = color;
	}
	
	public void setRGB ( int column , int row , int rgb ) {
		set ( column , row , new Color ( rgb ) );
	}
	
	public void fill ( Color color ) {
		Arrays.fill ( values , color != null ? color : TRANSPARENT );
	}
	
	public void fillRGB ( int rgb ) {
		fill ( new Color ( rgb ) );
	}
}