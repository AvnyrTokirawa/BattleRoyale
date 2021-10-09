package es.outlook.adriansrj.battleroyale.compass;

import es.outlook.adriansrj.battleroyale.enums.EnumCompassConfiguration;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

/**
 * Simple compass bar implementation which uses the model, length, color and style provided by the configuration
 * ({@link EnumCompassConfiguration}).
 *
 * @author AdrianSR / 09/09/2021 / 05:01 p. m.
 */
public class CompassBarSimple extends CompassBarBase {
	
	protected String model;
	
	public CompassBarSimple ( Player player ) {
		super ( player );
	}
	
	@Override
	protected String getModel ( ) {
		if ( model == null ) {
			StringBuilder          builder = new StringBuilder ( );
			CompassCardinalPoint[] points  = new CompassCardinalPoint[ CompassCardinalPoint.values ( ).length ];
			
			// indexing points in 0-360 order.
			int index = 0;
			
			for ( int d = 0 ; d < 360 ; d++ ) { // between 0 and 359
				CompassCardinalPoint point = CompassCardinalPoint.ofValue ( d );
				
				if ( point != null ) {
					points[ index++ ] = point;
				}
			}
			
			// then creating
			for ( CompassCardinalPoint point : points ) {
				builder.append ( StringUtil.stripColors ( StringUtil.defaultIfBlank (
						point.getAbbreviation ( ).getAsString ( ) , point.getDefaultAbbreviation ( ) ) ) );
				
				// space
				for ( int s = 0 ; s < Math.max ( 1 , EnumCompassConfiguration.SPACE.getAsInteger ( ) ) ; s++ ) {
					builder.append ( StringUtil.stripColors ( StringUtil.defaultIfBlank (
							EnumCompassConfiguration.FILLER.getAsString ( ) , " " ) ) );
				}
			}
			
			this.model = builder.toString ( );
		}
		return model;
	}
	
	@Override
	protected int getLength ( ) {
		return Math.max ( 10 , EnumCompassConfiguration.LENGTH.getAsInteger ( ) );
	}
	
	@Override
	protected BarColor getColor ( ) {
		BarColor color = EnumCompassConfiguration.COLOR.getAsEnum ( BarColor.class );
		
		return color != null ? color : ( BarColor ) EnumCompassConfiguration.COLOR.getDefaultValue ( );
	}
	
	@Override
	protected String getTextColor ( ) {
		return StringUtil.defaultIfBlank ( StringUtil.translateAlternateColorCodes (
				EnumCompassConfiguration.TEXT_COLOR.getAsString ( ) ) , "" );
	}
	
	@Override
	protected BarStyle getStyle ( ) {
		BarStyle style = EnumCompassConfiguration.STYLE.getAsEnum ( BarStyle.class );
		
		return style != null ? style : ( BarStyle ) EnumCompassConfiguration.STYLE.getDefaultValue ( );
	}
}
