package es.outlook.adriansrj.battleroyale.command;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author AdrianSR / 28/08/2021 / 12:28 p. m.
 */
class RestartArgument extends ArenaArgument {
	
	RestartArgument ( BattleRoyaleCommandHandler handler ) {
		super ( handler );
	}
	
	@Override
	public String getName ( ) {
		return "restart";
	}
	
	@Override
	public boolean execute ( CommandSender sender , Command command , String label , String[] subargs ) {
		BattleRoyaleArena arena = matchArena ( sender , subargs );
		
		if ( arena != null ) {
			switch ( arena.getState ( ) ) {
				case WAITING:
				case RUNNING:
					arena.restart ( );
					
					sender.sendMessage (
							ChatColor.GREEN + "Arena '" + arena.getName ( ) + "' restarted successfully!" );
					break;
				
				case RESTARTING:
					sender.sendMessage ( ChatColor.RED + "This arena is already being restarted!" );
					break;
				
				case STOPPED:
					sender.sendMessage (
							ChatColor.RED + "The server must be restarted in order to start this arena!" );
					break;
			}
		}
		
		return true;
	}
	
	@Override
	public List < String > tab ( CommandSender sender , Command command , String alias , String[] subargs ) {
		if ( subargs.length == 1 ) {
			return BattleRoyaleArenaHandler.getInstance ( ).getArenas ( )
					.stream ( ).map ( BattleRoyaleArena :: getName ).collect ( Collectors.toList ( ) );
		} else {
			return super.tab ( sender , command , alias , subargs );
		}
	}
}