package es.outlook.adriansrj.battleroyale.gui.parachute;

import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.parachute.creator.ParachuteCreationStageHandler;
import es.outlook.adriansrj.core.menu.custom.book.BookItemMenu;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.file.filter.YamlFileFilter;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.Objects;

/**
 * @author AdrianSR / 24/11/2021 / 10:56 a. m.
 */
class ParachuteCreationLoadGUI extends BookItemMenu {
	
	public ParachuteCreationLoadGUI ( BattleRoyale plugin ) {
		super ( ChatColor.BLACK + "Load Parachute" , ItemMenuSize.SIX_LINE );
		
		registerListener ( plugin );
	}
	
	@Override
	public Inventory open ( org.bukkit.entity.Player player ) {
		if ( Objects.requireNonNull ( EnumDirectory.PARACHUTE_DIRECTORY.getDirectory ( ).listFiles (
				new YamlFileFilter ( ) ) ).length > 0 ) {
			this.build ( );
		} else {
			getHandler ( ).delayedClose ( player , 0 );
			
			player.sendMessage (
					ChatColor.RED + "Couldn't find any parachute in the parachutes folder: "
							+ FilenameUtil.filePathSubPlugins (
							EnumDirectory.PARACHUTE_DIRECTORY.getDirectory ( ).getAbsolutePath ( ) ) );
		}
		
		return super.open ( player );
	}
	
	protected void build ( ) {
		for ( File parachute_file : Objects.requireNonNull (
				EnumDirectory.PARACHUTE_DIRECTORY.getDirectory ( ).listFiles ( new YamlFileFilter ( ) ) ) ) {
			addItem ( new ActionItem ( ChatColor.GOLD + FilenameUtil.getBaseName ( parachute_file ) ,
									   UniversalMaterial.SADDLE.getItemStack ( )
			).addAction ( action -> {
				action.setClose ( true );
				
				ParachuteCreationStageHandler.getInstance ( )
						.startStage ( action.getPlayer ( ) , parachute_file );
			} ) );
		}
	}
}
