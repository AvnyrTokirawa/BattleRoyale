package es.outlook.adriansrj.battleroyale.gui.setup.battlefield;

import es.outlook.adriansrj.battleroyale.battlefield.Battlefield;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldRegistry;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.menu.custom.book.BookItemMenu;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import java.util.Collection;

/**
 * @author AdrianSR / 28/08/2021 / 05:15 p. m.
 */
class BattlefieldSetupGUIEditing extends BookItemMenu {
	
	public BattlefieldSetupGUIEditing ( BattleRoyale plugin ) {
		super ( ChatColor.BLACK + "Edit Battlefield" , ItemMenuSize.THREE_LINE );
		
		registerListener ( plugin );
	}
	
	@Override
	public Inventory open ( org.bukkit.entity.Player player ) {
		if ( BattlefieldRegistry.getInstance ( ).getBattlefields ( ).size ( ) > 0 ) {
			this.build ( );
		} else {
			getHandler ( ).delayedClose ( player , 0 );
			
			player.sendMessage (
					ChatColor.RED + "Couldn't find any valid battlefield in the folder: "
							+ FilenameUtil.filePathSubPlugins (
							EnumDirectory.BATTLEFIELD_DIRECTORY.getDirectory ( ).getAbsolutePath ( ) ) );
		}
		
		return super.open ( player );
	}
	
	protected void build ( ) {
		this.clear ( );
		
		Collection < Battlefield > battlefields = BattlefieldRegistry.getInstance ( ).getBattlefields ( );
		
		for ( Battlefield battlefield : battlefields ) {
			addItem ( new ActionItem ( ChatColor.GOLD + battlefield.getName ( ) ,
									   UniversalMaterial.BOW.getItemStack ( )
			).addAction ( action -> {
				org.bukkit.entity.Player player  = action.getPlayer ( );
				BattlefieldSetupHandler  handler = BattlefieldSetupHandler.getInstance ( );
				
				player.sendMessage ( ChatColor.GOLD + "Generating setup world, please wait..." );
				
				// start and introduce once it is done generating the setup world.
				handler.startSession ( Player.getPlayer ( player ) , battlefield
						, session -> session.introduce ( player ) );
				
				action.setClose ( true );
			} ) );
		}
	}
}