package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.core.util.server.Version;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 25/08/2021 / Time: 10:21 a. m.
 */
public enum EnumDataVersion {
	
	v1_9 ( 169 , false , "1.9" ),
	v1_10 ( 510 , false , "1.10" ),
	v1_11 ( 819 , false , "1.11" ),
	v1_12 ( 1139 , false , "1.12" ),
	v1_13 ( 1519 , false , "1.13" ),
	v1_14 ( 1952 , false , "1.14" ),
	v1_15 ( 2225 , false , "1.15" ),
	v1_16 ( 2566 , false , "1.16" ),
	v1_17 ( 2724 , false , "1.17" ),
	
	// TODO: add versions like: v1_12_2
	
	;
	
	public static EnumDataVersion getServerDataVersion ( ) {
		return fromServerVersion ( Version.getServerVersion ( ) );
	}
	
	public static EnumDataVersion fromServerVersion ( Version version ) {
		switch ( Version.getServerVersion ( ) ) {
			case v1_9_R1:
			case v1_9_R2: {
				return EnumDataVersion.v1_9;
			}
			
			case v1_10_R1:
				return EnumDataVersion.v1_10;
			case v1_11_R1:
				return EnumDataVersion.v1_11;
			case v1_12_R1:
				return EnumDataVersion.v1_12;
			
			case v1_13_R1:
			case v1_13_R2: {
				return EnumDataVersion.v1_13;
			}
			
			case v1_14_R1:
				return EnumDataVersion.v1_14;
			case v1_15_R1:
				return EnumDataVersion.v1_15;
			
			case v1_16_R1:
			case v1_16_R2:
			case v1_16_R3: {
				return EnumDataVersion.v1_16;
			}
			
			case v1_17_R1:
				return EnumDataVersion.v1_17;
			default:
				throw new IllegalStateException ( "not implemented" );
		}
	}
	
	public static EnumDataVersion getById ( int id , boolean exact ) {
		EnumDataVersion closest = null;
		
		for ( EnumDataVersion version : values ( ) ) {
			if ( version.getId ( ) == id ) {
				return version;
			} else if ( version.getId ( ) < id ) {
				closest = version;
			}
		}
		return exact ? null : closest;
	}
	
	public static EnumDataVersion getById ( int id ) {
		return getById ( id , false );
	}
	
	private final int     id;
	private final int     y_min;
	private final int     y_max;
	private final boolean snapshot;
	private final String  name;
	
	EnumDataVersion ( int id , int y_min , int y_max , boolean snapshot , String name ) {
		this.id       = id;
		this.y_min    = y_min;
		this.y_max    = y_max;
		this.snapshot = snapshot;
		this.name     = name;
	}
	
	EnumDataVersion ( int id , boolean snapshot , String name ) {
		this ( id , 0 , 256 , snapshot , name );
	}
	
	public int getId ( ) {
		return id;
	}
	
	public int getChunkMinimumY ( ) {
		return y_min;
	}
	
	public int getChunkMaximumY ( ) {
		return y_max;
	}
	
	public boolean isSnapshot ( ) {
		return snapshot;
	}
	
	public String getName ( ) {
		return name;
	}
}