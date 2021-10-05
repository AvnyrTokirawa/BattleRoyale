package es.outlook.adriansrj.battleroyale.util.packet.interceptor;

/**
 * {@link PacketInterceptorProtocolLib} packet acceptor.
 *
 * @author AdrianSR / 23/09/2021 / 04:47 p. m.
 */
public interface PacketInterceptorAcceptor {
	
	/**
	 * Accepts the provided packet for the interception.
	 *
	 * @param packet packet the being sent.
	 * @return whether the interception will result in
	 * preventing the packet from being sent.
	 */
	boolean accept ( Object packet );
}