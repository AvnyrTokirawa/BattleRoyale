package es.outlook.adriansrj.battleroyale.command;

import es.outlook.adriansrj.core.command.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author AdrianSR / 28/08/2021 / 12:28 p. m.
 */
abstract class BaseArgument implements CommandArgument {
	
	protected final BattleRoyaleCommandHandler handler;
	
	BaseArgument ( BattleRoyaleCommandHandler handler ) {
		this.handler = handler;
	}
	
	@Override
	public String getUsage ( ) {
		return "/" + handler.getCommand ( ).getName ( ) + " " + getName ( );
	}
	
	@Override
	public List < String > tab ( CommandSender sender , Command command , String alias , String[] subargs ) {
		// adapted by default.
		return null;
	}
}