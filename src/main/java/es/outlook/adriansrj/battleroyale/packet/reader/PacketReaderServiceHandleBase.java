package es.outlook.adriansrj.battleroyale.packet.reader;

import es.outlook.adriansrj.battleroyale.packet.wrapper.out.PacketOutEntityTeleport;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.packet.reader.PacketReader;
import org.apache.commons.lang3.Validate;
import org.bukkit.util.Vector;

/**
 * @author AdrianSR / 28/09/2021 / 03:48 p. m.
 */
abstract class PacketReaderServiceHandleBase implements PacketReaderServiceHandle {
	
	@Override
	public PacketOutEntityTeleport readEntityTeleportPacket ( Object packet ) {
		PacketReader reader = new PacketReader (
				packetClassCheck ( Constants.PACKET_OUT_ENTITY_TELEPORT_CLASS , packet ) );
		
		int     entity_id = reader.readVarInt ( );
		double  x         = reader.readDouble ( );
		double  y         = reader.readDouble ( );
		double  z         = reader.readDouble ( );
		float   yaw       = ( reader.readByte ( ) * 360.F ) / 256.0F;
		float   pitch     = ( reader.readByte ( ) * 360.F ) / 256.0F;
		boolean on_ground = reader.readBoolean ( );
		
		return new PacketOutEntityTeleport ( entity_id , on_ground , new Vector ( x , y , z ) , yaw , pitch );
	}
	
	// -------- utils
	
	protected Object packetClassCheck ( Class < ? > actual , Object packet ) {
		Validate.notNull ( packet , "packet cannot be null" );
		Validate.isAssignableFrom ( actual , packet.getClass ( ) );
		
		return packet;
	}
}