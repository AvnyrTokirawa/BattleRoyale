package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationEntry;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import es.outlook.adriansrj.core.util.itemstack.wool.WoolColor;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author AdrianSR / 04/09/2021 / 07:33 p. m.
 */
public enum EnumSignConfiguration implements ConfigurationEntry {
	
	STATUS_BLOCK_ENABLE ( "status-block.enable" , "enable arena status blocks" , true ),
	
	STATUS_BLOCK_WAITING_MATERIAL ( "status-block.waiting.material" , "waiting status block material" ,
									UniversalMaterial.LIME_WOOL.name ( ) ),
	STATUS_BLOCK_WAITING_DATA ( "status-block.waiting.data" , "waiting status block data" ,
								0 ),
	
	STATUS_BLOCK_RUNNING_MATERIAL ( "status-block.running.material" , "running status block material" ,
									UniversalMaterial.RED_WOOL.name ( ) ),
	STATUS_BLOCK_RUNNING_DATA ( "status-block.running.data" , "running status block data" ,
								( int ) WoolColor.RED.getShortValue ( ) ),
	
	STATUS_BLOCK_RESTARTING_MATERIAL ( "status-block.restarting.material" , "restarting status block material" ,
									   UniversalMaterial.YELLOW_WOOL.name ( ) ),
	STATUS_BLOCK_RESTARTING_DATA ( "status-block.restarting.data" , "restarting status block data" ,
								   ( int ) WoolColor.YELLOW.getShortValue ( ) ),
	
	;
	
	private final String      key;
	private final String      comment;
	private final Object      default_value;
	private final Class < ? > type;
	private       Object      value;
	
	EnumSignConfiguration ( String key , String comment , Object default_value , Class < ? > type ) {
		this.key           = key;
		this.comment       = comment;
		this.default_value = default_value;
		this.value         = default_value;
		this.type          = type;
	}
	
	EnumSignConfiguration ( String key , String comment , Object default_value ) {
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