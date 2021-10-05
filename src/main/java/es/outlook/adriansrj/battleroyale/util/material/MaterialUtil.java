package es.outlook.adriansrj.battleroyale.util.material;

import es.outlook.adriansrj.core.util.material.UniversalMaterial;

/**
 * Useful class for dealing with materials.
 *
 * @author AdrianSR / 11/09/2021 / 11:10 a. m.
 */
public class MaterialUtil extends es.outlook.adriansrj.core.util.material.MaterialUtils {
	
	public static boolean isWool ( UniversalMaterial material ) {
		switch ( material ) {
			case BLUE_WOOL:
			case BROWN_WOOL:
			case WHITE_WOOL:
			case CYAN_WOOL:
			case GRAY_WOOL:
			case GREEN_WOOL:
			case LIGHT_BLUE_WOOL:
			case LIGHT_GRAY_WOOL:
			case LIME_WOOL:
			case MAGENTA_WOOL:
			case ORANGE_WOOL:
			case PINK_WOOL:
			case PURPLE_WOOL:
			case RED_WOOL:
			case YELLOW_WOOL:
			case BLACK_WOOL:
				return true;
			default:
				return false;
		}
	}
	
	public static boolean isBanner ( UniversalMaterial material ) {
		switch ( material ) {
			case BLUE_BANNER:
			case BROWN_BANNER:
			case WHITE_BANNER:
			case CYAN_BANNER:
			case GRAY_BANNER:
			case GREEN_BANNER:
			case LIGHT_BLUE_BANNER:
			case LIGHT_GRAY_BANNER:
			case LIME_BANNER:
			case MAGENTA_BANNER:
			case ORANGE_BANNER:
			case PINK_BANNER:
			case PURPLE_BANNER:
			case RED_BANNER:
			case YELLOW_BANNER:
			case BLACK_BANNER:
			case BLUE_WALL_BANNER:
			case BROWN_WALL_BANNER:
			case WHITE_WALL_BANNER:
			case CYAN_WALL_BANNER:
			case GRAY_WALL_BANNER:
			case GREEN_WALL_BANNER:
			case LIGHT_BLUE_WALL_BANNER:
			case LIGHT_GRAY_WALL_BANNER:
			case LIME_WALL_BANNER:
			case MAGENTA_WALL_BANNER:
			case ORANGE_WALL_BANNER:
			case PINK_WALL_BANNER:
			case PURPLE_WALL_BANNER:
			case RED_WALL_BANNER:
			case YELLOW_WALL_BANNER:
				return true;
			
			default:
				return false;
		}
	}
}
