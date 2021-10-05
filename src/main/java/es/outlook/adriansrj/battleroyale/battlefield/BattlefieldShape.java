package es.outlook.adriansrj.battleroyale.battlefield;

import es.outlook.adriansrj.battleroyale.exception.InvalidShapePartFileName;
import es.outlook.adriansrj.battleroyale.exception.InvalidShapePartLocation;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.core.util.file.filter.FileExtensionFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Represents the shape of a {@link Battlefield}, which is
 * composed by an undefined number of {@link BattlefieldShapePart}s.
 *
 * @author AdrianSR / 25/09/2021 / 08:31 p. m.
 */
public class BattlefieldShape {
	
	/**
	 *
	 * @param folder the folder of the battlefield.
	 * @return
	 * @throws FileNotFoundException if the data file doesn't exist.
	 */
	public static BattlefieldShape of ( File folder ) throws FileNotFoundException {
		// loading data
		File                 data_file = getDataFile ( folder );
		BattlefieldShapeData data      = BattlefieldShapeData.of ( data_file );
		
		// loading parts
		Set < BattlefieldShapePart > parts = new HashSet <> ( );
		
		for ( File file : Objects.requireNonNull (
				folder.listFiles ( FileExtensionFilter.of ( BattlefieldShapePart.PART_FILE_EXTENSION ) ) ) ) {
			try {
				parts.add ( BattlefieldShapePart.of ( file ) );
			} catch ( InvalidShapePartLocation | InvalidShapePartFileName ex ) {
				BattleRoyale.getInstance ( ).getLogger ( )
						.warning ( "Invalid shape part file: " + file.getAbsolutePath ( ) );
			}
		}
		
		return new BattlefieldShape ( data , parts );
	}
	
	protected static File getDataFile ( File folder ) throws FileNotFoundException {
		File data_file = new File ( folder , BattlefieldShapeData.SHAPE_DATA_FILENAME );
		
		if ( data_file.exists ( ) ) {
			return data_file;
		} else {
			throw new FileNotFoundException ( "missing data file. (" + data_file.getName ( ) + ")" );
		}
	}
	
	protected final Map < Location2I, BattlefieldShapePart > part_map = new HashMap <> ( );
	protected final BattlefieldShapeData                     data;
	protected final int                                      size;
	
	public BattlefieldShape ( BattlefieldShapeData data , Collection < BattlefieldShapePart > parts ) {
		this.data = data;
		
		// mapping parts
		parts.forEach ( part -> part_map.put ( part.getLocation ( ) , part ) );
		
		// then finding out size
		int maximum = 0;
		
		for ( Location2I location : part_map.keySet ( ) ) {
			maximum = Math.max ( maximum , location.getX ( ) );
			maximum = Math.max ( maximum , location.getZ ( ) );
		}
		
		this.size = maximum;
	}
	
	public BattlefieldShape ( BattlefieldShapeData data , BattlefieldShapePart... parts ) {
		this ( data , Arrays.asList ( parts ) );
	}
	
	public Map < Location2I, BattlefieldShapePart > getParts ( ) {
		return Collections.unmodifiableMap ( part_map );
	}
	
	public BattlefieldShapeData getData ( ) {
		return data;
	}
	
	public int getSizeExact ( ) {
		return data.getSizeExact ( );
	}
	
	public int getSize ( ) {
		return size;
	}
	
	public int getSizeInBlocks ( ) {
		return ( size + 1 ) << 7;
	}
	
	public BattlefieldShapePart getPart ( Location2I location ) {
		return part_map.get ( location );
	}
	
	public BattlefieldShapePart getPart ( int x , int z ) {
		return getPart ( new Location2I ( x , z ) );
	}
}