package es.outlook.adriansrj.battleroyale.world.chunk.v12;

import es.outlook.adriansrj.battleroyale.util.nbt.NBTConstants;
import es.outlook.adriansrj.battleroyale.util.nbt.NBTSerializable;
import es.outlook.adriansrj.battleroyale.world.chunk.Chunk;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.ByteArrayBinaryTag;
import net.kyori.adventure.nbt.ByteBinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author AdrianSR / 30/08/2021 / 05:45 p. m.
 */
public class ChunkSection12 implements NBTSerializable {
	
	// 16 * 16 * 16 block area (4096)
	protected final byte[] blocks      = new byte[ 16 * 16 * 16 ];
	protected final byte[] blocks_add  = new byte[ 128 * 16 ];
	protected final byte[] block_data  = new byte[ 128 * 16 ];
	protected final byte[] block_light = new byte[ 128 * 16 ];
	protected final byte[] sky_light   = new byte[ 128 * 16 ];
	
	protected final Chunk12 chunk;
	protected final int     y;
	
	public ChunkSection12 ( Chunk12 chunk , int y ) {
		this.chunk = chunk;
		this.y     = y & 0xF; // between 0 - 15 (bottom to top)
		
		// full light everywhere
		Arrays.fill ( sky_light , ( byte ) 0xFF );
	}
	
	public ChunkSection12 ( Chunk12 chunk , CompoundBinaryTag tag ) {
		this ( chunk , tag.getInt ( NBTConstants.Pre13.CHUNK_SECTION_Y_TAG ) & 0xF );
		
		// reading blocks
		byte[] blocks = tag.getByteArray ( NBTConstants.Pre13.CHUNK_SECTION_BLOCKS_TAG );
		System.arraycopy ( blocks , 0 , this.blocks , 0 , blocks.length );
		// reading blocks add
		byte[] blocks_add = tag.getByteArray ( NBTConstants.Pre13.CHUNK_SECTION_ADD_TAG );
		System.arraycopy ( blocks_add , 0 , this.blocks_add , 0 , blocks_add.length );
		// reading blocks data
		byte[] block_data = tag.getByteArray ( NBTConstants.Pre13.CHUNK_SECTION_DATA_TAG );
		System.arraycopy ( block_data , 0 , this.block_data , 0 , block_data.length );
		// reading blocks light
		byte[] block_light = tag.getByteArray ( NBTConstants.Pre13.CHUNK_SECTION_BLOCK_LIGHT_TAG );
		System.arraycopy ( block_light , 0 , this.block_light , 0 , block_light.length );
		// reading skylight
		byte[] sky_light = tag.getByteArray ( NBTConstants.Pre13.CHUNK_SECTION_SKY_LIGHT_TAG );
		System.arraycopy ( sky_light , 0 , this.sky_light , 0 , sky_light.length );
	}
	
	public Chunk getChunk ( ) {
		return chunk;
	}
	
	public int getY ( ) {
		return y;
	}
	
	public byte[] getBlocks ( ) {
		return blocks;
	}
	
	public byte[] getBlocksAdd ( ) {
		return blocks_add;
	}
	
	public byte[] getBlockData ( ) {
		return block_data;
	}
	
	public byte[] getBlockLight ( ) {
		return block_light;
	}
	
	public byte[] getSkyLight ( ) {
		return sky_light;
	}
	
	public boolean isEmpty ( ) {
		for ( int x = 0; x < 16; x++ ) {
			for ( int y = 0; y < 16; y++ ) {
				for ( int z = 0; z < 16; z++ ) {
					if ( getBlockId ( x , y , z ) != 0 ) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean hasExtendedId ( ) {
		for ( int x = 0; x < 16; x++ ) {
			for ( int y = 0; y < 16; y++ ) {
				for ( int z = 0; z < 16; z++ ) {
					if ( getBlockAdd ( x , y , z ) != 0 ) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public byte getBlock ( int x , int y , int z ) {
		return blocks[ blockOffset ( x , y , z ) ];
	}
	
	public byte getBlockAdd ( int x , int y , int z ) {
		return blocks_add[ blockOffset ( x , y , z ) >> 1 ];
	}
	
	public int getBlockId ( int x , int y , int z ) {
		if ( blocks_add.length > 0 ) {
			return ( blocks[ blockOffset ( x , y , z ) ] & 0xFF ) | ( getDataByte ( blocks_add , x , y , z ) << 8 );
		} else {
			return blocks[ blockOffset ( x , y , z ) ] & 0xFF;
		}
	}
	
	public byte getBlockData ( int x , int y , int z ) {
		return ( byte ) getDataByte ( block_data , x , y , z );
	}
	
	public void setBlock ( int x , int y , int z , byte id ) {
		blocks[ blockOffset ( x , y , z ) ] = id;
	}
	
	public void setBlockId ( int x , int y , int z , int id ) {
		// setting
		blocks[ blockOffset ( x , y , z ) ] = ( byte ) id;
		
		// block add
		if ( id > 255 ) {
			setDataByte ( blocks_add , x , y , z , id >> 8 );
		} else {
			// an extended block id might have been set earlier, so zero out the
			// high portion.
			setDataByte ( blocks_add , x , y , z , 0 );
		}
	}
	
	public void setBlockAdd ( int x , int y , int z , byte add ) {
		setDataByte ( blocks_add , x , y , z , add );
	}
	
	public void setBlockData ( int x , int y , int z , byte data ) {
		setDataByte ( block_data , x , y , z , data );
	}
	
	public void setBlockLight ( int x , int y , int z , byte light ) {
		setDataByte ( block_light , x , y , z , light );
	}
	
	public void setSkyLight ( int x , int y , int z , byte sky_light ) {
		setDataByte ( this.sky_light , x , y , z , sky_light );
	}
	
	public void setBlocks ( byte[] blocks ) {
		if ( blocks.length == this.blocks.length ) {
			System.arraycopy ( blocks , 0 , this.blocks , 0 , blocks.length );
		} else {
			throw new IllegalArgumentException (
					"blocks array length must be: " + this.blocks.length );
		}
	}
	
	public void setBlocksAdd ( byte[] blocks_add ) {
		if ( blocks_add.length == this.blocks_add.length ) {
			System.arraycopy ( blocks_add , 0 , this.blocks_add , 0 , blocks_add.length );
		} else {
			throw new IllegalArgumentException (
					"blocks add array length must be: " + this.blocks_add.length );
		}
	}
	
	public void setBlocksData ( byte[] blocks_data ) {
		if ( blocks_data.length == this.block_data.length ) {
			System.arraycopy ( blocks_data , 0 , this.block_data , 0 , blocks_data.length );
		} else {
			throw new IllegalArgumentException (
					"blocks data array length must be: " + this.block_data.length );
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
		Map < String, BinaryTag > root = new HashMap <> ( );
		
		root.put ( NBTConstants.Pre13.CHUNK_SECTION_Y_TAG , ByteBinaryTag.of ( ( byte ) y ) );
		root.put ( NBTConstants.Pre13.CHUNK_SECTION_BLOCKS_TAG ,
				   ByteArrayBinaryTag.of ( getBlocks ( ) ) );
		
		if ( hasExtendedId ( ) ) {
			root.put ( NBTConstants.Pre13.CHUNK_SECTION_ADD_TAG ,
					   ByteArrayBinaryTag.of ( getBlocksAdd ( ) ) );
		}
		
		root.put ( NBTConstants.Pre13.CHUNK_SECTION_DATA_TAG ,
				   ByteArrayBinaryTag.of ( getBlockData ( ) ) );
		root.put ( NBTConstants.Pre13.CHUNK_SECTION_BLOCK_LIGHT_TAG ,
				   ByteArrayBinaryTag.of ( getBlockLight ( ) ) );
		root.put ( NBTConstants.Pre13.CHUNK_SECTION_SKY_LIGHT_TAG ,
				   ByteArrayBinaryTag.of ( getSkyLight ( ) ) );
		
		return CompoundBinaryTag.from ( root );
	}
	
	// World Painter - pepijn
	private int getDataByte ( byte[] array , int x , int y , int z ) {
		int  blockOffset = blockOffset ( x , y , z );
		byte dataByte    = array[ blockOffset / 2 ];
		if ( blockOffset % 2 == 0 ) {
			// Even byte -> least significant bits
			return dataByte & 0x0F;
		} else {
			// Odd byte -> most significant bits
			return ( dataByte & 0xF0 ) >> 4;
		}
	}
	
	private void setDataByte ( byte[] array , int x , int y , int z , int dataValue ) {
		int  blockOffset = blockOffset ( x , y , z );
		int  offset      = blockOffset / 2;
		byte dataByte    = array[ offset ];
		if ( blockOffset % 2 == 0 ) {
			// Even byte -> least significant bits
			dataByte &= 0xF0;
			dataByte |= ( dataValue & 0x0F );
		} else {
			// Odd byte -> most significant bits
			dataByte &= 0x0F;
			dataByte |= ( ( dataValue & 0x0F ) << 4 );
		}
		array[ offset ] = dataByte;
	}
	
	private int blockOffset ( int x , int y , int z ) {
		return x | ( ( z | ( ( y & 0xF ) << 4 ) ) << 4 );
	}
	// World Painter - pepijn
}