package es.outlook.adriansrj.battleroyale.vehicle;

import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableCollectionEntry;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author AdrianSR / 14/09/2021 / 07:29 p. m.
 */
public class VehiclesConfiguration implements Configurable {
	
	public static VehiclesConfiguration of ( ConfigurationSection section ) {
		return new VehiclesConfiguration ( ).load ( section );
	}
	
	public static VehiclesConfiguration of ( File file ) {
		return of ( YamlConfigurationComments.loadConfiguration ( file ) );
	}
	
	@ConfigurableCollectionEntry ( subsectionprefix = "vehicle-" )
	protected final Set < VehiclesConfigurationEntry > entries = new HashSet <> ( );
	
	public VehiclesConfiguration ( Collection < VehiclesConfigurationEntry > entries ) {
		entries.stream ( ).filter ( Objects :: nonNull ).forEach ( this.entries :: add );
	}
	
	public VehiclesConfiguration ( VehiclesConfigurationEntry... entries ) {
		for ( VehiclesConfigurationEntry entry : entries ) {
			if ( entry != null ) {
				this.entries.add ( entry );
			}
		}
	}
	
	public VehiclesConfiguration ( ) {
		// to be loaded
	}
	
	public Set < VehiclesConfigurationEntry > getEntries ( ) {
		return Collections.unmodifiableSet ( entries );
	}
	
	public VehiclesConfigurationEntry getRandomEntry ( ) {
		if ( entries.stream ( ).anyMatch ( entry -> entry != null && entry.isValid ( ) ) ) {
			VehiclesConfigurationEntry[] array = entries.toArray ( new VehiclesConfigurationEntry[ 0 ] );
			
			// calculating total percent
			double total_percent = 0.0D;
			
			for ( VehiclesConfigurationEntry entry : entries ) {
				// we take into account only valid entries
				if ( entry != null && entry.isValid ( ) ) {
					total_percent += entry.chance;
				}
			}
			
			// then getting the random entry
			while ( true ) {
				VehiclesConfigurationEntry next   = array[ RandomUtils.nextInt ( 0 , array.length ) ];
				double                     chance = Math.random ( ) * total_percent;
				
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
	
	@Override
	public VehiclesConfiguration load ( ConfigurationSection section ) {
		this.entries.clear ( );
		this.loadEntries ( section );
		
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return saveEntries ( section );
	}
	
	public void save ( File file ) {
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( file );
		
		if ( save ( yaml ) > 0 ) {
			try {
				yaml.save ( file );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	@Override
	public boolean isValid ( ) {
		return true;
	}
}
