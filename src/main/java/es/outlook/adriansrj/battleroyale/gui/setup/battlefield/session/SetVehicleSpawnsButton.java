package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.enums.EnumBattleMapSetupTool;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 14/09/2021 / 05:55 p. m.
 */
class SetVehicleSpawnsButton extends SetupToolButtonDefaultTool {
	
	public SetVehicleSpawnsButton ( BattlefieldSetupSession session ) {
		super ( session , ChatColor.GOLD + "Set Vehicle Spawns" , UniversalMaterial.MINECART.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Click to set the" ,
				ChatColor.GRAY + "vehicle spawns." );
	}
	
	@Override
	protected EnumBattleMapSetupTool defaultTool ( ) {
		return EnumBattleMapSetupTool.SET_VEHICLE_SPAWNS;
	}
}