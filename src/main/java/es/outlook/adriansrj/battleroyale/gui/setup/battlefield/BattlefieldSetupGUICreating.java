package es.outlook.adriansrj.battleroyale.gui.setup.battlefield;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.item.action.open.OpenMenuActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 28/08/2021 / 04:56 p. m.
 */
class BattlefieldSetupGUICreating extends ItemMenu {
	
	protected final BattlefieldSetupGUISelectingWorldInput     world;
	protected final BattlefieldSetupGUISelectingSchematicInput schematic;
	
	public BattlefieldSetupGUICreating ( BattleRoyale plugin ) {
		super ( ChatColor.BLACK + "New Battlefield" , ItemMenuSize.THREE_LINE );
		
		this.world     = new BattlefieldSetupGUISelectingWorldInput ( plugin );
		this.schematic = new BattlefieldSetupGUISelectingSchematicInput ( plugin );
		
		setItem ( 12 , new OpenMenuActionItem (
				ChatColor.GOLD + "From a World" , UniversalMaterial.GRASS.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Create a new battlefield" ,
				ChatColor.GRAY + "copying a world." ).setMenu ( world ) );
		
		setItem ( 14 , new OpenMenuActionItem (
				ChatColor.GOLD + "From a Schematic" , UniversalMaterial.CHEST.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Create a new battlefield" ,
				ChatColor.GRAY + "copying a schematic." ).setMenu ( schematic ) );
		
		registerListener ( plugin );
	}
}