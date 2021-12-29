package es.outlook.adriansrj.battleroyale.gui.arena;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.gui.*;
import es.outlook.adriansrj.battleroyale.gui.arena.icon.ArenaSelectorGUIButtonArenaInstance;
import es.outlook.adriansrj.battleroyale.gui.arena.icon.ArenaSelectorGUIButtonLeaveArenaInstance;
import es.outlook.adriansrj.core.menu.ItemMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Arena selector GUI.
 *
 * @author AdrianSR / 02/10/2021 / 06:40 p. m.
 */
public class ArenaSelectorGUIInstance extends GUIInstance {
	
	protected ArenaSelectorGUIInstance ( GUIConfiguration configuration ) {
		super ( configuration );
	}
	
	@Override
	protected synchronized void populatePageContents ( GUIPage page , GUIPageModel model , ItemMenu page_instance ,
			Player player ) {
		BattleRoyaleArena          current = player.getArena ( );
		List < BattleRoyaleArena > arenas  = new ArrayList <> ( BattleRoyaleArenaHandler.getInstance ( ).getArenas ( ) );
		
		// sorted
		arenas.sort ( null );
		
		// the arena the player is currently
		// in will be skipped.
		if ( current != null ) {
			arenas.remove ( current );
		}
		
		for ( int i = 0 ; i < page_instance.getSize ( ).getSize ( ) ; i++ ) {
			GUIIconInstance slot_instance = buildSlot ( page , model , page_instance , i , player );
			
			// arena button
			if ( slot_instance instanceof ArenaSelectorGUIButtonArenaInstance ) {
				BattleRoyaleArena next = arenas.size ( ) > 0 ? arenas.remove ( 0 ) : null;
				
				if ( next != null ) {
					( ( ArenaSelectorGUIButtonArenaInstance ) slot_instance ).setArena ( next );
				} else {
					// we don't want it to display an
					// arena-button that will not actually
					// refer to an arena.
					slot_instance = null;
				}
			}
			
			// leave arena
			if ( slot_instance instanceof ArenaSelectorGUIButtonLeaveArenaInstance
					&& !player.isInArena ( ) ) {
				// we don't want it to display the leave-button
				// if the player is not actually in an arena.
				slot_instance = null;
			}
			
			if ( slot_instance != null ) {
				page_instance.setItem ( i , slot_instance );
			}
		}
	}
}