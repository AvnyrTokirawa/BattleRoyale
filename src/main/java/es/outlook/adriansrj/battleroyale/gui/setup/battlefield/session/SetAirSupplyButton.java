package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.enums.EnumBattleMapSetupTool;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 19/10/2021 / 06:29 p. m.
 */
class SetAirSupplyButton extends SetupToolButtonBase {
	
	public SetAirSupplyButton ( ) {
		super ( ChatColor.GOLD + "Set Air Supply" , UniversalMaterial.LEAD.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Click to set the air supply" ,
				ChatColor.GRAY + "generator configuration." );
	}
	
	@Override
	protected EnumBattleMapSetupTool tool ( ) {
		return EnumBattleMapSetupTool.SET_AIR_SUPPLY;
	}
}