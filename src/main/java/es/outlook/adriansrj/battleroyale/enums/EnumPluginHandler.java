package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.arena.airsupply.AirSupplyGeneratorHandler;
import es.outlook.adriansrj.battleroyale.arena.autostarter.AutoStarterHandler;
import es.outlook.adriansrj.battleroyale.arena.bombing.BombingZoneGeneratorHandler;
import es.outlook.adriansrj.battleroyale.arena.border.BattleRoyaleArenaBorderHandler;
import es.outlook.adriansrj.battleroyale.arena.drop.ItemDropHandler;
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
import es.outlook.adriansrj.battleroyale.configuration.gui.shop.ShopGUIsConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.gui.teamselector.TeamGUIsLanguageConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.lang.BattleRoyaleLanguageConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.lobby.BattleRoyaleLobbyConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.loot.LootConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.main.MainConfigurationHandler;
import es.outlook.adriansrj.battleroyale.configuration.parachute.ParachuteConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.parachute.ParachutesConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.parachute.color.ParachuteColorConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.scoreboard.ScoreboardConfigHandler;
import es.outlook.adriansrj.battleroyale.configuration.vehicle.BattleRoyaleVehiclesConfigHandler;
import es.outlook.adriansrj.battleroyale.data.DataStorageHandler;
import es.outlook.adriansrj.battleroyale.game.item.BattleRoyaleItemHandler;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfigurationRegistry;
import es.outlook.adriansrj.battleroyale.game.player.*;
import es.outlook.adriansrj.battleroyale.gui.arena.ArenaSelectorGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.parachute.ParachuteCreatorGUI;
import es.outlook.adriansrj.battleroyale.gui.setting.SettingsGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setting.bus.BusSettingsGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setting.parachute.ParachuteSettingsGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setting.parachute.ParachuteSettingsMainGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setting.parachute.color.ParachuteColorSettingsGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setup.SetupGUI;
import es.outlook.adriansrj.battleroyale.gui.setup.battlefield.BattlefieldSetupGUI;
import es.outlook.adriansrj.battleroyale.gui.setup.battlefield.session.BattlefieldSetupSessionGUI;
import es.outlook.adriansrj.battleroyale.gui.setup.lobby.LobbyMapSetupGUI;
import es.outlook.adriansrj.battleroyale.gui.shop.ShopConfirmationGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.shop.ShopGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.shop.bus.BusShopGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.shop.parachute.ParachuteShopGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.shop.parachute.color.ParachuteColorShopGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.spectator.SpectatorGUI;
import es.outlook.adriansrj.battleroyale.gui.team.TeamGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.team.TeamSelectorGUIHandler;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobbyHandler;
import es.outlook.adriansrj.battleroyale.mode.RunModeHandler;
import es.outlook.adriansrj.battleroyale.packet.factory.PacketFactoryService;
import es.outlook.adriansrj.battleroyale.packet.reader.PacketReaderService;
import es.outlook.adriansrj.battleroyale.packet.sender.PacketSenderService;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteHandler;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteRegistry;
import es.outlook.adriansrj.battleroyale.parachute.creator.ParachuteCreationStageHandler;
import es.outlook.adriansrj.battleroyale.placeholder.PlaceholderHandler;
import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNodeRegistry;
import es.outlook.adriansrj.battleroyale.schedule.ScheduledExecutorPool;
import es.outlook.adriansrj.battleroyale.scoreboard.ScoreboardConfigurationRegistry;
import es.outlook.adriansrj.battleroyale.scoreboard.ScoreboardHandler;
import es.outlook.adriansrj.battleroyale.util.PluginUtil;
import es.outlook.adriansrj.battleroyale.util.stuff.PlayerStuffChestHandler;
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
	SCHEDULED_EXECUTOR_POOL ( ScheduledExecutorPool.class ),
	MAIN_CONFIGURATION_HANDLER ( MainConfigurationHandler.class ),
	LANGUAGE_CONFIGURATION_HANDLER ( BattleRoyaleLanguageConfigHandler.class ),
	
	COMMAND_HANDLER ( BattleRoyaleCommandHandler.class ),
	PLACEHOLDER_NODE_REGISTRY ( PlaceholderNodeRegistry.class ),
	PLACEHOLDER_HANDLER ( PlaceholderHandler.class ),
	RUN_MODE_HANDLER ( RunModeHandler.class ),
	DATA_STORAGE_HANDLER ( DataStorageHandler.class ),
	PLAYER_HANDLER ( PlayerHandler.class ),
	TEAM_HANDLER ( TeamHandler.class ),
	TEAM_GUIS_LANGUAGE_HANDLER ( TeamGUIsLanguageConfigHandler.class ),
	TEAM_SELECTOR_GUI_HANDLER ( TeamSelectorGUIHandler.class ),
	TEAM_GUI_HANDLER ( TeamGUIHandler.class ),
	
	SPECTATOR_HANDLER ( PlayerSpectatorHandler.class , false ),
	SPECTATOR_GUI ( SpectatorGUI.class , false ),
	KNOCK_HANDLER ( PlayerKnockHandler.class , false ),
	REANIMATION_HANDLER ( PlayerReviveHandler.class , false ),
	
	ITEM_HANDLER ( BattleRoyaleItemHandler.class ),
	SHOP_GUIS_CONFIGURATION_HANDLER ( ShopGUIsConfigHandler.class ),
	SETTINGS_GUIS_CONFIGURATION_HANDLER ( SettingsGUIsConfigHandler.class ),
	SHOP_GUI ( ShopGUIHandler.class ),
	SHOP_CONFIRMATION_GUI ( ShopConfirmationGUIHandler.class ),
	BUS_SHOP_GUI ( BusShopGUIHandler.class ),
	PARACHUTE_SHOP_GUI ( ParachuteShopGUIHandler.class ),
	PARACHUTE_COLOR_SHOP_GUI ( ParachuteColorShopGUIHandler.class ),
	SETTINGS_GUI ( SettingsGUIHandler.class ),
	BUS_SETTINGS_GUI ( BusSettingsGUIHandler.class ),
	PARACHUTE_SETTINGS_MAIN_GUI ( ParachuteSettingsMainGUIHandler.class ),
	PARACHUTE_SETTINGS_GUI ( ParachuteSettingsGUIHandler.class ),
	PARACHUTE_COLOR_SETTINGS_GUI ( ParachuteColorSettingsGUIHandler.class ),
	PARACHUTE_CREATION_STAGE_HANDLER ( ParachuteCreationStageHandler.class ),
	PARACHUTE_CREATOR_GUI ( ParachuteCreatorGUI.class ),
	
	SETUP_HANDLER ( BattlefieldSetupHandler.class , false ),
	SETUP_GUI_HANDLER ( SetupGUI.class , false ),
	LOBBY_MAP_SETUP_GUI ( LobbyMapSetupGUI.class , false ),
	BATTLEFIELD_SETUP_GUI ( BattlefieldSetupGUI.class , false ),
	BATTLEFIELD_SETUP_SESSION_GUI ( BattlefieldSetupSessionGUI.class , false ),
	
	LOBBY_CONFIGURATION_HANDLER ( BattleRoyaleLobbyConfigHandler.class , false ),
	LOBBY_HANDLER ( BattleRoyaleLobbyHandler.class , false ),
	
	LOOT_CONFIGURATION_REGISTRY ( LootConfigurationRegistry.class , false ),
	LOOT_CONFIGURATION_HANDLER ( LootConfigHandler.class , false ),
	
	VEHICLES_CONFIGURATION_REGISTRY ( VehiclesConfigurationRegistry.class , false ),
	VEHICLES_CONFIGURATION_HANDLER ( BattleRoyaleVehiclesConfigHandler.class , false ),
	
	BATTLEFIELD_HANDLER ( BattlefieldRegistry.class , false ),
	
	ARENA_HANDLER ( BattleRoyaleArenaHandler.class , false ),
	ARENA_CONFIGURATION_HANDLER ( BattleRoyaleArenasConfigHandler.class , false ),
	ARENA_GUI_HANDLER ( ArenaSelectorGUIHandler.class , false ),
	ARENA_GUI_CONFIGURATION_HANDLER ( ArenaSelectorGUIConfigHandler.class , false ),
	ARENA_SIGN_HANDLER ( BattleRoyaleArenaSignHandler.class , false ),
	ARENA_SIGN_CONFIGURATION_HANDLER ( BattleRoyaleArenaSignConfigHandler.class , false ),
	ARENA_SIGN_CONTAINER_HANDLER ( BattleRoyaleArenaSignContainerHandler.class , false ),
	ARENA_BORDER_HANDLER ( BattleRoyaleArenaBorderHandler.class , false ),
	ARENA_AUTO_STARTER_HANDLER ( AutoStarterHandler.class , false ),
	ARENA_AIR_SUPPLY_HANDLER ( AirSupplyGeneratorHandler.class , false ),
	ARENA_BOMBING_ZONE_HANDLER ( BombingZoneGeneratorHandler.class , false ),
	ARENA_ITEM_DROP_HANDLER ( ItemDropHandler.class , false ),
	
	MINIMAP_HANDLER ( MinimapHandler.class , false ),
	
	BUS_HANDLER ( BusHandler.class , false ),
	BUS_REGISTRY ( BusRegistry.class , false ),
	BUS_CONFIGURATION_HANDLER ( BusConfigHandler.class , false ),
	BUSES_CONFIGURATION_HANDLER ( BusesConfigHandler.class , false ),
	
	PARACHUTE_HANDLER ( ParachuteHandler.class , false ),
	PARACHUTE_REGISTRY ( ParachuteRegistry.class , false ),
	PARACHUTE_CONFIGURATION_HANDLER ( ParachuteConfigHandler.class , false ),
	PARACHUTE_COLOR_CONFIGURATION_HANDLER ( ParachuteColorConfigHandler.class , false ),
	PARACHUTES_CONFIGURATION_HANDLER ( ParachutesConfigHandler.class , false ),
	
	COMPASS_HANDLER ( CompassBarHandler.class , false ),
	COMPASS_CONFIGURATION_HANDLER ( CompassBarConfigHandler.class , false ),
	
	SCOREBOARD_CONFIGURATION_REGISTRY ( ScoreboardConfigurationRegistry.class , false ),
	SCOREBOARD_HANDLER ( ScoreboardHandler.class , false ),
	SCOREBOARD_CONFIGURATION_HANDLER ( ScoreboardConfigHandler.class , false ),
	
	QUALITY_ARMORY_COMPATIBILITY_HANDLER (
			QualityArmoryCompatibilityHandler.class , PluginUtil :: isQualityArmoryEnabled , false ),
	
	PLAYER_STUFF_CHEST_HANDLER ( PlayerStuffChestHandler.class , false ),
	
	;
	
	private final Class < ? extends PluginHandler > clazz;
	private final Callable < Boolean >              init_flag;
	private final boolean                           support_lobby;
	
	EnumPluginHandler ( Class < ? extends PluginHandler > clazz , Callable < Boolean > init_flag , boolean support_lobby ) {
		this.clazz         = clazz;
		this.init_flag     = init_flag;
		this.support_lobby = support_lobby;
	}
	
	EnumPluginHandler ( Class < ? extends PluginHandler > clazz , boolean support_lobby ) {
		this ( clazz , ( ) -> {
			return Boolean.TRUE; // no special checks are required by default.
		} , support_lobby );
	}
	
	EnumPluginHandler ( Class < ? extends PluginHandler > clazz ) {
		this ( clazz , true );
	}
	
	public Class < ? extends PluginHandler > getHandlerClass ( ) {
		return clazz;
	}
	
	public boolean canInitialize ( ) {
		RunModeHandler mode_handler = RunModeHandler.getInstance ( );
		
		try {
			if ( mode_handler == null ) {
				return init_flag.call ( );
			} else {
				return mode_handler.getMode ( ) != EnumMode.LOBBY || ( support_lobby && init_flag.call ( ) );
			}
		} catch ( Exception ex ) {
			ex.printStackTrace ( );
			return false;
		}
	}
}