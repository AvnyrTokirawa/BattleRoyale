package es.outlook.adriansrj.battleroyale.battlefield.setup;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * @author AdrianSR / 28/08/2021 / 10:48 a. m.
 */
public abstract class BattlefieldSetupTool implements Listener {
	
	protected final BattlefieldSetupSession session;
	protected final Player                  configurator;
	protected       boolean                 disposed;
	
	protected BattlefieldSetupTool ( BattlefieldSetupSession session , Player configurator ) {
		this.session      = session;
		this.configurator = configurator;
	}
	
	public BattlefieldSetupSession getSession ( ) {
		return session;
	}
	
	public Player getConfigurator ( ) {
		return configurator;
	}
	
	public boolean isActive ( ) {
		return ! disposed;
	}
	
	protected abstract void initialize ( );
	
	public abstract boolean isModal ( );
	
	public abstract boolean isCancellable ( );
	
	protected void dispose ( ) {
		unregister ( );
		
		disposed = true;
	}
	
	protected void register ( ) {
		Bukkit.getPluginManager ( ).registerEvents ( this , BattleRoyale.getInstance ( ) );
	}
	
	protected void unregister ( ) {
		HandlerList.unregisterAll ( this );
	}
}