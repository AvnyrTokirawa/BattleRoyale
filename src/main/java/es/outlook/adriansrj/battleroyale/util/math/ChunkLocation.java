package es.outlook.adriansrj.battleroyale.util.math;

/**
 * Represents a chunk location.
 * <p>
 *
 * @author AdrianSR / Saturday 29 August, 2020 / 02:14 PM
 */
public class ChunkLocation extends Location2I {
	
	protected final int        region_x;
	protected final int        region_z;
	protected final Location2I region_location;
	
	public ChunkLocation ( int x , int z ) {
		super ( x , z );
		
		this.region_x        = this.x >> 5;
		this.region_z        = this.z >> 5;
		this.region_location = new Location2I ( region_x , region_z );
	}
	
	public int getRegionX ( ) {
		return region_x;
	}
	
	public int getRegionZ ( ) {
		return region_z;
	}
	
	public Location2I getRegionLocation ( ) {
		return region_location;
	}
}