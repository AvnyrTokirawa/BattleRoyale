package es.outlook.adriansrj.battleroyale.gui.arena.icon;

import es.outlook.adriansrj.battleroyale.gui.GUIButton;
import es.outlook.adriansrj.battleroyale.gui.GUIInstance;
import es.outlook.adriansrj.battleroyale.gui.arena.ArenaSelectorGUIIconType;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ArenaSelectorGUIButtonArenaInstance} configuration.
 *
 * @author AdrianSR / 03/10/2021 / 11:12 a. m.
 */
public class ArenaSelectorGUIButtonArena extends GUIButton {
	
	protected static final String DESCRIPTION_FORMAT_RUNNING_KEY    = "description-format-running";
	protected static final String DESCRIPTION_FORMAT_FULL_KEY       = "description-format-full";
	protected static final String DESCRIPTION_FORMAT_RESTARTING_KEY = "description-format-restarting";
	protected static final String DESCRIPTION_FORMAT_STOPPED_KEY    = "description-format-stopped";
	
	public static ArenaSelectorGUIButtonArena of ( ConfigurationSection section ) {
		return new ArenaSelectorGUIButtonArena ( ).load ( section );
	}
	
	protected final List < String > description_format_running    = new ArrayList <> ( );
	protected final List < String > description_format_full       = new ArrayList <> ( );
	protected final List < String > description_format_restarting = new ArrayList <> ( );
	protected final List < String > description_format_stopped    = new ArrayList <> ( );
	
	public ArenaSelectorGUIButtonArena ( String name ,
			Material material , int amount , int data ,
			String display_name_format ,
			List < String > description_format ,
			List < String > description_format_running ,
			List < String > description_format_full ,
			List < String > description_format_restarting ,
			List < String > description_format_stopped ) {
		super ( name , ArenaSelectorGUIIconType.ARENA , material , amount , data , description_format ,
				display_name_format );
		
		this.description_format_running.addAll ( StringUtil.untranslateAlternateColorCodes ( description_format_running ) );
		this.description_format_full.addAll ( StringUtil.untranslateAlternateColorCodes ( description_format_full ) );
		this.description_format_restarting.addAll ( StringUtil.untranslateAlternateColorCodes ( description_format_restarting ) );
		this.description_format_stopped.addAll ( StringUtil.untranslateAlternateColorCodes ( description_format_stopped ) );
	}
	
	public ArenaSelectorGUIButtonArena ( String name ,
			Material material , int amount , int data ,
			String display_name_format ,
			List < String > description_format ) {
		super ( name , ArenaSelectorGUIIconType.ARENA , material , amount , data , description_format ,
				display_name_format );
	}
	
	public ArenaSelectorGUIButtonArena ( String name ,
			Material material , int amount , int data , String display_name_format ,
			String... description_format ) {
		super ( name , ArenaSelectorGUIIconType.ARENA , material , amount , data , display_name_format ,
				description_format );
	}
	
	public ArenaSelectorGUIButtonArena ( String name ,
			Material material , int amount , int data , String display_name_format ) {
		super ( name , ArenaSelectorGUIIconType.ARENA , material , amount , data , display_name_format );
	}
	
	public ArenaSelectorGUIButtonArena ( ) {
		// to be loaded
		super ( ArenaSelectorGUIIconType.ARENA );
	}
	
	public List < String > getDescriptionFormatFull ( ) {
		return description_format_full;
	}
	
	public List < String > getDescriptionFormatRestarting ( ) {
		return description_format_restarting;
	}
	
	public List < String > getDescriptionFormatRunning ( ) {
		return description_format_running;
	}
	
	public List < String > getDescriptionFormatStopped ( ) {
		return description_format_stopped;
	}
	
	@Override
	protected ArenaSelectorGUIButtonArenaInstance createInstance ( GUIInstance gui , Player player ,
			String display_name , ItemStack icon , List < String > description ) {
		return new ArenaSelectorGUIButtonArenaInstance ( this , gui , display_name , icon , description );
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = super.save ( section );
		
		// saving description formats
		save += YamlUtil.setNotEqual (
				section , DESCRIPTION_FORMAT_RUNNING_KEY , description_format_running ) ? 1 : 0;
		save += YamlUtil.setNotEqual (
				section , DESCRIPTION_FORMAT_FULL_KEY , description_format_full ) ? 1 : 0;
		save += YamlUtil.setNotEqual (
				section , DESCRIPTION_FORMAT_RESTARTING_KEY , description_format_restarting ) ? 1 : 0;
		save += YamlUtil.setNotEqual (
				section , DESCRIPTION_FORMAT_STOPPED_KEY , description_format_stopped ) ? 1 : 0;
		
		return save;
	}
	
	@Override
	public ArenaSelectorGUIButtonArena load ( ConfigurationSection section ) {
		super.load ( section );
		
		// loading description formats
		this.description_format_running.clear ( );
		this.description_format_full.clear ( );
		this.description_format_restarting.clear ( );
		this.description_format_stopped.clear ( );
		
		this.description_format_running.addAll (
				StringUtil.translateAlternateColorCodes ( section.getStringList ( DESCRIPTION_FORMAT_RUNNING_KEY ) ) );
		this.description_format_full.addAll (
				StringUtil.translateAlternateColorCodes ( section.getStringList ( DESCRIPTION_FORMAT_FULL_KEY ) ) );
		this.description_format_restarting.addAll (
				StringUtil.translateAlternateColorCodes ( section.getStringList ( DESCRIPTION_FORMAT_RESTARTING_KEY ) ) );
		this.description_format_stopped.addAll (
				StringUtil.translateAlternateColorCodes ( section.getStringList ( DESCRIPTION_FORMAT_STOPPED_KEY ) ) );
		return this;
	}
}