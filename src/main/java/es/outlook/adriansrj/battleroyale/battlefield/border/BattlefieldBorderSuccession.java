package es.outlook.adriansrj.battleroyale.battlefield.border;

import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.configurable.ConfigurableCollectionEntry;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Border shrinking succession.
 *
 * @author AdrianSR / 06/09/2021 / 11:03 a. m.
 */
public class BattlefieldBorderSuccession implements Configurable {
	
	@ConfigurableCollectionEntry ( subsection = "points", subsectionprefix = "shrink-point-" )
	protected final Set < BattlefieldBorderShrink > points = new LinkedHashSet <> ( );
	
	/**
	 * Constructs the succession from the provided shrinking points.
	 * <br>
	 * <b>Note that the invalid points will be ignored.<b/>
	 *
	 * @param points the shrinking points.
	 */
	public BattlefieldBorderSuccession ( BattlefieldBorderShrink... points ) {
		Arrays.stream ( points ).filter ( BattlefieldBorderShrink :: isValid )
				.forEach ( this.points :: add );
	}
	
	public BattlefieldBorderSuccession ( ) {
		// empty succession; can be loaded from config though
	}
	
	public Set < BattlefieldBorderShrink > getPoints ( ) {
		return points;
	}
	
	@Override
	public boolean isValid ( ) {
		return points.stream ( ).anyMatch ( BattlefieldBorderShrink :: isValid );
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