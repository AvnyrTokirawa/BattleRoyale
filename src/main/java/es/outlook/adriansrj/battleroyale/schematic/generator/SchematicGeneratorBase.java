package es.outlook.adriansrj.battleroyale.schematic.generator;

import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShape;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShapeData;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShapePart;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.util.file.FileUtil;
import es.outlook.adriansrj.battleroyale.util.math.ChunkLocation;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.world.WorldUtil;
import es.outlook.adriansrj.battleroyale.world.chunk.provider.ChunkProviderWorldFolder;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author AdrianSR / 02/12/2021 / 10:14 a. m.
 */
public abstract class SchematicGeneratorBase extends SchematicGenerator {
	
	protected File                     copy_folder;
	protected ChunkProviderWorldFolder chunk_provider;
	
	protected SchematicGeneratorBase ( File world_folder ) {
		super ( world_folder );
	}
	
	protected SchematicGeneratorBase ( World world ) {
		super ( world );
	}
	
	@Override
	public BattlefieldShape generateBattlefieldShape ( BoundingBox bounds , File folder ) throws Exception {
		Vector origin = bounds.getMinimum ( );
		int    width  = ( int ) Math.round ( bounds.getWidth ( ) );
		int    height = ( int ) Math.round ( bounds.getHeight ( ) );
		int    depth  = ( int ) Math.round ( bounds.getDepth ( ) );
		
		// generating copy folder
		this.copy_folder    = new File ( EnumDirectory.BATTLEFIELD_TEMP_DIRECTORY.getDirectory ( ) ,
										 UUID.randomUUID ( ).toString ( ) );
		this.chunk_provider = new ChunkProviderWorldFolder ( copy_folder );
		
		if ( copy_folder.mkdirs ( ) ) {
			// finding out region files that are actually required.
			final Set < File > required = new HashSet <> ( );
			
			for ( int x = 0 ; x < width ; x++ ) {
				int xx = origin.getBlockX ( ) + x;
				for ( int z = 0 ; z < depth ; z++ ) {
					int zz = origin.getBlockZ ( ) + z;
					
					required.add ( FileUtil.getRegionFileWorldFolder (
							world_folder ,
							new ChunkLocation ( xx >> 4 , zz >> 4 ).getRegionLocation ( ) ) );
				}
			}
			
			// then creating a safe copy.
			WorldUtil.copyWorldRegionFolder (
					FileUtil.getRegionFolder ( world_folder ) ,
					FileUtil.getRegionFolder ( copy_folder ) ,
					required :: contains );
		} else {
			throw new IllegalStateException ( "couldn't generate copy folder" );
		}
		
		// data file
		BattlefieldShapeData data = new BattlefieldShapeData ( Math.max ( width , depth ) );
		data.save ( new File ( folder , BattlefieldShapeData.SHAPE_DATA_FILENAME ) );
		
		// generating parts
		Set < Location2I > done = new HashSet <> ( );
		
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
					
					generatePart ( new BoundingBox (
							part_min_x , origin.getBlockY ( ) , part_min_z ,
							part_max_x , origin.getBlockY ( ) + height , part_max_z ) , file );
				}
			}
		}
		
		return new BattlefieldShape ( data , done.stream ( ).map (
				BattlefieldShapePart :: new ).collect ( Collectors.toSet ( ) ) );
	}
	
	protected abstract void generatePart ( BoundingBox bounds , File out ) throws Exception;
	
	@Override
	public void dispose ( ) {
		// hey garbage collector, do your work!
		this.chunk_provider = null;
		
		// disposing copy folder
		if ( copy_folder.exists ( ) ) {
			try {
				FileUtil.deleteDirectory ( copy_folder );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
}
