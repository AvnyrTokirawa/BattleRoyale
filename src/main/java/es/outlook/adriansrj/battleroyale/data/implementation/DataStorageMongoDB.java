package es.outlook.adriansrj.battleroyale.data.implementation;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import es.outlook.adriansrj.battleroyale.cosmetic.Cosmetic;
import es.outlook.adriansrj.battleroyale.data.DataStorage;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.enums.EnumPlayerSetting;
import es.outlook.adriansrj.battleroyale.enums.EnumStat;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.battleroyale.player.PlayerDataStorage;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bukkit.ChatColor;

import java.util.*;

/**
 * MongoDB data storage system.
 *
 * @author AdrianSR / 15/09/2021 / 06:28 p. m.
 */
public class DataStorageMongoDB implements DataStorage {
	
	protected static final String COLLECTION_NAME      = "br_players";
	protected static final String ID_FIELD_NAME        = "_id";
	protected static final String NAME_FIELD_NAME      = "name";
	protected static final String COSMETICS_FIELD_NAME = "cosmetics";
	
	protected final BattleRoyale  plugin;
	protected       MongoClient   connection;
	protected       MongoDatabase database;
	
	public DataStorageMongoDB ( BattleRoyale plugin ) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean setUp ( ) {
		String connection_uri = EnumMainConfiguration.DATABASE_MONGODB_URI.getAsString ( );
		String database_name  = EnumMainConfiguration.DATABASE_MONGODB_DATABASE.getAsString ( );
		
		if ( StringUtil.isNotBlank ( database_name ) ) {
			if ( StringUtil.isNotBlank ( connection_uri ) ) {
				this.connection = MongoClients.create (
						MongoClientSettings.builder ( )
								.applyConnectionString ( new ConnectionString ( connection_uri ) )
								.uuidRepresentation ( UuidRepresentation.STANDARD )
								.build ( ) );
			} else {
				String host = EnumMainConfiguration.DATABASE_MONGODB_HOST.getAsString ( );
				int    port = EnumMainConfiguration.DATABASE_MONGODB_PORT.getAsInteger ( );
				
				if ( StringUtil.isNotBlank ( host ) ) {
					this.connection = MongoClients.create (
							MongoClientSettings.builder ( )
									.applyToClusterSettings (
											builder -> builder.hosts (
													Arrays.asList ( new ServerAddress ( host , port ) ) ) )
									.uuidRepresentation ( UuidRepresentation.STANDARD )
									.build ( ) );
				}
			}
			
			if ( connection != null ) {
				this.database = connection.getDatabase ( database_name );
				return true;
			} else {
				return false;
			}
		} else {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED ,
											"Invalid MongoDB database name!" , plugin );
			return false;
		}
	}
	
	@Override
	public Set < PlayerDataStorage > getStoredPlayers ( ) {
		Set < PlayerDataStorage > values = new HashSet <> ( );
		
		for ( Document document : getCollection ( ).find ( ) ) {
			UUID   uuid = null;
			String name = null;
			
			if ( document.containsKey ( ID_FIELD_NAME ) && document.containsKey ( NAME_FIELD_NAME ) ) {
				uuid = document.get ( ID_FIELD_NAME , UUID.class );
				name = document.getString ( NAME_FIELD_NAME );
			}
			
			// uuid cannot be null and name must not be blank
			if ( uuid == null || StringUtil.isBlank ( name ) ) {
				continue;
			}
			
			PlayerDataStorage player = new PlayerDataStorage ( uuid , name );
			
			// extracting stats
			player.setStats ( extractStatValues ( document ) );
			// extracting settings
			player.setSettings ( extractSettingValues ( document ) );
			// extracting cosmetics
			player.getCosmetics ( ).addAll ( extractCosmetics ( document ) );
			
			// then including
			values.add ( player );
		}
		
		return values;
	}
	
	// ------ stats
	
	@Override
	public Map < UUID, Map < EnumStat, Integer > > getStoredStatValues ( ) {
		Map < UUID, Map < EnumStat, Integer > > result = new HashMap <> ( );
		
		for ( Document document : getCollection ( ).find ( ) ) {
			UUID uuid = null;
			
			// extracting uuid
			if ( document.containsKey ( ID_FIELD_NAME ) ) {
				uuid = document.get ( ID_FIELD_NAME , UUID.class );
			} else {
				continue;
			}
			
			// extracting stat values
			result.put ( uuid , extractStatValues ( document ) );
		}
		return result;
	}
	
	@Override
	public Map < EnumStat, Integer > getStatValues ( UUID uuid ) throws Exception {
		Document result = getCollection ( ).find ( Filters.eq ( ID_FIELD_NAME , uuid ) ).first ( );
		
		return result != null ? extractStatValues ( result ) : new EnumMap <> ( EnumStat.class );
	}
	
	@Override
	public int getStatValue ( UUID uuid , EnumStat stat_type ) {
		Document result = getCollection ( ).find ( Filters.eq ( ID_FIELD_NAME , uuid ) ).first ( );
		String   key    = stat_type.name ( ).toLowerCase ( );
		
		if ( result != null && result.containsKey ( key ) ) {
			return result.getInteger ( key );
		} else {
			return 0;
		}
	}
	
	@Override
	public void loadStatValues ( PlayerDataStorage storage_player ) throws Exception {
		storage_player.setStats ( getStatValues ( storage_player.getUniqueId ( ) ) );
	}
	
	@Override
	public void setStatValue ( PlayerDataStorage storage_player , EnumStat stat_type , int value ) {
		set0 ( getCollection ( ) , storage_player.getUniqueId ( ) , storage_player.getName ( ) , stat_type , value );
	}
	
	@Override
	public void setStatValue ( Player br_player , EnumStat stat_type , int value ) {
		set0 ( getCollection ( ) , br_player.getUniqueId ( ) , br_player.getName ( ) , stat_type , value );
	}
	
	// ------ settings
	
	@Override
	public Map < UUID, Map < EnumPlayerSetting, NamespacedKey > > getStoredSettingValues ( ) {
		Map < UUID, Map < EnumPlayerSetting, NamespacedKey > > result = new HashMap <> ( );
		
		for ( Document document : getCollection ( ).find ( ) ) {
			UUID uuid = null;
			
			// extracting uuid
			if ( document.containsKey ( ID_FIELD_NAME ) ) {
				uuid = document.get ( ID_FIELD_NAME , UUID.class );
			} else {
				continue;
			}
			
			// extracting setting values
			result.put ( uuid , extractSettingValues ( document ) );
		}
		
		return result;
	}
	
	@Override
	public Map < EnumPlayerSetting, NamespacedKey > getSettingValues ( UUID uuid ) {
		Document result = getCollection ( ).find ( Filters.eq ( ID_FIELD_NAME , uuid ) ).first ( );
		
		return result != null ? extractSettingValues ( result ) : new EnumMap <> ( EnumPlayerSetting.class );
	}
	
	@Override
	public NamespacedKey getSettingValue ( UUID uuid , EnumPlayerSetting setting_type ) {
		Document result = getCollection ( ).find ( Filters.eq ( ID_FIELD_NAME , uuid ) ).first ( );
		String   key    = setting_type.name ( ).toLowerCase ( );
		
		if ( result != null && result.containsKey ( key ) ) {
			return NamespacedKey.of ( result.getString ( key ) );
		} else {
			return setting_type.getDefaultValue ( );
		}
	}
	
	@Override
	public void loadSettingValues ( PlayerDataStorage storage_player ) throws Exception {
		storage_player.setSettings ( getSettingValues ( storage_player.getUniqueId ( ) ) );
	}
	
	@Override
	public void setSettingValue ( PlayerDataStorage storage_player , EnumPlayerSetting setting_type ,
			NamespacedKey value ) {
		set0 ( getCollection ( ) , storage_player.getUniqueId ( ) , storage_player.getName ( ) ,
			   setting_type , value.toString ( ) );
	}
	
	@Override
	public void setSettingValue ( Player br_player , EnumPlayerSetting setting_type ,
			NamespacedKey value ) {
		set0 ( getCollection ( ) , br_player.getUniqueId ( ) , br_player.getName ( ) ,
			   setting_type , value.toString ( ) );
	}
	
	@Override
	public Map < UUID, Set < Cosmetic < ? > > > getStoredCosmetics ( ) throws Exception {
		Map < UUID, Set < Cosmetic < ? > > > result = new HashMap <> ( );
		
		for ( Document document : getCollection ( ).find ( ) ) {
			UUID uuid = null;
			
			// extracting uuid
			if ( document.containsKey ( ID_FIELD_NAME ) ) {
				uuid = document.get ( ID_FIELD_NAME , UUID.class );
			} else {
				continue;
			}
			
			// extracting cosmetics
			result.put ( uuid , extractCosmetics ( document ) );
		}
		
		return result;
	}
	
	@Override
	public Set < Cosmetic < ? > > getCosmetics ( UUID uuid ) {
		Document query = getCollection ( ).find ( Filters.eq ( ID_FIELD_NAME , uuid ) ).first ( );
		
		return query != null ? extractCosmetics ( query ) : new HashSet <> ( );
	}
	
	@Override
	public void loadCosmetics ( PlayerDataStorage storage_player ) {
		storage_player.getCosmetics ( ).addAll ( getCosmetics ( storage_player.getUniqueId ( ) ) );
	}
	
	@Override
	public void addCosmetic ( Player br_player , Cosmetic < ? > cosmetic ) {
		addToSet0 ( getCollection ( ) , br_player.getUniqueId ( ) , br_player.getName ( ) ,
					COSMETICS_FIELD_NAME , cosmetic.getKey ( ).toString ( ) );
	}
	
	@Override
	public void addCosmetic ( PlayerDataStorage storage_player , Cosmetic < ? > cosmetic ) {
		addToSet0 ( getCollection ( ) , storage_player.getUniqueId ( ) , storage_player.getName ( ) ,
					COSMETICS_FIELD_NAME , cosmetic.getKey ( ).toString ( ) );
	}
	
	@Override
	public void removeCosmetic ( Player br_player , Cosmetic < ? > cosmetic ) {
		pull0 ( getCollection ( ) , br_player.getUniqueId ( ) ,
				COSMETICS_FIELD_NAME , cosmetic.getKey ( ).toString ( ) );
	}
	
	@Override
	public void removeCosmetic ( PlayerDataStorage storage_player , Cosmetic < ? > cosmetic ) {
		pull0 ( getCollection ( ) , storage_player.getUniqueId ( ) ,
				COSMETICS_FIELD_NAME , cosmetic.getKey ( ).toString ( ) );
	}
	
	// ----- util methods
	
	protected Map < EnumStat, Integer > extractStatValues ( Document document ) {
		Map < EnumStat, Integer > values = new EnumMap <> ( EnumStat.class );
		
		for ( EnumStat stat_type : EnumStat.values ( ) ) {
			String key = stat_type.name ( ).toLowerCase ( );
			
			if ( document.containsKey ( key ) ) {
				values.put ( stat_type , document.getInteger ( key ) );
			}
		}
		return values;
	}
	
	protected Map < EnumPlayerSetting, NamespacedKey > extractSettingValues ( Document document ) {
		Map < EnumPlayerSetting, NamespacedKey > values = new EnumMap <> ( EnumPlayerSetting.class );
		
		for ( EnumPlayerSetting setting_type : EnumPlayerSetting.values ( ) ) {
			String key = setting_type.name ( ).toLowerCase ( );
			
			if ( document.containsKey ( key ) ) {
				values.put ( setting_type , NamespacedKey.of ( document.getString ( key ) ) );
			}
		}
		return values;
	}
	
	protected Set < Cosmetic < ? > > extractCosmetics ( Document document ) {
		Set < Cosmetic < ? > > result = new HashSet <> ( );
		
		if ( document.containsKey ( COSMETICS_FIELD_NAME ) ) {
			List < String > keys = document.getList ( COSMETICS_FIELD_NAME , String.class );
			
			keys.stream ( ).map ( NamespacedKey :: of ).map ( key -> {
				try {
					return Cosmetic.of ( key );
				} catch ( UnsupportedOperationException ex ) {
					// for any reason there is
					// a cosmetic with an unknown
					// name space stored in the database
					return null;
				}
			} ).filter ( Objects :: nonNull ).forEach ( result :: add );
		}
		
		return result;
	}
	
	protected void addToSet0 ( MongoCollection < Document > collection , UUID uuid , String name ,
			String array , Object value ) {
		// making sure player is registered
		collection.updateOne (
				Filters.eq ( ID_FIELD_NAME , uuid ) ,
				// will set the uuid and name
				new Document ( "$set" , new Document ( )
						.append ( ID_FIELD_NAME , uuid )
						.append ( NAME_FIELD_NAME , name ) ) ,
				// upsert must be enabled
				new UpdateOptions ( ).upsert ( true ) );
		
		// then adding
		collection.updateOne (
				Filters.eq ( ID_FIELD_NAME , uuid ) ,
				Updates.addToSet ( array , value ) );
	}
	
	protected void pull0 ( MongoCollection < Document > collection , UUID uuid , String array , Object target ) {
		collection.updateOne (
				Filters.eq ( ID_FIELD_NAME , uuid ) ,
				Updates.pull ( array , target ) );
	}
	
	protected void set0 ( MongoCollection < Document > collection , UUID uuid , String name ,
			Enum < ? > key , Object value ) {
		set0 ( collection , uuid , name , key.name ( ).toLowerCase ( ) , value );
	}
	
	protected void set0 ( MongoCollection < Document > collection , UUID uuid , String name ,
			String key , Object value ) {
		collection.updateOne (
				// filter
				new Document ( ID_FIELD_NAME , uuid ) ,
				// the update
				new Document ( "$set" , new Document ( )
						.append ( ID_FIELD_NAME , uuid )
						.append ( NAME_FIELD_NAME , name )
						.append ( key , value ) ) ,
				// upsert must be enabled
				new UpdateOptions ( ).upsert ( true ) );
	}
	
	protected MongoCollection < Document > getCollection ( ) {
		return database.getCollection ( COLLECTION_NAME );
	}
	
	@Override
	public void dispose ( ) {
		if ( connection != null ) {
			connection.close ( );
		}
	}
}