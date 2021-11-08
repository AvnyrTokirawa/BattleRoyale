package es.outlook.adriansrj.battleroyale.battlefield.setup;

import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldConfiguration;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.Minimap;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;

/**
 * Base battlefield setup result.
 *
 * @author AdrianSR / 28/08/2021 / 10:38 a. m.
 */
public class BattlefieldSetupResultBase implements BattlefieldSetupResult {
	
	protected final String                   name;
	protected final ZoneBounds               bounds;
	protected final Minimap                  minimap;
	protected final BattlefieldConfiguration configuration;
	
	public BattlefieldSetupResultBase ( String name , ZoneBounds bounds , Minimap minimap ,
			BattlefieldConfiguration configuration ) {
		this.name               = StringUtil.replaceFileCharacters ( name , "-" );
		this.bounds             = bounds;
		this.minimap            = minimap;
		this.configuration      = configuration != null ? configuration : new BattlefieldConfiguration ( );
	}
	
	@Override
	public String getName ( ) {
		return name;
	}
	
	@Override
	public ZoneBounds getBounds ( ) {
		return bounds;
	}
	
	@Override
	public Minimap getMinimap ( ) {
		return minimap;
	}
	
	@Override
	public BattlefieldConfiguration getConfiguration ( ) {
		return configuration;
	}
}