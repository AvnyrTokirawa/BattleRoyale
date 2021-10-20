package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.event.player.PlayerDeathEvent;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * Class that enables the customization of the death messages.
 *
 * @author AdrianSR / 17/10/2021 / 11:31 a. m.
 */
public final class DeathMessageListener extends BattleRoyaleArenaListener {
	
	public DeathMessageListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onDeath ( PlayerDeathEvent event ) {
		Player                 player   = event.getPlayer ( );
		Player                 killer   = event.getKiller ( );
		PlayerDeathEvent.Cause cause    = event.getCause ( );
		EnumLanguage           language = EnumLanguage.DEATH_PHRASE_GENERAL;
		
		switch ( cause ) {
			case VOID: {
				if ( killer != null ) {
					language = EnumLanguage.KILLED_PHRASE_VOID;
				} else {
					language = EnumLanguage.DEATH_PHRASE_VOID;
				}
				break;
			}
			
			case FALL: {
				if ( killer != null ) {
					language = EnumLanguage.KILLED_PHRASE_PUSH;
				} else {
					language = EnumLanguage.DEATH_PHRASE_FALL;
				}
				break;
			}
			
			case PROJECTILE: {
				if ( killer != null ) {
					language = EnumLanguage.KILLED_PHRASE_SHOT;
				}
				break;
			}
			
			case ENTITY_EXPLOSION:
			case BLOCK_EXPLOSION: {
				language = EnumLanguage.DEATH_PHRASE_EXPLOSION;
				break;
			}
			
			case BLEEDING_OUT: {
				if ( killer != null ) {
					language = EnumLanguage.KILLED_PHRASE_BLEEDING_OUT;
				} else {
					language = EnumLanguage.DEATH_PHRASE_BLEEDING_OUT;
				}
				break;
			}
			
			case OUT_OF_BOUNDS: {
				if ( killer == null ) {
					language = EnumLanguage.DEATH_PHRASE_OUT_OF_BOUNDS;
				}
				break;
			}
			
			default: {
				if ( killer != null ) {
					language = EnumLanguage.KILLED_PHRASE_GENERAL;
				}
				break;
			}
		}
		
		if ( killer != null ) {
			event.setDeathMessage ( String.format (
					language.getAsString ( ) , killer.getName ( ) , player.getName ( ) ) );
		} else {
			event.setDeathMessage ( String.format (
					language.getAsString ( ) , player.getName ( ) ) );
		}
	}
}
