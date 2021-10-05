package es.outlook.adriansrj.battleroyale.game.mode.complex;

import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleModeAdapter;

import java.io.File;

/**
 * Defines a class that enables the development of custom {@link BattleRoyaleMode}s.
 * <p>
 * @author AdrianSR / Sunday 16 May, 2021 / 04:48 PM
 */
public abstract class ComplexBattleRoyaleMode extends BattleRoyaleModeAdapter {

	ComplexBattleRoyaleModeDescription description;
	File                               file;

	/**
	 * Gets whether the conditions to start the game are met or not.
	 * <p>
	 * @return whether the conditions to start the game are met or not.
	 */
	public abstract boolean canStart ( );
	
	/**
	 * Gets the description of this battle mode.
	 * <p>
	 * @return the description of this battle mode.
	 */
	public final ComplexBattleRoyaleModeDescription getDescription ( ) {
		return description;
	}
	
	/**
	 * Gets the file from which this mode was loaded.
	 * <br>
	 * @return the file from which this mode was loaded.
	 */
	public final File getFile ( ) {
		return file;
	}

	@Override
	public final boolean isSolo ( ) {
		return getMaxPlayersPerTeam ( ) <= 1;
	}
}