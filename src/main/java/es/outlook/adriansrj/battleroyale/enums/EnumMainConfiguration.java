package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationEntry;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import org.bukkit.configuration.ConfigurationSection;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 22/08/2021 / Time: 09:19 p. m.
 */
public enum EnumMainConfiguration implements ConfigurationEntry {
	
	// database enable
	ENABLE_DATABASE ( "database.enable" , "enable/disable database" , true ),
	// database type
	DATABASE_TYPE ( "database.type" , "the type of database to use." , EnumDataStorage.MYSQL.name ( ) ),
	
	// mysql database
	DATABASE_MYSQL_HOST ( "database.mysql.host" , "MySQL host name" , "host name" ),
	DATABASE_MYSQL_PORT ( "database.mysql.port" , "MySQL port" , 3306 ),
	DATABASE_MYSQL_DATABASE ( "database.mysql.database" , "MySQL database name" , "database" ),
	DATABASE_MYSQL_USERNAME ( "database.mysql.username" , "MySQL user username" , "username" ),
	DATABASE_MYSQL_PASSWORD ( "database.mysql.password" , "MySQL user password" , "password" ),
	
	// sqlite database
	DATABASE_SQLITE_PATH ( "database.sqlite.path" , "SQLite database path" , "path" ),
	
	// mongodb database
	DATABASE_MONGODB_URI ( "database.mongodb.uri" , "MongoDB database uri/connection string. (Optional)" , "" ),
	DATABASE_MONGODB_HOST ( "database.mongodb.host" , "MongoDB database host." , "host name" ),
	DATABASE_MONGODB_PORT ( "database.mongodb.port" , "MongoDB database port." , 27017 ),
	DATABASE_MONGODB_DATABASE ( "database.mongodb.database" , "MongoDB database name (Required)." , "database name" ),
	
	;
	
	private final String      key;
	private final String      comment;
	private final Object      default_value;
	private final Class < ? > type;
	private       Object      value;
	
	EnumMainConfiguration ( String key , String comment , Object default_value , Class < ? > type ) {
		this.key           = key;
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