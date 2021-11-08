package es.outlook.adriansrj.battleroyale.data;

import es.outlook.adriansrj.battleroyale.cosmetic.Cosmetic;
import es.outlook.adriansrj.battleroyale.enums.EnumPlayerSetting;
import es.outlook.adriansrj.battleroyale.enums.EnumStat;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.PlayerDataStorage;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Data storage system interface.
 *
 * @author AdrianSR / 15/09/2021 / 06:15 p. m.
 */
public interface DataStorage {
	
	boolean setUp ( ) throws Exception;
	
	Set < PlayerDataStorage > getStoredPlayers ( ) throws Exception;
	
	// ------ stats
	
	Map < UUID, Map < EnumStat, Integer > > getStoredStatValues ( ) throws Exception;
	
	Map < EnumStat, Integer > getStatValues ( UUID uuid ) throws Exception;
	
	int getStatValue ( UUID uuid , EnumStat stat_type ) throws Exception;
	
	void loadStatValues ( PlayerDataStorage storage_player ) throws Exception;
	
	void setStatValue ( PlayerDataStorage storage_player , EnumStat stat_type , int value ) throws Exception;
	
	void setStatValue ( Player br_player , EnumStat stat_type , int value ) throws Exception;
	
	default Map < EnumStat, Integer > getStatValues ( Player br_player ) throws Exception {
		return getStatValues ( br_player.getUniqueId ( ) );
	}
	
	default int getStatValue ( Player br_player , EnumStat stat_type ) throws Exception {
		return getStatValue ( br_player.getUniqueId ( ) , stat_type );
	}
	
	// ------ settings
	
	Map < UUID, Map < EnumPlayerSetting, NamespacedKey > > getStoredSettingValues ( ) throws Exception;
	
	Map < EnumPlayerSetting, NamespacedKey > getSettingValues ( UUID uuid ) throws Exception;
	
	NamespacedKey getSettingValue ( UUID uuid , EnumPlayerSetting setting_type ) throws Exception;
	
	void loadSettingValues ( PlayerDataStorage storage_player ) throws Exception;
	
	void setSettingValue ( PlayerDataStorage storage_player , EnumPlayerSetting setting_type ,
			NamespacedKey value ) throws Exception;
	
	void setSettingValue ( Player br_player , EnumPlayerSetting setting_type , NamespacedKey value ) throws Exception;
	
	default Map < EnumPlayerSetting, NamespacedKey > getSettingValues ( Player br_player ) throws Exception {
		return getSettingValues ( br_player.getUniqueId ( ) );
	}
	
	default NamespacedKey getSettingValue ( Player br_player , EnumPlayerSetting setting_type ) throws Exception {
		return getSettingValue ( br_player.getUniqueId ( ) , setting_type );
	}
	
	// ------ cosmetics
	
	Map < UUID, Set < Cosmetic < ? > > > getStoredCosmetics ( ) throws Exception;
	
	Set < Cosmetic < ? > > getCosmetics ( UUID uuid ) throws Exception;
	
	void loadCosmetics ( PlayerDataStorage storage_player ) throws Exception;
	
	void addCosmetic ( Player br_player , Cosmetic < ? > cosmetic ) throws Exception;
	
	void addCosmetic ( PlayerDataStorage storage_player , Cosmetic < ? > cosmetic ) throws Exception;
	
	void removeCosmetic ( Player br_player , Cosmetic < ? > cosmetic ) throws Exception;
	
	void removeCosmetic ( PlayerDataStorage storage_player , Cosmetic < ? > cosmetic ) throws Exception;
	
	// ------ money
	
	Map < UUID, Integer > getStoredBalances ( ) throws Exception;
	
	int getBalance ( UUID uuid ) throws Exception;
	
	void loadBalance ( PlayerDataStorage storage_player ) throws Exception;
	
	void setBalance ( Player br_player , int balance ) throws Exception;
	
	void setBalance ( PlayerDataStorage storage_player , int balance ) throws Exception;
	
	void balanceDeposit ( Player br_player , int value ) throws Exception;
	
	void balanceDeposit ( PlayerDataStorage storage_player , int value ) throws Exception;
	
	void balanceWithdraw ( Player br_player , int value ) throws Exception;
	
	void balanceWithdraw ( PlayerDataStorage storage_player , int value ) throws Exception;
	
	default void clearBalance ( Player br_player ) throws Exception {
		setBalance ( br_player , 0 );
	}
	
	default void clearBalance ( PlayerDataStorage storage_player ) throws Exception {
		setBalance ( storage_player , 0 );
	}
	
	// ------
	
	void dispose ( );
}
