package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupTool;
import es.outlook.adriansrj.battleroyale.enums.EnumBattleMapSetupTool;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author AdrianSR / 08/11/2021 / 03:58 p. m.
 */
public abstract class SetupToolButtonDefaultTool extends SetupToolButtonBase {
	
	public SetupToolButtonDefaultTool ( BattlefieldSetupSession session , String name , ItemStack icon , String... lore ) {
		super ( session , name , icon , lore );
	}
	
	public SetupToolButtonDefaultTool ( BattlefieldSetupSession session , String name , ItemStack icon ,
			List < String > lore ) {
		super ( session , name , icon , lore );
	}
	
	public SetupToolButtonDefaultTool ( BattlefieldSetupSession session , ItemStack icon ) {
		super ( session , icon );
	}
	
	protected abstract EnumBattleMapSetupTool defaultTool ( );
	
	@Override
	protected BattlefieldSetupTool tool ( Player player ) {
		return defaultTool ( ).getNewInstance ( session , player );
	}
}