package es.outlook.adriansrj.battleroyale.world;

import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BaseBlock;
import es.outlook.adriansrj.battleroyale.util.nbt.NBTSerializable;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 1.13+ block material.
 *
 * @author AdrianSR / 25/08/2021 / Time: 03:36 p. m.
 */
public class Material implements NBTSerializable {
	
	/**
	 * Air material instance.
	 */
	public static final Material AIR = new Material ( "minecraft:air" );
	
	/**
	 * Gets the equivalent {@link Material} from a provided {@link BaseBlock}.
	 *
	 * @param block the block to extract data from.
	 *
	 * @return the resulting material.
	 */
	public static Material from ( BaseBlock block ) {
		Validate.notNull ( block , "block cannot be null" );
		Validate.notNull ( block.getBlockType ( ) , "block type cannot be null" );
		
		Material material = new Material ( block.getBlockType ( ).getId ( ) );
		
		// properties
		Map < Property < ? >, Object > states = block.getStates ( );
		
		for ( Map.Entry < Property < ? >, Object > entry : states.entrySet ( ) ) {
			material.setProperty ( entry.getKey ( ).getName ( ) ,
								   entry.getValue ( ).toString ( ).toLowerCase ( Locale.ROOT ) );
		}
		return material;
	}
	
	protected final String                 namespaced_id;
	protected final Map < String, String > properties;
	
	public Material ( String namespaced_id , Map < String, String > properties ) {
		this.namespaced_id = namespaced_id.toLowerCase ( ).trim ( );
		this.properties    = properties != null ? new HashMap <> ( properties ) : new HashMap <> ( );
	}
	
	public Material ( String namespaced_id ) {
		this ( namespaced_id , null );
	}
	
	public String getNamespacedId ( ) {
		return namespaced_id;
	}
	
	public Map < String, String > getProperties ( ) {
		return properties;
	}
	
	public boolean isEmpty ( ) {
		return StringUtils.isBlank ( namespaced_id ) || AIR.getNamespacedId ( ).equals ( getNamespacedId ( ) );
	}
	
	public String getProperty ( String property_name ) {
		return properties.get ( property_name );
	}
	
	public void setProperty ( String property_name , String property_value ) {
		properties.put ( property_name , property_value );
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass ( ) != o.getClass ( ) ) {
			return false;
		}
		Material material = ( Material ) o;
		return Objects.equals ( namespaced_id , material.namespaced_id ) && Objects.equals (
				properties , material.properties );
	}
	
	@Override
	public int hashCode ( ) {
		return Objects.hash ( namespaced_id , properties );
	}
	
	@Override
	public CompoundBinaryTag toNBT ( ) {
		Map < String, BinaryTag > root       = new HashMap <> ( );
		Map < String, BinaryTag > properties = new HashMap <> ( );
		
		root.put ( "Name" , StringBinaryTag.of ( namespaced_id ) );
		
		if ( this.properties.size ( ) > 0 ) {
			this.properties.forEach (
					( property_name , property_value ) ->
							properties.put ( property_name ,
											 StringBinaryTag.of ( property_value ) ) );
			
			root.put ( "Properties" , CompoundBinaryTag.from ( properties ) );
		}
		
		return CompoundBinaryTag.from ( root );
	}
}