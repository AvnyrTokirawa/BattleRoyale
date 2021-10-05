package es.outlook.adriansrj.battleroyale.world.region;

import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.world.chunk.Chunk;
import es.outlook.adriansrj.battleroyale.world.region.v12.Region12;
import es.outlook.adriansrj.battleroyale.world.region.v13.Region13;

import java.io.File;

/**
 * @author AdrianSR / 30/08/2021 / 07:25 p. m.
 */
public interface Region {
	
	public static final String REGION_FILE_NAME_FORMAT = "r.%d.%d.mca";
	
	public static Region newRegion ( Location2I location , File file , EnumDataVersion data_version ) {
		if ( data_version.getId ( ) < EnumDataVersion.v1_13.getId ( ) ) {
			return file != null ? new Region12 ( location , file ) : new Region12 ( location );
		} else {
			return file != null ? new Region13 ( location , file ) : new Region13 ( location );
		}
	}
	
	public static Region newRegion ( Location2I location , File file ) {
		return newRegion ( location , file , EnumDataVersion.getServerDataVersion ( ) );
	}
	
	public static Region newRegion ( Location2I location , EnumDataVersion data_version ) {
		return newRegion ( location , null , data_version );
	}
	
	public static Region newRegion ( Location2I location ) {
		return newRegion ( location , EnumDataVersion.getServerDataVersion ( ) );
	}
	
	public Location2I getLocation ( );
	
	public Chunk[][] getChunks ( );
	
	public Chunk getChunk ( ChunkLocation location );
	
	public boolean containsChunk ( ChunkLocation location );
	
	public void save ( File region_folder );
}