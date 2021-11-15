package es.outlook.adriansrj.battleroyale.arena;

import es.outlook.adriansrj.battleroyale.arena.listener.BattleRoyaleArenaListener;
import es.outlook.adriansrj.battleroyale.battlefield.Battlefield;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumItem;
import es.outlook.adriansrj.battleroyale.enums.EnumMode;
import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaLeaveEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaSetEvent;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobby;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobbyHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.mode.RunModeHandler;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.player.PlayerUtil;
import es.outlook.adriansrj.core.util.sound.UniversalSound;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Class responsible for handling the arenas.
 *
 * @author AdrianSR / 03/09/2021 / 02:09 p. m.
 */
public final class BattleRoyaleArenaHandler extends PluginHandler {
	
	private final Map < UUID, BattleRoyaleArena > arena_map = new ConcurrentHashMap <> ( );
	
	public static BattleRoyaleArenaHandler getInstance ( ) {
		return getPluginHandler ( BattleRoyaleArenaHandler.class );
	}
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BattleRoyaleArenaHandler ( BattleRoyale plugin ) {
		super ( plugin );
		register ( );
		
		// initializing listeners
		for ( Class < ? > listener_class : ClassReflection.getClasses (
				plugin.getFile ( ) , BattleRoyaleArenaListener.class.getPackage ( ).getName ( ) ) ) {
			if ( !BattleRoyaleArenaListener.class.isAssignableFrom ( listener_class )
					|| Modifier.isAbstract ( listener_class.getModifiers ( ) ) ) { continue; }
			
			try {
				listener_class.getConstructor ( BattleRoyale.class ).newInstance ( plugin );
			} catch ( InstantiationException | IllegalAccessException
					| InvocationTargetException | NoSuchMethodException ex ) {
				ex.printStackTrace ( );
			}
		}
	}
	
	public Optional < BattleRoyaleArena > getArena ( UUID id ) {
		return Optional.ofNullable ( arena_map.get ( id ) );
	}
	
	public Optional < BattleRoyaleArena > getArena ( String name ) {
		return arena_map.values ( ).stream ( ).filter (
				arena -> name.equalsIgnoreCase ( arena.getName ( ) ) ).findAny ( );
	}
	
	public Set < BattleRoyaleArena > getArenas ( ) {
		return Collections.unmodifiableSet ( new HashSet <> ( arena_map.values ( ) ) );
	}
	
	// -------------
	public void createArena ( String name , BattleRoyaleArenaConfiguration configuration ,
			final Consumer < BattleRoyaleArena > callback ) throws IllegalStateException {
		Validate.notNull ( configuration , "configuration cannot be null" );
		Validate.isValid ( configuration , "configuration cannot be invalid" );
		
		// make sure not exists
		if ( arena_map.values ( ).stream ( )
				.anyMatch ( arena -> Objects.equals ( name , arena.getName ( ) ) ) ) {
			throw new IllegalArgumentException ( "another arena with the same name already exists" );
		}
		
		callback.accept ( register ( new BattleRoyaleArena ( name , configuration ) ) );
	}
	// -------------
	
	// -------------
	public void createArena ( String name , Battlefield battlefield , BattleRoyaleMode mode ,
			final Consumer < BattleRoyaleArena > callback ) throws IllegalStateException {
		Validate.notNull ( battlefield , "battlefield cannot be null" );
		Validate.isValid ( Validate.notNull ( mode , "mode cannot be null" ) , "mode cannot be invalid" );
		
		BattleRoyaleArenaConfiguration configuration = new BattleRoyaleArenaConfiguration ( );
		
		configuration.setBattlefield ( battlefield );
		configuration.setMode ( mode );
		
		createArena ( name , configuration , callback );
	}
	// -------------
	
	private BattleRoyaleArena register ( BattleRoyaleArena arena ) {
		arena_map.put ( arena.getUniqueId ( ) , arena );
		return arena;
	}
	
	public void setArena ( Player player , BattleRoyaleArena arena ) {
		player.setArena ( arena );
	}
	
	@SuppressWarnings ( "deprecation" )
	public void joinArena ( Player br_player , BattleRoyaleArena arena ) {
		br_player.setArena ( arena );
		br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
			BattleRoyaleLobby lobby = BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( );
			
			if ( Objects.equals ( player.getWorld ( ) , lobby.getWorld ( ) )
					&& arena.getState ( ) == EnumArenaState.WAITING ) {
				// preparing inventory
				player.getInventory ( ).clear ( );
				player.getInventory ( ).setArmorContents ( null );
				
				if ( RunModeHandler.getInstance ( ).getMode ( ) != EnumMode.BUNGEE ) {
					EnumItem.ARENA_SELECTOR.give ( player );
				}
				
				if ( arena.getMode ( ).isTeamSelectionEnabled ( ) ) {
					EnumItem.TEAM_SELECTOR.give ( player );
				}
				
				EnumItem.SETTINGS.give ( player );
				EnumItem.SHOP.give ( player );
				EnumItem.LEAVE_ARENA.give ( player );
				
				player.updateInventory ( );
				
				// sending back to lobby spawn
				lobby.sendToSpawn ( player );
				
				// join effect
				player.addPotionEffect (
						new PotionEffect ( PotionEffectType.SLOW , 20 , 10 ) , true );
				player.playSound ( player.getLocation ( ) ,
								   UniversalSound.ENDERMAN_TELEPORT.asBukkit ( ) , 2.0F , 2.0F );
				
				Bukkit.getScheduler ( ).scheduleSyncDelayedTask ( plugin , ( ) -> player.addPotionEffect (
						new PotionEffect ( PotionEffectType.BLINDNESS , 20 , 0 ) , true ) , 3 );
				
				Bukkit.getScheduler ( ).scheduleSyncDelayedTask ( plugin , ( ) -> {
					player.removePotionEffect ( PotionEffectType.SLOW );
					player.removePotionEffect ( PotionEffectType.BLINDNESS );
				} , 6 );
			}
		} );
	}
	
	public void joinArena ( org.bukkit.entity.Player player , BattleRoyaleArena arena ) {
		joinArena ( Player.getPlayer ( player ) , arena );
	}
	
	public void leaveArena ( Player br_player ) {
		if ( br_player.leaveArena ( ) ) {
			br_player.getBukkitPlayerOptional ( ).ifPresent (
					player -> BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( ).introduce ( player ) );
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
	
	// ---------------------------------------------------------------
	
	// these event handlers are responsible for hiding/showing the players
	// in a certain arena to the player that joins/leaves.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onJoinArena ( PlayerArenaSetEvent event ) {
		// players in different arenas cannot see each other.
		Player br_player = event.getPlayer ( );
		
		br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
			// showing/hiding
			for ( org.bukkit.entity.Player other : Bukkit.getOnlinePlayers ( ) ) {
				Player br_other = Player.getPlayer ( other );
				
				if ( Objects.equals ( br_player , br_other ) ) {
					continue;
				}
				
				if ( Objects.equals ( event.getArena ( ) , br_other.getArena ( ) ) ) {
					PlayerUtil.showPlayer ( player , other , BattleRoyale.getInstance ( ) );
					PlayerUtil.showPlayer ( other , player , BattleRoyale.getInstance ( ) );
				} else {
					PlayerUtil.hidePlayer ( player , other , BattleRoyale.getInstance ( ) );
					PlayerUtil.hidePlayer ( other , player , BattleRoyale.getInstance ( ) );
				}
			}
		} );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onLeaveArena ( PlayerArenaLeaveEvent event ) {
		hideNotInArena ( event.getPlayer ( ) );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onJoin ( PlayerJoinEvent event ) {
		hideNotInArena ( Player.getPlayer ( event.getPlayer ( ) ) );
	}
	
	private void hideNotInArena ( Player br_player ) {
		// players that are not in an arena will not be able
		// to see players that are in an arena
		br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
			for ( org.bukkit.entity.Player other : Bukkit.getOnlinePlayers ( ) ) {
				Player br_other = Player.getPlayer ( other );
				
				if ( Objects.equals ( br_player , br_other ) ) {
					continue;
				}
				
				if ( br_other.isInArena ( ) ) {
					PlayerUtil.hidePlayer ( player , other , BattleRoyale.getInstance ( ) );
					PlayerUtil.hidePlayer ( other , player , BattleRoyale.getInstance ( ) );
				} else {
					PlayerUtil.showPlayer ( player , other , BattleRoyale.getInstance ( ) );
					PlayerUtil.showPlayer ( other , player , BattleRoyale.getInstance ( ) );
				}
			}
		} );
	}
	
	// ---------------------------------------------------------------
	
	// this event handler is responsible for removing a player
	// from the arena when leaving the server.
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onQuit ( PlayerQuitEvent event ) {
		// the player that disconnects is automatically
		// leaving the arena.
		org.bukkit.entity.Player player    = event.getPlayer ( );
		Player                   br_player = Player.getPlayer ( player );
		
		br_player.leaveArena ( );
	}
}