package es.outlook.adriansrj.battleroyale.world.arena;

import es.outlook.adriansrj.battleroyale.world.arena.v12.ArenaWorldGenerator12;
import es.outlook.adriansrj.battleroyale.world.data.v12.WorldData12;
import es.outlook.adriansrj.battleroyale.enums.EnumWorldGenerator;

import java.io.File;

/**
 * @author AdrianSR / 24/08/2021 / Time: 03:41 p. m.
 */
public class ArenaWorldGeneratorMainv1_9_v1_12 {
	
	public static final int NBT_VERSION = 19133;
	
	public static File getWorldFolder ( ) {
		File folder = new File (
				new File ( System.getProperty ( "user.dir" ) ) , "result" );
		
		folder.mkdirs ( );
		return folder;
	}
	
	public static void main ( String[] args ) {
		ArenaWorldGenerator12 world = new ArenaWorldGenerator12 ( getWorldFolder ( ) );
		WorldData12           data  = world.getWorldData ( );
		
		data.setName ( "BattleRoyaleArenaWorld" );
		data.setGeneratorType ( EnumWorldGenerator.FLAT );
		data.setGeneratorOptions ( "2;0;1" ); // void
		data.setGenerateStructures ( false );
		data.setInitialized ( false );
		data.setSpawnX ( 0 );
		data.setSpawnY ( 0 );
		data.setSpawnZ ( 0 );
		
		world.setBlockAt ( 0 , 0 , 0 , ( byte ) 2 );
		world.setBlockAt ( 0 , 1 , 0 , ( byte ) 2 );
		world.setBlockAt ( 0 , 2 , 0 , ( byte ) 2 );
		
		world.save ( );
	}
}
