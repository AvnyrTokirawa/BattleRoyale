package es.outlook.adriansrj.battleroyale.packet.sender;

import es.outlook.adriansrj.battleroyale.packet.factory.PacketFactoryService;
import es.outlook.adriansrj.core.util.reflection.bukkit.BukkitReflection;
import es.outlook.adriansrj.core.util.reflection.bukkit.PacketReflection;
import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * @author AdrianSR / 09/09/2021 / 10:06 p. m.
 */
public abstract class PacketSenderServiceHandleBase implements PacketSenderServiceHandle {
	
	protected final PacketFactoryService factory_service;
	
	protected PacketSenderServiceHandleBase ( ) {
		this.factory_service = PacketFactoryService.getInstance ( );
	}
	
	protected void setInteger ( Object packet , int index , int value ) {
		PacketReflection.setInteger ( packet , index , value );
	}
	
	protected void setDouble ( Object packet , int index , double value ) {
		PacketReflection.setDouble ( packet , index , value );
	}
	
	protected void setFloat ( Object packet , int index , float value ) {
		PacketReflection.setFloat ( packet , index , value );
	}
	
	protected void setByte ( Object packet , int index , byte value ) {
		PacketReflection.setByte ( packet , index , value );
	}
	
	protected void setBoolean ( Object packet , int index , boolean value ) {
		PacketReflection.setBoolean ( packet , index , value );
	}
	
	protected void setArray ( Object packet , int index , Object value ) {
		PacketReflection.setArray ( packet , index , value );
	}
	
	protected void setList ( Object packet , int index , Object value ) {
		PacketReflection.setList ( packet , index , value );
	}
	
	protected void setUUID ( Object packet , int index , UUID value ) {
		PacketReflection.setUUID ( packet , index , value );
	}
	
	protected void setByClass ( Object packet , Class < ? > clazz , int index , Object value ) {
		PacketReflection.setValueByType ( packet , clazz , index , value );
	}
	
	protected Field[] extractFields ( Object packet ) {
		return PacketReflection.extractFields ( packet );
	}
	
	protected void set ( Object packet , String field_name , Object value ) {
		try {
			FieldReflection.setValue ( packet , field_name , value );
		} catch ( SecurityException | NoSuchFieldException | IllegalAccessException ex ) {
			ex.printStackTrace ( );
		}
	}
	
	protected void send ( Player player , Object packet ) {
		BukkitReflection.sendPacket ( player , packet );
	}
}
