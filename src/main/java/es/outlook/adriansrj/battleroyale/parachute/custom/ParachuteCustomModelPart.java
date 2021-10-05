package es.outlook.adriansrj.battleroyale.parachute.custom;

import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author AdrianSR / 10/09/2021 / 08:57 p. m.
 */
public class ParachuteCustomModelPart implements Configurable , Cloneable {
	
	public static ParachuteCustomModelPart of ( ConfigurationSection section ) {
		return new ParachuteCustomModelPart ( ).load ( section );
	}
	
	protected static final String POSITION_KEY = "position";
	protected static final String SHAPE_KEY    = "shape";
	
	protected String                        name;
	@ConfigurableEntry ( subsection = POSITION_KEY )
	protected ParachuteCustomPartPosition   position;
	@ConfigurableEntry ( subsection = SHAPE_KEY )
	protected ParachuteCustomModelPartShape shape;
	
	public ParachuteCustomModelPart ( String name , ParachuteCustomPartPosition position , ParachuteCustomModelPartShape shape ) {
		this.name     = name;
		this.position = position;
		this.shape    = shape;
	}
	
	public ParachuteCustomModelPart ( ParachuteCustomPartPosition position , ParachuteCustomModelPartShape shape ) {
		this ( null , position , shape );
	}
	
	public ParachuteCustomModelPart ( ) {
		// to be loaded
	}
	
	public String getName ( ) {
		return name;
	}
	
	public ParachuteCustomPartPosition getPosition ( ) {
		return position;
	}
	
	public ParachuteCustomModelPartShape getShape ( ) {
		return shape;
	}
	
	@Override
	public ParachuteCustomModelPart load ( ConfigurationSection section ) {
		this.name = section.getName ( );
		
		loadEntries ( section );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return saveEntries ( section );
	}
	
	@Override
	public boolean isValid ( ) {
		return position != null && position.isValid ( ) && shape != null && shape.isValid ( );
	}
	
	@Override
	public boolean isInvalid ( ) {
		return !isValid ( );
	}
	
	@Override
	public ParachuteCustomModelPart clone ( ) {
		try {
			return ( ParachuteCustomModelPart ) super.clone ( );
		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError ( );
		}
	}
}
