package es.outlook.adriansrj.battleroyale.parachute.custom;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.core.util.configurable.ConfigurableEntry;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;

/**
 * Custom model parachute.
 *
 * @author AdrianSR / 12/09/2021 / 09:31 a. m.
 */
public class ParachuteCustom extends Parachute {
	
	public static ParachuteCustom of ( ConfigurationSection section ) {
		return new ParachuteCustom ( ).load ( section );
	}
	
	@ConfigurableEntry ( subsection = "model" )
	protected ParachuteCustomModel model;
	
	public ParachuteCustom ( int price , Permission permission , ParachuteCustomModel model ) {
		super ( price , permission );
		this.model = model;
	}
	
	public ParachuteCustom ( ParachuteCustomModel model ) {
		this ( 0 , null , model );
	}
	
	public ParachuteCustom ( ) {
		// to be loaded
	}
	
	public ParachuteCustomModel getModel ( ) {
		return model;
	}
	
	/**
	 *
	 * @param player
	 * @return <b>null</b> if the model <b>null</b> or invalid.
	 */
	@Override
	public ParachuteCustomInstance createInstance ( Player player ) {
		return model != null && model.isValid ( ) ? new ParachuteCustomInstance ( player , this ) : null;
	}
	
	@Override
	public ParachuteCustom load ( ConfigurationSection section ) {
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
		
		ParachuteCustom that = ( ParachuteCustom ) o;
		
		return new EqualsBuilder ( ).appendSuper ( super.equals ( o ) ).append ( model , that.model )
				.isEquals ( );
	}
	
	@Override
	public int hashCode ( ) {
		return new HashCodeBuilder ( 17 , 37 )
				.appendSuper ( super.hashCode ( ) ).append ( model ).toHashCode ( );
	}
	
	@Override
	public ParachuteCustom clone ( ) {
		return ( ParachuteCustom ) super.clone ( );
	}
}
