package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.enums.EnumBattleMapSetupTool;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 28/08/2021 / 10:10 p. m.
 */
class SetNameButton extends SetupToolButtonDefaultTool {
	
	public SetNameButton ( BattlefieldSetupSession session ) {
		super ( session , ChatColor.GOLD + "Set Name" , UniversalMaterial.NAME_TAG.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Click to set the name" ,
				ChatColor.GRAY + "of this battlefield." );
	}
	
	@Override
	protected EnumBattleMapSetupTool defaultTool ( ) {
		return EnumBattleMapSetupTool.SET_NAME;
	}
}