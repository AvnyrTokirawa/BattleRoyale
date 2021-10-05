package es.outlook.adriansrj.battleroyale.util.packet.interceptor;

import es.outlook.adriansrj.core.util.packet.PacketAdapter;
import es.outlook.adriansrj.core.util.packet.PacketChannelHandler;
import es.outlook.adriansrj.core.util.packet.PacketEvent;
import es.outlook.adriansrj.core.util.packet.PacketListener;

/**
 * Useful class for intercepting packets using the <b>ProtocolLib</b> API.
 *
 * @author AdrianSR / 23/09/2021 / 04:45 p. m.
 */
public class PacketInterceptorSimple extends PacketInterceptor {
	
	protected final PacketListener handle;
	
	/**
	 * Constructs the packet interceptor from the name
	 * of the classes of the packets.
	 *
	 * @param packet_class_names the name of the classes of the packets.
	 */
	public PacketInterceptorSimple ( String... packet_class_names ) {
		super ( packet_class_names );
		
		this.handle = new PacketAdapter ( ) {
			
			@Override
			public void onSending ( PacketEvent event ) {
				for ( PacketInterceptorAcceptor acceptor : acceptors ) {
					if ( acceptor instanceof PacketInterceptorInjector ) {
						Object result = ( ( PacketInterceptorInjector ) acceptor ).inject ( event.getPacket ( ) );
						
						if ( result != null ) {
							event.setPacket ( result );
						}
					} else if ( acceptor.accept ( event.getPacket ( ) ) ) {
						event.setCancelled ( true );
						break;
					}
				}
			}
		};
	}
	
	@Override
	public void register ( ) {
		for ( String packet_class_name : packet_class_names ) {
			PacketChannelHandler.addPacketListener (
					packet_class_name , PacketListener.Priority.HIGHEST , handle );
		}
	}
	
	@Override
	public void unregister ( ) {
		PacketChannelHandler.removePacketListener ( handle );
	}
}