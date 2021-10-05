package es.outlook.adriansrj.battleroyale.packet.reader;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.packet.wrapper.out.PacketOutEntityTeleport;
import es.outlook.adriansrj.core.handler.PluginHandler;

/**
 * @author AdrianSR / 28/09/2021 / 03:29 p. m.
 */
public final class PacketReaderService extends PluginHandler implements PacketReaderServiceHandle {
	
	public static PacketReaderService getInstance ( ) {
		return getPluginHandler ( PacketReaderService.class );
	}
	
	private final PacketReaderServiceHandle handle;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public PacketReaderService ( BattleRoyale plugin ) {
		super ( plugin );
		this.handle = PacketReaderServiceHandle.getNewHandle ( );
	}
	
	@Override
	public PacketOutEntityTeleport readEntityTeleportPacket ( Object packet ) {
		return handle.readEntityTeleportPacket ( packet );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
	
}
