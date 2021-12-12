package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.enums.EnumMode;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaStateChangeEvent;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.mode.RunModeHandler;
import es.outlook.adriansrj.battleroyale.util.server.ServerUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Listener responsible for updating the server MOTD.
 *
 * @author AdrianSR / 10/12/2021 / 11:05 a. m.
 */
public final class MotdListener extends BattleRoyaleArenaListener {
	
	public MotdListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	// responsible for changing the motd of a
	// ping response.
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onPing ( ServerListPingEvent event ) {
		if ( RunModeHandler.getInstance ( ).getMode ( ) == EnumMode.BUNGEE
				&& EnumMainConfiguration.MOTD_CUSTOM.getAsBoolean ( ) ) {
			BattleRoyaleArena arena = BattleRoyaleArenaHandler.getInstance ( ).getArena (
					EnumMode.BUNGEE.getArenaName ( ) ).orElse ( null );
			String motd = arena != null ? getMotd ( arena ) : null;
			
			if ( motd != null ) {
				event.setMotd ( motd );
			}
		}
	}
	
	// responsible for changing the motd of the
	// server when the arena changes the state.
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onStateChange ( ArenaStateChangeEvent event ) {
		BattleRoyaleArena arena = event.getArena ( );
		
		if ( RunModeHandler.getInstance ( ).getMode ( ) == EnumMode.BUNGEE
				&& EnumMainConfiguration.MOTD_CUSTOM.getAsBoolean ( ) ) {
			String motd = arena != null ? getMotd ( arena ) : null;
			
			if ( motd != null ) {
				ServerUtil.setMotd ( motd );
			}
		}
	}
	
	private String getMotd ( BattleRoyaleArena arena ) {
		EnumLanguage motd;
		
		switch ( arena.getState ( ) ) {
			case WAITING:
				motd = EnumLanguage.MOTD_WAITING;
				break;
			case RUNNING:
				motd = EnumLanguage.MOTD_RUNNING;
				break;
			case RESTARTING:
				motd = EnumLanguage.MOTD_RESTARTING;
				break;
			case STOPPED:
				motd = EnumLanguage.MOTD_STOPPED;
				break;
			default:
				motd = null;
				break;
		}
		
		if ( motd != null ) {
			return motd.getAsString ( );
		} else {
			return null;
		}
	}
}
