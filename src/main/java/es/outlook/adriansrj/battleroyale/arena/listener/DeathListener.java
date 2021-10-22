package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.event.player.PlayerDeathEvent;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.packet.sender.PacketSenderService;
import es.outlook.adriansrj.battleroyale.util.time.TimeUtil;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import es.outlook.adriansrj.core.util.titles.TitlesUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Class that handles the players who die on the battlefield.
 *
 * @author AdrianSR / 18/09/2021 / 06:47 p. m.
 */
public final class DeathListener extends BattleRoyaleArenaListener {
	
	// CASE 1: player dies and there are no living teammates left:
	//         the player will become a spectator
	
	// CASE 2: player dies and there are living teammates left:
	//         the player will become a spectator, but will only be
	//         able to spectate the players on the team from the camera of one of them
	
	// CASE 3: player dies and the respawn is enabled.
	//         the player will join the spectator mode for a couple seconds (configurable from mode configuration),
	//         and after waiting the couple seconds, will be respawned in the air with
	//         the parachute being open automatically
	
	/**
	 * Task that starts a countdown to respawn a player.
	 *
	 * @author AdrianSR / 19/09/2021 / 08:52 a. m.
	 */
	private static class RespawnTask extends BukkitRunnable {
		
		private final Player   br_player;
		private final Duration duration;
		
		// process
		private long timestamp;
		private long last;
		
		public RespawnTask ( Player br_player ) {
			this.br_player = br_player;
			this.duration  = br_player.getArena ( ).getMode ( ).getRespawnTime ( );
		}
		
		@Override
		public void run ( ) {
			org.bukkit.entity.Player player = br_player.getBukkitPlayer ( );
			long                     now    = System.currentTimeMillis ( );
			
			if ( player == null ) {
				cancel ( ); return;
			}
			
			if ( timestamp == 0L ) {
				this.timestamp = now;
			}
			
			if ( now - timestamp < duration.toMillis ( ) ) {
				if ( last == 0L || now - last >= 1000L ) {
					this.last = now;
					
					long left = TimeUnit.MILLISECONDS.toSeconds (
							duration.toMillis ( ) - ( now - timestamp ) );
					long left_pretty = left + 1;
					
					// this could seem redundant, but will prevent
					// a countdown higher than the actual duration
					// the first time this task runs.
					if ( left_pretty <= duration.toSeconds ( ) ) {
						TitlesUtil.send (
								player ,
								// title
								String.format ( EnumLanguage.RESPAWN_COUNTDOWN_TITLE.getAsString ( ) ,
												TimeUtil.formatTime ( Duration.ofSeconds ( left_pretty ) ) ) ,
								// subtitle
								String.format ( EnumLanguage.RESPAWN_COUNTDOWN_SUBTITLE.getAsString ( ) ,
												TimeUtil.formatTime ( Duration.ofSeconds ( left_pretty ) ) ) );
					}
				}
			} else {
				cancel ( );
				
				// clearing title
				TitlesUtil.send ( player , StringUtil.EMPTY , StringUtil.EMPTY );
				
				// setting back to survival mode
				player.setGameMode ( GameMode.SURVIVAL );
				
				// TODO: respawn in the air (as close as possible to the border), and open parachute
			}
		}
	}
	
	private final Set < UUID >            respawn_queue      = new HashSet <> ( );
	private final Map < UUID, Location >  death_location_map = new HashMap <> ( );
	private final Map < Player, Integer > position_map       = new HashMap <> ( );
	
	public DeathListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onDeath ( org.bukkit.event.entity.PlayerDeathEvent event ) {
		org.bukkit.entity.Player player    = event.getEntity ( );
		Player                   br_player = Player.getPlayer ( player );
		BattleRoyaleArena        arena     = br_player.getArena ( );
		
		if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING
				&& br_player.hasTeam ( ) && !br_player.isSpectator ( ) ) {
			int position = arena.getMode ( ).isRespawnEnabled ( )
					? -1 : getPosition ( br_player ); // -1 if respawn is enabled.
			
			// this will respawn the player in spectator mode
			respawn ( br_player );
			
			if ( arena.getMode ( ).isRespawnEnabled ( ) ) {
				event.setKeepInventory ( true );
				event.setKeepLevel ( true );
				
				// TODO: clear item in main hand and don't let change it when waiting to be respawned, so when
				//  respawned, the player will get again the item he had in the hand
				
				// TODO: check arena ends by max kills
			} else {
				// mapping position
				position_map.put ( br_player , position );
				
				// TODO: spawn player stuff chest: if the ground is too close, then spawn the chest normally,
				//  otherwise the chest will be spawned as a falling block, that once landing will actually spawn the
				//  chest with the stuff on the block on which it lands
			}
			
			// mapping death location
			death_location_map.put ( player.getUniqueId ( ) , player.getLocation ( ) );
			
			// firing event
			EntityDamageEvent last_damage = player.getLastDamageCause ( );
			PlayerDeathEvent.Cause cause = last_damage != null
					? PlayerDeathEvent.Cause.of ( last_damage.getCause ( ) ) : null;
			Player br_killer = null;
			
			if ( last_damage instanceof EntityDamageByEntityEvent ) {
				// the player was killed by another player
				Entity uncast_killer = ( ( EntityDamageByEntityEvent ) last_damage ).getDamager ( );
				
				if ( uncast_killer instanceof org.bukkit.entity.Player ) {
					br_killer = Player.getPlayer ( uncast_killer.getUniqueId ( ) );
				} else if ( uncast_killer instanceof Projectile ) {
					Projectile       projectile = ( Projectile ) uncast_killer;
					ProjectileSource shooter    = projectile.getShooter ( );
					
					if ( shooter instanceof org.bukkit.entity.Player ) {
						br_killer = Player.getPlayer ( ( org.bukkit.entity.Player ) shooter );
					}
				}
			}
			
			// battle royale custom causes
			if ( br_killer == null && cause == PlayerDeathEvent.Cause.CUSTOM ) {
				// died bleeding out
				if ( br_player.isKnocked ( ) ) {
					cause     = PlayerDeathEvent.Cause.BLEEDING_OUT;
					br_killer = br_player.getKnocker ( );
				}
				// died out of bounds
				else if ( !arena.getBorder ( ).getCurrentBounds ( ).contains ( player.getLocation ( ) ) ) {
					cause = PlayerDeathEvent.Cause.OUT_OF_BOUNDS;
				}
			}
			
			// finally firing
			PlayerDeathEvent wrapper = new PlayerDeathEvent (
					br_player , br_killer , cause , event.getDeathMessage ( ) , false );
			
			wrapper.setDeathMessage ( event.getDeathMessage ( ) );
			wrapper.setKeepInventory ( event.getKeepInventory ( ) );
			wrapper.setKeepLevel ( event.getKeepLevel ( ) );
			
			wrapper.call ( );
			
			// passing wrapper values
			event.setDeathMessage ( wrapper.getDeathMessage ( ) );
			event.setKeepInventory ( wrapper.isKeepInventory ( ) );
			event.setKeepLevel ( wrapper.isKeepLevel ( ) );
			
			// position.
			if ( position > 0 ) {
				// in case the killer is the last one standing,
				// means that is the end of the arena.
				if ( position == 1 && br_killer != null ) {
					// 0 for the last one standing
					br_killer.getBukkitPlayerOptional ( ).ifPresent (
							killer -> sendPositionTitle ( killer , 0 ) );
					
					// restarting
					restart ( arena );
				}
			} else {
				// dying player is the only one that was alive
				ConsoleUtil.sendPluginMessage (
						ChatColor.RED , "The winner could not be determined " +
								"as the only player who was alive has died" , BattleRoyale.getInstance ( ) );
				
				// restarting
				restart ( arena );
			}
		}
	}
	
	// this event handler is responsible for catching the
	// players that are being respawned just after dying,
	// and introduce them into the spectator mode. in case
	// the respawn is enabled in the current mode, the player
	// will be respawned in the air, otherwise, the position
	// will be displayed to the player in a title.
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onRespawn ( PlayerRespawnEvent event ) {
		org.bukkit.entity.Player player = event.getPlayer ( );
		
		if ( respawn_queue.remove ( player.getUniqueId ( ) ) ) {
			Player            br_player = Player.getPlayer ( player );
			BattleRoyaleArena arena     = br_player.getArena ( );
			
			// death location
			Location death_location = death_location_map.get ( player.getUniqueId ( ) );
			
			if ( death_location != null ) {
				event.setRespawnLocation ( death_location );
			}
			
			// then processing
			if ( arena != null ) {
				if ( arena.getMode ( ).isRespawnEnabled ( ) ) {
					// must be in spectator mode until respawned
					player.setGameMode ( GameMode.SPECTATOR );
					
					// scheduling respawn task
					new RespawnTask ( br_player ).runTaskTimer (
							BattleRoyale.getInstance ( ) , 0L , 10L );
				} else {
					// introducing into spectator mode
					SchedulerUtil.scheduleSyncDelayedTask ( ( ) -> br_player.setSpectator ( true ) );
					
					// displaying position.
					int position = position_map.get ( br_player );
					
					if ( position > 0 ) {
						sendPositionTitle ( player , position );
					}
				}
			}
		}
	}
	
	/**
	 * Calculates the position of the provided {@link Player}.
	 * <br>
	 * <b>This will obviously work only if the respawn is not
	 * enabled, otherwise the result is unknown.</b>
	 *
	 * @param player the player to get.
	 * @return the position of the player.
	 */
	private int getPosition ( Player player ) {
		return player.getArena ( ).getTeamRegistry ( ).getHandle ( ).stream ( )
				.map ( team -> team.getPlayers ( ).stream ( )
						.filter ( alive -> !Objects.equals ( alive , player ) )
						.filter ( Player :: isPlaying )
						.count ( ) )
				.reduce ( 0L , Long :: sum ).intValue ( );
	}
	
	private void sendPositionTitle ( org.bukkit.entity.Player player , int position ) {
		// java's count starts from 0,
		// mortal human's count starts from 1. :)
		final int pretty_position = position + 1;
		
		if ( pretty_position == 1 ) {
			TitlesUtil.send (
					player ,
					EnumLanguage.POSITION_WINNER_TITLE.getAsString ( ) ,
					EnumLanguage.POSITION_WINNER_SUBTITLE.getAsString ( ) );
		} else {
			TitlesUtil.send (
					player ,
					String.format ( EnumLanguage.POSITION_GAME_OVER_TITLE.getAsString ( ) , pretty_position ) ,
					String.format ( EnumLanguage.POSITION_GAME_OVER_SUBTITLE.getAsString ( ) , pretty_position ) );
		}
	}
	
	private void respawn ( Player player ) {
		this.respawn_queue.add ( player.getUniqueId ( ) );
		
		// will send respawn packet in the next tick
		SchedulerUtil.scheduleSyncDelayedTask ( ( ) -> player.getBukkitPlayerOptional ( ).ifPresent (
				PacketSenderService.getInstance ( ) :: sendRespawnPacket ) );
	}
	
	private void restart ( BattleRoyaleArena arena ) {
		// TODO: restart arena
	}
}