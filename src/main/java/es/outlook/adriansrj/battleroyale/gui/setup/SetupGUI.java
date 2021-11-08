package es.outlook.adriansrj.battleroyale.gui.setup;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupHandler;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.gui.setup.battlefield.BattlefieldSetupGUI;
import es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session.BattlefieldSetupSessionGUI;
import es.outlook.adriansrj.battleroyale.gui.setup.lobby.LobbyMapSetupGUI;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.item.action.ItemAction;
import es.outlook.adriansrj.core.menu.item.action.ItemActionPriority;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

/**
 * @author AdrianSR / 28/08/2021 / 04:09 p. m.
 */
public final class SetupGUI extends PluginHandler {
	
	public static SetupGUI getInstance ( ) {
		return getPluginHandler ( SetupGUI.class );
	}
	
	private final ItemMenu handle;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public SetupGUI ( BattleRoyale plugin ) {
		super ( plugin );
		
		this.handle = new ItemMenu ( ChatColor.BLACK + "Battle Royale Setup" , ItemMenuSize.THREE_LINE );
		this.handle.registerListener ( plugin );
		
		// lobby map setup
		this.handle.setItem ( 12 , new ActionItem (
				ChatColor.GOLD + "Lobby Setup" , UniversalMaterial.OAK_SIGN.getItemStack ( ) ).addAction (
				new ItemAction ( ) {
					@Override
					public ItemActionPriority getPriority ( ) {
						return ItemActionPriority.NORMAL;
					}
					
					@Override
					public void onClick ( ItemClickAction action ) {
						LobbyMapSetupGUI.getInstance ( ).open ( action.getPlayer ( ) );
					}
				} ) );
		// battle map setup
		this.handle.setItem ( 14 , new ActionItem (
				ChatColor.GOLD + "Battlefield Setup" , UniversalMaterial.BOW.getItemStack ( ) ).addAction (
				new ItemAction ( ) {
					@Override
					public ItemActionPriority getPriority ( ) {
						return ItemActionPriority.NORMAL;
					}
					
					@Override
					public void onClick ( ItemClickAction action ) {
						BattlefieldSetupGUI.getInstance ( ).open ( action.getPlayer ( ) );
					}
				} ) );
	}
	
	public void open ( org.bukkit.entity.Player player ) {
		// if the player is in an active battle map setup session,
		// the corresponding gui must open
		if ( ! ( openBattlefieldSetupSessionGUI ( player ) ) ) {
			handle.open ( player );
		}
	}
	
	private boolean openBattlefieldSetupSessionGUI ( org.bukkit.entity.Player player ) {
		if ( isInActiveBattlefieldSetupSession ( player ) ) {
			BattlefieldSetupSessionGUI.getInstance ( ).open ( player );
			return true;
		} else {
			return false;
		}
	}
	
	public void refresh ( org.bukkit.entity.Player player ) {
		if ( ! ( refreshBattlefieldSetupSessionGUI ( player ) ) ) {
			handle.update ( player );
		}
	}
	
	private boolean refreshBattlefieldSetupSessionGUI ( org.bukkit.entity.Player player ) {
		if ( isInActiveBattlefieldSetupSession ( player ) ) {
			BattlefieldSetupSessionGUI.getInstance ( ).refresh ( player );
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isInActiveBattlefieldSetupSession ( org.bukkit.entity.Player player ) {
		return BattlefieldSetupHandler.getInstance ( ).isInActiveSession ( Player.getPlayer ( player ) );
	}
	
//	private BattlefieldSetupSession getBattlefieldSetupSession ( org.bukkit.entity.Player player ) {
	//		Player                  configurator              = Player.getPlayer ( player );
	//		BattlefieldSetupHandler battlefield_setup_handler = BattlefieldSetupHandler.getInstance ( );
	//
	//		return battlefield_setup_handler.getSession ( configurator )
	//				.orElse ( battlefield_setup_handler.getSessionFromInvited (
	//						configurator ).orElse ( null ) );
	//	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}