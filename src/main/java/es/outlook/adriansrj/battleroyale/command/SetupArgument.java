package es.outlook.adriansrj.battleroyale.command;

import es.outlook.adriansrj.battleroyale.gui.setup.SetupGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author AdrianSR / 28/08/2021 / 12:28 p. m.
 */
class SetupArgument extends BaseArgument {
	
	SetupArgument ( BattleRoyaleCommandHandler handler ) {
		super ( handler );
	}
	
	@Override
	public String getName ( ) {
		return "setup";
	}
	
	@Override
	public boolean execute ( CommandSender sender , Command command , String label , String[] subargs ) {
		if ( sender instanceof Player ) {
			SetupGUI.getInstance ( ).open ( ( Player ) sender );
		} else {
			sender.sendMessage ( ChatColor.DARK_RED + "Must be an online player to execute this argument!" );
		}
		
		return true;
	}
}