package es.outlook.adriansrj.battleroyale.gui.setup.battlefield;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.menu.item.action.open.OpenMenuActionItem;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 28/08/2021 / 04:57 p. m.
 */
class BattlefieldSetupGUIEditButton extends OpenMenuActionItem {
	
	public BattlefieldSetupGUIEditButton ( BattleRoyale plugin ) {
		super ( ChatColor.GOLD + "Edit Battlefield" , UniversalMaterial.REDSTONE.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Click to edit a" ,
				ChatColor.GRAY + "battlefield." );
		
		setMenu ( new BattlefieldSetupGUIEditing ( plugin ) );
	}
}