package es.outlook.adriansrj.battleroyale.game.player;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.bus.Bus;
import es.outlook.adriansrj.battleroyale.bus.BusInstance;
import es.outlook.adriansrj.battleroyale.bus.BusRegistry;
import es.outlook.adriansrj.battleroyale.compass.CompassBar;
import es.outlook.adriansrj.battleroyale.compass.CompassBarSimple;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumPlayerSetting;
import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaLeaveEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaPreLeaveEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaSetEvent;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteInstance;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteRegistry;
import es.outlook.adriansrj.battleroyale.scoreboard.Scoreboard;
import es.outlook.adriansrj.battleroyale.scoreboard.ScoreboardSimple;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.core.player.PlayerWrapper;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.bukkit.entity.Entity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a battle royale player.
 *
 * @author AdrianSR / 28/08/2021 / 10:54 a. m.
 */
public final class Player extends PlayerWrapper {
	
	/**
	 * Get all the battle royale players that have been loaded so far.
	 *
	 * @return loaded battle royale players.
	 */
	public static Collection < Player > getPlayers ( ) {
		return PlayerHandler.getPlayers ( );
	}
	
	/**
	 * Gets the corresponding battle royale player from a given bukkit {@link org.bukkit.entity.Player}
	 *
	 * @param player the bukkit player.
	 *
	 * @return the battle royale player that corresponds to the given bukkit player.
	 */
	public static Player getPlayer ( org.bukkit.entity.Player player ) {
		return PlayerHandler.getPlayer ( player );
	}
	
	/**
	 * Gets the corresponding battle royale player from a given {@link UUID}. <br> Note that the {@link UUID} should be
	 * the <b>same</b> as would be used to get a certain <b>bukkit player</b>.
	 *
	 * @param id the unique {@link UUID} of the player to get.
	 *
	 * @return the battle royale player that corresponds to the given bukkit player.
	 */
	public static Player getPlayer ( UUID id ) {
		return PlayerHandler.getPlayer ( id );
	}
	
	private final PlayerDataStorage data_storage;
	
	/** data */
	private final Map < String, Object > data = new ConcurrentHashMap <> ( );
	
	/** the arena the player is in */
	BattleRoyaleArena arena;
	/** the team the player is on */
	Team              team;
	
	/** flag that determines whether the player is a spectator */
	private volatile boolean spectator;
	/** the target player to spectate */
	Player spectator_target;
	
	/** the compass of the player */
	private          CompassBar        compass;
	/** the scoreboard of the player */
	private          Scoreboard        scoreboard;
	/** the bus of the player */
	private          BusInstance < ? > bus;
	/** the parachute of the player */
	private          ParachuteInstance parachute;
	/** flag that determines whether the player can open the parachute. */
	private volatile boolean           parachute_flag;
	/** flag that determines whether the player is knocked. */
	private volatile boolean           knocked;
	
	/** flag player who knocked out this player. */
	private Player knocker;
	
	/** rank */
	private int rank;
	
	Player ( String name , UUID id , org.bukkit.entity.Player last_handle ) {
		super ( id , name );
		
		this.last_handle  = last_handle;
		this.data_storage = new PlayerDataStorage ( id , name );
		
		// unset rank
		this.rank = -1;
		
		// default scoreboard
		this.scoreboard = new ScoreboardSimple ( this );
		// it will be visible whn the player joins an arena.
		this.scoreboard.setVisible ( false );
		
		// default compass
		this.compass = new CompassBarSimple ( this );
		// we would like to set it visible when the arena starts
		this.compass.setVisible ( false );
	}
	
	public PlayerDataStorage getDataStorage ( ) {
		return data_storage;
	}
	
	public Optional < Object > getData ( String key ) {
		return Optional.ofNullable ( data.get ( key ) );
	}
	
	public boolean hasData ( String key ) {
		return getData ( key ).isPresent ( );
	}
	
	public synchronized BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	public Team getTeam ( ) {
		return team;
	}
	
	public boolean hasTeam ( ) {
		return team != null;
	}
	
	public synchronized boolean isSpectator ( ) {
		return spectator;
	}
	
	public synchronized boolean isPlaying ( ) {
		return isInArena ( ) && hasTeam ( ) && !isSpectator ( ) && isOnline ( );
	}
	
	//	@Override
	public synchronized Player getBRSpectatorTarget ( ) {
		return spectator_target;
	}
	
	public synchronized CompassBar getCompass ( ) {
		return compass;
	}
	
	public Scoreboard getBRScoreboard ( ) {
		return scoreboard;
	}
	
	public synchronized BusInstance < ? > getBus ( ) {
		this.dirtyCheck ( );
		
		// bus never be null
		if ( bus == null ) {
			bus = BusRegistry.getInstance ( )
					.getDefaultBus ( ).createInstance ( this );
		}
		
		return bus;
	}
	
	public synchronized ParachuteInstance getParachute ( ) {
		this.dirtyCheck ( );
		
		// parachute never be null
		if ( parachute == null ) {
			parachute = ParachuteRegistry.getInstance ( )
					.getDefaultParachute ( ).createInstance ( this );
		}
		
		return parachute;
	}
	
	/**
	 * Gets whether this player can open the parachute.
	 * <br>
	 * <b>Note that if the redeploy is enabled, this player will be
	 * able to open the parachute regardless of whether this flag is true.</b>
	 *
	 * @return whether this player can open the parachute.
	 */
	public synchronized boolean isCanOpenParachute ( ) {
		return parachute_flag;
	}
	
	public boolean isKnocked ( ) {
		return knocked;
	}
	
	public boolean isBeingRevived ( ) {
		return PlayerReviveHandler.getInstance ( ).reviving ( this );
	}
	
	public Player getKnocker ( ) {
		return knocker;
	}
	
	public int getRank ( ) {
		return rank;
	}
	
	public boolean isInArena ( ) {
		return arena != null;
	}
	
	public void setData ( String key , Object data ) {
		this.data.put ( key , data );
	}
	
	public synchronized boolean setKnocked ( boolean knocked , Player knocker ) {
		if ( this.arena != null && this.arena.getState ( ) == EnumArenaState.RUNNING
				&& this.knocked != knocked ) {
			this.knocked = knocked;
			
			if ( knocked ) {
				this.knocker = knocker;
			} else {
				this.knocker = null;
			}
			
			PlayerKnockHandler.getInstance ( ).process ( this , knocker , knocked );
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized boolean setKnocked ( boolean knocked ) {
		return setKnocked ( knocked , null );
	}
	
	public void setKnocker ( Player knocker ) {
		this.knocker = knocker;
	}
	
	public synchronized void setArena ( BattleRoyaleArena arena ) {
		Validate.notNull ( arena , "arena cannot be null" );
		Validate.isTrue ( arena.getState ( ) != EnumArenaState.STOPPED ,
						  "arena cannot be stopped" );
		
		final BattleRoyaleArena current = this.arena;
		
		if ( !Objects.equals ( current , arena ) ) {
			// leaving current
			if ( current != null ) {
				if ( current.getState ( ) == EnumArenaState.RUNNING ) {
					throw new UnsupportedOperationException (
							"cannot set player arena while is in arena that is running" );
				}
				
				// must leave team too
				leaveTeam ( );
			}
			
			this.arena = arena;
			
			// resetting temporal stats
			data_storage.resetTempStats ( );
			
			// firing event
			new PlayerArenaSetEvent ( this , arena ).callSafe ( );
		}
	}
	
	public synchronized boolean leaveArena ( ) {
		final BattleRoyaleArena current = this.arena;
		
		if ( current != null ) {
			// firing pre-event
			new PlayerArenaPreLeaveEvent ( this , current ).callSafe ( );
			
			// resetting values
			resetValues ( );
			// must leave team too
			leaveTeam ( );
			
			this.arena = null;
			
			// firing event
			new PlayerArenaLeaveEvent ( this , current ).callSafe ( );
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized boolean setTeam ( Team team ) {
		return TeamHandler.getInstance ( ).setTeam ( this , team );
	}
	
	public synchronized boolean setTeamDirty ( Team team ) {
		if ( team == null ) {
			leaveTeamDirty ( );
			return true;
		} else if ( Objects.equals ( team.getArena ( ) , arena ) && !Objects.equals ( team , this.team ) ) {
			leaveTeamDirty ( );
			
			if ( !team.players.contains ( this ) ) {
				team.players.add ( this );
			}
			
			if ( team.players.isEmpty ( ) && team.arena.getMode ( ).isTeamCreationEnabled ( ) ) {
				team.getArena ( ).getTeamRegistry ( ).unregisterTeam ( team );
			}
			
			team = null;
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized boolean leaveTeam ( boolean send_to_lobby ) {
		return TeamHandler.getInstance ( ).leaveTeam ( this , send_to_lobby );
	}
	
	public synchronized boolean leaveTeam ( ) {
		return TeamHandler.getInstance ( ).leaveTeam ( this );
	}
	
	public synchronized boolean leaveTeamDirty ( ) {
		if ( team != null ) {
			team.players.remove ( this );
			
			if ( team.players.isEmpty ( ) && team.arena.getMode ( ).isTeamCreationEnabled ( ) ) {
				team.getArena ( ).getTeamRegistry ( ).unregisterTeam ( team );
			}
			
			team = null;
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized void setSpectator ( boolean flag ) {
		Validate.notNull ( arena , "must be in an arena" );
		
		if ( this.spectator != flag ) {
			this.spectator = flag;
			
			PlayerSpectatorHandler.getInstance ( ).process ( this , spectator );
		}
	}
	
	public synchronized void setSpectatorTargetBR ( Player target ) {
		Validate.isTrue ( isSpectator ( ) , "must be a spectator" );
		Validate.notNull ( target , "target cannot be null" );
		Validate.isTrue ( !Objects.equals ( target , this ) , "cannot self-spectate" );
		
		PlayerSpectatorHandler.getInstance ( ).setSpectatorTarget ( this , target );
	}
	
	public void toggleSpectatorTarget ( ) {
		Validate.isTrue ( isSpectator ( ) , "must be in spectator mode" );
		
		getBukkitPlayerOptional ( ).ifPresent (
				PlayerSpectatorHandler.getInstance ( ) :: toggleCamera );
	}
	
	/**
	 * Sets the player to spectate.
	 * <p/>
	 * <b>Note that the target must be a player,
	 * otherwise {@link UnsupportedOperationException} will be thrown</b>
	 *
	 * @see #setSpectatorTargetBR(Player)
	 * @param entity the entity to follow or null to reset
	 */
	@Override
	public void setSpectatorTarget ( Entity entity ) {
		if ( entity instanceof org.bukkit.entity.Player ) {
			setSpectatorTargetBR ( Player.getPlayer ( entity.getUniqueId ( ) ) );
		} else {
			throw new UnsupportedOperationException ( "can only spectate players" );
		}
	}
	
	/**
	 *
	 * @param compass
	 * @param dispose_previous whether to dispose/destroy the previous compass.
	 */
	public synchronized void setCompass ( CompassBar compass , boolean dispose_previous ) {
		if ( !Objects.equals ( Objects.requireNonNull (
				compass , "compass cannot be null" ).getPlayer ( ) , this ) ) {
			throw new IllegalArgumentException ( "this player and the owner of the compass must match" );
		}
		
		final CompassBar previous = this.compass;
		
		if ( !Objects.equals ( previous , compass ) ) {
			this.compass = compass;
			
			if ( previous != null && dispose_previous ) {
				previous.destroy ( );
			}
		}
	}
	
	public synchronized void setCompass ( CompassBar compass ) {
		setCompass ( compass , true );
	}
	
	public void setScoreboardBR ( Scoreboard scoreboard ) {
		if ( !Objects.equals ( Objects.requireNonNull (
				scoreboard , "scoreboard cannot be null" ).getPlayer ( ) , this ) ) {
			throw new IllegalArgumentException ( "this player and the owner of the scoreboard must match" );
		}
		
		if ( !Objects.equals ( this.scoreboard , scoreboard ) ) {
			this.scoreboard = scoreboard;
		}
	}
	
	/**
	 * Sets whether this player can open the parachute.
	 * <br>
	 * <b>Note that if the redeploy is enabled, this player will be
	 * able to open the parachute regardless of whether this flag
	 * is set to true.</b>
	 *
	 * @param flag whether this player can open the parachute or not.
	 */
	public synchronized void setCanOpenParachute ( boolean flag ) {
		this.parachute_flag = flag;
	}
	
	public void setRank ( int rank ) {
		if ( rank >= 0 ) {
			this.rank = rank;
		} else {
			this.rank = -1; // unset
		}
	}
	
	private synchronized void dirtyCheck ( ) {
		if ( data_storage.dirty ) {
			// updating bus
			Bus bus_configuration = data_storage.getSetting ( Bus.class , EnumPlayerSetting.BUS );
			
			if ( this.bus == null || !Objects.equals ( bus_configuration , this.bus.getConfiguration ( ) ) ) {
				if ( bus != null && bus.isStarted ( ) ) {
					final BusInstance < ? > final_bus = this.bus;
					
					SchedulerUtil.runTask ( final_bus :: restart );
				}
				
				BusInstance < ? > instance = bus_configuration.createInstance ( this );
				
				if ( instance == null ) {
					throw new IllegalStateException (
							bus_configuration.getClass ( ).getName ( ) + " returned a null instance" );
				}
				
				this.bus = instance;
			}
			
			// updating parachute
			Parachute parachute_configuration = data_storage.getSetting (
					Parachute.class , EnumPlayerSetting.PARACHUTE );
			
			if ( this.parachute == null || !Objects.equals ( parachute_configuration , this.parachute.getConfiguration ( ) ) ) {
				boolean was_open = parachute != null && parachute.isOpen ( );
				
				if ( was_open ) {
					final ParachuteInstance final_parachute = this.parachute;
					
					SchedulerUtil.runTask ( final_parachute :: close );
				}
				
				ParachuteInstance instance = parachute_configuration.createInstance ( this );
				
				if ( instance == null ) {
					throw new IllegalStateException (
							parachute_configuration.getClass ( ).getName ( ) + " returned a null instance" );
				}
				
				this.parachute = instance;
				
				if ( was_open ) {
					SchedulerUtil.runTask ( instance :: open );
				}
			}
			
			// we're done updating
			data_storage.dirty = false;
		}
	}
	
	private synchronized void resetValues ( ) {
		this.rank           = -1;
		this.knocked        = false;
		this.knocker        = null;
		this.parachute_flag = false;
		
		// spectator mode
		this.setSpectator ( false );
		
		// restarting bus
		if ( bus != null && bus.isStarted ( ) ) {
			bus.restart ( );
		}
		
		// closing parachute
		if ( parachute != null && parachute.isOpen ( ) ) {
			parachute.close ( );
		}
	}
}