package es.outlook.adriansrj.battleroyale.world.region;

import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.util.file.filter.RegionFileFilter;
import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.world.RegionUtil;
import es.outlook.adriansrj.battleroyale.world.chunk.Chunk;
import es.outlook.adriansrj.battleroyale.world.region.v12.Region12;
import es.outlook.adriansrj.battleroyale.world.region.v13.Region13;

import java.io.File;

/**
 * @author AdrianSR / 30/08/2021 / 07:25 p. m.
 */
public interface Region {
	
	String REGION_FILE_NAME_FORMAT = "r.%d.%d." + RegionFileFilter.MCA_EXTENSION;
	
	static Region newRegion ( Location2I location , File file , EnumDataVersion data_version ) {
		if ( data_version.getId ( ) < EnumDataVersion.v1_13.getId ( ) ) {
			return file != null ? new Region12 ( location , file ) : new Region12 ( location );
		} else {
			return file != null ? new Region13 ( location , file ) : new Region13 ( location );
		}
	}
	
	static Region newRegion ( Location2I location , File file ) {
		EnumDataVersion server_data_version = EnumDataVersion.getServerDataVersion ( );
		
		if ( server_data_version.getId ( ) < EnumDataVersion.v1_13.getId ( ) ) {
			// pre1.13 servers will not support post1.13 worlds.
			return newRegion ( location , file , server_data_version );
		} else {
			EnumDataVersion data_version = RegionUtil.getRegionDataVersion ( file );
			
			return newRegion ( location , file , data_version != null ? data_version
					: EnumDataVersion.getServerDataVersion ( ) );
		}
	}
	
	/**
	 * <b>Note that a {@link Region} implementation for this
	 * server version will be returned.</b>
	 *
	 * @param location
	 * @param data_version
	 * @return
	 */
	static Region newRegion ( Location2I location , EnumDataVersion data_version ) {
		return newRegion ( location , null , data_version );
	}
	
	/**
	 * <b>Note that a {@link Region} implementation for this
	 * server version will be returned.</b>
	 *
	 * @param location
	 * @return
	 */
	static Region newRegion ( Location2I location ) {
		return newRegion ( location , EnumDataVersion.getServerDataVersion ( ) );
	}
	
	//	static Region newRegion ( Location2I location , File file , EnumDataVersion data_version ) {
	//		if ( data_version.getId ( ) < EnumDataVersion.v1_13.getId ( ) ) {
	//			return file != null ? new Region12 ( location , file ) : new Region12 ( location );
	//		} else {
	//			return file != null ? new Region13 ( location , file ) : new Region13 ( location );
	//		}
	//	}
	//
	//	static Region newRegion ( Location2I location , File file ) {
	//		return newRegion ( location , file , EnumDataVersion.getServerDataVersion ( ) );
	//	}
	//
	//	static Region newRegion ( Location2I location , EnumDataVersion data_version ) {
	//		return newRegion ( location , null , data_version );
	//	}
	//
	//	static Region newRegion ( Location2I location ) {
	//		return newRegion ( location , EnumDataVersion.getServerDataVersion ( ) );
	//	}
	
	Location2I getLocation ( );
	
	/**
	 * Gets the file this region loads chunks from.
	 *
	 * @return file this region loads chunks from, or <b>null</b>.
	 */
	default File getFile ( ) {
		return null;
	}
	
	Chunk[][] getChunks ( );
	
	Chunk getChunk ( ChunkLocation location );
	
	boolean containsChunk ( ChunkLocation location );
	
	void save ( File region_folder );
}