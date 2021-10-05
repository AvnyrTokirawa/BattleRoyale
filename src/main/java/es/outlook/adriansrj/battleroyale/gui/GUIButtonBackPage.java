package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

/**
 * Button which redirects to the previous page
 * when clicking on it.
 *
 * @author AdrianSR / 09/08/2021 / Time: 09:26 p. m.
 */
public class GUIButtonBackPage extends GUIButton {
	
	/**
	 * {@link GUIButtonBackPage} instance.
	 *
	 * @author AdrianSR / 30/09/2021 / 04:56 p. m.
	 */
	protected static class Instance extends GUIIconInstance {
		
		protected final GUIButtonBackPage configuration;
		
		public Instance ( GUIButtonBackPage configuration , GUIInstance gui , String name , ItemStack icon ,
				Collection < String > lore ) {
			super ( gui , name , icon , lore );
			this.configuration = configuration;
		}
		
		public Instance ( GUIButtonBackPage configuration , GUIInstance gui , String name , ItemStack icon ,
				String... lore ) {
			super ( gui , name , icon , lore );
			this.configuration = configuration;
		}
		
		public Instance ( GUIButtonBackPage configuration , GUIInstance gui , ItemStack icon ) {
			super ( gui , icon );
			this.configuration = configuration;
		}
		
		@Override
		public void onClick ( ItemClickAction action ) {
			action.setGoBack ( true );
		}
	}
	
	public static GUIButtonBackPage of ( ConfigurationSection section ) {
		return new GUIButtonBackPage ( ).load ( section );
	}
	
	public GUIButtonBackPage ( String name , Material material , int amount , int data ,
			List < String > description_format , String display_name_format ) {
		super ( name , GUIIconTypeDefault.NEXT_BUTTON , material , amount , data , description_format ,
				display_name_format );
	}
	
	public GUIButtonBackPage ( String name , Material material , int amount , int data ,
			String display_name_format , String... description_format ) {
		super ( name , GUIIconTypeDefault.NEXT_BUTTON , material , amount , data , display_name_format ,
				description_format );
	}
	
	public GUIButtonBackPage ( String name , Material material , int amount , int data ,
			String display_name_format ) {
		super ( name , GUIIconTypeDefault.NEXT_BUTTON , material , amount , data , display_name_format );
	}
	
	public GUIButtonBackPage ( String name , Material material , String display_name_format ) {
		super ( name , GUIIconTypeDefault.NEXT_BUTTON , material , 1 , 0 , display_name_format );
	}
	
	public GUIButtonBackPage ( ) {
		// to be loaded
		super ( GUIIconTypeDefault.NEXT_BUTTON );
	}
	
	@Override
	protected GUIIconInstance createInstance ( GUIInstance gui , Player player , String display_name ,
			ItemStack icon , List < String > description ) {
		return new Instance ( this , gui , display_name , icon , description );
	}
	
	@Override
	public GUIButtonBackPage load ( ConfigurationSection section ) {
		super.load ( section );
		return this;
	}
}