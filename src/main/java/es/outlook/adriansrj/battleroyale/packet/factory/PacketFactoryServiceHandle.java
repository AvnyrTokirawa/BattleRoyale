package es.outlook.adriansrj.battleroyale.packet.factory;

import es.outlook.adriansrj.battleroyale.packet.wrapper.out.PacketOutEntityTeleport;
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
	
	Object createEntityTeleportPacket ( int entity_id , boolean on_ground , double x , double y ,
			double z , float yaw , float pitch );
	
	default Object createEntityTeleportPacket ( PacketOutEntityTeleport wrapper ) {
		Vector location = wrapper.getLocation ( );
		
		return createEntityTeleportPacket (
				wrapper.getEntityId ( ) , wrapper.isOnGround ( ) ,
				location.getX ( ) , location.getY ( ) , location.getZ ( ) ,
				wrapper.getYaw ( ) , wrapper.getPitch ( ) );
	}
}