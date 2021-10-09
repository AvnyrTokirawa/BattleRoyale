package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

/**
 * Button which closes the GUI when clicking on it.
 *
 * @author AdrianSR / 09/08/2021 / Time: 09:26 p. m.
 */
public class GUIButtonClose extends GUIButton {
	
	/**
	 * {@link GUIButtonClose} instance.
	 *
	 * @author AdrianSR / 30/09/2021 / 04:56 p. m.
	 */
	protected static class Instance extends GUIIconInstance {
		
		protected final GUIButtonClose configuration;
		
		public Instance ( GUIButtonClose configuration , GUIInstance gui , String name , ItemStack icon ,
				Collection < String > lore ) {
			super ( gui , name , icon , lore );
			this.configuration = configuration;
		}
		
		public Instance ( GUIButtonClose configuration , GUIInstance gui , String name , ItemStack icon ,
				String... lore ) {
			super ( gui , name , icon , lore );
			this.configuration = configuration;
		}
		
		public Instance ( GUIButtonClose configuration , GUIInstance gui , ItemStack icon ) {
			super ( gui , icon );
			this.configuration = configuration;
		}
		
		@Override
		public void onClick ( ItemClickAction action ) {
			action.setClose ( true );
		}
	}
	
	public static GUIButtonClose of ( ConfigurationSection section ) {
		return new GUIButtonClose ( ).load ( section );
	}
	
	public GUIButtonClose ( String name , Material material , int amount , int data ,
			List < String > description_format , String display_name_format ) {
		super ( name , GUIIconTypeDefault.CLOSE_BUTTON , material , amount , data , description_format ,
				display_name_format );
	}
	
	public GUIButtonClose ( String name , Material material , int amount , int data ,
			String display_name_format , String... description_format ) {
		super ( name , GUIIconTypeDefault.CLOSE_BUTTON , material , amount , data , display_name_format ,
				description_format );
	}
	
	public GUIButtonClose ( String name , Material material , int amount , int data , String display_name_format ) {
		super ( name , GUIIconTypeDefault.CLOSE_BUTTON , material , amount , data , display_name_format );
	}
	
	public GUIButtonClose ( String name , Material material , String display_name_format ) {
		super ( name , GUIIconTypeDefault.CLOSE_BUTTON , material , 1 , 0 , display_name_format );
	}
	
	public GUIButtonClose ( ) {
		// to be loaded
		super ( GUIIconTypeDefault.CLOSE_BUTTON );
	}
	
	@Override
	protected GUIIconInstance createInstance ( GUIInstance gui , Player player , String display_name ,
			ItemStack icon , List < String > description ) {
		return new Instance ( this , gui , display_name , icon , description );
	}
	
	@Override
	public GUIButtonClose load ( ConfigurationSection section ) {
		super.load ( section );
		return this;
	}
}