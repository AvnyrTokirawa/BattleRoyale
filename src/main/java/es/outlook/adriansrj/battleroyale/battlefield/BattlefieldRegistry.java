package es.outlook.adriansrj.battleroyale.battlefield;

import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.file.filter.FileDirectoryFilter;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Battlefield registry.
 *
 * @author AdrianSR / 03/09/2021 / 04:47 p. m.
 */
public final class BattlefieldRegistry extends PluginHandler {
	
	public static BattlefieldRegistry getInstance ( ) {
		return getPluginHandler ( BattlefieldRegistry.class );
	}
	
	private final Map < String, Battlefield > battlefields = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BattlefieldRegistry ( BattleRoyale plugin ) {
		super ( plugin );
		
		// loading battlefields
		outer:
		for ( File folder : Objects.requireNonNull (
				EnumDirectory.BATTLEFIELD_DIRECTORY.getDirectory ( ).listFiles ( new FileDirectoryFilter ( ) ) ) ) {
			for ( EnumDirectory other : EnumDirectory.values ( ) ) {
				if ( other.getDirectory ( ).equals ( folder ) ) {
					continue outer;
				}
			}
			
			try {
				Battlefield battlefield = new Battlefield ( folder );
				
				if ( battlefield.getConfiguration ( ).isValid ( ) ) {
					battlefields.put ( battlefield.getName ( ).trim ( ).toLowerCase ( ) , battlefield );
					
					ConsoleUtil.sendPluginMessage (
							ChatColor.GREEN , "Battlefield '" + folder.getName ( )
									+ "' successfully loaded." , BattleRoyale.getInstance ( ) );
				} else {
					ConsoleUtil.sendPluginMessage (
							ChatColor.RED , "It seems that the configuration of the battlefield '"
									+ folder.getName ( ) + "' is not valid!" , BattleRoyale.getInstance ( ) );
				}
			} catch ( IOException ex ) {
				ConsoleUtil.sendPluginMessage (
						ChatColor.RED , "It seems that the battlefield '" + folder.getName ( )
								+ "' is not valid: " , BattleRoyale.getInstance ( ) );
				
				ex.printStackTrace ( );
			}
		}
	}
	
	public Collection < Battlefield > getBattlefields ( ) {
		return Collections.unmodifiableCollection ( battlefields.values ( ) );
	}
	
	public Battlefield getBattlefield ( String name ) {
		return battlefields.get ( name.toLowerCase ( ).trim ( ) );
	}
	
	public boolean hasBattlefield ( String name ) {
		return getBattlefield ( name ) != null;
	}
	
	public void registerBattlefield ( String name , Battlefield battlefield ) {
		Validate.isTrue ( getBattlefield ( name ) == null ,
						  "another battlefield with the same name already exists" );
		
		this.battlefields.put ( name.toLowerCase ( ).trim ( ) , battlefield );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}