package es.outlook.adriansrj.battleroyale.gui.setup.battlefield;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.menu.custom.book.BookItemMenu;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.world.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.FileFilter;

/**
 * @author AdrianSR / 28/08/2021 / 05:21 p. m.
 */
class BattlefieldSetupGUISelectingWorldInput extends BookItemMenu {
	
	protected static final FileFilter WORLD_FILE_FILTER = WorldUtil :: worldFolderCheck;
	
	public BattlefieldSetupGUISelectingWorldInput ( BattleRoyale plugin ) {
		super ( ChatColor.BLACK + "Valid Worlds" , ItemMenuSize.SIX_LINE );
		
		registerListener ( plugin );
	}
	
	@Override
	public Inventory open ( org.bukkit.entity.Player player ) {
		clearContents ( );
		
		File   folder = EnumDirectory.BATTLEFIELD_INPUT_DIRECTORY.getDirectory ( );
		File[] worlds = folder.listFiles ( WORLD_FILE_FILTER );
		
		if ( worlds != null && worlds.length > 0 ) {
			for ( File file : worlds ) {
				addItem ( new WorldItem ( file ) );
			}
		} else {
			getHandler ( ).delayedClose ( player , 0 );
			
			// showing player the path to the input folder.
			String path = folder.getAbsolutePath ( ).replace ( "\\" , "/" );
			path = path.substring ( path.indexOf ( "plugins" ) );
			
			player.sendMessage ( ChatColor.RED + "Couldn't find any valid world in the folder: " + path );
		}
		
		return super.open ( player );
	}
	
	/**
	 * @author AdrianSR / 28/08/2021 / 05:25 p. m.
	 */
	protected static class WorldItem extends ActionItem {
		
		public WorldItem ( final File world_folder ) {
			super ( ChatColor.GOLD + world_folder.getName ( ) , UniversalMaterial.GRASS.getItemStack ( ) );
			
			addAction ( action -> {
				action.setClose ( true );
				
				org.bukkit.entity.Player player  = action.getPlayer ( );
				BattlefieldSetupHandler  handler = BattlefieldSetupHandler.getInstance ( );
				
				player.sendMessage ( ChatColor.GOLD + "Loading world, please wait..." );
				
				try {
					// must make sure to start session from server
					// thread as it will load the world.
					Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> handler.startSession (
							Player.getPlayer ( player ) , world_folder ).introduce ( player ) );
				} catch ( IllegalArgumentException ex ) {
					ConsoleUtil.sendPluginMessage (
							ChatColor.RED , "Something went wrong when loading the world "
									+ world_folder.getAbsolutePath ( ) + ": " , BattleRoyale.getInstance ( ) );
					ex.printStackTrace ( );
					
					player.sendMessage ( ChatColor.RED + "Couldn't load the selected world, " +
												 "please check the console." );
				}
			} );
		}
	}
}