package es.outlook.adriansrj.battleroyale.battlefield.minimap;

/**
 * Enumerates the minimap zooms.
 * <p>
 * @author AdrianSR / Tuesday 01 September, 2020 / 06:18 PM
 */
public enum MinimapZoom {
	
	NORMAL ( -1 ) ,
	
//	MINIMUM ( 128 * 2 ) ,
	
	MEDIUM ( 128 ),
	
	MAXIMUM ( 128 / 2 ),
	
	;
	
	// the smaller the display range, the greater the zoom.
	private final int range;

	MinimapZoom ( int range ) {
		this.range = range;
	}

	public int getDisplayRange ( ) {
		return range;
	}
}