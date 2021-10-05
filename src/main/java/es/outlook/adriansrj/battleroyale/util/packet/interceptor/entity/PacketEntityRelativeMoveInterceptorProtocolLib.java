package es.outlook.adriansrj.battleroyale.util.packet.interceptor.entity;

import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.packet.interceptor.PacketInterceptorProtocolLib;

/**
 * Intercepts packets where a certain entity is moved (<b>from ProtocolLib</b>).
 *
 * @author AdrianSR / 23/09/2021 / 05:06 p. m.
 */
public class PacketEntityRelativeMoveInterceptorProtocolLib extends PacketInterceptorProtocolLib {
	
	private static final String[] MOVE_PACKETS_CLASS_NAMES;
	
	static {
		// the reason we made this in this way is that
		// there are more than one entity-movement related packets.
		MOVE_PACKETS_CLASS_NAMES = new String[] {
				Constants.PACKET_OUT_ENTITY_RELATIVE_MOVE_NAME ,
				Constants.PACKET_OUT_ENTITY_RELATIVE_MOVE_LOOK_NAME ,
		};
	}
	
	public PacketEntityRelativeMoveInterceptorProtocolLib ( ) {
		super ( MOVE_PACKETS_CLASS_NAMES );
	}
}