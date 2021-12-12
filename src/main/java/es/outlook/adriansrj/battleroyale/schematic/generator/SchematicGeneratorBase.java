package es.outlook.adriansrj.battleroyale.schematic.generator;

import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShape;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShapeData;
import es.outlook.adriansrj.battleroyale.battlefield.BattlefieldShapePart;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.file.filter.RegionFileFilter;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import es.outlook.adriansrj.core.util.world.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @author AdrianSR / 02/12/2021 / 10:14 a. m.
 */
public abstract class SchematicGeneratorBase implements SchematicGenerator {
	
	volatile boolean jeje = true;
	
	/**
	 * Saves the last modification time of each region file
	 * before calling save().
	 */
	protected final Map < File, Long > busy_map = new HashMap <> ( );
	
	private void test ( World world ) {
		// populating queue
		queue.clear ( );
		queue.addAll ( Arrays.asList ( Objects.requireNonNull ( new File (
				world.getWorldFolder ( ) , WorldUtil.REGION_FOLDER_NAME ).listFiles ( new RegionFileFilter ( ) ) ) ) );
		queue.forEach ( file -> {
			try {
				busy_map.put ( file , Files.size ( file.toPath ( ) ) );
				System.out.println ( ">>>> " + file.getName () + " size (before) = " + busy_map.get ( file ) );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		} );
		
		world.save ( );
		jeje = false;
		
		//		File region_folder = new File ( world.getWorldFolder ( ) , WorldUtil.REGION_FOLDER_NAME );
		//
		//		if ( region_folder.exists ( ) ) {
		//			for ( File region_file : Objects.requireNonNull ( region_folder.listFiles ( new RegionFileFilter ( ) ) ) ) {
		//				try ( FileWriter ignored = new FileWriter ( region_file ) ) {
		//					// no exception = file not open by another process
		//					busy_map.remove ( region_file );
		//				} catch ( IOException ex ) {
		//					// file open by another process, let's mark as busy.
		//					busy_map.put ( region_file , region_file.lastModified ( ) );
		//				}
		//			}
		//		}
	}
	
	protected final ConcurrentLinkedQueue < File > queue = new ConcurrentLinkedQueue <> ( );
	
//	@Override
//	public BattlefieldShape generateBattlefieldShape ( World world , BoundingBox bounds , File folder ) throws Exception {
//		// we're disabling the auto-saving as we don't want
//		// the world to be saved while we're reading its files
//		final boolean autosave = world.isAutoSave ( );
//		world.setAutoSave ( false );
//
//		/* saving world */
//		if ( Bukkit.isPrimaryThread ( ) ) {
//			test ( world );
//		} else {
//			Bukkit.getScheduler ( ).runTask (
//					BattleRoyale.getInstance ( ) , ( ) -> test ( world ) );
//		}
//
//		while ( jeje ) {
//			System.out.println ( "WAITING" );
//		}
//
//		while ( queue.size ( ) > 0 ) {
//			System.out.println ( ">>>> queue: " + queue.size ( ) );
//			File next = queue.peek ( );
//
//			if ( next == null ) {
//				break;
//			}
//
//			try ( FileWriter ignored = new FileWriter ( next ) ) {
//				// access granted
//				queue.remove ( next );
//				System.out.println ( ">>>> " + next.getName () + " size (during) = " + busy_map.get ( next ) );
//			} catch ( IOException ex ) {
//				// region file is probably being modified
//				// by the server right now, or will be at
//				// any moment so let's wait until then.
//			}
//		}
//
//		/* then generating */
//		Set < Location2I > done   = new HashSet <> ( );
//		Vector             origin = bounds.getMinimum ( );
//		int                width  = ( int ) Math.round ( bounds.getWidth ( ) );
//		int                height = ( int ) Math.round ( bounds.getHeight ( ) );
//		int                depth  = ( int ) Math.round ( bounds.getDepth ( ) );
//
//		// data file
//		BattlefieldShapeData data = new BattlefieldShapeData ( Math.max ( width , depth ) );
//		data.save ( new File ( folder , BattlefieldShapeData.SHAPE_DATA_FILENAME ) );
//
//		// generating parts
//		for ( int x = 0 ; x < width ; x++ ) {
//			for ( int z = 0 ; z < depth ; z++ ) {
//				int        part_x        = x >> 7;
//				int        part_z        = z >> 7;
//				Location2I part_location = new Location2I ( part_x , part_z );
//
//				if ( done.add ( part_location ) ) {
//					File file = new File ( folder , String.format (
//							BattlefieldShapePart.PART_FILE_NAME_FORMAT , part_x , part_z ) );
//
//					int part_min_x = origin.getBlockX ( ) + ( part_x << 7 );
//					int part_min_z = origin.getBlockZ ( ) + ( part_z << 7 );
//					int part_max_x = part_min_x + ( 1 << 7 );
//					int part_max_z = part_min_z + ( 1 << 7 );
//
//					generate ( world , new BoundingBox (
//							part_min_x , origin.getBlockY ( ) , part_min_z ,
//							part_max_x , origin.getBlockY ( ) + height , part_max_z ) , file );
//				}
//			}
//		}
//
//		// re-enabling auto-save
//		world.setAutoSave ( autosave );
//
//		return new BattlefieldShape ( data , done.stream ( ).map (
//				BattlefieldShapePart :: new ).collect ( Collectors.toSet ( ) ) );
//	}
	
	@Override
	public BattlefieldShape generateBattlefieldShape ( World world , BoundingBox bounds , File folder ) throws Exception {
		// we're disabling the auto-saving as we don't want
		// the world to be saved while we're reading its files
		final boolean autosave = world.isAutoSave ( );
		world.setAutoSave ( false );
		
		/* then generating */
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
		
		// re-enabling auto-save
		world.setAutoSave ( autosave );
		
		return new BattlefieldShape ( data , done.stream ( ).map (
				BattlefieldShapePart :: new ).collect ( Collectors.toSet ( ) ) );
	}
}
