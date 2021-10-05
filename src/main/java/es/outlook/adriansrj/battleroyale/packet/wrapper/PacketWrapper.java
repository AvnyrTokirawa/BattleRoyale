package es.outlook.adriansrj.battleroyale.packet.wrapper;

import es.outlook.adriansrj.core.util.reflection.bukkit.BukkitReflection;
import org.bukkit.entity.Player;

/**
 * @author AdrianSR / 28/09/2021 / 03:46 p. m.
 */
public abstract class PacketWrapper {
	
	public abstract Class < ? > getPacketClass ( );
	
	public abstract Object createInstance ( );
	
	public void send ( Player player ) {
		BukkitReflection.sendPacket ( player , createInstance ( ) );
	}
}