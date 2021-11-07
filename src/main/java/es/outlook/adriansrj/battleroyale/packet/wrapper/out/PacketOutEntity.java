package es.outlook.adriansrj.battleroyale.packet.wrapper.out;

import es.outlook.adriansrj.battleroyale.packet.wrapper.PacketWrapper;

/**
 * Base entity packet-out.
 *
 * @author AdrianSR / 28/09/2021 / 03:43 p. m.
 */
public abstract class PacketOutEntity extends PacketWrapper {
	
	protected int     entity_id;
	protected int     delta_x;
	protected int     delta_y;
	protected int     delta_z;
	protected byte    yaw;
	protected byte    pitch;
	protected boolean on_ground;
	protected boolean rotating;
	protected boolean moving;
	
	protected PacketOutEntity ( int entity_id , int delta_x , int delta_y , int delta_z , byte yaw , byte pitch ,
			boolean on_ground ,
			boolean rotating , boolean moving ) {
		this.entity_id = entity_id;
		this.delta_x   = delta_x;
		this.delta_y   = delta_y;
		this.delta_z   = delta_z;
		this.yaw       = yaw;
		this.pitch     = pitch;
		this.on_ground = on_ground;
		this.rotating  = rotating;
		this.moving    = moving;
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
	
	public int getDeltaX ( ) {
		return delta_x;
	}
	
	public void setDeltaX ( int delta_x ) {
		this.delta_x = delta_x;
	}
	
	public int getDeltaY ( ) {
		return delta_y;
	}
	
	public void setDeltaY ( int delta_y ) {
		this.delta_y = delta_y;
	}
	
	public int getDeltaZ ( ) {
		return delta_z;
	}
	
	public void setDeltaZ ( int delta_z ) {
		this.delta_z = delta_z;
	}
	
	public byte getYaw ( ) {
		return yaw;
	}
	
	public void setYaw ( byte yaw ) {
		this.yaw = yaw;
	}
	
	public byte getPitch ( ) {
		return pitch;
	}
	
	public void setPitch ( byte pitch ) {
		this.pitch = pitch;
	}
}