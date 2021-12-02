package es.outlook.adriansrj.battleroyale.bus.test;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permission;

/**
 * A battle royale bus with the shape of an entity.
 *
 * @author AdrianSR / 23/09/2021 / 08:55 p. m.
 */
public class BusPetTest extends BusTest {
	
	protected static final String SHAPE_KEY = "shape";
	
	public static BusPetTest of ( ConfigurationSection section ) {
		return new BusPetTest ( ).load ( section );
	}
	
	protected EntityType shape;
	
	public BusPetTest ( int price , Permission permission , EntityType shape ) {
		super ( price , permission );
		this.shape = shape;
	}
	
	public BusPetTest ( EntityType shape ) {
		this.shape = shape;
	}
	
	public BusPetTest ( ) {
		// to be loaded
	}
	
	@Override
	public BusPetInstanceTest createInstance ( BattleRoyaleArena arena , Location spawn ) {
		return new BusPetInstanceTest ( this , arena , spawn );
	}
	
	public EntityType getShape ( ) {
		return shape;
	}
	
	@Override
	public BusPetTest load ( ConfigurationSection section ) {
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
	public BusPetTest clone ( ) {
		return ( BusPetTest ) super.clone ( );
	}
}