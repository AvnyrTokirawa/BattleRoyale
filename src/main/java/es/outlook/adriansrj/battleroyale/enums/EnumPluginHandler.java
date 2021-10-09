package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.arena.border.BattleRoyaleArenaBorderHandler;
import es.outlook.adriansrj.battleroyale.arena.sign.BattleRoyaleArenaSignHandler;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldRegistry;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.MinimapHandler;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupHandler;
import es.outlook.adriansrj.battleroyale.bus.BusHandler;
import es.outlook.adriansrj.battleroyale.bus.BusRegistry;
import es.outlook.adriansrj.battleroyale.command.BattleRoyaleCommandHandler;
import es.outlook.adriansrj.battleroyale.compass.CompassBarHandler;
import es.outlook.adriansrj.battleroyale.compatibility.qualityarmory.QualityArmoryCompatibilityHandler;
import es.outlook.adriansrj.battleroyale.configuration.arena.BattleRoyaleArenaSignConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.arena.BattleRoyaleArenaSignContainerHandler;
import es.outlook.adriansrj.battleroyale.configuration.arena.BattleRoyaleArenasConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.bus.BusConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.bus.BusesConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.compass.CompassBarConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.gui.arenaselector.ArenaSelectorGUIConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.gui.settings.SettingsGUIsConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.gui.teamselector.TeamGUIsLanguageConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.lang.BattleRoyaleLanguageConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.lobby.BattleRoyaleLobbyConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.loot.LootConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.main.MainConfigurationHandler;
import es.outlook.adriansrj.battleroyale.configuration.parachute.ParachuteConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.parachute.ParachutesConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.scoreboard.ScoreboardConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.vehicle.BattleRoyaleVehiclesConfigHandler;
import es.outlook.adriansrj.battleroyale.data.DataStorageHandler;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfigurationRegistry;
import es.outlook.adriansrj.battleroyale.game.player.*;
import es.outlook.adriansrj.battleroyale.gui.arena.ArenaSelectorGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setting.SettingsGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setting.bus.BusSettingsGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setting.parachute.ParachuteSettingsGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setting.parachute.ParachuteSettingsMainGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setting.parachute.color.ParachuteColorSettingsGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setup.SetupGUI;
import es.outlook.adriansrj.battleroyale.gui.setup.battlefield.BattlefieldSetupGUI;
import es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session.BattlefieldSetupSessionGUI;
import es.outlook.adriansrj.battleroyale.gui.setup.lobby.LobbyMapSetupGUI;
import es.outlook.adriansrj.battleroyale.gui.spectator.SpectatorGUI;
import es.outlook.adriansrj.battleroyale.gui.team.TeamGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.team.TeamSelectorGUIHandler;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobbyHandler;
import es.outlook.adriansrj.battleroyale.packet.factory.PacketFactoryService;
import es.outlook.adriansrj.battleroyale.packet.reader.PacketReaderService;
import es.outlook.adriansrj.battleroyale.packet.sender.PacketSenderService;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteHandler;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteRegistry;
import es.outlook.adriansrj.battleroyale.placeholder.PlaceholderHandler;
import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNodeRegistry;
import es.outlook.adriansrj.battleroyale.scoreboard.ScoreboardConfigurationRegistry;
import es.outlook.adriansrj.battleroyale.scoreboard.ScoreboardHandler;
import es.outlook.adriansrj.battleroyale.util.PluginUtil;
import es.outlook.adriansrj.battleroyale.vehicle.VehiclesConfigurationRegistry;
import es.outlook.adriansrj.core.handler.PluginHandler;

import java.util.concurrent.Callable;

/**
 * @author AdrianSR / 22/08/2021 / Time: 10:18 p. m.
 */
public enum EnumPluginHandler {
	
	// order is vital
	
	PACKET_FACTORY_SERVICE ( PacketFactoryService.class ),
	PACKET_READER_SERVICE ( PacketReaderService.class ),
	PACKET_SENDER_SERVICE ( PacketSenderService.class ),
	MAIN_CONFIGURATION_HANDLER ( MainConfigurationHandler.class ),
	LANGUAGE_CONFIGURATION_HANDLER ( BattleRoyaleLanguageConfigHandler.class ),
	
	COMMAND_HANDLER ( BattleRoyaleCommandHandler.class ),
	PLACEHOLDER_NODE_REGISTRY ( PlaceholderNodeRegistry.class ),
	PLACEHOLDER_HANDLER ( PlaceholderHandler.class ),
	DATA_STORAGE_HANDLER ( DataStorageHandler.class ),
	PLAYER_HANDLER ( PlayerHandler.class ),
	TEAM_HANDLER ( TeamHandler.class ),
	TEAM_GUIS_LANGUAGE_HANDLER ( TeamGUIsLanguageConfigHandler.class ),
	TEAM_SELECTOR_GUI_HANDLER ( TeamSelectorGUIHandler.class ),
	TEAM_GUI_HANDLER ( TeamGUIHandler.class ),
	
	SPECTATOR_HANDLER ( PlayerSpectatorHandler.class ),
	SPECTATOR_GUI ( SpectatorGUI.class ),
	KNOCK_HANDLER ( PlayerKnockHandler.class ),
	REANIMATION_HANDLER ( PlayerReviveHandler.class ),
	
	SETTINGS_GUIS_CONFIGURATION_HANDLER ( SettingsGUIsConfigHandler.class ),
	SETTINGS_GUI ( SettingsGUIHandler.class ),
	BUS_SETTINGS_GUI ( BusSettingsGUIHandler.class ),
	PARACHUTE_SETTINGS_MAIN_GUI ( ParachuteSettingsMainGUIHandler.class ),
	PARACHUTE_SETTINGS_GUI ( ParachuteSettingsGUIHandler.class ),
	PARACHUTE_COLOR_SETTINGS_GUI ( ParachuteColorSettingsGUIHandler.class ),
	
	SETUP_HANDLER ( BattlefieldSetupHandler.class ),
	SETUP_GUI_HANDLER ( SetupGUI.class ),
	LOBBY_MAP_SETUP_GUI ( LobbyMapSetupGUI.class ),
	BATTLEFIELD_SETUP_GUI ( BattlefieldSetupGUI.class ),
	BATTLEFIELD_SETUP_SESSION_GUI ( BattlefieldSetupSessionGUI.class ),
	
	LOBBY_CONFIGURATION_HANDLER ( BattleRoyaleLobbyConfigHandler.class ),
	LOBBY_HANDLER ( BattleRoyaleLobbyHandler.class ),
	
	LOOT_CONFIGURATION_REGISTRY ( LootConfigurationRegistry.class ),
	LOOT_CONFIGURATION_HANDLER ( LootConfigHandler.class ),
	
	VEHICLES_CONFIGURATION_REGISTRY ( VehiclesConfigurationRegistry.class ),
	VEHICLES_CONFIGURATION_HANDLER ( BattleRoyaleVehiclesConfigHandler.class ),
	
	BATTLEFIELD_HANDLER ( BattlefieldRegistry.class ),
	
	ARENA_HANDLER ( BattleRoyaleArenaHandler.class ),
	ARENA_CONFIGURATION_HANDLER ( BattleRoyaleArenasConfigHandler.class ),
	ARENA_GUI_HANDLER ( ArenaSelectorGUIHandler.class ),
	ARENA_GUI_CONFIGURATION_HANDLER ( ArenaSelectorGUIConfigHandler.class ),
	ARENA_SIGN_HANDLER ( BattleRoyaleArenaSignHandler.class ),
	ARENA_SIGN_CONFIGURATION_HANDLER ( BattleRoyaleArenaSignConfigHandler.class ),
	ARENA_SIGN_CONTAINER_HANDLER ( BattleRoyaleArenaSignContainerHandler.class ),
	ARENA_BORDER_HANDLER ( BattleRoyaleArenaBorderHandler.class ),
	
	MINIMAP_HANDLER ( MinimapHandler.class ),
	
	BUS_HANDLER ( BusHandler.class ),
	BUS_REGISTRY ( BusRegistry.class ),
	BUS_CONFIGURATION_HANDLER ( BusConfigHandler.class ),
	BUSES_CONFIGURATION_HANDLER ( BusesConfigHandler.class ),
	
	PARACHUTE_HANDLER ( ParachuteHandler.class ),
	PARACHUTE_REGISTRY ( ParachuteRegistry.class ),
	PARACHUTE_CONFIGURATION_HANDLER ( ParachuteConfigHandler.class ),
	PARACHUTES_CONFIGURATION_HANDLER ( ParachutesConfigHandler.class ),
	
	COMPASS_HANDLER ( CompassBarHandler.class ),
	COMPASS_CONFIGURATION_HANDLER ( CompassBarConfigHandler.class ),
	
	SCOREBOARD_CONFIGURATION_REGISTRY ( ScoreboardConfigurationRegistry.class ),
	SCOREBOARD_HANDLER ( ScoreboardHandler.class ),
	SCOREBOARD_CONFIGURATION_HANDLER ( ScoreboardConfigHandler.class ),
	
	QUALITY_ARMORY_COMPATIBILITY_HANDLER (
			QualityArmoryCompatibilityHandler.class , PluginUtil :: isQualityArmoryEnabled ),
	
	;
	
	private final Class < ? extends PluginHandler > clazz;
	private final Callable < Boolean >              init_flag;
	
	private EnumPluginHandler ( Class < ? extends PluginHandler > clazz , Callable < Boolean > init_flag ) {
		this.clazz     = clazz;
		this.init_flag = init_flag;
	}
	
	private EnumPluginHandler ( Class < ? extends PluginHandler > clazz ) {
		this ( clazz , ( ) -> {
			return Boolean.TRUE; // no special checks are required by default.
		} );
	}
	
	public Class < ? extends PluginHandler > getHandlerClass ( ) {
		return clazz;
	}
	
	public boolean canInitialize ( ) {
		try {
			return init_flag.call ( );
		} catch ( Exception ex ) {
			ex.printStackTrace ( );
			return false;
		}
	}
}