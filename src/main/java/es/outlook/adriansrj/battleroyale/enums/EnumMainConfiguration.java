package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationEntry;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Plugin main configuration.
 *
 * @author AdrianSR / 22/08/2021 / Time: 09:19 p. m.
 */
public enum EnumMainConfiguration implements ConfigurationEntry {
	
	// mode
	MODE_TYPE ( "mode.type" , "the type of mode in which the plugin will run\n" +
			"types: " + Arrays.stream ( EnumMode.values ( ) ).map ( type -> type.name ( ).toLowerCase ( ) )
			.collect ( Collectors.joining ( ", " ) ) , EnumMode.MULTIARENA.name ( ) ),
	
	// bungee mode
	MODE_BUNGEE_ARENA ( "mode.bungee.arena" ,
						"the name of the only arena to be\n" +
								"played in this server." , "example" ),
	
	MODE_BUNGEE_RESTART_COMMAND ( "mode.bungee.restart-command" ,
								  "the command to restart the server\n" +
										  "after the arena ends." , "restart" ),
	
	// shared mode
	MODE_SHARED_COMMAND ( "mode.shared.command" ,
						  "the name for the command that wen\n" +
								  "executed sends the player to the\n" +
								  "battle royale lobby.\n" +
								  "you can use this command with gui\n" +
								  "plugins, or players can manually\n" +
								  "execute this command to join the battle royale." , "bttrl" ),
	
	// database enable
	ENABLE_DATABASE ( "database.enable" , "enable/disable database" , true ),
	// database type
	DATABASE_TYPE ( "database.type" , "the type of database to use.\n" +
			"types: " + Arrays.stream ( EnumDataStorage.values ( ) ).map ( type -> type.name ( ).toLowerCase ( ) )
			.collect ( Collectors.joining ( ", " ) ) , EnumDataStorage.MYSQL.name ( ) ),
	
	// mysql database
	DATABASE_MYSQL_HOST ( "database.mysql.host" , "MySQL host name." , "host name" ),
	DATABASE_MYSQL_PORT ( "database.mysql.port" , "MySQL port." , 3306 ),
	DATABASE_MYSQL_DATABASE ( "database.mysql.database" , "MySQL database name." , "database" ),
	DATABASE_MYSQL_USERNAME ( "database.mysql.username" , "MySQL user username." , "username" ),
	DATABASE_MYSQL_PASSWORD ( "database.mysql.password" , "MySQL user password." , "password" ),
	
	// sqlite database
	DATABASE_SQLITE_PATH ( "database.sqlite.path" , "SQLite database path" , "path" ),
	
	// mongodb database
	DATABASE_MONGODB_URI ( "database.mongodb.uri" ,
						   "MongoDB database uri/connection string. (Optional)" , "" ),
	DATABASE_MONGODB_HOST ( "database.mongodb.host" ,
							"MongoDB database host." , "host name" ),
	DATABASE_MONGODB_PORT ( "database.mongodb.port" ,
							"MongoDB database port." , 27017 ),
	DATABASE_MONGODB_DATABASE ( "database.mongodb.database" ,
								"MongoDB database name (Required)." , "database name" ),
	
	// vault
	VAULT_ENABLE ( "vault.enable" , "whether to enable vault compatibility.\n" +
			"note that if this option is enabled, the\n" +
			"vault balance will be used instead of the\b" +
			"battle royale balance." , false ),
	
	// game
	GAME_ENHANCED_DROPS_ENABLE ( "game.enhanced-drops.enable" , "if enabled, drops will be enhanced. " +
			"\ndropped items will have a better looking, " +
			"\nmaking it really easy for players to know " +
			"\nwhat they are grabbing from ground." +
			"\nnote that if this option is enabled, the loot chests " +
			"\nwill be replaced by items directly spawned on the ground." +
			"\nWARNING:" +
			"\nenabling this option could reduce the performance." +
			"\nnote that dropping lots of items on the ground" +
			"\nmight be stressful for the server." , false ),
	
	GAME_ENHANCED_DROPS_VISIBLE_NAME ( "game.enhanced-drops.visible-name" ,
									   "if enabled, the name if the item will be visible" , true ),
	
	GAME_ENHANCED_DROPS_CUSTOM_MODEL ( "game.enhanced-drops.custom-model" ,
									   "if disabled, the vanilla drops will be used instead, " +
											   "\nbut keeping the nametag of the item." , true ),
	
	GAME_ENHANCED_DROPS_LOOT_CONTAINER_ONLY ( "game.enhanced-drops.loot-container-only" ,
											  "if enabled, only the drops coming from a" +
													  "\na loot container (loot chest, air supply, etc...)" +
													  "\nwill be affected. (recommended)" , true ),
	
	METRICS_ENABLE ( "metrics.enable" , "whether to enable metrics" , true ),
	
	;
	
	private final String      key;
	private final String      comment;
	private final Object      default_value;
	private final Class < ? > type;
	private       Object      value;
	
	EnumMainConfiguration ( String key , String comment , Object default_value , Class < ? > type ) {
		this.key           = key.trim ( );
		this.comment       = comment;
		this.default_value = default_value;
		this.value         = default_value;
		this.type          = type;
	}
	
	EnumMainConfiguration ( String key , String comment , Object default_value ) {
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
}