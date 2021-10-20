package es.outlook.adriansrj.battleroyale.battlefield.setup.tool;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupTool;
import es.outlook.adriansrj.battleroyale.enums.EnumInternalLanguage;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.menu.ItemMenu;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

/**
 * @author AdrianSR / 19/10/2021 / 05:43 p. m.
 */
public abstract class BattlefieldSetupToolGUI extends BattlefieldSetupTool {
	
	protected ItemMenu handle;
	// this flag will stop the event handler
	// from calling the dispose method, which
	// would cause an infinite loop.
	protected boolean  disposing_flag;
	
	protected BattlefieldSetupToolGUI ( BattlefieldSetupSession session , Player configurator ) {
		super ( session , configurator );
	}
	
	protected abstract ItemMenu build ( );
	
	@Override
	protected void initialize ( ) {
		if ( handle == null ) {
			handle = Objects.requireNonNull ( build ( ) , "build() returned null" );
		}
		
		register ( );
		
		// opening gui
		configurator.getBukkitPlayerOptional ( )
				.ifPresent ( player -> handle.open ( player ) );
	}
	
	@Override
	protected void dispose ( ) {
		disposing_flag = true;
		
		// closing gui
		if ( handle != null ) {
			configurator.getBukkitPlayerOptional ( )
					.ifPresent ( player -> handle.close ( player ) );
		}
		
		super.dispose ( );
		disposing_flag = false;
	}
	
	@Override
	protected void register ( ) {
		handle.registerListener ( BattleRoyale.getInstance ( ) );
		
		// must register after handle,
		// as it could be null.
		super.register ( );
	}
	
	@Override
	protected void unregister ( ) {
		if ( handle != null ) {
			handle.unregisterListener ( );
			handle = null;
		}
	}
	
	@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public final void onCancel ( InventoryCloseEvent event ) {
		// the player can cancel this tool by closing gui
		Inventory   inventory = event.getInventory ( );
		HumanEntity player    = event.getPlayer ( );
		
		if ( !disposing_flag && handle != null && handle.isThisMenu ( inventory )
				&& Objects.equals ( configurator.getUniqueId ( ) , player.getUniqueId ( ) ) ) {
			dispose ( );
			
			// letting player know
			if ( isCancellable ( ) ) {
				player.sendMessage ( EnumInternalLanguage.TOOL_CANCELLED_MESSAGE.toString ( ) );
			} else {
				player.sendMessage ( EnumInternalLanguage.TOOL_FINISHED_MESSAGE.toString ( ) );
			}
		}
	}
}
