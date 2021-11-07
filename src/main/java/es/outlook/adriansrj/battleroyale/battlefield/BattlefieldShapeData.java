package es.outlook.adriansrj.battleroyale.battlefield;

import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * {@link BattlefieldShape} data file.
 *
 * @author AdrianSR / 27/09/2021 / 10:50 a. m.
 */
public class BattlefieldShapeData {
	
	public static final String SHAPE_DATA_FILENAME = "shape.data";
	public static final String SIZE_EXACT_KEY      = "size-exact";
	
	public static BattlefieldShapeData of ( File file ) {
		try ( FileInputStream input = new FileInputStream ( file ) ) {
			CompoundBinaryTag compound   = BinaryTagIO.reader ( ).read ( input , BinaryTagIO.Compression.GZIP );
			int               size_exact = compound.getInt ( SIZE_EXACT_KEY );
			
			return new BattlefieldShapeData ( size_exact );
		} catch ( IOException ex ) {
			throw new IllegalArgumentException ( "couldn't read data file: " , ex );
		}
	}
	
	protected final int size_exact;
	
	public BattlefieldShapeData ( int size_exact ) {
		this.size_exact = size_exact;
	}
	
	public int getSizeExact ( ) {
		return size_exact;
	}
	
	public void save ( File file ) {
		try ( FileOutputStream output = new FileOutputStream ( file ) ) {
			CompoundBinaryTag compound = CompoundBinaryTag.builder ( )
					.put ( SIZE_EXACT_KEY , IntBinaryTag.of ( size_exact ) )
					.build ( );
			
			BinaryTagIO.writer ( ).write ( compound , output , BinaryTagIO.Compression.GZIP );
		} catch ( IOException ex ) {
			throw new IllegalArgumentException ( "couldn't write data file: " , ex );
		}
	}
}
