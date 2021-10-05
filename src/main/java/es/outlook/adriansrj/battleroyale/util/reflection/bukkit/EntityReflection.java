package es.outlook.adriansrj.battleroyale.util.reflection.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Useful class for dealing with the reflection of Bukkit entities.
 *
 * @author AdrianSR / 13/09/2021 / 12:12 a. m.
 */
public class EntityReflection extends es.outlook.adriansrj.core.util.reflection.bukkit.EntityReflection {
	
	public static void directLocationUpdate ( Entity entity , Vector location ) {
		setPositionDirty ( entity , location );
		//		try {
		//			Class < ? > entity_nms_class = ClassReflection.getNmsClass ( "Entity" );
		//
		//			if ( Version.getServerVersion ( ).isOlder ( Version.v1_16_R1 ) ) {
		//				Field loc_x_field = FieldReflection.get ( entity_nms_class , "locX" );
		//				Field loc_y_field = FieldReflection.get ( entity_nms_class , "locY" );
		//				Field loc_z_field = FieldReflection.get ( entity_nms_class , "locZ" );
		//
		//				loc_x_field.set ( BukkitReflection.getHandle ( entity ) , location.getX ( ) );
		//				loc_y_field.set ( BukkitReflection.getHandle ( entity ) , location.getY ( ) );
		//				loc_z_field.set ( BukkitReflection.getHandle ( entity ) , location.getZ ( ) );
		//			} else {
		//				Object      handle       = BukkitReflection.getHandle ( entity );
		//				Field       loc_field    = FieldReflection.getAccessible ( entity_nms_class , "loc" );
		//				Class < ? > vector_class = ClassReflection.getNmsClass ( "Vec3D" );
		//
		//				loc_field.set ( handle , vector_class.getConstructor ( double.class , double.class , double
		//				.class )
		//						.newInstance ( location.getX ( ) , location.getY ( ) , location.getZ ( ) ) );
		//			}
		//		} catch ( ClassNotFoundException | SecurityException | NoSuchFieldException | IllegalArgumentException
		//				| IllegalAccessException | InvocationTargetException | NoSuchMethodException
		//				| InstantiationException ex ) {
		//			ex.printStackTrace ( );
		//		}
	}
}