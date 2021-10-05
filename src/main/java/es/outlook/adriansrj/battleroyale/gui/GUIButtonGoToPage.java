package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

/**
 * Button which redirects to a page specified by its name or its index.
 *
 * @author AdrianSR / 09/08/2021 / Time: 09:26 p. m.
 */
public class GUIButtonGoToPage extends GUIButton {
	
	/**
	 * {@link GUIButtonGoToPage} instance.
	 *
	 * @author AdrianSR / 30/09/2021 / 04:56 p. m.
	 */
	public static class Instance extends GUIIconInstance {
		
		protected final GUIButtonGoToPage configuration;
		
		public Instance ( GUIButtonGoToPage configuration , GUIInstance gui , String name , ItemStack icon ,
				Collection < String > lore ) {
			super ( gui , name , icon , lore );
			this.configuration = configuration;
		}
		
		public Instance ( GUIButtonGoToPage configuration , GUIInstance gui , String name , ItemStack icon ,
				String... lore ) {
			super ( gui , name , icon , lore );
			this.configuration = configuration;
		}
		
		public Instance ( GUIButtonGoToPage configuration , GUIInstance gui , ItemStack icon ) {
			super ( gui , icon );
			this.configuration = configuration;
		}
		
		public GUIButtonGoToPage getConfiguration ( ) {
			return configuration;
		}
		
		@Override
		public void onClick ( ItemClickAction action ) {
			int    destiny_index = configuration.getDestinyPageIndex ( );
			String destiny_name  = configuration.getDestinyPageName ( );
			
			if ( StringUtil.isNotBlank ( destiny_name ) ) { // by name
				gui.open ( action.getPlayer ( ) , destiny_name );
			} else if ( destiny_index >= 0 ) { // by index
				gui.open ( action.getPlayer ( ) , destiny_index );
			}
		}
	}
	
	public static GUIButtonGoToPage of ( ConfigurationSection section ) {
		return new GUIButtonGoToPage ( ).load ( section );
	}
	
	protected static final String DESTINY_PAGE_KEY = "destiny-page";
	
	protected Object destiny_page;
	
	public GUIButtonGoToPage ( String name , Material material , int amount , int data ,
			List < String > description_format , String display_name_format , Object destiny_page ) {
		super ( name , GUIIconTypeDefault.GO_TO_PAGE_BUTTON , material , amount , data , description_format ,
				display_name_format );
		this.destiny_page = destinyPageCheck ( destiny_page );
	}
	
	public GUIButtonGoToPage ( String name , Material material , int amount , int data ,
			String display_name_format , Object destiny_page , String... description_format ) {
		super ( name , GUIIconTypeDefault.GO_TO_PAGE_BUTTON , material , amount , data , display_name_format ,
				description_format );
		this.destiny_page = destinyPageCheck ( destiny_page );
	}
	
	public GUIButtonGoToPage ( String name , Material material , int amount , int data ,
			String display_name_format , Object destiny_page ) {
		super ( name , GUIIconTypeDefault.GO_TO_PAGE_BUTTON , material , amount , data , display_name_format );
		this.destiny_page = destinyPageCheck ( destiny_page );
	}
	
	public GUIButtonGoToPage ( String name , Material material , Object destiny_page ,
			String display_name_format ) {
		super ( name , GUIIconTypeDefault.GO_TO_PAGE_BUTTON , material , 1 , 0 , display_name_format );
		this.destiny_page = destinyPageCheck ( destiny_page );
	}
	
	public GUIButtonGoToPage ( ) {
		// to be loaded
		super ( GUIIconTypeDefault.GO_TO_PAGE_BUTTON );
	}
	
	@Override
	protected Instance createInstance ( GUIInstance gui , Player player , String display_name ,
			ItemStack icon , List < String > description ) {
		return new Instance ( this , gui , display_name , icon , description );
	}
	
	public int getDestinyPageIndex ( ) {
		if ( destiny_page instanceof Number ) {
			return ( ( Number ) destiny_page ).intValue ( );
		} else {
			return -1;
		}
	}
	
	public String getDestinyPageName ( ) {
		if ( destiny_page instanceof String ) {
			return ( String ) destiny_page;
		} else {
			return null;
		}
	}
	
	@Override
	public GUIButtonGoToPage load ( ConfigurationSection section ) {
		super.load ( section );
		
		// loading destiny page
		this.destiny_page = section.get ( DESTINY_PAGE_KEY );
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = super.save ( section );
		
		// saving destiny page
		if ( destiny_page instanceof Number || destiny_page instanceof String ) {
			save += YamlUtil.setNotEqual ( section , DESTINY_PAGE_KEY , destiny_page ) ? 1 : 0;
		}
		
		return save;
	}
	
	@Override
	public boolean isValid ( ) {
		return super.isValid ( ) && ( destiny_page instanceof Number
				&& ( ( Number ) destiny_page ).intValue ( ) >= 0
				|| destiny_page instanceof String );
	}
	
	protected Object destinyPageCheck ( Object destiny_page ) {
		Validate.notNull ( destiny_page , "destiny page cannot be null" );
		Validate.isTrue ( destiny_page instanceof Number || destiny_page instanceof String ,
						  "destiny page must be either a index (number) or a string (name)" );
		return destiny_page;
	}
}