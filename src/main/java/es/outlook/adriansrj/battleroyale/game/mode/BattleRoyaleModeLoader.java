package es.outlook.adriansrj.battleroyale.game.mode;

import java.io.File;

/**
 * Represents a {@link BattleRoyaleMode} loader, which loads battle modes in a certain
 * {@link BattleRoyaleModePresentation}.
 * <p>
 * @author AdrianSR / Sunday 16 May, 2021 / 04:17 PM
 */
public abstract class BattleRoyaleModeLoader {
	
	public abstract BattleRoyaleModePresentation getPresentation ( );
	
	public abstract BattleRoyaleMode load ( File file ) throws IllegalArgumentException;
}