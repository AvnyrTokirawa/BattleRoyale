package es.outlook.adriansrj.battleroyale.game.loot;

import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import es.outlook.adriansrj.core.util.math.RandomUtil;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

/**
 * @author AdrianSR / 12/09/2021 / 01:15 p. m.
 */
public class LootConfigurationContainer implements Configurable {
	
	protected static final int DEFAULT_MAXIMUM = 2;
	
	public static LootConfigurationContainer of ( ConfigurationSection section ) {
		return new LootConfigurationContainer ( ).load ( section );
	}
	
	@ConfigurableEntry ( key = "maximum", comment = "maximum number of items each 9 slot" )
	protected int maximum;
	
	protected final Map < String, LootConfigurationEntry > content = new HashMap <> ( );
	
	public LootConfigurationContainer ( int maximum , Map < String, LootConfigurationEntry > content ) {
		this.maximum = maximum;
		
		content.forEach ( ( key , entry ) -> this.content.put ( key , entry.clone ( ) ) );
	}
	
	public LootConfigurationContainer ( Map < String, LootConfigurationEntry > content ) {
		this ( DEFAULT_MAXIMUM , content );
	}
	
	public LootConfigurationContainer ( int maximum , LootConfigurationEntry... content ) {
		this.maximum = maximum;
		
		for ( int i = 0 ; i < Objects.requireNonNull ( content , "content cannot be null" ).length ; i++ ) {
			this.content.put ( "entry-" + i , content[ i ].clone ( ) );
		}
	}
	
	public LootConfigurationContainer ( LootConfigurationEntry... content ) {
		this ( DEFAULT_MAXIMUM , content );
	}
	
	public LootConfigurationContainer ( ) {
		// to be loaded
	}
	
	public Map < String, LootConfigurationEntry > getContent ( ) {
		return Collections.unmodifiableMap ( content );
	}
	
	/**
	 * Gets the maximum number of items each 9 slots.
	 *
	 * @return the maximum number of items each 9 slots.
	 */
	public int getMaximum ( ) {
		return maximum;
	}
	
	public LootConfigurationEntry getRandomEntry ( ) {
		if ( content.values ( ).stream ( ).anyMatch ( entry -> entry != null && entry.isValid ( ) ) ) {
			LootConfigurationEntry[] array = content.values ( ).toArray ( new LootConfigurationEntry[ 0 ] );
			
			// calculating total percent
			double total_percent = 0.0D;
			
			for ( LootConfigurationEntry entry : content.values ( ) ) {
				// we take into account only valid entries
				if ( entry != null && entry.isValid ( ) ) {
					total_percent += entry.chance;
				}
			}
			
			// then getting the random entry
			while ( true ) {
				LootConfigurationEntry next   = array[ RandomUtil.nextInt ( array.length ) ];
				double                 chance = Math.random ( ) * total_percent;
				
				if ( next != null && next.isValid ( ) && ( next.chance <= 0.0D || chance <= next.chance ) ) {
					return next;
				}
			}
		} else {
			// cannot get random entry as
			// there are no valid entries.
			return null;
		}
	}
	
	public Set < LootConfigurationEntry > getRandomEntries ( int maximum ) {
		Validate.isTrue ( maximum > 0 , "maximum must be > 0" );
		
		Set < LootConfigurationEntry > result = new HashSet <> ( );
		int valid_count = ( int ) content.values ( ).stream ( )
				.filter ( entry -> entry != null && entry.isValid ( ) ).count ( );
		
		outer:
		while ( content.size ( ) > 0 && result.size ( ) < maximum ) {
			LootConfigurationEntry next = getRandomEntry ( );
			
			if ( next == null ) {
				break;
			}
			
			if ( result.add ( next ) && result.size ( ) < maximum ) {
				for ( LootConfigurationEntry required : next.getRequired ( ) ) {
					if ( required == null || required.isInvalid ( ) ) { continue; }
					
					if ( result.size ( ) + 1 <= maximum ) {
						result.add ( required );
					} else {
						break outer;
					}
				}
			}
			
			// not enough entries
			if ( result.size ( ) == valid_count ) {
				break;
			}
		}
		
		return result;
	}
	
	public void fill ( Inventory inventory , Player player ) {
		if ( content.size ( ) > 0 && maximum > 0 ) {
			boolean player_inventory = inventory instanceof PlayerInventory;
			int number = Math.max (
					RandomUtil.nextInt ( ( maximum * ( inventory.getSize ( ) / 9 ) ) + 1 ) , 1 );
			
			if ( number > 0 ) {
				int                             count = 0;
				List < LootConfigurationEntry > loot  = new ArrayList <> ( getRandomEntries ( number ) );
				
				// we add the entries to random slots, for a better random experience.
				while ( count < number && loot.size ( ) > 0 ) {
					int next_slot = RandomUtil.nextInt ( inventory.getSize ( ) );
					
					if ( player_inventory || inventory.getItem ( next_slot ) == null ) {
						LootConfigurationEntry item = loot.get ( RandomUtil.nextInt ( loot.size ( ) ) );
						
						if ( player_inventory ) {
							inventory.addItem ( item.toItemStack ( player ) );
						} else {
							inventory.setItem ( next_slot , item.toItemStack ( player ) );
						}
						
						loot.remove ( item );
						count++;
					}
					
					// there is no space
					if ( inventory.firstEmpty ( ) == -1 ) {
						break;
					}
				}
			}
		}
	}
	
	public void fill ( Inventory inventory ) {
		fill ( inventory , inventory instanceof PlayerInventory
				? ( Player ) ( ( PlayerInventory ) inventory ).getHolder ( ) : null );
	}
	
	@Override
	public LootConfigurationContainer load ( ConfigurationSection section ) {
		loadEntries ( section );
		
		for ( String key : section.getKeys ( false ) ) {
			if ( section.isConfigurationSection ( key ) ) {
				ConfigurationSection   entry_section = section.getConfigurationSection ( key );
				LootConfigurationEntry entry         = LootConfigurationEntry.of ( entry_section );
				
				if ( entry.isValid ( ) ) {
					this.content.put ( key.trim ( ) , entry );
				}
			}
		}
		
		// finish loading required entries
		this.content.values ( ).forEach ( entry -> entry.finishLoadingRequired ( this ) );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save  = saveEntries ( section );
		int count = 0;
		
		for ( Map.Entry < String, LootConfigurationEntry > entry : content.entrySet ( ) ) {
			String                 key   = StringUtil.defaultIfBlank ( entry.getKey ( ) , "entry-" + count );
			LootConfigurationEntry value = entry.getValue ( );
			
			if ( value != null && value.isValid ( ) ) {
				save += value.save ( section.createSection ( key.trim ( ) ) );
			}
			
			count++;
		}
		
		return save;
	}
	
	@Override
	public boolean isValid ( ) {
		return content.values ( ).stream ( ).anyMatch ( LootConfigurationEntry :: isValid );
	}
}