package es.outlook.adriansrj.battleroyale.enums;

/**
 * Enumerates the different world generators.
 *
 * @author AdrianSR / 25/08/2021 / Time: 09:49 a. m.
 */
public enum EnumWorldGenerator {
	
	DEFAULT ( "default" ),
	FLAT ( "flat" ),
	LARGE_BIOMES ( "largeBiomes" ),
	AMPLIFIED ( "amplified" ),
	BUFFET ( "buffet" ),
	DEBUG_ALL_BLOCK_STATES ( "debug_all_block_states" ),
	DEFAULT_1_1 ( "default_1_1" ),
	CUSTOMIZED ( "customized" ),
	
	;
	
	private final String name;
	
	EnumWorldGenerator ( String name ) {
		this.name = name;
	}
	
	public String getName ( ) {
		return name;
	}
}
