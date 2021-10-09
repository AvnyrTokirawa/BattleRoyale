package es.outlook.adriansrj.battleroyale.game.loot;

import es.outlook.adriansrj.battleroyale.enums.EnumLootContainer;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author AdrianSR / 12/09/2021 / 01:07 p. m.
 */
public class LootConfiguration implements Configurable {
	
	public static LootConfiguration of ( ConfigurationSection section ) {
		return new LootConfiguration ( ).load ( section );
	}
	
	public static LootConfiguration of ( File file ) {
		return of ( YamlConfigurationComments.loadConfiguration ( file ) );
	}
	
	protected final Map < EnumLootContainer, LootConfigurationContainer > containers = new HashMap <> ( );
	
	public LootConfiguration ( Map < EnumLootContainer, LootConfigurationContainer > containers ) {
		containers.forEach ( ( key , value ) -> {
			if ( key != null && value != null ) {
				this.containers.put ( key , value );
			}
		} );
	}
	
	public LootConfiguration ( ) {
		// to be loaded
	}
	
	public Map < EnumLootContainer, LootConfigurationContainer > getContainers ( ) {
		return Collections.unmodifiableMap ( containers );
	}
	
	/**
	 *
	 * @param container
	 * @return the container, or <b>null</b> if not set.
	 */
	public LootConfigurationContainer getContainer ( EnumLootContainer container ) {
		return containers.get ( container );
	}
	
	@Override
	public boolean isValid ( ) {
		return true;
	}
	
	@Override
	public LootConfiguration load ( ConfigurationSection section ) {
		this.containers.clear ( );
		
		for ( EnumLootContainer container : EnumLootContainer.values ( ) ) {
			String key = formatKey ( container );
			
			if ( section.isConfigurationSection ( key ) ) {
				LootConfigurationContainer value = LootConfigurationContainer.of (
						section.getConfigurationSection ( key ) );
				
				if ( value.isValid ( ) ) {
					containers.put ( container , value );
				}
			}
		}
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = 0;
		
		for ( EnumLootContainer container : EnumLootContainer.values ( ) ) {
			LootConfigurationContainer value = getContainer ( container );
			
			if ( value != null && value.isValid ( ) ) {
				save += value.save ( section.createSection ( formatKey ( container ) ) );
			}
		}
		
		return save;
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
	
	protected String formatKey ( EnumLootContainer type ) {
		return type.name ( ).toLowerCase ( ).replace ( '_' , '-' );
	}
}