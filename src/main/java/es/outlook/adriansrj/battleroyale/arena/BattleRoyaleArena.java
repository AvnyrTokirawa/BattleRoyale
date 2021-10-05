package es.outlook.adriansrj.battleroyale.arena;

import es.outlook.adriansrj.battleroyale.arena.border.BattleRoyaleArenaBorder;
import es.outlook.adriansrj.battleroyale.battlefield.Battlefield;
import es.outlook.adriansrj.battleroyale.battlefield.bus.BusSpawn;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.renderer.MinimapRendererArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumLootContainer;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaStateChangeEvent;
import es.outlook.adriansrj.battleroyale.exception.WorldRegionLimitReached;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobby;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobbyHandler;
import es.outlook.adriansrj.battleroyale.loot.LootConfiguration;
import es.outlook.adriansrj.battleroyale.loot.LootConfigurationContainer;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.battleroyale.player.Team;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.MiniMapUtil;
import es.outlook.adriansrj.battleroyale.util.itemstack.ItemStackUtil;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.battleroyale.util.mode.BattleRoyaleModeUtil;
import es.outlook.adriansrj.core.util.configurable.vector.ConfigurableVector;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.entity.EntityUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.math.RandomUtil;
import org.apache.commons.lang3.Validate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author AdrianSR / 03/09/2021 / 10:56 a. m.
 */
public class BattleRoyaleArena {
	
	// TODO: when all the arenas in a certain world stops, the world must be restarted as well
	
	protected static final ExecutorService EXECUTOR_SERVICE;
	
	static {
		EXECUTOR_SERVICE = Executors.newWorkStealingPool ( );
	}
	
	protected final UUID                    id;
	protected final String                  name;
	protected final World                   world;
	protected final Battlefield             battlefield;
	protected final BattleRoyaleMode        mode;
	protected final BattleRoyaleArenaRegion region;
	protected final BattleRoyaleArenaBorder border;
	
	/** bus registry */
	protected final BattleRoyaleArenaBusRegistry  bus_registry;
	/** team registry */
	protected final BattleRoyaleArenaTeamRegistry team_registry;
	
	/** current state of the arena */
	protected volatile EnumArenaState state;
	protected volatile long           state_time;
	/** whether the battlefield is prepared */
	protected volatile boolean        prepared;
	
	protected BattleRoyaleArena ( String name , World world , Battlefield battlefield , BattleRoyaleMode mode )
			throws IllegalStateException {
		this.id            = UUID.randomUUID ( );
		this.name          = Objects.requireNonNull ( name , "name cannot be null" );
		this.world         = world;
		this.battlefield   = battlefield;
		this.mode          = mode;
		this.region        = new BattleRoyaleArenaRegion ( this );
		this.border        = new BattleRoyaleArenaBorder ( this );
		this.team_registry = new BattleRoyaleArenaTeamRegistry ( this );
		this.prepared      = false;
		
		this.setState ( EnumArenaState.WAITING );
		
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
	
	public World getWorld ( ) {
		return world;
	}
	
	public Battlefield getBattlefield ( ) {
		return battlefield;
	}
	
	public BattleRoyaleMode getMode ( ) {
		return mode;
	}
	
	public ZoneBounds getFullBounds ( ) {
		return region.bounds;
	}
	
	public ZoneBounds getCurrentBounds ( ) {
		return border.getCurrentBounds ( );
	}
	
	public ZoneBounds getFutureBounds ( ) {
		return border.getFutureBounds ( );
	}
	
	public BattleRoyaleArenaBorder getBorder ( ) {
		return border;
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
		return state;
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
	 * Gets whether this arena is prepared to start or not.
	 *
	 * @return whether this arena is prepared to start or not.
	 */
	public synchronized boolean isPrepared ( ) {
		return prepared;
	}
	
	/**
	 * Gets all the players in this arena.
	 *
	 * @param world whether to include only players in the world of this arena.
	 * @return all the players in this arena.
	 */
	public Set < Player > getPlayers ( boolean world ) {
		return ( world ? this.world.getPlayers ( ) : Bukkit.getOnlinePlayers ( ) )
				.stream ( ).map ( Player :: getPlayer )
				.filter ( player -> Objects.equals ( player.getArena ( ) , this ) )
				.collect ( Collectors.toSet ( ) );
	}
	
	public Set < Player > getPlayers ( ) {
		return getPlayers ( true );
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
	
	public void introduce ( org.bukkit.entity.Player player , boolean spectator ) {
		Player br_player     = Player.getPlayer ( player );
		Team   next_not_full = team_registry.getNextNotFull ( );
		
		if ( !mode.introduce ( br_player ) || ( !br_player.hasTeam ( )
				&& ( !mode.isAutoFillEnabled ( ) || ( team_registry.isFull ( ) && next_not_full == null ) ) ) ) {
			spectator = true;
		}
		
		if ( spectator ) {
			br_player.setSpectator ( true );
		} else {
			if ( !br_player.hasTeam ( ) ) {
				br_player.setTeam ( next_not_full != null ? next_not_full
											: team_registry.createAndRegisterTeam ( ) );
			}
			
			player.setGameMode ( GameMode.SURVIVAL );
			player.setTotalExperience ( 0 );
			player.setLevel ( 0 );
			player.setExp ( 0F );
			player.setFoodLevel ( 20 );
			player.setSaturation ( 20.0F );
			player.setHealth ( Math.max ( mode.getInitialHealth ( ) , 0.5D ) );
			EntityUtil.setMaxHealth ( player , Math.max ( mode.getMaxHealth ( ) , 0.5D ) );
			player.getActivePotionEffects ( ).clear ( );
			
			player.getInventory ( ).clear ( );
			player.getInventory ( ).setArmorContents ( null );
			
			// minimap
			player.getInventory ( ).addItem ( ItemStackUtil.createViewItemStack (
					MiniMapUtil.createView ( new MinimapRendererArena ( this ) , world ) ) );
			
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
					player.teleport ( region.bounds.project ( spawn ).toLocation ( world ) );
				} else {
					ConsoleUtil.sendPluginMessage (
							ChatColor.RED , "Couldn't find a valid spawn for the player '"
									+ player.getName ( ) + "'" , BattleRoyale.getInstance ( ) );
				}
			}
		}
		
		// world border
		border.getPlayers ( ).add ( br_player );
		border.refresh ( );
		
		// compass
		if ( br_player.getCompass ( ) != null ) {
			br_player.getCompass ( ).setVisible ( !spectator );
		}
		
		// parachute
		br_player.setCanOpenParachute ( true );
	}
	
	/**
	 * <br>
	 * <b>Note that the player must be online to be introduced.</b>
	 *
	 * @param br_player
	 * @param spectator TODO
	 */
	public void introduce ( Player br_player , boolean spectator ) {
		Validate.notNull ( br_player , "player cannot be null" );
		
		// player must actually be online to be introduced
		br_player.getBukkitPlayerOptional ( ).ifPresent (
				player -> introduce ( player , spectator ) );
	}
	
	public synchronized void start ( ) {
		switch ( state ) {
			case WAITING:
				break;
			
			case RUNNING:
				throw new IllegalStateException ( "arena already started" );
			case RESTARTING:
				throw new IllegalStateException ( "arena is restarting" );
			case STOPPED:
				throw new IllegalStateException ( "arena requires the world to be restarted" );
		}
		
		if ( prepared ) {
			this.setState ( EnumArenaState.RUNNING );
			
			// world border
			border.start ( );
			
			// TODO: put players without team on a team
			// introducing players
			for ( Team team : team_registry ) {
				for ( Player player : team.getPlayers ( ) ) {
					introduce ( player , false );
				}
			}
			
			// starting bus
			if ( bus_registry != null ) {
				bus_registry.start ( );
			}
		} else {
			throw new IllegalStateException ( "call prepare() first" );
		}
	}
	
	public synchronized void restart ( ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			switch ( state ) {
				case RUNNING:
				case WAITING:
					break;
				
				case RESTARTING:
					throw new IllegalStateException ( "arena is already restarting" );
				case STOPPED:
					throw new IllegalStateException ( "arena requires the world to be restarted" );
			}
			
			this.prepared = false;
			
			// moving players
			BattleRoyaleLobby lobby = BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( );
			
			Player.getPlayers ( ).stream ( )
					.filter ( player -> Objects.equals ( player.getArena ( ) , this ) )
					.forEach ( lobby :: introduce );
			
			this.region.disposeCurrentRegion ( player -> {
				lobby.introduce ( player );
				return true;
			} );
			
			// then restarting
			try {
				// must reassign the region before anything
				// else, as the other members will probably
				// access the bounds or a location within this arena.
				this.region.reassignRegion ( );
			} catch ( WorldRegionLimitReached ex ) {
				this.stop ( );
				return;
			}
			
			this.border.restart ( );
			this.team_registry.clear ( );
			
			if ( bus_registry != null ) {
				this.bus_registry.restart ( );
			}
			
			this.setState ( EnumArenaState.RESTARTING );
			this.prepare0 ( ( ) -> {
				this.setState ( EnumArenaState.WAITING /* ready to start */ );
			} );
		} else {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , this :: restart );
		}
	}
	
	public synchronized void stop ( ) {
		if ( state == EnumArenaState.STOPPED ) {
			throw new IllegalStateException ( "arena already stopped" );
		}
		
		this.setState ( EnumArenaState.STOPPED );
		this.region.disposeCurrentRegion ( player -> {
			BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( ).introduce ( player );
			return true;
		} );
		
		if ( bus_registry != null ) {
			this.bus_registry.finish ( );
		}
	}
	
	protected void prepare0 ( Runnable callback ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			this.region.shape ( ( ) -> {
				this.preparation ( );
				this.prepared = true;
				
				// callback
				if ( callback != null ) {
					callback.run ( );
				}
			} );
		} else {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> prepare0 ( callback ) );
		}
	}
	
	public void prepare ( Runnable callback ) {
		switch ( state ) {
			case WAITING:
				break;
			case RUNNING:
				throw new IllegalStateException ( "arena is running" );
			case RESTARTING:
				throw new IllegalStateException ( "arena is restarting" );
			case STOPPED:
				throw new IllegalStateException ( "arena requires the world to be restarted" );
		}
		
		if ( !prepared ) {
			prepare0 ( callback );
		}
	}
	
	public void prepare ( ) {
		prepare ( null );
	}
	
	protected void preparation ( ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			// preparing loot chests
			battlefield.getConfiguration ( ).getLootChests ( ).forEach ( location -> {
				Block block = world.getBlockAt ( region.bounds.project ( location ).toLocation ( world ) );
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
		
		final EnumArenaState old_state = this.state;
		this.state      = state;
		this.state_time = System.currentTimeMillis ( );
		
		new ArenaStateChangeEvent ( this , old_state , state ).callSafe ( );
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