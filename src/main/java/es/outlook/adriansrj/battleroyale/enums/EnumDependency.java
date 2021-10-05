package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.core.dependency.MavenDependency;
import es.outlook.adriansrj.core.plugin.Plugin;

import java.net.URLClassLoader;

/**
 * Enumerates all the required libraries. Unlike the ordinary library system, this will load the classes from an
 * <b>isolated</b> class loader to avoid conflicts with the libraries of another plugins.
 *
 * @author AdrianSR / 18/08/2021 / Time: 11:31 a. m.
 */
public enum EnumDependency {
	
	SQLITE_DRIVER ( "org.xerial" , "sqlite-jdbc" , "3.36.0.1" ),
	
	;
	
	private final String          coordinates;
	private final MavenDependency handle;
	private       URLClassLoader  loader;
	
	EnumDependency ( String coordinates ) {
		this.coordinates = coordinates;
		this.handle      = new MavenDependency ( coordinates );
	}
	
	EnumDependency ( String group_id , String artifact_id , String version ) {
		this ( group_id + ":" + artifact_id + ":" + version );
	}
	
	public String getCoordinates ( ) {
		return coordinates;
	}
	
	public URLClassLoader getLoader ( ) {
		if ( loader == null ) {
			loader = Plugin.LIBRARY_RESOLVER.createLoader ( true , handle );
		}
		
		return loader;
	}
}