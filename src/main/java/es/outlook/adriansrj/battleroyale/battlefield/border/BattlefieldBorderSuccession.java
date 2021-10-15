package es.outlook.adriansrj.battleroyale.battlefield.border;

import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableCollectionEntry;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Border resizing succession.
 *
 * @author AdrianSR / 06/09/2021 / 11:03 a. m.
 */
public class BattlefieldBorderSuccession implements Configurable {
	
	@ConfigurableCollectionEntry ( subsection = "points", subsectionprefix = "resizing-point-" )
	protected final Set < BattlefieldBorderResize > points = new LinkedHashSet <> ( );
	
	/**
	 * Constructs the succession from the provided resizing points.
	 * <br>
	 * <b>Note that the invalid points will be ignored.<b/>
	 *
	 * @param points the resizing points.
	 */
	public BattlefieldBorderSuccession ( BattlefieldBorderResize... points ) {
		Arrays.stream ( points ).filter ( BattlefieldBorderResize :: isValid )
				.forEach ( this.points :: add );
	}
	
	public BattlefieldBorderSuccession ( ) {
		// empty succession; can be loaded from config though
	}
	
	public Set < BattlefieldBorderResize > getPoints ( ) {
		return points;
	}
	
	@Override
	public boolean isValid ( ) {
		return points.stream ( ).anyMatch ( BattlefieldBorderResize :: isValid );
	}
	
	@Override
	public boolean isInvalid ( ) {
		return !isValid ( );
	}
	
	@Override
	public BattlefieldBorderSuccession load ( ConfigurationSection section ) {
		loadEntries ( section );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return saveEntries ( section );
	}
}