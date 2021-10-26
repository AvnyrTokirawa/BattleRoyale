package es.outlook.adriansrj.battleroyale.packet.sender;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Service for creating and sending packets.
 *
 * @author AdrianSR / 09/09/2021 / 09:50 p. m.
 */
public final class PacketSenderService extends PluginHandler implements PacketSenderServiceHandle {
	
	public static PacketSenderService getInstance ( ) {
		return getPluginHandler ( PacketSenderService.class );
	}
	
	private final PacketSenderServiceHandle handle;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public PacketSenderService ( BattleRoyale plugin ) {
		super ( plugin );
		this.handle = PacketSenderServiceHandle.getNewHandle ( );
	}
	
	@Override
	public Entity spawnEntity ( EntityType type , double x , double y , double z , float yaw , float pitch ,
			Consumer < Entity > modifier ) {
		return handle.spawnEntity ( type , x , y , z , yaw , pitch , modifier );
	}
	
	@Override
	public Entity sendSpawnEntityPacket ( Player player , EntityType type ,
			double x , double y , double z , float yaw , float pitch , Consumer < Entity > modifier ) {
		return handle.sendSpawnEntityPacket ( player , type , x , y , z , yaw , pitch , modifier );
	}
	
	@Override
	public void sendSpawnEntityPacket ( Player player , Entity entity ) {
		handle.sendSpawnEntityPacket ( player , entity );
	}
	
	@Override
	public void sendEntityTeleportPacket ( Player player , int id , boolean on_ground , double x , double y ,
			double z , float yaw , float pitch ) {
		handle.sendEntityTeleportPacket ( player , id , on_ground , x , y , z , yaw , pitch );
	}
	
	@Override
	public void sendEntityMetadataPacket ( Player player , int id , EntityType type , int index , boolean flag ) {
		handle.sendEntityMetadataPacket ( player , id , type , index , flag );
	}
	
	@Override
	public void sendEntityMetadataPacket ( Player player , Entity entity , int id ) {
		handle.sendEntityMetadataPacket ( player , entity , id );
	}
	
	@Override
	public void sendEntityEquipmentPacket ( Player player , LivingEntity entity , int id ) {
		handle.sendEntityEquipmentPacket ( player , entity , id );
	}
	
	@Override
	public void sendEntityAttachPacket ( Player player , int id , int leash_holder_id ) {
		handle.sendEntityAttachPacket ( player , id , leash_holder_id );
	}
	
	@Override
	public void sendUpdatePacket ( Player player , Entity entity ) {
		handle.sendUpdatePacket ( player , entity );
	}
	
	@Override
	public void sendDestroyEntityPacket ( Player player , int id ) {
		handle.sendDestroyEntityPacket ( player , id );
	}
	
	@Override
	public void sendRespawnPacket ( Player player ) {
		handle.sendRespawnPacket ( player );
	}
	
	@Override
	public void sendCameraPacket ( Player player , Entity camera ) {
		handle.sendCameraPacket ( player , camera );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}