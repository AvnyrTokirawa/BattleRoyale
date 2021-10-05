package es.outlook.adriansrj.battleroyale.gui.spectator;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * Spectator mode options GUI.
 *
 * @author AdrianSR / 18/09/2021 / 09:46 p. m.
 */
public final class SpectatorGUI extends PluginHandler {
	
	// TODO: make configurable
	// TODO: button to toggle camera
	
	public static SpectatorGUI getInstance ( ) {
		return getPluginHandler ( SpectatorGUI.class );
	}
	
	protected final ItemMenu handle;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public SpectatorGUI ( BattleRoyale plugin ) {
		super ( plugin );
		
		this.handle = new ItemMenu ( ChatColor.BLACK + "Spectator Mode" , ItemMenuSize.THREE_LINE );
		this.handle.registerListener ( plugin );
	}
	
	public synchronized void open ( org.bukkit.entity.Player player ) {
		this.build ( );
		this.handle.open ( player );
	}
	
	public synchronized void open ( Player player ) {
		player.getBukkitPlayerOptional ( ).ifPresent ( this :: open );
	}
	
	private synchronized void build ( ) {
		this.handle.clear ( );
		
		this.handle.setItem ( 0 , new ActionItem (
				"Next" , UniversalMaterial.CHAINMAIL_CHESTPLATE.getItemStack ( ) ).addAction ( action -> {
			Player.getPlayer ( action.getPlayer ( ) ).toggleSpectatorTarget ( );
		} ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}