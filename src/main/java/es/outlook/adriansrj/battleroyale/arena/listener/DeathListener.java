package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaStat;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.enums.EnumStat;
import es.outlook.adriansrj.battleroyale.event.player.PlayerDeathEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerStatSetEvent;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.Team;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.packet.sender.PacketSenderService;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteInstance;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.battleroyale.util.mode.BattleRoyaleModeUtil;
import es.outlook.adriansrj.battleroyale.util.time.TimeUtil;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import es.outlook.adriansrj.core.util.math.Vector2D;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
		private long      timestamp;
		private long      last;
		private ItemStack item_in_hand;
		
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
				
				// backing up item in hand
				this.item_in_hand = br_player.getItemInHand ( );
				
				br_player.setItemInHand ( null );
				player.updateInventory ( );
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
						br_player.sendTitle (
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
				
				// resetting title
				br_player.sendTitle ( StringUtil.EMPTY , StringUtil.EMPTY );
				
				// restoring item in hand
				if ( item_in_hand != null ) {
					br_player.setItemInHand ( item_in_hand );
					player.updateInventory ( );
				}
				
				// parachute should be closed at this
				// point as it should be closed when
				// the player dies; we make sure is closed though
				ParachuteInstance parachute = br_player.getParachute ( );
				
				if ( parachute.isOpen ( ) ) {
					parachute.close ( );
				}
				
				// respawning in the air
				player.teleport ( calculateRespawnLocation ( player.getLocation ( ) ) );
				
				// must change game mode from a delayed task,
				// otherwise weird things could happen.
				Bukkit.getScheduler ( ).scheduleSyncDelayedTask ( BattleRoyale.getInstance ( ) , ( ) -> {
					// setting back to survival mode
					player.setGameMode ( GameMode.SURVIVAL );
					
					// it is safer to open the parachute in the
					// next tick, once the player is teleported.
					Bukkit.getScheduler ( ).scheduleSyncDelayedTask (
							BattleRoyale.getInstance ( ) , parachute :: open );
				} , 5L );
			}
		}
		
		private Location calculateRespawnLocation ( Location player_location ) {
			World      world          = br_player.getArena ( ).getWorld ( );
			ZoneBounds current_bounds = br_player.getArena ( ).getCurrentBounds ( );
			Location2I center         = current_bounds.getCenter ( );
			Vector[]   corners        = current_bounds.toBoundingBox ( ).getCorners ( );
			Vector     respawn_vector = corners[ 0 ];
			
			// finding out closets corner
			double closest_distance = Double.MAX_VALUE;
			
			for ( Vector corner : corners ) {
				double distance = new Vector2D ( corner.getX ( ) , corner.getZ ( ) )
						.distance ( new Vector2D ( player_location.getX ( ) , player_location.getZ ( ) ) );
				
				if ( distance < closest_distance ) {
					closest_distance = distance;
					respawn_vector   = corner;
				}
			}
			
			// direction towards bounds center
			float[] look = DirectionUtil.lookAt (
					player_location.toVector ( ) , new Vector ( center.getX ( ) , 0 , center.getZ ( ) ) );
			
			Location result  = respawn_vector.toLocation ( world );
			Block    highest = world.getHighestBlockAt ( result );
			
			// height. the very top must be a bit
			// below the maximum height, otherwise
			// weird things with the player and the
			// parachute could happen.
			int very_top = world.getMaxHeight ( ) - 5;
			
			if ( highest.getY ( ) < 0 || highest.isEmpty ( ) ) {
				result.setY ( very_top );
			} else {
				result.setY ( Math.min ( highest.getY ( ) + 50.0D , very_top ) );
			}
			
			// facing direction
			result.setYaw ( look[ 0 ] );
			
			return result;
		}
	}
	
	private final Set < UUID >                         respawn_queue      = new HashSet <> ( );
	private final Map < UUID, Location >               death_location_map = new HashMap <> ( );
	private final Map < UUID, RespawnTask >            task_map           = new HashMap <> ( );
	private final Map < Player, Integer >              rank_map           = new HashMap <> ( );
	// map responsible for storing the time the
	// stat of a player is set.
	private final Map < UUID, Map < EnumStat, Long > > stat_set_map       = new HashMap <> ( );
	
	public DeathListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	// event handler responsible for mapping the time the
	// stat of a player is set.
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onStat ( PlayerStatSetEvent event ) {
		Player            player = event.getPlayer ( );
		BattleRoyaleArena arena  = player != null ? player.getArena ( ) : null;
		
		if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING ) {
			stat_set_map.computeIfAbsent ( player.getUniqueId ( ) , k -> new EnumMap <> ( EnumStat.class ) )
					.put ( event.getStatType ( ) , System.currentTimeMillis ( ) );
		}
	}
	
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onDeath ( org.bukkit.event.entity.PlayerDeathEvent event ) {
		org.bukkit.entity.Player player    = event.getEntity ( );
		Player                   br_player = Player.getPlayer ( player );
		BattleRoyaleArena        arena     = br_player.getArena ( );
		boolean                  restart   = false;
		
		if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING
				&& br_player.hasTeam ( ) && !br_player.isSpectator ( ) ) {
			BattleRoyaleMode mode = arena.getMode ( );
			int              rank = mode.isRespawnEnabled ( ) ? -1 : getRank ( br_player ); // -1 if respawn is enabled.
			
			// this will respawn the player in spectator mode
			respawn ( br_player );
			
			if ( arena.getMode ( ).isRespawnEnabled ( ) ) {
				event.setKeepInventory ( true );
				event.setKeepLevel ( true );
			} else {
				// mapping rank
				rank_map.put ( br_player , rank );
				
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
			
			// rank.
			if ( rank > 0 ) {
				// in case the killer is the last one standing,
				// means that is the end of the arena.
				if ( rank == 1 && br_killer != null ) {
					// mark for restarting
					restart = true;
					
					// 0 for the last one standing
					sendRankTitle ( br_killer , 0 );
					
					// killer won!
					win ( br_killer );
				}
			} else {
				// mark for restarting
				restart = true;
				
				// dying player is the only one that was alive
				ConsoleUtil.sendPluginMessage (
						ChatColor.RED , "The winner could not be determined " +
								"as the only player who was alive has died" , BattleRoyale.getInstance ( ) );
			}
			
			// determined by kills
			if ( BattleRoyaleModeUtil.isDeterminedByKills ( mode ) ) {
				int kill_limit = mode.getMaxKills ( );
				
				if ( arena.getStats ( ).get ( EnumArenaStat.KILLS ) >= kill_limit ) {
					restart = true;
					
					// finding out winner
					Player winner = arena.getPlayers ( false ).stream ( )
							.filter ( Player :: isPlaying ).min ( statComparator ( EnumStat.KILLS ) )
							.orElse ( null );
					
					if ( winner != null ) {
						win ( winner );
						
						// rank title for the winner (first)
						sendRankTitle ( winner , 0 );
						
						// rank for the rest of the players
						List < Player > rest = arena.getPlayers ( false ).stream ( )
								.filter ( Player :: isPlaying ).filter ( other -> !Objects.equals ( other , winner ) )
								.sorted ( statComparator ( EnumStat.KILLS ) ).collect ( Collectors.toList ( ) );
						
						for ( int i = 0 ; i < rest.size ( ) ; i++ ) {
							sendRankTitle ( rest.get ( i ) , i + 1 );
						}
					} else {
						ConsoleUtil.sendPluginMessage (
								ChatColor.RED , "The winner could not be determined" ,
								BattleRoyale.getInstance ( ) );
					}
				}
			}
			
			// restarting
			if ( restart && !arena.getMode ( ).isRespawnEnabled ( ) ) {
				arena.restart ( false );
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
					RespawnTask task = new RespawnTask ( br_player );
					task.runTaskTimer (
							BattleRoyale.getInstance ( ) , 0L , 10L );
					
					task_map.put ( player.getUniqueId ( ) , task );
				} else {
					// introducing into spectator mode
					SchedulerUtil.scheduleSyncDelayedTask ( ( ) -> br_player.setSpectator ( true ) );
					
					// displaying position.
					Integer position = rank_map.get ( br_player );
					
					if ( position != null && position > 0 ) {
						sendRankTitle ( br_player , position );
					}
				}
			}
		}
	}
	
	// this event handler is responsible for stop
	// players from held the current item in hand,
	// as we will clear the slot it is in, so the
	// player cannot use that item until is respawned.
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onHeld ( PlayerItemHeldEvent event ) {
		RespawnTask task = task_map.get ( event.getPlayer ( ).getUniqueId ( ) );
		
		if ( task != null && !task.isCancelled ( ) ) {
			event.setCancelled ( true );
		}
	}
	
	/**
	 * Sets the provided player as the winner
	 * of the arena.
	 *
	 * @param player the player who wins.
	 */
	private void win ( Player player ) {
		player.getDataStorage ( ).incrementStat ( EnumStat.WINS , 1 , true );
		player.getDataStorage ( ).incrementTempStat ( EnumStat.WINS , 1 );
	}
	
	/**
	 * Returns a {@link Comparator} which returns the player with
	 * the highest value; or in case both have the same value, returns
	 * the player who reached that value first.
	 *
	 * @param stat_type the type of stat to compare.
	 * @return player with the highest value, or the first player who reached
	 * the value.
	 */
	private Comparator < Player > statComparator ( EnumStat stat_type ) {
		return ( a , b ) -> {
			int a_value = a.getDataStorage ( ).getTempStat ( stat_type );
			int b_value = b.getDataStorage ( ).getTempStat ( stat_type );
			
			if ( a_value > b_value ) {
				return -1;
			} else if ( b_value > a_value ) {
				return 1;
			} else {
				// same value; the player to be returned
				// will be the first to reach this value.
				Map < EnumStat, Long > a_map = stat_set_map.get ( a.getUniqueId ( ) );
				Map < EnumStat, Long > b_map = stat_set_map.get ( b.getUniqueId ( ) );
				
				if ( a_map != null && b_map != null ) {
					Long a_timestamp = a_map.get ( stat_type );
					Long b_timestamp = b_map.get ( stat_type );
					
					if ( a_timestamp != null && b_timestamp != null ) {
						if ( a_timestamp < b_timestamp ) {
							return -1;
						} else if ( b_timestamp < a_timestamp ) {
							return 1;
						}
					}
				} else if ( a_map != null ) {
					return -1;
				} else if ( b_map != null ) {
					return 1;
				}
			}
			
			return 0;
		};
	}
	
	/**
	 * Calculates the rank of the provided {@link Player}.
	 * <br>
	 * <b>This will obviously work only if the respawn is not
	 * enabled, otherwise the result is unknown.</b>
	 *
	 * @param player the player to get.
	 * @return the rank of the player.
	 */
	private int getRank ( Player player ) {
		return player.getArena ( ).getTeamRegistry ( ).getHandle ( ).stream ( )
				.map ( team -> team.getPlayers ( ).stream ( )
						.filter ( alive -> !Objects.equals ( alive , player ) )
						.filter ( Player :: isPlaying )
						.count ( ) )
				.reduce ( 0L , Long :: sum ).intValue ( );
	}
	
	private int getRank ( Team team ) {
		return ( int ) team.getArena ( ).getTeamRegistry ( ).getHandle ( ).stream ( )
				.filter ( other -> !Objects.equals ( team , other ) )
				.filter ( other -> !other.isEmpty ( ) && other.getPlayers ( ).stream ( ).anyMatch ( Player :: isPlaying ) )
				.count ( );
	}
	
	private void sendRankTitle ( Player player , int rank ) {
		// java's count starts from 0,
		// mortal human's count starts from 1. :)
		final int pretty = rank + 1;
		
		if ( pretty == 1 ) {
			player.sendTitle ( EnumLanguage.RANK_WINNER_TITLE.getAsString ( ) ,
							   EnumLanguage.RANK_WINNER_SUBTITLE.getAsString ( ) );
		} else {
			player.sendTitle ( String.format ( EnumLanguage.RANK_GAME_OVER_TITLE.getAsString ( ) , pretty ) ,
							   String.format ( EnumLanguage.RANK_GAME_OVER_SUBTITLE.getAsString ( ) , pretty ) );
		}
	}
	
	private void respawn ( Player player ) {
		this.respawn_queue.add ( player.getUniqueId ( ) );
		
		// will send respawn packet in the next tick
		SchedulerUtil.scheduleSyncDelayedTask ( ( ) -> player.getBukkitPlayerOptional ( ).ifPresent (
				PacketSenderService.getInstance ( ) :: sendRespawnPacket ) );
	}
}