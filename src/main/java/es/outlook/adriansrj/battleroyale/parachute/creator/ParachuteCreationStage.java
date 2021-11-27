package es.outlook.adriansrj.battleroyale.parachute.creator;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobbyHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.schedule.ScheduledExecutorPool;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.core.util.file.filter.YamlFileFilter;
import es.outlook.adriansrj.core.util.player.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * @author AdrianSR / 22/11/2021 / 08:54 p. m.
 */
public class ParachuteCreationStage {
	
	static final           Vector          STAGE_LOCATION           = new Vector ( 1001 , 30 , 1006 );
	static final           Vector          STAGE_DISPLAYER_LOCATION = new Vector ( 1002 , 30 , 1000 );
	protected static final ExecutorService EXECUTOR_SERVICE;
	
	static {
		EXECUTOR_SERVICE = ScheduledExecutorPool.getInstance ( ).getNewWorkStealingPool ( );
	}
	
	private final Player                  player;
	private final File                    file;
	private final World                   world;
	private       boolean                 started;
	private       boolean                 stopped;
	private       ParachuteShapeDisplayer displayer;
	
	ParachuteCreationStage ( Player player , File file , World world ) {
		Validate.notNull ( file , "file cannot be null" );
		Validate.isTrue ( new YamlFileFilter ( ).accept ( file ) , "file must be a valid .yml file" );
		Validate.notNull ( world , "world cannot be null" );
		
		this.player = player;
		this.file   = file;
		this.world  = world;
		
		// file watcher
		EXECUTOR_SERVICE.execute ( ( ) -> {
			boolean poll = BattleRoyale.getInstance ( ).isEnabled ( ) && this.isActive ( );
			
			try {
				WatchService watch_service  = FileSystems.getDefault ( ).newWatchService ( );
				Path         directory_path = file.getParentFile ( ).toPath ( );
				
				directory_path.register ( watch_service , StandardWatchEventKinds.ENTRY_MODIFY ,
										  StandardWatchEventKinds.ENTRY_DELETE ,
										  StandardWatchEventKinds.ENTRY_CREATE );
				
				while ( poll ) {
					WatchKey key    = watch_service.take ( );
					Path     path   = ( Path ) key.watchable ( );
					boolean  update = false;
					
					for ( WatchEvent < ? > event : key.pollEvents ( ) ) {
						File event_file = path.resolve ( ( Path ) event.context ( ) ).toFile ( );
						
						if ( Objects.equals ( event_file , file ) ) {
							update = true;
						}
					}
					
					poll = key.reset ( );
					
					// updating
					if ( update ) {
						// we must call the update method later as the
						// watcher fires the events even before the
						// file stops being used by another process.
						Bukkit.getScheduler ( ).scheduleSyncDelayedTask (
								BattleRoyale.getInstance ( ) , this :: update , 10L );
					}
				}
			} catch ( IOException e ) {
				e.printStackTrace ( );
			} catch ( InterruptedException ex ) {
				poll = false;
			}
		} );
	}
	
	public Player getPlayer ( ) {
		return player;
	}
	
	public File getFile ( ) {
		return file;
	}
	
	public World getWorld ( ) {
		return world;
	}
	
	public boolean isStarted ( ) {
		return started;
	}
	
	public boolean isActive ( ) {
		return started && !stopped;
	}
	
	void start ( ) {
		started = true;
		
		// making file
		if ( !file.exists ( ) ) {
			try {
				file.getParentFile ( ).mkdirs ( );
				file.createNewFile ( );
			} catch ( IOException ex ) {
				ex.printStackTrace ( );
			}
		}
		
		// introducing player
		org.bukkit.entity.Player player = this.player.getBukkitPlayer ( );
		
		if ( player != null ) {
			player.getInventory ( ).clear ( );
			player.updateInventory ( );
			player.setGameMode ( GameMode.CREATIVE );
			player.setAllowFlight ( true );
			player.setFlying ( true );
			
			player.teleport ( STAGE_LOCATION.toLocation ( world , 180.0F , 0.0F ) );
			
			// hiding players
			for ( org.bukkit.entity.Player other : world.getPlayers ( ) ) {
				PlayerUtil.hidePlayer ( other , player , BattleRoyale.getInstance ( ) );
				PlayerUtil.hidePlayer ( player , other , BattleRoyale.getInstance ( ) );
			}
		}
		
		// parachute will be loaded
		// and displayed to the creator.
		update ( );
	}
	
	public void update ( ) {
		// destroying current
		if ( displayer != null ) {
			displayer.destroy ( );
			displayer = null;
		}
		
		// loading configuration
		if ( file.exists ( ) ) {
			YamlConfiguration yaml          = YamlConfiguration.loadConfiguration ( file );
			Parachute         configuration = Parachute.of ( yaml );
			
			// then display
			if ( configuration != null ) {
				displayer = ParachuteShapeDisplayer.of ( player , configuration );
				
				if ( displayer != null ) {
					if ( Bukkit.isPrimaryThread ( ) ) {
						displayer.show ( );
					} else {
						Bukkit.getScheduler ( ).runTask (
								BattleRoyale.getInstance ( ) , displayer :: show );
					}
				}
			}
		}
	}
	
	void stop ( ) {
		stopped = true;
		
		// destroying displayer
		if ( displayer != null ) {
			displayer.destroy ( );
			displayer = null;
		}
		
		// showing players
		org.bukkit.entity.Player player = this.player.getBukkitPlayer ( );
		
		if ( player != null ) {
			for ( org.bukkit.entity.Player other : world.getPlayers ( ) ) {
				PlayerUtil.hidePlayer ( other , player , BattleRoyale.getInstance ( ) );
				PlayerUtil.hidePlayer ( player , other , BattleRoyale.getInstance ( ) );
			}
			
			// sending player back to lobby
			BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( ).introduce ( player );
		}
	}
}