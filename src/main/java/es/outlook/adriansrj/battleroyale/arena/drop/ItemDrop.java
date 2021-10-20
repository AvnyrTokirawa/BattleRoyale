package es.outlook.adriansrj.battleroyale.arena.drop;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.packet.sender.PacketSenderService;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.battleroyale.util.itemstack.ItemStackUtil;
import es.outlook.adriansrj.battleroyale.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.player.PlayerWrapper;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.Objects;

/**
 * @author AdrianSR / 15/10/2021 / 07:42 p. m.
 */
public class ItemDrop {
	
	/**
	 * @author AdrianSR / 15/10/2021 / 09:42 p. m.
	 */
	protected static class ItemRotationMatcher {
		
		private ItemRotationMatcher ( ) {
			// singleton
		}
		
		protected static EulerAngle match ( ItemStack item ) {
			UniversalMaterial wrapper = UniversalMaterial.match ( item );
			EulerAngle rotation = new EulerAngle (
					Math.toRadians ( 0 ) , 0 , Math.toRadians ( 270.0D ) );
			
			// blocks
			if ( item.getType ( ).isBlock ( ) ) {
				rotation = new EulerAngle (
						Math.toRadians ( 345.0D ) ,
						Math.toRadians ( 315.0D ) ,
						0.0D );
			}
			
			// heads
			if ( wrapper.name ( ).contains ( "_SKULL_ITEM" ) ) {
				rotation = new EulerAngle ( 0 , 0 , 0 )
						.setX ( Math.toRadians ( -45.0D ) )
						.setY ( Math.toRadians ( -100 ) );
			}
			
			// armor
			if ( wrapper.name ( ).contains ( "_LEGGINGS" )
					|| wrapper.name ( ).contains ( "_BOOTS" )
					|| wrapper.name ( ).contains ( "_HELMET" )
					|| wrapper.name ( ).contains ( "_CHESTPLATE" ) ) {
				rotation = new EulerAngle ( 0 , 0 , 0 )
						.setY ( Math.toRadians ( -90.0D ) );
			}
			return rotation;
		}
	}
	
	protected final Item              handle;
	protected       ArmorStand        holder;
	protected final BattleRoyaleArena arena;
	protected       boolean           initialized;
	
	public ItemDrop ( Item handle , BattleRoyaleArena arena ) {
		Validate.notNull ( handle , "handle cannot be null" );
		Validate.isTrue ( !handle.isDead ( ) , "handle cannot be dead" );
		Validate.notNull ( arena , "arena cannot be null" );
		
		this.handle = handle;
		this.arena  = arena;
		
		// initializing holder
		Location spawn = handle.getLocation ( );
		this.holder = ( ArmorStand ) PacketSenderService.getInstance ( ).spawnEntity (
				EntityType.ARMOR_STAND , spawn.getX ( ) , spawn.getY ( ) , spawn.getZ ( ) ,
				0.0F , 0.0F , entity -> {
					ArmorStand stand   = ( ArmorStand ) entity;
					ItemStack  content = handle.getItemStack ( );
					
					stand.setBasePlate ( false );
					stand.setGravity ( false );
					stand.setSmall ( true );
					stand.setVisible ( false );
					
					if ( EnumMainConfiguration.GAME_ENHANCED_DROPS_CUSTOM_MODEL.getAsBoolean ( ) ) {
						stand.getEquipment ( ).setItemInMainHand ( content );
						stand.setRightArmPose ( ItemRotationMatcher.match ( content ) );
					}
					
					// display name
					String display_name = ItemStackUtil.extractName ( content , false );
					
					if ( EnumMainConfiguration.GAME_ENHANCED_DROPS_VISIBLE_NAME.getAsBoolean ( )
							&& StringUtil.isNotBlank ( display_name ) ) {
						stand.setCustomName ( display_name );
						stand.setCustomNameVisible ( true );
					}
				} );
	}
	
	/**
	 * Initializes this drop.
	 * <br>
	 * The {@link #handle} will be hidden to players
	 * in the arena, and the corresponding armor stand
	 * displaying the enhanced drop will be spawned for them.
	 */
	protected void initialize ( ) {
		if ( !initialized ) {
			PacketSenderService packet_sender = PacketSenderService.getInstance ( );
			
			arena.getPlayers ( true ).stream ( ).map ( PlayerWrapper :: getPlayer ).filter (
					Objects :: nonNull ).forEach ( player -> {
				// hiding handle
				if ( EnumMainConfiguration.GAME_ENHANCED_DROPS_CUSTOM_MODEL.getAsBoolean ( ) ) {
					SchedulerUtil.scheduleSyncDelayedTask (
							( ) -> packet_sender.sendDestroyEntityPacket ( player , handle ) );
				}
				
				// showing holder
				packet_sender.sendSpawnEntityPacket ( player , holder );
				packet_sender.sendEntityMetadataPacket ( player , holder );
				packet_sender.sendEntityEquipmentPacket ( player , holder );
			} );
			
			this.initialized = true;
		}
	}
	
	protected void tick ( ) {
		if ( !initialized || holder == null ) {
			return;
		}
		
		if ( !handle.isDead ( ) ) {
			PacketSenderService packet_sender = PacketSenderService.getInstance ( );
			
			arena.getPlayers ( true ).stream ( ).map ( PlayerWrapper :: getPlayer ).filter (
					Objects :: nonNull ).forEach ( player -> {
				// updating location
				Location location = handle.getLocation ( );
				
				packet_sender.sendEntityTeleportPacket (
						player , EntityReflection.getEntityID ( holder ) , handle.isOnGround ( ) ,
						location.getX ( ) , location.getY ( ) - 0.35D , location.getZ ( ) ,
						location.getYaw ( ) , location.getPitch ( ) );
			} );
		} else {
			// handle has died
			remove ( );
		}
	}
	
	public Item getHandle ( ) {
		return handle;
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	public boolean exists ( ) {
		return !handle.isDead ( );
	}
	
	public boolean remove ( ) {
		if ( exists ( ) || holder != null ) {
			if ( holder != null ) {
				final ArmorStand final_reference = this.holder;
				
				arena.getPlayers ( true ).stream ( ).map ( PlayerWrapper :: getPlayer ).filter (
						Objects :: nonNull ).forEach ( player -> SchedulerUtil.scheduleSyncDelayedTask (
						( ) -> PacketSenderService.getInstance ( ).sendDestroyEntityPacket ( player , final_reference ) ) );
				holder = null;
			}
			
			if ( !handle.isDead ( ) ) {
				handle.remove ( );
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean equals ( Object object ) {
		if ( this != object ) {
			if ( object instanceof ItemDrop ) {
				return ( ( ItemDrop ) object )
						.handle.getUniqueId ( ).equals ( handle.getUniqueId ( ) );
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	
	@Override
	public int hashCode ( ) {
		return handle.getUniqueId ( ).hashCode ( );
	}
}