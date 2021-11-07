package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaTeamRegistry;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaStat;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.enums.EnumStat;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaEndEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaPreLeaveEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerDeathEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerStatSetEvent;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.Team;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.packet.sender.PacketSenderService;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteInstance;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.battleroyale.util.mode.BattleRoyaleModeUtil;
import es.outlook.adriansrj.battleroyale.util.stuff.PlayerStuffChestHandler;
import es.outlook.adriansrj.battleroyale.util.task.BukkitRunnableWrapper;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
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
	
	// SOLO: the rank is determined when the player dies, or when is the last one alive. (player rank)
	// TEAM: the rank is determined when all the players in the team dies, or when the team is the last one alive. (team rank)
	// KILL-LIMIT: the rank is determined when the kill-limit is reached. (player rank)
	
	/**
	 * Task that starts a countdown to respawn a player.
	 *
	 * @author AdrianSR / 19/09/2021 / 08:52 a. m.
	 */
	private static class RespawnTask extends BukkitRunnableWrapper {
		
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
		
		if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING
				&& !arena.isOver ( )
				&& br_player.hasTeam ( ) && !br_player.isSpectator ( ) ) {
			BattleRoyaleMode mode       = arena.getMode ( );
			boolean          respawning = mode.isRespawnEnabled ( );
			
			// death location
			setDeathLocation ( br_player , player.getLocation ( ) );
			// this will respawn the player in spectator mode
			respawn ( br_player );
			
			// keeping inventory and level;
			// or spawning stuff chest.
			if ( respawning ) {
				event.setKeepInventory ( true );
				event.setKeepLevel ( true );
			} else {
				PlayerStuffChestHandler.getInstance ( ).spawnStuffChest (
						event.getDrops ( ) , player.getLocation ( ).add ( 0.0D , 1.0D , 0.0D ) );
				
				// clearing drops
				event.getDrops ( ).clear ( );
			}
			
			// processing
			processRanking ( processDeathEvent ( event ) );
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
					if ( !arena.isOver ( ) ) {
						RespawnTask task = new RespawnTask ( br_player );
						task.runTaskTimer (
								BattleRoyale.getInstance ( ) , 0L , 10L );
						
						task_map.put ( player.getUniqueId ( ) , task );
					}
				} else {
					// introducing into spectator mode
					SchedulerUtil.scheduleSyncDelayedTask ( ( ) -> br_player.setSpectator ( true ) );
					
					// displaying position.
					int rank = br_player.getRank ( );
					
					if ( rank != -1 ) {
						sendRankTitle ( br_player , rank );
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
	
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onDesert ( PlayerArenaPreLeaveEvent event ) {
		Player            player = event.getPlayer ( );
		BattleRoyaleArena arena  = event.getArena ( );
		
		if ( arena.getState ( ) == EnumArenaState.RUNNING
				&& !arena.isOver ( )
				&& player.hasTeam ( ) && !player.isSpectator ( ) ) {
			processRanking ( event );
		}
	}
	
	// this handler will cancel the respawn task
	// when the arena ends, as it would override
	// the rank titles; and as them are not necessary
	// at this point, then we can cancel them.
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onEnd ( ArenaEndEvent event ) {
		task_map.values ( ).forEach ( RespawnTask :: cancel );
		task_map.clear ( );
	}
	
	// -------------- utils
	
	private PlayerDeathEvent processDeathEvent ( org.bukkit.event.entity.PlayerDeathEvent event ) {
		org.bukkit.entity.Player player      = event.getEntity ( );
		Player                   br_player   = Player.getPlayer ( player );
		Player                   br_killer   = null;
		EntityDamageEvent        last_damage = player.getLastDamageCause ( );
		BattleRoyaleArena        arena       = br_player.getArena ( );
		
		PlayerDeathEvent.Cause cause = last_damage != null
				? PlayerDeathEvent.Cause.of ( last_damage.getCause ( ) ) : null;
		boolean headshot = false;
		
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
					
					// checking headshot
					if ( ( projectile.getLocation ( ).getY ( )
							- player.getLocation ( ).getY ( ) ) > 1.35D ) {
						headshot = true;
					}
				}
			}
		}
		
		// headshot from metadata
		if ( player.hasMetadata ( Constants.HEADSHOT_METADATA_KEY ) ) {
			headshot = true;
			
			// disposing metadata
			for ( Plugin plugin : Bukkit.getPluginManager ( ).getPlugins ( ) ) {
				player.removeMetadata ( Constants.HEADSHOT_METADATA_KEY , plugin );
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
				br_player , br_killer , cause , headshot , event.getDeathMessage ( ) , false );
		
		wrapper.setDeathMessage ( event.getDeathMessage ( ) );
		wrapper.setKeepInventory ( event.getKeepInventory ( ) );
		wrapper.setKeepLevel ( event.getKeepLevel ( ) );
		
		wrapper.call ( );
		
		// passing wrapper values
		event.setDeathMessage ( wrapper.getDeathMessage ( ) );
		event.setKeepInventory ( wrapper.isKeepInventory ( ) );
		event.setKeepLevel ( wrapper.isKeepLevel ( ) );
		
		return wrapper;
	}
	
	private void processRanking ( PlayerDeathEvent event ) {
		Player            br_player        = event.getPlayer ( );
		BattleRoyaleArena arena            = br_player.getArena ( );
		BattleRoyaleMode  mode             = arena.getMode ( );
		boolean           respawning       = mode.isRespawnEnabled ( );
		boolean           determined_kills = BattleRoyaleModeUtil.isDeterminedByKills ( mode );
		String            error_message    = null;
		Player            winning_player   = null;
		Team              winning_team     = null;
		boolean           over             = false;
		
		if ( !determined_kills && !respawning ) {
			// if it is solo, the rank will be
			// instantly calculated; otherwise,
			// the rank will be calculated once
			// the last living member of the team dies.
			int rank = -1;
			
			if ( mode.isSolo ( ) ) {
				rank = calculateRank ( br_player );
				
				// player rank
				br_player.setRank ( rank );
			} else {
				Team team = br_player.getTeam ( );
				
				if ( team.getPlayers ( ).stream ( ).noneMatch (
						teammate -> teammate.isPlaying ( ) && !Objects.equals ( teammate , br_player ) ) ) {
					rank = calculateRank ( team );
					
					// team rank
					team.setRank ( rank );
					// player rank (the same as the team rank)
					br_player.setRank ( rank );
				}
			}
			
			// game ending
			if ( rank != -1 ) {
				if ( rank > 0 ) {
					Player br_killer = event.getKiller ( );
					
					// in case rank is 1, this means there
					// is only one player/team alive, which
					// is actually the winner of the match.
					if ( rank == 1 ) {
						if ( mode.isSolo ( ) ) {
							if ( br_killer != null ) {
								winning_player = br_killer;
							} else {
								// was not killed by another player;
								// we must find out who is the last
								// player standing.
								winning_player = arena.getPlayers ( ).stream ( )
										.filter ( other -> !Objects.equals ( other , br_player ) )
										.filter ( Player :: isPlaying )
										.findFirst ( ).orElse ( null );
							}
							
							if ( winning_player != null ) {
								// best rank for the winner
								winning_player.setRank ( 0 );
								sendRankTitle ( winning_player , 0 );
								
								// incrementing stat
								incrementWinStat ( winning_player );
							} else {
								error_message = "The winning player could not be determined";
							}
						} else {
							if ( br_killer != null ) {
								winning_team = br_killer.getTeam ( );
							} else {
								// was not killed by another player;
								// we must find out which is the last
								// team standing.
								winning_team = arena.getTeamRegistry ( ).stream ( )
										.filter ( other -> !Objects.equals ( other , br_player.getTeam ( ) ) )
										.filter ( Team :: isAlive )
										.findFirst ( ).orElse ( null );
							}
							
							if ( winning_team != null ) {
								// best rank for the winning team
								// and its members
								winning_team.setRank ( 0 );
								winning_team.getPlayers ( ).forEach ( member -> {
									member.setRank ( 0 );
									sendRankTitle ( member , 0 );
									
									// incrementing stat
									incrementWinStat ( member );
								} );
							} else {
								error_message = "The winning team could not be determined";
							}
						}
						
						// marking as over
						over = true;
					}
				} else {
					if ( mode.isSolo ( ) ) {
						error_message = "The winner could not be determined " +
								"as the only player who was alive has died";
					} else {
						error_message = "The winning-team could not be determined " +
								"as there is only one team in the arena";
					}
					
					// marking as over
					over = true;
				}
			}
		} else if ( determined_kills ) {
			// end determined by kills
			int kill_limit = mode.getMaxKills ( );
			
			if ( arena.getStats ( ).get ( EnumArenaStat.KILLS ) >= kill_limit ) {
				Player winner = arena.getPlayers ( false ).stream ( )
						.filter ( Player :: isPlaying ).min ( statComparator ( EnumStat.KILLS ) )
						.orElse ( null );
				
				if ( winner != null ) {
					// best rank for the winner
					winner.setRank ( 0 );
					sendRankTitle ( winner , 0 );
					
					// incrementing stat
					incrementWinStat ( winner );
					
					// ranking rest of players
					List < Player > rest = arena.getPlayers ( false ).stream ( )
							.filter ( player -> player.hasTeam ( ) && player.isOnline ( ) )
							.filter ( other -> !Objects.equals ( other , winner ) )
							.sorted ( statComparator ( EnumStat.KILLS ) ).collect ( Collectors.toList ( ) );
					
					for ( int i = 0 ; i < rest.size ( ) ; i++ ) {
						Player other = rest.get ( i );
						int    rank  = i + 1;
						
						other.setRank ( rank );
						sendRankTitle ( other , rank );
						
						// incrementing stat
						incrementWinStat ( other );
					}
				} else {
					ConsoleUtil.sendPluginMessage (
							ChatColor.RED , "The winner could not be determined" ,
							BattleRoyale.getInstance ( ) );
				}
				
				// marking as over
				over = true;
			}
		}
		
		// then ending
		if ( over && !arena.isOver ( ) ) {
			arena.end ( winning_player , winning_team );
		}
		
		// couldn't determine winner
		if ( error_message != null ) {
			ConsoleUtil.sendPluginMessage (
					ChatColor.RED , error_message , BattleRoyale.getInstance ( ) );
		}
	}
	
	private void processRanking ( PlayerArenaPreLeaveEvent event ) {
		Player                        deserter      = event.getPlayer ( );
		BattleRoyaleArena             arena         = event.getArena ( );
		BattleRoyaleArenaTeamRegistry team_registry = arena.getTeamRegistry ( );
		BattleRoyaleMode              mode          = arena.getMode ( );
		
		int count = ( int ) team_registry.stream ( )
				.filter ( Team :: isAlive ).count ( );
		
		if ( count <= 2 ) {
			Player winning_player = null;
			Team   winning_team   = null;
			String error_message  = null;
			
			if ( count == 2 ) {
				if ( mode.isSolo ( ) ) {
					winning_player = arena.getPlayers ( ).stream ( )
							.filter ( other -> !Objects.equals ( other , deserter ) )
							.filter ( Player :: isPlaying )
							.findFirst ( ).orElse ( null );
				} else {
					winning_team = arena.getTeamRegistry ( ).stream ( )
							.filter ( other -> !Objects.equals ( other , deserter.getTeam ( ) ) )
							.filter ( Team :: isAlive )
							.findFirst ( ).orElse ( null );
				}
				
				if ( winning_team != null ) {
					// best rank for the winning team
					// and its members
					winning_team.setRank ( 0 );
					winning_team.getPlayers ( ).forEach ( member -> {
						member.setRank ( 0 );
						sendRankTitle ( member , 0 );
						
						// incrementing stat
						incrementWinStat ( member );
					} );
				} else if ( winning_player != null ) {
					// best rank for the winner
					winning_player.setRank ( 0 );
					sendRankTitle ( winning_player , 0 );
					
					// incrementing stat
					incrementWinStat ( winning_player );
				}
			} else {
				if ( mode.isSolo ( ) ) {
					error_message = "The winner could not be determined " +
							"as the only player who was alive has deserted";
				} else {
					error_message = "The winning-team could not be determined " +
							"as there is only one team in the arena";
				}
			}
			
			// couldn't determine winner
			if ( error_message != null ) {
				ConsoleUtil.sendPluginMessage (
						ChatColor.RED , error_message , BattleRoyale.getInstance ( ) );
			}
			
			// then ending
			arena.end ( winning_player , winning_team );
		}
	}
	
	/**
	 * Increments the {@link EnumStat#WINS} for the
	 * provided player.
	 *
	 * @param player the player to benefit.
	 */
	private void incrementWinStat ( Player player ) {
		player.getDataStorage ( ).incrementStat ( EnumStat.WINS , 1 , true );
		player.getDataStorage ( ).incrementTempStat ( EnumStat.WINS , 1 );
	}
	
	/**
	 * Calculates the rank of the provided {@link Player}.
	 * <br>
	 * <b>This will obviously work only if the respawn is not
	 * enabled, otherwise the result is unknown.</b>
	 *
	 * @param player the player to calculate.
	 * @return the rank of the player.
	 */
	private int calculateRank ( Player player ) {
		return player.getArena ( ).getTeamRegistry ( ).getHandle ( ).stream ( )
				.map ( team -> team.getPlayers ( ).stream ( )
						.filter ( alive -> !Objects.equals ( alive , player ) )
						.filter ( Player :: isPlaying )
						.count ( ) )
				.reduce ( 0L , Long :: sum ).intValue ( );
	}
	
	/**
	 * Calculates the rank of the provided {@link Team}.
	 * <br>
	 * <b>This will obviously work only if the respawn is not
	 * enabled, otherwise the result is unknown.</b>
	 *
	 * @param team the team to calculate.
	 * @return the rank of the team.
	 */
	private int calculateRank ( Team team ) {
		return ( int ) team.getArena ( ).getTeamRegistry ( ).getHandle ( ).stream ( )
				.filter ( other -> !Objects.equals ( team , other ) )
				.filter ( other -> !other.isEmpty ( ) && other.getPlayers ( ).stream ( ).anyMatch ( Player :: isPlaying ) )
				.count ( );
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
	
	private void setDeathLocation ( Player player , Location location ) {
		death_location_map.put ( player.getUniqueId ( ) , location );
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