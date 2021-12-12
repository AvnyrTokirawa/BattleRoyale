package es.outlook.adriansrj.battleroyale.util.server;

import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;
import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;
import es.outlook.adriansrj.core.util.reflection.general.MethodReflection;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author AdrianSR / 11/12/2021 / 09:34 a. m.
 */
public class ServerUtil {
	
	public static void setMotd ( String motd ) {
		try {
			Server bukkit_server = Bukkit.getServer ( );
			Object server = FieldReflection.getAccessible (
					bukkit_server.getClass ( ) , "console" , true ).get ( bukkit_server );
			Method set_method = MethodReflection.getAccessible ( ClassReflection.getMinecraftClass (
					"MinecraftServer" ) , "setMotd" , String.class );
			
			set_method.invoke ( server , motd );
		} catch ( IllegalAccessException | NoSuchFieldException | ClassNotFoundException
				| NoSuchMethodException | InvocationTargetException e ) {
			e.printStackTrace ( );
		}
	}
}
