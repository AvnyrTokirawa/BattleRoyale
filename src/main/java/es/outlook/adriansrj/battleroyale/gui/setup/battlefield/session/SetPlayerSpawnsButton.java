package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.enums.EnumBattleMapSetupTool;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 04/09/2021 / 11:44 p. m.
 */
class SetPlayerSpawnsButton extends SetupToolButtonDefaultTool {
	
	public SetPlayerSpawnsButton ( BattlefieldSetupSession session ) {
		super ( session , ChatColor.GOLD + "Set Player Spawns" , UniversalMaterial.WHITE_BED.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Click to set the" ,
				ChatColor.GRAY + "player spawns." );
	}
	
	@Override
	protected EnumBattleMapSetupTool defaultTool ( ) {
		return EnumBattleMapSetupTool.SET_PLAYER_SPAWNS;
	}
}