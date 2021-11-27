package es.outlook.adriansrj.battleroyale.packet.sender;

import es.outlook.adriansrj.battleroyale.packet.wrapper.out.PacketOutEntityAttach;
import es.outlook.adriansrj.battleroyale.packet.wrapper.out.PacketOutMount;
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
	
	default void sendEntityAttachPacket ( Player player , int id , int leash_holder_id ) {
		new PacketOutEntityAttach ( id , leash_holder_id ).send ( player );
	}
	
	default void sendEntityAttachPacket ( Player player , Entity entity , Entity leash_holder ) {
		sendEntityAttachPacket ( player , EntityReflection.getEntityID ( entity ) ,
								 EntityReflection.getEntityID ( leash_holder ) );
	}
	
	default void sendMountPacket ( Player player , int id , int... passengers_ids ) {
		new PacketOutMount ( id , passengers_ids ).send ( player );
	}
	
	default void sendMountPacket ( Player player , Entity entity , Entity... passengers ) {
		new PacketOutMount ( entity , passengers ).send ( player );
	}
	
	void sendUpdatePacket ( Player player , Entity entity );
	
	void sendDestroyEntityPacket ( Player player , int id );
	
	default void sendDestroyEntityPacket ( Player player , Entity entity ) {
		sendDestroyEntityPacket ( player , EntityReflection.getEntityID ( entity ) );
	}
	
	void sendRespawnPacket ( Player player );
	
	void sendCameraPacket ( Player player , Entity camera );
}