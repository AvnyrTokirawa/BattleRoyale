package es.outlook.adriansrj.battleroyale.configuration.gui.shop;

import es.outlook.adriansrj.battleroyale.configuration.EnumConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.enums.EnumShopGUIsConfiguration;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;

import java.io.File;

/**
 * @author AdrianSR / 07/11/2021 / 11:48 a. m.
 */
public final class ShopGUIsConfigHandler
		extends EnumConfigurationHandler < EnumShopGUIsConfiguration > {
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public ShopGUIsConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.SHOP_GUIS_CONFIGURATION.getFile ( );
	}
	
	@Override
	public Class < EnumShopGUIsConfiguration > getEnumClass ( ) {
		return EnumShopGUIsConfiguration.class;
	}
}