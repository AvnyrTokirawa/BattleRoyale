package es.outlook.adriansrj.battleroyale.command;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.core.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Optional;

/**
 * @author AdrianSR / 28/08/2021 / 12:28 p. m.
 */
class StartArgument extends BaseArgument {
	
	StartArgument ( BattleRoyaleCommandHandler handler ) {
		super ( handler );
	}
	
	@Override
	public String getName ( ) {
		return "start";
	}
	
	@Override
	public String getUsage ( ) {
		return super.getUsage ( ) + " [arena]";
	}
	
	@Override
	public boolean execute ( CommandSender sender , Command command , String label , String[] subargs ) {
		if ( subargs.length > 0 && StringUtil.isNotBlank ( subargs[ 0 ] ) ) {
			String arena_name = subargs[ 0 ];
			Optional < BattleRoyaleArena > optional = BattleRoyaleArenaHandler.getInstance ( )
					.getArena ( arena_name );
			
			if ( optional.isPresent ( ) ) {
				BattleRoyaleArena arena = optional.get ( );
				
				if ( arena.getState ( ) == EnumArenaState.WAITING ) {
					arena.start ( );
					
					sender.sendMessage ( ChatColor.GREEN + "Arena '" + arena_name + "' started successfully!" );
				} else {
					switch ( arena.getState ( ) ) {
						case RUNNING:
							sender.sendMessage ( ChatColor.RED + "This arena is already running!" );
							break;
						case RESTARTING:
							sender.sendMessage ( ChatColor.RED + "This arena is being restarted!" );
							break;
						case STOPPED:
							sender.sendMessage (
									ChatColor.RED + "The server must be restarted in order to start this arena!" );
							break;
					}
					
				}
			} else {
				sender.sendMessage ( ChatColor.RED + "Couldn't find any arena with name '" + arena_name + "'!" );
			}
		} else {
			sender.sendMessage ( ChatColor.RED + getUsage ( ) );
		}
		
		return true;
	}
}