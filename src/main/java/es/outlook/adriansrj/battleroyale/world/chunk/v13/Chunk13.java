package es.outlook.adriansrj.battleroyale.world.chunk.v13;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.registry.LegacyMapper;
import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.nbt.NBTConstants;
import es.outlook.adriansrj.battleroyale.world.Material;
import es.outlook.adriansrj.battleroyale.world.RegionFile;
import es.outlook.adriansrj.battleroyale.world.block.BlockColor;
import es.outlook.adriansrj.battleroyale.world.block.BlockColorDefault;
import es.outlook.adriansrj.battleroyale.world.block.BlockTileEntity;
import es.outlook.adriansrj.battleroyale.world.block.v13.BlockColorMap13;
import es.outlook.adriansrj.battleroyale.world.chunk.Chunk;
import es.outlook.adriansrj.battleroyale.world.chunk.ChunkHeightmap;
import es.outlook.adriansrj.battleroyale.world.chunk.ChunkSurface;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import net.kyori.adventure.nbt.*;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.apache.commons.lang.Validate;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Chunk for minecraft versions 1.13 - 1.15.
 *
 * @author AdrianSR / 30/08/2021 / 05:43 p. m.
 */
public class Chunk13 implements Chunk {
	
	// 16 sections. 0 - 15 (bottom to top)
	protected final ChunkSection13[]         sections      = new ChunkSection13[ 16 ];
	// tile entities
	protected final List < BlockTileEntity > tile_entities = new ArrayList <> ( );
	
	protected final ChunkLocation  location;
	protected final ChunkHeightmap heightmap;
	protected final ChunkSurface   surface;
	protected       int            data_version;
	protected       long           last_update;
	protected       Chunk13Status  status;
	
	public Chunk13 ( CompoundBinaryTag tag ) {
		CompoundBinaryTag level = tag.getCompound ( NBTConstants.Post13.CHUNK_LEVEL_TAG );
		
		// reading location
		this.location = new ChunkLocation ( level.getInt ( NBTConstants.Post13.CHUNK_X_POS_TAG ) ,
											level.getInt ( NBTConstants.Post13.CHUNK_Z_POS_TAG ) );
		// reading data version
		this.data_version = level.getInt ( NBTConstants.Post13.CHUNK_DATA_VERSION_TAG );
		// reading status
		this.status = EnumReflection.getEnumConstant (
				Chunk13Status.class , tag.getString ( NBTConstants.Post13.CHUNK_STATUS_TAG ) );
		this.status = status == null ? Chunk13Status.POST_PROCESSED : status;
		// reading last update
		this.last_update = level.getLong ( NBTConstants.Post13.CHUNK_LAST_UPDATE_TAG );
		
		// reading sections
		for ( BinaryTag section_tag : level.getList ( NBTConstants.Post13.CHUNK_SECTIONS_TAG ) ) {
			ChunkSection13 section = new ChunkSection13 ( this , ( CompoundBinaryTag ) section_tag );
			
			// between 0 - 15
			sections[ section.y & 0xF ] = section;
		}
		
		// reading tile entities
		for ( BinaryTag tile_entity_tag : level.getList ( NBTConstants.Post13.CHUNK_TILE_ENTITIES_TAG ) ) {
			tile_entities.add ( new BlockTileEntity ( ( CompoundBinaryTag ) tile_entity_tag , EnumDataVersion.v1_13 ) );
		}
		
		// heightmap and surface
		this.heightmap = new ChunkHeightmap ( );
		this.surface   = new ChunkSurface ( this );
	}
	
	public Chunk13 ( CompoundTag tag ) {
		CompoundTag level = tag.getCompoundTag ( NBTConstants.Post13.CHUNK_LEVEL_TAG );
		
		// reading location
		this.location = new ChunkLocation ( level.getNumber ( NBTConstants.Post13.CHUNK_X_POS_TAG ).intValue ( ) ,
											level.getNumber ( NBTConstants.Post13.CHUNK_Z_POS_TAG ).intValue ( ) );
		
		// reading data version
		Number raw_data_version = tag.getNumber ( NBTConstants.Post13.CHUNK_DATA_VERSION_TAG );
		
		if ( raw_data_version != null ) {
			this.data_version = raw_data_version.intValue ( );
		}
		
		// reading status
		this.status = EnumReflection.getEnumConstant (
				Chunk13Status.class , tag.getString ( NBTConstants.Post13.CHUNK_STATUS_TAG ) );
		this.status = status == null ? Chunk13Status.POST_PROCESSED : status;
		// reading last update
		this.last_update = level.getLong ( NBTConstants.Post13.CHUNK_LAST_UPDATE_TAG );
		
		// reading sections
		ListTag < ? > raw_sections = level.getListTag ( NBTConstants.Post13.CHUNK_SECTIONS_TAG );
		
		if ( raw_sections != null && raw_sections.size ( ) > 0
				&& CompoundTag.class.isAssignableFrom ( raw_sections.getTypeClass ( ) ) ) {
			for ( CompoundTag section_tag : raw_sections.asCompoundTagList ( ) ) {
				ChunkSection13 section = new ChunkSection13 ( this , section_tag );
				
				// between 0 - 15
				sections[ section.y & 0xF ] = section;
			}
		}
		
		// reading tile entities
		ListTag < ? > raw_tile_entities = level.getListTag ( NBTConstants.Post13.CHUNK_TILE_ENTITIES_TAG );
		
		if ( raw_tile_entities != null && raw_tile_entities.size ( ) > 0
				&& CompoundTag.class.isAssignableFrom ( raw_tile_entities.getTypeClass ( ) ) ) {
			for ( CompoundTag tile_entity_tag : raw_tile_entities.asCompoundTagList ( ) ) {
				tile_entities.add ( new BlockTileEntity ( tile_entity_tag , EnumDataVersion.v1_13 ) );
			}
		}
		
		// heightmap and surface
		this.heightmap = new ChunkHeightmap ( );
		this.surface   = new ChunkSurface ( this );
	}
	
	public Chunk13 ( ChunkLocation location ) {
		this.location     = location;
		this.data_version = EnumDataVersion.v1_13.getId ( );
		this.status       = Chunk13Status.POST_PROCESSED;
		this.heightmap    = new ChunkHeightmap ( );
		this.surface      = new ChunkSurface ( this );
	}
	
	@Override
	public List < BlockTileEntity > getTileEntities ( ) {
		return tile_entities;
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
				for ( int y = 255 ; y >= 0 ; y-- ) {
					ChunkSection13 section  = getSectionFromYCoordinate ( y );
					Material       material = section.getMaterial ( x , y & 15 , z );
					
					if ( !material.isEmpty ( ) ) {
						heightmap.setHeight ( x , z , y );
						break;
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
					ChunkSection13 section     = getSectionFromYCoordinate ( y );
					Material       material    = section.getMaterial ( x , y & 0xF , z );
					BlockColor     block_color = BlockColorMap13.INSTANCE.getColor ( material.getNamespacedId ( ) );
					
					if ( block_color == BlockColorDefault.AIR ) {
						for ( ; y > 0 ; y-- ) {
							section = getSectionFromYCoordinate ( y );
							
							if ( ( block_color = BlockColorMap13.INSTANCE.getColor (
									section.getMaterial ( x , y & 0xF , z )
											.getNamespacedId ( ) ) ) != BlockColorDefault.AIR ) {
								break;
							}
						}
					}
					
					if ( block_color != BlockColorDefault.AIR ) {
						surface.setColor ( x , z , block_color );
						
						// depth effect disabled for now as is not working properly
						//						// depth effect on water surfaces.
						//						if ( block_color == BlockColorDefault.WATER ) {
						//							// here we're implementing a depth effect on water surfaces.
						//							int depth = 1;
						//							y -= 1;
						//
						//							for ( ; y >= 0 ; y-- ) {
						//								section = getSectionFromYCoordinate ( y );
						//
						//								if ( ( block_color = BlockColorMap13.INSTANCE.getColor (
						//										section.getMaterial ( x , y & 0xF , z )
						//												.getNamespacedId ( ) ) ) != BlockColorDefault.WATER ) {
						//									break;
						//								}
						//
						//								depth += 1;
						//							}
						//
						//							float   alpha  = Math.max ( 0.0F , Math.min ( depth / 32.0F , 1.0F ) );
						//							float[] colors = block_color.getColor ( ).getRGBComponents ( null );
						//
						//							Color background = new Color ( 100.0F / 255.0F , 0.8F , 1.0F );
						//							Color color      = new Color ( colors[ 0 ] , colors[ 1 ] , colors[ 2 ] ,
						//							alpha );
						//
						//							int r = color.getRed ( ) * color.getAlpha ( ) + background.getRed ( )
						//									* ( 255 - color.getAlpha ( ) );
						//							int g = color.getGreen ( ) * color.getAlpha ( ) + background.getGreen ( )
						//									* ( 255 - color.getAlpha ( ) );
						//							int b = color.getBlue ( ) * color.getAlpha ( ) + background.getBlue ( )
						//									* ( 255 - color.getAlpha ( ) );
						//
						//							float[] blend = ColorUtil.blend (
						//									new float[] { r / 255.0F , g / 255.0F , b / 255.0F , 1.0F } ,
						//									new float[] { colors[ 0 ] , colors[ 1 ] , colors[ 2 ] , alpha } );
						//
						//							surface.setColor ( x , z , new BlockColorCustom (
						//									new Color ( blend[ 0 ] / 255 , blend[ 1 ] / 255 ,
						//												blend[ 2 ] / 255 , blend[ 3 ] ).getRGB ( ) ) );
						//						} else {
						//							surface.setColor ( x , z , block_color );
						//						}
					} else {
						// transparency
						surface.setColor ( x , z , BlockColorDefault.AIR );
					}
				}
			}
		}
	}
	
	public ChunkSection13[] getSections ( ) {
		return sections;
	}
	
	public ChunkSection13 getSection ( int y ) {
		int            index   = y & ( sections.length - 1 );
		ChunkSection13 section = sections[ index ];
		
		if ( section == null ) {
			sections[ index ] = ( section = new ChunkSection13 ( this , index ) );
		}
		
		return section;
	}
	
	public ChunkSection13 getSectionFromYCoordinate ( int y ) {
		return getSection ( ( y >> 4 ) & ( sections.length - 1 ) );
	}
	
	public boolean containsSection ( int y ) {
		return sections[ y & ( sections.length - 1 ) ] != null;
	}
	
	public long getLastUpdate ( ) {
		return last_update;
	}
	
	public Chunk13Status getStatus ( ) {
		return status;
	}
	
	public void setLastUpdate ( long last_update ) {
		this.last_update = last_update;
	}
	
	public void setStatus ( Chunk13Status status ) {
		Validate.notNull ( status , "status cannot be null" );
		
		this.status = status;
	}
	
	public Material getMaterial ( int x , int y , int z ) {
		return getSectionFromYCoordinate ( y ).getMaterial ( x & 0xF , y & 0xF , z & 0xF );
	}
	
	public void setMaterial ( int x , int y , int z , Material material ) {
		getSectionFromYCoordinate ( y ).setMaterial ( x & 0xF , y & 0xF , z & 0xF , material );
	}
	
	@SuppressWarnings ( "deprecation" )
	public int getBlockIDAt ( int x , int y , int z ) {
		Material material = getMaterial ( x , y , z );
		
		if ( material != null && !material.isEmpty ( ) ) {
			BlockState state = getBlockStateFrom ( material );
			
			if ( state != null && state.getBlockType ( ) != null ) {
				return state.getBlockType ( ).getLegacyId ( );
			}
		}
		return 0;
	}
	
	@SuppressWarnings ( "deprecation" )
	public int getBlockDataAt ( int x , int y , int z ) {
		Material material = getMaterial ( x , y , z );
		
		if ( material != null && !material.isEmpty ( ) ) {
			BlockState state = getBlockStateFrom ( material );
			
			if ( state != null && state.getBlockType ( ) != null ) {
				return state.getBlockType ( ).getLegacyData ( );
			}
		}
		return 0;
	}
	
	protected BlockState getBlockStateFrom ( Material material ) {
		ParserContext context = new ParserContext ( );
		context.setRestricted ( false );
		context.setTryLegacy ( false );
		context.setPreferringWildcard ( false );
		
		try {
			return WorldEdit.getInstance ( ).getBlockFactory ( )
					.parseFromInput ( material.getNamespacedId ( ) , context ).toImmutableState ( );
		} catch ( InputParseException e ) {
			e.printStackTrace ( );
		}
		return null;
	}
	
	public void setBlockAt ( int x , int y , int z , int id , byte data ) {
		BlockState state = LegacyMapper.getInstance ( ).getBlockFromLegacy ( id , data );
		BaseBlock  block = state != null ? state.toBaseBlock ( ) : null;
		
		if ( block != null ) {
			setBlockAt ( x , y , z , block );
		}
	}
	
	public void setBlockAt ( int x , int y , int z , String namespaced_id , Map < String, String > properties ) {
		setMaterial ( x , y , z , new Material ( namespaced_id , properties ) );
	}
	
	public void setBlockAt ( int x , int y , int z , String namespaced_id ) {
		setBlockAt ( x , y , z , namespaced_id , null );
	}
	
	public void setBlockAt ( int x , int y , int z , BaseBlock block ) {
		setMaterial ( x , y , z , Material.from ( block ) );
	}
	
	@Override
	public CompoundBinaryTag toNBT ( ) {
		Map < String, BinaryTag > root  = new HashMap <> ( );
		Map < String, BinaryTag > level = new HashMap <> ( );
		
		// sections
		level.put ( NBTConstants.Post13.CHUNK_SECTIONS_TAG , ListBinaryTag.from (
				Arrays.stream ( this.sections )
						.filter ( Objects :: nonNull )
						.filter ( section -> !section.isEmpty ( ) )
						.map ( ChunkSection13 :: toNBT )
						.collect ( Collectors.toList ( ) ) ) );
		
		// tile entities
		level.put ( NBTConstants.Post13.CHUNK_TILE_ENTITIES_TAG , ListBinaryTag.from (
				this.tile_entities.stream ( )
						.filter ( Objects :: nonNull )
						.map ( BlockTileEntity :: toNBT )
						.collect ( Collectors.toList ( ) ) ) );
		
		level.put ( NBTConstants.Post13.CHUNK_X_POS_TAG , IntBinaryTag.of ( location.getX ( ) ) );
		level.put ( NBTConstants.Post13.CHUNK_Z_POS_TAG , IntBinaryTag.of ( location.getZ ( ) ) );
		
		if ( last_update > 0 ) {
			level.put ( NBTConstants.Post13.CHUNK_LAST_UPDATE_TAG , LongBinaryTag.of ( last_update ) );
		}
		
		level.put ( NBTConstants.Post13.CHUNK_STATUS_TAG , StringBinaryTag.of ( status.getName ( ) ) );
		level.put ( NBTConstants.Post13.CHUNK_ENTITIES_TAG , ListBinaryTag.empty ( ) );
		
		root.put ( NBTConstants.Post13.CHUNK_LEVEL_TAG , CompoundBinaryTag.from ( level ) );
		root.put ( NBTConstants.Post13.CHUNK_DATA_VERSION_TAG , IntBinaryTag.of ( data_version ) );
		
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