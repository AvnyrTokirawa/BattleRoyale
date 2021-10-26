package es.outlook.adriansrj.battleroyale.world.border;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.util.reflection.bukkit.BukkitReflection;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import net.minecraft.server.v1_16_R2.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_16_R2.WorldBorder;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;

/**
 * @author AdrianSR / 05/09/2021 / 11:53 p. m.
 */
class WorldBorderHandle_v1_16_R2 extends WorldBorderHandleBase {
	
	// world border events were added and will
	// be thrown when manipulating the border, resulting in
	// an exception if we manipulate it asynchronously.
	
	protected final WorldBorder handle;
	protected       double      future_size;
	
	public WorldBorderHandle_v1_16_R2 ( World world ) {
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
		this.broadcastUpdatePacket ( PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER );
	}
	
	@Override
	public void setSize ( double size ) {
		this.future_size = size;
		
		SchedulerUtil.runTask ( ( ) -> this.handle.setSize ( size ) );
		this.broadcastUpdatePacket ( PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE );
	}
	
	@Override
	public void setSizeTransition ( double size , long milliseconds ) {
		if ( handle.getSize ( ) != size ) {
			this.future_size = size;
			
			SchedulerUtil.runTask ( ( ) -> this.handle.transitionSizeBetween (
					handle.getSize ( ) , size , milliseconds ) );
			this.broadcastUpdatePacket ( PacketPlayOutWorldBorder.EnumWorldBorderAction.LERP_SIZE );
		}
	}
	
	@Override
	protected void resetBorder ( Player br_player ) {
		br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
			WorldBorder fake = new WorldBorder ( );
			fake.world = handle.world;
			
			BukkitReflection.sendPacket ( player , new PacketPlayOutWorldBorder (
					fake , PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE ) );
		} );
	}
	
	@Override
	protected void refresh ( Player player ) {
		updatePacket ( player , PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER );
		updatePacket ( player , PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE );
		
		if ( getState ( ) == WorldBorderState.RESIZING ) {
			updatePacket ( player , PacketPlayOutWorldBorder.EnumWorldBorderAction.LERP_SIZE );
		}
	}
	
	protected void broadcastUpdatePacket ( PacketPlayOutWorldBorder.EnumWorldBorderAction action ) {
		safeGetPlayers ( ).forEach ( player -> updatePacket ( player , action ) );
	}
	
	protected void updatePacket ( Player br_player , PacketPlayOutWorldBorder.EnumWorldBorderAction action ) {
		br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> BukkitReflection.sendPacket (
				player , new PacketPlayOutWorldBorder ( handle , action ) ) );
	}
}