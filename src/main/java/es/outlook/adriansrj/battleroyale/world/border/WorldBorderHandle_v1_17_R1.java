package es.outlook.adriansrj.battleroyale.world.border;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.util.reflection.bukkit.BukkitReflection;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.world.level.border.WorldBorder;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

/**
 * @author AdrianSR / 05/09/2021 / 11:53 p. m.
 */
class WorldBorderHandle_v1_17_R1 extends WorldBorderHandleBase {
	
	// world border events were added and will
	// be thrown when manipulating the border, resulting in
	// an exception if we manipulate it asynchronously.
	
	protected final WorldBorder handle;
	protected       double      future_size;
	
	public WorldBorderHandle_v1_17_R1 ( World world ) {
		super ( world );
		
		this.handle       = new WorldBorder ( );
		this.handle.world = ( ( CraftWorld ) world ).getHandle ( );
	}
	
	@Override
	public double getCenterX ( ) {
		return handle.getCenterX ( );
	}
	
	@Override
	public double getCenterZ ( ) {
		return handle.getCenterZ ( );
	}
	
	@Override
	public double getSize ( ) {
		return handle.getSize ( );
	}
	
	@Override
	public WorldBorderState getState ( ) {
		return Double.compare ( future_size , handle.getSize ( ) ) != 0
				? WorldBorderState.RESIZING : WorldBorderState.STATIONARY;
	}
	
	@Override
	public void setCenter ( double x , double z ) {
		SchedulerUtil.runTask ( ( ) -> this.handle.setCenter ( x , z ) );
		this.broadcastUpdateCenterPacket ( );
	}
	
	@Override
	public void setSize ( double size ) {
		this.future_size = size;
		
		SchedulerUtil.runTask ( ( ) -> this.handle.setSize ( size ) );
		this.broadcastUpdateSetSizePacket ( );
	}
	
	@Override
	public void setSizeTransition ( double size , long milliseconds ) {
		if ( handle.getSize ( ) != size ) {
			this.future_size = size;
			
			SchedulerUtil.runTask ( ( ) -> this.handle.transitionSizeBetween (
					handle.getSize ( ) , size , milliseconds ) );
			this.broadcastLerpSizePacket ( );
		}
	}
	
	@Override
	protected void resetBorder ( Player br_player ) {
		br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
			WorldBorder fake = new WorldBorder ( );
			fake.world = handle.world;
			
			BukkitReflection.sendPacket ( player , new ClientboundInitializeBorderPacket ( fake ) );
		} );
	}
	
	@Override
	protected void refresh ( Player player ) {
		updateCenterPacket ( player );
		updateSetSizePacket ( player );
		
		if ( getState ( ) == WorldBorderState.RESIZING ) {
			updateLerpSizePacket ( player );
		}
	}
	
	protected void broadcastUpdateCenterPacket ( ) {
		safeGetPlayers ( ).forEach ( this :: updateCenterPacket );
	}
	
	protected void updateCenterPacket ( Player br_player ) {
		updatePacket ( br_player , new ClientboundSetBorderCenterPacket ( handle ) );
	}
	
	protected void broadcastUpdateSetSizePacket ( ) {
		safeGetPlayers ( ).forEach ( this :: updateSetSizePacket );
	}
	
	protected void updateSetSizePacket ( Player br_player ) {
		updatePacket ( br_player , new ClientboundSetBorderSizePacket ( handle ) );
	}
	
	protected void broadcastLerpSizePacket ( ) {
		safeGetPlayers ( ).forEach ( this :: updateLerpSizePacket );
	}
	
	protected void updateLerpSizePacket ( Player br_player ) {
		updatePacket ( br_player , new ClientboundSetBorderLerpSizePacket ( handle ) );
	}
	
	protected void updatePacket ( Player br_player , Packet < ? > packet ) {
		br_player.getBukkitPlayerOptional ( ).ifPresent (
				player -> BukkitReflection.sendPacket ( player , packet ) );
	}
}