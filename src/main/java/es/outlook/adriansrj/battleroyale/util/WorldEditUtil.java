package es.outlook.adriansrj.battleroyale.util;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import es.outlook.adriansrj.core.util.math.Vector3D;
import es.outlook.adriansrj.core.util.server.Version;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author AdrianSR / 27/08/2021 / Time: 09:17 p. m.
 */
public class WorldEditUtil {
	
	public static Vector3D getDimensions ( Clipboard clipboard ) {
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 ) ) {
			return new Vector3D ( clipboard.getDimensions ( ).getX ( ) ,
								  clipboard.getDimensions ( ).getY ( ) ,
								  clipboard.getDimensions ( ).getZ ( ) );
		} else {
			try {
				Class < ? > vector_class = Class.forName ( "com.sk89q.worldedit.Vector" );
				Object      we_vector    = Clipboard.class.getMethod ( "getDimensions" ).invoke ( clipboard );
				
				return new Vector3D (
						( double ) vector_class.getMethod ( "getX" ).invoke ( we_vector ) ,
						( double ) vector_class.getMethod ( "getY" ).invoke ( we_vector ) ,
						( double ) vector_class.getMethod ( "getZ" ).invoke ( we_vector ) );
			} catch ( IllegalAccessException | InvocationTargetException
					| NoSuchMethodException | ClassNotFoundException e ) {
				e.printStackTrace ( );
			}
		}
		
		return null;
	}
}
