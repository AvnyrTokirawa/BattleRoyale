package es.outlook.adriansrj.battleroyale.packet.factory;

import es.outlook.adriansrj.battleroyale.packet.wrapper.out.*;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

/**
 * @author AdrianSR / 28/09/2021 / 03:28 p. m.
 */
public interface PacketFactoryServiceHandle {
	
	static PacketFactoryServiceHandle getNewHandle ( ) {
		try {
			return Class.forName ( PacketFactoryServiceHandle.class.getPackage ( ).getName ( )
										   + ".PacketFactoryServiceHandle_" + Version.getServerVersion ( ).name ( ) )
					.asSubclass ( PacketFactoryServiceHandle.class )
					.getConstructor ( ).newInstance ( );
		} catch ( ClassNotFoundException | InvocationTargetException
				| InstantiationException | IllegalAccessException | NoSuchMethodException e ) {
			throw new IllegalStateException ( e );
		}
	}
	
	// ------ entity relative move packet
	
	Object createEntityRelativeMovePacket ( int entity_id , int delta_x , int delta_y ,
			int delta_z , boolean on_ground );
	
	default Object createEntityRelativeMovePacket ( PacketOutEntityRelativeMove wrapper ) {
		return createEntityRelativeMovePacket (
				wrapper.getEntityId ( ) ,
				wrapper.getDeltaX ( ) , wrapper.getDeltaY ( ) , wrapper.getDeltaZ ( ) ,
				wrapper.isOnGround ( ) );
	}
	
	// ------ entity relative move and look packet
	
	Object createEntityRelativeMoveLookPacket ( int entity_id ,
			int delta_x , int delta_y , int delta_z ,
			byte yaw , byte pitch , boolean on_ground );
	
	default Object createEntityRelativeMoveLookPacket ( PacketOutEntityRelativeMoveLook wrapper ) {
		return createEntityRelativeMoveLookPacket (
				wrapper.getEntityId ( ) ,
				wrapper.getDeltaX ( ) , wrapper.getDeltaY ( ) , wrapper.getDeltaZ ( ) ,
				wrapper.getYaw ( ) , wrapper.getPitch ( ) , wrapper.isOnGround ( ) );
	}
	
	// ------ entity teleport packet
	
	Object createEntityTeleportPacket ( int entity_id , boolean on_ground , double x , double y ,
			double z , float yaw , float pitch );
	
	default Object createEntityTeleportPacket ( PacketOutEntityTeleport wrapper ) {
		Vector location = wrapper.getLocation ( );
		
		return createEntityTeleportPacket (
				wrapper.getEntityId ( ) , wrapper.isOnGround ( ) ,
				location.getX ( ) , location.getY ( ) , location.getZ ( ) ,
				wrapper.getYaw ( ) , wrapper.getPitch ( ) );
	}
	
	// ------ attach entity packet
	
	Object createEntityAttachPacket ( int entity_id , int leash_holder_id );
	
	default Object createEntityAttachPacket ( PacketOutEntityAttach wrapper ) {
		return createEntityAttachPacket ( wrapper.getEntityId ( ) , wrapper.getLeashHolderId ( ) );
	}
	
	// ------ mount packet
	
	Object createMountPacket ( int entity_id , int... passengers_ids );
	
	default Object createMountPacket ( PacketOutMount wrapper ) {
		return createMountPacket ( wrapper.getEntityId ( ) , wrapper.getPassengerIds ( ) );
	}
}