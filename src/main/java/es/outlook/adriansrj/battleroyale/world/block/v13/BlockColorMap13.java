package es.outlook.adriansrj.battleroyale.world.block.v13;

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
 * Maps post-1.13 block colors by: [Block Key] <-> [{@link BlockColor} id].
 * <br> <b>Block key format</b>: <code>minecraft:<b>block name</b></code>
 *
 * @author AdrianSR / 31/08/2021 / 10:34 p. m.
 */
public final class BlockColorMap13 extends BlockColorMap {
	
	/** the namespace representing all inbuilt keys. */
	public static final String          MINECRAFT = "minecraft";
	/** post-1.13 block color map. */
	public static final BlockColorMap13 INSTANCE;
	
	static {
		INSTANCE = new BlockColorMap13 ( );
		
		try {
			InputStream input = BattleRoyale.getInstance ( )
					.getResource ( "DefaultBlockColorsMap/v13/default-block-colors.map" );
			BufferedReader reader = new BufferedReader ( new InputStreamReader ( input ) );
			
			for ( String line : reader.lines ( ).collect ( Collectors.toList ( ) ) ) {
				if ( StringUtil.isBlank ( line ) ) {
					continue;
				}
				
				// minecraft:stone=[11;7368816]
				String[] a = line.split ( "=" );
				
				// minecraft:stone | [11;7368816]
				String namespaced_key = a[ 0 ].trim ( ).toLowerCase ( );
				String raw_value      = a[ 1 ].trim ( );
				
				// 11;7368816
				String[] b = raw_value.substring ( 1 , raw_value.length ( ) - 1 ).split ( ";" );
				
				// 11 | 7368816
				String uncast_id  = b[ 0 ].trim ( );
				String uncast_rgb = b[ 1 ].trim ( );
				
				int color_id  = Integer.parseInt ( uncast_id );
				int color_rgb = Integer.parseInt ( uncast_rgb );
				
				BlockColorDefault color = BlockColorDefault.getById ( color_id )
						.orElse ( BlockColorDefault.getByMinecraftRGB ( color_rgb ).orElse ( null ) );
				
				if ( color == null ) {
					throw new IllegalStateException ( );
				}
				
				INSTANCE.setColor ( namespaced_key , color );
			}
			
			reader.close ( );
			input.close ( );
		} catch ( IOException ex ) {
			ex.printStackTrace ( );
		}
	}
	
	private BlockColorMap13 ( ) {
		// just a private constructor
	}
	
	@Override
	protected boolean isValidBlockKey ( String block_key ) {
		block_key = block_key.trim ( ).toLowerCase ( );
		int index = block_key.indexOf ( ':' );
		int extra = block_key.lastIndexOf ( ':' );
		
		return index != -1 && index == extra && block_key.length ( ) > 2
				&& block_key.split ( ":" )[ 0 ].equalsIgnoreCase ( MINECRAFT );
	}
}
