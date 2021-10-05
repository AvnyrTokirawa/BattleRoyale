package es.outlook.adriansrj.battleroyale.arena.sign;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.math.Vector3I;
import es.outlook.adriansrj.core.util.reflection.general.EnumReflection;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author AdrianSR / 04/09/2021 / 11:22 a. m.
 */
public class BattleRoyaleArenaSign implements Configurable {
	
	protected static final String DIRECTION_KEY = "direction";
	
	/**
	 * Loads a {@link BattleRoyaleArenaSign} from the provided {@link ConfigurationSection}.
	 *
	 * @param section the configuration section to load from.
	 * @return the resulting sign.
	 */
	public static BattleRoyaleArenaSign of ( ConfigurationSection section ) {
		return new BattleRoyaleArenaSign ( ).load ( section );
	}
	
	protected String    arena_name;
	protected Vector3I  location;
	protected BlockFace facing_direction;
	
	public BattleRoyaleArenaSign ( Vector3I location , BlockFace facing_direction , BattleRoyaleArena arena ) {
		this.arena_name       = arena.getName ( );
		this.location         = location;
		this.facing_direction = facing_direction;
	}
	
	public BattleRoyaleArenaSign ( ) {
		// to be loaded.
	}
	
	public Vector3I getLocation ( ) {
		return location;
	}
	
	public BlockFace getFacingDirection ( ) {
		return facing_direction;
	}
	
	public String getArenaName ( ) {
		return arena_name;
	}
	
	public BattleRoyaleArena getArena ( ) {
		return BattleRoyaleArenaHandler.getInstance ( )
				.getArena ( arena_name ).orElse ( null );
	}
	
	@Override
	public BattleRoyaleArenaSign load ( ConfigurationSection section ) {
		this.arena_name = section.getString ( Constants.ARENA_KEY , "" ).trim ( );
		
		if ( section.isConfigurationSection ( Constants.LOCATION_KEY ) ) {
			ConfigurationSection location_section = section.getConfigurationSection ( Constants.LOCATION_KEY );
			
			this.location = new Vector3I ( location_section.getInt ( Constants.X_KEY ) ,
										   location_section.getInt ( Constants.Y_KEY ) ,
										   location_section.getInt ( Constants.Z_KEY ) );
		}
		
		this.facing_direction = EnumReflection.getEnumConstant (
				BlockFace.class , section.getString ( DIRECTION_KEY , "" ) );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = ( arena_name != null && YamlUtil.setNotEqual (
				section , Constants.ARENA_KEY , arena_name ) ) ? 1 : 0;
		
		if ( location != null ) {
			ConfigurationSection location_section = section.createSection ( Constants.LOCATION_KEY );
			
			save += YamlUtil.setNotEqual ( location_section , Constants.X_KEY , location.getX ( ) ) ? 1 : 0;
			save += YamlUtil.setNotEqual ( location_section , Constants.Y_KEY , location.getY ( ) ) ? 1 : 0;
			save += YamlUtil.setNotEqual ( location_section , Constants.Z_KEY , location.getZ ( ) ) ? 1 : 0;
		}
		
		if ( facing_direction != null ) {
			save += YamlUtil.setNotEqual ( section , DIRECTION_KEY , facing_direction.name ( ) ) ? 1 : 0;
		}
		
		return save;
	}
	
	@Override
	public boolean isValid ( ) {
		return StringUtil.isNotBlank ( arena_name ) && location != null && facing_direction != null;
	}
	
	@Override
	public boolean isInvalid ( ) {
		return ! isValid ( );
	}
}