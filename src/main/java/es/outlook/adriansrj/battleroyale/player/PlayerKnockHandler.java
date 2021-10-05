package es.outlook.adriansrj.battleroyale.player;

import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.event.player.PlayerKnockedOutEvent;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.actionbar.ActionBarUtil;
import es.outlook.adriansrj.core.util.entity.EntityUtil;
import es.outlook.adriansrj.core.util.entity.UUIDEntity;
import es.outlook.adriansrj.core.util.packet.PacketChannelHandler;
import es.outlook.adriansrj.core.util.packet.PacketEvent;
import es.outlook.adriansrj.core.util.packet.PacketListener;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class responsible for handling knocked players.
 *
 * @author AdrianSR / 15/09/2021 / 09:58 p. m.
 */
public final class PlayerKnockHandler extends PluginHandler implements PacketListener, Runnable {
	
	// TODO: see what happens when the player is knocked and falls into the void
	
	private static final String KNOCK_SEAT_METADATA_KEY = UUID.randomUUID ( ).toString ( );
	
	static PlayerKnockHandler getInstance ( ) {
		return getPluginHandler ( PlayerKnockHandler.class );
	}
	
	private final Map < Player, UUIDEntity < ArmorStand > > knock_seat_map   = new ConcurrentHashMap <> ( );
	private final Map < Player, Long >                      knock_damage_map = new ConcurrentHashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public PlayerKnockHandler ( BattleRoyale plugin ) {
		super ( plugin );
		register ( );
		
		// registering packet listener
		PacketChannelHandler.addPacketListener (
				"PacketPlayInSteerVehicle" , PacketListener.Priority.LOWEST , this );
		
		// scheduling update task
		Bukkit.getScheduler ( ).runTaskTimer ( plugin , this , 17L , 17L );
	}
	
	@Override
	public void onReceiving ( PacketEvent event ) {
		// this will prevent players from leaving the knock seat.
		org.bukkit.entity.Player player = event.getPlayer ( );
		
		if ( player.getVehicle ( ) instanceof ArmorStand
				&& player.getVehicle ( ).hasMetadata ( KNOCK_SEAT_METADATA_KEY ) ) {
			event.setCancelled ( true );
		}
	}
	
	@Override
	public void onSending ( PacketEvent event ) {
		// nothing to do here
	}
	
	@Override
	public void run ( ) {
		// this task is responsible for decreasing the health
		// of the players that are knocked as they are bleeding out
		synchronized ( Player.getPlayers ( ) ) {
			for ( Player br_player : Player.getPlayers ( ) ) {
				if ( !br_player.hasTeam ( ) || !br_player.isKnocked ( )
						|| PlayerReviveHandler.getInstance ( ).reviving ( br_player ) ) {
					continue;
				}
				
				br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
					Long   last     = knock_damage_map.get ( br_player );
					long   now      = System.currentTimeMillis ( );
					double decrease = 0.0D;
					
					if ( br_player.getTeam ( ).getPlayers ( ).stream ( ).anyMatch (
							teammate -> !Objects.equals ( br_player , teammate ) && teammate.isOnline ( )
									&& !teammate.isKnocked ( ) && !teammate.isSpectator ( ) ) ) {
						if ( last == null || ( now - last ) >= 1000L ) {
							final double percent = 10.0D; // TODO: make configurable from mode configuration
							
							if ( percent > 0.0D ) {
								decrease = EntityUtil.getMaxHealth ( player )
										* ( Math.min ( percent , 100.0D ) / 100.0D );
							}
							
							// mapping last time
							knock_damage_map.put ( br_player , now );
						}
					} else {
						// in case there are no alive teammates left, the player
						// will automatically die as there are no players who can revive.
						decrease = player.getHealth ( );
					}
					
					if ( decrease > 0.0D ) {
						final boolean will_die = player.getHealth ( ) - decrease <= 0.0D;
						
						// decreasing health, or killing if health is too low
						player.damage ( decrease );
						
						// will not die, is still bleeding out
						if ( !will_die ) {
							ActionBarUtil.send ( player , EnumLanguage.KNOCKED_BLEEDING_OUT.getAsString ( ) );
						}
					}
				} );
			}
		}
	}
	
	void process ( Player br_player , Player br_knocker , boolean knocked ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			UUIDEntity < ArmorStand > seat = knock_seat_map.get ( br_player );
			
			if ( seat != null ) {
				seat.getOptional ( ).ifPresent ( Entity :: remove );
			}
			
			if ( knocked ) {
				br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
					// increasing health
					player.setHealth ( EntityUtil.getMaxHealth ( player ) );
					
					// putting in knock seat
					Location   location = player.getLocation ( );
					ArmorStand handle   = player.getWorld ( ).spawn ( location , ArmorStand.class );
					
					handle.setVisible ( false );
					handle.setSmall ( true );
					EntityReflection.setSilent ( handle , true );
					handle.setMetadata ( KNOCK_SEAT_METADATA_KEY ,
										 new FixedMetadataValue ( BattleRoyale.getInstance ( ) , true ) );
					
					try {
						handle.addPassenger ( player );
					} catch ( NoSuchMethodError ex ) {
						// legacy versions
						handle.setPassenger ( player );
					}
					
					knock_seat_map.put ( br_player , new UUIDEntity <> ( handle ) );
					
					// firing event
					new PlayerKnockedOutEvent ( br_player , br_knocker ).call ( );
				} );
			} else {
				knock_seat_map.remove ( br_player );
			}
		} else {
			Bukkit.getScheduler ( ).runTask ( plugin , ( ) -> process ( br_player , br_knocker , knocked ) );
		}
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onDamage ( EntityDamageEvent event ) {
		if ( event.getEntity ( ).hasMetadata ( KNOCK_SEAT_METADATA_KEY ) ) {
			event.setCancelled ( true );
		}
	}
	
	// this handler prevents knocked players from damaging
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onDamage ( EntityDamageByEntityEvent event ) {
		if ( event.getDamager ( ) instanceof org.bukkit.entity.Player
				&& Player.getPlayer ( event.getDamager ( ).getUniqueId ( ) ).isKnocked ( ) ) {
			event.setCancelled ( true );
		}
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onManipulate ( PlayerArmorStandManipulateEvent event ) {
		if ( event.getRightClicked ( ).hasMetadata ( KNOCK_SEAT_METADATA_KEY ) ) {
			event.setCancelled ( true );
		}
	}
	
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onDeath ( org.bukkit.event.entity.PlayerDeathEvent event ) {
		// we will un-knock the player in the next tick
		// as we want the event handlers listening the
		// PlayerDeathEvent to be able to use player.isKnocked()
		// without any problems.
		SchedulerUtil.scheduleSyncDelayedTask (
				( ) -> Player.getPlayer ( event.getEntity ( ) ).setKnocked ( false ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
