package es.outlook.adriansrj.battleroyale.game.player;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @author AdrianSR / 07/11/2021 / 10:21 a. m.
 */
class PlayerVaultBalance {
	
	private static final Economy ECONOMY_HANDLER;
	
	static {
		RegisteredServiceProvider < Economy > provider = Bukkit.getServicesManager ( )
				.getRegistration ( Economy.class );
		
		if ( provider != null ) {
			ECONOMY_HANDLER = provider.getProvider ( );
		} else {
			ECONOMY_HANDLER = null;
			
			// logging error
			ConsoleUtil.sendPluginMessage (
					ChatColor.RED , "Could not find an economy plugin on your server." ,
					BattleRoyale.getInstance ( ) );
		}
	}
	
	private final PlayerDataStorage player;
	
	PlayerVaultBalance ( PlayerDataStorage player ) {
		this.player = player;
	}
	
	public boolean isHooked ( ) {
		return ECONOMY_HANDLER != null;
	}
	
	public int getBalance ( ) {
		if ( ECONOMY_HANDLER != null ) {
			return ( int ) Math.round ( ECONOMY_HANDLER.getBalance (
					Bukkit.getOfflinePlayer ( player.getUniqueId ( ) ) ) );
		} else {
			return 0;
		}
	}
	
	public void setBalance ( int balance ) {
		balance = Math.max ( balance , 0 );
		
		OfflinePlayer player  = Bukkit.getOfflinePlayer ( this.player.getUniqueId ( ) );
		int           current = getBalance ( );
		
		if ( ECONOMY_HANDLER != null && current != balance ) {
			if ( current < balance ) {
				ECONOMY_HANDLER.depositPlayer ( player , ( double ) balance - current );
			} else {
				ECONOMY_HANDLER.withdrawPlayer ( player , ( double ) current - balance );
			}
		}
	}
	
	public void balanceDeposit ( int value ) {
		if ( ECONOMY_HANDLER != null && value > 0 ) {
			ECONOMY_HANDLER.depositPlayer (
					Bukkit.getOfflinePlayer ( player.getUniqueId ( ) ) , value );
		}
	}
	
	public void balanceWithdraw ( int value ) {
		if ( ECONOMY_HANDLER != null && value > 0 ) {
			ECONOMY_HANDLER.withdrawPlayer (
					Bukkit.getOfflinePlayer ( player.getUniqueId ( ) ) , value );
		}
	}
	
	public void clearBalance ( ) {
		setBalance ( 0 );
	}
}
