package es.outlook.adriansrj.battleroyale.world.chunk.v13;

import es.outlook.adriansrj.battleroyale.util.nbt.NBTConstants;
import es.outlook.adriansrj.battleroyale.util.nbt.NBTSerializable;
import es.outlook.adriansrj.battleroyale.world.Material;
import net.kyori.adventure.nbt.*;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;

import java.util.*;

/**
 * @author AdrianSR / 30/08/2021 / 07:59 p. m.
 */
public class ChunkSection13 implements NBTSerializable {
	
	protected final Material[] materials   = new Material[ 16 * 16 * 16 ];
	protected final byte[]     block_light = new byte[ 128 * 16 ];
	protected final byte[]     sky_light   = new byte[ 128 * 16 ];
	
	protected final Chunk13 chunk;
	protected final int     y;
	
	public ChunkSection13 ( Chunk13 chunk , int y ) {
		this.chunk = chunk;
		this.y     = y & 0xF; // between 0 - 15 (bottom to top)
		
		// full light everywhere
		Arrays.fill ( sky_light , ( byte ) 0xFF );
	}
	
	public ChunkSection13 ( Chunk13 chunk , CompoundBinaryTag tag ) {
		this ( chunk , tag.getInt ( NBTConstants.Post13.CHUNK_SECTION_Y_TAG ) & 0xF );
		
		// reading blocks light
		byte[] block_light = tag.getByteArray ( NBTConstants.Post13.CHUNK_SECTION_BLOCK_LIGHT_TAG );
		System.arraycopy ( block_light , 0 , this.block_light , 0 , block_light.length );
		// reading skylight
		byte[] sky_light = tag.getByteArray ( NBTConstants.Post13.CHUNK_SECTION_SKY_LIGHT_TAG );
		System.arraycopy ( sky_light , 0 , this.sky_light , 0 , sky_light.length );
		
		// reading palette
		long[] block_states = tag.getLongArray (
				NBTConstants.Post13.CHUNK_SECTION_BLOCK_STATES_TAG );
		List < CompoundBinaryTag > palette = new ArrayList <> ( );
		
		for ( BinaryTag palette_tag : tag.getList ( "Palette" ) ) {
			if ( palette_tag instanceof CompoundBinaryTag ) {
				palette.add ( ( CompoundBinaryTag ) palette_tag );
			}
		}
		
		Material[] palette_materials = new Material[ palette.size ( ) ];
		
		for ( int i = 0 ; i < palette_materials.length ; i++ ) {
			palette_materials[ i ] = getPaletteMaterial ( palette.get ( i ) );
		}
		
		if ( palette_materials.length > 0 ) {
			readPalette ( block_states , palette_materials );
		}
	}
	
	public ChunkSection13 ( Chunk13 chunk , CompoundTag tag ) {
		this ( chunk , tag.getNumber ( NBTConstants.Post13.CHUNK_SECTION_Y_TAG ).byteValue ( ) & 0xF );
		
		// reading blocks light
		byte[] block_light = tag.getByteArray ( NBTConstants.Post13.CHUNK_SECTION_BLOCK_LIGHT_TAG );
		System.arraycopy ( block_light , 0 , this.block_light , 0 , block_light.length );
		// reading skylight
		byte[] sky_light = tag.getByteArray ( NBTConstants.Post13.CHUNK_SECTION_SKY_LIGHT_TAG );
		System.arraycopy ( sky_light , 0 , this.sky_light , 0 , sky_light.length );
		
		// reading palette
		long[] block_states = tag.getLongArray (
				NBTConstants.Post13.CHUNK_SECTION_BLOCK_STATES_TAG );
		ListTag < ? >        raw_palette = tag.getListTag ( "Palette" );
		List < CompoundTag > palette     = new ArrayList <> ( );
		
		if ( raw_palette != null ) {
			for ( CompoundTag palette_tag : raw_palette.asCompoundTagList ( ) ) {
				palette.add ( palette_tag );
			}
		}
		
		Material[] palette_materials = new Material[ palette.size ( ) ];
		
		for ( int i = 0 ; i < palette_materials.length ; i++ ) {
			palette_materials[ i ] = getPaletteMaterial ( palette.get ( i ) );
		}
	
		if ( palette_materials.length > 0 ) {
			readPalette ( block_states , palette_materials );
		}
	}
	
	public int getY ( ) {
		return y;
	}
	
	public Material[] getMaterials ( ) {
		return materials;
	}
	
	public byte[] getBlockLight ( ) {
		return block_light;
	}
	
	public byte[] getSkyLight ( ) {
		return sky_light;
	}
	
	public boolean isEmpty ( ) {
		for ( int x = 0 ; x < 16 ; x++ ) {
			for ( int y = 0 ; y < 16 ; y++ ) {
				for ( int z = 0 ; z < 16 ; z++ ) {
					Material material = getMaterial ( x , y , z );
					
					if ( material != null && !material.isEmpty ( ) ) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public Material getMaterial ( int x , int y , int z ) {
		Material material = materials[ blockOffset ( x , y , z ) ];
		
		return material != null ? material : Material.AIR;
	}
	
	public void setMaterial ( int x , int y , int z , Material material ) {
		materials[ blockOffset ( x , y , z ) ] = material != null ? ( material.isEmpty ( ) ? null : material ) : null;
	}
	
	protected int blockOffset ( int x , int y , int z ) {
		return x | ( ( z | ( ( y & 0xF ) << 4 ) ) << 4 );
	}
	
	public void setMaterials ( Material[] materials ) {
		if ( materials.length == this.materials.length ) {
			System.arraycopy ( materials , 0 , this.materials , 0 , materials.length );
		} else {
			throw new IllegalArgumentException (
					"materials array length must be: " + this.materials.length );
		}
	}
	
	public void setBlocksLight ( byte[] blocks_light ) {
		if ( blocks_light.length == this.block_light.length ) {
			System.arraycopy ( blocks_light , 0 , this.block_light , 0 , blocks_light.length );
		} else {
			throw new IllegalArgumentException (
					"blocks light array length must be: " + this.block_light.length );
		}
	}
	
	public void setSkyLight ( byte[] sky_light ) {
		if ( sky_light.length == this.sky_light.length ) {
			System.arraycopy ( sky_light , 0 , this.sky_light , 0 , sky_light.length );
		} else {
			throw new IllegalArgumentException (
					"sky light array length must be: " + this.sky_light.length );
		}
	}
	
	@Override
	public CompoundBinaryTag toNBT ( ) {
		Map < String, BinaryTag > contents = new HashMap <> ( );
		
		contents.put ( "Y" , ByteBinaryTag.of ( ( byte ) y ) );
		
		// - WorldPainter start | pepijn
		// Create the palette. We have to do this first, because otherwise
		// we don't know how many bits the indices will be and therefore how
		// big to make the blockStates array
		Map < Material, Integer > reversePalette = new HashMap <> ( );
		List < Material >         palette        = new LinkedList <> ( );
		
		for ( Material material : materials ) {
			if ( material == null ) {
				material = Material.AIR;
			}
			
			if ( !reversePalette.containsKey ( material ) ) {
				reversePalette.put ( material , palette.size ( ) );
				palette.add ( material );
			}
		}
		
		List < CompoundBinaryTag > palette_list = new ArrayList <> ( palette.size ( ) );
		
		for ( Material material : palette ) {
			palette_list.add ( material.toNBT ( ) );
		}
		
		contents.put ( "Palette" , ListBinaryTag.from ( palette_list ) );
		
		// Create the blockStates array and fill it, using the appropriate
		// length palette indices so that it just fits
		int paletteIndexSize = Math.max ( ( int ) Math.ceil ( Math.log ( palette.size ( ) ) / Math.log ( 2 ) ) , 4 );
		
		if ( paletteIndexSize == 4 ) {
			// Optimised special case
			long[] blockStates = new long[ 256 ];
			
			for ( int i = 0 ; i < 4096 ; i += 16 ) {
				blockStates[ i >> 4 ] =
						reversePalette.get ( materials[ i ] != null ? materials[ i ] : Material.AIR )
								| ( reversePalette.get (
								materials[ i + 1 ] != null ? materials[ i + 1 ] : Material.AIR ) << 4 )
								| ( reversePalette.get (
								materials[ i + 2 ] != null ? materials[ i + 2 ] : Material.AIR ) << 8 )
								| ( reversePalette.get (
								materials[ i + 3 ] != null ? materials[ i + 3 ] : Material.AIR ) << 12 )
								| ( reversePalette.get (
								materials[ i + 4 ] != null ? materials[ i + 4 ] : Material.AIR ) << 16 )
								| ( reversePalette.get (
								materials[ i + 5 ] != null ? materials[ i + 5 ] : Material.AIR ) << 20 )
								| ( reversePalette.get (
								materials[ i + 6 ] != null ? materials[ i + 6 ] : Material.AIR ) << 24 )
								| ( ( long ) ( reversePalette.get (
								materials[ i + 7 ] != null ? materials[ i + 7 ] : Material.AIR ) ) << 28 )
								| ( ( long ) ( reversePalette.get (
								materials[ i + 8 ] != null ? materials[ i + 8 ] : Material.AIR ) ) << 32 )
								| ( ( long ) ( reversePalette.get (
								materials[ i + 9 ] != null ? materials[ i + 9 ] : Material.AIR ) ) << 36 )
								| ( ( long ) ( reversePalette.get (
								materials[ i + 10 ] != null ? materials[ i + 10 ] : Material.AIR ) ) << 40 )
								| ( ( long ) ( reversePalette.get (
								materials[ i + 11 ] != null ? materials[ i + 11 ] : Material.AIR ) ) << 44 )
								| ( ( long ) ( reversePalette.get (
								materials[ i + 12 ] != null ? materials[ i + 12 ] : Material.AIR ) ) << 48 )
								| ( ( long ) ( reversePalette.get (
								materials[ i + 13 ] != null ? materials[ i + 13 ] : Material.AIR ) ) << 52 )
								| ( ( long ) ( reversePalette.get (
								materials[ i + 14 ] != null ? materials[ i + 14 ] : Material.AIR ) ) << 56 )
								| ( ( long ) ( reversePalette.get (
								materials[ i + 15 ] != null ? materials[ i + 15 ] : Material.AIR ) ) << 60 );
			}
			
			contents.put ( "BlockStates" , LongArrayBinaryTag.of ( blockStates ) );
		} else {
			BitSet blockStates = new BitSet ( 4096 * paletteIndexSize );
			
			for ( int i = 0 ; i < 4096 ; i++ ) {
				int offset = i * paletteIndexSize;
				int index  = reversePalette.get ( materials[ i ] != null ? materials[ i ] : Material.AIR );
				
				for ( int j = 0 ; j < paletteIndexSize ; j++ ) {
					if ( ( index & ( 1 << j ) ) != 0 ) {
						blockStates.set ( offset + j );
					}
				}
			}
			
			long[] blockStatesArray = blockStates.toLongArray ( );
			
			// Pad with zeros if necessary
			int requiredLength = 64 * paletteIndexSize;
			
			if ( blockStatesArray.length != requiredLength ) {
				long[] expandedArray = new long[ requiredLength ];
				
				System.arraycopy ( blockStatesArray , 0 , expandedArray , 0 , blockStatesArray.length );
				
				contents.put ( "BlockStates" , LongArrayBinaryTag.of ( expandedArray ) );
			} else {
				contents.put ( "BlockStates" , LongArrayBinaryTag.of ( blockStatesArray ) );
			}
		}
		// - WorldPainter end | pepijn
		
		contents.put ( "BlockLight" , ByteArrayBinaryTag.of ( this.block_light ) );
		contents.put ( "SkyLight" , ByteArrayBinaryTag.of ( this.sky_light ) );
		
		return CompoundBinaryTag.from ( contents );
	}
	
	// ------- utils
	
	private void readPalette ( long[] block_states , Material[] palette_materials ) {
		int wordSize = Math.max ( 4 , ( int ) Math.ceil (
				Math.log ( palette_materials.length ) / Math.log ( 2 ) ) );
		int blockStateArrayLengthInBytes               = block_states.length * 8;
		int expectedPackedBlockStateArrayLengthInBytes = wordSize * 512;
		
		if ( wordSize == 4 ) {
			// optimised special case
			for ( int w = 0 ; w < materials.length ; w += 16 ) {
				final long data = block_states[ w >> 4 ];
				
				materials[ w ]      = palette_materials[ ( int ) ( data & 0xf ) ];
				materials[ w + 1 ]  = palette_materials[ ( int ) ( ( data & 0xf0 ) >> 4 ) ];
				materials[ w + 2 ]  = palette_materials[ ( int ) ( ( data & 0xf00 ) >> 8 ) ];
				materials[ w + 3 ]  = palette_materials[ ( int ) ( ( data & 0xf000 ) >> 12 ) ];
				materials[ w + 4 ]  = palette_materials[ ( int ) ( ( data & 0xf0000 ) >> 16 ) ];
				materials[ w + 5 ]  = palette_materials[ ( int ) ( ( data & 0xf00000 ) >> 20 ) ];
				materials[ w + 6 ]  = palette_materials[ ( int ) ( ( data & 0xf000000 ) >> 24 ) ];
				materials[ w + 7 ]  = palette_materials[ ( int ) ( ( data & 0xf0000000L ) >> 28 ) ];
				materials[ w + 8 ]  = palette_materials[ ( int ) ( ( data & 0xf00000000L ) >> 32 ) ];
				materials[ w + 9 ]  = palette_materials[ ( int ) ( ( data & 0xf000000000L ) >> 36 ) ];
				materials[ w + 10 ] = palette_materials[ ( int ) ( ( data & 0xf0000000000L ) >> 40 ) ];
				materials[ w + 11 ] = palette_materials[ ( int ) ( ( data & 0xf00000000000L ) >> 44 ) ];
				materials[ w + 12 ] = palette_materials[ ( int ) ( ( data & 0xf000000000000L ) >> 48 ) ];
				materials[ w + 13 ] = palette_materials[ ( int ) ( ( data & 0xf0000000000000L ) >> 52 ) ];
				materials[ w + 14 ] = palette_materials[ ( int ) ( ( data & 0xf00000000000000L ) >> 56 ) ];
				materials[ w + 15 ] = palette_materials[ ( int ) ( ( data & 0xf000000000000000L ) >>> 60 ) ];
			}
		} else if ( blockStateArrayLengthInBytes != expectedPackedBlockStateArrayLengthInBytes ) {
			// a weird format where the block states are packed per
			// long (leaving bits unused). Unpack each long individually
			final long mask          = ( long ) ( Math.pow ( 2 , wordSize ) ) - 1;
			final int  bitsInUse     = ( 64 / wordSize ) * wordSize;
			int        materialIndex = 0;
			
			outer:
			for ( long packedStates : block_states ) {
				for ( int offset = 0 ; offset < bitsInUse ; offset += wordSize ) {
					materials[ materialIndex++ ] =
							palette_materials[ ( int ) ( ( packedStates & ( mask << offset ) ) >>> offset ) ];
					if ( materialIndex >= materials.length ) {
						// The last long was not fully used
						break outer;
					}
				}
			}
		} else {
			final BitSet bitSet = BitSet.valueOf ( block_states );
			
			for ( int w = 0 ; w < materials.length ; w++ ) {
				final int wordOffset = w * wordSize;
				int       index      = 0;
				
				for ( int b = 0 ; b < wordSize ; b++ ) {
					index |= bitSet.get ( wordOffset + b ) ? 1 << b : 0;
				}
				
				materials[ w ] = palette_materials[ index ];
			}
		}
	}
	
	protected Material getPaletteMaterial ( CompoundBinaryTag tag ) {
		Material material = new Material ( tag.getString ( "Name" ) );
		
		// properties
		for ( Map.Entry < String, ? extends BinaryTag > entry : tag.getCompound ( "Properties" ) ) {
			if ( entry.getValue ( ) instanceof StringBinaryTag ) {
				material.setProperty (
						entry.getKey ( ).toLowerCase ( Locale.ROOT ) ,
						( ( StringBinaryTag ) entry.getValue ( ) ).value ( ).toLowerCase ( Locale.ROOT ) );
			}
		}
		return material;
	}
	
	protected Material getPaletteMaterial ( CompoundTag tag ) {
		Material material = new Material ( tag.getString ( "Name" ) );
		
		// properties
		CompoundTag properties = tag.getCompoundTag ( "Properties" );
		
		if ( properties != null ) {
			for ( Map.Entry < String, Tag < ? > > entry : properties.entrySet ( ) ) {
				Tag < ? > value = entry.getValue ( );
				
				if ( value instanceof StringTag ) {
					material.setProperty (
							entry.getKey ( ).toLowerCase ( Locale.ROOT ) ,
							( ( StringTag ) value ).getValue ( ).toLowerCase ( Locale.ROOT ) );
				}
			}
		}
		return material;
	}
}