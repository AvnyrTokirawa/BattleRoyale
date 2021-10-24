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
	 *
	 * @param world
	 * @param bounds
	 * @param folder the folder of the battlefield.
	 */
	default BattlefieldShape generateBattlefieldShape ( World world , BoundingBox bounds , File folder )
			throws Exception {
		Set < Location2I > done   = new HashSet <> ( );
		Vector             origin = bounds.getMinimum ( );
		int                width  = ( int ) Math.round ( bounds.getWidth ( ) );
		int                height = ( int ) Math.round ( bounds.getHeight ( ) );
		int                depth  = ( int ) Math.round ( bounds.getDepth ( ) );
		
		// data file
		BattlefieldShapeData data = new BattlefieldShapeData ( Math.max ( width , depth ) );
		data.save ( new File ( folder , BattlefieldShapeData.SHAPE_DATA_FILENAME ) );
		
		// generating parts
		for ( int x = 0 ; x < width ; x++ ) {
			for ( int z = 0 ; z < depth ; z++ ) {
				int        part_x        = x >> 7;
				int        part_z        = z >> 7;
				Location2I part_location = new Location2I ( part_x , part_z );
				
				if ( done.add ( part_location ) ) {
					File file = new File ( folder , String.format (
							BattlefieldShapePart.PART_FILE_NAME_FORMAT , part_x , part_z ) );
					
					int part_min_x = origin.getBlockX ( ) + ( part_x << 7 );
					int part_min_z = origin.getBlockZ ( ) + ( part_z << 7 );
					int part_max_x = part_min_x + ( 1 << 7 );
					int part_max_z = part_min_z + ( 1 << 7 );
					
					generate ( world , new BoundingBox (
							part_min_x , origin.getBlockY ( ) , part_min_z ,
							part_max_x , origin.getBlockY ( ) + height , part_max_z ) , file );
				}
			}
		}
		
		return new BattlefieldShape ( data , done.stream ( ).map (
				BattlefieldShapePart :: new ).collect ( Collectors.toSet ( ) ) );
	}
	
	void generate ( World world , BoundingBox bounds , File out ) throws Exception;
	
}