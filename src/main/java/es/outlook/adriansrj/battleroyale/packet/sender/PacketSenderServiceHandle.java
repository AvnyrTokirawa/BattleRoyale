package es.outlook.adriansrj.battleroyale.packet.sender;

import es.outlook.adriansrj.core.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

/**
 * @author AdrianSR / 09/09/2021 / 09:59 p. m.
 */
interface PacketSenderServiceHandle {
	
	static PacketSenderServiceHandle getNewHandle ( ) {
		try {
			return Class.forName ( PacketSenderServiceHandle.class.getPackage ( ).getName ( )
										   + ".PacketSenderServiceHandle_" + Version.getServerVersion ( ).name ( ) )
					.asSubclass ( PacketSenderServiceHandle.class )
					.getConstructor ( ).newInstance ( );
		} catch ( ClassNotFoundException | InvocationTargetException
				| InstantiationException | IllegalAccessException | NoSuchMethodException e ) {
			throw new IllegalStateException ( e );
		}
	}
	
	Entity spawnEntity ( EntityType type , double x , double y , double z ,
			float yaw , float pitch , Consumer < Entity > modifier );
	
	Entity sendSpawnEntityPacket ( Player player , EntityType type ,
			double x , double y , double z , float yaw , float pitch , Consumer < Entity > modifier );
	
	void sendSpawnEntityPacket ( Player player , Entity entity );
	
	void sendEntityRelativeMovePacket ( Player player , int id ,
			double x , double y , double z ,
			double previous_x , double previous_y , double previous_z ,
			boolean on_ground );
	
	void sendEntityRelativeMoveLookPacket ( Player player , int id ,
			double delta_x , double delta_y , double delta_z ,
			float yaw , float pitch ,
			boolean on_ground );
	
	void sendEntityTeleportPacket ( Player player , int id , boolean on_ground ,
			double x , double y , double z , float yaw , float pitch );
	
	void sendEntityMetadataPacket ( Player player , int id , EntityType type , int index , boolean flag );
	
	void sendEntityMetadataPacket ( Player player , Entity entity , int id );
	
	default void sendEntityMetadataPacket ( Player player , Entity entity ) {
		sendEntityMetadataPacket ( player , entity , entity.getEntityId ( ) );
	}
	
	void sendEntityEquipmentPacket ( Player player , LivingEntity entity , int id );
	
	default void sendEntityEquipmentPacket ( Player player , LivingEntity entity ) {
		sendEntityEquipmentPacket ( player , entity , EntityReflection.getEntityID ( entity ) );
	}
	
	void sendUpdatePacket ( Player player , Entity entity );
	
	void sendDestroyEntityPacket ( Player player , int id );
	
	default void sendDestroyEntityPacket ( Player player , Entity entity ) {
		sendDestroyEntityPacket ( player , EntityReflection.getEntityID ( entity ) );
	}
	
	void sendRespawnPacket ( Player player );
	
	void sendCameraPacket ( Player player , Entity camera );
}