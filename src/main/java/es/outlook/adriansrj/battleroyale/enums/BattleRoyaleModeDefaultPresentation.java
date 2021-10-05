package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleModeLoader;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleModePresentation;
import es.outlook.adriansrj.battleroyale.game.mode.complex.ComplexBattleRoyaleModeLoader;
import es.outlook.adriansrj.battleroyale.game.mode.simple.SimpleBattleRoyaleModeLoader;

import java.io.File;

/**
 * Enumerates the default battle royale mode presentations.
 * <p>
 * @author AdrianSR / Sunday 16 May, 2021 / 04:17 PM
 */
public enum BattleRoyaleModeDefaultPresentation implements BattleRoyaleModePresentation {
	
	JAR_FILE ( "jar" ) {
		@Override
		public BattleRoyaleModeLoader createLoader ( ) {
			return new ComplexBattleRoyaleModeLoader ( );
		}
	},
	
	YAML_FILE ( "yml" ) {
		@Override
		public BattleRoyaleModeLoader createLoader ( ) {
			return new SimpleBattleRoyaleModeLoader ( );
		}
	},
	;
	
	public static BattleRoyaleModeDefaultPresentation of ( File file ) {
		for ( BattleRoyaleModeDefaultPresentation presentation : BattleRoyaleModeDefaultPresentation.values ( ) ) {
			if ( presentation.getFileFilter ( ).accept ( file ) ) {
				return presentation;
			}
		}
		return null;
	}
	
	private final String extension;
	
	private BattleRoyaleModeDefaultPresentation ( String extension ) {
		this.extension = extension;
	}
	
	public abstract BattleRoyaleModeLoader createLoader ( );
	
	@Override
	public String[] getExtensions ( ) {
		return new String[] { extension };
	}
}