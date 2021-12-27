package es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaWorld;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupHandler;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.file.FileUtil;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.Item;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author AdrianSR / 28/08/2021 / 03:50 p. m.
 */
public final class BattlefieldSetupSessionGUI extends PluginHandler {
	
	public static BattlefieldSetupSessionGUI getInstance ( ) {
		return getPluginHandler ( BattlefieldSetupSessionGUI.class );
	}
	
	private final ItemMenu              handle;
	private final Map < Integer, Item > extras;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BattlefieldSetupSessionGUI ( BattleRoyale plugin ) {
		super ( plugin );
		
		this.handle = new ItemMenu ( ChatColor.BLACK + "Battlefield Setup" , ItemMenuSize.SIX_LINE );
		this.extras = new ConcurrentHashMap <> ( );
		
		this.handle.registerListener ( plugin );
	}
	
	public synchronized void open ( org.bukkit.entity.Player player ) {
		this.build ( Player.getPlayer ( player ) );
		this.handle.open ( player );
	}
	
	public synchronized void refresh ( org.bukkit.entity.Player player ) {
		this.build ( Player.getPlayer ( player ) );
		this.handle.update ( player );
	}
	
	public void registerTool ( int index , Item item ) {
		extras.put ( index , item );
	}
	
	public Item unregisterTool ( int index ) {
		return extras.remove ( index );
	}
	
	private synchronized void build ( Player player ) {
		this.handle.clear ( );
		
		BattlefieldSetupHandler handler = BattlefieldSetupHandler.getInstance ( );
		BattlefieldSetupSession session = handler.getSession ( player )
				.orElse ( handler.getSessionFromInvited ( player ).orElse ( null ) );
		
		if ( session != null ) {
			this.handle.setItem ( 10 , new SetNameButton ( session ) );
			
			if ( session.isNameSet ( ) ) {
				this.handle.setItem ( 12 , new SetBoundsButton ( session ) );
			}
			
			if ( session.isNameSet ( ) && session.isBoundsSet ( ) ) {
				this.handle.setItem ( 14 , new SetBorderButton ( session ) );
				this.handle.setItem ( 16 , new SetBusSpawnsButton ( session ) );
				this.handle.setItem ( 19 , new SetPlayerSpawnsButton ( session ) );
				this.handle.setItem ( 21 , new SetLootChestsButton ( session ) );
				this.handle.setItem ( 23 , new SetAirSupplyButton ( session ) );
				this.handle.setItem ( 25 , new SetBombingZoneButton ( session ) );
				this.handle.setItem ( 28 , new SetVehicleSpawnsButton ( session ) );
				
				// vehicles configuration
				SetVehiclesConfigurationButton vehicles_configuration = new SetVehiclesConfigurationButton ( );
				
				if ( vehicles_configuration.available ( ) ) {
					this.handle.setItem ( 30 , vehicles_configuration );
				}
				
				// loot configuration
				SetLootConfigurationButton loot_configuration = new SetLootConfigurationButton ( );
				
				if ( loot_configuration.available ( ) ) {
					this.handle.setItem ( 32 , loot_configuration );
				}
				
				// save world changes
				this.handle.setItem ( 53 , new Item (
						ChatColor.GREEN + "Save world changes" ,
						UniversalMaterial.GRASS.getItemStack ( ) ,
						"" ,
						ChatColor.GRAY + "Did you place or destroy any" ,
						ChatColor.GRAY + "block? Then you need to save" ,
						ChatColor.GRAY + "the world changes." ,
						ChatColor.GREEN + "Just click here to save changes." ,
						"" ,
						ChatColor.GOLD + "Note that this might take a" ,
						ChatColor.GOLD + "while depending on the size" ,
						ChatColor.GOLD + "of your battlefield."
				) {
					@Override
					public void onClick ( ItemClickAction action ) {
						if ( session.isActive ( ) && session.isNameSet ( ) && session.isBoundsSet ( ) ) {
							action.getPlayer ( ).sendMessage (
									ChatColor.GOLD + "Saving changes..." );
							
							// deleting prepared world. shape changes
							// are now present, so the prepared world
							// must be generated again.
							File prepared = new File ( session.getFolder ( ) ,
													   BattleRoyaleArenaWorld.PREPARED_WORLD_FOLDER_NAME );
							
							if ( prepared.exists ( ) ) {
								try {
									FileUtil.deleteDirectory ( prepared );
								} catch ( IOException e ) {
									e.printStackTrace ( );
								}
							}
							
							// then recalculating shape and minimap
							session.recalculateShapeChanges (
									true ,
									minimap -> {
										if ( minimap != null ) {
											action.getPlayer ( ).sendMessage (
													ChatColor.GREEN + "Minimap recalculated successfully!" );
										} else {
											action.getPlayer ( ).sendMessage (
													ChatColor.DARK_RED + "Something went wrong when recalculating" +
															"minimap. Please check the console." );
										}
									} ,
									true ,
									successful -> {
										if ( successful ) {
											action.getPlayer ( ).sendMessage (
													ChatColor.GREEN + "Changes saved successfully!" );
										} else {
											action.getPlayer ( ).sendMessage (
													ChatColor.DARK_RED + "Something went wrong when saving changes." +
															"Please check the console." );
										}
									} );
						}
					}
				} );
				
				// extra tools
				this.extras.forEach ( ( index , item ) -> {
					ItemMenuSize size = ItemMenuSize.fitOf ( index );
					
					if ( size.getSize ( ) > this.handle.getSize ( ).getSize ( ) ) {
						this.handle.setSize ( size );
					}
					
					this.handle.setItem ( index , item );
				} );
			}
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}