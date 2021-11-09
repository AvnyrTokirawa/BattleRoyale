package es.outlook.adriansrj.battleroyale.gui.setup.battlefield;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author AdrianSR / 28/08/2021 / 03:50 p. m.
 */
public final class BattlefieldSetupGUI extends PluginHandler {
	
	public static BattlefieldSetupGUI getInstance ( ) {
		return getPluginHandler ( BattlefieldSetupGUI.class );
	}
	
	private final ItemMenu handle;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BattlefieldSetupGUI ( BattleRoyale plugin ) {
		super ( plugin );
		
		this.handle = new ItemMenu ( ChatColor.BLACK + "Battlefield Setup" , ItemMenuSize.THREE_LINE );
		this.handle.registerListener ( plugin );
		
		// create button
		this.handle.setItem ( 12 , new BattlefieldSetupGUICreateButton ( plugin ) );
		// edit existing
		this.handle.setItem ( 14 , new BattlefieldSetupGUIEditButton ( plugin ) );
	}
	
	public void open ( Player player ) {
		handle.open ( player );
	}
	
	public void refresh ( Player player ) {
		handle.update ( player );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}