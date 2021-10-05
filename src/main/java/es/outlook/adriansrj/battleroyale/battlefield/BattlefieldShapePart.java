package es.outlook.adriansrj.battleroyale.battlefield;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import es.outlook.adriansrj.battleroyale.exception.InvalidShapePartFileName;
import es.outlook.adriansrj.battleroyale.exception.InvalidShapePartLocation;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.schematic.SchematicUtil;
import es.outlook.adriansrj.core.util.file.FilenameUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Represents a part of a {@link BattlefieldShape}, which is a
 * segment of <b>128 x 255 x 128</b> blocks.
 *
 * @author AdrianSR / 25/09/2021 / 08:31 p. m.
 */
public class BattlefieldShapePart {
	
	public static final String PART_FILE_EXTENSION   = "part";
	public static final String PART_FILE_NAME_FORMAT = "%d-%d." + PART_FILE_EXTENSION;
	
	public static BattlefieldShapePart of ( File file ) throws InvalidShapePartLocation, InvalidShapePartFileName {
		String[] coordinates = FilenameUtil.getBaseName ( file.getName ( ) ).split ( "-" );
		
		if ( coordinates.length > 1 ) {
			try {
				int x = Integer.parseInt ( coordinates[ 0 ] );
				int z = Integer.parseInt ( coordinates[ 1 ] );
				
				if ( x >= 0 && z >= 0 ) {
					return new BattlefieldShapePart ( new Location2I ( x , z ) );
				} else {
					throw new InvalidShapePartLocation ( );
				}
			} catch ( NumberFormatException ex ) {
				throw new InvalidShapePartFileName ( file.getName ( ) );
			}
		}
		
		throw new InvalidShapePartFileName ( file.getName ( ) );
	}
	
	protected final int x;
	protected final int z;
	
	public BattlefieldShapePart ( int x , int z ) {
		Validate.isTrue ( x >= 0 , "x must be >= 0" );
		Validate.isTrue ( z >= 0 , "z must be >= 0" );
		
		this.x = x;
		this.z = z;
	}
	
	public BattlefieldShapePart ( Location2I location ) {
		this ( location.getX ( ) , location.getZ ( ) );
	}
	
	public int getX ( ) {
		return x;
	}
	
	public int getZ ( ) {
		return z;
	}
	
	public Location2I getLocation ( ) {
		return new Location2I ( x , z );
	}
	
	public final String getFileName ( ) {
		return String.format ( PART_FILE_NAME_FORMAT , x , z );
	}
	
	public Clipboard loadContent ( File folder ) throws FileNotFoundException {
		File file = new File ( folder , getFileName ( ) );
		
		if ( file.exists ( ) ) {
			try {
				return SchematicUtil.loadSchematic ( file );
			} catch ( ClassNotFoundException | NoSuchMethodException | InvocationTargetException
					| IllegalAccessException | InstantiationException | IOException ex ) {
				throw new IllegalArgumentException ( "shape part couldn't be loaded" , ex );
			}
		} else {
			throw new FileNotFoundException ( file.getAbsolutePath ( ) );
		}
	}
}