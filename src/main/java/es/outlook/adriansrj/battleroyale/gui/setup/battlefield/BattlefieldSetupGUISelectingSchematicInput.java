package es.outlook.adriansrj.battleroyale.gui.setup.battlefield;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.file.FileUtil;
import es.outlook.adriansrj.battleroyale.util.schematic.SchematicUtil;
import es.outlook.adriansrj.core.menu.custom.book.BookItemMenu;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author AdrianSR / 28/08/2021 / 05:21 p. m.
 */
class BattlefieldSetupGUISelectingSchematicInput extends BookItemMenu {
	
	public BattlefieldSetupGUISelectingSchematicInput ( BattleRoyale plugin ) {
		super ( ChatColor.BLACK + "Valid Schematics" , ItemMenuSize.SIX_LINE );
		
		registerListener ( plugin );
	}
	
	@Override
	public Inventory open ( org.bukkit.entity.Player player ) {
		clearContents ( );
		
		File   folder     = EnumDirectory.BATTLEFIELD_INPUT_DIRECTORY.getDirectory ( );
		File[] schematics = folder.listFiles ( FileUtil.GENERIC_SCHEMATIC_FILE_FILTER );
		
		if ( schematics != null && schematics.length > 0 ) {
			for ( File file : schematics ) {
				addItem ( new SchematicItem ( file ) );
			}
		} else {
			getHandler ( ).delayedClose ( player , 0 );
			
			// showing player the path to the input folder.
			String path = folder.getAbsolutePath ( ).replace ( "\\" , "/" );
			path = path.substring ( path.indexOf ( "plugins" ) );
			
			player.sendMessage ( ChatColor.RED + "Couldn't find any valid schematic in the folder: " + path );
		}
		
		return super.open ( player );
	}
	
	/**
	 * @author AdrianSR / 28/08/2021 / 05:25 p. m.
	 */
	protected static class SchematicItem extends ActionItem {
		
		public SchematicItem ( final File file ) {
			super ( ChatColor.GOLD + FilenameUtil.getBaseName ( file ) ,
					UniversalMaterial.CHEST.getItemStack ( ) );
			
			addAction ( action -> {
				// part n°1 of the process.
				org.bukkit.entity.Player player = action.getPlayer ( );
				player.sendMessage ( ChatColor.GOLD + "Loading schematic, please wait..." );
				
				try {
					Clipboard               schematic = SchematicUtil.loadSchematic ( file );
					BattlefieldSetupHandler handler   = BattlefieldSetupHandler.getInstance ( );
					
					// part n°2 of the process.
					player.sendMessage ( ChatColor.GOLD + "Generating setup world, please wait..." );
					
					// start and introduce once it is done generating the setup world.
					handler.startSession ( Player.getPlayer ( player ) , schematic
							, FilenameUtil.getBaseName ( file ) , session -> session.introduce ( player ) );
				} catch ( IOException | ClassNotFoundException | NoSuchMethodException
						| InvocationTargetException | IllegalAccessException | InstantiationException ex ) {
					ConsoleUtil.sendPluginMessage (
							ChatColor.RED , "Something went wrong when loading the schematic "
									+ file.getAbsolutePath ( ) + ": " , BattleRoyale.getInstance ( ) );
					ex.printStackTrace ( );
					
					player.sendMessage ( ChatColor.RED + "Couldn't load the selected schematic, " +
												 "please check the console." );
				}
				
				action.setClose ( true );
			} );
		}
	}
}