package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupHandler;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.vehicle.VehiclesConfigurationRegistry;
import es.outlook.adriansrj.core.menu.custom.book.BookItemMenu;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.file.filter.YamlFileFilter;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.Objects;

/**
 * @author AdrianSR / 13/09/2021 / 11:44 a. m.
 */
class SetVehiclesConfigurationGUI extends BookItemMenu {
	
	static final SetVehiclesConfigurationGUI INSTANCE;
	
	static {
		INSTANCE = new SetVehiclesConfigurationGUI ( );
		INSTANCE.registerListener ( BattleRoyale.getInstance ( ) );
	}
	
	private SetVehiclesConfigurationGUI ( ) {
		super ( ChatColor.BLACK + "Valid vehicles configuration" , ItemMenuSize.THREE_LINE );
	}
	
	@Override
	public Inventory open ( org.bukkit.entity.Player player ) {
		build ( );
		return super.open ( player );
	}
	
	@Override
	public boolean update ( org.bukkit.entity.Player player ) {
		build ( );
		return super.update ( player );
	}
	
	protected void build ( ) {
		this.clearContents ( );
		
		for ( File file : Objects.requireNonNull (
				EnumDirectory.VEHICLE_DIRECTORY.getDirectory ( ).listFiles ( new YamlFileFilter ( ) ) ) ) {
			if ( !file.exists ( ) || VehiclesConfigurationRegistry.getInstance ( ).getConfiguration (
					FilenameUtil.getBaseName ( file ) ) == null ) { continue; }
			
			addItem ( new ActionItem ( ChatColor.GOLD + FilenameUtil.getBaseName ( file ) ,
									   UniversalMaterial.PAPER.getItemStack ( ) ).addAction ( action -> {
				action.setClose ( true );
				
				Player                  player  = Player.getPlayer ( action.getPlayer ( ) );
				BattlefieldSetupHandler handler = BattlefieldSetupHandler.getInstance ( );
				
				if ( handler.isInActiveSession ( player ) ) {
					BattlefieldSetupSession session = handler.getSession ( player ).orElse (
							handler.getSessionFromInvited ( player ).orElse ( null ) );
					
					if ( session != null ) {
						session.setVehiclesConfiguration ( FilenameUtil.getBaseName ( file ) );
						
						action.getPlayer ( ).sendMessage (
								ChatColor.GREEN + "This battlefield will now use the vehicles configuration " +
										"provided by the file '" + ChatColor.GOLD
										+ file.getName ( ) + ChatColor.GREEN + "'" );
					}
				}
			} ) );
		}
	}
}