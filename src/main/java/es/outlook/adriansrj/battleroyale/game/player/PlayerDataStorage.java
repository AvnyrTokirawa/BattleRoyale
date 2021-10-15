package es.outlook.adriansrj.battleroyale.game.player;

import es.outlook.adriansrj.battleroyale.cosmetic.Cosmetic;
import es.outlook.adriansrj.battleroyale.data.DataStorage;
import es.outlook.adriansrj.battleroyale.data.DataStorageHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.enums.EnumPlayerSetting;
import es.outlook.adriansrj.battleroyale.enums.EnumStat;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;
import es.outlook.adriansrj.battleroyale.util.Validate;

import java.util.*;

/**
 * Battle royale {@link Player} data storage.
 *
 * @author AdrianSR / 15/09/2021 / 06:13 p. m.
 */
public final class PlayerDataStorage {
	
	private final UUID   uuid;
	private final String name;
	
	// stat map
	private final Map < EnumStat, Integer >                stat_values      = new EnumMap <> ( EnumStat.class );
	// temporal stat map
	private final Map < EnumStat, Integer >                temp_stat_values = new EnumMap <> ( EnumStat.class );
	// setting map
	private final Map < EnumPlayerSetting, NamespacedKey > setting_values   = new EnumMap <> ( EnumPlayerSetting.class );
	// purchased cosmetics
	private final Set < Cosmetic < ? > >                   cosmetics        = new HashSet <> ( );
	
	// flag that determines whether something has changed and
	// an update might be required.
	volatile boolean dirty;
	
	public PlayerDataStorage ( UUID uuid , String name ) {
		this.uuid = uuid;
		this.name = name;
		
		// mapping 0 as default stat values, so it
		// will never return null.
		for ( EnumStat stat_type : EnumStat.values ( ) ) {
			stat_values.put ( stat_type , 0 );
			temp_stat_values.put ( stat_type , 0 );
		}
	}
	
	public UUID getUniqueId ( ) {
		return uuid;
	}
	
	public String getName ( ) {
		return name;
	}
	
	public Player getPlayer ( ) {
		return Player.getPlayer ( uuid );
	}
	
	// ------- stats
	
	public Map < EnumStat, Integer > getStats ( ) {
		return Collections.unmodifiableMap ( stat_values );
	}
	
	public Map < EnumStat, Integer > getTempStats ( ) {
		return Collections.unmodifiableMap ( temp_stat_values );
	}
	
	public int getStat ( EnumStat stat_type ) {
		return stat_values.get ( stat_type );
	}
	
	public int getTempStat ( EnumStat stat_type ) {
		return temp_stat_values.get ( stat_type );
	}
	
	public void setStat ( EnumStat stat_type , int value , boolean upload ) {
		Validate.notNull ( stat_type , "stat type cannot be null" );
		Validate.isTrue ( value >= 0 , "value must be >= 0" );
		
		this.stat_values.put ( stat_type , value );
		this.dirty = true;
		
		// then uploading
		if ( upload && EnumMainConfiguration.ENABLE_DATABASE.getAsBoolean ( ) ) {
			try {
				DataStorageHandler.getInstance ( ).getDataStorage ( )
						.setStatValue ( this , stat_type , value );
			} catch ( Exception e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	public void setTempStat ( EnumStat stat_type , int value ) {
		Validate.notNull ( stat_type , "stat type cannot be null" );
		Validate.isTrue ( value >= 0 , "value must be >= 0" );
		
		this.temp_stat_values.put ( stat_type , value );
		this.dirty = true;
	}
	
	public void setStat ( EnumStat stat_type , int value ) {
		setStat ( stat_type , value , false );
	}
	
	public void incrementStat ( EnumStat stat_type , int amount , boolean upload ) {
		Validate.notNull ( stat_type , "stat type cannot be null" );
		
		setStat ( stat_type , Math.max ( getStat ( stat_type ) + amount , 0 ) , upload );
	}
	
	public void incrementTempStat ( EnumStat stat_type , int amount ) {
		Validate.notNull ( stat_type , "stat type cannot be null" );
		
		setTempStat ( stat_type , Math.max ( getTempStat ( stat_type ) + amount , 0 ) );
	}
	
	public void incrementStat ( EnumStat stat_type , int amount ) {
		incrementStat ( stat_type , amount , false );
	}
	
	public void decrementStat ( EnumStat stat_type , int amount , boolean upload ) {
		Validate.notNull ( stat_type , "stat type cannot be null" );
		
		setStat ( stat_type , Math.max ( getStat ( stat_type ) - amount , 0 ) , upload );
	}
	
	public void decrementTempStat ( EnumStat stat_type , int amount ) {
		Validate.notNull ( stat_type , "stat type cannot be null" );
		
		setTempStat ( stat_type , Math.max ( getTempStat ( stat_type ) - amount , 0 ) );
	}
	
	public void decrementStat ( EnumStat stat_type , int amount ) {
		decrementStat ( stat_type , amount , false );
	}
	
	public void setStats ( Map < EnumStat, Integer > stat_values ) {
		stat_values.forEach ( ( stat_type , value ) -> {
			if ( value >= 0 ) {
				this.setStat ( stat_type , value );
			}
		} );
	}
	
	public void setTempStats ( Map < EnumStat, Integer > stat_values ) {
		stat_values.forEach ( ( stat_type , value ) -> {
			if ( value >= 0 ) {
				this.setTempStat ( stat_type , value );
			}
		} );
	}
	
	public void resetTempStats ( ) {
		for ( EnumStat stat_type : EnumStat.values ( ) ) {
			temp_stat_values.put ( stat_type , 0 );
		}
	}
	
	// ------- settings
	
	public Map < EnumPlayerSetting, NamespacedKey > getSettings ( ) {
		return Collections.unmodifiableMap ( setting_values );
	}
	
	public NamespacedKey getSetting ( EnumPlayerSetting setting_type ) {
		NamespacedKey value = setting_values.get ( setting_type );
		
		return value == null ? setting_type.getDefaultValue ( ) : value;
	}
	
	public < T > T getSetting ( Class < T > clazz , EnumPlayerSetting setting_type ) {
		return clazz.cast ( setting_type.getValue ( clazz , getSetting ( setting_type ) ) );
	}
	
	/**
	 * <br>
	 * <b>Note that passing a <code>null</code> value is the
	 * same as passing the default value of the specified setting.</b>
	 *
	 * @param setting_type
	 * @param value
	 * @param upload whether to upload changes to database.
	 */
	public void setSetting ( EnumPlayerSetting setting_type , NamespacedKey value , boolean upload ) {
		Validate.notNull ( setting_type , "setting type cannot be null" );
		
		if ( value != null ) {
			setting_values.put ( setting_type , Validate.namespace (
					setting_type.getDefaultValue ( ).getNamespace ( ) , value ) );
		} else {
			setting_values.put ( setting_type , setting_type.getDefaultValue ( ) );
		}
		
		this.dirty = true;
		
		// then uploading
		if ( upload && EnumMainConfiguration.ENABLE_DATABASE.getAsBoolean ( ) ) {
			try {
				DataStorageHandler.getInstance ( ).getDataStorage ( )
						.setSettingValue ( this , setting_type , value );
			} catch ( Exception e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	public void setSetting ( EnumPlayerSetting setting_type , NamespacedKey value ) {
		setSetting ( setting_type , value , false );
	}
	
	public void setSettings ( Map < EnumPlayerSetting, NamespacedKey > setting_types , boolean upload ) {
		setting_types.forEach ( ( type , value ) -> setSetting ( type , value , upload ) );
	}
	
	public void setSettings ( Map < EnumPlayerSetting, NamespacedKey > setting_types ) {
		setSettings ( setting_types , false );
	}
	
	// ------- cosmetics
	
	public Set < Cosmetic < ? > > getCosmetics ( ) {
		return cosmetics;
	}
	
	public < T extends Cosmetic < ? > > Set < T > getCosmeticsByClass ( Class < T > type ) {
		Set < T > result = new HashSet <> ( );
		
		for ( Cosmetic < ? > cosmetic : cosmetics ) {
			if ( type.isAssignableFrom ( cosmetic.getValue ( ).getClass ( ) ) ) {
				result.add ( type.cast ( cosmetic ) );
			}
		}
		
		return result;
	}
	
	public < T > Set < Cosmetic < ? > > getCosmeticsByReturnType ( Class < T > return_type ) {
		Set < Cosmetic < ? > > result = new HashSet <> ( );
		
		for ( Cosmetic < ? > cosmetic : cosmetics ) {
			if ( return_type.isAssignableFrom ( cosmetic.getValue ( ).getClass ( ) ) ) {
				result.add ( cosmetic );
			}
		}
		
		return result;
	}
	
	public boolean hasCosmetic ( Cosmetic < ? > cosmetic ) {
		return cosmetics.contains ( cosmetic );
	}
	
	/**
	 *
	 * @param cosmetic
	 * @param upload whether to upload changes to database.
	 */
	public void addCosmetic ( Cosmetic < ? > cosmetic , boolean upload ) {
		cosmetics.add ( cosmetic );
		
		// then uploading
		if ( upload && EnumMainConfiguration.ENABLE_DATABASE.getAsBoolean ( ) ) {
			try {
				DataStorageHandler.getInstance ( ).getDataStorage ( )
						.addCosmetic ( this , cosmetic );
			} catch ( Exception e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	public void addCosmetic ( Cosmetic < ? > cosmetic ) {
		addCosmetic ( cosmetic , false );
	}
	
	/**
	 *
	 * @param cosmetic
	 * @param upload whether to upload changes to database.
	 */
	public void removeCosmetic ( Cosmetic < ? > cosmetic , boolean upload ) {
		cosmetics.remove ( cosmetic );
		
		// then uploading
		if ( upload && EnumMainConfiguration.ENABLE_DATABASE.getAsBoolean ( ) ) {
			try {
				DataStorageHandler.getInstance ( ).getDataStorage ( )
						.removeCosmetic ( this , cosmetic );
			} catch ( Exception e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	public void removeCosmetic ( Cosmetic < ? > cosmetic ) {
		removeCosmetic ( cosmetic , false );
	}
	
	/**
	 * Fetches the values from the database.
	 *
	 * <strong>This means that this method will fetch the values that corresponds to this
	 * player from the database.<strong/>
	 */
	public void fetch ( ) {
		Validate.isTrue ( EnumMainConfiguration.ENABLE_DATABASE.getAsBoolean ( ) , "database not enabled" );
		
		DataStorage database = DataStorageHandler.getInstance ( ).getDataStorage ( );
		
		// fetching stats
		try {
			database.loadStatValues ( this );
		} catch ( Exception e ) {
			e.printStackTrace ( );
		}
		
		// fetching settings
		try {
			database.loadSettingValues ( this );
		} catch ( Exception e ) {
			e.printStackTrace ( );
		}
		
		// fetching cosmetics
		try {
			database.loadCosmetics ( this );
		} catch ( Exception e ) {
			e.printStackTrace ( );
		}
	}
	
	/**
	 * Upload the values to the database.
	 *
	 * <strong>This means that this method will upload to the database the values stored in this object.<strong/>
	 */
	public void upload ( ) {
		Validate.isTrue ( EnumMainConfiguration.ENABLE_DATABASE.getAsBoolean ( ) , "database not enabled" );
		
		DataStorage database = DataStorageHandler.getInstance ( ).getDataStorage ( );
		
		// uploading stats
		stat_values.forEach ( ( stat_type , value ) -> {
			try {
				database.setStatValue ( this , stat_type , value );
			} catch ( Exception e ) {
				e.printStackTrace ( );
			}
		} );
		
		// uploading settings
		setting_values.forEach ( ( setting_type , value ) -> {
			try {
				database.setSettingValue ( this , setting_type , value );
			} catch ( Exception e ) {
				e.printStackTrace ( );
			}
		} );
		
		// uploading cosmetics
		cosmetics.forEach ( cosmetic -> {
			try {
				database.addCosmetic ( this , cosmetic );
			} catch ( Exception e ) {
				e.printStackTrace ( );
			}
		} );
	}
	
	@Override
	public String toString ( ) {
		return "PlayerDataStorage{" +
				"uuid=" + uuid +
				", name='" + name + '\'' +
				", stat_values=" + stat_values +
				", setting_values=" + setting_values +
				", cosmetics=" + cosmetics +
				'}';
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		PlayerDataStorage that = ( PlayerDataStorage ) o;
		return uuid.equals ( that.uuid );
	}
	
	@Override
	public int hashCode ( ) {
		return Objects.hash ( uuid );
	}
}