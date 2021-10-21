package es.outlook.adriansrj.battleroyale.gui.team;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.enums.EnumTeamGUIsLanguage;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.Team;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.Item;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.menu.custom.book.BookItemMenu;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Team selector GUI.
 *
 * @author AdrianSR / 07/09/2021 / 01:43 p. m.
 */
public final class TeamSelectorGUIHandler extends PluginHandler {
	
	public static TeamSelectorGUIHandler getInstance ( ) {
		return getPluginHandler ( TeamSelectorGUIHandler.class );
	}
	
	private final Map < UUID, BookItemMenu > handle_map = new HashMap <> ( );
	private       ItemMenu                   create_handle;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public TeamSelectorGUIHandler ( BattleRoyale plugin ) {
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
		this.arenaCheck ( player );
		this.build ( player ).open ( player );
	}
	
	public void open ( Player player ) {
		player.getBukkitPlayerOptional ( ).ifPresent ( this :: open );
	}
	
	public void update ( org.bukkit.entity.Player player ) {
		this.arenaCheck ( player );
		this.build ( player ).update ( player );
	}
	
	public void update ( Player player ) {
		player.getBukkitPlayerOptional ( ).ifPresent ( this :: update );
	}
	
	private ItemMenu build ( org.bukkit.entity.Player player ) {
		// create team gui
		if ( create_handle == null ) {
			this.create_handle = new ItemMenu ( EnumTeamGUIsLanguage.SELECTOR_TITLE.getAsString ( ) ,
												ItemMenuSize.FIVE_LINE );
			this.create_handle.registerListener ( BattleRoyale.getInstance ( ) );
		}
		
		// actual selector
		BookItemMenu handle = this.handle_map.computeIfAbsent ( player.getUniqueId ( ) , k -> {
			BookItemMenu result = new BookItemMenu (
					EnumTeamGUIsLanguage.SELECTOR_TITLE.getAsString ( ) , ItemMenuSize.FIVE_LINE );
			
			// must register listener
			result.registerListener ( BattleRoyale.getInstance ( ) );
			return result;
		} );
		
		handle.clear ( );
		create_handle.clear ( );
		
		// then building
		Player            br_player = Player.getPlayer ( player );
		BattleRoyaleArena arena     = br_player.getArena ( );
		BattleRoyaleMode  mode      = arena.getMode ( );
		
		// my team button
		if ( br_player.getTeam ( ) != null ) {
			handle.setBarButton ( 0 , new ActionItem (
					EnumTeamGUIsLanguage.BUTTON_MY_TEAM.getAsString ( ) ,
					UniversalMaterial.IRON_CHESTPLATE.getItemStack ( )
			).addAction ( // opens player's team menu
						  action -> TeamGUIHandler.getInstance ( ).open ( action.getPlayer ( ) ) ) );
		}
		
		// create button
		// player must not be on a team
		// to create a new one
		if ( br_player.getTeam ( ) == null && mode.isTeamCreationEnabled ( ) &&
				( mode.getMaxTeams ( ) <= 0 || arena.getTeamRegistry ( ).getCount ( ) < mode.getMaxTeams ( ) ) ) {
			ActionItem create_button = new ActionItem (
					EnumTeamGUIsLanguage.BUTTON_CREATE_TEAM.getAsString ( ) ,
					UniversalMaterial.DIAMOND_CHESTPLATE.getItemStack ( ) ,
					EnumTeamGUIsLanguage.BUTTON_CREATE_TEAM_DESCRIPTION.getAsStringList ( )
			).addAction ( action -> {
				Team team = arena.getTeamRegistry ( ).createAndRegisterTeam ( );
				
				if ( team != null ) {
					br_player.setTeam ( team );
					
					action.getPlayer ( ).sendMessage (
							EnumTeamGUIsLanguage.MESSAGE_TEAM_CREATED.getAsString ( ) );
					
					// redirecting to team menu
					TeamGUIHandler.getInstance ( ).open ( player );
				} else {
					action.getPlayer ( ).sendMessage (
							EnumTeamGUIsLanguage.MESSAGE_CANNOT_CREATE_TEAM.getAsString ( ) );
				}
			} );
			
			// this button will be displayed at the
			// center of the gui if there are no teams,
			// so it will be obvious.
			if ( arena.getTeamRegistry ( ).isEmpty ( ) ) {
				this.create_handle.setItem ( 22 , create_button );
				
				// this create-gui will be open
				// instead, so it will be easier to understand.
				return this.create_handle;
			} else {
				handle.setBarButton ( 1 , create_button );
			}
		}
		
		// inserting teams.
		List < Team > teams = new ArrayList <> (
				br_player.getArena ( ).getTeamRegistry ( ).getHandle ( ) );
		
		// team player will be excluded as it
		// is shown in the button "My Team".
		teams.remove ( br_player.getTeam ( ) );
		
		// the available teams will be inserted first,
		// and then the teams that are full.
		teams.sort ( ( ta , tb ) -> {
			boolean a = available ( ta , mode );
			boolean b = available ( tb , mode );
			
			if ( a != b ) {
				if ( a ) {
					return -1;
				} else {
					return 1;
				}
			} else {
				return 0;
			}
		} );
		
		// rest of the teams
		for ( int i = 0 ; i < teams.size ( ) ; i++ ) {
			handle.addItem ( new TeamItem ( teams.get ( i ) , i + 1 ) );
		}
		
		return handle;
	}
	
	/**
	 * @author AdrianSR / 02/10/2021 / 08:25 a. m.
	 */
	private static class TeamItem extends Item {
		
		private final Team team;
		
		public TeamItem ( Team team , int number ) {
			super ( String.format ( EnumTeamGUIsLanguage.BUTTON_JOIN_TEAM.getAsString ( ) , number ) ,
					UniversalMaterial.CHAINMAIL_CHESTPLATE.getItemStack ( ) ,
					EnumTeamGUIsLanguage.BUTTON_JOIN_TEAM_DESCRIPTION.getAsStringList ( ) );
			this.team = team;
		}
		
		@Override
		public void onClick ( ItemClickAction action ) {
			if ( team.getArena ( ).getState ( ) == EnumArenaState.WAITING ) {
				if ( team.getPlayers ( ).size ( ) < team.getArena ( ).getMode ( ).getMaxPlayersPerTeam ( ) ) {
					Player.getPlayer ( action.getPlayer ( ) ).setTeam ( team );
					
					// redirecting to team gui
					TeamGUIHandler.getInstance ( ).open ( action.getPlayer ( ) );
				} else {
					action.getPlayer ( ).sendMessage (
							EnumTeamGUIsLanguage.MESSAGE_TEAM_FULL.getAsString ( ) );
				}
			} else {
				action.getPlayer ( ).sendMessage (
						EnumTeamGUIsLanguage.MESSAGE_CANNOT_JOIN_TEAM.getAsString ( ) );
			}
		}
		
		@Override
		public ItemStack getDisplayIcon ( ) {
			// updating description
			List < String > description = new ArrayList <> ( );
			
			if ( team.getPlayers ( ).size ( ) < team.getArena ( ).getMode ( ).getMaxPlayersPerTeam ( ) ) {
				// when available
				description.addAll ( EnumTeamGUIsLanguage.BUTTON_JOIN_TEAM_DESCRIPTION.getAsStringList ( ) );
			} else {
				// when not-available
				description.addAll (
						EnumTeamGUIsLanguage.BUTTON_JOIN_TEAM_FULL_DESCRIPTION.getAsStringList ( ) );
			}
			
			// displaying members
			if ( team.getPlayers ( ).size ( ) > 0 ) {
				description.addAll ( buildMemberList ( team , null ) );
			} else {
				description.add ( EnumTeamGUIsLanguage.BUTTON_JOIN_TEAM_NO_MEMBERS.getAsString ( ) );
			}
			
			setLore ( description );
			return super.getDisplayIcon ( );
		}
	}
	
	private static List < String > buildMemberList ( Team team , org.bukkit.entity.Player player ) {
		List < String > result = new ArrayList <> ( );
		String          format = EnumTeamGUIsLanguage.BUTTON_JOIN_TEAM_MEMBERS.getAsString ( );
		String[]        split  = format.split ( "\n" );
		
		for ( String line : split ) {
			// members line placeholder found
			if ( line.contains ( "%s" ) ) {
				String entry_format = EnumTeamGUIsLanguage.BUTTON_JOIN_TEAM_MEMBERS_ENTRY.getAsString ( );
				
				for ( Player member : team.getPlayers ( ) ) {
					if ( player != null && Objects.equals ( member.getUniqueId ( ) , player.getUniqueId ( ) ) ) {
						result.add ( String.format (
								entry_format , EnumLanguage.YOU_WORD.getAsStringStripColors ( ) ) );
					} else {
						result.add ( String.format (
								entry_format , member.getName ( ) ) );
					}
				}
			} else {
				result.add ( line );
			}
		}
		
		return result;
	}
	
	private boolean available ( Team team , BattleRoyaleMode mode ) {
		return team.getPlayers ( ).size ( ) < mode.getMaxPlayersPerTeam ( );
	}
	
	private void arenaCheck ( org.bukkit.entity.Player player ) {
		Player            br_player = Player.getPlayer ( player );
		BattleRoyaleArena arena     = br_player.getArena ( );
		
		if ( arena == null ) {
			throw new UnsupportedOperationException (
					"player must be in an arena in order to join a team" );
		} else if ( arena.getState ( ) == EnumArenaState.RUNNING ) {
			throw new UnsupportedOperationException (
					"player cannot join a team if the arena is already running" );
		} else if ( !arena.getMode ( ).isTeamSelectionEnabled ( ) ) {
			throw new UnsupportedOperationException (
					"team selection is disabled in the current arena" );
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}