package es.outlook.adriansrj.battleroyale.parachute;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.bus.BusInstance;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumParachuteConfiguration;
import es.outlook.adriansrj.battleroyale.event.player.PlayerCloseParachuteEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerDeathEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerJumpOffBusEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerOpenParachuteEvent;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.VehicleUtil;
import es.outlook.adriansrj.core.enums.EnumMessageType;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.packet.PacketChannelHandler;
import es.outlook.adriansrj.core.util.packet.PacketEvent;
import es.outlook.adriansrj.core.util.packet.PacketListener;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class responsible for handle parachutes.
 *
 * @author AdrianSR / 10/09/2021 / 10:22 a. m.
 */
public final class ParachuteHandler extends PluginHandler implements PacketListener {
	
	/** minimum height to open a parachute. */
	public static final  double   DEFAULT_MINIMUM_HEIGHT               = 8.0D;
	/** dangerous height, so the parachute should be automatically opened at this point. */
	public static final  double   DANGEROUS_HEIGHT                     =
			DEFAULT_MINIMUM_HEIGHT + ( DEFAULT_MINIMUM_HEIGHT / 2 );
	/** cooldown to open a parahute after jumping off the bus.*/
	private static final Duration OPEN_COOLDOWN_JUMP_OFF_BUS           = Duration.ofMilliseconds ( 800 );
	/** cooldown to open a parahute after closing another. */
	private static final Duration OPEN_COOLDOWN_CLOSING_ANOTHER        = Duration.ofMilliseconds ( 1500 );
	/** last time parachute closes metadata key. */
	private static final String   LAST_TIME_CLOSING_METADATA_KEY       = UUID.randomUUID ( ).toString ( );
	/** last time player received the parachute bar metadata key. */
	private static final String   LAST_TIME_PARACHUTE_BAR_METADATA_KEY = UUID.randomUUID ( ).toString ( );
	
	/** stores the {@link UUID} of the players who jumped off the bus recently. */
	private static final Set < UUID > RECENTLY_DISMOUNTING = Collections.synchronizedSet ( new HashSet <> ( ) );
	
	// stores the millis of the instant a player leaves the bus
	private final Map < Player, Long > bus_off_millis = new ConcurrentHashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public ParachuteHandler ( BattleRoyale plugin ) {
		super ( plugin );
		register ( );
		
		// registering packet listener
		PacketChannelHandler.addPacketListener (
				"PacketPlayInSteerVehicle" , Priority.LOWEST , this );
	}
	
	@Override
	public void onReceiving ( PacketEvent event ) {
		Object                   packet    = event.getPacket ( );
		org.bukkit.entity.Player player    = event.getPlayer ( );
		Player                   br_player = Player.getPlayer ( player );
		ParachuteInstance        parachute = br_player.getParachute ( );
		
		if ( parachute != null && parachute.isOpen ( ) && VehicleUtil.isSneaking ( packet ) ) {
			event.setCancelled ( true );
		}
	}
	
	public void openParachute ( Player player ) {
		ParachuteInstance parachute = player.getParachute ( );
		
		if ( !parachute.isOpen ( ) ) {
			RECENTLY_DISMOUNTING.remove ( player.getUniqueId ( ) );
			
			parachute.open ( );
		}
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onLeave ( PlayerJumpOffBusEvent event ) {
		bus_off_millis.put ( event.getPlayer ( ) , System.currentTimeMillis ( ) );
		RECENTLY_DISMOUNTING.add ( event.getPlayer ( ).getUniqueId ( ) );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onClose ( PlayerCloseParachuteEvent event ) {
		// registering last time the parachute closes
		event.getPlayer ( ).setData ( LAST_TIME_CLOSING_METADATA_KEY , System.currentTimeMillis ( ) );
		
		// disabling flight
		SchedulerUtil.runTask ( ( ) -> event.getPlayer ( ).getBukkitPlayerOptional ( ).ifPresent ( player -> {
			if ( player.getGameMode ( ) != GameMode.CREATIVE ) {
				player.setFlying ( false );
				player.setAllowFlight ( false );
			}
		} ) );
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onOpen ( PlayerToggleFlightEvent event ) {
		org.bukkit.entity.Player player    = event.getPlayer ( );
		Player                   br_player = Player.getPlayer ( event.getPlayer ( ) );
		BattleRoyaleArena        arena     = br_player.getArena ( );
		ParachuteInstance        parachute = br_player.getParachute ( );
		
		if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING
				&& !br_player.isSpectator ( ) && player.getGameMode ( ) != GameMode.CREATIVE
				&& player.getGameMode ( ) != GameMode.SPECTATOR && !parachute.isOpen ( )
				&& ( br_player.isCanOpenParachute ( ) || arena.getMode ( ).isRedeployEnabled ( ) ) ) {
			PlayerOpenParachuteEvent open_event = new PlayerOpenParachuteEvent ( br_player , parachute );
			open_event.call ( );
			
			if ( !open_event.isCancelled ( ) ) {
				event.setCancelled ( true ); // disabling fly
				
				// consuming parachute
				br_player.setCanOpenParachute ( false );
				
				// then opening
				openParachute ( br_player );
			}
		}
	}
	
	@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void parachuteMonitor ( PlayerMoveEvent event ) {
		org.bukkit.entity.Player player    = event.getPlayer ( );
		Player                   br_player = Player.getPlayer ( player );
		BattleRoyaleArena        arena     = br_player.getArena ( );
		Location                 from      = event.getFrom ( );
		Location                 to        = event.getTo ( );
		
		// making sure that the player is actually in free fall
		if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING
				&& !br_player.isSpectator ( ) && player.getGameMode ( ) != GameMode.SPECTATOR
				&& player.getGameMode ( ) != GameMode.CREATIVE ) {
			ParachuteInstance parachute = br_player.getParachute ( );
			
			if ( ( parachute == null || !parachute.isOpen ( ) )
					&& ( br_player.isCanOpenParachute ( ) || arena.getMode ( ).isRedeployEnabled ( ) ) ) {
				player.setFlying ( false ); // must not be flying in no way
				
				// making sure is actually falling
				if ( to != null && to.getY ( ) < from.getY ( ) ) {
					// enabling flight flag, so players will be able to perform a toggle flight
					double height         = calculateHeight ( player );
					double minimum_height = EnumParachuteConfiguration.MINIMUM_HEIGHT.getAsDouble ( );
					
					if ( height >= minimum_height ) {
						// then enabling player to open the parachute
						player.setAllowFlight ( true );
						
						// warning bar
						openWarningBar ( br_player );
					} else {
						// not high enough
						player.setAllowFlight ( false );
					}
					
					// automatically opening parachute
					Long bus_off_millis = this.bus_off_millis.get ( br_player );
					
					if ( EnumParachuteConfiguration.AUTOMATIC.getAsBoolean ( )
							&& ( bus_off_millis == null || bus_off_millis > 0L )
							&& RECENTLY_DISMOUNTING.contains ( player.getUniqueId ( ) ) ) {
						// there is a little cooldown to open a parachute after
						// jumping off of the bus.
						if ( !openCooldownBusCheck ( br_player ) || !openCooldownParachuteCheck ( br_player ) ) {
							return;
						}
						
						// player is dangerously close to the ground, let's save the life
						if ( height <= DANGEROUS_HEIGHT && height >= 1.0D ) {
							// player saved!
							RECENTLY_DISMOUNTING.remove ( player.getUniqueId ( ) );
							
							openParachute ( br_player );
						}
					}
				}
			}
		}
	}
	
	// this event handler will automatically close
	// the parachute of a player who just died.
	@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onDie ( PlayerDeathEvent event ) {
		Player            player    = event.getPlayer ( );
		ParachuteInstance parachute = player.getParachute ( );
		
		if ( parachute.isOpen ( ) ) {
			parachute.close ( );
		}
	}
	
	private double calculateHeight ( org.bukkit.entity.Player player ) {
		Location location = player.getLocation ( );
		Block    block    = location.getBlock ( );
		
		while ( block.isEmpty ( ) && block.getY ( ) > 0 ) {
			block = block.getRelative ( BlockFace.DOWN );
		}
		
		return location.getY ( ) - Math.max ( block.getY ( ) , 0 );
	}
	
	private void openWarningBar ( Player br_player ) {
		// cooldown
		Optional < Object > data = br_player.getData ( LAST_TIME_PARACHUTE_BAR_METADATA_KEY );
		
		if ( data.isPresent ( ) && data.get ( ) instanceof Long ) {
			Long value = ( Long ) data.get ( );
			
			// 1000 millisecond delay
			if ( ( System.currentTimeMillis ( ) - value ) < 1000L ) {
				return;
			}
		}
		
		br_player.setData ( LAST_TIME_PARACHUTE_BAR_METADATA_KEY , System.currentTimeMillis ( ) );
		
		// then sending
		br_player.sendMessage ( EnumMessageType.ACTION_BAR , StringUtil.translateAlternateColorCodes (
				EnumParachuteConfiguration.OPEN_WARNING_BAR.getAsString ( ) ) );
	}
	
	private boolean openCooldownBusCheck ( Player br_player ) {
		BattleRoyaleArena arena = br_player.getArena ( );
		BusInstance < ? > bus   = arena != null ? arena.getBusRegistry ( ).getBus ( br_player ) : null;
		
		if ( bus != null && bus.isDoorOpen ( ) ) {
			Long close_millis = bus_off_millis.get ( br_player );
			
			if ( close_millis != null && close_millis > 0L ) {
				// cooldown is active
				return System.currentTimeMillis ( ) - close_millis >= OPEN_COOLDOWN_JUMP_OFF_BUS.toMillis ( );
			} else {
				// it seems that the player is still on the bus
				return false;
			}
		}
		return true;
	}
	
	private boolean openCooldownParachuteCheck ( Player br_player ) {
		Optional < Object > raw_data = br_player.getData ( LAST_TIME_CLOSING_METADATA_KEY );
		
		if ( raw_data.isPresent ( ) ) {
			Object uncast = raw_data.get ( );
			
			// cooldown is active
			return !( uncast instanceof Long )
					|| ( System.currentTimeMillis ( ) - ( Long ) uncast ) >= OPEN_COOLDOWN_CLOSING_ANOTHER.toMillis ( );
		}
		return true;
	}
	
	@Override
	public void onSending ( PacketEvent event ) {
		// nothing to do here.
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
