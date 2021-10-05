package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.battleroyale.player.Team;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Objects;

/**
 * Class that handles the damage the players receive
 * on the battlefield.
 *
 * @author AdrianSR / 15/09/2021 / 09:41 p. m.
 */
public final class DamageListener extends BattleRoyaleArenaListener {
	
	public DamageListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	// event handler responsible for knocking players
	// when the health is too low.
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onKnock ( EntityDamageEvent event ) {
		if ( !( event.getEntity ( ) instanceof org.bukkit.entity.Player ) ) { return; }
		
		org.bukkit.entity.Player player    = ( org.bukkit.entity.Player ) event.getEntity ( );
		Player                   br_player = Player.getPlayer ( player );
		
		if ( knockCheck ( br_player ) && player.getHealth ( ) - event.getFinalDamage ( ) <= 0.0D ) {
			event.setCancelled ( true );
			
			// knocking out
			Player br_knocker = null;
			
			if ( event instanceof EntityDamageByEntityEvent ) {
				Entity uncast_knocker = ( ( EntityDamageByEntityEvent ) event ).getDamager ( );
				
				if ( uncast_knocker instanceof org.bukkit.entity.Player ) {
					br_knocker = Player.getPlayer ( uncast_knocker.getUniqueId ( ) );
				}
			}
			
			if ( br_knocker != null ) {
				br_player.setKnocked ( true , br_knocker );
			} else {
				br_player.setKnocked ( true );
			}
		}
	}
	
	/**
	 * Checks whether the provided player can be knocked out.
	 * <br>
	 * A player can be knocked out if the respawn is not enabled,
	 * and the reviving is enabled in the {@link BattleRoyaleMode}
	 * of the arena the player is in; and if there are at least one
	 * teammate alive.
	 */
	private boolean knockCheck ( Player player ) {
		BattleRoyaleArena arena = player.getArena ( );
		BattleRoyaleMode  mode  = arena != null ? arena.getMode ( ) : null;
		Team              team  = player.getTeam ( );
		
		if ( !player.isKnocked ( ) && team != null && arena != null && arena.getState ( ) == EnumArenaState.RUNNING
				&& !mode.isRespawnEnabled ( ) && mode.isRevivingEnabled ( ) ) {
			return team.getPlayers ( ).stream ( ).anyMatch (
					teammate -> !Objects.equals ( player , teammate ) && teammate.isOnline ( )
							&& !teammate.isKnocked ( ) && !teammate.isSpectator ( ) );
		} else {
			return false;
		}
	}
	
	// event handler responsible for preventing
	// players from damaging teammates.
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onDamageTeammate ( EntityDamageByEntityEvent event ) {
		Entity uncast         = event.getEntity ( );
		Entity uncast_damager = event.getDamager ( );
		
		if ( uncast instanceof org.bukkit.entity.Player && uncast_damager instanceof org.bukkit.entity.Player ) {
			org.bukkit.entity.Player player     = ( ( ( org.bukkit.entity.Player ) uncast ).getPlayer ( ) );
			org.bukkit.entity.Player damager    = ( ( ( org.bukkit.entity.Player ) uncast_damager ).getPlayer ( ) );
			Player                   br_player  = Player.getPlayer ( player );
			Player                   br_damager = Player.getPlayer ( damager );
			
			if ( Objects.equals ( br_player.getTeam ( ) , br_damager.getTeam ( ) ) ) {
				event.setCancelled ( true );
			}
		}
	}
}