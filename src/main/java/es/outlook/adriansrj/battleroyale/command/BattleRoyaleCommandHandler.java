package es.outlook.adriansrj.battleroyale.command;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.command.CommandHandler;

/**
 * @author AdrianSR / 28/08/2021 / 12:23 p. m.
 */
public final class BattleRoyaleCommandHandler extends CommandHandler {
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BattleRoyaleCommandHandler ( BattleRoyale plugin ) {
		super ( plugin , "battleroyale" );
		
		// 'setup' argument
		registerArgument ( new SetupArgument ( this ) );
		// 'start' argument
		registerArgument ( new StartArgument ( this ) );
		// 'restart' argument
		registerArgument ( new RestartArgument ( this ) );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}