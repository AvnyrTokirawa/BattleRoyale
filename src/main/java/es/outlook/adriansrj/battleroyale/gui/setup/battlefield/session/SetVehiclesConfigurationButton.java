package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.core.menu.item.action.open.OpenMenuActionItem;
import es.outlook.adriansrj.core.util.file.filter.YamlFileFilter;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 14/09/2021 / 08:23 p. m.
 */
class SetVehiclesConfigurationButton extends OpenMenuActionItem {
	
	public SetVehiclesConfigurationButton ( ) {
		super ( ChatColor.GOLD + "Set Vehicles Configuration" , UniversalMaterial.PAPER.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Click to set the vehicles" ,
				ChatColor.GRAY + "configuration this" ,
				ChatColor.GRAY + "battlefield will use" ,
				ChatColor.GRAY + "to spawn vehicles" ,
				ChatColor.GRAY + "around." );
		
		setMenu ( SetVehiclesConfigurationGUI.INSTANCE );
	}
	
	public boolean available ( ) {
		// this option will be available only
		// if there is more than one vehicles configuration
		// file, as it is not necessary to specify
		// a vehicles configuration if there is only one
		// file in the folder.
		return EnumDirectory.VEHICLE_DIRECTORY.getDirectory ( ).listFiles ( new YamlFileFilter ( ) ).length > 1;
	}
}