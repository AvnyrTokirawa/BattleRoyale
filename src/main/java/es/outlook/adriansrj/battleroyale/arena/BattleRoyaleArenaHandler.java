package es.outlook.adriansrj.battleroyale.arena;

import es.outlook.adriansrj.battleroyale.arena.listener.BattleRoyaleArenaListener;
import es.outlook.adriansrj.battleroyale.battlefield.Battlefield;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.enums.EnumWorldGenerator;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaStateChangeEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaLeaveEvent;
import es.outlook.adriansrj.battleroyale.event.player.PlayerArenaSetEvent;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobby;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobbyHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.WorldUtil;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import es.outlook.adriansrj.battleroyale.world.arena.ArenaWorldGenerator;
import es.outlook.adriansrj.battleroyale.world.data.WorldData;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.player.PlayerUtil;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import es.outlook.adriansrj.core.util.server.Version;
import es.outlook.adriansrj.core.util.sound.UniversalSound;
import es.outlook.adriansrj.core.util.world.GameRuleDisableDaylightCycle;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
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
	
	public BattleRoyaleArena createArena ( String name , World world , Battlefield battlefield ,
			BattleRoyaleMode mode )
			throws IllegalStateException {
		Validate.notNull ( world , "world cannot be null" );
		Validate.notNull ( battlefield , "battlefield cannot be null" );
		Validate.notNull ( mode , "mode cannot be null" );
		
		if ( arena_map.values ( ).stream ( )
				.anyMatch ( arena -> Objects.equals ( name , arena.getName ( ) ) ) ) {
			throw new IllegalArgumentException ( "another arena with the same name already exists" );
		}
		
		return new BattleRoyaleArena ( name , world , battlefield , mode );
	}
	
	public void createArena ( String name , File world_folder , Battlefield battlefield , BattleRoyaleMode mode ,
			final Consumer < BattleRoyaleArena > callback ) throws IllegalStateException {
		Validate.notNull ( world_folder , "world folder cannot be null" );
		Validate.isTrue ( StringUtil.isBlank ( FilenameUtil.getExtension ( world_folder.getName ( ) ) ) ,
						  "world folder must be a valid directory" );
		Validate.notNull ( battlefield , "battlefield cannot be null" );
		
		if ( arena_map.values ( ).stream ( )
				.anyMatch ( arena -> Objects.equals ( name , arena.getName ( ) ) ) ) {
			throw new IllegalArgumentException ( "another arena with the same name already exists" );
		}
		
		// in case the world doesn't exist we will
		// generate an empty world.
		if ( !world_folder.exists ( ) || !WorldUtil.worldFolderCheck ( world_folder ) ) {
			generateEmptyWorld ( world_folder ).save ( );
		}
		
		if ( Bukkit.isPrimaryThread ( ) ) {
			callback.accept ( register (
					new BattleRoyaleArena ( name , loadWorld ( world_folder ) , battlefield , mode ) ) );
		} else {
			Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> callback.accept ( register (
					new BattleRoyaleArena ( name , loadWorld ( world_folder ) , battlefield , mode ) ) ) );
		}
	}
	
	public void createArena ( String name , String world_name , Battlefield battlefield , BattleRoyaleMode mode ,
			Consumer < BattleRoyaleArena > callback ) throws IllegalStateException {
		createArena ( name , new File ( EnumDirectory.BATTLEFIELD_TEMP_DIRECTORY.getDirectory ( ) ,
										world_name ) , battlefield , mode , callback );
	}
	
	public void createArena ( String world_name , Battlefield battlefield , BattleRoyaleMode mode ,
			Consumer < BattleRoyaleArena > callback ) throws IllegalStateException {
		createArena ( world_name , world_name , battlefield , mode , callback );
	}
	
	private BattleRoyaleArena register ( BattleRoyaleArena arena ) {
		arena_map.put ( arena.getUniqueId ( ) , arena );
		return arena;
	}
	
	public void setArena ( Player player , BattleRoyaleArena arena ) {
		player.setArena ( arena );
	}
	
	public void joinArena ( Player br_player , BattleRoyaleArena arena ) {
		br_player.setArena ( arena );
		br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
			BattleRoyaleLobby lobby = BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( );
			
			if ( Objects.equals ( player.getWorld ( ) , lobby.getWorld ( ) )
					&& arena.getState ( ) == EnumArenaState.WAITING ) {
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
		br_player.leaveArena ( );
		br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
			BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( ).introduce ( player );
		} );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
	
	// ---------------------------------------------------------------
	
	// this event handler is responsible for restarting a certain
	// world when all the arenas that take places in it are stopped.
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onStop ( ArenaStateChangeEvent event ) {
		if ( event.getState ( ) == EnumArenaState.STOPPED ) {
			World             world = event.getArena ( ).getWorld ( );
			BattleRoyaleLobby lobby = BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( );
			
			if ( arena_map.values ( ).stream ( )
					.filter ( arena -> !Objects.equals ( arena , event.getArena ( ) ) )
					.filter ( arena -> arena.getState ( ) != EnumArenaState.STOPPED )
					.anyMatch ( arena -> Objects.equals ( arena.getWorld ( ) , world ) ) ) {
				return;
			}
			
			// sending players back to lobby, so bukkit will
			// actually be able to unload the world.
			world.getPlayers ( ).forEach ( lobby :: introduce );
			
			// then restarting
			if ( Bukkit.isPrimaryThread ( ) ) {
				restartWorld ( world );
			} else {
				SchedulerUtil.runTask ( ( ) -> restartWorld ( world ) );
			}
		}
	}
	
	private void restartWorld ( World world ) {
		Validate.isTrue ( Bukkit.isPrimaryThread ( ) , "must run on server thread" );
		
		File folder = world.getWorldFolder ( );
		
		// we will not save changes
		Bukkit.unloadWorld ( world , false );
		// clearing regions used by the matches
		BattleRoyaleArenaRegion.RegionFileAssigner.clear ( folder );
		// we're now clear to delete it
		try {
			FileDeleteStrategy.FORCE.delete ( folder );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		}
		
		// then we can generate and load the world again; and then
		// reassign it (update reference to it from arenas).
		generateEmptyWorld ( folder ).save ( );
		
		world = loadWorld ( folder );
		
		// reassigning and preparing arenas.
		for ( BattleRoyaleArena arena : arena_map.values ( ) ) {
			if ( Objects.equals ( arena.getWorld ( ).getWorldFolder ( ) , folder ) ) {
				arena.world = world; // reassign
				
				// preparing
				arena.region.reassignRegion ( );
				arena.prepare0 ( ( ) -> arena.setState ( EnumArenaState.WAITING ) /* ready to start */ );
			}
		}
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
	
	// ---------------------------------------------------------------
	
	private ArenaWorldGenerator generateEmptyWorld ( File world_folder ) {
		ArenaWorldGenerator generator      = ArenaWorldGenerator.createGenerator ( world_folder );
		WorldData           generator_data = generator.getWorldData ( );
		
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 ) ) {
			generator_data.setGeneratorOptions ( "minecraft:air;minecraft:air" );
		} else {
			generator_data.setGeneratorOptions ( "2;0;1" );
		}
		
		generator_data.setGeneratorType ( EnumWorldGenerator.FLAT );
		generator_data.setGenerateStructures ( false );
		generator_data.setInitialized ( true );
		generator_data.setName ( world_folder.getName ( ) );
		generator_data.setSpawnX ( 0 );
		generator_data.setSpawnY ( 0 );
		generator_data.setSpawnZ ( 0 );
		
		// bukkit world loader must have something to load!
		// if the region folder of the world is empty, bukkit
		// will not load the world, so we have to give bukkit
		// something to load.
		generator.setBlockAtFromLegacyId ( 0 , 0 , 0 , 1 );
		return generator;
	}
	
	private World loadWorld ( File world_folder ) {
		World world = Bukkit.getWorld ( world_folder.getName ( ) );
		
		if ( world == null && ( world = Bukkit.getWorld ( world_folder.getAbsolutePath ( ) ) ) == null ) {
			world = WorldUtil.loadWorldEmpty ( world_folder );
		}
		
		new GameRuleDisableDaylightCycle ( ).apply ( world );
		
		return world;
	}
}