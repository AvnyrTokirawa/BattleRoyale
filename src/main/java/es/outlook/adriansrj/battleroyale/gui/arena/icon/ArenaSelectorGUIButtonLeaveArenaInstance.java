package es.outlook.adriansrj.battleroyale.gui.arena.icon;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.gui.GUIIconInstance;
import es.outlook.adriansrj.battleroyale.gui.GUIInstance;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/**
 * Button which makes the player leaves the arena.
 *
 * @author AdrianSR / 03/10/2021 / 11:12 a. m.
 */
public class ArenaSelectorGUIButtonLeaveArenaInstance extends GUIIconInstance {
	
	protected final ArenaSelectorGUIButtonLeaveArena configuration;
	
	public ArenaSelectorGUIButtonLeaveArenaInstance ( ArenaSelectorGUIButtonLeaveArena configuration ,
			GUIInstance gui , String name , ItemStack icon , Collection < String > lore ) {
		super ( gui , name , icon , lore );
		this.configuration = configuration;
	}
	
	public ArenaSelectorGUIButtonLeaveArenaInstance ( ArenaSelectorGUIButtonLeaveArena configuration ,
			GUIInstance gui , String name , ItemStack icon , String... lore ) {
		super ( gui , name , icon , lore );
		this.configuration = configuration;
	}
	
	public ArenaSelectorGUIButtonLeaveArenaInstance ( ArenaSelectorGUIButtonLeaveArena configuration ,
			GUIInstance gui , ItemStack icon ) {
		super ( gui , icon );
		this.configuration = configuration;
	}
	
	@Override
	public void onClick ( ItemClickAction action ) {
		Player br_player = Player.getPlayer ( action.getPlayer ( ) );
		
		if ( br_player.isInArena ( ) ) {
			BattleRoyaleArenaHandler.getInstance ( ).leaveArena ( br_player );
		}
		
		action.setUpdate ( true );
	}
}