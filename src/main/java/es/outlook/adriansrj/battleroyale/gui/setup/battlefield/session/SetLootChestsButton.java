package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.enums.EnumBattleMapSetupTool;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 13/09/2021 / 12:41 p. m.
 */
class SetLootChestsButton extends SetupToolButtonBase {
	
	public SetLootChestsButton ( ) {
		super ( ChatColor.GOLD + "Set Loot Chests" , UniversalMaterial.CHEST.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Click to set the" ,
				ChatColor.GRAY + "loot chests." );
	}
	
	@Override
	protected EnumBattleMapSetupTool tool ( ) {
		return EnumBattleMapSetupTool.SET_LOOT_CHESTS;
	}
}