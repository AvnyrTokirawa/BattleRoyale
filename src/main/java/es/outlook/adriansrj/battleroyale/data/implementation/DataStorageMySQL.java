package es.outlook.adriansrj.battleroyale.data.implementation;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import es.outlook.adriansrj.battleroyale.cosmetic.Cosmetic;
import es.outlook.adriansrj.battleroyale.data.DataStorage;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.enums.EnumPlayerSetting;
import es.outlook.adriansrj.battleroyale.enums.EnumStat;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.game.player.PlayerDataStorage;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;

import java.sql.*;
import java.util.*;

/**
 * MySQL data storage system.
 *
 * @author AdrianSR / 15/09/2021 / 06:28 p. m.
 */
public class DataStorageMySQL implements DataStorage {
	
	/**
	 * MySQL's jdbc URL format.
	 */
	protected static final String URL_FORMAT = "jdbc:mysql://"
			+ "%s" // host
			+ ":"
			+ "%d" // port
			+ "/"
			+ "%s" // database
			+ "?useSSL="
			+ "%s" // use ssl
			;
	
	protected static final String PLAYER_ID_TABLE_NAME       = "br_player_id";
	protected static final String STATS_TABLE_NAME           = "br_player_stats";
	protected static final String SETTINGS_TABLE_NAME_FORMAT = "br_player_settings";
	protected static final String COSMETICS_TABLE_NAME       = "br_player_cosmetics";
	protected static final String UID_COLUMN                 = "UUID";
	protected static final String NAME_COLUMN                = "NAME";
	protected static final String KEY_COLUMN                 = "_KEY";
	protected static final String VALUE_COLUMN               = "VALUE";
	
	protected final HikariConfig     configuration;
	protected       HikariDataSource source;
	
	public DataStorageMySQL ( BattleRoyale plugin ) {
		this.configuration = new HikariConfig ( );
		
		this.configuration.setJdbcUrl (
				String.format ( URL_FORMAT ,
								EnumMainConfiguration.DATABASE_MYSQL_HOST.getAsString ( ) ,
								EnumMainConfiguration.DATABASE_MYSQL_PORT.getAsInteger ( ) ,
								EnumMainConfiguration.DATABASE_MYSQL_DATABASE.getAsString ( ) ,
								true ) );
		
		this.configuration.setUsername ( EnumMainConfiguration.DATABASE_MYSQL_USERNAME.getAsString ( ) );
		this.configuration.setPassword ( EnumMainConfiguration.DATABASE_MYSQL_PASSWORD.getAsString ( ) );
		this.configuration.addDataSourceProperty ( "cachePrepStmts" , "true" );
		this.configuration.addDataSourceProperty ( "prepStmtCacheSize" , "250" );
		this.configuration.addDataSourceProperty ( "prepStmtCacheSqlLimit" , "2048" );
		this.configuration.addDataSourceProperty ( "useServerPrepStmts" , "true" );
	}
	
	@Override
	public boolean setUp ( ) throws SQLException {
		this.source = new HikariDataSource ( configuration );
		
		// checking tables
		try ( Connection connection = source.getConnection ( ) ) {
			// checking structure of the tables
			tablesStructureCheck ( connection );
			// creating not existing tables
			tablesCheck ( connection );
		}
		
		return true;
	}
	
	protected void tablesCheck ( Connection connection ) throws SQLException {
		// creating player id table
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"CREATE TABLE IF NOT EXISTS %s (" +
						"%s VARCHAR(36) PRIMARY KEY NOT NULL, " +
						"%s VARCHAR(36) UNIQUE NOT NULL)" ,
				PLAYER_ID_TABLE_NAME , UID_COLUMN , NAME_COLUMN ) ) ) {
			
			statement.executeUpdate ( );
		}
		
		// creating stats table
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"CREATE TABLE IF NOT EXISTS %s (" +
						"%s VARCHAR(36) NOT NULL, " +
						"%s VARCHAR(36) NOT NULL, " +
						"%s INTEGER NOT NULL, " +
						"PRIMARY KEY (%s, %s), " +
						"FOREIGN KEY (%s) REFERENCES %s(%s))" ,
				STATS_TABLE_NAME , UID_COLUMN , KEY_COLUMN , VALUE_COLUMN ,
				UID_COLUMN , KEY_COLUMN ,
				UID_COLUMN , PLAYER_ID_TABLE_NAME , UID_COLUMN) ) ) {
			
			statement.executeUpdate ( );
		}
		
		// creating settings table
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"CREATE TABLE IF NOT EXISTS %s (" +
						"%s VARCHAR(36) NOT NULL, " +
						"%s VARCHAR(36) NOT NULL, " +
						"%s VARCHAR(36) NOT NULL, " +
						"PRIMARY KEY (%s, %s), " +
						"FOREIGN KEY (%s) REFERENCES %s(%s))" ,
				SETTINGS_TABLE_NAME_FORMAT , UID_COLUMN , KEY_COLUMN , VALUE_COLUMN ,
				UID_COLUMN , KEY_COLUMN ,
				UID_COLUMN , PLAYER_ID_TABLE_NAME , UID_COLUMN ) ) ) {
			
			statement.executeUpdate ( );
		}
		
		// creating cosmetics table
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"CREATE TABLE IF NOT EXISTS %s (" +
						"%s VARCHAR(36) NOT NULL, " +
						"%s VARCHAR(36) NOT NULL, " +
						"PRIMARY KEY(%s, %s), " +
						"FOREIGN KEY (%s) REFERENCES %s(%s))" ,
				COSMETICS_TABLE_NAME , UID_COLUMN , VALUE_COLUMN ,
				UID_COLUMN , VALUE_COLUMN ,
				UID_COLUMN , PLAYER_ID_TABLE_NAME , UID_COLUMN ) ) ) {
			statement.executeUpdate ( );
		}
	}
	
	protected void tablesStructureCheck ( Connection connection ) throws SQLException {
		// checking structure of the stats table
		tableStructureCheck ( connection , STATS_TABLE_NAME , UID_COLUMN , KEY_COLUMN , VALUE_COLUMN );
		// checking structure of the settings table
		tableStructureCheck ( connection , SETTINGS_TABLE_NAME_FORMAT , UID_COLUMN , KEY_COLUMN , VALUE_COLUMN );
		// checking structure of the cosmetics table
		tableStructureCheck ( connection , COSMETICS_TABLE_NAME , UID_COLUMN , VALUE_COLUMN );
	}
	
	@Override
	public Set < PlayerDataStorage > getStoredPlayers ( ) throws Exception {
		Set < PlayerDataStorage > result = new HashSet <> ( );
		
		try ( Connection connection = source.getConnection ( ) ) {
			// querying registered players
			try ( PreparedStatement statement = connection.prepareStatement ( String.format (
					"SELECT %s, %s FROM %s" ,
					UID_COLUMN , NAME_COLUMN , PLAYER_ID_TABLE_NAME ) ) ) {
				
				try ( ResultSet result_set = statement.executeQuery ( ) ) {
					while ( result_set.next ( ) ) {
						try {
							UUID   uuid = UUID.fromString ( result_set.getString ( UID_COLUMN ) );
							String name = result_set.getString ( NAME_COLUMN );
							
							result.add ( new PlayerDataStorage ( uuid , name ) );
						} catch ( IllegalArgumentException ex ) {
							// ignore invalid uuid.
						}
					}
				}
			}
			
			// then loading into players
			for ( PlayerDataStorage storage_player : result ) {
				// loading stats
				storage_player.setStats ( getStatValues0 ( connection , storage_player.getUniqueId ( ) ) );
				// loading settings
				storage_player.setSettings ( getSettingValues0 ( connection , storage_player.getUniqueId ( ) ) );
				// loading cosmetics
				storage_player.getCosmetics ( )
						.addAll ( getCosmetics0 ( connection , storage_player.getUniqueId ( ) ) );
			}
		}
		
		return result;
	}
	
	@Override
	public Map < UUID, Map < EnumStat, Integer > > getStoredStatValues ( ) throws Exception {
		Map < UUID, Map < EnumStat, Integer > > result = new HashMap <> ( );
		
		try ( Connection connection = source.getConnection ( ) ) {
			for ( UUID uuid : queryIds ( connection ) ) {
				result.put ( uuid , getStatValues0 ( connection , uuid ) );
			}
		}
		
		return result;
	}
	
	@Override
	public Map < EnumStat, Integer > getStatValues ( Player br_player ) throws Exception {
		return getStatValues ( br_player.getUniqueId ( ) );
	}
	
	@Override
	public Map < EnumStat, Integer > getStatValues ( UUID uuid ) throws Exception {
		try ( Connection connection = source.getConnection ( ) ) {
			return getStatValues0 ( connection , uuid );
		}
	}
	
	// single connection
	protected Map < EnumStat, Integer > getStatValues0 ( Connection connection , UUID uuid ) throws Exception {
		Validate.notNull ( uuid , "uuid cannot be null" );
		
		Map < EnumStat, Integer > result = new EnumMap <> ( EnumStat.class );
		
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"SELECT %s, %s FROM %s WHERE %s = ?" ,
				KEY_COLUMN , VALUE_COLUMN , STATS_TABLE_NAME , UID_COLUMN ) ) ) {
			
			statement.setString ( 1 , uuid.toString ( ) );
			
			try ( ResultSet result_set = statement.executeQuery ( ) ) {
				while ( result_set.next ( ) ) {
					EnumStat stat_type = EnumReflection.getEnumConstant ( EnumStat.class , StringUtil.defaultIfBlank (
							result_set.getString ( KEY_COLUMN ) , StringUtil.EMPTY ) );
					
					if ( stat_type != null ) {
						result.put ( stat_type , Math.max ( result_set.getInt ( VALUE_COLUMN ) , 0 ) );
					}
				}
			}
		}
		
		return result;
	}
	
	@Override
	public int getStatValue ( UUID uuid , EnumStat stat_type ) throws Exception {
		try ( Connection connection = source.getConnection ( ) ) {
			return getStatValue0 ( connection , uuid , stat_type );
		}
	}
	
	// single connection
	protected int getStatValue0 ( Connection connection , UUID uuid , EnumStat stat_type ) throws Exception {
		Validate.notNull ( uuid , "uuid cannot be null" );
		Validate.notNull ( stat_type , "stat type cannot be null" );
		
		return ( int ) query0 ( connection , STATS_TABLE_NAME , uuid , stat_type.name ( ) , 0 );
	}
	
	@Override
	public void loadStatValues ( PlayerDataStorage storage_player ) throws Exception {
		storage_player.setStats ( getStatValues ( storage_player.getUniqueId ( ) ) );
	}
	
	@Override
	public void setStatValue ( PlayerDataStorage storage_player , EnumStat stat_type , int value )
			throws Exception {
		Validate.notNull ( storage_player , "player cannot be null" );
		
		setStatValue0 ( storage_player.getUniqueId ( ) , storage_player.getName ( ) , stat_type , value );
	}
	
	@Override
	public void setStatValue ( Player br_player , EnumStat stat_type , int value ) throws Exception {
		Validate.notNull ( br_player , "player cannot be null" );
		
		setStatValue0 ( br_player.getUniqueId ( ) , br_player.getName ( ) , stat_type , value );
	}
	
	// direct connection
	public void setStatValue0 ( UUID uuid , String name , EnumStat stat_type , int value ) throws Exception {
		try ( Connection connection = source.getConnection ( ) ) {
			setStatValue0 ( connection , uuid , name , stat_type , value );
		}
	}
	
	// single connection
	public void setStatValue0 ( Connection connection , UUID uuid ,
			String name , EnumStat stat_type , int value ) throws Exception {
		set0 ( connection , STATS_TABLE_NAME , uuid , name , stat_type.name ( ) , value );
	}
	
	// ---------- settings
	
	@Override
	public Map < UUID, Map < EnumPlayerSetting, NamespacedKey > > getStoredSettingValues ( ) throws Exception {
		try ( Connection connection = source.getConnection ( ) ) {
			Map < UUID, Map < EnumPlayerSetting, NamespacedKey > > result = new HashMap <> ( );
			
			for ( UUID uuid : queryIds ( connection ) ) {
				result.put ( uuid , getSettingValues0 ( connection , uuid ) );
			}
			
			return result;
		}
	}
	
	@Override
	public Map < EnumPlayerSetting, NamespacedKey > getSettingValues ( UUID uuid ) throws Exception {
		try ( Connection connection = source.getConnection ( ) ) {
			return getSettingValues0 ( connection , uuid );
		}
	}
	
	// single connection
	protected Map < EnumPlayerSetting, NamespacedKey > getSettingValues0 ( Connection connection , UUID uuid )
			throws Exception {
		Validate.notNull ( uuid , "uuid cannot be null" );
		
		Map < EnumPlayerSetting, NamespacedKey > result = new EnumMap <> ( EnumPlayerSetting.class );
		
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"SELECT %s, %s FROM %s WHERE %s = ?" ,
				KEY_COLUMN , VALUE_COLUMN , SETTINGS_TABLE_NAME_FORMAT , UID_COLUMN ) ) ) {
			
			statement.setString ( 1 , uuid.toString ( ) );
			
			try ( ResultSet result_set = statement.executeQuery ( ) ) {
				while ( result_set.next ( ) ) {
					EnumPlayerSetting setting_type = EnumReflection.getEnumConstant (
							EnumPlayerSetting.class , StringUtil.defaultIfBlank (
									result_set.getString ( KEY_COLUMN ) , StringUtil.EMPTY ) );
					
					try {
						if ( setting_type != null ) {
							result.put ( setting_type , NamespacedKey.of ( StringUtil.defaultIfBlank (
									result_set.getString ( VALUE_COLUMN ) , StringUtil.EMPTY ) ) );
						}
					} catch ( IllegalArgumentException ex ) {
						// ignore invalid name spaced key
					}
				}
			}
		}
		
		return result;
	}
	
	@Override
	public NamespacedKey getSettingValue ( UUID uuid , EnumPlayerSetting setting_type ) throws Exception {
		try ( Connection connection = source.getConnection ( ) ) {
			return getSettingValue0 ( connection , uuid , setting_type );
		}
	}
	
	// single connection
	protected NamespacedKey getSettingValue0 ( Connection connection , UUID uuid , EnumPlayerSetting setting_type )
			throws Exception {
		Validate.notNull ( uuid , "uuid cannot be null" );
		Validate.notNull ( setting_type , "setting type cannot be null" );
		
		return NamespacedKey.of (
				query0 ( connection , SETTINGS_TABLE_NAME_FORMAT ,
						 uuid , setting_type.name ( ) , setting_type.getDefaultValue ( ) ).toString ( ) );
	}
	
	@Override
	public void loadSettingValues ( PlayerDataStorage storage_player ) throws Exception {
		storage_player.setSettings ( getSettingValues ( storage_player.getUniqueId ( ) ) );
	}
	
	@Override
	public void setSettingValue ( PlayerDataStorage storage_player , EnumPlayerSetting setting_type ,
			NamespacedKey value ) throws Exception {
		setSettingValue0 ( storage_player.getUniqueId ( ) , storage_player.getName ( ) , setting_type , value );
	}
	
	@Override
	public void setSettingValue ( Player br_player , EnumPlayerSetting setting_type , NamespacedKey value )
			throws Exception {
		setSettingValue0 ( br_player.getUniqueId ( ) , br_player.getName ( ) , setting_type , value );
	}
	
	// direct connection
	public void setSettingValue0 ( UUID uuid , String name ,
			EnumPlayerSetting setting_type , NamespacedKey value ) throws Exception {
		try ( Connection connection = source.getConnection ( ) ) {
			setSettingValue0 ( connection , uuid , name , setting_type , value );
		}
	}
	
	// single connection
	public void setSettingValue0 ( Connection connection , UUID uuid ,
			String name , EnumPlayerSetting setting_type , NamespacedKey value ) throws Exception {
		set0 ( connection , SETTINGS_TABLE_NAME_FORMAT , uuid , name , setting_type.name ( ) , value.toString ( ) );
	}
	
	// ------------ cosmetics
	
	@Override
	public Map < UUID, Set < Cosmetic < ? > > > getStoredCosmetics ( ) throws Exception {
		try ( Connection connection = source.getConnection ( ) ) {
			Map < UUID, Set < Cosmetic < ? > > > result = new HashMap <> ( );
			
			for ( UUID uuid : queryIds ( connection ) ) {
				result.put ( uuid , getCosmetics0 ( connection , uuid ) );
			}
			
			return result;
		}
	}
	
	@Override
	public Set < Cosmetic < ? > > getCosmetics ( UUID uuid ) throws Exception {
		try ( Connection connection = source.getConnection ( ) ) {
			return getCosmetics0 ( connection , uuid );
		}
	}
	
	// single connection
	protected Set < Cosmetic < ? > > getCosmetics0 ( Connection connection , UUID uuid ) throws Exception {
		Set < Cosmetic < ? > > result = new HashSet <> ( );
		
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"SELECT %s FROM %s WHERE %s = ?" ,
				VALUE_COLUMN , COSMETICS_TABLE_NAME , UID_COLUMN ) ) ) {
			
			statement.setString ( 1 , uuid.toString ( ) );
			
			try ( ResultSet result_set = statement.executeQuery ( ) ) {
				while ( result_set.next ( ) ) {
					String namespaced_key = result_set.getString ( VALUE_COLUMN );
					
					try {
						result.add ( Cosmetic.of ( NamespacedKey.of ( namespaced_key ) ) );
					} catch ( Exception ex ) {
						// invalid name spaced key
					}
				}
			}
		}
		return result;
	}
	
	@Override
	public void loadCosmetics ( PlayerDataStorage storage_player ) throws Exception {
		storage_player.getCosmetics ( ).addAll ( getCosmetics ( storage_player.getUniqueId ( ) ) );
	}
	
	@Override
	public void addCosmetic ( Player br_player , Cosmetic < ? > cosmetic ) throws Exception {
		addCosmetic0 ( br_player.getUniqueId ( ) , br_player.getName ( ) , cosmetic );
	}
	
	@Override
	public void addCosmetic ( PlayerDataStorage storage_player , Cosmetic < ? > cosmetic ) throws Exception {
		addCosmetic0 ( storage_player.getUniqueId ( ) , storage_player.getName ( ) , cosmetic );
	}
	
	protected void addCosmetic0 ( UUID uuid , String name , Cosmetic < ? > cosmetic ) throws SQLException {
		try ( Connection connection = source.getConnection ( ) ) {
			add1 ( connection , COSMETICS_TABLE_NAME , uuid , name , cosmetic.getKey ( ).toString ( ) );
		}
	}
	
	@Override
	public void removeCosmetic ( Player br_player , Cosmetic < ? > cosmetic ) throws Exception {
		removeCosmetic0 ( br_player.getUniqueId ( ) , cosmetic );
	}
	
	@Override
	public void removeCosmetic ( PlayerDataStorage storage_player , Cosmetic < ? > cosmetic ) throws Exception {
		removeCosmetic0 ( storage_player.getUniqueId ( ) , cosmetic );
	}
	
	protected void removeCosmetic0 ( UUID uuid , Cosmetic < ? > cosmetic ) throws SQLException {
		try ( Connection connection = source.getConnection ( ) ) {
			delete1 ( connection , COSMETICS_TABLE_NAME , uuid , cosmetic.getKey ( ).toString ( ) );
		}
	}
	
	// --------- utils
	
	protected void tableStructureCheck ( Connection connection , String name , String... structure )
			throws SQLException {
		if ( tableExists ( connection , name ) ) {
			int     index   = 0;
			boolean invalid = false;
			
			try ( PreparedStatement statement = connection.prepareStatement ( "DESCRIBE " + name ) ;
					ResultSet result = statement.executeQuery ( ) ) {
				while ( result.next ( ) && !invalid ) {
					String column_name = result.getString ( 1 );
					
					if ( index < structure.length ) {
						if ( !Objects.equals ( column_name , structure[ index ] ) ) {
							invalid = true;
						}
					} else {
						invalid = true;
					}
					
					index++;
				}
			}
			
			// the table will be dropped in case it is
			// not the right structure.
			if ( invalid ) {
				try ( PreparedStatement drop_statement = connection.prepareStatement (
						"DROP TABLE " + name ) ) {
					
					drop_statement.executeUpdate ( );
				}
			}
		}
	}
	
	protected boolean tableExists ( Connection connection , String tableName ) throws SQLException {
		// https://www.baeldung.com/jdbc-check-table-exists
		DatabaseMetaData meta = connection.getMetaData ( );
		ResultSet resultSet = meta.getTables ( null , null ,
											   tableName , new String[] { "TABLE" } );
		return resultSet.next ( );
	}
	
	// query for tables with structure: [ UID_COLUMN | KEY_COLUMN | VALUE_COLUMN ]
	protected Object query0 ( Connection connection , String table_name ,
			UUID uuid , String key , Object default_value ) throws Exception {
		Validate.notNull ( uuid , "uuid cannot be null" );
		Validate.notBlank ( table_name , "table_name cannot be null/blank" );
		
		List < Object > result = querySeveral0 ( connection , table_name , uuid , key );
		
		return result.isEmpty ( ) ? default_value : result.get ( 0 );
	}
	
	// query for tables with structure: [ UID_COLUMN | KEY_COLUMN | VALUE_COLUMN ]
	protected List < Object > querySeveral0 ( Connection connection , String table_name ,
			UUID uuid , String key ) throws Exception {
		
		Validate.notNull ( uuid , "uuid cannot be null" );
		Validate.notBlank ( table_name , "table_name cannot be null/blank" );
		
		List < Object > result = new ArrayList <> ( );
		
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"SELECT %s FROM %s WHERE %s = ? AND %s = ?" ,
				VALUE_COLUMN , table_name , UID_COLUMN , KEY_COLUMN ) ) ) {
			
			statement.setString ( 1 , uuid.toString ( ) );
			statement.setString ( 2 , key );
			
			try ( ResultSet result_set = statement.executeQuery ( ) ) {
				if ( result_set.next ( ) ) {
					Object next = result_set.getObject ( VALUE_COLUMN );
					
					if ( next != null ) {
						result.add ( next );
					}
				}
			}
		}
		
		return result;
	}
	
	// delete for tables with structure: [ UID_COLUMN | VALUE_COLUMN ]
	protected void delete1 ( Connection connection , String table_name , UUID uuid , Object value )
			throws SQLException {
		Validate.notNull ( value , "value cannot be null" );
		
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"DELETE FROM %s WHERE %s = ? AND %s = ?" ,
				table_name , UID_COLUMN , VALUE_COLUMN ) ) ) {
			
			statement.setString ( 1 , uuid.toString ( ) );
			statement.setObject ( 2 , value );
			
			statement.executeUpdate ( );
		}
	}
	
	// add for tables with structure: [ UID_COLUMN (PRIMARY) | VALUE_COLUMN (PRIMARY) ]
	protected void add1 ( Connection connection , String table_name , UUID uuid , String name , Object value )
			throws SQLException {
		Validate.notNull ( value , "value cannot be null" );
		
		// registering player id
		idCheck ( connection , uuid , name );
		
		// then inserting
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"INSERT INTO %s (%s, %s) VALUES (?, ?) ON DUPLICATE KEY UPDATE %s = ?" ,
				table_name , UID_COLUMN , VALUE_COLUMN , VALUE_COLUMN ) ) ) {
			
			statement.setString ( 1 , uuid.toString ( ) );
			statement.setObject ( 2 , value );
			statement.setObject ( 3 , value );
			
			statement.executeUpdate ( );
		}
	}
	
	// set for tables with structure: [ UID_COLUMN | KEY_COLUMN | VALUE_COLUMN ]
	protected void set0 ( Connection connection , String table_name ,
			UUID uuid , String name , String key , Object value ) throws SQLException {
		Validate.notNull ( value , "value cannot be null" );
		
		// registering player id
		idCheck ( connection , uuid , name );
		
		// then inserting
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE %s = ?" ,
				table_name , UID_COLUMN , KEY_COLUMN , VALUE_COLUMN , VALUE_COLUMN ) ) ) {
			
			statement.setString ( 1 , uuid.toString ( ) );
			statement.setString ( 2 , key );
			statement.setObject ( 3 , value );
			statement.setObject ( 4 , value );
			
			statement.executeUpdate ( );
		}
	}
	
	protected void idCheck ( Connection connection , UUID uuid , String name ) throws SQLException {
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"INSERT INTO %s (%s, %s) VALUES (?, ?) ON DUPLICATE KEY UPDATE %s = ?" ,
				PLAYER_ID_TABLE_NAME , UID_COLUMN , NAME_COLUMN , NAME_COLUMN ) ) ) {
			
			statement.setString ( 1 , uuid.toString ( ) );
			statement.setString ( 2 , name );
			statement.setString ( 3 , name );
			
			statement.executeUpdate ( );
		}
	}
	
	// query for stored player unique ids (UUID)
	protected Set < UUID > queryIds ( Connection connection ) throws Exception {
		Set < UUID > ids = new HashSet <> ( );
		
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"SELECT %s FROM %s" , UID_COLUMN , PLAYER_ID_TABLE_NAME ) ) ) {
			try ( ResultSet result_set = statement.executeQuery ( ) ) {
				while ( result_set.next ( ) ) {
					try {
						ids.add ( UUID.fromString ( result_set.getString ( UID_COLUMN ) ) );
					} catch ( IllegalArgumentException ex ) {
						// ignore invalid uuid.
					}
				}
			}
		}
		
		return ids;
	}
	
	@Override
	public void dispose ( ) {
		if ( source != null ) {
			source.close ( );
		}
	}
}