package es.outlook.adriansrj.battleroyale.gui.arena.icon;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.gui.GUIIconInstance;
import es.outlook.adriansrj.battleroyale.gui.GUIInstance;
import es.outlook.adriansrj.battleroyale.util.itemstack.ItemStackUtil;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.util.StringUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Button which makes the player join an arena.
 * <br>
 * <b>The player will not be able to join the arena if:</b>
 * <ul>
 *     <li><b>Running.</b></li>
 *     <li><b>Full.</b></li>
 *     <li><b>Restarting</b></li>
 *     <li><b>Stopped</b></li>
 * </ul>
 *
 * @author AdrianSR / 03/10/2021 / 11:12 a. m.
 */
public class ArenaSelectorGUIButtonArenaInstance extends GUIIconInstance {
	
	protected final ArenaSelectorGUIButtonArena configuration;
	protected       BattleRoyaleArena           arena;
	
	public ArenaSelectorGUIButtonArenaInstance ( ArenaSelectorGUIButtonArena configuration ,
			GUIInstance gui , String name , ItemStack icon , Collection < String > lore ) {
		super ( gui , name , icon , lore );
		this.configuration = configuration;
	}
	
	public ArenaSelectorGUIButtonArenaInstance ( ArenaSelectorGUIButtonArena configuration ,
			GUIInstance gui , String name , ItemStack icon , String... lore ) {
		super ( gui , name , icon , lore );
		this.configuration = configuration;
	}
	
	public ArenaSelectorGUIButtonArenaInstance ( ArenaSelectorGUIButtonArena configuration ,
			GUIInstance gui , ItemStack icon ) {
		super ( gui , icon );
		this.configuration = configuration;
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	public void setArena ( BattleRoyaleArena arena ) {
		this.arena = arena;
	}
	
	@Override
	public ItemStack getDisplayIcon ( ) {
		ItemStack icon = super.getDisplayIcon ( );
		
		if ( arena != null ) {
			// display name
			ItemStackUtil.setName ( icon , String.format (
					configuration.getDisplayNameFormat ( ) ,
					StringUtil.capitalize ( arena.getName ( ).toLowerCase ( ) ) ) );
			
			// description
			List < String > description = configuration.getDescriptionFormat ( );
			
			if ( arena.getState ( ) == EnumArenaState.RUNNING ) {
				description = configuration.getDescriptionFormatRunning ( );
			} else if ( arena.getState ( ) == EnumArenaState.RESTARTING ) {
				description = configuration.getDescriptionFormatRestarting ( );
			} else if ( arena.getState ( ) == EnumArenaState.STOPPED ) {
				description = configuration.getDescriptionFormatStopped ( );
			} else if ( arena.isFull ( ) ) {
				description = configuration.getDescriptionFormatFull ( );
			}
			
			ItemStackUtil.setLore ( icon , description != null
					? new ArrayList <> ( description ) : Collections. < String > emptyList ( ) );
		}
		
		return icon;
	}
	
	@Override
	public void onClick ( ItemClickAction action ) {
		Player br_player = Player.getPlayer ( action.getPlayer ( ) );
		
		if ( arena != null ) {
			if ( arena.getState ( ) == EnumArenaState.RUNNING ) {
				action.getPlayer ( ).sendMessage ( EnumLanguage.ARENA_MESSAGE_RUNNING.getAsString ( ) );
			} else if ( arena.getState ( ) == EnumArenaState.RESTARTING ) {
				action.getPlayer ( ).sendMessage ( EnumLanguage.ARENA_MESSAGE_RESTARTING.getAsString ( ) );
			} else if ( arena.getState ( ) == EnumArenaState.STOPPED ) {
				action.getPlayer ( ).sendMessage ( EnumLanguage.ARENA_MESSAGE_STOPPED.getAsString ( ) );
			} else if ( arena.isFull ( ) ) {
				action.getPlayer ( ).sendMessage ( EnumLanguage.ARENA_MESSAGE_FULL.getAsString ( ) );
			} else {
				BattleRoyaleArenaHandler.getInstance ( ).joinArena ( br_player , arena );
			}
		}
		
		action.setClose ( true );
	}
}