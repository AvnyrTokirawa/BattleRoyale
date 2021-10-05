package es.outlook.adriansrj.battleroyale.game.mode;

import es.outlook.adriansrj.battleroyale.enums.BattleRoyaleModeDefaultPresentation;
import es.outlook.adriansrj.core.util.file.FilenameUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for managing battle royale modes.
 * <p>
 * @author AdrianSR / Sunday 16 May, 2021 / 03:58 PM
 */
public class BattleRoyaleModeManager {
	
	private final Map < String, BattleRoyaleModeLoader > loaders = new HashMap <> ( );
	
	public BattleRoyaleModeManager ( boolean defaults ) {
		if ( defaults ) {
			BattleRoyaleModeDefaultPresentation other  = null;
			BattleRoyaleModeLoader              loader = null;
			
			for ( BattleRoyaleModeDefaultPresentation presentation : BattleRoyaleModeDefaultPresentation.values ( ) ) {
				if ( other != presentation ) {
					other  = presentation;
					loader = presentation.createLoader ( );
				}
				
				if ( loader == null ) {
					loader = presentation.createLoader ( );
				}
				
				registerLoader ( loader );
			}
		}
	}
	
	public BattleRoyaleModeManager ( ) {
		this ( true );
	}
	
	public void registerLoader ( BattleRoyaleModeLoader loader ) {
		for ( String extension : loader.getPresentation ( ).getExtensions ( ) ) {
			loaders.put ( extension.toLowerCase ( ).replace ( "." , "" ).trim ( ) , loader );
		}
	}
	
	public BattleRoyaleMode load ( File file ) throws FileNotFoundException {
		BattleRoyaleMode mode = null;
		
		if ( file.exists ( ) ) {
			BattleRoyaleModeLoader loader = loaders.get (
					FilenameUtil.getExtension ( file.getName ( ) )
							.replace ( "." , "" ).toLowerCase ( ).trim ( ) );
			
			if ( loader != null ) {
				try {
					if ( ( mode = loader.load ( file ) ) != null && mode.initialize ( ) ) {
						return mode;
					} else {
						throw new IllegalStateException (
								"something went wrong when loading mode '" + file.getName ( ) + "'" );
					}
				} catch ( IllegalArgumentException ex ) {
					throw new IllegalStateException (
							"something went wrong when loading mode '" + file.getName ( ) + "'" , ex );
				}
			} else {
				throw new IllegalArgumentException (
						"couldn't find a loader for '" + file.getName ( ) + "'!" );
			}
		} else {
			throw new FileNotFoundException ( file.getName ( ) + " doesn't exists" );
		}
	}
}