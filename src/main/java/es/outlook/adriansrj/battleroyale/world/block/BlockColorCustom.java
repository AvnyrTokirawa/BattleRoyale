package es.outlook.adriansrj.battleroyale.world.block;

import es.outlook.adriansrj.battleroyale.util.ColorUtil;

import java.awt.*;

/**
 * Represents a custom block color.
 *
 * @author AdrianSR / 31/08/2021 / 02:16 p. m.
 */
public class BlockColorCustom implements BlockColor {
	
	protected final int   rgb;
	protected final Color color;
	
	public BlockColorCustom ( int rgb ) {
		this.rgb   = rgb;
		this.color = ColorUtil.getColorFromRGB ( rgb );
	}
	
	public BlockColorCustom ( Color color ) {
		this.rgb   = color.getRGB ( );
		this.color = color;
	}
	
	@Override
	public int getRGB ( ) {
		return rgb;
	}
	
	@Override
	public Color getColor ( ) {
		return color;
	}
}