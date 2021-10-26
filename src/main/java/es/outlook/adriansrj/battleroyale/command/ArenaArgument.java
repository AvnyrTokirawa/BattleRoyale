package es.outlook.adriansrj.battleroyale.command;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author AdrianSR / 26/10/2021 / 07:27 p. m.
 */
abstract class ArenaArgument extends BaseArgument {
	
	ArenaArgument ( BattleRoyaleCommandHandler handler ) {
		super ( handler );
	}
	
	@Override
	public String getUsage ( ) {
		return super.getUsage ( ) + " [arena]";
	}
	
	// ----- utils
	
	protected BattleRoyaleArena matchArena ( CommandSender sender , String[] subargs ) {
		BattleRoyaleArena arena;
		String            arena_name;
		
		if ( subargs.length > 0 && StringUtil.isNotBlank ( subargs[ 0 ] ) ) {
			arena_name = subargs[ 0 ].trim ( );
			arena      = BattleRoyaleArenaHandler.getInstance ( )
					.getArena ( arena_name ).orElse ( null );
		} else if ( sender instanceof org.bukkit.entity.Player ) {
			arena = Player.getPlayer ( ( org.bukkit.entity.Player ) sender ).getArena ( );
			
			if ( arena != null ) {
				arena_name = arena.getName ( );
			} else {
				arena_name = null;
				
				sender.sendMessage ( ChatColor.RED + getUsage ( ) );
			}
		} else {
			sender.sendMessage ( ChatColor.RED + getUsage ( ) );
			return null;
		}
		
		if ( arena == null ) {
			sender.sendMessage (
					ChatColor.RED + "Couldn't find any arena with name '" + arena_name + "'!" );
		}
		
		return arena;
	}
}
