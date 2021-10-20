package es.outlook.adriansrj.battleroyale.world.chunk.v12;

import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.util.ColorUtil;
import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.nbt.NBTConstants;
import es.outlook.adriansrj.battleroyale.world.RegionFile;
import es.outlook.adriansrj.battleroyale.world.block.BlockColor;
import es.outlook.adriansrj.battleroyale.world.block.BlockColorCustom;
import es.outlook.adriansrj.battleroyale.world.block.BlockColorDefault;
import es.outlook.adriansrj.battleroyale.world.block.v12.BlockColorMap12;
import es.outlook.adriansrj.battleroyale.world.chunk.Chunk;
import es.outlook.adriansrj.battleroyale.world.chunk.ChunkHeightmap;
import es.outlook.adriansrj.battleroyale.world.chunk.ChunkSurface;
import net.kyori.adventure.nbt.*;

import java.awt.*;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chunk for minecraft versions 1.9 - 1.12.2.
 *
 * @author AdrianSR / 30/08/2021 / 05:43 p. m.
 */
public class Chunk12 implements Chunk {
	
	// 16 sections. 0 - 15 (bottom to top)
	protected final ChunkSection12[] sections = new ChunkSection12[ 16 ];
	
	protected final ChunkLocation  location;
	protected final ChunkHeightmap heightmap;
	protected final ChunkSurface   surface;
	protected       int            data_version;
	protected       long           last_update;
	protected       boolean        terrain_populated;
	protected       boolean        light_populated;
	
	public Chunk12 ( CompoundBinaryTag tag ) {
		CompoundBinaryTag level = tag.getCompound ( NBTConstants.Pre13.CHUNK_LEVEL_TAG );
		
		// reading location
		this.location = new ChunkLocation ( level.getInt ( NBTConstants.Pre13.CHUNK_X_POS_TAG ) ,
											level.getInt ( NBTConstants.Pre13.CHUNK_Z_POS_TAG ) );
		// reading data version
		this.data_version = level.getInt ( NBTConstants.Pre13.CHUNK_DATA_VERSION_TAG );
		// reading last update
		this.last_update = level.getLong ( NBTConstants.Pre13.CHUNK_LAST_UPDATE_TAG );
		
		// reading sections
		for ( BinaryTag section_tag : level.getList ( NBTConstants.Pre13.CHUNK_SECTIONS_TAG ) ) {
			ChunkSection12 section = new ChunkSection12 ( this , ( CompoundBinaryTag ) section_tag );
			
			// between 0 - 15
			sections[ section.y & 0xF ] = section;
		}
		
		// heightmap and surface
		this.heightmap = new ChunkHeightmap ( );
		this.surface   = new ChunkSurface ( this );
	}
	
	public Chunk12 ( ChunkLocation location ) {
		this.location     = location;
		this.data_version = EnumDataVersion.v1_12.getId ( );
		this.heightmap    = new ChunkHeightmap ( );
		this.surface      = new ChunkSurface ( this );
	}
	
	@Override
	public ChunkLocation getLocation ( ) {
		return location;
	}
	
	@Override
	public ChunkHeightmap getHeightmap ( ) {
		return heightmap;
	}
	
	@Override
	public ChunkSurface getSurface ( ) {
		return surface;
	}
	
	@Override
	public void recalculateHeightmap ( ) {
		for ( int x = 0 ; x < 16 ; x++ ) {
			for ( int z = 0 ; z < 16 ; z++ ) {
				section:
				for ( int si = sections.length - 1 ; si > 0 ; si-- ) {
					ChunkSection12 section = sections[ si ];
					int            base_y  = si << 4; // section min y
					
					if ( section != null && !section.isEmpty ( ) ) {
						for ( int y = 15 ; y > 0 ; y-- ) {
							if ( section.getBlockId ( x , y , z ) > 0 ) {
								heightmap.setHeight ( x , z , base_y + y );
								break section;
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void recalculateSurface ( ) {
		for ( int x = 0 ; x < 16 ; x++ ) {
			for ( int z = 0 ; z < 16 ; z++ ) {
				int y = heightmap.getHeight ( x , z );
				
				if ( containsSection ( y >> 4 ) ) {
					ChunkSection12 section     = getSectionFromYCoordinate ( y );
					int            block_id    = section.getBlockId ( x , y & 0xF , z );
					byte           block_data  = section.getBlockData ( x , y & 0xF , z );
					BlockColor     block_color = BlockColorMap12.INSTANCE.getColor ( block_id , block_data );
					
					if ( block_color == BlockColorDefault.AIR ) {
						for ( ; y > 0 ; y-- ) {
							section = getSectionFromYCoordinate ( y );
							
							if ( ( block_color = BlockColorMap12.INSTANCE.getColor (
									section.getBlockId ( x , y & 0xF , z ) ,
									section.getBlockData ( x , y & 0xF , z ) ) ) != BlockColorDefault.AIR ) {
								break;
							}
						}
					}
					
					if ( block_color != BlockColorDefault.AIR ) {
						// depth effect on water surfaces.
						if ( block_color == BlockColorDefault.WATER ) {
							// here we're implementing a depth effect on water surfaces.
							int depth = 1;
							y -= 1;
							
							for ( ; y >= 0 ; y-- ) {
								section = getSectionFromYCoordinate ( y );
								
								if ( ( block_color = BlockColorMap12.INSTANCE.getColor (
										section.getBlockId ( x , y & 0xF , z ) ) ) != BlockColorDefault.WATER ) {
									break;
								}
								
								depth += 1;
							}
							
							float   alpha  = Math.max ( 0.0F , Math.min ( depth / 32.0F , 1.0F ) );
							float[] colors = block_color.getColor ( ).getRGBComponents ( null );
							
							Color background = new Color ( 100.0F / 255.0F , 0.8F , 1.0F );
							Color color      = new Color ( colors[ 0 ] , colors[ 1 ] , colors[ 2 ] , alpha );
							
							int r = color.getRed ( ) * color.getAlpha ( ) + background.getRed ( )
									* ( 255 - color.getAlpha ( ) );
							int g = color.getGreen ( ) * color.getAlpha ( ) + background.getGreen ( )
									* ( 255 - color.getAlpha ( ) );
							int b = color.getBlue ( ) * color.getAlpha ( ) + background.getBlue ( )
									* ( 255 - color.getAlpha ( ) );
							
							float[] blend = ColorUtil.blend (
									new float[] { r / 255.0F , g / 255.0F , b / 255.0F , 1.0F } ,
									new float[] { colors[ 0 ] , colors[ 1 ] , colors[ 2 ] , alpha } );
							
							surface.setColor ( x , z , new BlockColorCustom (
									new Color ( blend[ 0 ] / 255 , blend[ 1 ] / 255 ,
												blend[ 2 ] / 255 , blend[ 3 ] ).getRGB ( ) ) );
						} else {
							surface.setColor ( x , z , block_color );
						}
					} else {
						// transparency
						surface.setColor ( x , z , BlockColorDefault.AIR );
					}
				}
			}
		}
	}
	
	public ChunkSection12[] getSections ( ) {
		return sections;
	}
	
	public ChunkSection12 getSection ( int y ) {
		int            index   = y & ( sections.length - 1 );
		ChunkSection12 section = sections[ index ];
		
		if ( section == null ) {
			sections[ index ] = ( section = new ChunkSection12 ( this , index ) );
		}
		
		return section;
	}
	
	public ChunkSection12 getSectionFromYCoordinate ( int y ) {
		return getSection ( ( y >> 4 ) & ( sections.length - 1 ) );
	}
	
	public boolean containsSection ( int y ) {
		return sections[ y & ( sections.length - 1 ) ] != null;
	}
	
	public long getLastUpdate ( ) {
		return last_update;
	}
	
	public boolean isTerrainPopulated ( ) {
		return terrain_populated;
	}
	
	public boolean isLightPopulated ( ) {
		return light_populated;
	}
	
	public void setLastUpdate ( long last_update ) {
		this.last_update = last_update;
	}
	
	public void setTerrainPopulated ( boolean terrain_populated ) {
		this.terrain_populated = terrain_populated;
	}
	
	public void setLightPopulated ( boolean light_populated ) {
		this.light_populated = light_populated;
	}
	
	public byte getBlock ( int x , int y , int z ) {
		return getSectionFromYCoordinate ( y ).getBlock ( x & 0xF , y & 0xF , z & 0xF );
	}
	
	public int getBlockId ( int x , int y , int z ) {
		return getSectionFromYCoordinate ( y ).getBlockId ( x & 0xF , y & 0xF , z & 0xF );
	}
	
	public int getBlockData ( int x , int y , int z ) {
		return getSectionFromYCoordinate ( y ).getBlockData ( x & 0xF , y & 0xF , z & 0xF );
	}
	
	public byte getBlockAdd ( int x , int y , int z ) {
		return getSectionFromYCoordinate ( y ).getBlockAdd ( x & 0xF , y & 0xF , z & 0xF );
	}
	
	public void setBlock ( int x , int y , int z , byte block ) {
		getSectionFromYCoordinate ( y ).setBlock ( x & 0xF , y & 0xF , z & 0xF , block );
	}
	
	public void setBlockId ( int x , int y , int z , int id ) {
		getSectionFromYCoordinate ( y ).setBlockId ( x & 0xF , y & 0xF , z & 0xF , id );
	}
	
	public void setBlockAdd ( int x , int y , int z , byte add ) {
		getSectionFromYCoordinate ( y ).setBlockAdd ( x & 0xF , y & 0xF , z & 0xF , add );
	}
	
	public void setBlockData ( int x , int y , int z , byte data ) {
		getSectionFromYCoordinate ( y ).setBlockData ( x & 0xF , y & 0xF , z & 0xF , data );
	}
	
	@Override
	public CompoundBinaryTag toNBT ( ) {
		Map < String, BinaryTag > root  = new HashMap <> ( );
		Map < String, BinaryTag > level = new HashMap <> ( );
		
		// sections
		List < CompoundBinaryTag > sections = new ArrayList <> ( );
		
		for ( ChunkSection12 section : this.sections ) {
			if ( section != null && !section.isEmpty ( ) ) {
				sections.add ( section.toNBT ( ) );
			}
		}
		
		level.put ( NBTConstants.Pre13.CHUNK_SECTIONS_TAG , ListBinaryTag.from ( sections ) );
		level.put ( NBTConstants.Pre13.CHUNK_X_POS_TAG , IntBinaryTag.of ( location.getX ( ) ) );
		level.put ( NBTConstants.Pre13.CHUNK_Z_POS_TAG , IntBinaryTag.of ( location.getZ ( ) ) );
		level.put ( NBTConstants.Pre13.CHUNK_LAST_UPDATE_TAG , LongBinaryTag.of ( last_update ) );
		level.put ( NBTConstants.Pre13.CHUNK_TERRAIN_POPULATED_TAG ,
					ByteBinaryTag.of ( ( byte ) ( terrain_populated ? 1 : 0 ) ) );
		level.put ( NBTConstants.Pre13.CHUNK_LIGHT_POPULATED_TAG ,
					ByteBinaryTag.of ( ( byte ) ( light_populated ? 1 : 0 ) ) );
		level.put ( NBTConstants.Pre13.CHUNK_ENTITIES_TAG , ListBinaryTag.empty ( ) );
		level.put ( NBTConstants.Pre13.CHUNK_TILE_ENTITIES_TAG , ListBinaryTag.empty ( ) );
		
		root.put ( NBTConstants.Pre13.CHUNK_LEVEL_TAG , CompoundBinaryTag.from ( level ) );
		root.put ( NBTConstants.Pre13.CHUNK_DATA_VERSION_TAG , IntBinaryTag.of ( data_version ) );
		
		return CompoundBinaryTag.from ( root );
	}
	
	@Override
	public void write ( RegionFile region_file ) {
		DataOutputStream out = region_file.getChunkDataOutputStream (
				location.getX ( ) & 31 ,
				location.getZ ( ) & 31 );
		
		try {
			BinaryTagIO.writer ( ).write ( toNBT ( ) , ( DataOutput ) out );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		} finally {
			try {
				// the corresponding chunk buffer will save
				// the changes at the moment of closing it.
				out.close ( );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
}