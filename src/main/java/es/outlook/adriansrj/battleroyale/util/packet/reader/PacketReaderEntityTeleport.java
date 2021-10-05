package es.outlook.adriansrj.battleroyale.util.packet.reader;

import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.Validate;

/**
 *
 *
 * @author AdrianSR / 28/09/2021 / 03:41 p. m.
 */
public class PacketReaderEntityTeleport extends PacketReader {
	
	public PacketReaderEntityTeleport ( Object packet ) {
		super ( packet );
		Validate.isAssignableFrom ( Constants.PACKET_OUT_ENTITY_TELEPORT_CLASS , packet.getClass ( ) );
	}
}
