package es.outlook.adriansrj.battleroyale.util.mode;

import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;

/**
 * Useful class for dealing with {@link BattleRoyaleMode}s.
 *
 * @author AdrianSR / 02/10/2021 / 11:55 a. m.
 */
public class BattleRoyaleModeUtil {
	
	/**
	 * Gets whether the provided mode is determined by kills.
	 * <br>
	 * In other words it returns whether the game ends
	 * when a certain number of kills is reached.
	 *
	 * @param mode the mode to check.
	 * @return whether the provided mode is determined by kills.
	 */
	public static boolean isDeterminedByKills ( BattleRoyaleMode mode ) {
		return mode.getMaxKills ( ) > 0;
	}
	
	/**
	 * Gets whether the provided mode has a limited number of slots
	 * for the teams.
	 *
	 * @param mode the mode to check.
	 * @return whether the mode limits the number of teams.
	 */
	public static boolean isLimitedTeams ( BattleRoyaleMode mode ) {
		return mode.getMaxTeams ( ) > 0;
	}
	
	/**
	 * Gets whether the provided mode has a limited number of players per team.
	 *
	 * @param mode the mode to check.
	 * @return whether the provided mode has a limited number of players per team.
	 */
	public static boolean isLimitedPlayersPerTeam ( BattleRoyaleMode mode ) {
		return mode.getMaxPlayersPerTeam ( ) > 0;
	}
	
	/**
	 * Gets whether the provided mode limits the number of player
	 * that can join the match.
	 *
	 * @param mode the mode to check.
	 * @return whether the provided mode limits the number of player that can join the match.
	 */
	public static boolean isLimitedPlayers ( BattleRoyaleMode mode ) {
		return mode.getMaxPlayers ( ) > 0;
	}
}