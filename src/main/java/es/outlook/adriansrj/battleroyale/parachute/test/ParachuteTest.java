package es.outlook.adriansrj.battleroyale.parachute.test;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustomModel;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;

/**
 * Custom model parachute.
 *
 * @author AdrianSR / 12/09/2021 / 09:31 a. m.
 */
public class ParachuteTest extends Parachute {
	
	public static ParachuteTest of ( ConfigurationSection section ) {
		return new ParachuteTest ( ).load ( section );
	}
	
	@ConfigurableEntry ( subsection = "model" )
	protected ParachuteCustomModel model;
	
	public ParachuteTest ( int price , Permission permission , ParachuteCustomModel model ) {
		super ( price , permission );
		this.model = model;
	}
	
	public ParachuteTest ( ParachuteCustomModel model ) {
		this ( 0 , null , model );
	}
	
	public ParachuteTest ( ) {
		// to be loaded
	}
	
	public ParachuteCustomModel getModel ( ) {
		return model;
	}
	
	@Override
	public ParachuteTestInstance createInstance ( BattleRoyaleArena arena , Location spawn ) {
		return model != null && model.isValid ( ) ? new ParachuteTestInstance ( arena , spawn ) : null;
	}
	
	@Override
	public ParachuteTest load ( ConfigurationSection section ) {
		super.load ( section );
		loadEntries ( section );
		
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		return super.save ( section ) + saveEntries ( section );
	}
	
	@Override
	public boolean isValid ( ) {
		return model != null && model.isValid ( );
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		
		ParachuteTest that = ( ParachuteTest ) o;
		
		return new EqualsBuilder ( ).appendSuper ( super.equals ( o ) ).append ( model , that.model )
				.isEquals ( );
	}
	
	@Override
	public int hashCode ( ) {
		return new HashCodeBuilder ( 17 , 37 )
				.appendSuper ( super.hashCode ( ) ).append ( model ).toHashCode ( );
	}
	
	@Override
	public ParachuteTest clone ( ) {
		return ( ParachuteTest ) super.clone ( );
	}
}
