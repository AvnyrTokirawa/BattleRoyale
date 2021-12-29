package es.outlook.adriansrj.battleroyale.bus.pet;

import es.outlook.adriansrj.battleroyale.bus.BusInstanceBase;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.event.player.PlayerJumpOffBusEvent;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.packet.sender.PacketSenderService;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.battleroyale.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.entity.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link BusPet} instance.
 *
 * @author AdrianSR / 23/09/2021 / 09:01 p. m.
 * @see BusPet
 */
public class BusPetInstance extends BusInstanceBase < BusPet > implements Listener {
	
	protected final Player  player;
	protected       Entity  shape;
	protected       Chicken seat;
	protected       boolean in_queue;
	
	protected BusPetInstance ( BusPet configuration , Player player ) {
		super ( configuration );
		
		Validate.notNull ( configuration.getShape ( ) , "configuration returned a null shape" );
		Validate.notNull ( player , "player cannot be null" );
		
		this.player = player;
	}
	
	public Player getPlayer ( ) {
		return player;
	}
	
	@Override
	public boolean isPassenger ( Player player ) {
		return Objects.equals ( player , this.player ) && seat != null
				&& !seat.isDead ( ) && EntityUtil.getPassengers ( seat ).size ( ) > 0;
	}
	
	@Override
	public void start ( ) {
		syncCheck ( );
		super.start ( );
		
		// we must listen for PlayerChangeWorldEvent
		Bukkit.getPluginManager ( ).registerEvents (
				this , BattleRoyale.getInstance ( ) );
		
		// putting player
		this.putPlayer ( );
	}
	
	protected void putPlayer ( ) {
		syncCheck ( );
		org.bukkit.entity.Player player = this.player.getBukkitPlayer ( );
		
		if ( player != null ) {
			// as the displacement task is constantly
			// running even when the player is not in
			// the seat, we have to make sure that the
			// finish() method will not be called before
			// even spawning the seat.
			in_queue = true;
			
			// player must be in the world of the arena, otherwise
			// we will need to teleport them, and wait until the teleportation
			// process is finished to actually put the player.
			if ( Objects.equals ( player.getWorld ( ) , arena.getWorld ( ) ) ) {
				Bukkit.getScheduler ( ).scheduleSyncDelayedTask (
						BattleRoyale.getInstance ( ) , ( ) -> putPlayer0 ( player ) );
			} else {
				// making sure chunk is loaded
				Location location = getLocation ( ).toLocation (
						arena.getWorld ( ) , getSpawn ( ).getYaw ( ) , 0.0F );
				Chunk chunk = location.getChunk ( );
				
				if ( !chunk.isLoaded ( ) ) {
					chunk.load ( true );
				}
				
				// changing world
				player.teleport ( location );
			}
		}
	}
	
	// event handler responsible for putting the player on the seat
	// when done changing of world.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onEnterWorld ( PlayerChangedWorldEvent event ) {
		org.bukkit.entity.Player player = event.getPlayer ( );
		
		// then putting
		if ( Objects.equals ( player.getWorld ( ).getWorldFolder ( ) , arena.getWorld ( ).getWorldFolder ( ) )
				&& in_queue && Objects.equals ( player.getUniqueId ( ) , this.player.getUniqueId ( ) ) ) {
			Bukkit.getScheduler ( ).scheduleSyncDelayedTask (
					BattleRoyale.getInstance ( ) , ( ) -> putPlayer0 ( player ) , 2L );
		}
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onDamage ( EntityDamageEvent event ) {
		if ( seat != null && Objects.equals (
				event.getEntity ( ).getUniqueId ( ) , seat.getUniqueId ( ) ) ) {
			event.setCancelled ( true );
		}
	}
	
	@SuppressWarnings ( "deprecation" )
	protected void putPlayer0 ( org.bukkit.entity.Player player ) {
		syncCheck ( );
		
		player.setAllowFlight ( true );
		player.setFlying ( true );
		
		this.seat = arena.getWorld ( ).spawn (
				getLocation ( ).toLocation ( arena.getWorld ( ) , spawn.getYaw ( ) , 0.0F ) ,
				Chicken.class );
		
		this.seat.setRemoveWhenFarAway ( false );
		this.seat.setAdult ( );
		this.seat.addPotionEffect ( new PotionEffect (
				PotionEffectType.INVISIBILITY , Integer.MAX_VALUE , 0 ) );
		
		try {
			this.seat.addPassenger ( player );
		} catch ( NoSuchMethodError ex ) {
			// legacy versions
			this.seat.setPassenger ( player );
		}
		
		// seat spawned, we can now
		// disable the queue flag
		this.in_queue = false;
		
		// hiding player for others
		arena.getPlayers ( false ).stream ( ).map ( Player :: getBukkitPlayer )
				.filter ( Objects :: nonNull )
				.filter ( other -> !Objects.equals ( other.getUniqueId ( ) , player.getUniqueId ( ) )
						&& other.canSee ( player ) )
				.forEach ( other -> Player.getPlayer ( other ).hidePlayer ( player ) );
		
		// blindness effect
		EntityUtil.addPotionEffectForcing (
				player , PotionEffectType.SLOW , Duration.ofSeconds ( 3 ) , 10 );
		EntityUtil.addPotionEffectForcing (
				player , PotionEffectType.BLINDNESS , Duration.ofSeconds ( 3 ) , 0 );
		
		// showing shape
		PacketSenderService packet_service = PacketSenderService.getInstance ( );
		Entity              shape          = updateShape ( );
		
		// shape and seat positions must match at
		// the moment of spawning, otherwise, the
		// movement packets outgoing from the server
		// will cause the shape to be away from the seat.
		Location seat_position = seat.getLocation ( );
		
		EntityReflection.setLocation ( shape , new Vector (
				seat_position.getX ( ) ,
				seat_position.getY ( ) + ( seat.getEyeHeight ( ) - EntityReflection.getHeight ( shape ) ) ,
				seat_position.getZ ( ) ) );
		EntityReflection.setYawPitch ( shape , spawn.getYaw ( ) , 0.0F );
		
		packet_service.sendSpawnEntityPacket ( player , shape );
		packet_service.sendEntityMetadataPacket ( player , shape );
	}
	
	protected void eject ( ) {
		syncCheck ( );
		this.in_queue = false;
		
		// disposing seat
		if ( seat != null ) {
			seat.remove ( );
			seat = null;
		}
		
		player.setAllowFlight ( player.getGameMode ( ) == GameMode.CREATIVE );
		player.setFlying ( false );
		player.setCanOpenParachute ( true ); // parachute
		
		// showing player for others
		player.getBukkitPlayerOptional ( ).ifPresent (
				player -> arena.getPlayers ( false ).stream ( ).map ( Player :: getBukkitPlayer )
						.filter ( Objects :: nonNull )
						.filter ( other -> !Objects.equals ( player.getUniqueId ( ) , other.getUniqueId ( ) )
								&& !other.canSee ( player ) )
						.forEach ( other -> Player.getPlayer ( other ).showPlayer ( player ) ) );
		
		// firing event
		new PlayerJumpOffBusEvent ( player , this ).call ( );
	}
	
	@Override
	protected void lifeLoop ( ) {
		syncCheck ( );
		
		// displacing
		boolean                  success = true;
		org.bukkit.entity.Player player  = this.player.getBukkitPlayer ( );
		
		if ( player != null && player.isOnline ( ) ) {
			if ( seat != null ) {
				// calculating stuff
				Vector   velocity       = direction.clone ( ).multiply ( spawn.getSpeed ( ) );
				Location shape_location = seat.getLocation ( ).add ( velocity );
				
				//				// floor
				//				Block floor_base = shape_location.getBlock ( ).getRelative ( BlockFace.DOWN );
				//				floor_base.setType ( UniversalMaterial.BARRIER.getMaterial ( ) );
				//				floor_base.getState ( ).update ( true );
				//
				//				for ( BlockFace face : DirectionUtil.FACES_90 ) {
				//					Block floor = floor_base.getRelative ( face );
				//
				//					floor.setType ( UniversalMaterial.BARRIER.getMaterial ( ) );
				//					floor.getState ( ).update ( true );
				//				}
				
				// then displacing
				seat.setVelocity ( velocity );
				
				// updating location
				location.setX ( shape_location.getX ( ) );
				location.setY ( shape_location.getY ( ) );
				location.setZ ( shape_location.getZ ( ) );
				
				// displacing shape
				Entity shape = updateShape ( );
				
				PacketSenderService.getInstance ( ).sendEntityTeleportPacket (
						player , shape.getEntityId ( ) , false ,
						location.getX ( ) ,
						location.getY ( ) + ( seat.getEyeHeight ( ) - EntityReflection.getHeight ( shape ) ) ,
						location.getZ ( ) ,
						spawn.getYaw ( ) , 0.0F );
				PacketSenderService.getInstance ( ).sendEntityMetadataPacket ( player , shape );
			} else {
				// it seems that seat was
				// removed for any reason.
				if ( !in_queue ) {
					this.finish ( );
					success = false;
				}
			}
		} else {
			// player seems to be offline.
			this.finish ( );
			success = false;
		}
		
		// then life loop
		if ( success ) {
			super.lifeLoop ( );
		}
	}
	
	//	@Override
	//	protected void displace ( Vector location ) {
	//		syncCheck ( );
	//		org.bukkit.entity.Player player = this.player.getBukkitPlayer ( );
	//
	//		if ( player != null && player.isOnline ( ) ) {
	//			if ( shape != null ) {
	//				shape.setVelocity ( direction.clone ( ).multiply ( 1.2D ) );
	//
	//				//				// displacing seat
	//				//				EntityReflection.setLocation ( seat , location );
	//				//
	//				//				// displacing shape
	//				//				Entity shape = updateShape ( );
	//				//
	//				//				PacketSenderService.getInstance ( ).sendEntityTeleportPacket (
	//				//						player , shape.getEntityId ( ) , false ,
	//				//						location.getX ( ) ,
	//				//						location.getY ( ) + ( seat.getEyeHeight ( ) - EntityReflection.getHeight ( shape )
	//				) ,
	//				//						location.getZ ( ) ,
	//				//						spawn.getYaw ( ) , 0.0F );
	//				//				PacketSenderService.getInstance ( )
	//				//						.sendEntityMetadataPacket ( player , shape );
	//			} else {
	//				// it seems that seat was
	//				// removed for any reason.
	//				if ( !in_queue ) {
	//					this.finish ( );
	//				}
	//			}
	//		} else {
	//			// player seems to be offline.
	//			this.finish ( );
	//		}
	//	}
	
	protected Set < org.bukkit.entity.Player > getPlayersInArena ( ) {
		return arena.getPlayers ( true ).stream ( ).map ( Player :: getBukkitPlayerOptional )
				.filter ( Optional :: isPresent ).map ( Optional :: get )
				.collect ( Collectors.toSet ( ) );
	}
	
	@Override
	protected void jumpTutorial ( ) {
		// don't need to check if the player
		// is actually in the bus as the bus
		// will be immediately disposed when
		// the player ejects.
		this.player.sendTitle ( EnumLanguage.BUS_JUMP_TITLE.getAsString ( ) ,
								EnumLanguage.BUS_JUMP_SUBTITLE.getAsString ( ) ,
								0 , 10 , 0 );
	}
	
	@Override
	public synchronized void finish ( ) {
		syncCheck ( );
		this.eject ( );
		
		// then finishing
		super.finish ( );
		this.dispose ( );
	}
	
	@Override
	public synchronized void restart ( ) {
		syncCheck ( );
		
		this.eject ( );
		super.restart ( );
		this.dispose ( );
	}
	
	protected Entity updateShape ( ) {
		syncCheck ( );
		
		// spawning shape
		if ( shape == null ) {
			this.shape = PacketSenderService.getInstance ( ).spawnEntity (
					configuration.getShape ( ) ,
					getLocation ( ).getX ( ) ,
					getLocation ( ).getY ( ) ,
					getLocation ( ).getZ ( ) , spawn.getYaw ( ) , 0.0F , entity -> {
						EntityReflection.setInvulnerable ( entity , true );
						EntityReflection.setSilent ( entity , true );
					} );
		}
		
		return shape;
	}
	
	protected void dispose ( ) {
		syncCheck ( );
		HandlerList.unregisterAll ( this );
		
		// de-spawning shape
		if ( shape != null ) {
			player.getBukkitPlayerOptional ( ).ifPresent ( player -> PacketSenderService.getInstance ( )
					.sendDestroyEntityPacket ( player , shape ) );
		}
		
		if ( seat != null ) {
			seat.remove ( );
			seat = null;
		}
	}
}