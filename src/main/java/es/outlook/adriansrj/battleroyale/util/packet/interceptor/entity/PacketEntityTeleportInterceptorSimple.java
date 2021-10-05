package es.outlook.adriansrj.battleroyale.util.packet.interceptor.entity;

import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.packet.interceptor.PacketInterceptorSimple;

/**
 * Intercepts packets where a certain entity is teleported (<b>from the AdrianSRCore packet API</b>).
 *
 * @author AdrianSR / 23/09/2021 / 05:06 p. m.
 */
public class PacketEntityTeleportInterceptorSimple extends PacketInterceptorSimple {
	
	private static final String[] TELEPORT_PACKET_CLASS_NAMES;
	
	static {
		// the reason we made this in this way is that
		// the name of the packet could be different
		// depending on the version of the server.
		TELEPORT_PACKET_CLASS_NAMES = new String[] {
				Constants.PACKET_OUT_ENTITY_TELEPORT_NAME ,
		};
	}
	
	public PacketEntityTeleportInterceptorSimple ( ) {
		super ( TELEPORT_PACKET_CLASS_NAMES );
	}
}