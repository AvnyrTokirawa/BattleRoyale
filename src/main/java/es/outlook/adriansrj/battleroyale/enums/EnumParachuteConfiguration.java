package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationEntry;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteHandler;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Parachute configuration entries in an enumeration.
 *
 * @author AdrianSR / 09/09/2021 / 05:04 p. m.
 */
public enum EnumParachuteConfiguration implements ConfigurationEntry {
	
	FALLING_SPEED ( "falling-speed" , "falling speed" , 0.7D ) {
		@Override
		public double getAsDouble ( ) {
			return Math.max ( super.getAsDouble ( ) , 0.1D );
		}
	},
	
	MINIMUM_HEIGHT ( "minimum-height" , "how high a player must be to open the parachute" ,
					 ParachuteHandler.DEFAULT_MINIMUM_HEIGHT ) {
		@Override
		public double getAsDouble ( ) {
			return Math.max ( super.getAsDouble ( ) , 1.0D );
		}
		
		@Override
		public int getAsInteger ( ) {
			return Math.max ( super.getAsInteger ( ) , 1 );
		}
	},
	AUTOMATIC ( "automatic" , "if enabled, the player's parachute will open automatically\n" +
			"when close to the ground after jumping from the bus." , true ),
	
	OPEN_WARNING_BAR ( "language.bar.open-warning" , "an action-bar message that warns the\n" +
			"player to open the parachute." , ChatColor.GOLD + "PRESS " + ChatColor.DARK_RED
							   + "SPACE" + ChatColor.YELLOW + " TWICE (OPEN PARACHUTE)" ),
	
	;
	
	private final String      key;
	private final String      comment;
	private final Object      default_value;
	private final Class < ? > type;
	private       Object      value;
	
	EnumParachuteConfiguration ( String key , String comment , Object default_value , Class < ? > type ) {
		this.key           = key;
		this.comment       = comment;
		this.default_value = default_value;
		this.value         = default_value;
		this.type          = type;
	}
	
	EnumParachuteConfiguration ( String key , String comment , Object default_value ) {
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