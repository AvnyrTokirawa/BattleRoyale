package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationEntry;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import es.outlook.adriansrj.core.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Compass configuration entries in an enumeration.
 *
 * @author AdrianSR / 09/09/2021 / 05:04 p. m.
 */
public enum EnumCompassConfiguration implements ConfigurationEntry {
	
	LENGTH ( "length" , "the length of the bar" , 70 ),
	SPACE ( "space" , "the space between cardinal points" , 1 ),
	COLOR ( "color" , "the color of the bar" , BarColor.RED.name ( ) ),
	TEXT_COLOR ( "text-color" , "the color of the text on the bar" ,
				 StringUtil.untranslateAlternateColorCodes ( ChatColor.GOLD.toString ( ) ) ),
	STYLE ( "style" , "the style of the bar" , BarStyle.SEGMENTED_20.name ( ) ),
	
	FILLER ( "language.filler" , "text used to fill the bar" , " . . . : . . . " ),
	NORTH_WORD ( "language.north-word" , "" , "N" ),
	EAST_WORD ( "language.east-word" , "" , "E" ),
	SOUTH_WORD ( "language.south-word" , "" , "S" ),
	WEST_WORD ( "language.west-word" , "" , "W" ),
	NORTH_EAST_WORD ( "language.north-east-word" , "" , "NE" ),
	NORTH_WEST_WORD ( "language.north-west-word" , "" , "NW" ),
	SOUTH_EAST_WORD ( "language.south-east-word" , "" , "SE" ),
	SOUTH_WEST_WORD ( "language.south-west-word" , "" , "SW" ),
	
	;
	
	private final String      key;
	private final String      comment;
	private final Object      default_value;
	private final Class < ? > type;
	private       Object      value;
	
	EnumCompassConfiguration ( String key , String comment , Object default_value , Class < ? > type ) {
		this.key           = key;
		this.comment       = comment;
		this.default_value = default_value;
		this.value         = default_value;
		this.type          = type;
	}
	
	EnumCompassConfiguration ( String key , String comment , Object default_value ) {
		this ( key , comment , default_value , default_value.getClass ( ) );
	}
	
	@Override
	public String getKey ( ) {
		return key;
	}
	
	@Override
	public String getComment ( ) {
		return comment;
	}
	
	@Override
	public Object getDefaultValue ( ) {
		return default_value;
	}
	
	@Override
	public Object getValue ( ) {
		return value;
	}
	
	@Override
	public Class < ? > getValueType ( ) {
		return type;
	}
	
	@Override
	public void load ( ConfigurationSection section ) {
		Object raw = section.get ( getKey ( ) );
		
		if ( raw != null && ClassReflection.compatibleTypes ( this.type , raw ) ) {
			this.value = raw;
		}
	}
}