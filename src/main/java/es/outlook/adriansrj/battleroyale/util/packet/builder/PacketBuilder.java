package es.outlook.adriansrj.battleroyale.util.packet.builder;

import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import es.outlook.adriansrj.core.util.math.Vector3I;
import es.outlook.adriansrj.core.util.reflection.general.MethodReflection;
import es.outlook.adriansrj.core.util.server.Version;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.UUID;

/**
 * Useful class for building packets.
 *
 * @author AdrianSR / 28/09/2021 / 12:14 p. m.
 */
public class PacketBuilder {
	
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
	
	protected final Class < ? > packet_type;
	protected       Object      data_serializer;
	
	public PacketBuilder ( Class < ? > packet_class ) {
		this.packet_type     = packet_class;
		this.data_serializer = getNewDataSerializer ( Unpooled.buffer ( ) );
	}
	
	public PacketBuilder ( Object packet ) {
		Validate.notNull ( packet , "packet cannot be null" );
		Validate.isAssignableFrom ( Constants.PACKET_CLASS , packet.getClass ( ) );
		
		this.packet_type     = packet.getClass ( );
		this.data_serializer = extractSerializedData ( packet );
	}
	
	public PacketBuilder ( String packet_class_name , String packet_class_package ) throws ClassNotFoundException {
		this ( ClassReflection.getMinecraftClass ( packet_class_name , packet_class_package ) );
	}
	
	public PacketBuilder ( String packet_class_name ) throws ClassNotFoundException {
		this ( packet_class_name , null );
	}
	
	public Class < ? > getPacketClass ( ) {
		return packet_type;
	}
	
	public Object getDataSerializer ( ) {
		return data_serializer;
	}
	
	public ByteBuf getBuffer ( ) {
		return ( ByteBuf ) data_serializer;
	}
	
	// ---
	
	public Object build ( ) {
		// special cases.
		// there are some packets (like PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook)
		// that doesn't have a constructor with PacketDataSerializer, but they instead have a static
		// method that creates an instance of the packet from a PacketDataSerializer.
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_17_R1 ) ) {
			// method name may vary depending on version of the server
			try {
				return packet_type.getMethod ( "b" , Constants.PACKET_DATA_SERIALIZER_CLASS )
						.invoke ( null , data_serializer );
			} catch ( NoSuchMethodException | InvocationTargetException | IllegalAccessException e ) {
				// not a special case
			}
		}
		
		try {
			try {
				return packet_type.getConstructor ( Constants.PACKET_DATA_SERIALIZER_CLASS )
						.newInstance ( data_serializer );
			} catch ( InstantiationException | IllegalAccessException | InvocationTargetException ex ) {
				throw new IllegalStateException ( ex );
			}
		} catch ( NoSuchMethodException ex ) {
			// legacy versions
			try {
				Object packet = packet_type.getConstructor ( ).newInstance ( );
				
				Constants.PACKET_READ_DATA_METHOD.invoke ( packet , data_serializer );
				return packet;
			} catch ( InstantiationException | IllegalAccessException
					| InvocationTargetException | NoSuchMethodException ex_b ) {
				throw new IllegalStateException ( ex_b );
			}
		}
	}
	
	// ---
	
	public PacketBuilder setBuffer ( ByteBuf buffer ) {
		this.data_serializer = getNewDataSerializer (
				Objects.requireNonNull ( buffer , "buffer cannot be null" ) );
		return this;
	}
	
	// ---
	
	public PacketBuilder writeBoolean ( boolean value ) {
		( ( ByteBuf ) data_serializer ).writeBoolean ( value );
		return this;
	}
	
	public PacketBuilder setBoolean ( int index , boolean value ) {
		( ( ByteBuf ) data_serializer ).setBoolean ( index , value );
		return this;
	}
	
	// ---
	
	public PacketBuilder writeShort ( short value ) {
		( ( ByteBuf ) data_serializer ).writeShort ( value );
		return this;
	}
	
	public PacketBuilder setShort ( int index , short value ) {
		( ( ByteBuf ) data_serializer ).setShort ( index , value );
		return this;
	}
	
	// ---
	
	public PacketBuilder writeByte ( int value ) {
		( ( ByteBuf ) data_serializer ).writeByte ( value );
		return this;
	}
	
	public PacketBuilder setByte ( int index , int value ) {
		( ( ByteBuf ) data_serializer ).setByte ( index , value );
		return this;
	}
	
	// ---
	
	public PacketBuilder writeChar ( char value ) {
		( ( ByteBuf ) data_serializer ).writeChar ( value );
		return this;
	}
	
	public PacketBuilder setChar ( int index , char value ) {
		( ( ByteBuf ) data_serializer ).setChar ( index , value );
		return this;
	}
	
	// ---
	
	public PacketBuilder writeCharSequence ( CharSequence value , Charset charset ) {
		( ( ByteBuf ) data_serializer ).writeCharSequence ( value , charset );
		return this;
	}
	
	public PacketBuilder setCharSequence ( int index , CharSequence value , Charset charset ) {
		( ( ByteBuf ) data_serializer ).setCharSequence ( index , value , charset );
		return this;
	}
	
	// ---
	
	public PacketBuilder writeFloat ( float value ) {
		( ( ByteBuf ) data_serializer ).writeFloat ( value );
		return this;
	}
	
	public PacketBuilder setFloat ( int index , float value ) {
		( ( ByteBuf ) data_serializer ).setFloat ( index , value );
		return this;
	}
	
	// ---
	
	public PacketBuilder writeDouble ( double value ) {
		( ( ByteBuf ) data_serializer ).writeDouble ( value );
		return this;
	}
	
	public PacketBuilder setDouble ( int index , double value ) {
		( ( ByteBuf ) data_serializer ).setDouble ( index , value );
		return this;
	}
	
	// ---
	
	public PacketBuilder writeVarInt ( int value ) {
		while ( ( value & -128 ) != 0 ) {
			this.writeByte ( value & 127 | 128 );
			value >>>= 7;
		}
		
		this.writeByte ( value );
		return this;
	}
	
	public PacketBuilder writeInt ( int value ) {
		( ( ByteBuf ) data_serializer ).writeInt ( value );
		return this;
	}
	
	public PacketBuilder setInt ( int index , int value ) {
		( ( ByteBuf ) data_serializer ).setInt ( index , value );
		return this;
	}
	
	// ---
	
	public PacketBuilder writeVarLong ( long value ) {
		try {
			// method name may vary depending on version of the server
			MethodReflection.invoke ( data_serializer , "b" , value );
			return this;
		} catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException ex ) {
			throw new IllegalStateException ( ex );
		}
	}
	
	public PacketBuilder writeLong ( long value ) {
		( ( ByteBuf ) data_serializer ).writeLong ( value );
		return this;
	}
	
	public PacketBuilder setLong ( int index , long value ) {
		( ( ByteBuf ) data_serializer ).setLong ( index , value );
		return this;
	}
	
	// ---
	
	public PacketBuilder writeByteArray ( byte[] array ) {
		writeVarInt ( array.length );
		
		( ( ByteBuf ) data_serializer ).writeBytes ( array );
		return this;
	}
	
	public PacketBuilder writeIntArray ( int[] array ) {
		writeVarInt ( array.length );
		
		for ( int value : array ) {
			writeVarInt ( value );
		}
		return this;
	}
	
	public PacketBuilder writeLongArray ( long[] array ) {
		writeVarInt ( array.length );
		
		for ( long value : array ) {
			writeLong ( value );
		}
		return this;
	}
	
	public PacketBuilder writeBlockPosition ( int x , int y , int z ) {
		try {
			Class < ? > position_class = ClassReflection.getMinecraftClass (
					"BlockPosition" , "core" );
			Object position = position_class.getConstructor (
					int.class , int.class , int.class ).newInstance ( x , y , z );
			
			// method name may vary depending on version of the server
			Constants.PACKET_DATA_SERIALIZER_CLASS.getMethod ( "a" , position_class )
					.invoke ( data_serializer , position );
			return this;
		} catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException
				| ClassNotFoundException | InstantiationException ex ) {
			throw new IllegalStateException ( ex );
		}
	}
	
	public PacketBuilder writeBlockPosition ( Vector3I position ) {
		writeBlockPosition ( position.getX ( ) , position.getY ( ) , position.getZ ( ) );
		return this;
	}
	
	public PacketBuilder writeBlockPosition ( Vector position ) {
		writeBlockPosition ( position.getBlockX ( ) , position.getBlockY ( ) , position.getBlockZ ( ) );
		return this;
	}
	
	public PacketBuilder writeChunkPosition ( int x , int z ) {
		try {
			Class < ? > position_class = ClassReflection.getMinecraftClass (
					"ChunkCoordIntPair" , "world.level" );
			Object position = position_class.getConstructor (
					int.class , int.class ).newInstance ( x , z );
			
			// method name may vary depending on version of the server
			Constants.PACKET_DATA_SERIALIZER_CLASS.getMethod ( "a" , position_class )
					.invoke ( data_serializer , position );
			return this;
		} catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException
				| ClassNotFoundException | InstantiationException ex ) {
			throw new IllegalStateException ( ex );
		}
	}
	
	// ---
	
	public PacketBuilder writeEnum ( Enum < ? > value ) {
		writeVarInt ( value.ordinal ( ) );
		return this;
	}
	
	// ---
	
	public PacketBuilder writeUUID ( UUID value ) {
		writeLong ( value.getMostSignificantBits ( ) );
		writeLong ( value.getLeastSignificantBits ( ) );
		return this;
	}
	
	public PacketBuilder setUUID ( int index , UUID value ) {
		setLong ( index , value.getMostSignificantBits ( ) );
		setLong ( index + 1 , value.getLeastSignificantBits ( ) );
		return this;
	}
}