package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.enums.EnumBattleMapSetupTool;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 07/09/2021 / 08:44 p. m.
 */
class SetBusSpawnsButton extends SetupToolButtonBase {
	
	public SetBusSpawnsButton ( ) {
		super ( ChatColor.GOLD + "Set Bus Spawns" , UniversalMaterial.FISHING_ROD.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Click to set the" ,
				ChatColor.GRAY + "bus spawns." );
	}
	
	@Override
	protected EnumBattleMapSetupTool tool ( ) {
		return EnumBattleMapSetupTool.SET_BUS_SPAWNS;
	}
}