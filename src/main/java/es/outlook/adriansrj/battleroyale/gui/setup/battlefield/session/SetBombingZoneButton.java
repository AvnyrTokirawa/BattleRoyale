package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.enums.EnumBattleMapSetupTool;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 19/10/2021 / 07:23 p. m.
 */
class SetBombingZoneButton extends SetupToolButtonDefaultTool {
	
	public SetBombingZoneButton ( BattlefieldSetupSession session ) {
		super ( session , ChatColor.GOLD + "Set Bombing Zone" , UniversalMaterial.TNT.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Click to set the bombing zone" ,
				ChatColor.GRAY + "generator configuration." );
	}
	
	@Override
	protected EnumBattleMapSetupTool defaultTool ( ) {
		return EnumBattleMapSetupTool.SET_BOMBING_ZONE;
	}
}