package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Button which redirects to the next page
 * when clicking on it.
 *
 * @author AdrianSR / 09/08/2021 / Time: 09:26 p. m.
 */
public class GUIButtonNextPage extends GUIButton {
	
	/**
	 * {@link GUIButtonNextPage} instance.
	 *
	 * @author AdrianSR / 30/09/2021 / 04:56 p. m.
	 */
	protected static class Instance extends GUIIconInstance {
		
		protected final GUIButtonNextPage configuration;
		
		public Instance ( GUIButtonNextPage configuration , GUIInstance gui , String name , ItemStack icon ,
				Collection < String > lore ) {
			super ( gui , name , icon , lore );
			this.configuration = configuration;
		}
		
		public Instance ( GUIButtonNextPage configuration , GUIInstance gui , String name , ItemStack icon ,
				String... lore ) {
			super ( gui , name , icon , lore );
			this.configuration = configuration;
		}
		
		public Instance ( GUIButtonNextPage configuration , GUIInstance gui , ItemStack icon ) {
			super ( gui , icon );
			this.configuration = configuration;
		}
		
		@Override
		public void onClick ( ItemClickAction action ) {
			// finding out current page
			ItemMenu[] indexed_pages = gui.getIndexedPages ( );
			int        page_index    = -1;
			
			outer:
			for ( int index = 0 ; index < indexed_pages.length ; index++ ) {
				ItemMenu page = indexed_pages[ index ];
				
				for ( int i = 0 ; i < page.getSize ( ).getSize ( ) ; i++ ) {
					if ( Objects.equals ( page.getItem ( i ) , this ) ) {
						page_index = index;
						break outer;
					}
				}
			}
			
			// then redirecting
			if ( page_index != -1 ) {
				gui.open ( action.getPlayer ( ) , page_index + 1 );
			}
		}
	}
	
	public static GUIButtonNextPage of ( ConfigurationSection section ) {
		return new GUIButtonNextPage ( ).load ( section );
	}
	
	public GUIButtonNextPage ( String name , Material material , int amount , int data ,
			List < String > description_format , String display_name_format ) {
		super ( name , GUIIconTypeDefault.NEXT_BUTTON , material , amount , data , description_format ,
				display_name_format );
	}
	
	public GUIButtonNextPage ( String name , Material material , int amount , int data ,
			String display_name_format , String... description_format ) {
		super ( name , GUIIconTypeDefault.NEXT_BUTTON , material , amount , data , display_name_format ,
				description_format );
	}
	
	public GUIButtonNextPage ( String name , Material material , int amount , int data ,
			String display_name_format ) {
		super ( name , GUIIconTypeDefault.NEXT_BUTTON , material , amount , data , display_name_format );
	}
	
	public GUIButtonNextPage ( String name , Material material , String display_name_format ) {
		super ( name , GUIIconTypeDefault.NEXT_BUTTON , material , 1 , 0 , display_name_format );
	}
	
	public GUIButtonNextPage ( ) {
		// to be loaded
		super ( GUIIconTypeDefault.NEXT_BUTTON );
	}
	
	@Override
	protected GUIIconInstance createInstance ( GUIInstance gui , Player player , String display_name ,
			ItemStack icon , List < String > description ) {
		return new Instance ( this , gui , display_name , icon , description );
	}
	
	@Override
	public GUIButtonNextPage load ( ConfigurationSection section ) {
		super.load ( section );
		return this;
	}
}