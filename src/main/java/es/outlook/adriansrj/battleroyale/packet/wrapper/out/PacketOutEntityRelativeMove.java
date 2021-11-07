package es.outlook.adriansrj.battleroyale.packet.wrapper.out;

import es.outlook.adriansrj.battleroyale.packet.factory.PacketFactoryService;
import es.outlook.adriansrj.battleroyale.util.Constants;

/**
 * Entity relative move packet.
 *
 * @author AdrianSR / 28/09/2021 / 03:43 p. m.
 */
public class PacketOutEntityRelativeMove extends PacketOutEntity {
	
	public PacketOutEntityRelativeMove ( int entity_id , int delta_x , int delta_y , int delta_z , boolean on_ground ) {
		super ( entity_id ,
				delta_x , delta_y , delta_z ,
				( byte ) 0 , ( byte ) 0 ,
				on_ground , false , true );
	}
	
	public PacketOutEntityRelativeMove ( int entity_id , long delta_x , long delta_y , long delta_z , boolean on_ground ) {
		this ( entity_id , ( int ) delta_x , ( int ) delta_y , ( int ) delta_z , on_ground );
	}
	
	@Override
	public Class < ? > getPacketClass ( ) {
		return Constants.PACKET_OUT_ENTITY_RELATIVE_MOVE_CLASS;
	}
	
	@Override
	public Object createInstance ( ) {
		return PacketFactoryService.getInstance ( ).createEntityRelativeMovePacket ( this );
	}
}