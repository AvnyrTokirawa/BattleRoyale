package es.outlook.adriansrj.battleroyale.game.mode;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.Validable;

/**
 * Represents a battle royale modality that determines the behavior of the game.
 * <p>
 * @author AdrianSR / Sunday 16 May, 2021 / 03:43 PM
 */
public abstract class BattleRoyaleMode implements Validable {
	
	/**
	 * Ask for the initialization of this battle mode.
	 * <br>
	 * @return true if successfully initialized.
	 */
	protected abstract boolean initialize ( );
	
	/**
	 * Introduces the specified {@link Player} into the battle.
	 * <br>
	 * Note that <b>false</b> will be returned if the player is
	 * introduced as <strong>spectator.</strong>
	 * <br>
	 * @param player the player to introduce.
	 * @return whether conditions to be introduced are met or not.
	 */
	public abstract boolean introduce ( Player player );
	
	/**
	 * Called whenever the plugin is going to end and arena.
	 * <br>
	 * Note that <b>false</b> will be returned if the arena
	 * should not end.
	 * <br>
	 * @return whether conditions to end the arena are met or not.
	 */
	public abstract boolean end ( BattleRoyaleArena arena );
	
	/**
	 * Gets the initial health (health players will have when the game starts).
	 * <p>
	 * @return health players will have when the game starts.
	 */
	public abstract double getInitialHealth ( );
	
	/**
	 * Gets the maximum health players can have.
	 * <p>
	 * @return the maximum health players can have.
	 */
	public abstract double getMaxHealth ( );
	
	/**
	 * Gets the maximum kills for this modality, which means that the game will
	 * end when it reaches this number of kills.
	 * <br>
	 * <b>The winner is the player with the highest number of kills.</b>
	 * <p>
	 * @return maximum kills.
	 */
	public abstract int getMaxKills ( );
	
	/**
	 * Gets the maximum number of players allowed to join the match/arena.
	 * <p>
	 * @return the maximum number of players allowed to join the match/arena.
	 */
	public abstract int getMaxPlayers ( );
	
	/**
	 * Gets the maximum numbers of teams allowed for this mode.
	 * <p>
	 * @return the maximum numbers of teams allowed for this mode.
	 */
	public abstract int getMaxTeams ( );
	
	/**
	 * Gets the maximum numbers of players per team allowed for this mode.
	 * <p>
	 * @return the maximum numbers of players per team allowed for this mode.
	 */
	public abstract int getMaxPlayersPerTeam ( );
	
	/**
	 * Gets whether the team creation is enabled.
	 * <br>
	 * If the team creation is disabled, players will not able
	 * to create teams; instead, they will have to select from
	 * the existing teams.
	 * <br>
	 * These existing teams will be automatically created by
	 * the plugin based on the team limit ({@link #getMaxTeams()}).
	 *
	 * @return whether the team creation is enabled.
	 */
	public abstract boolean isTeamCreationEnabled ( );
	
	/**
	 * Gets whether the team selection is enabled.
	 * <br>
	 * If the team selection is disabled, players will not able
	 * to select a team; instead, they will be automatically set
	 * in a team by the plugin.
	 * <br>
	 * {@link #getMaxTeams()} and {@link #isAutoFillEnabled()}
	 * will have no effect if this option is disabled.
	 *
	 * @return whether the team selection is enabled.
	 */
	public abstract boolean isTeamSelectionEnabled ( );
	
	/**
	 * Gets whether team auto-fill is enabled or not.
	 * <br>
	 * @return whether team auto-fill is enabled or not.
	 */
	public abstract boolean isAutoFillEnabled ( );
	
	/**
	 * Gets whether there will be only single player teams or not.
	 * <p>
	 * @return whether there will be only single player teams or not.
	 */
	public boolean isSolo ( ) {
		return getMaxPlayersPerTeam ( ) <= 1;
	}
	
	/**
	 * Gets whether teammates reanimation is enabled.
	 * <p>
	 * @return whether teammates reanimation is enabled or not.
	 */
	public abstract boolean isRevivingEnabled ( );
	
	/**
	 * Gets how long it will take to revive a teammate.
	 * <p>
	 * @return how long it will take to revive a teammate.
	 */
	public abstract Duration getRevivingTime ( );
	
	/**
	 * Gets the health for a teammate after getting revived.
	 * <p>
	 * @return the health for a teammate after getting revived.
	 */
	public abstract double getHealthAfterReviving ( );
	
	/**
	 * Gets whether respawn is enabled, which means that players respawn
	 * automatically after dying or not.
	 * <p>
	 * @return whether respawn is enabled, which means that players
	 *         respawn automatically after dying or not.
	 */
	public abstract boolean isRespawnEnabled ( );
	
	/**
	 * Gets how long it will take to respawn a player.
	 * <p>
	 * @return how long it will take to respawn a player.
	 */
	public abstract Duration getRespawnTime ( );
	
	/**
	 * Gets whether to respawn the player in the air.
	 *
	 * @return whether to respawn the player in the air.
	 */
	public abstract boolean isRespawnInAir ( );
	
	/**
	 * Gets whether the parachute redeploy is enabled or not.
	 * <p>
	 * @return whether the parachute redeploy is enabled or not.
	 */
	public abstract boolean isRedeployEnabled ( );
}