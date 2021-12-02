package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationEntrySettable;
import es.outlook.adriansrj.battleroyale.configuration.lobby.BattleRoyaleLobbyConfigHandler;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Lobby configuration entries.
 *
 * @author AdrianSR / 03/09/2021 / 09:35 p. m.
 */
public enum EnumLobbyConfiguration implements ConfigurationEntrySettable {
	
	WORLD_NAME ( "world.name" , "name of the world for the lobby" , "lobby-world" ),
	WORLD_DISABLE_DAMAGE ( "world.disable-damage" ,
						   "whether to disable damage in the world of the lobby" , true ),
	WORLD_DISABLE_HUNGER ( "world.disable-hunger" ,
						   "whether to disable hunger in the world of the lobby" , true ),
	WORLD_DISABLE_FIRE_TICKS ( "world.disable-fire-ticks" ,
							   "whether to disable fire ticks in the world of the lobby" , true ),
	WORLD_DISABLE_MOBS ( "world.disable-mobs" ,
						 "whether to disable mobs in the world of the lobby" , true ),
	
	WORLD_DISABLE_ADVANCEMENTS_ANNOUNCEMENT ( "world.advancements.disable-announcement" ,
						 "whether to disable advancements announcement\n" +
								 "in the world of the lobby" , false ),
	
	SPAWN_ENABLE ( "spawn.enable" , "whether to use a custom spawn location" , true ),
	SPAWN_JOIN ( "spawn.join" , "whether to send players to lobby spawn when joining the server" , false ),
	SPAWN_VOID ( "spawn.void" , "whether to send players back to spawn when falling into void" , true ),
	SPAWN_X ( "spawn.x" , "" , 0.0D ),
	SPAWN_Y ( "spawn.y" , "" , 0.0D ),
	SPAWN_Z ( "spawn.z" , "" , 0.0D ),
	SPAWN_YAW ( "spawn.yaw" , "" , 0.0F ),
	SPAWN_PITCH ( "spawn.pitch" , "" , 0.0F ),
	
	;
	
	/**
	 * Save lobby configuration.
	 */
	public static void saveConfiguration ( ) {
		BattleRoyaleLobbyConfigHandler.getInstance ( ).save ( );
	}
	
	private final String      key;
	private final String      comment;
	private final Object      default_value;
	private final Class < ? > type;
	private       Object      value;
	
	EnumLobbyConfiguration ( String key , String comment , Object default_value , Class < ? > type ) {
		this.key           = key;
		this.comment       = comment;
		this.default_value = default_value;
		this.value         = default_value;
		this.type          = type;
	}
	
	EnumLobbyConfiguration ( String key , String comment , Object default_value ) {
		this ( key , comment , default_value , default_value.getClass ( ) );
	}
	
	@Override
	public String getKey ( ) {
		return key;
	}
	
	@Override
	public String getComment ( ) {
		return comment;
	}
	
	@Override
	public Object getDefaultValue ( ) {
		return default_value;
	}
	
	@Override
	public Object getValue ( ) {
		return value;
	}
	
	@Override
	public Class < ? > getValueType ( ) {
		return type;
	}
	
	@Override
	public void load ( ConfigurationSection section ) {
		Object raw = section.get ( getKey ( ) );
		
		if ( raw != null && ClassReflection.compatibleTypes ( this.type , raw ) ) {
			this.value = raw;
		}
	}
	
	@Override
	public void setValue ( Object value ) {
		Validate.isTrue ( ClassReflection.compatibleTypes ( getValueType ( ) , value ) ,
						  "incompatible types" );
		
		this.value = value;
	}
}