package es.outlook.adriansrj.battleroyale.packet.wrapper.out;

import es.outlook.adriansrj.battleroyale.packet.factory.PacketFactoryService;
import es.outlook.adriansrj.battleroyale.packet.wrapper.PacketWrapper;
import es.outlook.adriansrj.battleroyale.util.Constants;
import org.bukkit.entity.Entity;

/**
 * Packet-out for mounting entities.
 *
 * @author AdrianSR / 24/11/2021 / 10:29 a. m.
 */
public class PacketOutMount extends PacketWrapper {
	
	protected int   entity_id;
	protected int[] passenger_ids;
	
	public PacketOutMount ( int entity_id , int... passenger_ids ) {
		this.entity_id     = entity_id;
		this.passenger_ids = passenger_ids;
	}
	
	public PacketOutMount ( Entity entity , Entity... passengers ) {
		this.entity_id     = entity.getEntityId ( );
		this.passenger_ids = new int[ passengers.length ];
		
		for ( int i = 0 ; i < passengers.length ; i++ ) {
			passenger_ids[ i ] = passengers[ i ].getEntityId ( );
		}
	}
	
	public int getEntityId ( ) {
		return entity_id;
	}
	
	public void setEntityId ( int entity_id ) {
		this.entity_id = entity_id;
	}
	
	public void setEntity ( Entity entity ) {
		this.entity_id = entity.getEntityId ( );
	}
	
	public int[] getPassengerIds ( ) {
		return passenger_ids;
	}
	
	public void setPassengerIds ( int... passenger_ids ) {
		this.passenger_ids = passenger_ids;
	}
	
	public void setPassengers ( Entity... passengers ) {
		this.passenger_ids = new int[ passengers.length ];
		
		for ( int i = 0 ; i < passengers.length ; i++ ) {
			passenger_ids[ i ] = passengers[ i ].getEntityId ( );
		}
	}
	
	@Override
	public Class < ? > getPacketClass ( ) {
		return Constants.PACKET_OUT_MOUNT_CLASS;
	}
	
	@Override
	public Object createInstance ( ) {
		return PacketFactoryService.getInstance ( ).createMountPacket ( this );
	}
}