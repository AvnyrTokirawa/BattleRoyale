package es.outlook.adriansrj.battleroyale.bus.pet;

import es.outlook.adriansrj.battleroyale.bus.Bus;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permission;

/**
 * A battle royale bus with the shape of an entity.
 *
 * @author AdrianSR / 23/09/2021 / 08:55 p. m.
 */
public class BusPet extends Bus {
	
	protected static final String SHAPE_KEY = "shape";
	
	public static BusPet of ( ConfigurationSection section ) {
		return new BusPet ( ).load ( section );
	}
	
	protected EntityType shape;
	
	public BusPet ( int price , Permission permission , EntityType shape ) {
		super ( price , permission );
		this.shape = shape;
	}
	
	public BusPet ( EntityType shape ) {
		this.shape = shape;
	}
	
	public BusPet ( ) {
		// to be loaded
	}
	
	@Override
	public BusPetInstance createInstance ( Player player ) {
		return new BusPetInstance ( this , player );
	}
	
	public EntityType getShape ( ) {
		return shape;
	}
	
	@Override
	public BusPet load ( ConfigurationSection section ) {
		super.load ( section );
		
		// shape
		this.shape = EnumReflection.getEnumConstant (
				EntityType.class , section.getString ( SHAPE_KEY , StringUtil.EMPTY ) );
		
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = super.save ( section );
		
		// shape
		if ( shape != null ) {
			save += YamlUtil.setNotEqual ( section , SHAPE_KEY , shape.name ( ) ) ? 1 : 0;
		}
		
		return save;
	}
	
	@Override
	public boolean isValid ( ) {
		return shape != null;
	}
	
	@Override
	public BusPet clone ( ) {
		return ( BusPet ) super.clone ( );
	}
}