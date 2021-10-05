package es.outlook.adriansrj.battleroyale.parachute.custom;

import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteInstance;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link ParachuteInstance} model.
 *
 * @author AdrianSR / 09/09/2021 / 09:25 p. m.
 */
public class ParachuteCustomModel implements Configurable, Cloneable {
	
	public static final ParachuteCustomModel DEFAULT_MODEL;
	
	static {
		DEFAULT_MODEL = new ParachuteCustomModel.Builder ( )
				// left wing
				.withPart ( new ParachuteCustomPartPosition ( 0.2 , 3 , 0 , 0 , 90 , 100 ) ,
							new ParachuteCustomModelPartShape ( UniversalMaterial.WHITE_BANNER ,
																Parachute.Color.PLAYER ) )
				// right wing
				.withPart ( new ParachuteCustomPartPosition ( -0.2 , 3 , 0 , 0 , -90 , -100 ) ,
							new ParachuteCustomModelPartShape ( UniversalMaterial.WHITE_BANNER ,
																Parachute.Color.PLAYER ) )
				.build ( );
	}
	
	public static ParachuteCustomModel of ( ConfigurationSection section ) {
		return new ParachuteCustomModel ( ).load ( section );
	}
	
	/**
	 * @author AdrianSR / 10/09/2021 / 08:30 p. m.
	 */
	public static class Builder {
		
		protected final Map < ParachuteCustomPartPosition, ParachuteCustomModelPart > map = new HashMap <> ( );
		
		public Builder withPart ( ParachuteCustomModelPart part ) {
			map.put ( part.getPosition ( ) , part );
			return this;
		}
		
		public Builder withPart ( ParachuteCustomPartPosition position , ParachuteCustomModelPartShape shape ) {
			return withPart ( new ParachuteCustomModelPart ( position , shape ) );
		}
		
		public Builder withPart ( float x , float y , float z , float yaw ,
				float pitch , float roll , ParachuteCustomModelPartShape shape ) {
			return withPart ( new ParachuteCustomPartPosition ( x , y , z , yaw , pitch , roll ) , shape );
		}
		
		public Builder withPart ( float x , float y , float z , ParachuteCustomModelPartShape shape ) {
			return withPart ( new ParachuteCustomPartPosition ( x , y , z ) , shape );
		}
		
		public ParachuteCustomModel build ( ) {
			return new ParachuteCustomModel ( map );
		}
	}
	
	protected final Map < ParachuteCustomPartPosition, ParachuteCustomModelPart > map = new HashMap <> ( );
	
	public ParachuteCustomModel ( Map < ParachuteCustomPartPosition, ParachuteCustomModelPart > contents ) {
		contents.forEach ( ( position , part ) -> map.put ( position.clone ( ) , part.clone ( ) ) );
	}
	
	public ParachuteCustomModel ( ) {
		// to be loaded
	}
	
	public Map < ParachuteCustomPartPosition, ParachuteCustomModelPart > getParts ( ) {
		return Collections.unmodifiableMap ( map );
	}
	
	@Override
	public boolean isValid ( ) {
		return map.size ( ) > 0 && map.values ( ).stream ( ).anyMatch ( ParachuteCustomModelPart :: isValid );
	}
	
	@Override
	public boolean isInvalid ( ) {
		return !isValid ( );
	}
	
	@Override
	public ParachuteCustomModel load ( ConfigurationSection section ) {
		this.map.clear ( );
		
		// loading parts
		for ( String key : section.getKeys ( false ) ) {
			if ( section.isConfigurationSection ( key ) ) {
				ParachuteCustomModelPart part =
						ParachuteCustomModelPart.of ( section.getConfigurationSection ( key ) );
				
				if ( part.isValid ( ) ) {
					this.map.put ( part.getPosition ( ) , part );
				}
			}
		}
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save  = 0;
		int count = 0;
		
		for ( ParachuteCustomModelPart part : map.values ( ) ) {
			if ( part != null ) {
				save += part.save ( section.createSection (
						StringUtil.defaultIfBlank ( part.getName ( ) , "part-" + count ) ) );
				count++;
			}
		}
		
		return save;
	}
	
	@Override
	public ParachuteCustomModel clone ( ) {
		try {
			return ( ParachuteCustomModel ) super.clone ( );
		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError ( );
		}
	}
}