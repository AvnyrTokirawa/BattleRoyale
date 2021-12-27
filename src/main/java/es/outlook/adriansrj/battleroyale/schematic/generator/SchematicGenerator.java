package es.outlook.adriansrj.battleroyale.schematic.generator;

import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShape;
import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.schematic.generator.v12.SchematicGenerator_v12;
import es.outlook.adriansrj.battleroyale.schematic.generator.v13.SchematicGenerator_v13;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import org.bukkit.World;

import java.io.File;

/**
 * Battle royale plugin schematic generator, which <b>will not copy entities or biomes<b/>.<br> The {@link
 * SchematicGenerator}s are supposed to save only the required data into the schematic, and to be faster at the moment
 * of copying data. The {@link SchematicGenerator}s will extract the data directly from the files of the world, making
 * it really faster,<br> so the world should be saved before generating a schematic for accuracy.
 *
 * @author AdrianSR / 29/08/2021 / 01:44 p. m.
 */
public abstract class SchematicGenerator {
	
	public static SchematicGenerator newSchematicGenerator ( File world_folder , EnumDataVersion data_version ) {
		if ( data_version.getId ( ) < EnumDataVersion.v1_13.getId ( ) ) {
			return new SchematicGenerator_v12 ( world_folder , data_version );
		} else {
			return new SchematicGenerator_v13 ( world_folder , data_version );
		}
	}
	
	public static SchematicGenerator newSchematicGenerator ( World world , EnumDataVersion data_version ) {
		return newSchematicGenerator ( world.getWorldFolder ( ) , data_version );
	}
	
	public static SchematicGenerator newSchematicGenerator ( File world_folder ) {
		return newSchematicGenerator ( world_folder , EnumDataVersion.getServerDataVersion ( ) );
	}
	
	public static SchematicGenerator newSchematicGenerator ( World world ) {
		return newSchematicGenerator ( world.getWorldFolder ( ) );
	}
	
	protected final File world_folder;
	
	protected SchematicGenerator ( File world_folder ) {
		this.world_folder = world_folder;
	}
	
	protected SchematicGenerator ( World world ) {
		this ( world.getWorldFolder ( ) );
	}
	
	/**
	 * Blocks until done.
	 * <br>
	 * World save method <b>should not</b> be called before
	 * calling this method.
	 *
	 * @param bounds the bounds of the area to generate the schematic from.
	 * @param folder the folder of the battlefield.
	 */
	public abstract BattlefieldShape generateBattlefieldShape ( BoundingBox bounds , File folder ) throws Exception;
	
	/**
	 * Must be disposed in order
	 * to release using resources.
	 */
	public abstract void dispose ( );
}