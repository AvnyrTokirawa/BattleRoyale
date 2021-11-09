package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.enums.EnumBattleMapSetupTool;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 18/10/2021 / 04:13 p. m.
 */
class SetBorderButton extends SetupToolButtonDefaultTool {
	
	public SetBorderButton ( BattlefieldSetupSession session ) {
		super ( session , ChatColor.GOLD + "Set Border Resize Succession" , UniversalMaterial.STRING.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Click to set the border" ,
				ChatColor.GRAY + "resize succession." );
	}
	
	@Override
	protected EnumBattleMapSetupTool defaultTool ( ) {
		return EnumBattleMapSetupTool.SET_BORDER;
	}
}