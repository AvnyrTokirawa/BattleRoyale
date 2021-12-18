package es.outlook.adriansrj.battleroyale.util.schematic;

import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.schematic.generator.SchematicGenerator;
import es.outlook.adriansrj.battleroyale.util.file.filter.RegionFileFilter;
import es.outlook.adriansrj.battleroyale.util.world.RegionUtil;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import es.outlook.adriansrj.core.util.server.Version;
import es.outlook.adriansrj.core.util.world.WorldUtil;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

/**
 * Useful class when dealing with schematics.
 *
 * @author AdrianSR / 24/08/2021 / Time: 12:51 p. m.
 */
public class SchematicUtil {
	
	public static void generateSchematic ( World world , BoundingBox bounds , File out ) throws Exception {
		SchematicGenerator.newSchematicGenerator ( getDataVersion ( world ) )
				.generate ( world , bounds , out );
	}
	
	/**
	 * Blocks until done.
	 *
	 * @param world
	 * @param bounds
	 * @param folder the folder of the battlefield.
	 */
	public static void generateBattlefieldShape ( World world , BoundingBox bounds , File folder ) {
		try {
			SchematicGenerator.newSchematicGenerator ( getDataVersion ( world ) )
					.generateBattlefieldShape ( world , bounds , folder );
		} catch ( Exception e ) {
			e.printStackTrace ( );
		}
	}
	
	private static EnumDataVersion getDataVersion ( World world ) {
		File            world_folder  = world.getWorldFolder ( );
		File            region_folder = new File ( world_folder , WorldUtil.REGION_FOLDER_NAME );
		EnumDataVersion data_version  = EnumDataVersion.getServerDataVersion ( );
		
		if ( region_folder.exists ( ) ) {
			for ( File region_file : Objects.requireNonNull (
					region_folder.listFiles ( new RegionFileFilter ( ) ) ) ) {
				try {
					if ( region_file.exists ( ) && Files.size ( region_file.toPath ( ) ) > 0L ) {
						EnumDataVersion region_version = RegionUtil.getRegionDataVersion ( region_file );
						
						if ( region_version != null ) {
							data_version = region_version;
							break;
						}
					}
				} catch ( IOException e ) {
					e.printStackTrace ( );
				}
			}
		}
		return data_version;
	}
	
	public static Clipboard loadSchematic ( File file )
			throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
			IllegalAccessException, InstantiationException {
		// this code looks really weird, but since world edit
		// package names are different in 6.0 and 7.0, we have
		// no better ways to do this.
		Clipboard schematic = null;
		
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 ) ) {
			com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat format =
					com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats.findByFile ( file );
			
			// trying to find by alias
			if ( format == null ) {
				format = com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
						.findByAlias ( FilenameUtil.getExtension ( file.getName ( ) ) );
			}
			
			if ( format != null ) {
				schematic = format.getReader ( new FileInputStream ( file ) ).read ( );
			} else {
				throw new IllegalArgumentException ( "unknown format '" + file.getAbsolutePath ( ) + "'" );
			}
		} else {
			try ( NBTInputStream input = new NBTInputStream ( new GZIPInputStream (
					new FileInputStream ( file ) ) ) ) {
				Class < ? > legacy_schematic_reader_class = Class
						.forName ( "com.sk89q.worldedit.extent.clipboard.io.SchematicReader" );
				Class < ? > world_data_class = Class
						.forName ( "com.sk89q.worldedit.world.registry.WorldData" );
				
				Method read_method = legacy_schematic_reader_class.getMethod ( "read" , world_data_class );
				Object reader = legacy_schematic_reader_class
						.getConstructor ( NBTInputStream.class ).newInstance ( input );
				
				schematic = ( Clipboard ) read_method.invoke (
						reader ,
						world_data_class.cast ( null ) );
			}
		}
		
		return schematic;
	}
}