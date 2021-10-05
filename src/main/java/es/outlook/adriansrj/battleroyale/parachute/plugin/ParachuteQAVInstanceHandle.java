package es.outlook.adriansrj.battleroyale.parachute.plugin;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumParachuteConfiguration;
import es.outlook.adriansrj.battleroyale.event.player.PlayerCloseParachuteEvent;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import es.outlook.adriansrj.core.util.reflection.bukkit.EntityReflection;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.api.events.VehicleChangeSpeedEvent;
import me.zombie_striker.qav.api.events.VehicleDamageEvent;
import me.zombie_striker.qav.api.events.VehicleDestroyEvent;
import me.zombie_striker.qav.api.events.VehicleTurnEvent;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * @author AdrianSR / 12/09/2021 / 10:14 a. m.
 */
class ParachuteQAVInstanceHandle {
	
	protected static final Thread                             PARACHUTE_LIFE_THREAD;
	protected static final Listener                           PARACHUTE_LISTENER;
	protected static final Set < ParachuteQAVInstanceHandle > PARACHUTE_SET;
	
	static {
		PARACHUTE_SET         = Collections.synchronizedSet ( new HashSet <> ( ) );
		PARACHUTE_LIFE_THREAD = new Thread ( new Runnable ( ) {
			
			@Override
			public void run ( ) {
				while ( true ) {
					PARACHUTE_SET.stream ( ).filter ( handle -> handle.started )
							.forEach ( ParachuteQAVInstanceHandle :: lifeLoop );
					// unregistering destroyed parachutes
					PARACHUTE_SET.removeIf ( handle -> handle.destroyed || ( handle.started
							&& ( handle.handle == null || handle.handle.getDriverSeat ( ) == null ) ) );
					
					// sleeping each 33 milliseconds asynchronously.
					// equivalent of the server ticking system.
					try {
						Thread.sleep ( 33 );
					} catch ( InterruptedException ex ) {
						ex.printStackTrace ( );
					}
				}
			}
		} , "parachute-qav-lifeloop" );
		PARACHUTE_LIFE_THREAD.start ( );
		
		PARACHUTE_LISTENER = new Listener ( ) {
			@EventHandler ( priority = EventPriority.LOWEST, ignoreCancelled = true )
			public void onSpeed ( VehicleChangeSpeedEvent event ) {
				if ( anyMatch ( event.getVehicle ( ) ) ) {
					event.setNewSpeed ( 0.0D );
					event.setCanceled ( true );
				}
			}
			
			@EventHandler ( priority = EventPriority.LOWEST, ignoreCancelled = true )
			public void onTurn ( VehicleTurnEvent event ) {
				if ( anyMatch ( event.getVehicle ( ) ) ) { event.setCanceled ( true ); }
			}
			
			@EventHandler ( priority = EventPriority.LOWEST, ignoreCancelled = true )
			public void onDamage ( VehicleDamageEvent event ) {
				if ( anyMatch ( event.getVehicle ( ) ) ) { event.setCanceled ( true ); }
			}
			
			@EventHandler ( priority = EventPriority.LOWEST, ignoreCancelled = true )
			public void onDamage ( VehicleDestroyEvent event ) {
				if ( anyMatch ( event.getVehicle ( ) ) ) { event.setCanceled ( true ); }
			}
			
			boolean anyMatch ( VehicleEntity vehicle ) {
				return PARACHUTE_SET.stream ( ).anyMatch (
						parachute -> Objects.equals ( vehicle , parachute.handle ) );
			}
		};
		Bukkit.getPluginManager ( ).registerEvents ( PARACHUTE_LISTENER , BattleRoyale.getInstance ( ) );
	}
	
	protected final ParachuteQAVInstance parachute;
	protected final Player               player;
	protected final UUID                 uuid;
	protected       double               fall_speed;
	
	protected AbstractVehicle handle_configuration;
	protected VehicleEntity   handle;
	protected boolean         started;
	protected boolean         destroyed;
	
	protected World world;
	protected float x;
	protected float y;
	protected float z;
	protected float rotation;
	
	public ParachuteQAVInstanceHandle ( ParachuteQAVInstance parachute ) {
		this.parachute = parachute;
		this.player    = parachute.getPlayer ( );
		this.uuid      = UUID.randomUUID ( );
		
		// falling speed
		this.fall_speed = EnumParachuteConfiguration.FALLING_SPEED.getAsDouble ( );
		
		// registering
		synchronized ( PARACHUTE_SET ) {
			PARACHUTE_SET.add ( this );
		}
	}
	
	public boolean start ( ) {
		if ( handle != null ) {
			throw new IllegalStateException ( "parachute already started" );
		} else if ( !Bukkit.isPrimaryThread ( ) ) {
			throw new IllegalStateException ( "must run on server thread" );
		}
		
		org.bukkit.entity.Player player = this.player.getBukkitPlayer ( );
		
		if ( player != null && player.isOnline ( ) ) {
			Location location = player.getLocation ( );
			
			// initial location
			this.world    = location.getWorld ( );
			this.x        = ( float ) location.getX ( );
			this.y        = ( float ) location.getY ( );
			this.z        = ( float ) location.getZ ( );
			this.rotation = location.getYaw ( );
			
			// spawning
			this.handle = new VehicleEntity ( handle_configuration = QualityArmoryVehicles.getVehicle (
					parachute.getConfiguration ( ).getModelName ( ) ) , location , player.getUniqueId ( ) );
			this.handle.spawn ( );
			// we're adding fuel as we don't want QAV
			// to send messages if the player tries
			// to drive the vehicle.
			this.handle.setFuel ( Integer.MAX_VALUE );
			
			try {
				this.handle.getModelEntities ( ).forEach ( part -> part.setGravity ( false ) );
			} catch ( NoSuchMethodError ex ) {
				// legacy version
			}
			
			// climbing
			QualityArmoryVehicles.addPlayerToCar ( handle , player , true );
			
			// marking as started
			this.started = true;
			return true;
		} else {
			return false;
		}
	}
	
	protected synchronized boolean isOnGround ( ) {
		return handle != null && handle.getDriverSeat ( ) != null && handle.isOnGround ( );
	}
	
	protected void lifeLoop ( ) {
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
			destroy ( ); return;
		}
		
		// updating handle
		EntityReflection.setLocation ( handle.getDriverSeat ( ) , x , y , z , rotation , 0.0F );
	}
	
	protected void destroy ( ) {
		if ( destroyed ) {
			throw new IllegalStateException ( "parachute already destroy" );
		}
		
		this.destroyed = true;
		org.bukkit.entity.Player player = this.player.getBukkitPlayer ( );
		
		if ( player == null ) {
			player = this.player.getLastHandle ( );
		}
		
		this.handle.deconstruct ( player , "parachute destroyed" );
		
		// firing event
		new PlayerCloseParachuteEvent ( this.player , parachute ).call ( );
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		ParachuteQAVInstanceHandle that = ( ParachuteQAVInstanceHandle ) o;
		return uuid.equals ( that.uuid );
	}
	
	@Override
	public int hashCode ( ) {
		return Objects.hash ( uuid );
	}
}