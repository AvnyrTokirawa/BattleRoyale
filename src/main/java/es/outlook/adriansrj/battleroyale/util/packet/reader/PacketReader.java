package es.outlook.adriansrj.battleroyale.util.packet.reader;

import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.Validate;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

/**
 * Useful class for reading serialized data from packets.
 *
 * @author AdrianSR / 28/09/2021 / 02:40 p. m.
 */
public class PacketReader {
	
	protected static Object getNewDataSerializer ( ByteBuf buffer ) {
		try {
			return Constants.PACKET_DATA_SERIALIZER_CONSTRUCTOR.newInstance ( buffer );
		} catch ( InstantiationException | IllegalAccessException | InvocationTargetException ex ) {
			throw new IllegalStateException ( ex );
		}
	}
	
	protected static Object getNewDataSerializer ( ) {
		return getNewDataSerializer ( Unpooled.buffer ( ) );
	}
	
	protected static Object extractSerializedData ( Object packet ) {
		Object result = getNewDataSerializer ( );
		
		try {
			Constants.PACKET_WRITE_DATA_METHOD.invoke ( packet , result );
		} catch ( IllegalAccessException | InvocationTargetException ex ) {
			throw new IllegalStateException ( ex );
		}
		return result;
	}
	
	protected final Object packet;
	protected       Object data_serializer;
	
	public PacketReader ( Object packet ) {
		Validate.notNull ( packet , "packet cannot be null" );
		
		this.packet          = packet;
		this.data_serializer = extractSerializedData ( packet );
	}
	
	public Object getPacket ( ) {
		return packet;
	}
	
	public Object getDataSerializer ( ) {
		return data_serializer;
	}
	
	public ByteBuf getBuffer ( ) {
		return ( ByteBuf ) data_serializer;
	}
	
	// ---
	
	public boolean readBoolean ( ) {
		return ( ( ByteBuf ) data_serializer ).readBoolean ( );
	}
	
	public boolean getBoolean ( int index ) {
		return ( ( ByteBuf ) data_serializer ).getBoolean ( index );
	}
	
	// ---
	
	public short readShort ( ) {
		return ( ( ByteBuf ) data_serializer ).readShort ( );
	}
	
	public short getShort ( int index ) {
		return ( ( ByteBuf ) data_serializer ).getShort ( index );
	}
	
	// ---
	
	public byte readByte ( ) {
		return ( ( ByteBuf ) data_serializer ).readByte ( );
	}
	
	public byte getByte ( int index ) {
		return ( ( ByteBuf ) data_serializer ).getByte ( index );
	}
	
	// ---
	
	public char readChar ( ) {
		return ( ( ByteBuf ) data_serializer ).readChar ( );
	}
	
	public char getChar ( int index ) {
		return ( ( ByteBuf ) data_serializer ).getChar ( index );
	}
	
	// ---
	
	public CharSequence readCharSequence ( int length , Charset charset ) {
		return ( ( ByteBuf ) data_serializer ).readCharSequence ( length , charset );
	}
	
	public CharSequence getCharSequence ( int index , int length , Charset charset ) {
		return ( ( ByteBuf ) data_serializer ).getCharSequence ( index , length , charset );
	}
	
	// ---
	
	public float readFloat ( ) {
		return ( ( ByteBuf ) data_serializer ).readFloat ( );
	}
	
	public float getFloat ( int index ) {
		return ( ( ByteBuf ) data_serializer ).getFloat ( index );
	}
	
	// ---
	
	public double readDouble ( ) {
		return ( ( ByteBuf ) data_serializer ).readDouble ( );
	}
	
	public double getDouble ( int index ) {
		return ( ( ByteBuf ) data_serializer ).getDouble ( index );
	}
	
	// ---
	
	public int readVarInt ( ) {
		// deserialization may vary depending on version of the server.
		// we've decided to use code from minecraft server as it would
		// be too much work to access method using reflection.
		int i = 0;
		int j = 0;
		
		byte b0;
		
		do {
			b0 = this.readByte ( );
			i |= ( b0 & 127 ) << j++ * 7;
			
			if ( j > 5 ) {
				throw new RuntimeException ( "VarInt too big" );
			}
		} while ( ( b0 & 128 ) == 128 );
		
		return i;
	}
	
	public int readInt ( ) {
		return ( ( ByteBuf ) data_serializer ).readInt ( );
	}
	
	public int getInt ( int index ) {
		return ( ( ByteBuf ) data_serializer ).getInt ( index );
	}
	
	// ---
	
	public long readVarLong ( ) {
		// deserialization may vary depending on version of the server.
		// we've decided to use code from minecraft server as it would
		// be too much work to access method using reflection.
		long i = 0L;
		int  j = 0;
		
		byte b0;
		
		do {
			b0 = this.readByte ( );
			i |= ( long ) ( b0 & 127 ) << j++ * 7;
			
			if ( j > 10 ) {
				throw new RuntimeException ( "VarLong too big" );
			}
		} while ( ( b0 & 128 ) == 128 );
		
		return i;
	}
	
	public long readLong ( ) {
		return ( ( ByteBuf ) data_serializer ).readLong ( );
	}
	
	public long getLong ( int index ) {
		return ( ( ByteBuf ) data_serializer ).getLong ( index );
	}
}