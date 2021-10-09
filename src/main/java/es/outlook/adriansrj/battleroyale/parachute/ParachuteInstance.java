package es.outlook.adriansrj.battleroyale.parachute;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import org.apache.commons.lang3.Validate;

/**
 * BattleRoyale plugin parachute instance.
 *
 * @author AdrianSR / 09/09/2021 / 08:56 p. m.
 */
public abstract class ParachuteInstance {
	
	protected final Player    player;
	protected final Parachute configuration;
	
	protected ParachuteInstance ( Player player , Parachute configuration ) {
		Validate.notNull ( player , "player cannot be null" );
		Validate.notNull ( configuration , "configuration cannot be null" );
		Validate.isTrue ( configuration.isValid ( ) , "configuration must be valid" );
		
		this.player        = player;
		this.configuration = configuration;
	}
	
	/**
	 * Gets the {@link Player} who owns this parachute.
	 *
	 * @return the {@link Player} who owns this parachute.
	 */
	public Player getPlayer ( ) {
		return player;
	}
	
	public Parachute getConfiguration ( ) {
		return configuration;
	}
	
	public abstract boolean isOpen ( );
	
	public abstract void open ( );
	
	public abstract void close ( );
}