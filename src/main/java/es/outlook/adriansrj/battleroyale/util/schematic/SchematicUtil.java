package es.outlook.adriansrj.battleroyale.util.schematic;

import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.schematic.generator.SchematicGenerator;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.zip.GZIPInputStream;

/**
 * Useful class when dealing with schematics.
 *
 * @author AdrianSR / 24/08/2021 / Time: 12:51 p. m.
 */
public class SchematicUtil {
	
	public static void generateSchematic ( World world , BoundingBox bounds , File out ) throws Exception {
		SchematicGenerator generator = SchematicGenerator.newSchematicGenerator (
				EnumDataVersion.getServerDataVersion ( ) );
		
		generator.generate ( world , bounds , out );
	}
	
	/**
	 *
	 * @param world
	 * @param bounds
	 * @param folder the folder of the battlefield.
	 */
	public static void generateBattlefieldShape ( World world , BoundingBox bounds , File folder ) {
		SchematicGenerator generator = SchematicGenerator.newSchematicGenerator (
				EnumDataVersion.getServerDataVersion ( ) );
		
		try {
			generator.generateBattlefieldShape ( world , bounds , folder );
		} catch ( Exception e ) {
			e.printStackTrace ( );
		}
	}
	
	//	public static void saveSchematic ( Clipboard schematic , File out ) throws IOException {
	//		ClipboardWriter writer = null;
	//
	//		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 ) ) {
	//			FileOutputStream output = new FileOutputStream ( out );
	//
	//			writer = com.sk89q.worldedit.extent.clipboard.io
	//					.BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter ( output );
	//			writer.write ( schematic );
	//		} else {
	//			NBTOutputStream output = new NBTOutputStream ( new GZIPOutputStream ( new FileOutputStream ( out ) ) );
	//
	//			try {
	//				writer = ConstructorReflection.newInstance (
	//						Class.forName ( "com.sk89q.worldedit.extent.clipboard.io.SchematicWriter" ) ,
	//						new Class < ? >[] { NBTOutputStream.class } ,
	//						output );
	//
	//				Method write_method = MethodReflection.getAccessible (
	//						ClipboardWriter.class , "write" ,
	//						Clipboard.class , Class.forName ( "com.sk89q.worldedit.world.registry.WorldData" ) );
	//
	//				write_method.invoke ( writer , schematic , null );
	//			} catch ( ClassNotFoundException | InvocationTargetException
	//					| InstantiationException | NoSuchMethodException | IllegalAccessException e ) {
	//				e.printStackTrace ( );
	//			}
	//		}
	//
	//		writer.close ( );
	//	}
	//
	//	public static Clipboard createBattleRoyaleSchematic ( World world , Vector min_corner , Vector max_corner ) {
	//		// TODO
	//		return null;
	//	}
	
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