package es.outlook.adriansrj.battleroyale.world.arena;

import es.outlook.adriansrj.battleroyale.enums.EnumWorldGenerator;
import es.outlook.adriansrj.battleroyale.world.Material;
import es.outlook.adriansrj.battleroyale.world.arena.v13.ArenaWorldGenerator13;
import es.outlook.adriansrj.battleroyale.world.data.v13.WorldData13;

import java.io.File;

/**
 * @author AdrianSR / 24/08/2021 / Time: 03:41 p. m.
 */
public class ArenaWorldGeneratorMainv1_13_v1_17 {
	
	public static final int NBT_VERSION = 19133;
	
	public static File getWorldFolder ( ) {
		File folder = new File (
				new File ( System.getProperty ( "user.dir" ) ) , "result_v1_13_v1_17" );
		
		folder.mkdirs ( );
		return folder;
	}
	
	public static File getSchematicFolder ( ) {
		File folder = new File (
				new File ( System.getProperty ( "user.dir" ) ) , "schematics" );
		
		folder.mkdirs ( );
		return folder;
	}
	
	public static void main ( String[] args ) {
		ArenaWorldGenerator13 world = new ArenaWorldGenerator13 ( getWorldFolder ( ) );
		WorldData13           data  = world.getWorldData ( );
		
		data.setName ( "BattleRoyaleArenaWorld" );
		data.setGeneratorType ( EnumWorldGenerator.FLAT );
		data.setGenerateStructures ( false );
		data.setInitialized ( true );
		data.setSpawnX ( 0 );
		data.setSpawnY ( 0 );
		data.setSpawnZ ( 0 );
		
		world.setMaterialAt ( 0 , 0 , 0 , new Material ( "minecraft:diamond_ore" ) );
		world.setMaterialAt ( 0 , 1 , 0 , new Material ( "minecraft:diamond_block" ) );
		world.setMaterialAt ( 0 , 2 , 0 , new Material ( "minecraft:gold_ore" ) );
		
		// schematic
//		File schematic_folder = getSchematicFolder ( );
//		File schematic_file   = new File ( schematic_folder , "sponge-schematic.schem" );
//
//		try {
//			FileInputStream input     = new FileInputStream ( schematic_file );
//			Clipboard       schematic = ClipboardFormats.findByFile ( schematic_file )
//					.getReader ( input ).read ( );
//
//			world.insert ( schematic , Vector3.at ( 0 , 0 , 0 ) , true );
//
//			input.close ( );
//		} catch ( IOException e ) {
//			e.printStackTrace ( );
//		}
		
		world.save ( );
	}
}
