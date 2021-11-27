package es.outlook.adriansrj.battleroyale.parachute.creator;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.packet.sender.PacketSenderService;
import es.outlook.adriansrj.battleroyale.parachute.custom.*;
import es.outlook.adriansrj.battleroyale.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author AdrianSR / 23/11/2021 / 12:14 p. m.
 */
class ParachuteShapeDisplayerCustom extends ParachuteShapeDisplayer {
	
	/**
	 * @author AdrianSR / 09/09/2021 / 09:42 p. m.
	 */
	protected static class Part {
		
		/**
		 * The height of the ArmorStand instances. It means that this constant has
		 * the height of a small ArmorStand entity as value.
		 */
		protected static final float HANDLES_HEIGHT = 1.032F;
		
		protected final ArmorStand                    handle;
		protected final int                           id;
		protected final ParachuteShapeDisplayerCustom displayer;
		protected final ParachuteCustomPartPosition   position;
		protected final PositionData                  position_data;
		
		public Part ( org.bukkit.entity.Player player , ParachuteShapeDisplayerCustom displayer ,
				ParachuteCustomPartPosition position , ItemStack shape ) {
			Validate.isTrue ( Bukkit.isPrimaryThread ( ) , "must run on server thread" );
			
			this.displayer     = displayer;
			this.position      = position;
			this.position_data = new PositionData ( position );
			
			// location
			Location location = ParachuteCreationStage.STAGE_DISPLAYER_LOCATION
					.toLocation ( player.getWorld ( ) );
			
			double x        = location.getX ( );
			double y        = location.getY ( );
			double z        = location.getZ ( );
			float  rotation = 0.0F;
			
			float part_rotation = DirectionUtil.normalize ( rotation );
			float angle         = DirectionUtil.normalize ( ( part_rotation - position_data.angle ) + 90F );
			
			x = x + ( ( float ) Math.cos ( Math.toRadians ( angle ) ) * position_data.xz_distance );
			y = ( ( y + position_data.y_distance ) - HANDLES_HEIGHT ) + 0.35F;
			z = z + ( ( float ) Math.sin ( Math.toRadians ( angle ) ) * position_data.xz_distance );
			
			// spawning
			this.handle = ( ArmorStand ) PacketSenderService.getInstance ( ).sendSpawnEntityPacket (
					player , EntityType.ARMOR_STAND , x , y , z , 0.0F , 0.0F , entity -> {
						ArmorStand stand = ( ( ArmorStand ) entity );
						
						// shape rotation
						stand.setHeadPose ( new EulerAngle (
								Math.toRadians ( position.getPitch ( ) ) ,
								Math.toRadians ( position.getYaw ( ) ) ,
								Math.toRadians ( position.getRoll ( ) ) ) );
						
						// rest
						stand.setGravity ( false );
						stand.setVisible ( false );
						stand.setSmall ( true );
						stand.getEquipment ( ).setHelmet ( shape );
						EntityReflection.setSilent ( stand , true );
					} );
			this.id     = handle.getEntityId ( );
			
			// equipment packet
			PacketSenderService.getInstance ( ).sendEntityEquipmentPacket ( player , handle );
			// part metadata packet
			PacketSenderService.getInstance ( ).sendEntityMetadataPacket ( player , handle );
		}
		
		public void destroy ( ) {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) ->
					displayer.player.getBukkitPlayerOptional ( ).ifPresent (
							bukkit -> PacketSenderService.getInstance ( ).sendDestroyEntityPacket ( bukkit , handle ) ) );
		}
		
		@Override
		public boolean equals ( Object o ) {
			if ( this == o ) { return true; }
			if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
			Part part = ( Part ) o;
			return id == part.id;
		}
		
		@Override
		public int hashCode ( ) {
			return Objects.hash ( id );
		}
	}
	
	private final Set < Part > parts = new HashSet <> ( );
	private       ArmorStand   seat;
	private       Zombie       passenger;
	
	ParachuteShapeDisplayerCustom ( Player player , ParachuteCustom configuration ) {
		super ( player , configuration );
	}
	
	@SuppressWarnings ( "deprecation" )
	@Override
	public void show ( ) {
		org.bukkit.entity.Player player        = this.player.getBukkitPlayer ( );
		ParachuteCustom          configuration = ( ParachuteCustom ) this.configuration;
		ParachuteCustomModel     model         = configuration.getModel ( );
		
		if ( player != null && model != null ) {
			PacketSenderService packet_service = PacketSenderService.getInstance ( );
			
			// seat & passenger
			Location location = ParachuteCreationStage.STAGE_DISPLAYER_LOCATION
					.toLocation ( player.getWorld ( ) );
			
			this.passenger = ( Zombie ) packet_service.sendSpawnEntityPacket (
					player , EntityType.ZOMBIE , location.getX ( ) , location.getY ( ) , location.getZ ( ) ,
					0.0F , 0.0F , entity -> EntityReflection.setSilent ( entity , true ) );
			
			this.seat = ( ArmorStand ) packet_service.sendSpawnEntityPacket (
					player , EntityType.ARMOR_STAND , location.getX ( ) , location.getY ( ) , location.getZ ( ) ,
					0.0F , 0.0F , entity -> {
						ArmorStand stand = ( ArmorStand ) entity;
						
						try {
							stand.addPassenger ( passenger );
						} catch ( NoSuchMethodError ex ) {
							// legacy versions
							stand.setPassenger ( passenger );
						}
						
						stand.setGravity ( false );
						stand.setVisible ( false );
						stand.setSmall ( true );
						EntityReflection.setSilent ( stand , true );
					} );
			
			packet_service.sendEntityMetadataPacket ( player , seat );
			packet_service.sendEntityMetadataPacket ( player , passenger );
			packet_service.sendMountPacket ( player , seat , passenger );
			
			// parts
			for ( ParachuteCustomModelPart part : model.getParts ( ).values ( ) ) {
				if ( part != null && part.isValid ( ) ) {
					parts.add ( new Part ( player , this , part.getPosition ( ) ,
										   part.getShape ( ).toItemStack ( this.player ) ) );
				}
			}
		}
	}
	
	@Override
	public void destroy ( ) {
		// destroying passenger & seat
		if ( passenger != null ) {
			destroy ( passenger );
		}
		
		if ( seat != null ) {
			destroy ( seat );
		}
		
		// destroying parts
		parts.forEach ( Part :: destroy );
		parts.clear ( );
	}
	
	// ----- utils
	
	private void destroy ( Entity entity ) {
		player.getBukkitPlayerOptional ( ).ifPresent (
				bukkit -> PacketSenderService.getInstance ( ).sendDestroyEntityPacket ( bukkit , entity ) );
	}
}
