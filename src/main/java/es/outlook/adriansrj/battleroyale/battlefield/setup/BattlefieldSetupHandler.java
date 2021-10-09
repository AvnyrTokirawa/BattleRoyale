package es.outlook.adriansrj.battleroyale.battlefield.setup;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import es.outlook.adriansrj.battleroyale.battlefield.Battlefield;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.handler.PluginHandler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Class responsible for handling the setup battlefield.
 *
 * @author AdrianSR / 27/08/2021 / Time: 09:32 p. m.
 */
public final class BattlefieldSetupHandler extends PluginHandler {
	
	public static BattlefieldSetupHandler getInstance ( ) {
		return PluginHandler.getPluginHandler ( BattlefieldSetupHandler.class );
	}
	
	protected final Map < UUID, BattlefieldSetupSession > session_map = new ConcurrentHashMap <> ( );
	protected final Set < Player >                        locked      = new HashSet <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BattlefieldSetupHandler ( BattleRoyale plugin ) {
		super ( plugin );
		register ( );
	}
	
	public List < BattlefieldSetupSession > getActiveSessions ( ) {
		return session_map.values ( ).stream ( ).filter ( BattlefieldSetupSession :: isActive ).collect (
				Collectors.toList ( ) );
	}
	
	public BattlefieldSetupSession startSession ( Player configurator , File input_world_folder ) {
		try {
			return startSession ( BattlefieldSetupSession.newSetupSession ( configurator , input_world_folder ) );
		} catch ( IOException ex ) {
			throw new IllegalArgumentException ( "couldn't start the session: " , ex );
		}
	}
	
	public void startSession ( final Player configurator , Battlefield input ,
			final Consumer < BattlefieldSetupSession > callback )  {
		if ( locked.add ( configurator ) ) {
			BattlefieldSetupSession.newSetupSession (
					configurator , input , new Consumer < BattlefieldSetupSession > ( ) {
				@Override
				public void accept ( BattlefieldSetupSession session ) {
					locked.remove ( configurator );
					
					startSession ( session );
					
					callback.accept ( session );
				}
			} );
		} else {
			throw new IllegalStateException ( "another session is being started" );
		}
	}
	
	/**
	 * @param configurator
	 * @param input
	 * @param name name for the new battlefield.
	 * @param callback
	 *
	 * @throws IllegalStateException if called when another session with the same configurator is being started.
	 */
	public void startSession ( final Player configurator , Clipboard input , String name ,
			final Consumer < BattlefieldSetupSession > callback ) throws IllegalStateException {
		if ( locked.add ( configurator ) ) {
			BattlefieldSetupSession.newSetupSession ( configurator , input  , name, new Consumer < BattlefieldSetupSession > ( ) {
				@Override
				public void accept ( BattlefieldSetupSession session ) {
					locked.remove ( configurator );
					
					startSession ( session );
					
					callback.accept ( session );
				}
			} );
		} else {
			throw new IllegalStateException ( "another session is being started" );
		}
	}
	
	protected BattlefieldSetupSession startSession ( BattlefieldSetupSession session ) {
		// closing current session if any.
		getSession ( session.getOwner ( ) ).ifPresent ( current -> closeSession ( current ) );
		
		// then registering the new one
		session_map.put ( session.getOwner ( ).getUniqueId ( ) , session );
		
		return session;
	}
	
	public boolean invite ( Player br_player , BattlefieldSetupSession session ) {
		if ( Objects.equals ( br_player , session.getOwner ( ) )
				|| isInActiveSession ( br_player ) ) {
			return false;
		}
		
		// will only be successfully invited if is online
		org.bukkit.entity.Player player = br_player.getBukkitPlayer ( );
		
		if ( player != null && session.getGuestList ( ).add ( br_player ) ) {
			session.introduce ( player );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns whether the provided player is in an active session or not.
	 *
	 * @param player the player to check.
	 *
	 * @return whether the provided player is in an active session or not.
	 */
	public boolean isInActiveSession ( Player player ) {
		return getSession ( player ).isPresent ( ) || session_map.values ( ).stream ( )
				.anyMatch ( session -> session.getGuestList ( ).contains ( player ) );
	}
	
	/**
	 * Returns the session the provided configurator is invited to.
	 *
	 * @param invited the invited configurator.
	 *
	 * @return session the provided configurator is invited to <b>if any</b>.
	 */
	public Optional < BattlefieldSetupSession > getSessionFromInvited ( Player invited ) {
		return session_map.values ( ).stream ( ).filter ( session -> session.getGuestList ( ).contains ( invited ) )
				.findAny ( );
	}
	
	/**
	 * Gets the session that was started by the provided configurator, <b>if any</b>.
	 *
	 * @param owner the player who started the session.
	 *
	 * @return the session that was started by the provided configurator, <b>if any</b>.
	 */
	public Optional < BattlefieldSetupSession > getSession ( Player owner ) {
		BattlefieldSetupSession session = session_map.get ( owner.getUniqueId ( ) );
		
		// will return null instead of closed sessions.
		if ( session != null && !session.isActive ( ) ) {
			session_map.remove ( owner.getUniqueId ( ) );
			return Optional.empty ( );
		}
		
		return Optional.ofNullable ( session );
	}
	
	public void closeSession ( BattlefieldSetupSession session ) {
		session_map.remove ( session.getOwner ( ).getUniqueId ( ) );
		session.close ( );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
