package es.outlook.adriansrj.battleroyale.game.mode;

import es.outlook.adriansrj.core.util.file.filter.FileExtensionFilter;

/**
 * Battle royale mode presentation.
 *
 * @author AdrianSR / 06/09/2021 / 09:54 p. m.
 */
public interface BattleRoyaleModePresentation {
	
	public String[] getExtensions ( );
	
	default FileExtensionFilter getFileFilter ( ) {
		return FileExtensionFilter.Multiplexer.of ( getExtensions ( ) );
	}
}