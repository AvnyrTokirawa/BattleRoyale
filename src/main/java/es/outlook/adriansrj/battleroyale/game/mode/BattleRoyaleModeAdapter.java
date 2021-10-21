package es.outlook.adriansrj.battleroyale.game.mode;

import es.outlook.adriansrj.battleroyale.game.player.Player;

/**
 * Adapted implementation of {@link BattleRoyaleMode}.
 * <p>
 * @author AdrianSR / Sunday 16 May, 2021 / 04:44 PM
 */
public abstract class BattleRoyaleModeAdapter extends BattleRoyaleMode {
	
	@Override
	public boolean introduce ( Player player ) {
		// nothing to do by default
		return true;
	}
	
	@Override
	public boolean isTeamCreationEnabled ( ) {
		return true;
	}
	
	@Override
	public boolean isTeamSelectionEnabled ( ) {
		return true;
	}
}