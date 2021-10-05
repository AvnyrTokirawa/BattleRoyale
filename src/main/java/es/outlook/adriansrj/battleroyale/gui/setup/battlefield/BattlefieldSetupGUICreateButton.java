package es.outlook.adriansrj.battleroyale.gui.setup.battlefield;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.menu.item.action.open.OpenMenuActionItem;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 28/08/2021 / 04:57 p. m.
 */
class BattlefieldSetupGUICreateButton extends OpenMenuActionItem {
	
	public BattlefieldSetupGUICreateButton ( BattleRoyale plugin ) {
		super ( ChatColor.GOLD + "New Battlefield" , UniversalMaterial.PAPER.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Click to start the" ,
				ChatColor.GRAY + "setup of a new battle map.");
		
		setMenu ( new BattlefieldSetupGUICreating ( plugin ) );
	}
}