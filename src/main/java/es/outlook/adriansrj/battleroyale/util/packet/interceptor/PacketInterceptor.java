package es.outlook.adriansrj.battleroyale.util.packet.interceptor;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Useful class for intercepting packets.
 *
 * @author AdrianSR / 27/09/2021 / 02:11 p. m.
 */
public abstract class PacketInterceptor {
	
	protected final String[]                          packet_class_names;
	protected final Set < PacketInterceptorAcceptor > acceptors;
	
	/**
	 * Constructs the packet interceptor from the name
	 * of the classes of the packets.
	 *
	 * @param packet_class_names the name of the classes of the packets.
	 */
	protected PacketInterceptor ( String... packet_class_names ) {
		this.packet_class_names = packet_class_names;
		this.acceptors          = new LinkedHashSet <> ( ); // order is important
	}
	
	public Set < PacketInterceptorAcceptor > getAcceptors ( ) {
		return acceptors;
	}
	
	public boolean containsAcceptor ( PacketInterceptorAcceptor acceptor ) {
		return getAcceptors ( ).contains ( acceptor );
	}
	
	public boolean registerAcceptor ( PacketInterceptorAcceptor acceptor ) {
		return getAcceptors ( ).add ( acceptor );
	}
	
	public boolean unregisterAcceptor ( PacketInterceptorAcceptor acceptor ) {
		return getAcceptors ( ).remove ( acceptor );
	}
	
	public abstract void register ( );
	
	public abstract void unregister ( );
}
