package es.outlook.adriansrj.battleroyale.game.player;

import es.outlook.adriansrj.battleroyale.cosmetic.Cosmetic;
import es.outlook.adriansrj.battleroyale.data.DataStorage;
import es.outlook.adriansrj.battleroyale.data.DataStorageHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.enums.EnumPlayerSetting;
import es.outlook.adriansrj.battleroyale.enums.EnumStat;
import es.outlook.adriansrj.battleroyale.event.player.PlayerStatSetEvent;
import es.outlook.adriansrj.battleroyale.util.NamespacedKey;
import es.outlook.adriansrj.battleroyale.util.PluginUtil;
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
	// balance
	private       int                                      balance;
	// vault balance
	private final PlayerVaultBalance                       vault_balance;
	
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
		
		// vault
		if ( EnumMainConfiguration.VAULT_ENABLE.getAsBoolean ( ) && PluginUtil.isVaultEnabled ( ) ) {
			PlayerVaultBalance vault_balance = new PlayerVaultBalance ( this );
			
			if ( vault_balance.isHooked ( ) ) {
				this.vault_balance = vault_balance;
			} else {
				this.vault_balance = null;
			}
		} else {
			this.vault_balance = null;
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
		
		final int current_value = getStat ( stat_type );
		
		if ( value != current_value ) {
			this.stat_values.put ( stat_type , value );
			this.dirty = true;
			
			// firing event
			new PlayerStatSetEvent (
					uuid , stat_type , current_value , value ).callSafe ( );
			
			// then uploading
			DataStorage data_storage = DataStorageHandler.getInstance ( ).getDataStorage ( );
			
			if ( upload && data_storage != null ) {
				try {
					data_storage.setStatValue ( this , stat_type , value );
				} catch ( Exception e ) {
					e.printStackTrace ( );
				}
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
		DataStorage data_storage = DataStorageHandler.getInstance ( ).getDataStorage ( );
		
		if ( upload && data_storage != null ) {
			try {
				data_storage.setSettingValue ( this , setting_type , value );
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
		DataStorage data_storage = DataStorageHandler.getInstance ( ).getDataStorage ( );
		
		if ( upload && data_storage != null ) {
			try {
				data_storage.addCosmetic ( this , cosmetic );
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
		DataStorage data_storage = DataStorageHandler.getInstance ( ).getDataStorage ( );
		
		if ( upload && data_storage != null ) {
			try {
				data_storage.removeCosmetic ( this , cosmetic );
			} catch ( Exception e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	public void removeCosmetic ( Cosmetic < ? > cosmetic ) {
		removeCosmetic ( cosmetic , false );
	}
	
	// ------- balance
	
	public int getBalance ( ) {
		return vault_balance != null ? vault_balance.getBalance ( ) : balance;
	}
	
	public void setBalance ( int balance , boolean upload ) {
		if ( vault_balance != null ) {
			vault_balance.setBalance ( balance );
		} else {
			this.balance = Math.max ( 0 , balance );
			
			// then uploading
			DataStorage data_storage = DataStorageHandler.getInstance ( ).getDataStorage ( );
			
			if ( upload && data_storage != null ) {
				try {
					data_storage.setBalance ( this , this.balance );
				} catch ( Exception e ) {
					e.printStackTrace ( );
				}
			}
		}
	}
	
	public void setBalance ( int balance ) {
		setBalance ( balance , false );
	}
	
	public void balanceDeposit ( int value , boolean upload ) {
		setBalance ( balance + value , upload );
	}
	
	public void balanceDeposit ( int value ) {
		balanceDeposit ( value , false );
	}
	
	public void balanceWithdraw ( int value , boolean upload ) {
		setBalance ( balance - value , upload );
	}
	
	public void balanceWithdraw ( int value ) {
		balanceWithdraw ( value , false );
	}
	
	public void clearBalance ( ) {
		setBalance ( 0 );
	}
	
	// -------
	
	/**
	 * Fetches the values from the database.
	 * <br>
	 * Note that this will not have any effect if
	 * the option {@link EnumMainConfiguration#ENABLE_DATABASE} is false,
	 * or if it could not connect to the database.
	 *
	 * <strong>This means that this method will fetch the values that corresponds to this
	 * player from the database.<strong/>
	 */
	public void fetch ( ) {
		DataStorage database = DataStorageHandler.getInstance ( ).getDataStorage ( );
		
		if ( database != null ) {
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
			
			// fetching balance
			try {
				this.balance = database.getBalance ( getUniqueId ( ) );
			} catch ( Exception e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	/**
	 * Upload the values to the database.
	 * <br>
	 * Note that this will not have any effect if
	 * the option {@link EnumMainConfiguration#ENABLE_DATABASE} is false,
	 * or if it could not connect to the database.
	 *
	 * <strong>This means that this method will upload to the database the values stored in this object.<strong/>
	 */
	public void upload ( ) {
		DataStorage database = DataStorageHandler.getInstance ( ).getDataStorage ( );
		
		if ( database != null ) {
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
			
			// uploading balance
			try {
				database.setBalance ( this , balance );
			} catch ( Exception e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	@Override
	public String toString ( ) {
		return "PlayerDataStorage{" +
				"uuid=" + uuid +
				", name='" + name + '\'' +
				", stat_values=" + stat_values +
				", setting_values=" + setting_values +
				", cosmetics=" + cosmetics +
				", balance=" + balance +
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