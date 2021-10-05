package es.outlook.adriansrj.battleroyale.util.packet.interceptor;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

import java.util.HashSet;
import java.util.Set;

/**
 * Useful class for intercepting packets using the <b>ProtocolLib</b> API.
 *
 * @author AdrianSR / 23/09/2021 / 04:45 p. m.
 */
public class PacketInterceptorProtocolLib extends PacketInterceptor {
	
	protected final PacketListener handle;
	
	/**
	 * Constructs the packet interceptor from the name
	 * of the classes of the packets.
	 *
	 * @param packet_class_names the name of the classes of the packets.
	 */
	public PacketInterceptorProtocolLib ( String... packet_class_names ) {
		super ( packet_class_names );
		
		// initializing handler
		Set < PacketType > types = new HashSet <> ( );
		
		for ( String packet_class_name : packet_class_names ) {
			PacketType type = null;
			
			// finding out packet type
			for ( PacketType other : PacketType.values ( ) ) {
				if ( other.getPacketClass ( ) != null && other.getPacketClass ( )
						.getSimpleName ( ).equals ( packet_class_name ) ) {
					type = other;
					break;
				}
			}
			
			if ( type != null ) {
				types.add ( type );
			} else {
				throw new IllegalArgumentException ( "Unknown packet '" + packet_class_name + "'" );
			}
		}
		
		this.handle = new PacketAdapter ( BattleRoyale.getInstance ( ) , ListenerPriority.HIGHEST , types ) {
			
			@Override
			public void onPacketSending ( PacketEvent event ) {
				for ( PacketInterceptorAcceptor acceptor : acceptors ) {
					if ( acceptor instanceof PacketInterceptorInjector ) {
						Object result = ( ( PacketInterceptorInjector ) acceptor ).inject (
								event.getPacket ( ).getHandle ( ) );
						
						if ( result != null && event.getPacketType ( ).getPacketClass ( )
								.isAssignableFrom ( result.getClass ( ) ) ) {
							event.setPacket ( new PacketContainer ( event.getPacketType ( ) , result ) );
						}
					} else if ( acceptor.accept ( event.getPacket ( ).getHandle ( ) ) ) {
						event.setCancelled ( true );
						break;
					}
				}
			}
		};
	}
	
	@Override
	public void register ( ) {
		ProtocolLibrary.getProtocolManager ( ).addPacketListener ( handle );
	}
	
	@Override
	public void unregister ( ) {
		ProtocolLibrary.getProtocolManager ( ).removePacketListener ( handle );
	}
}