package es.outlook.adriansrj.battleroyale.mode;

import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.enums.EnumMode;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaStateChangeEvent;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobbyHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Run mode handler.
 *
 * @author AdrianSR / 06/11/2021 / 02:43 p. m.
 */
public final class RunModeHandler extends PluginHandler {
	
	public static RunModeHandler getInstance ( ) {
		return getPluginHandler ( RunModeHandler.class );
	}
	
	private final EnumMode mode;
	private       String   arena_name;
	private       String   shared_mode_command;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public RunModeHandler ( BattleRoyale plugin ) {
		super ( plugin );
		register ( );
		
		// loading mode
		String   mode_name = EnumMainConfiguration.MODE_TYPE.getAsString ( );
		EnumMode mode      = EnumReflection.getEnumConstant ( EnumMode.class , mode_name.toUpperCase ( ) );
		
		if ( mode != null ) {
			this.mode = mode;
		} else {
			this.mode = EnumMode.MULTIARENA;
			
			// logging error
			ConsoleUtil.sendPluginMessage (
					ChatColor.RED ,
					"Run mode '" + mode_name + "' is unknown. Enabling " +
							"plugin in MultiArena mode..." , plugin );
		}
		
		if ( mode == EnumMode.BUNGEE ) {
			this.arena_name = mode.getArenaName ( );
		} else if ( mode == EnumMode.SHARED ) {
			String command         = mode.getCommand ( );
			String default_command = ( String ) EnumMainConfiguration.MODE_SHARED_COMMAND.getDefaultValue ( );
			
			if ( StringUtil.isBlank ( command ) ) {
				command = default_command;
				
				// logging error
				ConsoleUtil.sendPluginMessage (
						ChatColor.RED ,
						"Shared mode command specified in configuration is invalid. " +
								"The command " + default_command + " will be used instead." , plugin );
			}
			
			if ( Bukkit.getPluginCommand ( command ) != null ) {
				command = default_command;
				
				// logging error
				ConsoleUtil.sendPluginMessage (
						ChatColor.RED ,
						"Shared mode command '" + command + "' is unavailable as " +
								"another plugin is already using it." , plugin );
			}
			
			// then registering
			this.shared_mode_command = command;
		}
	}
	
	public EnumMode getMode ( ) {
		return mode;
	}
	
	// ----- bungee
	
	// this event handler restart the server when the
	// only arena in the server ends.
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onRestart ( ArenaStateChangeEvent event ) {
		if ( mode == EnumMode.BUNGEE && event.getState ( ) == EnumArenaState.RESTARTING
				&& event.getArena ( ).getName ( ).equalsIgnoreCase ( arena_name ) ) {
			Bukkit.getScheduler ( ).runTask (
					BattleRoyale.getInstance ( ) ,
					( ) -> Bukkit.dispatchCommand ( Bukkit.getConsoleSender ( ) , mode.getRestartCommand ( ) ) );
		}
	}
	
	// ----- shared
	
	// this event handler listens for players executing
	// the shared command.
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onExecuteSharedCommand ( PlayerCommandPreprocessEvent event ) {
		if ( mode == EnumMode.SHARED ) {
			String[] args = event.getMessage ( ).split ( " " );
			
			if ( args[ 0 ].replaceFirst ( "/" , "" )
					.equalsIgnoreCase ( shared_mode_command ) ) {
				BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( ).introduce ( event.getPlayer ( ) );
			}
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
