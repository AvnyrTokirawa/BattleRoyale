package es.outlook.adriansrj.battleroyale.game.player;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaStateChangeEvent;
import es.outlook.adriansrj.battleroyale.gui.spectator.SpectatorGUI;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobby;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobbyHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.function.FunctionUtil;
import es.outlook.adriansrj.core.util.packet.PacketChannelHandler;
import es.outlook.adriansrj.core.util.packet.PacketEvent;
import es.outlook.adriansrj.core.util.packet.PacketListener;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class responsible for handling spectator players.
 *
 * @author AdrianSR / 18/09/2021 / 09:22 p. m.
 */
public final class PlayerSpectatorHandler extends PluginHandler implements PacketListener {
	
	static PlayerSpectatorHandler getInstance ( ) {
		return getPluginHandler ( PlayerSpectatorHandler.class );
	}
	
	// stores the last time a spectator was watching
	// a certain player in the arena.
	private final Map < Player, Map < Player, Long > > camera_millis_map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public PlayerSpectatorHandler ( BattleRoyale plugin ) {
		super ( plugin );
		register ( );
		
		/* registering packet listeners */
		// move packet listeners
		PacketChannelHandler.addPacketListener (
				Constants.PACKET_IN_POSITION_NAME , PacketListener.Priority.LOWEST , this );
		PacketChannelHandler.addPacketListener (
				Constants.PACKET_IN_LOOK_NAME , PacketListener.Priority.LOWEST , this );
		// interact packet listeners
		PacketChannelHandler.addPacketListener (
				Constants.PACKET_IN_ARM_ANIMATION_NAME , PacketListener.Priority.LOWEST , this );
	}
	
	@Override
	public void onReceiving ( PacketEvent event ) {
		org.bukkit.entity.Player player    = event.getPlayer ( );
		Player                   br_player = Player.getPlayer ( player );
		Player                   br_target = br_player.getBRSpectatorTarget ( );
		
		if ( br_player.isSpectator ( ) && player.getGameMode ( ) == GameMode.SPECTATOR ) {
			// spectator opening the spectator mode gui
			if ( Constants.PACKET_IN_ARM_ANIMATION_NAME.equals (
					event.getPacket ( ).getClass ( ).getSimpleName ( ) ) ) {
				SchedulerUtil.runTask ( ( ) -> SpectatorGUI.getInstance ( ).open ( player ) );
			}
			// spectator trying to move
			else {
				// the spectators will not be able to freely fly around,
				// but just watch the game from the camera of the target player (if any).
				org.bukkit.entity.Player target = br_target != null ? br_target.getBukkitPlayer ( ) : null;
				
				if ( target != null ) {
					event.setCancelled ( true );
					
					// resetting target
					SchedulerUtil.scheduleSyncDelayedTask (
							( ) -> player.setSpectatorTarget ( target ) );
				}
			}
		}
	}
	
	@Override
	public void onSending ( PacketEvent event ) {
		// nothing to do here
	}
	
	void process ( Player br_player , boolean flag ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			BattleRoyaleArena arena = br_player.getArena ( );
			BattleRoyaleLobby lobby = BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( );
			
			if ( br_player.getParachute ( ).isOpen ( ) ) {
				br_player.getParachute ( ).close ( );
			}
			
			br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
				if ( flag ) {
					if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING ) {
						if ( !toggleCamera ( player ) && br_player.hasTeam ( ) ) {
							// there are no players the player can spectate,
							// but since the player was playing we have no choice
							// but enter the spectator mode
							player.setGameMode ( GameMode.SPECTATOR );
						}
					}
				} else {
					lobby.introduce ( player );
				}
			} );
		} else {
			Bukkit.getScheduler ( ).runTask ( plugin , ( ) -> process ( br_player , flag ) );
		}
	}
	
	// this event handler is responsible for introducing
	// spectators into the battlefield when the arena starts.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onStart ( ArenaStateChangeEvent event ) {
		BattleRoyaleArena arena = event.getArena ( );
		
		if ( event.getState ( ) == EnumArenaState.RUNNING ) {
			for ( Player br_player : arena.getPlayers ( false ) ) {
				if ( br_player.isSpectator ( ) ) {
					br_player.getBukkitPlayerOptional ( ).ifPresent ( this :: toggleCamera );
				}
			}
		}
	}
	
	boolean toggleCamera ( org.bukkit.entity.Player player ) {
		Player          br_player = Player.getPlayer ( player );
		List < Player > cameras   = new ArrayList <> ( );
		
		// looking for valid players in the arena.
		// if the player has a team, and there are teammates living,
		// then will not be able to spectate players from another
		// teams as that would be too OP
		if ( br_player.hasTeam ( ) ) {
			br_player.getTeam ( ).getPlayers ( ).stream ( )
					.filter ( Player :: isOnline )
					.filter ( FunctionUtil.negate ( Player :: isSpectator ) )
					.filter ( FunctionUtil.negate ( Player :: isKnocked ) )
					.forEach ( cameras :: add );
		}
		
		if ( cameras.isEmpty ( ) ) {
			br_player.getArena ( ).getPlayers ( ).stream ( )
					.filter ( Player :: isOnline ).filter ( Player :: hasTeam )
					.filter ( FunctionUtil.negate ( Player :: isSpectator ) )
					.filter ( FunctionUtil.negate ( Player :: isKnocked ) )
					.forEach ( cameras :: add );
		}
		
		// ordering by time
		Map < Player, Long > camera_millis = getCameraMillis ( br_player );
		
		cameras.sort ( ( a , b ) -> {
			Long ma = camera_millis.get ( a );
			Long mb = camera_millis.get ( b );
			
			if ( ma == null ) {
				return -1;
			} else if ( mb == null ) {
				return 1;
			} else {
				if ( ma.equals ( mb ) ) {
					return 0;
				} else {
					return ma < mb ? -1 : 1;
				}
			}
		} );
		
		// then setting camera
		if ( !cameras.isEmpty ( ) ) {
			setSpectatorTarget ( br_player , cameras.get ( 0 ) );
			return true;
		} else {
			return false;
		}
	}
	
	synchronized void setSpectatorTarget ( Player spectator , Player target ) {
		spectator.spectator_target = target;
		
		spectator.getBukkitPlayerOptional ( ).ifPresent ( player -> {
			// making sure is in spectator mode
			player.setGameMode ( GameMode.SPECTATOR );
			
			// then setting
			org.bukkit.entity.Player bukkit = target.getBukkitPlayer ( );
			
			if ( bukkit != null ) {
				player.setSpectatorTarget ( bukkit );
			}
		} );
		
		// updating millis
		getCameraMillis ( spectator ).put ( target , System.currentTimeMillis ( ) );
	}
	
	private Map < Player, Long > getCameraMillis ( Player spectator ) {
		return this.camera_millis_map.computeIfAbsent (
				spectator , k -> new ConcurrentHashMap <> ( ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}