package es.outlook.adriansrj.battleroyale.world.block;

import es.outlook.adriansrj.battleroyale.enums.EnumDataVersion;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.battleroyale.util.nbt.NBTConstants;
import es.outlook.adriansrj.battleroyale.util.nbt.NBTSerializable;
import es.outlook.adriansrj.core.util.server.Version;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Block tile entity.
 *
 * @author AdrianSR / 24/10/2021 / 01:11 p. m.
 */
public class BlockTileEntity implements NBTSerializable {
	
	protected EnumDataVersion data_version;
	protected String          id;
	protected int             x;
	protected int             y;
	protected int             z;
	
	public BlockTileEntity ( String id , int x , int y , int z ) {
		this.data_version = EnumDataVersion.getServerDataVersion ( );
		this.id           = id;
		this.x            = x;
		this.y            = y;
		this.z            = z;
	}
	
	public BlockTileEntity ( CompoundBinaryTag tag , EnumDataVersion data_version ) {
		this.data_version = Objects.requireNonNull ( data_version , "data version cannot be null" );
		
		if ( data_version.getId ( ) < EnumDataVersion.v1_13.getId ( ) ) {
			// id
			this.id = tag.getString ( NBTConstants.Pre13.CHUNK_TILE_ENTITY_ID_TAG );
			// coordinates
			this.x = tag.getInt ( NBTConstants.Pre13.CHUNK_TILE_ENTITY_X_TAG );
			this.y = tag.getInt ( NBTConstants.Pre13.CHUNK_TILE_ENTITY_Y_TAG );
			this.z = tag.getInt ( NBTConstants.Pre13.CHUNK_TILE_ENTITY_Z_TAG );
		} else { // legacy versions.
			// id
			this.id = tag.getString ( NBTConstants.Post13.CHUNK_TILE_ENTITY_ID_TAG );
			// coordinates
			this.x = tag.getInt ( NBTConstants.Post13.CHUNK_TILE_ENTITY_X_TAG );
			this.y = tag.getInt ( NBTConstants.Post13.CHUNK_TILE_ENTITY_Y_TAG );
			this.z = tag.getInt ( NBTConstants.Post13.CHUNK_TILE_ENTITY_Z_TAG );
		}
	}
	
	public BlockTileEntity ( BlockTileEntity copy ) {
		this.data_version = copy.data_version;
		this.id           = copy.id;
		this.x            = copy.x;
		this.y            = copy.y;
		this.z            = copy.z;
	}
	
	public String getId ( ) {
		return id;
	}
	
	public void setId ( String id ) {
		this.id = id;
	}
	
	public int getX ( ) {
		return x;
	}
	
	public void setX ( int x ) {
		this.x = x;
	}
	
	public int getY ( ) {
		return y;
	}
	
	public void setY ( int y ) {
		this.y = y;
	}
	
	public int getZ ( ) {
		return z;
	}
	
	public void setZ ( int z ) {
		this.z = z;
	}
	
	public CompoundBinaryTag toNBT ( EnumDataVersion data_version ) {
		Validate.notNull ( data_version , "data version cannot be null" );
		
		Map < String, BinaryTag > root = new HashMap <> ( );
		
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 ) ) {
			// id
			root.put ( NBTConstants.Post13.CHUNK_TILE_ENTITY_ID_TAG , StringBinaryTag.of ( id ) );
			// coordinates
			root.put ( NBTConstants.Post13.CHUNK_TILE_ENTITY_X_TAG , IntBinaryTag.of ( x ) );
			root.put ( NBTConstants.Post13.CHUNK_TILE_ENTITY_Y_TAG , IntBinaryTag.of ( y ) );
			root.put ( NBTConstants.Post13.CHUNK_TILE_ENTITY_Z_TAG , IntBinaryTag.of ( z ) );
		} else { // legacy versions.
			// id
			root.put ( NBTConstants.Pre13.CHUNK_TILE_ENTITY_ID_TAG , StringBinaryTag.of ( id ) );
			// coordinates
			root.put ( NBTConstants.Pre13.CHUNK_TILE_ENTITY_X_TAG , IntBinaryTag.of ( x ) );
			root.put ( NBTConstants.Pre13.CHUNK_TILE_ENTITY_Y_TAG , IntBinaryTag.of ( y ) );
			root.put ( NBTConstants.Pre13.CHUNK_TILE_ENTITY_Z_TAG , IntBinaryTag.of ( z ) );
		}
		
		return CompoundBinaryTag.from ( root );
	}
	
	@Override
	public CompoundBinaryTag toNBT ( ) {
		return toNBT ( data_version != null ? data_version : EnumDataVersion.getServerDataVersion ( ) );
	}
	
	@Override
	public String toString ( ) {
		return "BlockTileEntity{" +
				"id='" + id + '\'' +
				", x=" + x +
				", y=" + y +
				", z=" + z +
				'}';
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		
		BlockTileEntity that = ( BlockTileEntity ) o;
		
		return new EqualsBuilder ( ).append ( x , that.x ).append ( y , that.y )
				.append ( z , that.z ).append ( id , that.id ).isEquals ( );
	}
	
	@Override
	public int hashCode ( ) {
		return new HashCodeBuilder ( 17 , 37 )
				.append ( id ).append ( x ).append ( y ).append ( z ).toHashCode ( );
	}
}