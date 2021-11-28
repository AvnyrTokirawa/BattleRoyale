package es.outlook.adriansrj.battleroyale.parachute.custom;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumParachuteConfiguration;
import es.outlook.adriansrj.battleroyale.event.player.PlayerCloseParachuteEvent;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.packet.reader.PacketReaderService;
import es.outlook.adriansrj.battleroyale.packet.sender.PacketSenderService;
import es.outlook.adriansrj.battleroyale.packet.wrapper.out.PacketOutEntityTeleport;
import es.outlook.adriansrj.battleroyale.schedule.ScheduledExecutorPool;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.packet.interceptor.PacketInterceptorAcceptor;
import es.outlook.adriansrj.battleroyale.util.packet.interceptor.PacketInterceptorInjector;
import es.outlook.adriansrj.battleroyale.util.packet.interceptor.entity.PacketEntityRelativeMoveInterceptorSimple;
import es.outlook.adriansrj.battleroyale.util.packet.interceptor.entity.PacketEntityTeleportInterceptorSimple;
import es.outlook.adriansrj.battleroyale.util.packet.reader.PacketReader;
import es.outlook.adriansrj.battleroyale.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author AdrianSR / 09/09/2021 / 08:32 p. m.
 */
class ParachuteCustomInstanceHandle {
	
	private static final Set < ParachuteCustomInstanceHandle > PARACHUTE_SET;
	private static final ScheduledExecutorService              EXECUTOR_SERVICE;
	
	static {
		EXECUTOR_SERVICE = ScheduledExecutorPool.getInstance ( ).getSingleThreadScheduledExecutor ( );
		PARACHUTE_SET    = Collections.synchronizedSet ( new HashSet <> ( ) );
		
		// parachutes life loop
		Runnable life_loop = ( ) -> {
			synchronized ( PARACHUTE_SET ) {
				PARACHUTE_SET.stream ( )
						.filter ( handle -> handle.started && !handle.destroyed && handle.seat != null )
						.forEach ( ParachuteCustomInstanceHandle :: lifeLoop );
			}
			
			// unregistering destroyed parachutes
			PARACHUTE_SET.removeIf ( handle -> handle.destroyed
					|| ( handle.started && handle.seat == null ) );
		};
		
		// scheduling life loop
		EXECUTOR_SERVICE.scheduleAtFixedRate (
				life_loop ,
				Constants.PARACHUTE_LIFE_LOOP_EXECUTOR_PERIOD ,
				Constants.PARACHUTE_LIFE_LOOP_EXECUTOR_PERIOD ,
				TimeUnit.MILLISECONDS );
		
		// packet interceptors
		// order is important
		PacketEntityTeleportInterceptorSimple     tp_interceptor = new PacketEntityTeleportInterceptorSimple ( );
		PacketEntityRelativeMoveInterceptorSimple mv_interceptor = new PacketEntityRelativeMoveInterceptorSimple ( );
		
		tp_interceptor.register ( );
		mv_interceptor.register ( );
		
		// this acceptor will cancel the packets coming from the server.
		PacketInterceptorAcceptor canceller = packet -> {
			PacketReader reader = new PacketReader ( packet );
			// first var int is the entity id
			int entity_id = reader.readVarInt ( );
			
			synchronized ( PARACHUTE_SET ) {
				if ( PARACHUTE_SET.stream ( ).anyMatch ( parachute -> parachute.seat != null
						&& entity_id == parachute.seat.getEntityId ( ) ) ) {
					// coming from the server, we must cancel it; otherwise
					// it would cause client-side flickering.
					return true;
				}
			}
			return false;
		};
		
		tp_interceptor.registerAcceptor ( canceller );
		mv_interceptor.registerAcceptor ( canceller );
		
		// this acceptor will inject the outgoing teleport packet (coming from this class)
		tp_interceptor.registerAcceptor ( ( PacketInterceptorInjector ) packet -> {
			// this class will send teleport packets to teleport
			// the seat; the thing is that the id of the seat is masked,
			// so we can identify if the packet is coming from the server or from this class.
			PacketOutEntityTeleport wrapper = PacketReaderService.getInstance ( )
					.readEntityTeleportPacket ( packet );
			
			synchronized ( PARACHUTE_SET ) {
				ParachuteCustomInstanceHandle handle = PARACHUTE_SET.stream ( )
						.filter ( parachute -> parachute.seat != null
								&& wrapper.getEntityId ( ) == parachute.seat.getEntityId ( ) << 4 )
						.findAny ( ).orElse ( null );
				
				if ( handle != null ) {
					// then we can pass the actual entity id
					wrapper.setEntityId ( handle.seat.getEntityId ( ) );
					
					return wrapper.createInstance ( );
				}
			}
			
			return packet;
		} );
	}
	
	/**
	 * @author AdrianSR / 09/09/2021 / 09:42 p. m.
	 */
	protected static class Part {
		
		/**
		 * The height of the ArmorStand instances. It means that this constant has the height of a small ArmorStand
		 * entity
		 * as value.
		 */
		protected static final float HANDLES_HEIGHT = 1.032F;
		
		protected final ArmorStand                    handle;
		protected final int                           id;
		protected final ParachuteCustomInstanceHandle parachute_handle;
		protected final ParachuteCustomPartPosition   position;
		protected final PositionData                  position_data;
		
		public Part ( org.bukkit.entity.Player player , ParachuteCustomInstanceHandle parachute_handle ,
				ParachuteCustomPartPosition position , ItemStack shape ) {
			Validate.isTrue ( Bukkit.isPrimaryThread ( ) , "must run on server thread" );
			
			this.parachute_handle = parachute_handle;
			this.position         = position;
			this.position_data    = new PositionData ( position );
			
			// spawning
			Location initial_location = player.getLocation ( );
			
			this.handle = ( ArmorStand ) PacketSenderService.getInstance ( ).sendSpawnEntityPacket (
					player , EntityType.ARMOR_STAND , initial_location.getX ( ) , initial_location.getY ( ) ,
					initial_location.getZ ( ) , initial_location.getYaw ( ) , initial_location.getPitch ( ) ,
					entity -> {
						ArmorStand stand = ( ( ArmorStand ) entity );
						
						stand.setGravity ( false );
						stand.setVisible ( false );
						stand.setSmall ( true );
						stand.getEquipment ( ).setHelmet ( shape );
						EntityReflection.setSilent ( stand , true );
					} );
			this.id     = handle.getEntityId ( );
			
			// spawning for players in arena
			parachute_handle.getPlayersInArena ( ).forEach ( bukkit -> {
				// spawn packet
				if ( !Objects.equals ( bukkit.getUniqueId ( ) , player.getUniqueId ( ) ) ) {
					PacketSenderService.getInstance ( ).sendSpawnEntityPacket ( bukkit , handle );
				}
				
				// equipment packet
				PacketSenderService.getInstance ( ).sendEntityEquipmentPacket ( bukkit , handle );
			} );
		}
		
		public void destroy ( ) {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> {
				parachute_handle.getPlayersInArena ( ).forEach (
						bukkit -> PacketSenderService.getInstance ( ).sendDestroyEntityPacket ( bukkit , handle ) );
			} );
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
	
	protected final ParachuteCustomInstance parachute;
	protected final Player                  player;
	protected final BattleRoyaleArena       arena;
	protected final ParachuteCustomModel    model;
	protected final UUID                    uuid;
	protected       double                  fall_speed;
	
	// parts
	protected volatile boolean      started;
	protected volatile boolean      destroyed;
	protected          ArmorStand   seat;
	protected final    Set < Part > parts = new HashSet <> ( );
	
	// location
	protected World world;
	protected float x;
	protected float y;
	protected float z;
	protected float rotation;
	
	public ParachuteCustomInstanceHandle ( ParachuteCustomInstance parachute ) {
		this.parachute = parachute;
		this.player    = parachute.getPlayer ( );
		this.arena     = Objects.requireNonNull ( player.getArena ( ) , "player must be in an arena" );
		this.model     = parachute.getConfiguration ( ).getModel ( );
		this.uuid      = UUID.randomUUID ( );
		
		// falling speed
		this.fall_speed = EnumParachuteConfiguration.FALLING_SPEED.getAsDouble ( );
		
		// registering
		PARACHUTE_SET.add ( this );
	}
	
	@SuppressWarnings ( "deprecation" )
	public synchronized boolean start ( ) {
		if ( seat != null ) {
			throw new IllegalStateException ( "parachute already started" );
		}
		
		if ( !Bukkit.isPrimaryThread ( ) ) {
			throw new IllegalStateException ( "must run on server thread" );
		}
		
		org.bukkit.entity.Player player = this.player.getBukkitPlayer ( );
		
		if ( player != null && player.isOnline ( ) ) {
			Location location = player.getLocation ( );
			
			// initial location
			this.x = ( float ) location.getX ( );
			this.y = ( float ) location.getY ( );
			this.z = ( float ) location.getZ ( );
			
			// spawning seat
			this.seat = player.getWorld ( ).spawn ( location , ArmorStand.class );
			this.seat.setVisible ( false );
			this.seat.setGravity ( false );
			this.seat.setSmall ( true );
			this.seat.setRemoveWhenFarAway ( false );
			
			try {
				seat.addPassenger ( player );
			} catch ( NoSuchMethodError ex ) {
				// legacy versions
				seat.setPassenger ( player );
			}
			
			// parts
			for ( ParachuteCustomModelPart part : model.getParts ( ).values ( ) ) {
				if ( part != null && part.isValid ( ) ) {
					parts.add ( new Part ( player , this , part.getPosition ( ) ,
										   part.getShape ( ).toItemStack ( this.player ) ) );
				}
			}
			
			// marking as started
			this.started = true;
		} else {
			return false;
		}
		
		return true;
	}
	
	protected synchronized boolean isOnGround ( ) {
		if ( seat != null ) {
			int block_x = NumberConversions.floor ( x );
			int block_y = NumberConversions.floor ( y );
			int block_z = NumberConversions.floor ( z );
			
			for ( int y = block_y ; y >= 0 ; y-- ) {
				Block block = seat.getWorld ( ).getBlockAt ( block_x , y , block_z );
				
				if ( !block.isEmpty ( ) && Math.abs ( block_y - y ) < 2 ) {
					return true;
				}
			}
			return seat.isOnGround ( );
		} else {
			// not started
			return false;
		}
	}
	
	protected synchronized void lifeLoop ( ) {
		BattleRoyaleArena        arena  = this.player.getArena ( );
		org.bukkit.entity.Player player = this.player.getBukkitPlayer ( );
		
		if ( player == null || arena == null ) {
			destroy ( ); return;
		}
		
		float player_rotation = player.getLocation ( ).getYaw ( );
		// 30 degrees down for fall
		Vector direction = DirectionUtil.getDirection (
				player_rotation , 30.0F ).multiply ( fall_speed );
		
		this.x += direction.getX ( );
		this.y += direction.getY ( );
		this.z += direction.getZ ( );
		this.rotation = player_rotation;
		
		// making sure has not landed
		if ( isOnGround ( ) ) {
			// updating last location (from server thread).
			final ArmorStand final_seat = seat; // last reference
			
			SchedulerUtil.runTask ( ( ) -> {
				EntityReflection.setLocation ( final_seat , x , y , z , rotation , 0.0F );
				EntityReflection.setLocation ( player , x , y , z , rotation , 0.0F );
			} );
			
			// then destroying
			destroy ( ); return;
		}
		
		Set < org.bukkit.entity.Player > players = getPlayersInArena ( );
		
		// moving seat
		players.forEach ( bukkit -> PacketSenderService.getInstance ( ).sendEntityTeleportPacket (
				bukkit ,
				// masking id
				seat.getEntityId ( ) << 4 ,
				false ,
				x , y , z ,
				rotation , 0.0F ) );
		
		EntityReflection.setPositionDirty ( seat , new Vector ( x , y , z ) );
		
		// moving parts
		for ( Part part : parts ) {
			float x        = this.x;
			float y        = this.y;
			float z        = this.z;
			float rotation = this.rotation;
			
			float part_rotation = DirectionUtil.normalize ( rotation );
			float angle         = DirectionUtil.normalize ( ( part_rotation - part.position_data.angle ) + 90F );
			
			x = x + ( ( float ) Math.cos ( Math.toRadians ( angle ) ) * part.position_data.xz_distance );
			y = ( ( y + part.position_data.y_distance ) - Part.HANDLES_HEIGHT ) + 0.35F;
			z = z + ( ( float ) Math.sin ( Math.toRadians ( angle ) ) * part.position_data.xz_distance );
			
			// head pose
			part.handle.setHeadPose ( new EulerAngle (
					Math.toRadians ( part.position.getPitch ( ) ) ,
					Math.toRadians ( part.position.getYaw ( ) ) ,
					Math.toRadians ( part.position.getRoll ( ) ) ) );
			part.handle.setGravity ( false );
			part.handle.setSmall ( true );
			part.handle.setVisible ( false );
			
			// location/metadata packets
			float final_x = x;
			float final_y = y;
			float final_z = z;
			
			players.forEach ( bukkit -> {
				// part location packet
				PacketSenderService.getInstance ( ).sendEntityTeleportPacket (
						bukkit , part.id , false , final_x , final_y , final_z ,
						rotation , 0.0F );
				// part metadata packet
				PacketSenderService.getInstance ( )
						.sendEntityMetadataPacket ( bukkit , part.handle );
			} );
		}
		
		// updating player location for another players
		arena.getPlayers ( ).stream ( ).filter (
				other -> !Objects.equals ( this.player , other ) ).map (
				Player :: getBukkitPlayer ).filter (
				Objects :: nonNull ).forEach ( other -> {
			PacketSenderService.getInstance ( ).sendEntityTeleportPacket (
					other , EntityReflection.getEntityID ( player ) ,
					false , x , y , z , player_rotation , 0.0F );
		} );
	}
	
	protected Set < org.bukkit.entity.Player > getPlayersInArena ( ) {
		return arena.getPlayers ( true ).stream ( ).map ( Player :: getBukkitPlayerOptional )
				.filter ( Optional :: isPresent ).map ( Optional :: get )
				.collect ( Collectors.toSet ( ) );
	}
	
	public synchronized void destroy ( ) {
		if ( destroyed ) {
			throw new IllegalStateException ( "parachute already destroy" );
		}
		
		this.destroyed = true;
		
		// sending player to last location
		org.bukkit.entity.Player player = this.player.getBukkitPlayer ( );
		
		if ( player != null ) {
			// must be teleported from server thread
			org.bukkit.entity.Player finalPlayer = player;
			
			SchedulerUtil.runTask ( ( ) -> finalPlayer.teleport (
					new Location ( finalPlayer.getWorld ( ) , x , y , z , rotation , 0.0F ) ) );
		} else {
			if ( ( player = this.player.getLastHandle ( ) ) != null ) {
				EntityReflection.setPositionDirty ( player , new Vector ( x , y , z ) );
			}
		}
		
		// removing parts
		if ( seat != null ) {
			// must eject/remove from server thread.
			// we will need to create a final reference
			// as we will later set seat to null, resulting
			// in NullPointerException
			final ArmorStand final_ref = seat;
			
			SchedulerUtil.runTask ( ( ) -> {
				final_ref.eject ( );
				final_ref.remove ( );
			} );
			
			seat = null;
		}
		
		parts.forEach ( Part :: destroy );
		parts.clear ( );
		
		// firing event from server thread.
		new PlayerCloseParachuteEvent ( this.player , parachute ).callSafe ( );
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		ParachuteCustomInstanceHandle that = ( ParachuteCustomInstanceHandle ) o;
		return uuid.equals ( that.uuid );
	}
	
	@Override
	public int hashCode ( ) {
		return Objects.hash ( uuid );
	}
}