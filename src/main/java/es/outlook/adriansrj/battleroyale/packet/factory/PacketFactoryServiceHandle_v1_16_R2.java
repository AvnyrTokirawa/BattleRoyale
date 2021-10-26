package es.outlook.adriansrj.battleroyale.packet.factory;

import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_16_R2.PacketDataSerializer;
import net.minecraft.server.v1_16_R2.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityTeleport;

import java.io.IOException;

/**
 * @author AdrianSR / 26/10/2021 / 06:28 p. m.
 */
class PacketFactoryServiceHandle_v1_16_R2 implements PacketFactoryServiceHandle {
	
	public PacketFactoryServiceHandle_v1_16_R2 ( ) {
		// java 16 and its constructor system!
	}
	
	@Override
	public Object createEntityTeleportPacket ( int entity_id , boolean on_ground ,
			double x , double y , double z ,
			float yaw , float pitch ) {
		PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport ( );
		PacketDataSerializer        data   = new PacketDataSerializer ( Unpooled.buffer ( ) );
		
		// entity id
		data.d ( entity_id );
		// x, y, z
		data.writeDouble ( x );
		data.writeDouble ( y );
		data.writeDouble ( z );
		// yaw, pitch
		data.writeByte ( ( byte ) ( yaw * 256.0F / 360.0F ) );
		data.writeByte ( ( byte ) ( pitch * 256.0F / 360.0F ) );
		// on ground flag
		data.writeBoolean ( on_ground );
		
		try {
			packet.a ( data );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		}
		return packet;
	}
	
	@Override
	public Object createEntityAttachPacket ( int entity_id , int leash_holder_id ) {
		PacketPlayOutAttachEntity packet = new PacketPlayOutAttachEntity ( );
		PacketDataSerializer      data   = new PacketDataSerializer ( Unpooled.buffer ( ) );
		
		// entity id
		data.writeInt ( entity_id );
		// leash holder entity id
		data.writeInt ( leash_holder_id );
		
		try {
			packet.a ( data );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		}
		
		return packet;
	}
}