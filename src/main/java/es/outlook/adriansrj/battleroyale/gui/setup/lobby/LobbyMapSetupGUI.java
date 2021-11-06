package es.outlook.adriansrj.battleroyale.gui.setup.lobby;

import es.outlook.adriansrj.battleroyale.enums.EnumInternalLanguage;
import es.outlook.adriansrj.battleroyale.enums.EnumLobbyConfiguration;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobbyHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.item.action.ActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.world.GameRuleType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * @author AdrianSR / 28/08/2021 / 03:50 p. m.
 */
public final class LobbyMapSetupGUI extends PluginHandler {
	
	public static LobbyMapSetupGUI getInstance ( ) {
		return getPluginHandler ( LobbyMapSetupGUI.class );
	}
	
	protected final ItemMenu handle;
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public LobbyMapSetupGUI ( BattleRoyale plugin ) {
		super ( plugin );
		
		this.handle = new ItemMenu ( ChatColor.BLACK + "Lobby Setup" , ItemMenuSize.THREE_LINE );
		this.handle.registerListener ( plugin );
	}
	
	public void open ( Player player ) {
		build ( );
		
		handle.open ( player );
	}
	
	public void refresh ( Player player ) {
		build ( );
		
		handle.update ( player );
	}
	
	private void build ( ) {
		this.handle.clear ( );
		
		// spawn location
		this.handle.setItem ( 10 , new ActionItem (
				ChatColor.GOLD + "Set Spawn" ,
				UniversalMaterial.WHITE_BED.getItemStack ( ) ,
				Arrays.asList (
						"" ,
						ChatColor.GRAY + "Click to set here" ,
						ChatColor.GRAY + "the spawn of the" ,
						ChatColor.GRAY + "lobby."
				) ).addAction ( action -> {
			Player   player   = action.getPlayer ( );
			Location location = player.getLocation ( );
			
			EnumLobbyConfiguration.WORLD_NAME.setValue ( player.getWorld ( ).getName ( ) );
			EnumLobbyConfiguration.SPAWN_X.setValue ( location.getX ( ) );
			EnumLobbyConfiguration.SPAWN_Y.setValue ( location.getY ( ) );
			EnumLobbyConfiguration.SPAWN_Z.setValue ( location.getZ ( ) );
			EnumLobbyConfiguration.SPAWN_YAW.setValue ( location.getYaw ( ) );
			EnumLobbyConfiguration.SPAWN_PITCH.setValue ( location.getPitch ( ) );
			EnumLobbyConfiguration.SPAWN_ENABLE.setValue ( true );
			
			EnumLobbyConfiguration.saveConfiguration ( );
			
			// letting player know
			action.getPlayer ( ).sendMessage ( String.format (
					EnumInternalLanguage.LOBBY_SETUP_SPAWN_SET.toString ( ) ,
					location.getBlockX ( ) + ", " + location.getBlockY ( ) + ", " + location.getBlockZ ( ) ) );
		} ) );
		
		// damage
		this.handle.setItem ( 11 , new ActionItem (
				EnumLobbyConfiguration.WORLD_DISABLE_DAMAGE.getAsBoolean ( ) ?
						ChatColor.GREEN + "Damage disabled" :
						ChatColor.YELLOW + "Damage enabled" ,
				
				EnumLobbyConfiguration.WORLD_DISABLE_DAMAGE.getAsBoolean ( ) ?
						UniversalMaterial.CHAINMAIL_CHESTPLATE.getItemStack ( ) :
						UniversalMaterial.SKELETON_SKULL_ITEM.getItemStack ( ) ,
				
				EnumLobbyConfiguration.WORLD_DISABLE_DAMAGE.getAsBoolean ( ) ?
						
						Arrays.asList (
								"" ,
								ChatColor.GRAY + "Click to enable" ,
								ChatColor.GRAY + "the damage in" ,
								ChatColor.GRAY + "the lobby." ) :
						
						Arrays.asList (
								"" ,
								ChatColor.GRAY + "Click to disable" ,
								ChatColor.GRAY + "the damage in" ,
								ChatColor.GRAY + "the lobby." ) ).addAction ( action -> {
			boolean flag = !EnumLobbyConfiguration.WORLD_DISABLE_DAMAGE.getAsBoolean ( );
			
			EnumLobbyConfiguration.WORLD_DISABLE_DAMAGE.setValue ( flag );
			EnumLobbyConfiguration.saveConfiguration ( );
			
			// letting player know
			if ( flag ) {
				action.getPlayer ( ).sendMessage ( EnumInternalLanguage.LOBBY_SETUP_DAMAGE_ENABLED.toString ( ) );
			} else {
				action.getPlayer ( ).sendMessage ( EnumInternalLanguage.LOBBY_SETUP_DAMAGE_DISABLED.toString ( ) );
			}
			
			build ( );
			action.setUpdate ( true );
		} ) );
		
		// hunger
		this.handle.setItem ( 12 , new ActionItem (
				EnumLobbyConfiguration.WORLD_DISABLE_HUNGER.getAsBoolean ( ) ?
						ChatColor.GREEN + "Hunger disabled" :
						ChatColor.YELLOW + "Hunger enabled" ,
				
				EnumLobbyConfiguration.WORLD_DISABLE_HUNGER.getAsBoolean ( ) ?
						UniversalMaterial.BREAD.getItemStack ( ) :
						UniversalMaterial.WHEAT_SEEDS.getItemStack ( ) ,
				
				EnumLobbyConfiguration.WORLD_DISABLE_HUNGER.getAsBoolean ( ) ?
						Arrays.asList (
								"" ,
								ChatColor.GRAY + "Click to enable" ,
								ChatColor.GRAY + "the hunger in" ,
								ChatColor.GRAY + "the lobby." ) :
						Arrays.asList (
								"" ,
								ChatColor.GRAY + "Click to disable" ,
								ChatColor.GRAY + "the hunger in" ,
								ChatColor.GRAY + "the lobby." ) ).addAction ( action -> {
			boolean flag = !EnumLobbyConfiguration.WORLD_DISABLE_HUNGER.getAsBoolean ( );
			
			GameRuleType.DISABLE_HUNGER.apply (
					BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( ).getWorld ( ) , !flag );
			EnumLobbyConfiguration.WORLD_DISABLE_HUNGER.setValue ( flag );
			EnumLobbyConfiguration.saveConfiguration ( );
			
			// letting player know
			if ( flag ) {
				action.getPlayer ( ).sendMessage ( EnumInternalLanguage.LOBBY_SETUP_HUNGER_ENABLED.toString ( ) );
			} else {
				action.getPlayer ( ).sendMessage ( EnumInternalLanguage.LOBBY_SETUP_HUNGER_DISABLED.toString ( ) );
			}
			
			build ( );
			action.setUpdate ( true );
		} ) );
		
		// mob spawning
		this.handle.setItem ( 13 , new ActionItem (
				EnumLobbyConfiguration.WORLD_DISABLE_MOBS.getAsBoolean ( ) ?
						ChatColor.GREEN + "Mobs disabled" :
						ChatColor.YELLOW + "Mobs enabled" ,
				
				EnumLobbyConfiguration.WORLD_DISABLE_MOBS.getAsBoolean ( ) ?
						UniversalMaterial.FEATHER.getItemStack ( ) :
						UniversalMaterial.CREEPER_HEAD.getItemStack ( ) ,
				
				EnumLobbyConfiguration.WORLD_DISABLE_MOBS.getAsBoolean ( ) ?
						
						Arrays.asList (
								"" ,
								ChatColor.GRAY + "Click to enable" ,
								ChatColor.GRAY + "the mobs in" ,
								ChatColor.GRAY + "the lobby." ) :
						
						Arrays.asList (
								"" ,
								ChatColor.GRAY + "Click to disable" ,
								ChatColor.GRAY + "the mobs in" ,
								ChatColor.GRAY + "the lobby." ) ).addAction ( action -> {
			boolean flag = !EnumLobbyConfiguration.WORLD_DISABLE_MOBS.getAsBoolean ( );
			
			GameRuleType.MOB_SPAWNING.apply (
					BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( ).getWorld ( ) , flag );
			EnumLobbyConfiguration.WORLD_DISABLE_MOBS.setValue ( flag );
			EnumLobbyConfiguration.saveConfiguration ( );
			
			// letting player know
			if ( flag ) {
				action.getPlayer ( ).sendMessage ( EnumInternalLanguage.LOBBY_SETUP_MOBS_ENABLED.toString ( ) );
			} else {
				action.getPlayer ( ).sendMessage ( EnumInternalLanguage.LOBBY_SETUP_MOBS_DISABLED.toString ( ) );
			}
			
			build ( );
			action.setUpdate ( true );
		} ) );
		
		// join
		this.handle.setItem ( 14 , new ActionItem (
				EnumLobbyConfiguration.SPAWN_JOIN.getAsBoolean ( ) ?
						ChatColor.GREEN + "Send to spawn when joining enabled" :
						ChatColor.YELLOW + "Send to spawn when joining disabled" ,
				
				EnumLobbyConfiguration.SPAWN_JOIN.getAsBoolean ( ) ?
						UniversalMaterial.CHEST_MINECART.getItemStack ( ) :
						UniversalMaterial.MINECART.getItemStack ( ) ,
				
				EnumLobbyConfiguration.SPAWN_JOIN.getAsBoolean ( ) ?
						
						Arrays.asList (
								"" ,
								ChatColor.GRAY + "The players will be" ,
								ChatColor.GRAY + "sent to the spawn" ,
								ChatColor.GRAY + "when joining the server." ,
								"" ,
								ChatColor.GRAY + "Click to disable." ) :
						
						Arrays.asList (
								"" ,
								ChatColor.GRAY + "The players will not be" ,
								ChatColor.GRAY + "sent to the spawn when" ,
								ChatColor.GRAY + "joining the server." ,
								"" ,
								ChatColor.GRAY + "Click to enable." ) ).addAction ( action -> {
			boolean flag = !EnumLobbyConfiguration.SPAWN_JOIN.getAsBoolean ( );
			
			EnumLobbyConfiguration.SPAWN_JOIN.setValue ( flag );
			EnumLobbyConfiguration.saveConfiguration ( );
			
			// letting player know
			if ( flag ) {
				action.getPlayer ( ).sendMessage ( EnumInternalLanguage.LOBBY_SETUP_SPAWN_JOIN_ENABLED.toString ( ) );
			} else {
				action.getPlayer ( ).sendMessage ( EnumInternalLanguage.LOBBY_SETUP_SPAWN_JOIN_DISABLED.toString ( ) );
			}
			
			build ( );
			action.setUpdate ( true );
		} ) );
		
		// void
		this.handle.setItem ( 15 , new ActionItem (
				EnumLobbyConfiguration.SPAWN_VOID.getAsBoolean ( ) ?
						ChatColor.GREEN + "Send to spawn when falling into void enabled" :
						ChatColor.YELLOW + "Send to spawn when falling into void disabled" ,
				
				EnumLobbyConfiguration.SPAWN_VOID.getAsBoolean ( ) ?
						UniversalMaterial.STRING.getItemStack ( ) :
						UniversalMaterial.HOPPER.getItemStack ( ) ,
				
				EnumLobbyConfiguration.SPAWN_VOID.getAsBoolean ( ) ?
						
						Arrays.asList (
								"" ,
								ChatColor.GRAY + "The players will be" ,
								ChatColor.GRAY + "sent back to the spawn" ,
								ChatColor.GRAY + "when falling into the void." ,
								"" ,
								ChatColor.GRAY + "Click to disable." ) :
						
						Arrays.asList (
								"" ,
								ChatColor.GRAY + "The players will not be" ,
								ChatColor.GRAY + "sent back to the spawn" ,
								ChatColor.GRAY + "when falling into the void." ,
								"" ,
								ChatColor.GRAY + "Click to enable." ) ).addAction ( action -> {
			boolean flag = !EnumLobbyConfiguration.SPAWN_VOID.getAsBoolean ( );
			
			EnumLobbyConfiguration.SPAWN_VOID.setValue ( flag );
			EnumLobbyConfiguration.saveConfiguration ( );
			
			// letting player know
			if ( flag ) {
				action.getPlayer ( ).sendMessage ( EnumInternalLanguage.LOBBY_SETUP_SPAWN_VOID_ENABLED.toString ( ) );
			} else {
				action.getPlayer ( ).sendMessage ( EnumInternalLanguage.LOBBY_SETUP_SPAWN_VOID_DISABLED.toString ( ) );
			}
			
			build ( );
			action.setUpdate ( true );
		} ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}