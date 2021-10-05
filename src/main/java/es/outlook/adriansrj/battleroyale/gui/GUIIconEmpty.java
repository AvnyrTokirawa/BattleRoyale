package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.util.StringUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * An icon for the Team GUI which displays nothing.
 *
 * @author AdrianSR / 09/08/2021 / Time: 09:25 p. m.
 */
public class GUIIconEmpty extends GUIIcon {
	
	public static GUIIconEmpty of ( ConfigurationSection section ) {
		return new GUIIconEmpty ( ).load ( section );
	}
	
	public GUIIconEmpty ( String name ) {
		super ( name , GUIIconTypeDefault.EMPTY , StringUtil.EMPTY );
	}
	
	public GUIIconEmpty ( ) {
		super ( StringUtil.EMPTY , GUIIconTypeDefault.EMPTY , StringUtil.EMPTY );
	}
	
	@Override
	protected GUIIconInstance createInstance ( GUIInstance gui , Player player , String display_name ,
			List < String > description ) {
		return null; // empty, literally
	}
	
	@Override
	public GUIIconEmpty load ( ConfigurationSection section ) {
		super.load ( section );
		return this;
	}
}