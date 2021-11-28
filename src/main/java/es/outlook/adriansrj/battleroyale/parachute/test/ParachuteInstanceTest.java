package es.outlook.adriansrj.battleroyale.parachute.test;

import org.apache.commons.lang3.Validate;

/**
 * BattleRoyale plugin parachute instance.
 *
 * @author AdrianSR / 09/09/2021 / 08:56 p. m.
 */
public abstract class ParachuteInstanceTest {
	
	protected final Parachute configuration;
	
	protected ParachuteInstanceTest ( Parachute configuration ) {
		Validate.notNull ( configuration , "configuration cannot be null" );
		Validate.isTrue ( configuration.isValid ( ) , "configuration must be valid" );
		
		this.configuration = configuration;
	}
	
	public Parachute getConfiguration ( ) {
		return configuration;
	}
	
	public abstract boolean isOpen ( );
	
	public abstract void open ( );
	
	public abstract void close ( );
}