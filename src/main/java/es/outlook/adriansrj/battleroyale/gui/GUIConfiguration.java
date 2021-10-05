package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.configurable.Configurable;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Team GUI configuration.
 *
 * @author AdrianSR / 09/08/2021 / Time: 11:57 a. m.
 */
public class GUIConfiguration implements Configurable {
	
	protected static final String ICONS_SECTION       = "icon";
	protected static final String PAGES_SECTION       = "page";
	protected static final String PAGE_MODELS_SECTION = "page-models";
	
	public static GUIConfiguration of ( ConfigurationSection section ) {
		return new GUIConfiguration ( ).load ( section );
	}
	
	protected final List < GUIIcon >      icons       = new ArrayList <> ( );
	protected final List < GUIPage >      pages       = new ArrayList <> ( );
	protected final List < GUIPageModel > page_models = new ArrayList <> ( );
	
	protected GUIPageModel page_model_base;
	
	public GUIConfiguration ( Collection < GUIIcon > icons ,
			Collection < GUIPage > pages ,
			GUIPageModel page_model_base ,
			Collection < GUIPageModel > page_models ) {
		this.page_model_base = page_model_base;
		
		this.icons.addAll ( icons );
		this.pages.addAll ( pages );
		this.page_models.addAll ( page_models );
	}
	
	public GUIConfiguration ( ) {
		// to be loaded
	}
	
	public List < GUIIcon > getIcons ( ) {
		return icons;
	}
	
	public GUIIcon getIconByName ( String icon_name ) {
		return icons.stream ( ).filter ( icon -> icon.getName ( ).trim ( ).equalsIgnoreCase ( icon_name.trim ( ) ) )
				.findAny ( ).orElse ( null );
	}
	
	public List < GUIPage > getPages ( ) {
		return pages;
	}
	
	public GUIPage getPageByIndex ( int index ) {
		return this.pages.stream ( ).filter ( GUIPage :: isIndexable )
				.filter ( page -> page.getIndex ( ) == index ).findAny ( ).orElse ( null );
	}
	
	public GUIPageModel getPageModelBase ( ) {
		return page_model_base;
	}
	
	public List < GUIPageModel > getPageModels ( ) {
		return page_models;
	}
	
	public GUIPageModel getPageModelByName ( String name ) {
		return this.page_models.stream ( )
				.filter ( model -> model.getName ( ).trim ( ).equalsIgnoreCase ( name.trim ( ) ) ).findAny ( )
				.orElse ( null );
	}
	
	@Override
	public GUIConfiguration load ( ConfigurationSection section ) {
		// loading icons
		ConfigurationSection icons_section = section.getConfigurationSection ( ICONS_SECTION );
		
		if ( icons_section != null ) {
			this.icons.clear ( );
			
			for ( String key : icons_section.getKeys ( false ) ) {
				ConfigurationSection icon_section = icons_section.getConfigurationSection ( key );
				
				if ( icon_section != null ) {
					GUIIcon icon = GUIIcon.of ( icon_section );
					
					if ( icon != null && icon.isValid ( ) ) {
						icons.add ( icon );
					}
				}
			}
		}
		
		// loading pages
		ConfigurationSection page_section = section.getConfigurationSection ( PAGES_SECTION );
		
		if ( page_section != null ) {
			this.pages.clear ( );
			
			for ( String key : page_section.getKeys ( false ) ) {
				if ( page_section.isConfigurationSection ( key ) ) {
					GUIPage page = GUIPage.of ( page_section.getConfigurationSection ( key ) );
					
					if ( page.isValid ( ) ) {
						this.pages.add ( page );
					}
				}
			}
		}
		
		// loading page models
		ConfigurationSection page_models_section = section.getConfigurationSection ( PAGE_MODELS_SECTION );
		
		if ( page_models_section != null ) {
			this.page_models.clear ( );
			
			for ( String key : page_models_section.getKeys ( false ) ) {
				if ( page_models_section.isConfigurationSection ( key ) ) {
					GUIPageModel page_model = new GUIPageModel ( )
							.load ( page_models_section.getConfigurationSection ( key ) );
					
					if ( page_model.isValid ( ) ) {
						if ( GUIPageModel.PAGE_MODEL_BASE_NAME.equalsIgnoreCase ( key.trim ( ) ) ) {
							this.page_model_base = page_model;
						} else {
							this.page_models.add ( page_model );
						}
					}
				}
			}
		}
		return this;
	}
	
	@Override
	public int save ( ConfigurationSection section ) {
		int save = 0;
		
		// saving icons
		if ( icons.stream ( ).anyMatch ( GUIIcon :: isValid ) ) {
			ConfigurationSection icon_section = section.createSection ( ICONS_SECTION );
			
			save += icons.stream ( )
					.map ( icon -> icon.save ( icon_section.createSection ( icon.getName ( ) ) ) )
					.reduce ( 0 , Integer :: sum );
		}
		
		// saving pages
		if ( pages.stream ( ).anyMatch ( GUIPage :: isValid ) ) {
			ConfigurationSection page_section = section.createSection ( PAGES_SECTION );
			
			save += pages.stream ( ).filter ( GUIPage :: isValid )
					.map ( page -> page.save ( page_section.createSection (
							StringUtil.isNotBlank ( page.getName ( ) ) ? page.getName ( ).trim ( ) :
									"page-" + page.getIndex ( ) ) ) )
					.reduce ( 0 , Integer :: sum );
		}
		
		// saving page models
		if ( page_models.size ( ) > 0 || ( page_model_base != null && page_model_base.isValid ( ) ) ) {
			ConfigurationSection page_models_section = section.createSection ( PAGE_MODELS_SECTION );
			
			// saving page model base
			if ( page_model_base != null && page_model_base.isValid ( ) ) {
				save += page_model_base.save (
						page_models_section.createSection ( GUIPageModel.PAGE_MODEL_BASE_NAME ) );
			}
			
			// saving the rest
			for ( GUIPageModel page_model : this.page_models ) {
				if ( page_model.isValid ( ) ) {
					save += page_model.save ( page_models_section.createSection ( page_model.getName ( ).trim ( ) ) );
				}
			}
		}
		return save;
	}
	
	@Override
	public boolean isValid ( ) {
		return true;
	}
}