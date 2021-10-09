package es.outlook.adriansrj.battleroyale.world.border;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import org.bukkit.World;

import java.util.Set;

/**
 * Simple world border that <b>doesn't notify the server</b>, but only the players
 * contained in the set returned by {@link #getPlayers()}.
 *
 * @author AdrianSR / 05/09/2021 / 11:46 p. m.
 */
public class WorldBorder implements WorldBorderHandle {
	
	protected final WorldBorderHandle handle;
	
	public WorldBorder ( World world ) {
		this.handle = WorldBorderHandle.getNewHandle ( world );
	}
	
	public WorldBorder ( ) {
		this ( null );
	}
	
	public WorldBorder ( World world , double x , double z , double size ) {
		this ( world );
		
		handle.setCenter ( x , z );
		handle.setSize ( size );
	}
	
	public WorldBorder ( double x , double z , double size ) {
		this ( null , x , z , size );
	}
	
	@Override
	public Set < Player > getPlayers ( ) {
		return handle.getPlayers ( );
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
		return handle.getState ( );
	}
	
	@Override
	public void setCenter ( double x , double z ) {
		handle.setCenter ( x , z );
	}
	
	@Override
	public void setSize ( double size ) {
		handle.setSize ( size );
	}
	
	@Override
	public void setSizeTransition ( double size , long milliseconds ) {
		handle.setSizeTransition ( size , milliseconds );
	}
	
	@Override
	public void refresh ( ) {
		handle.refresh ( );
	}
}