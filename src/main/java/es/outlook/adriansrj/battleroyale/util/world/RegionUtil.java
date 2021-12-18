package es.outlook.adriansrj.battleroyale.util.world;

import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.util.nbt.NBTConstants;
import es.outlook.adriansrj.battleroyale.world.RegionFile;
import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author AdrianSR / 14/12/2021 / 11:22 a. m.
 */
public class RegionUtil {
	
	public static EnumDataVersion getRegionDataVersion ( File region_file ) {
		try ( RegionFile handler = new RegionFile ( region_file , true ) ) {
			for ( int x = 0 ; x < 32 ; x++ ) {
				for ( int z = 0 ; z < 32 ; z++ ) {
					if ( handler.containsChunk ( x , z ) ) {
						try ( DataInputStream input = handler.getChunkDataInputStream ( x , z ) ) {
							if ( input != null ) {
								NamedTag tag = new NBTDeserializer ( false ).fromStream ( input );
								
								if ( tag != null && tag.getTag ( ) instanceof CompoundTag ) {
									CompoundTag   compound_tag = ( CompoundTag ) tag.getTag ( );
									CompoundTag   level        = null;
									ListTag < ? > sections     = null;
									
									if ( ( level = compound_tag.getCompoundTag (
											NBTConstants.Pre13.CHUNK_LEVEL_TAG ) ) == null ) {
										level = compound_tag.getCompoundTag ( NBTConstants.Post13.CHUNK_LEVEL_TAG );
									}
									
									if ( level != null && ( sections = level.getListTag (
											NBTConstants.Pre13.CHUNK_SECTIONS_TAG ) ) == null ) {
										sections = level.getListTag ( NBTConstants.Post13.CHUNK_SECTIONS_TAG );
									}
									
									if ( sections != null && sections.size ( ) > 0
											&& CompoundTag.class.isAssignableFrom ( sections.getTypeClass ( ) ) ) {
										for ( CompoundTag section_tag : sections.asCompoundTagList ( ) ) {
											if ( section_tag.containsKey ( NBTConstants.Post13.CHUNK_SECTION_PALETTE_TAG ) ) {
												return EnumDataVersion.v1_13;
											} else {
												return EnumDataVersion.v1_12;
											}
										}
									}
								}
							}
						} catch ( IOException ex ) {
							ex.printStackTrace ( );
						}
					}
				}
			}
		} catch ( IOException ex ) {
			ex.printStackTrace ( );
		}
		return null;
	}
}
