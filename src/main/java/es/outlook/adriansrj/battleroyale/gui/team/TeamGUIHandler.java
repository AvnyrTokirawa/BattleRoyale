package es.outlook.adriansrj.battleroyale.gui.team;

import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.enums.EnumTeamGUIsLanguage;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.Team;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.Item;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.menu.custom.book.BookItemMenu;
import es.outlook.adriansrj.core.menu.item.voidaction.VoidActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Team GUI.
 *
 * @author AdrianSR / 02/10/2021 / 07:28 a. m.
 */
public final class TeamGUIHandler extends PluginHandler {
	
	public static TeamGUIHandler getInstance ( ) {
		return getPluginHandler ( TeamGUIHandler.class );
	}
	
	private final Map < UUID, BookItemMenu > handle_map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public TeamGUIHandler ( BattleRoyale plugin ) {
		super ( plugin );
		
		// this task is responsible for updating
		// the team selector guis so players can
		// actually see changes in real-time.
		Bukkit.getScheduler ( ).runTaskTimerAsynchronously ( plugin , ( ) -> {
			for ( Map.Entry < UUID, BookItemMenu > next : handle_map.entrySet ( ) ) {
				org.bukkit.entity.Player player = Bukkit.getPlayer ( next.getKey ( ) );
				
				if ( player != null && player.isOnline ( )
						&& Player.getPlayer ( player ).isInArena ( ) ) {
					BookItemMenu handle = next.getValue ( );
					
					if ( handle.isMenuOpen ( player ) ) {
						update ( player );
					}
				}
			}
		} , 5L , 5L );
	}
	
	public void open ( org.bukkit.entity.Player player ) {
		teamCheck ( Player.getPlayer ( player ) );
		build ( player ).open ( player );
	}
	
	public void open ( Player player ) {
		player.getBukkitPlayerOptional ( ).ifPresent ( this :: open );
	}
	
	public void update ( org.bukkit.entity.Player player ) {
		teamCheck ( Player.getPlayer ( player ) );
		build ( player ).update ( player );
	}
	
	public void update ( Player player ) {
		player.getBukkitPlayerOptional ( ).ifPresent ( this :: update );
	}
	
	private ItemMenu build ( org.bukkit.entity.Player player ) {
		BookItemMenu handle = this.handle_map.computeIfAbsent ( player.getUniqueId ( ) , k -> {
			BookItemMenu result = new BookItemMenu (
					EnumTeamGUIsLanguage.MY_TEAM_TITLE.getAsString ( ) , ItemMenuSize.FOUR_LINE );
			
			// must register listener
			result.registerListener ( BattleRoyale.getInstance ( ) );
			return result;
		} );
		
		handle.clear ( );
		
		// then building
		Player br_player = Player.getPlayer ( player );
		Team   team      = br_player.getTeam ( );
		
		// inserting teammates
		// TODO: set actual player skin to player head
		team.getPlayers ( ).forEach ( member -> handle.addItem (
				new VoidActionItem (
						ChatColor.BLUE + ( Objects.equals ( player.getUniqueId ( ) , member.getUniqueId ( ) )
								? EnumLanguage.YOU_WORD.getAsStringStripColors ( ) :
								member.getName ( ) ) ,
						UniversalMaterial.PLAYER_HEAD_ITEM.getItemStack ( ) ) ) );
		
		// see all team button
		handle.setBarButton ( 0 , new Item (
				EnumTeamGUIsLanguage.BUTTON_SEE_ALL_TEAMS.getAsString ( ) ,
				UniversalMaterial.PAINTING.getItemStack ( ) ) {
			@Override
			public void onClick ( ItemClickAction action ) {
				TeamSelectorGUIHandler.getInstance ( ).open ( action.getPlayer ( ) );
			}
		} );
		
		// leave team button
		handle.setBarButton ( 8 , new Item (
				EnumTeamGUIsLanguage.BUTTON_LEAVE_TEAM.getAsString ( ) ,
				UniversalMaterial.BLACK_WOOL.getItemStack ( ) ) {
			@Override
			public void onClick ( ItemClickAction action ) {
				Player br_player = Player.getPlayer ( action.getPlayer ( ) );
				Team   team      = br_player.getTeam ( );
				
				if ( team != null ) {
					br_player.leaveTeam ( );
				}
				
				// redirecting to the selector
				TeamSelectorGUIHandler.getInstance ( ).open ( action.getPlayer ( ) );
			}
		} );
		
		return handle;
	}
	
	private void teamCheck ( Player player ) {
		if ( player.getTeam ( ) == null ) {
			throw new UnsupportedOperationException ( "player must be in a team" );
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
