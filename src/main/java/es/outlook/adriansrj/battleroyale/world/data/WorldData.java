package es.outlook.adriansrj.battleroyale.world.data;

import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.enums.EnumWorldGenerator;
import es.outlook.adriansrj.battleroyale.util.nbt.NBTSerializable;

/**
 * TODO: Description
 * </p>
 *
 * @author AdrianSR / 25/08/2021 / Time: 11:08 a. m.
 */
public interface WorldData extends NBTSerializable {
	
	public EnumDataVersion getDataVersion ( );
	
	public String getName ( );
	
	public boolean isInitialized ( );
	
	public EnumWorldGenerator getGeneratorType ( );
	
	public String getGeneratorOptions ( );
	
	public long getSeed ( );
	
	public void setSeed ( long seed );
	
	public boolean isGenerateStructures ( );
	
	public long getLastTimePlayed ( );
	
	public void setLastTimePlayed ( long last_time_played );
	
	public int getSpawnX ( );
	
	public int getSpawnY ( );
	
	public int getSpawnZ ( );
	
	public void setName ( String name );
	
	public void setInitialized ( boolean initialized );
	
	public void setGeneratorType ( EnumWorldGenerator generator_type );
	
	public void setGeneratorOptions ( String generator_options );
	
	public void setGenerateStructures ( boolean generate_structures );
	
	public void setSpawnX ( int spawn_x );
	
	public void setSpawnY ( int spawn_y );
	
	public void setSpawnZ ( int spawn_z );
}