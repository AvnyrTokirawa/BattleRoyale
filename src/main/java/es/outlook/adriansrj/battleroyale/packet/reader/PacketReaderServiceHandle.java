package es.outlook.adriansrj.battleroyale.packet.reader;

import es.outlook.adriansrj.battleroyale.packet.wrapper.out.PacketOutEntityTeleport;
import es.outlook.adriansrj.core.util.server.Version;

import java.lang.reflect.InvocationTargetException;

/**
 * @author AdrianSR / 28/09/2021 / 03:28 p. m.
 */
public interface PacketReaderServiceHandle {
	
	static PacketReaderServiceHandle getNewHandle ( ) {
		try {
			return Class.forName ( PacketReaderServiceHandle.class.getPackage ( ).getName ( )
										   + ".PacketReaderServiceHandle_" + Version.getServerVersion ( ).name ( ) )
					.asSubclass ( PacketReaderServiceHandle.class )
					.getConstructor ( ).newInstance ( );
		} catch ( ClassNotFoundException | InvocationTargetException
				| InstantiationException | IllegalAccessException | NoSuchMethodException e ) {
			throw new IllegalStateException ( e );
		}
	}
	
	PacketOutEntityTeleport readEntityTeleportPacket ( Object packet );
}