package es.outlook.adriansrj.battleroyale.packet.sender;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftServer;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author AdrianSR / 26/10/2021 / 06:26 p. m.
 */
class PacketSenderServiceHandle_v1_16_R1 extends PacketSenderServiceHandleBase {
	
	/**
	 * @author AdrianSR / 09/09/2021 / 10:27 p. m.
	 */
	protected static class FakeEntityTrackerEntry extends EntityTrackerEntry {
		
		private static final Consumer < Packet < ? > > VOID_PACKET_CONSUMER = packet -> { };
		
		public FakeEntityTrackerEntry ( Entity entity ) {
			super ( ( WorldServer ) entity.getWorld ( ) , entity , 0 , false ,
					VOID_PACKET_CONSUMER , new HashSet <> ( ) );
		}
	}
	
	public PacketSenderServiceHandle_v1_16_R1 ( ) {
		// java 16 and its constructor system!
	}
	
	@Override
	public org.bukkit.entity.Entity spawnEntity ( EntityType type , double x , double y , double z , float yaw ,
			float pitch , Consumer < org.bukkit.entity.Entity > modifier ) {
		// the world doesn't matter here
		CraftWorld world = ( CraftWorld ) Bukkit.getWorlds ( ).get ( 0 );
		Entity result = world.createEntity (
				new Location ( world , x , y , z , yaw , pitch ) , type.getEntityClass ( ) );
		CraftEntity craft_result = CraftEntity.getEntity ( ( ( CraftServer ) Bukkit.getServer ( ) ) , result );
		
		// apply modifier
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
		Packet < ? >           packet  = handle.O ( ); // spawn packet
		
		// sending spawn packet
		send ( player , packet );
		// sending update packet
		tracker.a ( update_packet -> send ( player , update_packet ) , ( ( CraftPlayer ) player ).getHandle ( ) );
	}
	
	@Override
	public void sendEntityTeleportPacket ( Player player , int id , boolean on_ground , double x , double y ,
			double z , float yaw , float pitch ) {
		PacketDataSerializer        data   = new PacketDataSerializer ( Unpooled.buffer ( ) );
		PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport ( );
		
		// entity id
		data.d ( id );
		// x, y, z
		data.writeDouble ( x );
		data.writeDouble ( y );
		data.writeDouble ( z );
		// yaw, pitch
		data.writeByte ( ( byte ) ( yaw * 256.0F / 360.0F ) );
		data.writeByte ( ( byte ) ( pitch * 256.0F / 360.0F ) );
		// on ground flag
		data.writeBoolean ( on_ground );
		
		// then sending
		try {
			packet.a ( data );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		}
		
		send ( player , packet );
	}
	
	@Override
	public void sendEntityMetadataPacket ( Player player , int id , EntityType type , int index , boolean flag ) {
		CraftWorld world = ( CraftWorld ) player.getWorld ( );
		Entity fake = world.createEntity (
				player.getLocation ( ) , type.getEntityClass ( ) );
		
		// watching
		fake.setFlag ( index , flag );
		
		// then sending. the boolean parameter must be true as
		// we want it to send all the data in the data watcher.
		send ( player , new PacketPlayOutEntityMetadata ( id , fake.getDataWatcher ( ) , true ) );
	}
	
	@Override
	public void sendEntityMetadataPacket ( Player player , org.bukkit.entity.Entity entity , int id ) {
		CraftEntity craft = ( CraftEntity ) entity;
		
		// then sending. the boolean parameter must be true as
		// we want it to send all the data in the data watcher.
		send ( player , new PacketPlayOutEntityMetadata ( id , craft.getHandle ( ).getDataWatcher ( ) , true ) );
	}
	
	@Override
	public void sendEntityEquipmentPacket ( Player player , org.bukkit.entity.LivingEntity entity , int id ) {
		CraftLivingEntity                         craft = ( CraftLivingEntity ) entity;
		List < Pair < EnumItemSlot, ItemStack > > list  = new ArrayList <> ( );
		
		for ( EnumItemSlot slot : EnumItemSlot.values ( ) ) {
			ItemStack stack = craft.getHandle ( ).getEquipment ( slot );
			
			list.add ( Pair.of ( slot , ( stack != null ? stack : ItemStack.b ) ) );
		}
		
		send ( player , new PacketPlayOutEntityEquipment ( id , list ) );
	}
	
	@Override
	public void sendUpdatePacket ( Player player , org.bukkit.entity.Entity entity ) {
		Entity                 handle  = ( ( CraftEntity ) entity ).getHandle ( );
		FakeEntityTrackerEntry tracker = new FakeEntityTrackerEntry ( handle );
		
		tracker.a ( update_packet -> send ( player , update_packet ) ,
					( ( CraftPlayer ) player ).getHandle ( ) );
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