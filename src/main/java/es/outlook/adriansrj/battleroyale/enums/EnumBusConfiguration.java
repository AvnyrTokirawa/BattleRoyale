package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationEntry;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Bus configuration entries in an enumeration.
 *
 * @author AdrianSR / 23/09/2021 / 10:11 p. m.
 */
public enum EnumBusConfiguration implements ConfigurationEntry {
	
	ENABLE_DRAGON_BUS ( "dragon.enable" , "whether to enable the dragon bus.\n" +
			"players will not be able to choose any\n" +
			"other kind of bus if this option is enabled." , false ),
	
	;
	
	private final String      key;
	private final String      comment;
	private final Object      default_value;
	private final Class < ? > type;
	private       Object      value;
	
	EnumBusConfiguration ( String key , String comment , Object default_value , Class < ? > type ) {
		this.key           = key;
		this.comment       = comment;
		this.default_value = default_value;
		this.value         = default_value;
		this.type          = type;
	}
	
	EnumBusConfiguration ( String key , String comment , Object default_value ) {
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
		
		System.out.println ( ">>> load: " + getKey ( ) + " raw = " + raw );
		
		if ( raw != null && ClassReflection.compatibleTypes ( this.type , raw ) ) {
			this.value = raw;
		}
	}
}