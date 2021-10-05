package es.outlook.adriansrj.battleroyale.configuration.gui.arenaselector;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.gui.*;
import es.outlook.adriansrj.battleroyale.gui.arena.ArenaSelectorGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.arena.icon.ArenaSelectorGUIButtonArena;
import es.outlook.adriansrj.battleroyale.gui.arena.icon.ArenaSelectorGUIButtonLeaveArena;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.itemstack.wool.WoolColor;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author AdrianSR / 03/10/2021 / 10:15 a. m.
 */
public final class ArenaSelectorGUIConfigHandler extends ConfigurationHandler {
	
	public static final GUIConfiguration DEFAULT_TEAM_GUI_CONFIGURATION;
	
	static {
		List < GUIIcon >      icons           = new ArrayList <> ( );
		List < GUIPage >      pages           = new ArrayList <> ( );
		List < GUIPageModel > models          = new ArrayList <> ( );
		GUIPageModel          page_model_base = null;
		
		/* icons */
		icons.add ( new ArenaSelectorGUIButtonArena (
				"button-arena" ,
				UniversalMaterial.BOW.getMaterial ( ) ,
				1 , 0 ,
				ChatColor.GOLD + "%s" ,
				
				Arrays.asList ( "" , ChatColor.GREEN + "Click to Join" ,
								"" , ChatColor.DARK_GRAY + "%br_arena_count%/%br_arena_limit%" ) ,
				
				Arrays.asList ( "" , ChatColor.DARK_RED + "In Game" , "" ) ,
				Arrays.asList ( "" , ChatColor.DARK_RED + "Full" ,
								"" , ChatColor.DARK_RED + "%br_arena_count%/%br_arena_limit%" ) ,
				Arrays.asList ( "" , ChatColor.DARK_RED + "Restarting..." , "" ) ,
				Arrays.asList ( "" , ChatColor.DARK_RED + "Unavailable" , "" )
		) );
		
		icons.add ( new ArenaSelectorGUIButtonLeaveArena (
				"leave-arena" ,
				UniversalMaterial.BLACK_WOOL.getMaterial ( ) ,
				1 ,
				WoolColor.BLACK.getShortValue ( ) ,
				ChatColor.RED + "Leave Arena"
		) );
		
		icons.add ( new GUIButtonClose (
				"button-close" , UniversalMaterial.BARRIER.getMaterial ( ) ,
				1 , 0 ,
				ChatColor.DARK_RED + "Close" ) );
		
		/* pages */
		pages.add ( new GUIPage (
				"arenas" , 0 , ChatColor.BLACK + "Arenas" , GUIPageModel.PAGE_MODEL_BASE_NAME ) );
		
		/* page models */
		page_model_base = new GUIPageModel (
				GUIPageModel.PAGE_MODEL_BASE_NAME , ItemMenuSize.THREE_LINE , new GUIPageModelContent (
				// base
				new GUIPageModelSlotBase ( "button-arena" ) ,
				// leave button
				new GUIPageModelSlot ( 18 , "leave-arena" ) ,
				// close button
				new GUIPageModelSlot ( 26 , "button-close" )
		) );
		
		DEFAULT_TEAM_GUI_CONFIGURATION = new GUIConfiguration (
				icons , pages , page_model_base , models );
	}
	
	public static ArenaSelectorGUIConfigHandler getInstance ( ) {
		return getPluginHandler ( ArenaSelectorGUIConfigHandler.class );
	}
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public ArenaSelectorGUIConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public void initialize ( ) {
		File file = EnumFile.ARENA_SELECTOR_GUI.getFile ( );
		
		if ( !file.exists ( ) ) {
			// saving default configuration
			try {
				file.getParentFile ( ).mkdirs ( );
				file.createNewFile ( );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
			
			YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( file );
			
			if ( DEFAULT_TEAM_GUI_CONFIGURATION.save ( yaml ) > 0 ) {
				try {
					yaml.save ( file );
				} catch ( IOException e ) {
					e.printStackTrace ( );
				}
			}
		}
		
		// then loading
		loadConfiguration ( );
	}
	
	@Override
	public void loadConfiguration ( ) {
		File                      file          = EnumFile.ARENA_SELECTOR_GUI.getFile ( );
		YamlConfigurationComments yaml          = YamlConfigurationComments.loadConfiguration ( file );
		GUIConfiguration          configuration = GUIConfiguration.of ( yaml );
		
		if ( configuration.isValid ( ) ) {
			ArenaSelectorGUIHandler.getInstance ( ).setConfiguration ( configuration );
		} else {
			ConsoleUtil.sendPluginMessage (
					ChatColor.RED , "The configuration of the file "
							+ file.getName ( ) + " seems to be invalid!" ,
					BattleRoyale.getInstance ( ) );
		}
	}
	
	@Override
	public void save ( ) {
		// nothing to do here
	}
}