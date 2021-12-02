package es.outlook.adriansrj.battleroyale.bus;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaBusRegistry;
import es.outlook.adriansrj.battleroyale.bus.dragon.BusDragonInstance;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.VehicleUtil;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.packet.PacketChannelHandler;
import es.outlook.adriansrj.core.util.packet.PacketEvent;
import es.outlook.adriansrj.core.util.packet.PacketListener;
import org.bukkit.Bukkit;

/**
 * Class responsible for handling battle royale buses.
 *
 * @author AdrianSR / 08/09/2021 / 11:01 p. m.
 */
public final class BusHandler extends PluginHandler implements PacketListener {
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BusHandler ( BattleRoyale plugin ) {
		super ( plugin );
		
		// registering packet listener
		PacketChannelHandler.addPacketListener (
				"PacketPlayInSteerVehicle" , Priority.LOWEST , this );
	}
	
	@Override
	public void onReceiving ( PacketEvent event ) {
		org.bukkit.entity.Player     player    = event.getPlayer ( );
		Player                       br_player = Player.getPlayer ( player );
		BattleRoyaleArena            arena     = br_player.getArena ( );
		BattleRoyaleArenaBusRegistry registry  = arena != null ? arena.getBusRegistry ( ) : null;
		BusInstance < ? >            bus       = registry != null ? registry.getBus ( player ) : null;
		
		if ( VehicleUtil.isSneaking ( event.getPacket ( ) ) && bus != null && bus.isStarted ( ) ) {
			if ( bus.isDoorOpen ( ) ) {
				Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> {
					if ( bus instanceof BusDragonInstance ) {
						( ( BusDragonInstance ) bus ).ejectPlayer ( br_player );
					} else if ( !bus.isFinished ( ) ) {
						bus.finish ( );
					}
				} );
			}
			
			event.setCancelled ( true );
		}
	}
	
	@Override
	public void onSending ( PacketEvent event ) {
		// nothing to do here
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}