package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupHandler;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupTool;
import es.outlook.adriansrj.battleroyale.enums.EnumBattleMapSetupTool;
import es.outlook.adriansrj.battleroyale.enums.EnumInternalLanguage;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.item.action.ItemActionAdapter;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * @author AdrianSR / 04/09/2021 / 11:45 p. m.
 */
abstract class SetupToolButtonBase extends ActionItem {
	
	/** default action */
	protected final ItemActionAdapter default_action = action -> {
		org.bukkit.entity.Player player    = action.getPlayer ( );
		Player                   br_player = Player.getPlayer ( player );
		BattlefieldSetupHandler  handler   = BattlefieldSetupHandler.getInstance ( );
		BattlefieldSetupSession session = handler.getSession ( br_player )
				.orElse ( handler.getSessionFromInvited ( br_player ).orElse ( null ) );
		
		if ( session != null ) {
			BattlefieldSetupTool tool = session.getCurrentTool ( player );
			
			if ( tool == null || ! tool.isModal ( ) ) {
				session.populateTool ( player , Objects.requireNonNull (
						tool ( ) , "tool() returned null" ) );
			} else {
				player.sendMessage ( EnumInternalLanguage.TOOL_BUSY.toString ( ) );
			}
		} else {
			throw new IllegalStateException ( );
		}
		
		action.setClose ( true );
	};
	
	public SetupToolButtonBase ( String name , ItemStack icon , String... lore ) {
		super ( name , icon , lore );
		addAction ( default_action );
	}
	
	public SetupToolButtonBase ( String name , ItemStack icon , List < String > lore ) {
		super ( name , icon , lore );
		addAction ( default_action );
	}
	
	public SetupToolButtonBase ( ItemStack icon ) {
		super ( icon );
		addAction ( default_action );
	}
	
	protected abstract EnumBattleMapSetupTool tool ( );
}