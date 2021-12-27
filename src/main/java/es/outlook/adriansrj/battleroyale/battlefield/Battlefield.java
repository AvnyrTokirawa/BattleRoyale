package es.outlook.adriansrj.battleroyale.battlefield;

import es.outlook.adriansrj.battleroyale.battlefield.minimap.Minimap;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * @author AdrianSR / 03/09/2021 / 10:56 a. m.
 */
public class Battlefield {
	
	protected final File                     folder;
	protected final String                   name;
	protected final Minimap                  minimap;
	protected final BattlefieldShape         shape;
	protected final BattlefieldConfiguration configuration;
	
	public Battlefield ( File folder ) throws IOException, IllegalArgumentException {
		Validate.isTrue ( folder.isDirectory ( ) , "folder must be a valid directory" );
		Validate.isTrue ( folder.exists ( ) , "folder must exist" );
		
		this.folder        = folder;
		this.name          = folder.getName ( );
		this.configuration = new BattlefieldConfiguration ( );
		
		// loading minimap
		this.minimap = new Minimap ( new File ( folder , Constants.BATTLEFIELD_MINIMAP_FILE_NAME ) );
		
		// loading configuration
		File configuration_file = new File ( folder , Constants.BATTLEFIELD_CONFIGURATION_FILE_NAME );
		
		if ( configuration_file.exists ( ) ) {
			configuration.load ( YamlConfiguration.loadConfiguration ( configuration_file ) );
		}
		
		// loading shape
		this.shape = BattlefieldShape.of ( folder );
	}
	
	public Battlefield ( String name ) throws IOException, IllegalArgumentException {
		this ( new File ( EnumDirectory.BATTLEFIELD_DIRECTORY.getDirectory ( ) ,
						  StringUtil.formatBattlefieldName ( name ) ) );
	}
	
	public File getFolder ( ) {
		return folder;
	}
	
	public String getName ( ) {
		return name;
	}
	
	public Minimap getMinimap ( ) {
		return minimap;
	}
	
	public BattlefieldShape getShape ( ) {
		return shape;
	}
	
	public int getSize ( ) {
		return shape.getSize ( );
	}
	
	public BattlefieldConfiguration getConfiguration ( ) {
		return configuration;
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		
		Battlefield that = ( Battlefield ) o;
		
		return new EqualsBuilder ( ).append ( folder , that.folder ).isEquals ( );
	}
	
	@Override
	public int hashCode ( ) {
		return new HashCodeBuilder ( 17 , 37 ).append ( folder ).toHashCode ( );
	}
}