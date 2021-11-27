package es.outlook.adriansrj.battleroyale.command;

import es.outlook.adriansrj.battleroyale.gui.parachute.ParachuteCreatorGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author AdrianSR / 22/11/2021 / 08:41 p. m.
 */
class ParachuteCreatorArgument extends BaseArgument {
	
	ParachuteCreatorArgument ( BattleRoyaleCommandHandler handler ) {
		super ( handler );
	}
	
	@Override
	public String getName ( ) {
		return "parachutecreator";
	}
	
	@Override
	public boolean execute ( CommandSender sender , Command command , String label , String[] subargs ) {
		if ( sender instanceof Player ) {
			ParachuteCreatorGUI.getInstance ( ).open ( ( Player ) sender );
		} else {
			sender.sendMessage ( ChatColor.DARK_RED + "Must be an online player to execute this argument!" );
		}
		
		return true;
	}
}