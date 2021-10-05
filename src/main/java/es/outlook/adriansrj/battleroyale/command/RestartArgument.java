package es.outlook.adriansrj.battleroyale.command;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Optional;

/**
 * @author AdrianSR / 28/08/2021 / 12:28 p. m.
 */
class RestartArgument extends BaseArgument {
	
	RestartArgument ( BattleRoyaleCommandHandler handler ) {
		super ( handler );
	}
	
	@Override
	public String getName ( ) {
		return "restart";
	}
	
	@Override
	public String getUsage ( ) {
		return super.getUsage ( ) + " [arena]";
	}
	
	@Override
	public boolean execute ( CommandSender sender , Command command , String label , String[] subargs ) {
		if ( subargs.length > 0 ) {
			String arena_name = subargs[ 0 ];
			Optional < BattleRoyaleArena > optional = BattleRoyaleArenaHandler.getInstance ( )
					.getArena ( arena_name );
			
			if ( optional.isPresent ( ) ) {
				BattleRoyaleArena arena = optional.get ( );
				
				switch ( arena.getState ( ) ) {
					case WAITING:
					case RUNNING:
						arena.restart ( );
						
						sender.sendMessage (
								ChatColor.GREEN + "Arena '" + arena_name + "' restarted successfully!" );
						break;
					
					case RESTARTING:
						sender.sendMessage ( ChatColor.RED + "This arena is already being restarted!" );
						break;
					
					case STOPPED:
						sender.sendMessage (
								ChatColor.RED + "The server must be restarted in order to start this arena!" );
						break;
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