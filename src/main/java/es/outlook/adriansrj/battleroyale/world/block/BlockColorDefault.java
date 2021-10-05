package es.outlook.adriansrj.battleroyale.world.block;

import es.outlook.adriansrj.battleroyale.util.ColorUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

/**
 * Enumerates the minecraft default block colors.
 *
 * @author AdrianSR / 31/08/2021 / 01:37 p. m.
 */
public enum BlockColorDefault implements BlockColor {
	
	AIR ( 0 , 0 ),
	GRASS ( 1 , 8368696 ),
	SAND ( 2 , 16247203 ),
	CLOTH ( 3 , 13092807 ),
	TNT ( 4 , 16711680 ),
	ICE ( 5 , 10526975 ),
	IRON ( 6 , 10987431 ),
	FOLIAGE ( 7 , 31744 ),
	SNOW ( 8 , 16777215 ),
	CLAY ( 9 , 10791096 ),
	DIRT ( 10 , 9923917 ),
	STONE ( 11 , 7368816 ),
	WATER ( 12 , 4210943 ),
	WOOD ( 13 , 9402184 ),
	QUARTZ ( 14 , 16776437 ),
	ADOBE ( 15 , 14188339 ),
	MAGENTA ( 16 , 11685080 ),
	LIGHT_BLUE ( 17 , 6724056 ),
	YELLOW ( 18 , 15066419 ),
	LIME ( 19 , 8375321 ),
	PINK ( 20 , 15892389 ),
	GRAY ( 21 , 5000268 ),
	SILVER ( 22 , 10066329 ),
	CYAN ( 23 , 5013401 ),
	PURPLE ( 24 , 8339378 ),
	BLUE ( 25 , 3361970 ),
	BROWN ( 26 , 6704179 ),
	GREEN ( 27 , 6717235 ),
	RED ( 28 , 10040115 ),
	BLACK ( 29 , 1644825 ),
	GOLD ( 30 , 16445005 ),
	DIAMOND ( 31 , 6085589 ),
	LAPIS ( 32 , 4882687 ),
	EMERALD ( 33 , 55610 ),
	OBSIDIAN ( 34 , 8476209 ),
	NETHERRACK ( 35 , 7340544 ),
	WHITE_STAINED_HARDENED_CLAY ( 36 , 13742497 ),
	ORANGE_STAINED_HARDENED_CLAY ( 37 , 10441252 ),
	MAGENTA_STAINED_HARDENED_CLAY ( 38 , 9787244 ),
	LIGHT_BLUE_STAINED_HARDENED_CLAY ( 39 , 7367818 ),
	YELLOW_STAINED_HARDENED_CLAY ( 40 , 12223780 ),
	LIME_STAINED_HARDENED_CLAY ( 41 , 6780213 ),
	PINK_STAINED_HARDENED_CLAY ( 42 , 10505550 ),
	GRAY_STAINED_HARDENED_CLAY ( 43 , 3746083 ),
	SILVER_STAINED_HARDENED_CLAY ( 44 , 8874850 ),
	CYAN_STAINED_HARDENED_CLAY ( 45 , 5725276 ),
	PURPLE_STAINED_HARDENED_CLAY ( 46 , 8014168 ),
	BLUE_STAINED_HARDENED_CLAY ( 47 , 4996700 ),
	BROWN_STAINED_HARDENED_CLAY ( 48 , 4993571 ),
	GREEN_STAINED_HARDENED_CLAY ( 49 , 5001770 ),
	RED_STAINED_HARDENED_CLAY ( 50 , 9321518 ),
	BLACK_STAINED_HARDENED_CLAY ( 51 , 2430480 ),
	CRIMSON_NYLIUM ( 52 , 12398641 ),
	CRIMSON_STEM ( 53 , 9715553 ),
	CRIMSON_HYPHAE ( 54 , 6035741 ),
	WARPED_NYLIUM ( 55 , 1474182 ),
	WARPED_STEM ( 56 , 3837580 ),
	WARPED_HYPHAE ( 57 , 5647422 ),
	WARPED_WART_BLOCK ( 58 , 1356933 );
	
	protected final int   id;
	protected final int   mc_rgb;
	protected final Color color;
	protected final int   rgb;
	
	BlockColorDefault ( int id , int mc_rgb ) {
		this.id     = id;
		this.mc_rgb = mc_rgb;
		this.color  = ColorUtil.getColorFromRGB ( mc_rgb );
		this.rgb    = color.getRGB ( );
	}
	
	public int getId ( ) {
		return id;
	}
	
	public int getMinecraftRGB ( ) {
		return mc_rgb;
	}
	
	@Override
	public int getRGB ( ) {
		return rgb;
	}
	
	@Override
	public Color getColor ( ) {
		return color;
	}
	
	public static Optional < BlockColorDefault > getById ( int id ) {
		return Arrays.stream ( values ( ) ).filter ( target -> target.id == id ).findAny ( );
	}
	
	public static Optional < BlockColorDefault > getByMinecraftRGB ( int mc_rgb ) {
		return Arrays.stream ( values ( ) ).filter ( target -> target.mc_rgb == mc_rgb ).findAny ( );
	}
	
	public static Optional < BlockColorDefault > getByRGB ( int rgb ) {
		return Arrays.stream ( values ( ) ).filter ( target -> target.rgb == rgb ).findAny ( );
	}
}