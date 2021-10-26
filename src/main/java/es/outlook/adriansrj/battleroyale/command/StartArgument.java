package es.outlook.adriansrj.battleroyale.command;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author AdrianSR / 28/08/2021 / 12:28 p. m.
 */
class StartArgument extends ArenaArgument {
	
	StartArgument ( BattleRoyaleCommandHandler handler ) {
		super ( handler );
	}
	
	@Override
	public String getName ( ) {
		return "start";
	}
	
	@Override
	public boolean execute ( CommandSender sender , Command command , String label , String[] subargs ) {
		BattleRoyaleArena arena = matchArena ( sender , subargs );
		
		if ( arena != null ) {
			if ( arena.getState ( ) == EnumArenaState.WAITING && arena.isPrepared ( ) ) {
				arena.start ( );
				
				sender.sendMessage ( ChatColor.GREEN + "Arena '" + arena.getName ( ) + "' started successfully!" );
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
								ChatColor.RED + "The world of the arena must be restarted in order to start!" );
						break;
					
					default:
						if ( !arena.isPrepared ( ) ) {
							if ( arena.isPreparing ( ) ) {
								sender.sendMessage ( ChatColor.RED + "This arena is being prepared!" );
							} else {
								arena.prepare ( arena :: start );
							}
						}
						
						break;
				}
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