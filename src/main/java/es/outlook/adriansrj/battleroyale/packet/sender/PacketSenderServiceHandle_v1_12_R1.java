package es.outlook.adriansrj.battleroyale.packet.sender;

import es.outlook.adriansrj.core.util.reflection.general.MethodReflection;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

/**
 * @author AdrianSR / 09/09/2021 / 10:00 p. m.
 */
class PacketSenderServiceHandle_v1_12_R1 extends PacketSenderServiceHandleBase {
	
	/**
	 * @author AdrianSR / 09/09/2021 / 10:27 p. m.
	 */
	protected static class FakeEntityTrackerEntry extends EntityTrackerEntry {
		
		public FakeEntityTrackerEntry ( Entity entity ) {
			super ( entity , 0 , 0 , 0 , false );
		}
		
		public Packet < ? > createSpawnPacket ( ) {
			try {
				return ( Packet < ? > ) MethodReflection.getAccessible (
						EntityTrackerEntry.class , "e" ).invoke ( this );
			} catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
				e.printStackTrace ( );
				return null;
			}
		}
		
		@Override
		public boolean c ( EntityPlayer entityplayer ) {
			return true;
		}
	}
	
	public PacketSenderServiceHandle_v1_12_R1 ( ) {
		// java 16 and its constructor system!
	}
	
	@Override
	public org.bukkit.entity.Entity spawnEntity ( EntityType type , double x , double y , double z , float yaw ,
			float pitch , Consumer < org.bukkit.entity.Entity > modifier ) {
		// the world doesn't matter here
		CraftWorld world = ( CraftWorld ) Bukkit.getWorlds ( ).get ( 0 );
		Entity result = world.createEntity (
				new Location ( world , x , y , z , yaw , pitch ) , type.getEntityClass ( ) );
		CraftEntity craft_result = CraftEntity.getEntity ( world.getHandle ( ).getServer ( ) , result );
		
		// applying modifier
		modifier.accept ( craft_result );
		
		return craft_result;
	}
	
	@Override
	public org.bukkit.entity.Entity sendSpawnEntityPacket ( Player player , EntityType type ,
			double x , double y , double z , float yaw , float pitch ,
			Consumer < org.bukkit.entity.Entity > modifier ) {
		return spawnEntity ( type , x , y , z , yaw , pitch , entity -> {
			Entity handle = ( ( CraftEntity ) entity ).getHandle ( );
			
			// applying modifier
			modifier.accept ( entity );
			
			// then sending packet
			handle.dead = false; // must not be dead
			
			sendSpawnEntityPacket ( player , entity );
		} );
	}
	
	@Override
	public void sendSpawnEntityPacket ( Player player , org.bukkit.entity.Entity entity ) {
		Entity                 handle  = ( ( CraftEntity ) entity ).getHandle ( );
		FakeEntityTrackerEntry tracker = new FakeEntityTrackerEntry ( handle );
		Packet < ? >           packet  = tracker.createSpawnPacket ( );
		
		// sending spawn packet
		send ( player , packet );
		// sending update packet
		tracker.updatePlayer ( ( ( CraftPlayer ) player ).getHandle ( ) );
	}
	
//	@Override
	//	public void sendEntityRelativeMovePacket ( Player player , int id ,
	//			double x , double y , double z ,
	//			double previous_x , double previous_y , double previous_z ,
	//			boolean on_ground ) {
	//		send ( player , new PacketPlayOutEntity.PacketPlayOutRelEntityMove (
	//				id ,
	//				EntityTracker.a ( x - previous_x ) ,
	//				EntityTracker.a ( y - previous_y ) ,
	//				EntityTracker.a ( z - previous_z ) ,
	//				false
	//		) );
	//	}
	//
	//	@Override
	//	public void sendEntityRelativeMoveLookPacket ( Player player , int id , double delta_x , double delta_y ,
	//			double delta_z , float yaw , float pitch , boolean on_ground ) {
	//		send ( player , new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook (
	//				id ,
	//				EntityTracker.a ( delta_x ) ,
	//				EntityTracker.a ( delta_y ) ,
	//				EntityTracker.a ( delta_z ) ,
	//				( byte ) MathHelper.d ( yaw * 256.0F / 360.0F ) ,
	//				( byte ) MathHelper.d ( pitch * 256.0F / 360.0F ) ,
	//				false
	//		) );
	//	}
	
	@Override
	public void sendEntityTeleportPacket ( Player player , int id , boolean on_ground , double x , double y ,
			double z , float yaw , float pitch ) {
		send ( player , factory_service.createEntityTeleportPacket (
				id , on_ground , x , y , z , yaw , pitch ) );
	}
	
	@Override
	public void sendEntityMetadataPacket ( Player player , int id , EntityType type , int index , boolean flag ) {
		CraftWorld world = ( CraftWorld ) player.getWorld ( );
		net.minecraft.server.v1_12_R1.Entity fake = world.createEntity (
				player.getLocation ( ) , type.getEntityClass ( ) );
		
		// watching
		fake.setFlag ( index , flag );
		
		// then sending
		send ( player , new PacketPlayOutEntityMetadata ( id , fake.getDataWatcher ( ) , true ) );
	}
	
	@Override
	public void sendEntityMetadataPacket ( Player player , org.bukkit.entity.Entity entity , int id ) {
		CraftEntity craft = ( CraftEntity ) entity;
		
		// then sending
		send ( player , new PacketPlayOutEntityMetadata ( id , craft.getHandle ( ).getDataWatcher ( ) , true ) );
	}
	
	@Override
	public void sendEntityEquipmentPacket ( Player player , org.bukkit.entity.LivingEntity entity , int id ) {
		CraftLivingEntity craft = ( CraftLivingEntity ) entity;
		
		for ( EnumItemSlot slot : EnumItemSlot.values ( ) ) {
			ItemStack stack = craft.getHandle ( ).getEquipment ( slot );
			
			send ( player , new PacketPlayOutEntityEquipment (
					id , slot , stack != null ? stack : ItemStack.a ) );
		}
	}
	
	@Override
	public void sendUpdatePacket ( Player player , org.bukkit.entity.Entity entity ) {
		Entity                 handle  = ( ( CraftEntity ) entity ).getHandle ( );
		FakeEntityTrackerEntry tracker = new FakeEntityTrackerEntry ( handle );
		
		tracker.updatePlayer ( ( ( CraftPlayer ) player ).getHandle ( ) );
	}
	
	@Override
	public void sendDestroyEntityPacket ( Player player , int id ) {
		send ( player , new PacketPlayOutEntityDestroy ( id ) );
	}
	
	@Override
	public void sendRespawnPacket ( Player player ) {
		( ( CraftPlayer ) player ).getHandle ( ).playerConnection.a ( new PacketPlayInClientCommand (
				PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN ) );
	}
	
	@Override
	public void sendCameraPacket ( Player player , org.bukkit.entity.Entity camera ) {
		send ( player , new PacketPlayOutCamera ( ( ( CraftEntity ) camera ).getHandle ( ) ) );
	}
}