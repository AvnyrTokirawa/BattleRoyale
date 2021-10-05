package es.outlook.adriansrj.battleroyale.gui.arena.icon;

import es.outlook.adriansrj.battleroyale.gui.GUIButton;
import es.outlook.adriansrj.battleroyale.gui.GUIInstance;
import es.outlook.adriansrj.battleroyale.gui.arena.ArenaSelectorGUIIconType;
import es.outlook.adriansrj.battleroyale.player.Player;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * {@link ArenaSelectorGUIButtonLeaveArenaInstance} configuration.
 *
 * @author AdrianSR / 03/10/2021 / 11:12 a. m.
 */
public class ArenaSelectorGUIButtonLeaveArena extends GUIButton {
	
	public static ArenaSelectorGUIButtonLeaveArena of ( ConfigurationSection section ) {
		return new ArenaSelectorGUIButtonLeaveArena ( ).load ( section );
	}
	
	public ArenaSelectorGUIButtonLeaveArena ( String name ,
			Material material , int amount , int data ,
			String display_name_format ,
			List < String > description_format ) {
		super ( name , ArenaSelectorGUIIconType.LEAVE_ARENA , material , amount , data , description_format ,
				display_name_format );
	}
	
	public ArenaSelectorGUIButtonLeaveArena ( String name ,
			Material material , int amount , int data , String display_name_format ,
			String... description_format ) {
		super ( name , ArenaSelectorGUIIconType.LEAVE_ARENA , material , amount , data , display_name_format ,
				description_format );
	}
	
	public ArenaSelectorGUIButtonLeaveArena ( String name ,
			Material material , int amount , int data , String display_name_format ) {
		super ( name , ArenaSelectorGUIIconType.LEAVE_ARENA , material , amount , data , display_name_format );
	}
	
	public ArenaSelectorGUIButtonLeaveArena ( ) {
		// to be loaded
		super ( ArenaSelectorGUIIconType.LEAVE_ARENA );
	}
	
	@Override
	protected ArenaSelectorGUIButtonLeaveArenaInstance createInstance ( GUIInstance gui , Player player ,
			String display_name , ItemStack icon , List < String > description ) {
		return new ArenaSelectorGUIButtonLeaveArenaInstance ( this , gui , display_name , icon , description );
	}
	
	@Override
	public ArenaSelectorGUIButtonLeaveArena load ( ConfigurationSection section ) {
		super.load ( section );
		return this;
	}
}