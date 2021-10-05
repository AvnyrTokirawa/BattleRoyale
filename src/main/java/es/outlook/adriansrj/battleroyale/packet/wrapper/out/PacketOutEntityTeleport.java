package es.outlook.adriansrj.battleroyale.packet.wrapper.out;

import es.outlook.adriansrj.battleroyale.packet.factory.PacketFactoryService;
import es.outlook.adriansrj.battleroyale.packet.wrapper.PacketWrapper;
import es.outlook.adriansrj.battleroyale.util.Constants;
import org.bukkit.util.Vector;

import java.util.Objects;

/**
 * Packet-out for teleporting entities.
 *
 * @author AdrianSR / 28/09/2021 / 03:43 p. m.
 */
public class PacketOutEntityTeleport extends PacketWrapper {
	
	protected int     entity_id;
	protected boolean on_ground;
	protected Vector  location;
	protected float   yaw;
	protected float   pitch;
	
	public PacketOutEntityTeleport ( int entity_id , boolean on_ground , Vector location , float yaw , float pitch ) {
		this.entity_id = entity_id;
		this.on_ground = on_ground;
		this.location  = location;
		this.yaw       = yaw;
		this.pitch     = pitch;
	}
	
	public int getEntityId ( ) {
		return entity_id;
	}
	
	public void setEntityId ( int entity_id ) {
		this.entity_id = entity_id;
	}
	
	public boolean isOnGround ( ) {
		return on_ground;
	}
	
	public void setIsOnGround ( boolean on_ground ) {
		this.on_ground = on_ground;
	}
	
	public Vector getLocation ( ) {
		return location;
	}
	
	public void setLocation ( Vector location ) {
		this.location = Objects.requireNonNull ( location , "location cannot be null" );
	}
	
	public float getYaw ( ) {
		return yaw;
	}
	
	public void setYaw ( float yaw ) {
		this.yaw = yaw;
	}
	
	public float getPitch ( ) {
		return pitch;
	}
	
	public void setPitch ( float pitch ) {
		this.pitch = pitch;
	}
	
	@Override
	public Class < ? > getPacketClass ( ) {
		return Constants.PACKET_OUT_ENTITY_TELEPORT_CLASS;
	}
	
	@Override
	public Object createInstance ( ) {
		return PacketFactoryService.getInstance ( ).createEntityTeleportPacket ( this );
	}
	
	@Override
	public String toString ( ) {
		return "PacketOutEntityTeleport{" +
				"entity_id=" + entity_id +
				", on_ground=" + on_ground +
				", location=" + location +
				", yaw=" + yaw +
				", pitch=" + pitch +
				'}';
	}
}