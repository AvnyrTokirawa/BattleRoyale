package es.outlook.adriansrj.battleroyale.packet.wrapper.out;

import es.outlook.adriansrj.battleroyale.packet.factory.PacketFactoryService;
import es.outlook.adriansrj.battleroyale.packet.wrapper.PacketWrapper;
import es.outlook.adriansrj.battleroyale.util.Constants;

/**
 * Packet-out for leashing entities.
 *
 * @author AdrianSR / 14/10/2021 / 02:29 p. m.
 */
public class PacketOutEntityAttach extends PacketWrapper {
	
	protected int entity_id;
	protected int leash_holder_id;
	
	public PacketOutEntityAttach ( int entity_id , int leash_holder_id ) {
		this.entity_id       = entity_id;
		this.leash_holder_id = leash_holder_id;
	}
	
	public int getEntityId ( ) {
		return entity_id;
	}
	
	public void setEntityId ( int entity_id ) {
		this.entity_id = entity_id;
	}
	
	public int getLeashHolderId ( ) {
		return leash_holder_id;
	}
	
	public void setLeashHolderId ( int leash_holder_id ) {
		this.leash_holder_id = leash_holder_id;
	}
	
	@Override
	public Class < ? > getPacketClass ( ) {
		return Constants.PACKET_OUT_ATTACH_ENTITY_CLASS;
	}
	
	@Override
	public Object createInstance ( ) {
		return PacketFactoryService.getInstance ( ).createEntityAttachPacket ( this );
	}
}