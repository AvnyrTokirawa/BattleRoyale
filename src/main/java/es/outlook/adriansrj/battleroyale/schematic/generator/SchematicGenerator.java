package es.outlook.adriansrj.battleroyale.schematic.generator;

import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShape;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShapeData;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShapePart;
import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.schematic.generator.v12.SchematicGenerator_v12;
import es.outlook.adriansrj.battleroyale.schematic.generator.v13.SchematicGenerator_v13;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Battle royale plugin schematic generator, which <b>will not copy entities or biomes<b/>.<br> The {@link
 * SchematicGenerator}s are supposed to save only the required data into the schematic, and to be faster at the moment
 * of copying data. The {@link SchematicGenerator}s will extract the data directly from the files of the world, making
 * it really faster,<br> so the world should be saved before generating a schematic for accuracy.
 *
 * @author AdrianSR / 29/08/2021 / 01:44 p. m.
 */
public interface SchematicGenerator {
	
	static SchematicGenerator newSchematicGenerator ( EnumDataVersion data_version ) {
		if ( data_version.getId ( ) < EnumDataVersion.v1_13.getId ( ) ) {
			return new SchematicGenerator_v12 ( data_version );
		} else {
			return new SchematicGenerator_v13 ( data_version );
		}
	}
	
	static SchematicGenerator newSchematicGenerator ( ) {
		return newSchematicGenerator ( EnumDataVersion.getServerDataVersion ( ) );
	}
	
	/**
	 * Blocks until done.
	 * <br>
	 * World save method <b>should not</b> be called before
	 * calling this method.
	 *
	 * @param world
	 * @param bounds
	 * @param folder the folder of the battlefield.
	 */
	BattlefieldShape generateBattlefieldShape ( World world , BoundingBox bounds , File folder ) throws Exception;
	
	/**
	 * Blocks until done.
	 *
	 * @param world
	 * @param bounds
	 * @param out
	 * @throws Exception
	 */
	void generate ( World world , BoundingBox bounds , File out ) throws Exception;
}