package es.outlook.adriansrj.battleroyale.world.border;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.World;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * @author AdrianSR / 05/09/2021 / 11:50 p. m.
 */
public interface WorldBorderHandle {
	
	public static WorldBorderHandle getNewHandle ( World world ) {
		try {
			return Class.forName ( WorldBorderHandle.class.getPackage ( ).getName ( ) + ".WorldBorderHandle_"
										   + Version.getServerVersion ( ).name ( ) )
					.asSubclass ( WorldBorderHandle.class )
					.getConstructor ( World.class ).newInstance ( world );
		} catch ( ClassNotFoundException | InvocationTargetException
				| InstantiationException | IllegalAccessException | NoSuchMethodException e ) {
			e.printStackTrace ( );
		}
		return null;
	}
	
	public Set < Player > getPlayers ( );
	
	public double getCenterX ( );
	
	public double getCenterZ ( );
	
	public double getSize ( );
	
	public WorldBorderState getState ( );
	
	public void setCenter ( double x , double z );
	
	public void setSize ( double size );
	
	public void setSizeTransition ( double size , long milliseconds );
	
	public void refresh ( );
}