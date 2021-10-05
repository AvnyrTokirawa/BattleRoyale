package es.outlook.adriansrj.battleroyale.world.block;

import org.apache.commons.lang.Validate;

import java.util.HashMap;

/**
 * Maps block colors by: [Block Key] <-> [{@link BlockColor} id].
 *
 * @author AdrianSR / 31/08/2021 / 12:01 p. m.
 */
public abstract class BlockColorMap {
	
	protected final HashMap < String, BlockColor > values = new HashMap <> ( );
	
	public BlockColor getColor ( String block_key ) {
		isValidBlockKey ( block_key );
		
		return values.getOrDefault ( block_key.trim ( ).toLowerCase ( ) , BlockColorDefault.AIR );
	}
	
	protected void setColor ( String block_key , BlockColor color ) {
		blockKeyCheck ( block_key );
		Validate.notNull ( color , "color cannot be null" );
		
		values.put ( block_key.trim ( ).toLowerCase ( ) , color );
	}
	
	/**
	 * Gets whether the provided block key is valid.
	 *
	 * @param block_key the key to check.
	 *
	 * @return true if matches the format.
	 */
	protected abstract boolean isValidBlockKey ( String block_key );
	
	/**
	 * @param block_key the key to check.
	 *
	 * @throws IllegalArgumentException if the key is invalid.
	 */
	protected void blockKeyCheck ( String block_key ) throws IllegalArgumentException {
		if ( !isValidBlockKey ( block_key ) ) {
			throw new IllegalArgumentException ( "invalid block key '" + block_key + "'" );
		}
	}
}