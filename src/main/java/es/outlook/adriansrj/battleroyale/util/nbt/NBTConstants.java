package es.outlook.adriansrj.battleroyale.util.nbt;

import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;

/**
 * @author AdrianSR / 30/08/2021 / 06:15 p. m.
 */
public class NBTConstants {
	
	/**
	 * Versions older than {@link EnumDataVersion#v1_13}.
	 *
	 * @author AdrianSR / 30/08/2021 / 06:16 p. m.
	 */
	public static class Pre13 {
		
		public static final String CHUNK_LEVEL_TAG             = "Level";
		public static final String CHUNK_DATA_VERSION_TAG      = "DataVersion";
		public static final String CHUNK_SECTIONS_TAG          = "Sections";
		public static final String CHUNK_X_POS_TAG             = "xPos";
		public static final String CHUNK_Z_POS_TAG             = "zPos";
		public static final String CHUNK_LAST_UPDATE_TAG       = "LastUpdate";
		public static final String CHUNK_TERRAIN_POPULATED_TAG = "TerrainPopulated";
		public static final String CHUNK_LIGHT_POPULATED_TAG   = "LightPopulated";
		public static final String CHUNK_ENTITIES_TAG          = "Entities";
		
		public static final String CHUNK_TILE_ENTITIES_TAG  = "TileEntities";
		public static final String CHUNK_TILE_ENTITY_ID_TAG = "id";
		public static final String CHUNK_TILE_ENTITY_X_TAG  = "x";
		public static final String CHUNK_TILE_ENTITY_Y_TAG  = "y";
		public static final String CHUNK_TILE_ENTITY_Z_TAG  = "z";
		
		public static final String CHUNK_SECTION_Y_TAG           = "Y";
		public static final String CHUNK_SECTION_BLOCKS_TAG      = "Blocks";
		public static final String CHUNK_SECTION_ADD_TAG         = "Add";
		public static final String CHUNK_SECTION_DATA_TAG        = "Data";
		public static final String CHUNK_SECTION_BLOCK_LIGHT_TAG = "BlockLight";
		public static final String CHUNK_SECTION_SKY_LIGHT_TAG   = "SkyLight";
		public static final String CHUNK_HEIGHT_MAP_TAG          = "HeightMap";
	}
	
	/**
	 * Versions equals to or newer than {@link EnumDataVersion#v1_13}.
	 *
	 * @author AdrianSR / 30/08/2021 / 06:16 p. m.
	 */
	public static class Post13 {
		
		public static final String CHUNK_LEVEL_TAG        = "Level";
		public static final String CHUNK_DATA_VERSION_TAG = "DataVersion";
		public static final String CHUNK_SECTIONS_TAG     = "Sections";
		public static final String CHUNK_X_POS_TAG        = "xPos";
		public static final String CHUNK_Z_POS_TAG        = "zPos";
		public static final String CHUNK_LAST_UPDATE_TAG  = "LastUpdate";
		public static final String CHUNK_STATUS_TAG       = "Status";
		public static final String CHUNK_ENTITIES_TAG     = "Entities";
		
		public static final String CHUNK_TILE_ENTITIES_TAG  = "TileEntities";
		public static final String CHUNK_TILE_ENTITY_ID_TAG = "id";
		public static final String CHUNK_TILE_ENTITY_X_TAG  = "x";
		public static final String CHUNK_TILE_ENTITY_Y_TAG  = "y";
		public static final String CHUNK_TILE_ENTITY_Z_TAG  = "z";
		
		public static final String CHUNK_SECTION_Y_TAG            = "Y";
		public static final String CHUNK_SECTION_PALETTE_TAG      = "Palette";
		public static final String CHUNK_SECTION_BLOCK_STATES_TAG = "BlockStates";
		public static final String CHUNK_SECTION_BLOCK_LIGHT_TAG  = "BlockLight";
		public static final String CHUNK_SECTION_SKY_LIGHT_TAG    = "SkyLight";
	}
}