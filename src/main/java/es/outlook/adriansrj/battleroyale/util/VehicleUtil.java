package es.outlook.adriansrj.battleroyale.util;

import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;
import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;
import es.outlook.adriansrj.core.util.server.Version;

/**
 * Useful class for dealing with vehicles.
 *
 * @author AdrianSR / 21/09/2021 / 06:59 p. m.
 */
public class VehicleUtil {
	
	/**
	 * Finds out whether the sneaking flag in the provided <b>PacketPlayInSteerVehicle</b> is <b>true</b>.
	 *
	 * @param in_steer_packet the packet instance to check.
	 * @return whether the sneaking flag in the provided packet play in steer vehicle is <b>true</b>.
	 */
	public static boolean isSneaking ( Object in_steer_packet ) {
		boolean sneaking = false;
		
		try {
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_17_R1 ) ) {
				sneaking = FieldReflection.getValue ( in_steer_packet , "f" );
			} else {
				sneaking = FieldReflection.getValue ( in_steer_packet , "d" );
			}
		} catch ( NoSuchFieldException | IllegalAccessException e ) {
			e.printStackTrace ( );
		}
		
		return sneaking;
	}
	
	/**
	 * Extracts the id of the entity involved in the provided <b>entity-packet</b>.
	 * <p>
	 * Pretty much any entity-related packet is supported, there are some exceptions tough.
	 *
	 * @param packet the entity-related the packet instance to get extract.
	 * @return the id of the entity involved.
	 * @throws UnsupportedOperationException if the packet is not supported.
	 */
	public static int getEntityId ( Object packet ) {
		// the name of the field that holds the entity id
		// might be different depending on the version of the server.
		Class < ? > packet_class = packet.getClass ( );
		
		try {
			Class < ? > entity_packet_class = ClassReflection.getMinecraftClass (
					"PacketPlayOutEntity" , "network.protocol.game" );
			Class < ? > entity_teleport_packet_class = ClassReflection.getMinecraftClass (
					"PacketPlayOutEntityTeleport" , "network.protocol.game" );
			
			if ( entity_packet_class.isAssignableFrom ( packet_class ) ) {
				return FieldReflection.getAccessible ( entity_packet_class , "a" ).getInt ( packet );
			} else if ( entity_teleport_packet_class.isAssignableFrom ( packet_class ) ) {
				return FieldReflection.getAccessible ( entity_teleport_packet_class , "a" ).getInt ( packet );
			} else {
				// packet not implemented here; we will
				// anyways try though.
				try {
					return FieldReflection.getValue ( packet , "a" );
				} catch ( NoSuchFieldException | IllegalAccessException e ) {
					throw new UnsupportedOperationException ( );
				}
			}
		} catch ( ClassNotFoundException | NoSuchFieldException | IllegalAccessException e ) {
			throw new UnsupportedOperationException ( e );
		}
	}
}
