package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaEndEvent;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaStateChangeEvent;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.entity.EntityUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Optional;

/**
 * Class that handles the end of the arenas.
 *
 * @author AdrianSR / 23/10/2021 / 03:36 p. m.
 */
public final class EndListener extends BattleRoyaleArenaListener {
	
	public EndListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	// this handler is responsible for restarting arenas that ends,
	// and for freezing players once the game is over.
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onEnd ( ArenaEndEvent event ) {
		BattleRoyaleArena arena = event.getArena ( );
		
		// restarting
		arena.restart ( false );
		
		// freezing players
		arena.getPlayers ( ).stream ( )
				.map ( Player :: getBukkitPlayerOptional )
				.filter ( Optional :: isPresent ).map ( Optional :: get )
				.forEach ( player -> {
					EntityUtil.addPotionEffectForcing (
							player , PotionEffectType.SLOW , Duration.INFINITE , 100 );
					EntityUtil.addPotionEffectForcing (
							player , PotionEffectType.JUMP , Duration.INFINITE , 1000 );
				} );
	}
	
	// this handler is responsible for stopping players
	// from moving once the game is over.
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onMove ( PlayerMoveEvent event ) {
		Player br_player = Player.getPlayer ( event.getPlayer ( ) );
		
		if ( br_player.isInArena ( ) && br_player.getArena ( ).isOver ( )
				&& event.getTo ( ) != null
				&& !Objects.equals ( event.getTo ( ).toVector ( ) , event.getFrom ( ).toVector ( ) ) ) {
			event.setTo ( event.getFrom ( ) );
		}
	}
	
	// this handler is responsible for stopping players
	// from interacting once the game is over.
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onMove ( PlayerInteractEvent event ) {
		Player br_player = Player.getPlayer ( event.getPlayer ( ) );
		
		if ( br_player.isInArena ( ) && br_player.getArena ( ).isOver ( ) ) {
			event.setCancelled ( true );
		}
	}
	
	// this handler is responsible for unfreezing players
	// once the arena changes of state after ending.
	@EventHandler ( priority = EventPriority.MONITOR )
	public void afterEnd ( ArenaStateChangeEvent event ) {
		BattleRoyaleArena arena = event.getArena ( );
		
		if ( event.getPreviousState ( ) == EnumArenaState.RUNNING ) {
			// unfreezing players
			arena.getPlayers ( ).stream ( )
					.map ( Player :: getBukkitPlayerOptional )
					.filter ( Optional :: isPresent ).map ( Optional :: get )
					.forEach ( player -> {
						player.removePotionEffect ( PotionEffectType.SLOW );
						player.removePotionEffect ( PotionEffectType.JUMP );
					} );
		}
	}
}
