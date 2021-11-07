package es.outlook.adriansrj.battleroyale.packet.wrapper.out;

import es.outlook.adriansrj.battleroyale.packet.factory.PacketFactoryService;
import es.outlook.adriansrj.battleroyale.util.Constants;

/**
 * Entity relative move and look packet.
 *
 * @author AdrianSR / 28/09/2021 / 03:43 p. m.
 */
public class PacketOutEntityRelativeMoveLook extends PacketOutEntity {
	
	public PacketOutEntityRelativeMoveLook ( int entity_id ,
			int delta_x , int delta_y , int delta_z ,
			byte yaw , byte pitch , boolean on_ground ) {
		super ( entity_id ,
				delta_x , delta_y , delta_z ,
				yaw , pitch ,
				on_ground , true , true );
	}
	
	public PacketOutEntityRelativeMoveLook ( int entity_id ,
			long delta_x , long delta_y , long delta_z ,
			byte yaw , byte pitch , boolean on_ground ) {
		this ( entity_id ,
			   ( int ) delta_x , ( int ) delta_y , ( int ) delta_z ,
			   yaw , pitch , on_ground );
	}
	
	@Override
	public Class < ? > getPacketClass ( ) {
		return Constants.PACKET_OUT_ENTITY_RELATIVE_MOVE_LOOK_CLASS;
	}
	
	@Override
	public Object createInstance ( ) {
		return PacketFactoryService.getInstance ( ).createEntityRelativeMoveLookPacket ( this );
	}
}