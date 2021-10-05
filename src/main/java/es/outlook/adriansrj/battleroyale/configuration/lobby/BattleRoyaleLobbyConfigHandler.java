package es.outlook.adriansrj.battleroyale.configuration.lobby;

import es.outlook.adriansrj.battleroyale.configuration.EnumConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.enums.EnumLobbyConfiguration;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

import java.io.File;

/**
 * @author AdrianSR / 03/09/2021 / 09:34 p. m.
 */
public final class BattleRoyaleLobbyConfigHandler extends EnumConfigurationHandler < EnumLobbyConfiguration > {
	
	public static BattleRoyaleLobbyConfigHandler getInstance ( ) {
		return getPluginHandler ( BattleRoyaleLobbyConfigHandler.class );
	}
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public BattleRoyaleLobbyConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.LOBBY_CONFIGURATION.getFile ( );
	}
	
	@Override
	public Class < EnumLobbyConfiguration > getEnumClass ( ) {
		return EnumLobbyConfiguration.class;
	}
}