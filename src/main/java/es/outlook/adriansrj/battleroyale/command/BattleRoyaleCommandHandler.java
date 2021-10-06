package es.outlook.adriansrj.battleroyale.command;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.command.CommandHandler;
import es.outlook.adriansrj.core.util.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Optional;

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
		
		// FIXME: test 'stop' argument
		registerArgument ( new BaseArgument ( this ) {
			
			@Override
			public String getName ( ) {
				return "stop";
			}
			
			@Override
			public boolean execute ( CommandSender sender , Command command , String label , String[] subargs ) {
				if ( subargs.length > 0 && StringUtil.isNotBlank ( subargs[ 0 ] ) ) {
					String arena_name = subargs[ 0 ];
					Optional < BattleRoyaleArena > optional = BattleRoyaleArenaHandler.getInstance ( )
							.getArena ( arena_name );
					
					if ( optional.isPresent ( ) ) {
						optional.get ( ).stop ( );
						
						sender.sendMessage ( ">>>>>> stopping....." );
					}
				}
				return true;
			}
		} );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}