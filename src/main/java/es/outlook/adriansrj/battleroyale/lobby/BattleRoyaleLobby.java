package es.outlook.adriansrj.battleroyale.lobby;

import es.outlook.adriansrj.battleroyale.compass.CompassBar;
import es.outlook.adriansrj.battleroyale.enums.EnumLobbyConfiguration;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.util.entity.EntityUtil;
import es.outlook.adriansrj.core.util.world.GameRuleType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

/**
 * Battle royale matchmaking lobby.
 *
 * @author AdrianSR / 03/09/2021 / 09:32 p. m.
 */
public class BattleRoyaleLobby implements Listener {
	
	protected final World world;
	
	public BattleRoyaleLobby ( World world ) {
		this.world = Objects.requireNonNull ( world , "world cannot be null" );
		
		if ( EnumLobbyConfiguration.WORLD_DISABLE_FIRE_TICKS.getAsBoolean ( ) ) {
			GameRuleType.FIRE_TICK.apply ( world , false );
		}
		
		if ( EnumLobbyConfiguration.WORLD_DISABLE_MOBS.getAsBoolean ( ) ) {
			GameRuleType.MOB_SPAWNING.apply ( world , false );
		}
		
		if ( EnumLobbyConfiguration.WORLD_DISABLE_HUNGER.getAsBoolean ( ) ) {
			GameRuleType.DISABLE_HUNGER.apply ( world , true );
		}
	}
	
	public World getWorld ( ) {
		return world;
	}
	
	public boolean isCustomSpawnEnabled ( ) {
		return EnumLobbyConfiguration.SPAWN_ENABLE.getAsBoolean ( );
	}
	
	public Location getCustomSpawn ( ) {
		if ( isCustomSpawnEnabled ( ) ) {
			double x     = EnumLobbyConfiguration.SPAWN_X.getAsDouble ( );
			double y     = EnumLobbyConfiguration.SPAWN_Y.getAsDouble ( );
			double z     = EnumLobbyConfiguration.SPAWN_Z.getAsDouble ( );
			float  yaw   = ( float ) EnumLobbyConfiguration.SPAWN_YAW.getAsDouble ( );
			float  pitch = ( float ) EnumLobbyConfiguration.SPAWN_PITCH.getAsDouble ( );
			
			return new Location ( world , x , y , z , yaw , pitch );
		} else {
			return null;
		}
	}
	
	public void introduce ( org.bukkit.entity.Player player ) {
		introduce0 ( Player.getPlayer ( player ) );
		
		player.setGameMode ( GameMode.ADVENTURE );
		player.setFlying ( false );
		player.setAllowFlight ( false );
		player.setTotalExperience ( 0 );
		player.setLevel ( 0 );
		player.setExp ( 0F );
		player.setFoodLevel ( 20 );
		player.setSaturation ( 20.0F );
		EntityUtil.setMaxHealth ( player , 20.0D );
		player.setHealth ( 20.0D );
		player.getActivePotionEffects ( ).clear ( );
		player.getInventory ( ).clear ( );
		player.getInventory ( ).setArmorContents ( null );
		player.updateInventory ( );
		
		// sending to spawn
		sendToSpawn ( player );
	}
	
	public void introduce ( Player br_player ) {
		org.bukkit.entity.Player player = br_player.getBukkitPlayer ( );
		
		if ( player != null ) {
			introduce ( player );
		} else {
			introduce0 ( br_player );
		}
	}
	
	protected void introduce0 ( Player player ) {
		player.leaveArena ( );
		
		// hiding compass
		CompassBar compass = player.getCompass ( );
		
		if ( compass != null ) {
			compass.setVisible ( false );
		}
	}
	
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onJoin ( PlayerJoinEvent event ) {
		if ( EnumLobbyConfiguration.SPAWN_JOIN.getAsBoolean ( ) && isCustomSpawnEnabled ( ) ) {
			introduce ( event.getPlayer ( ) );
		}
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onDamage ( EntityDamageEvent event ) {
		if ( event.getEntity ( ) instanceof org.bukkit.entity.Player
				&& Objects.equals ( getWorld ( ) , event.getEntity ( ).getWorld ( ) ) ) {
			boolean cancel = EnumLobbyConfiguration.WORLD_DISABLE_DAMAGE.getAsBoolean ( );
			
			if ( isCustomSpawnEnabled ( )
					&& EnumLobbyConfiguration.SPAWN_VOID.getAsBoolean ( )
					&& event.getCause ( ) == EntityDamageEvent.DamageCause.VOID ) {
				cancel = true;
				
				// sending back to spawn
				introduce ( ( org.bukkit.entity.Player ) event.getEntity ( ) );
			}
			
			if ( cancel ) {
				event.setCancelled ( true );
			}
		}
	}
	
	public void sendToSpawn ( org.bukkit.entity.Player player ) {
		Location spawn = isCustomSpawnEnabled ( ) ? getCustomSpawn ( ) : null;
		
		if ( spawn == null ) {
			spawn = getWorld ( ).getSpawnLocation ( );
		}
		
		// sending
		player.teleport ( spawn );
	}
	
	public void sendToSpawn ( Player player ) {
		player.getBukkitPlayerOptional ( ).ifPresent ( this :: sendToSpawn );
	}
	
	protected void register ( Plugin plugin ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , plugin );
	}
	
	protected void unregister ( ) {
		HandlerList.unregisterAll ( this );
	}
}