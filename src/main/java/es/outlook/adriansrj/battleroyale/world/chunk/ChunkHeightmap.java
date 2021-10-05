package es.outlook.adriansrj.battleroyale.world.chunk;

/**
 * @author AdrianSR / 31/08/2021 / 11:12 a. m.
 */
public class ChunkHeightmap {
	
	protected final int values[] = new int[ 16 * 16 ];
	
	/**
	 * The 16 * 16 int array storing the heights of the chunk.
	 *
	 * @return chunk heights.
	 */
	public int[] getHeights ( ) {
		return values;
	}
	
	/**
	 * Gets the height at the specified relative coordinates <strong>x, z</strong>.
	 *
	 * @param x the block x relative to chunk ( 0 - 15 )
	 * @param z the block z relative to chunk ( 0 - 15 )
	 *
	 * @return the height, between 0 and 255.
	 */
	public int getHeight ( int x , int z ) {
		return values[ ( x & 0xF ) + ( z & 0xF ) * 16 ] & 0xFF;
	}
	
	/**
	 * Sets the height at the specified relative coordinates <strong>x, z</strong>.
	 *
	 * @param x the block x relative to chunk ( 0 - 15 )
	 * @param z the block z relative to chunk ( 0 - 15 )
	 *
	 * @return the height, between 0 and 255.
	 */
	public void setHeight ( int x , int z , int height ) {
		values[ ( x & 0xF ) + ( z & 0xF ) * 16 ] = height & 0xFF;
	}
}