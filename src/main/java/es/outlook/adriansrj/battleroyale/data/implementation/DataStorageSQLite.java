package es.outlook.adriansrj.battleroyale.data.implementation;

import es.outlook.adriansrj.battleroyale.cosmetic.Cosmetic;
import es.outlook.adriansrj.battleroyale.data.DataStorage;
import es.outlook.adriansrj.battleroyale.enums.EnumDependency;
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

import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.*;

/**
 * SQLite data storage system.
 *
 * @author AdrianSR / 15/09/2021 / 06:26 p. m.
 */
public class DataStorageSQLite implements DataStorage {
	
	/**
	 * SQLite's jdbc URL format.
	 */
	protected static final String URL_FORMAT = "jdbc:sqlite:%s";
	
	protected static final String PLAYER_ID_TABLE_NAME       = "br_player_id";
	protected static final String STATS_TABLE_NAME           = "br_player_stats";
	protected static final String SETTINGS_TABLE_NAME_FORMAT = "br_player_settings";
	protected static final String COSMETICS_TABLE_NAME       = "br_player_cosmetics";
	protected static final String UID_COLUMN                 = "UUID";
	protected static final String NAME_COLUMN                = "NAME";
	protected static final String KEY_COLUMN                 = "_KEY";
	protected static final String VALUE_COLUMN               = "VALUE";
	
	protected final String     file_path;
	protected final String     connection_url;
	protected       Connection connection;
	
	public DataStorageSQLite ( BattleRoyale plugin ) {
		this.file_path      = EnumMainConfiguration.DATABASE_SQLITE_PATH.getAsString ( );
		this.connection_url = String.format ( URL_FORMAT , file_path );
	}
	
	@Override
	public boolean setUp ( ) throws SQLException {
		// connecting
		try {
			URLClassLoader class_loader     = EnumDependency.SQLITE_DRIVER.getLoader ( );
			Class < ? >    connection_class = class_loader.loadClass ( "org.sqlite.jdbc4.JDBC4Connection" );
			
			this.connection = ( Connection ) connection_class.getConstructor (
							String.class , String.class , Properties.class )
					.newInstance ( connection_url , file_path , new Properties ( ) );
		} catch ( ClassNotFoundException | NoSuchMethodException | InvocationTargetException
				| InstantiationException | IllegalAccessException e ) {
			e.printStackTrace ( );
			return false;
		}
		
		// checking structure of the tables
		tablesStructureCheck ( );
		// creating not existing tables
		tablesCheck ( );
		
		return true;
	}
	
	protected void tablesCheck ( ) throws SQLException {
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
				UID_COLUMN , PLAYER_ID_TABLE_NAME , UID_COLUMN ) ) ) {
			
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
	
	protected void tablesStructureCheck ( ) throws SQLException {
		// checking structure of the stats table
		tableStructureCheck ( STATS_TABLE_NAME , UID_COLUMN , KEY_COLUMN , VALUE_COLUMN );
		// checking structure of the settings table
		tableStructureCheck ( SETTINGS_TABLE_NAME_FORMAT , UID_COLUMN , KEY_COLUMN , VALUE_COLUMN );
		// checking structure of the cosmetics table
		tableStructureCheck ( COSMETICS_TABLE_NAME , UID_COLUMN , VALUE_COLUMN );
	}
	
	@Override
	public Set < PlayerDataStorage > getStoredPlayers ( ) throws Exception {
		Set < PlayerDataStorage > result = new HashSet <> ( );
		
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
			loadStatValues ( storage_player );
			// lading settings
			loadSettingValues ( storage_player );
			// loading cosmetics
			loadCosmetics ( storage_player );
		}
		
		return result;
	}
	
	@Override
	public Map < UUID, Map < EnumStat, Integer > > getStoredStatValues ( ) throws Exception {
		Map < UUID, Map < EnumStat, Integer > > result = new HashMap <> ( );
		
		for ( UUID uuid : queryIds ( ) ) {
			result.put ( uuid , getStatValues ( uuid ) );
		}
		
		return result;
	}
	
	@Override
	public Map < EnumStat, Integer > getStatValues ( UUID uuid ) throws Exception {
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
		Validate.notNull ( uuid , "uuid cannot be null" );
		Validate.notNull ( stat_type , "stat type cannot be null" );
		
		return ( int ) query0 ( STATS_TABLE_NAME , uuid , stat_type.name ( ) , 0 );
	}
	
	@Override
	public void loadStatValues ( PlayerDataStorage storage_player ) throws Exception {
		storage_player.setStats ( getStatValues ( storage_player.getUniqueId ( ) ) );
	}
	
	@Override
	public void setStatValue ( PlayerDataStorage storage_player , EnumStat stat_type , int value )
			throws Exception {
		Validate.notNull ( storage_player , "player cannot be null" );
		
		set0 ( STATS_TABLE_NAME , storage_player.getUniqueId ( ) , storage_player.getName ( ) ,
			   stat_type.name ( ) , value );
	}
	
	@Override
	public void setStatValue ( Player br_player , EnumStat stat_type , int value ) throws Exception {
		Validate.notNull ( br_player , "player cannot be null" );
		
		set0 ( STATS_TABLE_NAME , br_player.getUniqueId ( ) , br_player.getName ( ) ,
			   stat_type.name ( ) , value );
	}
	
	// ---------- settings
	
	@Override
	public Map < UUID, Map < EnumPlayerSetting, NamespacedKey > > getStoredSettingValues ( ) throws Exception {
		Map < UUID, Map < EnumPlayerSetting, NamespacedKey > > result = new HashMap <> ( );
		
		for ( UUID uuid : queryIds ( ) ) {
			result.put ( uuid , getSettingValues ( uuid ) );
		}
		
		return result;
	}
	
	@Override
	public Map < EnumPlayerSetting, NamespacedKey > getSettingValues ( UUID uuid ) throws Exception {
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
		Validate.notNull ( uuid , "uuid cannot be null" );
		Validate.notNull ( setting_type , "setting type cannot be null" );
		
		return NamespacedKey.of (
				query0 ( SETTINGS_TABLE_NAME_FORMAT ,
						 uuid , setting_type.name ( ) , setting_type.getDefaultValue ( ) ).toString ( ) );
	}
	
	@Override
	public void loadSettingValues ( PlayerDataStorage storage_player ) throws Exception {
		storage_player.setSettings ( getSettingValues ( storage_player.getUniqueId ( ) ) );
	}
	
	@Override
	public void setSettingValue ( PlayerDataStorage storage_player , EnumPlayerSetting setting_type ,
			NamespacedKey value ) throws Exception {
		set0 ( SETTINGS_TABLE_NAME_FORMAT , storage_player.getUniqueId ( ) , storage_player.getName ( ) ,
			   setting_type.name ( ) , value.toString ( ) );
	}
	
	@Override
	public void setSettingValue ( Player br_player , EnumPlayerSetting setting_type ,
			NamespacedKey value ) throws Exception {
		set0 ( SETTINGS_TABLE_NAME_FORMAT , br_player.getUniqueId ( ) , br_player.getName ( ) ,
			   setting_type.name ( ) , value.toString ( ) );
	}
	
	// ------------ cosmetics
	
	@Override
	public Map < UUID, Set < Cosmetic < ? > > > getStoredCosmetics ( ) throws Exception {
		Map < UUID, Set < Cosmetic < ? > > > result = new HashMap <> ( );
		
		for ( UUID uuid : queryIds ( ) ) {
			result.put ( uuid , getCosmetics ( uuid ) );
		}
		
		return result;
	}
	
	@Override
	public Set < Cosmetic < ? > > getCosmetics ( UUID uuid ) throws Exception {
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
		add1 ( COSMETICS_TABLE_NAME , br_player.getUniqueId ( ) , br_player.getName ( ) ,
			   cosmetic.getKey ( ).toString ( ) );
	}
	
	@Override
	public void addCosmetic ( PlayerDataStorage storage_player , Cosmetic < ? > cosmetic ) throws Exception {
		add1 ( COSMETICS_TABLE_NAME , storage_player.getUniqueId ( ) , storage_player.getName ( ) ,
			   cosmetic.getKey ( ).toString ( ) );
	}
	
	@Override
	public void removeCosmetic ( Player br_player , Cosmetic < ? > cosmetic ) throws Exception {
		delete1 ( COSMETICS_TABLE_NAME , br_player.getUniqueId ( ) , cosmetic.getKey ( ).toString ( ) );
	}
	
	@Override
	public void removeCosmetic ( PlayerDataStorage storage_player , Cosmetic < ? > cosmetic ) throws Exception {
		delete1 ( COSMETICS_TABLE_NAME , storage_player.getUniqueId ( ) , cosmetic.getKey ( ).toString ( ) );
	}
	
	// --------- utils
	
	protected void tableStructureCheck ( String name , String... structure ) throws SQLException {
		if ( tableExists ( name ) ) {
			int     index   = 0;
			boolean invalid = false;
			
			try ( PreparedStatement statement = connection.prepareStatement ( "PRAGMA table_info([" + name + "])" ) ;
					ResultSet result = statement.executeQuery ( ) ) {
				while ( result.next ( ) && !invalid ) {
					String column_name = result.getString ( 2 );
					
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
				try ( PreparedStatement drop_statement = this.connection.prepareStatement (
						"DROP TABLE " + name ) ) {
					
					drop_statement.executeUpdate ( );
				}
			}
		}
	}
	
	protected boolean tableExists ( String table ) throws SQLException {
		DatabaseMetaData meta   = connection.getMetaData ( );
		boolean          exists = false;
		
		// unlike MySQL, we have to make sure that the result set
		// will be closed, otherwise, the table will get locked.
		try ( ResultSet result = meta.getTables ( null , null , table , null ) ) {
			exists = result.next ( );
		}
		
		return exists;
	}
	
	// query for tables with structure: [ UID_COLUMN | KEY_COLUMN | VALUE_COLUMN ]
	protected Object query0 ( String table_name ,
			UUID uuid , String key , Object default_value ) throws Exception {
		Validate.notNull ( uuid , "uuid cannot be null" );
		Validate.notBlank ( table_name , "table_name cannot be null/blank" );
		
		List < Object > result = querySeveral0 ( table_name , uuid , key );
		
		return result.isEmpty ( ) ? default_value : result.get ( 0 );
	}
	
	// query for tables with structure: [ UID_COLUMN | KEY_COLUMN | VALUE_COLUMN ]
	protected List < Object > querySeveral0 ( String table_name , UUID uuid , String key ) throws Exception {
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
	
	// delete for tables with structure: [ UID_COLUMN (PRIMARY) | VALUE_COLUMN (PRIMARY) ]
	protected void delete1 ( String table_name , UUID uuid , Object value )
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
	protected void add1 ( String table_name , UUID uuid , String name , Object value )
			throws SQLException {
		Validate.notNull ( value , "value cannot be null" );
		
		// registering player id
		idCheck ( uuid , name );
		
		// then inserting
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"INSERT INTO %s (%s, %s) VALUES (?, ?) ON CONFLICT (%s) DO UPDATE SET %s = ?" ,
				table_name , UID_COLUMN , VALUE_COLUMN , UID_COLUMN , VALUE_COLUMN ) ) ) {
			
			statement.setString ( 1 , uuid.toString ( ) );
			statement.setObject ( 2 , value );
			statement.setObject ( 3 , value );
			
			statement.executeUpdate ( );
		}
	}
	
	// set for tables with structure: [ UID_COLUMN | KEY_COLUMN | VALUE_COLUMN ]
	protected void set0 ( String table_name , UUID uuid ,
			String name , String key , Object value ) throws SQLException {
		Validate.notNull ( value , "value cannot be null" );
		
		// registering player id
		idCheck ( uuid , name );
		
		// then inserting
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?) ON CONFLICT (%s, %s) DO UPDATE SET %s = ?" ,
				table_name , UID_COLUMN , KEY_COLUMN , VALUE_COLUMN , UID_COLUMN , KEY_COLUMN , VALUE_COLUMN ) ) ) {
			
			statement.setString ( 1 , uuid.toString ( ) );
			statement.setString ( 2 , key );
			statement.setObject ( 3 , value );
			statement.setObject ( 4 , value );
			
			statement.executeUpdate ( );
		}
	}
	
	protected void idCheck ( UUID uuid , String name ) throws SQLException {
		try ( PreparedStatement statement = connection.prepareStatement ( String.format (
				"INSERT INTO %s (%s, %s) VALUES (?, ?) ON CONFLICT (%s) DO UPDATE SET %s = ?" ,
				PLAYER_ID_TABLE_NAME , UID_COLUMN , NAME_COLUMN , UID_COLUMN , NAME_COLUMN ) ) ) {
			
			statement.setString ( 1 , uuid.toString ( ) );
			statement.setString ( 2 , name );
			statement.setString ( 3 , name );
			
			statement.executeUpdate ( );
		}
	}
	
	// query for stored player unique ids (UUID)
	protected Set < UUID > queryIds ( ) throws Exception {
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
		if ( connection != null ) {
			try {
				connection.close ( );
			} catch ( SQLException e ) {
				e.printStackTrace ( );
			}
		}
	}
}
