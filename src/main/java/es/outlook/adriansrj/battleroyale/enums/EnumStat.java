package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.configuration.stat.StatRewardConfigHandler;

/**
 * Enumerates the different types of stats for the Battle royale game.
 *
 * @author AdrianSR / 15/09/2021 / 06:11 p. m.
 */
public enum EnumStat {
	
	KILLS ( 5 ),
	DEATHS ( 0 ),
	WINS ( 100 ),
	LOSSES ( 0 ),
	HEADSHOTS ( 8 ),
	KNOCKED_ENEMIES ( 2 ),
	TIMES_KNOCKED ( 0 ),
	;
	
	final int default_reward;
	
	EnumStat ( int default_reward ) {
		this.default_reward = default_reward;
	}
	
	/**
	 * Gets default reward coins/money/xp
	 * the players will receive when accomplishing
	 * this stat.
	 *
	 * @return default stat reward.
	 */
	public int getDefaultReward ( ) {
		return default_reward;
	}
	
	/**
	 * Gets how many reward coins/money/xp
	 * the players will receive when accomplishing
	 * this stat.
	 *
	 * @return stat reward.
	 */
	public int getReward ( ) {
		return StatRewardConfigHandler.getInstance ( ).getReward ( this );
	}
}