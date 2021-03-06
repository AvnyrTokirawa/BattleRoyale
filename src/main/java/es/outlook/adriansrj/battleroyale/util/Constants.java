package es.outlook.adriansrj.battleroyale.util;

import es.outlook.adriansrj.battleroyale.util.file.FileUtil;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import es.outlook.adriansrj.core.util.server.Version;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author AdrianSR / 02/09/2021 / 06:46 p. m.
 */
public class Constants {
	
	public static final String BATTLEFIELD_SCHEMATIC_FILE_BASENAME = "shape";
	public static final String BATTLEFIELD_SCHEMATIC_FILE_NAME     =
			BATTLEFIELD_SCHEMATIC_FILE_BASENAME + "." + FileUtil.PROPER_SCHEMATIC_EXTENSION;
	public static final String BATTLEFIELD_MINIMAP_FILE_NAME       = "minimap-image.png";
	public static final String BATTLEFIELD_CONFIGURATION_FILE_NAME = "configuration.yml";
	
	/** Location configuration entry key. */
	public static final String LOCATION_KEY           = "location";
	/** Location x coordinate key */
	public static final String X_KEY                  = "x";
	/** Location y coordinate key */
	public static final String Y_KEY                  = "y";
	/** Location z coordinate key */
	public static final String Z_KEY                  = "z";
	/** Location yaw coordinate key */
	public static final String YAW_KEY                = "yaw";
	/** Location pitch coordinate key */
	public static final String PITCH_KEY              = "pitch";
	/** Location roll coordinate key */
	public static final String ROLL_KEY               = "roll";
	/** World configuration entry key. */
	public static final String WORLD_KEY              = "world";
	/** Arena configuration entry key. */
	public static final String ARENA_KEY              = "arena";
	/** Arenas configuration entry key. */
	public static final String ARENAS_KEY             = "arenas";
	/** Battlefield configuration entry key. */
	public static final String BATTLEFIELD_KEY        = "battlefield";
	/** Mode configuration entry key. */
	public static final String MODE_KEY               = "mode";
	/** Default yaml file name. */
	public static final String DEFAULT_YAML_FILE_NAME = "default.yml";
	/** BattleRoyale sign keyword */
	public static final String ARENA_SIGN_KEYWORD     = "br:";
	
	public static final String SCHEMATIC_WIDTH_KEY  = "Width";
	public static final String SCHEMATIC_HEIGHT_KEY = "Height";
	public static final String SCHEMATIC_LENGTH_KEY = "Length";
	
	public static final String NAME_KEY      = "name";
	public static final String LIMIT_KEY     = "limit";
	public static final String DELAY_KEY     = "delay";
	public static final String DURATION_KEY  = "duration";
	public static final String SIZE_KEY      = "size";
	public static final String STRENGTH_KEY  = "strength";
	public static final String DATA_KEY      = "data";
	public static final String CHANCE_KEY    = "chance";
	public static final String REQUIRED_KEY  = "required";
	public static final String DIRECTION_KEY = "direction";
	
	//	/** Update period of the displacement executor of the buses */
	//	public static final long BUS_DISPLACEMENT_EXECUTOR_PERIOD    = 60L;
	/** Parachute life-loop executor period */
	public static final long PARACHUTE_LIFE_LOOP_EXECUTOR_PERIOD = 30L;
	/** Bus life-loop executor period */
	public static final long BUS_LIFE_LOOP_EXECUTOR_PERIOD       = 30L;
	
	// ---------- plugin
	
	public static final String PROTOCOL_LIB_PLUGIN_NAME            = "ProtocolLib";
	public static final String PLACEHOLDER_API_PLUGIN_NAME         = "PlaceholderAPI";
	public static final String VAULT_PLUGIN_NAME                   = "Vault";
	public static final String FEATHERBOARD_PLUGIN_NAME            = "FeatherBoard";
	public static final String QUALITY_ARMORY_PLUGIN_NAME          = "QualityArmory";
	public static final String QUALITY_ARMORY_VEHICLES_PLUGIN_NAME = "QualityArmoryVehicles2";
	public static final String CRACK_SHOT_PLUGIN_NAME              = "CrackShot";
	public static final String CRACK_SHOT_PLUS_PLUGIN_NAME         = "CrackShotPlus";
	public static final String MMO_ITEMS_PLUGIN_NAME               = "MMOItems";
	
	// ---------- metadata
	
	public static final String LOOT_CHEST_METADATA_KEY = UUID.randomUUID ( ).toString ( );
	
	/**
	 * Key to the metadata to be set for players
	 * who have been shot in the head.
	 * <br>
	 * The metadata to be set is a system that enables
	 * another plugins to tell BattleRoyale that a headshot
	 * was performed.
	 */
	public static final String HEADSHOT_METADATA_KEY = UUID.randomUUID ( ).toString ( );
	
	// ---------- placeholder
	
	public static final String BATTLE_ROYALE_PLACEHOLDER_IDENTIFIER = "br";
	
	// ---------- namespace
	
	public static final String BUS_NAMESPACE             = "bus";
	public static final String PARACHUTE_NAMESPACE       = "parachute";
	public static final String PARACHUTE_COLOR_NAMESPACE = "parachute-color";
	
	// ---------- text
	
	public static final String HEART_TEXT = String.valueOf ( ( char ) 10084 );
	
	// ---------- packet class names
	
	public static final String PACKET_IN_ARM_ANIMATION_NAME              = "PacketPlayInArmAnimation";
	public static final String PACKET_IN_POSITION_NAME                   = "PacketPlayInPosition";
	public static final String PACKET_IN_LOOK_NAME                       = "PacketPlayInPositionLook";
	public static final String PACKET_OUT_ENTITY_NAME                    = "PacketPlayOutEntity";
	public static final String PACKET_OUT_ENTITY_TELEPORT_NAME           = "PacketPlayOutEntityTeleport";
	public static final String PACKET_OUT_ATTACH_ENTITY_NAME             = "PacketPlayOutAttachEntity";
	public static final String PACKET_OUT_MOUNT_NAME                     = "PacketPlayOutMount";
	public static final String PACKET_OUT_ENTITY_RELATIVE_MOVE_NAME      = "PacketPlayOutRelEntityMove";
	public static final String PACKET_OUT_ENTITY_RELATIVE_MOVE_LOOK_NAME = "PacketPlayOutRelEntityMoveLook";
	
	// ---------- packet-related classes
	
	public static final Class < ? > PACKET_OUT_ENTITY_CLASS;
	public static final Class < ? > PACKET_OUT_ENTITY_RELATIVE_MOVE_CLASS;
	public static final Class < ? > PACKET_OUT_ENTITY_RELATIVE_MOVE_LOOK_CLASS;
	public static final Class < ? > PACKET_OUT_ENTITY_TELEPORT_CLASS;
	public static final Class < ? > PACKET_OUT_ATTACH_ENTITY_CLASS;
	public static final Class < ? > PACKET_OUT_MOUNT_CLASS;
	
	public static final Class < ? >       PACKET_CLASS;
	public static final Method            PACKET_WRITE_DATA_METHOD;
	/** <b>pre-1.17</b> method */
	public static final Method            PACKET_READ_DATA_METHOD;
	public static final Class < ? >       PACKET_DATA_SERIALIZER_CLASS;
	public static final Constructor < ? > PACKET_DATA_SERIALIZER_CONSTRUCTOR;
	
	static {
		// packet classes
		try {
			PACKET_OUT_ENTITY_CLASS = ClassReflection.getMinecraftClass (
					PACKET_OUT_ENTITY_NAME , "network.protocol.game" );
			
			PACKET_OUT_ENTITY_TELEPORT_CLASS = ClassReflection.getMinecraftClass (
					PACKET_OUT_ENTITY_TELEPORT_NAME , "network.protocol.game" );
			
			PACKET_OUT_ATTACH_ENTITY_CLASS = ClassReflection.getMinecraftClass (
					PACKET_OUT_ATTACH_ENTITY_NAME , "network.protocol.game" );
			
			PACKET_OUT_MOUNT_CLASS = ClassReflection.getMinecraftClass (
					PACKET_OUT_MOUNT_NAME , "network.protocol.game" );
			
			// entity relative move packet
			PACKET_OUT_ENTITY_RELATIVE_MOVE_CLASS = ClassReflection.getSubClass (
					PACKET_OUT_ENTITY_CLASS , PACKET_OUT_ENTITY_RELATIVE_MOVE_NAME );
			
			// entity relative move and look packet
			PACKET_OUT_ENTITY_RELATIVE_MOVE_LOOK_CLASS = ClassReflection.getSubClass (
					PACKET_OUT_ENTITY_CLASS , PACKET_OUT_ENTITY_RELATIVE_MOVE_LOOK_NAME );
			
			// class/constructor/method names may vary depending on version of the server
			PACKET_CLASS                       = ClassReflection.getMinecraftClass (
					"Packet" , "network.protocol" );
			PACKET_DATA_SERIALIZER_CLASS       = ClassReflection.getMinecraftClass (
					"PacketDataSerializer" , "network" );
			PACKET_DATA_SERIALIZER_CONSTRUCTOR = PACKET_DATA_SERIALIZER_CLASS.getConstructor ( ByteBuf.class );
			
			// write method
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_17_R1 ) ) {
				PACKET_WRITE_DATA_METHOD = PACKET_CLASS.getMethod ( "a" , PACKET_DATA_SERIALIZER_CLASS );
			} else {
				PACKET_WRITE_DATA_METHOD = PACKET_CLASS.getMethod ( "b" , PACKET_DATA_SERIALIZER_CLASS );
			}
			
			// read method
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_17_R1 ) ) {
				PACKET_READ_DATA_METHOD = null;
			} else {
				PACKET_READ_DATA_METHOD = PACKET_CLASS.getMethod ( "a" , PACKET_DATA_SERIALIZER_CLASS );
			}
		} catch ( ClassNotFoundException | NoSuchMethodException ex ) {
			throw new IllegalStateException ( ex );
		}
	}
}