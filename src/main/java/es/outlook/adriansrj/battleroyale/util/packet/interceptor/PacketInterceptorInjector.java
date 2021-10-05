package es.outlook.adriansrj.battleroyale.util.packet.interceptor;

/**
 * A {@link PacketInterceptorAcceptor} implementation
 * that enables the injection of packets.
 *
 * @author AdrianSR / 28/09/2021 / 11:59 a. m.
 * @see PacketInterceptorAcceptor
 */
public interface PacketInterceptorInjector extends PacketInterceptorAcceptor {
	
	/**
	 * <b>Packets will not be cancelled
	 * with this acceptor, as they will now
	 * be injected.</b>
	 *
	 * @param packet packet the being sent.
	 * @return <b>false</b>.
	 */
	@Override
	default boolean accept ( Object packet ) {
		return false;
	}
	
	/**
	 * Injects the provided packet.
	 * <br>
	 * <b>Note that in newer Minecraft versions
	 * packets cannot be injected normally; that's
	 * why this method will return a new instance
	 * of the packet that will be the one that is
	 * to be actually sent instead.</b>
	 *
	 * @param packet the packet to inject/reference.
	 * @return the packet that will actually be sent.
	 */
	Object inject ( Object packet );
}