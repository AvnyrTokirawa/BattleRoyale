package es.outlook.adriansrj.battleroyale.util.packet.interceptor.entity;

import es.outlook.adriansrj.battleroyale.util.packet.interceptor.PacketInterceptorProtocolLib;

/**
 * Intercepts packets where a certain entity is teleported (<b>from ProtocolLib</b>).
 *
 * @author AdrianSR / 23/09/2021 / 05:06 p. m.
 */
public class PacketEntityTeleportInterceptorProtocolLib extends PacketInterceptorProtocolLib {
	
	private static final String[] TELEPORT_PACKET_CLASS_NAMES;
	
	static {
		// the reason we made this in this way is that
		// the name of the packet could be different
		// depending on the version of the server.
		TELEPORT_PACKET_CLASS_NAMES = new String[] {
				"PacketPlayOutEntityTeleport" ,
		};
	}
	
	public PacketEntityTeleportInterceptorProtocolLib ( ) {
		super ( TELEPORT_PACKET_CLASS_NAMES );
	}
}