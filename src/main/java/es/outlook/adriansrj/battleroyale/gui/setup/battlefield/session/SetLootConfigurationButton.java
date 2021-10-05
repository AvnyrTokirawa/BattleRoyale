package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.core.menu.item.action.open.OpenMenuActionItem;
import es.outlook.adriansrj.core.util.file.filter.YamlFileFilter;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 13/09/2021 / 11:44 a. m.
 */
class SetLootConfigurationButton extends OpenMenuActionItem {
	
	public SetLootConfigurationButton ( ) {
		super ( ChatColor.GOLD + "Set Loot Configuration" , UniversalMaterial.PAPER.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Click to set the loot" ,
				ChatColor.GRAY + "configuration this" ,
				ChatColor.GRAY + "battlefield will use" ,
				ChatColor.GRAY + "to fill chests, air" ,
				ChatColor.GRAY + "supply, etc..." );
		
		setMenu ( SetLootConfigurationGUI.INSTANCE );
	}
	
	public boolean available ( ) {
		// this option will be available only
		// if there is more than one loot configuration
		// file, as it is not necessary to specify
		// a loot configuration if there is only one
		// file in the folder.
		return EnumDirectory.LOOT_DIRECTORY.getDirectory ( ).listFiles ( new YamlFileFilter ( ) ).length > 1;
	}
}