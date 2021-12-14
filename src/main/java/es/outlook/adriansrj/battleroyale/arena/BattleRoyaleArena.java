package es.outlook.adriansrj.battleroyale.arena;

import es.outlook.adriansrj.battleroyale.arena.airsupply.AirSupplyGenerator;
import es.outlook.adriansrj.battleroyale.arena.autostarter.AutoStarter;
import es.outlook.adriansrj.battleroyale.arena.bombing.BombingZoneGenerator;
import es.outlook.adriansrj.battleroyale.arena.border.BattleRoyaleArenaBorder;
import es.outlook.adriansrj.battleroyale.arena.drop.ItemDropManager;
import es.outlook.adriansrj.battleroyale.arena.restarter.Restarter;
import es.outlook.adriansrj.battleroyale.battlefield.Battlefield;
import es.outlook.adriansrj.battleroyale.battlefield.bus.BusSpawn;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.renderer.MinimapRendererArena;
import es.outlook.adriansrj.battleroyale.compass.CompassBar;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumLootContainer;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaEndEvent;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaPreparedEvent;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaStateChangeEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaIntroducedEvent;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfiguration;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfigurationContainer;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.Team;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobby;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobbyHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.schedule.ScheduledExecutorPool;
import es.outlook.adriansrj.battleroyale.scoreboard.Scoreboard;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.MiniMapUtil;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.battleroyale.util.itemstack.ItemStackUtil;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.battleroyale.util.mode.BattleRoyaleModeUtil;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.configurable.vector.ConfigurableVector;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.entity.EntityUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.math.RandomUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author AdrianSR / 03/09/2021 / 10:56 a. m.
 */
public class BattleRoyaleArena {
	
	protected static final ExecutorService EXECUTOR_SERVICE;
	
	static {
		EXECUTOR_SERVICE = ScheduledExecutorPool.getInstance ( ).getNewWorkStealingPool ( );
	}
	
	protected final UUID                           id;
	protected final String                         name;
	protected final BattleRoyaleArenaConfiguration configuration;
	protected final Battlefield                    battlefield;
	protected final BattleRoyaleMode               mode;
	protected final BattleRoyaleArenaWorld         world;
	protected       BattleRoyaleArenaBorder        border;
	protected final AutoStarter                    auto_starter;
	protected final Restarter                      restarter;
	protected final AirSupplyGenerator             air_supplies;
	protected final BombingZoneGenerator           bombing_zones;
	protected final ItemDropManager                drop_manager;
	
	/** stats */
	protected final BattleRoyaleArenaStats        stats;
	/** bus registry */
	protected final BattleRoyaleArenaBusRegistry  bus_registry;
	/** team registry */
	protected final BattleRoyaleArenaTeamRegistry team_registry;
	
	/** current state of the arena */
	protected volatile EnumArenaState state;
	protected volatile long           state_time;
	/** whether the battlefield is being prepared */
	protected volatile boolean        preparing;
	/** whether the battlefield is prepared */
	protected volatile boolean        prepared;
	/** whether this arena is over */
	protected volatile boolean        over;
	
	protected BattleRoyaleArena ( String name , BattleRoyaleArenaConfiguration configuration ) throws IllegalStateException {
		Validate.isValid ( Validate.notNull ( configuration , "configuration cannot be null" ) ,
						   "configuration cannot be invalid" );
		
		this.id            = UUID.randomUUID ( );
		this.name          = Objects.requireNonNull ( name , "name cannot be null" );
		this.configuration = new BattleRoyaleArenaConfiguration ( configuration );
		this.battlefield   = Validate.notNull ( configuration.getBattlefield ( ) , "battlefield cannot be null" );
		
		try {
			this.mode = Validate.notNull ( configuration.getMode ( ) , "mode cannot be null" );
		} catch ( FileNotFoundException ex ) {
			throw new IllegalStateException ( "configuration couldn't resolve the mode: " , ex );
		}
		
		this.world = new BattleRoyaleArenaWorld ( this );
		
		if ( configuration.isAutostartEnabled ( ) ) {
			this.auto_starter = new AutoStarter ( this );
		} else {
			this.auto_starter = null;
		}
		
		this.restarter     = new Restarter ( this );
		this.air_supplies  = new AirSupplyGenerator ( this );
		this.bombing_zones = new BombingZoneGenerator ( this );
		this.drop_manager  = new ItemDropManager ( this );
		this.stats         = new BattleRoyaleArenaStats ( this );
		this.team_registry = new BattleRoyaleArenaTeamRegistry ( this );
		this.prepared      = false;
		
		if ( Bukkit.isPrimaryThread ( ) ) {
			this.setState ( EnumArenaState.WAITING );
		} else {
			Bukkit.getScheduler ( ).runTask (
					BattleRoyale.getInstance ( ) , ( ) -> this.setState ( EnumArenaState.WAITING ) );
		}
		
		// bus registry
		Set < BusSpawn > spawns = battlefield.getConfiguration ( ).getBusSpawns ( );
		
		if ( spawns.size ( ) > 0 && spawns.stream ( ).anyMatch ( BusSpawn :: isValid ) ) {
			this.bus_registry = new BattleRoyaleArenaBusRegistry ( this );
		} else {
			this.bus_registry = null;
		}
	}
	
	public UUID getUniqueId ( ) {
		return id;
	}
	
	public String getName ( ) {
		return name;
	}
	
	public String getDescription ( ) {
		return configuration.getDescription ( );
	}
	
	public BattleRoyaleArenaConfiguration getConfiguration ( ) {
		return new BattleRoyaleArenaConfiguration ( configuration ); // safe copy
	}
	
	public World getWorld ( ) {
		Validate.isTrue ( prepared , "arena not prepared" );
		return world.getWorld ( );
	}
	
	public Battlefield getBattlefield ( ) {
		return battlefield;
	}
	
	public BattleRoyaleMode getMode ( ) {
		return mode;
	}
	
	public ZoneBounds getFullBounds ( ) {
		return world.bounds;
	}
	
	public ZoneBounds getCurrentBounds ( ) {
		return border.getCurrentBounds ( );
	}
	
	public ZoneBounds getFutureBounds ( ) {
		return border.getFutureBounds ( );
	}
	
	public BattleRoyaleArenaBorder getBorder ( ) {
		Validate.isTrue ( prepared , "arena not prepared" );
		return border;
	}
	
	/**
	 * Gets the auto-starter responsible for
	 * automatically start this arena.
	 *
	 * @return the auto-starter, or <b>null</b>
	 * if disabled in this arena.
	 */
	public AutoStarter getAutoStarter ( ) {
		return auto_starter;
	}
	
	/**
	 * Gets the restarter responsible for
	 * scheduling a task to restart this arena
	 * with a defined delay.
	 *
	 * @return the restarter useful to restart this arena.
	 */
	public Restarter getRestarter ( ) {
		return restarter;
	}
	
	public AirSupplyGenerator getAirSupplyGenerator ( ) {
		return air_supplies;
	}
	
	public BombingZoneGenerator getBombingZoneGenerator ( ) {
		return bombing_zones;
	}
	
	public ItemDropManager getDropManager ( ) {
		return drop_manager;
	}
	
	public BattleRoyaleArenaStats getStats ( ) {
		return stats;
	}
	
	/**
	 * Gets the bus registry of this arena.
	 * <br>
	 * Note that <b>null</b> if no valid {@link BusSpawn}s were provided
	 * to the battlefield configuration.
	 *
	 * @return the bus registry of this arena, or <b>null</b> if no valid
	 * {@link BusSpawn}s were provided to the battlefield configuration.
	 */
	public BattleRoyaleArenaBusRegistry getBusRegistry ( ) {
		return bus_registry;
	}
	
	public BattleRoyaleArenaTeamRegistry getTeamRegistry ( ) {
		return team_registry;
	}
	
	public synchronized EnumArenaState getState ( ) {
		if ( state == null ) {
			return EnumArenaState.STOPPED;
		} else {
			return state;
		}
	}
	
	/**
	 * Gets how long the arena has been in the state returned by {@link #getState()}.
	 *
	 * @return state time in milliseconds.
	 */
	public synchronized long getStateTime ( ) {
		return state_time;
	}
	
	/**
	 * Gets whether this arena is being prepared or not.
	 *
	 * @return whether this arena is being prepared or not.
	 */
	public synchronized boolean isPreparing ( ) {
		return preparing;
	}
	
	/**
	 * Gets whether this arena is prepared or not.
	 *
	 * @return whether this arena is prepared or not.
	 */
	public synchronized boolean isPrepared ( ) {
		return prepared;
	}
	
	/**
	 * Gets whether this arena is over.
	 *
	 * @return whether this arena is over.
	 */
	public synchronized boolean isOver ( ) {
		return over;
	}
	
	/**
	 * Gets all the players in this arena.
	 *
	 * @param world whether to include only players in the world of this arena.
	 * @return all the players in this arena.
	 */
	public Set < Player > getPlayers ( boolean world ) {
		return ( world ? this.world.getWorld ( ).getPlayers ( ) : Bukkit.getOnlinePlayers ( ) )
				.stream ( ).map ( Player :: getPlayer )
				.filter ( player -> Objects.equals ( player.getArena ( ) , this ) )
				.collect ( Collectors.toSet ( ) );
	}
	
	public Set < Player > getPlayers ( ) {
		return getPlayers ( getState ( ) == EnumArenaState.RUNNING );
	}
	
	public int getCount ( boolean world ) {
		return getPlayers ( world ).size ( );
	}
	
	public int getCount ( ) {
		return getCount ( getState ( ) == EnumArenaState.RUNNING );
	}
	
	public boolean isEmpty ( ) {
		return getCount ( ) == 0;
	}
	
	/**
	 * Gets whether this arena is full.
	 * <br>
	 * In other words this returns whether the limit of player
	 * (<b>determined by the mode</b>) is reached.
	 *
	 * @return whether this arena is full or not.
	 */
	public boolean isFull ( ) {
		return BattleRoyaleModeUtil.isLimitedPlayers ( mode ) && Bukkit.getOnlinePlayers ( )
				.stream ( ).map ( Player :: getPlayer )
				.filter ( player -> Objects.equals ( player.getArena ( ) , this ) )
				.count ( ) >= mode.getMaxPlayers ( );
	}
	
	public void prepare ( Runnable callback ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			switch ( state ) {
				case WAITING:
					break;
				case RUNNING:
					throw new IllegalStateException ( "arena is running" );
				case RESTARTING:
					throw new IllegalStateException ( "arena is restarting" );
				case STOPPED:
					throw new IllegalStateException ( "arena must be restarted" );
			}
			
			if ( !prepared && !preparing ) {
				prepare0 ( callback );
			}
		} else {
			Bukkit.getScheduler ( ).runTask (
					BattleRoyale.getInstance ( ) , ( ) -> prepare ( callback ) );
		}
	}
	
	public void prepare ( ) {
		prepare ( null );
	}
	
	protected void prepare0 ( Runnable callback ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			this.preparing = true;
			this.prepared  = false;
			
			if ( world.isPrepared ( ) ) {
				this.prepare1 ( callback );
			} else {
				this.world.prepare ( ( ) -> prepare1 ( callback ) );
			}
		} else {
			Bukkit.getScheduler ( ).runTask (
					BattleRoyale.getInstance ( ) , ( ) -> prepare0 ( callback ) );
		}
	}
	
	protected void prepare1 ( Runnable callback ) {
		this.preparation ( );
		
		this.preparing = false;
		this.prepared  = true;
		
		// border
		this.border = new BattleRoyaleArenaBorder ( this );
		
		// callback
		if ( callback != null ) {
			callback.run ( );
		}
		
		// firing event
		new ArenaPreparedEvent ( this ).callSafe ( );
	}
	
	/**
	 * Introduces the provided player into the game.
	 * <br>
	 * <b>Note that the player must be online to be introduced.</b>
	 * <br>
	 * <b>Note that the player will be introduced as a spectator
	 * if there are no non-full teams and the team limit is reached.</b>
	 *
	 * @param player the player to introduce.
	 * @param spectator whether to introduce the player as spectator.
	 */
	public void introduce ( org.bukkit.entity.Player player , boolean spectator ) {
		Validate.isTrue ( getState ( ) == EnumArenaState.RUNNING ,
						  "must be running to introduce a player" );
		
		// player must be in a team
		// to be introduced.
		Player br_player = Player.getPlayer ( player );
		
		if ( !br_player.hasTeam ( ) ) {
			throw new UnsupportedOperationException ( "player must be in a team" );
		}
		
		// then introducing
		if ( Bukkit.isPrimaryThread ( ) ) {
			if ( spectator || !mode.introduce ( br_player ) ) {
				br_player.setSpectator ( true );
				
				// firing event
				new PlayerArenaIntroducedEvent (
						br_player , this , true ).callSafe ( );
			} else {
				player.setGameMode ( GameMode.SURVIVAL );
				player.setTotalExperience ( 0 );
				player.setLevel ( 0 );
				player.setExp ( 0F );
				player.setFoodLevel ( 20 );
				player.setSaturation ( 20.0F );
				player.setHealth ( Math.max ( mode.getInitialHealth ( ) , 0.5D ) );
				EntityUtil.setMaxHealth ( player , Math.max ( mode.getMaxHealth ( ) , 0.5D ) );
				EntityUtil.clearPotionEffects ( player );
				
				player.getInventory ( ).clear ( );
				player.getInventory ( ).setArmorContents ( null );
				
				// minimap
				player.getInventory ( ).addItem ( ItemStackUtil.createViewItemStack (
						MiniMapUtil.createView ( new MinimapRendererArena ( this ) , world.getWorld ( ) ) ) );
				
				// initial loot
				LootConfiguration loot_configuration = battlefield.getConfiguration ( ).getLootConfiguration ( );
				
				if ( loot_configuration != null ) {
					LootConfigurationContainer initial = loot_configuration.getContainer ( EnumLootContainer.INITIAL );
					
					if ( initial != null && initial.isValid ( ) ) {
						initial.fill ( player.getInventory ( ) );
					}
				}
				
				player.updateInventory ( );
				
				// if no bus spawns were set, the bus registry will
				// be null; in that case we have to send players to
				// a random player spawn if any set.
				if ( bus_registry == null ) {
					Set < ConfigurableVector > player_spawns = battlefield.getConfiguration ( ).getPlayerSpawns ( );
					ConfigurableVector         spawn         = null;
					
					if ( player_spawns.size ( ) > 0 && player_spawns.stream ( )
							.anyMatch ( ConfigurableVector :: isValid ) ) {
						while ( spawn == null ) {
							spawn = RandomUtil.getRandomElement ( player_spawns );
							spawn = spawn != null && spawn.isValid ( ) ? spawn : null;
						}
					}
					
					if ( spawn != null ) {
						Location spawn_location = world.bounds.project ( spawn ).toLocation ( world.getWorld ( ) );
						
						// firing event
						PlayerArenaIntroducedEvent event = new PlayerArenaIntroducedEvent (
								br_player , this , spawn_location , false );
						event.callSafe ( );
						
						// teleporting
						player.teleport ( event.getSpawn ( ) );
					} else {
						ConsoleUtil.sendPluginMessage (
								ChatColor.RED , "Couldn't find a valid spawn for the player '"
										+ player.getName ( ) + "'" , BattleRoyale.getInstance ( ) );
					}
				} else {
					// firing event
					new PlayerArenaIntroducedEvent (
							br_player , this , false ).callSafe ( );
				}
			}
			
			// world border
			border.getPlayers ( ).add ( br_player );
			border.refresh ( );
			
			// making sure scoreboard is visible
			Scoreboard scoreboard = br_player.getBRScoreboard ( );
			
			if ( scoreboard != null ) {
				scoreboard.setVisible ( !spectator );
			}
			
			// making sure compass is visible
			CompassBar compass = br_player.getCompass ( );
			
			if ( compass != null ) {
				compass.setVisible ( !spectator );
			}
			
			// parachute
			br_player.setCanOpenParachute ( true );
		} else {
			Bukkit.getScheduler ( ).runTask (
					BattleRoyale.getInstance ( ) , ( ) -> introduce ( player , spectator ) );
		}
	}
	
	/**
	 * Introduces the provided player into the game.
	 * <br>
	 * <b>Note that the player must be online to be introduced.</b>
	 * <br>
	 * <b>Note that the player will be introduced as a spectator
	 * if there are no non-full teams and the team limit is reached.</b>
	 *
	 * @param br_player the player to introduce.
	 * @param spectator whether to introduce the player as spectator.
	 */
	public void introduce ( Player br_player , boolean spectator ) {
		Validate.notNull ( br_player , "br_player cannot be null" );
		
		// player must actually be online to be introduced
		br_player.getBukkitPlayerOptional ( ).ifPresent (
				player -> introduce ( player , spectator ) );
	}
	
	protected void remove ( org.bukkit.entity.Player player ) {
		remove ( Player.getPlayer ( Objects.requireNonNull ( player , "player cannot be null" ) ) );
	}
	
	protected void remove ( Player br_player ) {
		Validate.notNull ( br_player , "br_player cannot be null" );
		
		// un-showing border
		border.getPlayers ( ).remove ( br_player );
		border.refresh ( );
	}
	
	public synchronized void start ( ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			switch ( state ) {
				case WAITING:
					break;
				
				case RUNNING:
					throw new IllegalStateException ( "arena already started" );
				case RESTARTING:
					throw new IllegalStateException ( "arena is restarting" );
				case STOPPED:
					throw new IllegalStateException ( "arena must be restarted" );
			}
			
			if ( prepared ) {
				// filling teams
				this.getPlayers ( false ).stream ( ).filter (
						player -> !player.hasTeam ( ) ).forEach ( player -> {
					Team team = null;
					
					if ( mode.isSolo ( ) ) {
						team = team_registry.createAndRegisterTeam ( );
					} else if ( mode.isAutoFillEnabled ( ) ) {
						team = team_registry.getNextNotFull ( );
						
						if ( team == null && !team_registry.isFull ( ) ) {
							team = team_registry.createAndRegisterTeam ( );
						}
					}
					
					if ( team != null ) {
						player.setTeam ( team );
					}
				} );
				
				// updating state
				this.setState ( EnumArenaState.RUNNING );
				
				// world border
				border.start ( );
				
				// introducing players
				this.getPlayers ( false ).stream ( ).filter ( Player :: hasTeam ).forEach (
						player -> introduce ( player , false ) );
				
				// starting bus
				if ( bus_registry != null ) {
					bus_registry.start ( );
				}
			} else {
				if ( preparing ) {
					throw new IllegalStateException ( "battlefield is being prepared" );
				} else {
					throw new IllegalStateException ( "call prepare() first" );
				}
			}
		} else {
			Bukkit.getScheduler ( ).runTask (
					BattleRoyale.getInstance ( ) , this :: start );
		}
	}
	
	public synchronized void end ( Player winning_player , Team winning_team ) {
		Validate.isTrue ( getState ( ) == EnumArenaState.RUNNING ,
						  "must be running to end the arena" );
		Validate.isTrue ( !over , "arena is already over" );
		
		if ( Bukkit.isPrimaryThread ( ) ) {
			this.over = true;
			
			// firing event
			ArenaEndEvent event;
			
			if ( winning_player != null ) {
				event = new ArenaEndEvent ( this , winning_player );
			} else if ( winning_team != null ) {
				event = new ArenaEndEvent ( this , winning_team );
			} else {
				event = new ArenaEndEvent ( this );
			}
			
			event.callSafe ( );
		} else {
			Bukkit.getScheduler ( ).runTask (
					BattleRoyale.getInstance ( ) , ( ) -> end ( winning_player , winning_team ) );
		}
	}
	
	public synchronized void end ( Player winner ) {
		end ( winner , null );
	}
	
	public synchronized void end ( Team winner ) {
		end ( null , winner );
	}
	
	public synchronized void restart ( ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			switch ( state ) {
				case RUNNING:
				case WAITING:
				case STOPPED:
					break;
				
				case RESTARTING:
					throw new IllegalStateException ( "arena is already restarting" );
			}
			
			this.prepared = false;
			
			// moving players
			BattleRoyaleLobby lobby = BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( );
			
			// this ensures all the players in the arena
			// will actually be moved.
			Player.getPlayers ( ).stream ( )
					.filter ( player -> Objects.equals ( player.getArena ( ) , this ) )
					.forEach ( lobby :: introduce );
			
			// then restarting
			this.restartModules ( );
			this.setState ( EnumArenaState.RESTARTING );
			this.prepare0 ( ( ) -> Bukkit.getScheduler ( ).runTask (
					// ready to start
					BattleRoyale.getInstance ( ) , ( ) -> this.setState ( EnumArenaState.WAITING ) ) );
			this.over = false;
		} else {
			Bukkit.getScheduler ( ).runTask (
					BattleRoyale.getInstance ( ) , ( Runnable ) this :: restart );
		}
	}
	
	public synchronized void restart ( boolean instantly ) {
		if ( instantly ) {
			restart ( );
		} else {
			restarter.start ( );
		}
	}
	
	public synchronized void restart ( Duration countdown_duration ) {
		restarter.start ( Objects.requireNonNull (
				countdown_duration , "countdown_duration cannot be null" ) );
	}
	
	public synchronized void stop ( ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			if ( state == EnumArenaState.STOPPED ) {
				throw new IllegalStateException ( "arena already stopped" );
			}
			
			this.restartModules ( );
			this.setState ( EnumArenaState.STOPPED );
			this.over = false;
			
			// this ensures all the players in the arena
			// will actually be moved.
			BattleRoyaleLobby lobby = BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( );
			
			Player.getPlayers ( ).stream ( )
					.filter ( player -> Objects.equals ( player.getArena ( ) , this ) )
					.forEach ( lobby :: introduce );
			
			// then stopping world
			world.stop ( );
		} else {
			Bukkit.getScheduler ( ).runTask (
					BattleRoyale.getInstance ( ) , this :: stop );
		}
	}
	
	/**
	 * Prepares the battlefield for the game.
	 * <br>
	 * The world of the arena should be already prepared
	 * at this point; only things like loot chests are going
	 * to be prepared by this method.
	 */
	protected void preparation ( ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			// preparing loot chests
			battlefield.getConfiguration ( ).getLootChests ( ).forEach ( location -> {
				World world = this.world.getWorld ( );
				Block block = world.getBlockAt ( this.world.bounds.project ( location ).toLocation ( world ) );
				Chunk chunk = block.getChunk ( );
				
				if ( !chunk.isLoaded ( ) ) {
					chunk.load ( true );
				}
				
				block.setType ( UniversalMaterial.CHEST.getMaterial ( ) );
				block.getState ( ).update ( );
				block.setMetadata ( Constants.LOOT_CHEST_METADATA_KEY ,
									new FixedMetadataValue ( BattleRoyale.getInstance ( ) , this ) );
			} );
		} else {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , this :: preparation );
		}
	}
	
	/**
	 * Sets the state, state the time, and fires the state change event.
	 */
	protected synchronized void setState ( EnumArenaState state ) {
		Validate.notNull ( state , "state cannot be null" );
		Validate.isTrue ( Bukkit.isPrimaryThread ( ) , "must run in server thread" );
		
		final EnumArenaState old_state = this.state;
		this.state      = state;
		this.state_time = System.currentTimeMillis ( );
		
		new ArenaStateChangeEvent ( this , old_state , state ).callSafe ( );
	}
	
	protected synchronized void restartModules ( ) {
		Validate.isTrue ( Bukkit.isPrimaryThread ( ) , "must run in server thread" );
		
		this.world.restart ( );
		this.border.restart ( );
		
		if ( auto_starter != null ) {
			this.auto_starter.restart ( );
		}
		
		this.restarter.restart ( );
		this.air_supplies.restart ( );
		this.team_registry.clear ( );
		
		if ( bus_registry != null ) {
			this.bus_registry.restart ( );
		}
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		BattleRoyaleArena arena = ( BattleRoyaleArena ) o;
		return id.equals ( arena.id );
	}
	
	@Override
	public int hashCode ( ) {
		return Objects.hash ( id );
	}
}