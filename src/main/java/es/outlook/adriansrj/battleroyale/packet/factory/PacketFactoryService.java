package es.outlook.adriansrj.battleroyale.packet.factory;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;

/**
 * @author AdrianSR / 28/09/2021 / 03:29 p. m.
 */
public final class PacketFactoryService extends PluginHandler implements PacketFactoryServiceHandle {
	
	public static PacketFactoryService getInstance ( ) {
		return getPluginHandler ( PacketFactoryService.class );
	}
	
	private final PacketFactoryServiceHandle handle;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public PacketFactoryService ( BattleRoyale plugin ) {
		super ( plugin );
		this.handle = PacketFactoryServiceHandle.getNewHandle ( );
	}
	
	@Override
	public Object createEntityTeleportPacket ( int entity_id , boolean on_ground , double x , double y , double z ,
			float yaw , float pitch ) {
		return handle.createEntityTeleportPacket ( entity_id , on_ground , x , y , z , yaw , pitch );
	}
	
	@Override
	public Object createEntityAttachPacket ( int entity_id , int leash_holder_id ) {
		return handle.createEntityAttachPacket ( entity_id , leash_holder_id );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
