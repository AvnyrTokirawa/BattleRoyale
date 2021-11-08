package es.outlook.adriansrj.battleroyale.util.file;

import es.outlook.adriansrj.core.util.file.filter.FileExtensionFilter;
import es.outlook.adriansrj.core.util.server.Version;

/**
 * Useful class for dealing with files.
 *
 * @author AdrianSR / 30/08/2021 / 04:26 p. m.
 */
public class FileUtil extends es.outlook.adriansrj.core.util.file.FileUtil {
	
	public static final String[] MCEDIT_SCHEMATIC_EXTENSIONS        = { "schematic" , "mcedit" , "mce" };
	public static final String   MCEDIT_SCHEMATIC_PRIMARY_EXTENSION = MCEDIT_SCHEMATIC_EXTENSIONS[ 0 ];
	public static final String[] SPONGE_SCHEMATIC_EXTENSIONS        = { "schem" , "sponge" };
	public static final String   SPONGE_SCHEMATIC_PRIMARY_EXTENSION = SPONGE_SCHEMATIC_EXTENSIONS[ 0 ];
	
	public static final FileExtensionFilter.Multiplexer MCEDIT_SCHEMATICS_FILE_FILTER = FileExtensionFilter
			.Multiplexer.of ( MCEDIT_SCHEMATIC_EXTENSIONS );
	public static final FileExtensionFilter.Multiplexer SPONGE_SCHEMATICS_FILE_FILTER = FileExtensionFilter
			.Multiplexer.of ( SPONGE_SCHEMATIC_EXTENSIONS );
	
	/**
	 * Schematic file filter that accepts any schematic format.
	 */
	public static final FileExtensionFilter GENERIC_SCHEMATIC_FILE_FILTER = FileExtensionFilter.Multiplexer.of (
			MCEDIT_SCHEMATICS_FILE_FILTER ,
			SPONGE_SCHEMATICS_FILE_FILTER );
	
	/**
	 * The proper schematic extension for the server this plugin is running on.
	 */
	// the schematic format of McEdit is not supported for minecraft versions > 1.12,
	// so sponge schematic format is to be used instead.
	public static final String PROPER_SCHEMATIC_EXTENSION =
			Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 )
					? FileUtil.SPONGE_SCHEMATIC_PRIMARY_EXTENSION
					: FileUtil.MCEDIT_SCHEMATIC_PRIMARY_EXTENSION;
}