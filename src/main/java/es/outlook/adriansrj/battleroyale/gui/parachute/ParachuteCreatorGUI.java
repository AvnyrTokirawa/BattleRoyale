package es.outlook.adriansrj.battleroyale.gui.parachute;

import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.parachute.creator.ParachuteCreationStageHandler;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.Item;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.menu.item.action.close.CloseMenuActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ExactMatchConversationCanceller;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author AdrianSR / 22/11/2021 / 08:45 p. m.
 */
public final class ParachuteCreatorGUI extends PluginHandler {
	
	/**
	 * Cancel conversation keywords.
	 */
	private static final String[] CANCEL_KEYWORDS = {
			"exit" , "cancel" , "quit"
	};
	
	public static ParachuteCreatorGUI getInstance ( ) {
		return getPluginHandler ( ParachuteCreatorGUI.class );
	}
	
	private final ItemMenu                 handle;
	private final ParachuteCreationLoadGUI load_gui;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public ParachuteCreatorGUI ( BattleRoyale plugin ) {
		super ( plugin );
		
		this.handle   = new ItemMenu ( ChatColor.BLACK + "Parachute Creator" , ItemMenuSize.THREE_LINE );
		this.load_gui = new ParachuteCreationLoadGUI ( plugin );
		
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
		ParachuteCreationStageHandler stage_handler = ParachuteCreationStageHandler.getInstance ( );
		
		this.handle.clear ( );
		
		// new parachute
		this.handle.setItem ( 12 , new Item (
				ChatColor.GREEN + "New Parachute" ,
				UniversalMaterial.CRAFTING_TABLE.getItemStack ( )
		) {
			@Override
			public void onClick ( ItemClickAction action ) {
				action.setClose ( true );
				
				ConversationFactory factory = new ConversationFactory ( BattleRoyale.getInstance ( ) );
				ParachuteCreationPrompt prompt = new ParachuteCreationPrompt (
						( player , filename ) -> stage_handler.startStage (
								player , new File ( EnumDirectory.PARACHUTE_DIRECTORY.getDirectory ( ) , filename ) ) );
				
				// cancellers
				for ( String keyword : CANCEL_KEYWORDS ) {
					factory.withConversationCanceller ( new ExactMatchConversationCanceller ( keyword ) {
						@Override
						public boolean cancelBasedOnInput (
								@NotNull ConversationContext context , @NotNull String input ) {
							return keyword.equalsIgnoreCase ( input );
						}
					} );
				}
				
				// then begin
				factory.withFirstPrompt ( prompt ).withModality ( true ).withLocalEcho ( true )
						.buildConversation ( action.getPlayer ( ) ).begin ( );
			}
		} );
		
		// load parachute
		this.handle.setItem ( 14 , new Item (
				ChatColor.GOLD + "Load Parachute" , UniversalMaterial.FISHING_ROD.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Loads an existing parachute" ,
				ChatColor.GRAY + "for editing."
		) {
			@Override
			public void onClick ( ItemClickAction action ) {
				load_gui.open ( action.getPlayer ( ) );
			}
		} );
		
		// done button
		this.handle.setItem ( 18 , new Item (
				ChatColor.GREEN + "I'm Done" , UniversalMaterial.EMERALD.getItemStack ( )
		) {
			@Override
			public void onClick ( ItemClickAction action ) {
				action.setClose ( true );
				
				stage_handler.stopStage ( action.getPlayer ( ) );
			}
		} );
		
		// close button
		this.handle.setItem ( 26 , new CloseMenuActionItem ( ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}