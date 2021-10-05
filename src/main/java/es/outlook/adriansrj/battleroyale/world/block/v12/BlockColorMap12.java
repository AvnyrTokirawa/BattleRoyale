package es.outlook.adriansrj.battleroyale.world.block.v12;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.world.block.BlockColor;
import es.outlook.adriansrj.battleroyale.world.block.BlockColorDefault;
import es.outlook.adriansrj.battleroyale.world.block.BlockColorMap;
import es.outlook.adriansrj.core.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Maps pre-1.13 block colors by: [Block Key] <-> [{@link BlockColor} id].
 * <br> <b>Block key format</b>: <code>[<b>block id</b>]:[<b>block data</b>]</code>
 *
 * @author AdrianSR / 31/08/2021 / 12:13 p. m.
 */
public final class BlockColorMap12 extends BlockColorMap {
	
	/** pre-1.13 block key format: <code>[<b>block id</b>]:[<b>block data</b>]</code> */
	public static final String          BLOCK_KEY_FORMAT = "%d:%d";
	/** pre-1.13 block color map. */
	public static final BlockColorMap12 INSTANCE;
	
	static {
		INSTANCE = new BlockColorMap12 ( );
		
		try {
			InputStream input = BattleRoyale.getInstance ( )
					.getResource ( "DefaultBlockColorsMap/v12/default-block-colors.map" );
			BufferedReader reader = new BufferedReader ( new InputStreamReader ( input ) );
			
			for ( String line : reader.lines ( ).collect ( Collectors.toList ( ) ) ) {
				if ( StringUtil.isBlank ( line ) ) {
					continue;
				}
				
				// 18:6=[7;31744]
				String[] a = line.split ( "=" );
				
				// 18:6 | [7;31744]
				String key       = a[ 0 ].trim ( );
				int    id        = Integer.parseInt ( key.split ( ":" )[ 0 ].trim ( ) );
				int    data      = Integer.parseInt ( key.split ( ":" )[ 1 ].trim ( ) );
				String raw_value = a[ 1 ].trim ( );
				
				// 7;31744
				String[] b = raw_value.substring ( 1 , raw_value.length ( ) - 1 ).split ( ";" );
				
				// 7 | 31744
				String uncast_id  = b[ 0 ].trim ( );
				String uncast_rgb = b[ 1 ].trim ( );
				
				int color_id  = Integer.parseInt ( uncast_id );
				int color_rgb = Integer.parseInt ( uncast_rgb );
				
				BlockColorDefault color = BlockColorDefault.getById ( color_id )
						.orElse ( BlockColorDefault.getByMinecraftRGB ( color_rgb ).orElse ( null ) );
				
				if ( color == null ) {
					throw new IllegalStateException ( );
				}
				
				INSTANCE.setColor ( key , color );
			}
			
			reader.close ( );
			input.close ( );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		}
	}
	
	private BlockColorMap12 ( ) {
		// just a private constructor
	}
	
	public BlockColor getColor ( int block_id , byte block_data ) {
		return getColor ( formatKey ( block_id , block_data ) );
	}
	
	public BlockColor getColor ( int block_id ) {
		return getColor ( block_id , ( byte ) 0 );
	}
	
	protected String formatKey ( int block_id , int block_data ) {
		return String.format ( BLOCK_KEY_FORMAT , block_id , block_data );
	}
	
	@Override
	protected boolean isValidBlockKey ( String block_key ) {
		block_key = block_key.trim ( );
		int index = block_key.indexOf ( ':' );
		int extra = block_key.lastIndexOf ( ':' );
		
		if ( index != -1 && index == extra && block_key.length ( ) > 2 ) {
			String[] split = block_key.split ( ":" );
			
			try {
				Integer.valueOf ( split[ 0 ].trim ( ) );
				Integer.valueOf ( split[ 1 ].trim ( ) );
				return true;
			} catch ( NumberFormatException ex ) {
				return false;
			}
		}
		return false;
	}
}