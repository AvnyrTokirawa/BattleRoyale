package es.outlook.adriansrj.battleroyale.world.chunk.v13;

/**
 * @author AdrianSR / 30/08/2021 / 08:02 p. m.
 */
public enum Chunk13Status {
	
	POST_PROCESSED ( "postprocessed" ),
	EMPTY ( "empty" ),
	STRUCTURE_STARTS ( "structure_starts" ),
	STRUCTURE_REFERENCES ( "structure_starts" ),
	BIOMES ( "biomes" ),
	NOISE ( "noise" ),
	SURFACE ( "surface" ),
	CARVERS ( "carvers" ),
	LIQUID_CARVERS ( "liquid_carvers" ),
	FEATURES ( "features" ),
	LIGHT ( "light" ),
	SPAWN ( "spawn" ),
	HEIGHTMAPS ( "heightmaps" ),
	FULL ( "full" ),
	;
	
	protected final String name;
	
	Chunk13Status ( String name ) {
		this.name = name;
	}
	
	public String getName ( ) {
		return name;
	}
}