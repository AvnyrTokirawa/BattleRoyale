package es.outlook.adriansrj.battleroyale.packet.reader;

import org.apache.commons.lang3.Validate;

/**
 * @author AdrianSR / 28/09/2021 / 03:48 p. m.
 */
abstract class PacketReaderServiceHandleBase implements PacketReaderServiceHandle {
	
	protected Object packetClassCheck ( Class < ? > actual , Object packet ) {
		Validate.notNull ( packet , "packet cannot be null" );
		Validate.isAssignableFrom ( actual , packet.getClass ( ) );
		return packet;
	}
}