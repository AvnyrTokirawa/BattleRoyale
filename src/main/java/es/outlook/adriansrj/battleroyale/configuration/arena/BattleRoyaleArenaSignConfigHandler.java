package es.outlook.adriansrj.battleroyale.configuration.arena;

import es.outlook.adriansrj.battleroyale.configuration.EnumConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.enums.EnumSignConfiguration;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

import java.io.File;

/**
 * @author AdrianSR / 03/09/2021 / 02:30 p. m.
 */
public final class BattleRoyaleArenaSignConfigHandler extends EnumConfigurationHandler < EnumSignConfiguration > {
	
	public static BattleRoyaleArenaSignConfigHandler getInstance ( ) {
		return getPluginHandler ( BattleRoyaleArenaSignConfigHandler.class );
	}
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public BattleRoyaleArenaSignConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public Class < EnumSignConfiguration > getEnumClass ( ) {
		return EnumSignConfiguration.class;
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.ARENA_SIGNS_CONFIGURATION.getFile ( );
	}
}