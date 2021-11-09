package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.enums.EnumBattleMapSetupTool;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 28/08/2021 / 10:10 p. m.
 */
class SetBoundsButton extends SetupToolButtonDefaultTool {
	
	public SetBoundsButton ( BattlefieldSetupSession session ) {
		super ( session ,ChatColor.GOLD + "Set Bounds" , UniversalMaterial.BEACON.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Click to set the" ,
				ChatColor.GRAY + "bounds/area in which" ,
				ChatColor.GRAY + "the arenas will take" ,
				ChatColor.GRAY + "place." );
	}
	
	@Override
	protected EnumBattleMapSetupTool defaultTool ( ) {
		return EnumBattleMapSetupTool.SET_BOUNDS;
	}
}