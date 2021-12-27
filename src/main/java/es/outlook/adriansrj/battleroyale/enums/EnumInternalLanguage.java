package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.battlefield.setup.tool.BattlefieldSetupToolPrompt;
import es.outlook.adriansrj.core.util.StringUtil;
import org.bukkit.ChatColor;

/**
 * Internal language.
 *
 * @author AdrianSR / 29/08/2021 / 12:26 p. m.
 */
public enum EnumInternalLanguage {
	
	LOBBY_SETUP_SPAWN_SET ( ChatColor.GREEN + "Lobby spawn set at " + ChatColor.GOLD + "%s." ),
	
	LOBBY_SETUP_DAMAGE_ENABLED ( ChatColor.YELLOW + "Damage in lobby enabled. This is not recommended" +
										 " as players will be vulnerable in the lobby." ),
	LOBBY_SETUP_DAMAGE_DISABLED ( ChatColor.GREEN + "Damage in lobby disabled." ),
	
	LOBBY_SETUP_HUNGER_ENABLED ( ChatColor.YELLOW + "Hunger in lobby enabled. This is not recommended" +
										 " as players will be hungry in the lobby." ),
	LOBBY_SETUP_HUNGER_DISABLED ( ChatColor.GREEN + "Hunger in lobby disabled." ),
	
	LOBBY_SETUP_MOBS_ENABLED ( ChatColor.YELLOW + "Mobs in lobby enabled. This is not recommended" +
									   " as players can be attacked by mobs in the lobby." ),
	LOBBY_SETUP_MOBS_DISABLED ( ChatColor.GREEN + "Mobs in lobby disabled." ),
	
	LOBBY_SETUP_SPAWN_JOIN_ENABLED ( ChatColor.GREEN + "Players will be sent to the spawn when joining the server." ),
	LOBBY_SETUP_SPAWN_JOIN_DISABLED (
			ChatColor.YELLOW + "Players will not be sent to the spawn when joining the server." ),
	
	LOBBY_SETUP_SPAWN_VOID_ENABLED (
			ChatColor.GREEN + "Players will be sent back to the spawn when falling into the void." ),
	LOBBY_SETUP_SPAWN_VOID_DISABLED (
			ChatColor.YELLOW + "Players will not be sent back to the spawn when falling into the void." ),
	
	TOOL_BUSY ( ChatColor.RED + "It seems that you are already working with another tool. " +
						"Please finish working with that tool, or cancel it before you start" +
						" working with any other tools!" ),
	
	TOOL_CANCELLED_MESSAGE ( ChatColor.YELLOW + "Tool cancelled!" ),
	TOOL_FINISHED_MESSAGE ( ChatColor.GREEN + "Tool finished!" ),
	
	TOOL_PROMPT_BUSY ( ChatColor.RED + "You're not able to do this right now!" ),
	TOOL_PROMPT_CANCEL ( ChatColor.YELLOW + "Enter " + ChatColor.GOLD + "'"
								 + BattlefieldSetupToolPrompt.CANCEL_KEYWORDS[ 0 ] + "' to cancel." ),
	TOOL_PROMPT_INVALID_INPUT ( ChatColor.RED + "Please enter a valid answer/data!" ),
	TOOL_PROMPT_BATTLEFIELD_NAME ( ChatColor.GOLD + "Please enter the name for this battlefield." ),
	TOOL_PROMPT_BATTLEFIELD_NAME_USED ( ChatColor.RED + "Another battlefield with the same name already exists!" ),
	TOOL_PROMPT_BATTLEFIELD_NAME_SET ( ChatColor.GREEN + "The name of this battlefield is now "
											   + ChatColor.GOLD + "%s" ),
	TOOL_PROMPT_BATTLEFIELD_BORDER_SET ( ChatColor.GREEN + "Battlefield border resizing succession set successfully!" ),
	
	TOOL_BOUNDS_MINIMAP_WAIT ( ChatColor.GOLD + "* Generating minimap..." ),
	TOOL_BOUNDS_SCHEMATIC_WAIT ( ChatColor.GOLD + "* Generating schematic..." ),
	TOOL_BOUNDS_MINIMAP_SET ( ChatColor.GREEN + "Minimap generated successfully!" ),
	TOOL_BOUNDS_SCHEMATIC_SET ( ChatColor.GREEN + "Schematic generated successfully!" ),
	
	PARACHUTE_CREATOR_PROMPT ( ChatColor.GOLD + "Please enter the name for the new parachute." ),
	PARACHUTE_CREATOR_PROMPT_NAME_USED ( ChatColor.RED + "Another parachute with the same name already exists!" ),
	PARACHUTE_CREATOR_PROMPT_MUST_BE_PLAYER ( ChatColor.RED + "Must be an online player." ),
	
	;
	
	private final String text;
	
	EnumInternalLanguage ( String text ) {
		this.text = text;
	}
	
	@Override
	public String toString ( ) {
		return StringUtil.translateAlternateColorCodes ( text );
	}
}
