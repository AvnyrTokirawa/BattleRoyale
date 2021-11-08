package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.util.StringUtil;

import java.util.*;

/**
 * @author AdrianSR / 30/09/2021 / 10:33 a. m.
 */
public abstract class GUIInstance {
	
	protected GUIConfiguration configuration;
	
	protected final Map < String, ItemMenu > named_pages = new HashMap <> ( );
	protected       ItemMenu[]               indexed_pages;
	
	protected GUIInstance ( GUIConfiguration configuration ) {
		setConfiguration ( configuration );
	}
	
	public synchronized GUIConfiguration getConfiguration ( ) {
		return configuration;
	}
	
	public synchronized void setConfiguration ( GUIConfiguration configuration ) {
		Validate.notNull ( configuration , "configuration cannot be null" );
		Validate.isTrue ( configuration.isValid ( ) , "configuration must be valid" );
		
		this.configuration = configuration;
	}
	
	public Map < String, ItemMenu > getNamedPages ( ) {
		return Collections.unmodifiableMap ( named_pages );
	}
	
	public ItemMenu[] getIndexedPages ( ) {
		return Arrays.copyOf ( indexed_pages , indexed_pages.length );
	}
	
	public synchronized void open ( org.bukkit.entity.Player player ) {
		build ( Player.getPlayer ( player ) );
		
		if ( indexed_pages != null && indexed_pages.length > 0 ) {
			indexed_pages[ 0 ].open ( player );
		} else if ( named_pages.size ( ) > 0 ) {
			named_pages.values ( ).iterator ( ).next ( ).open ( player );
		}
	}
	
	public synchronized void open ( org.bukkit.entity.Player player , String page_name ) {
		build ( Player.getPlayer ( player ) );
		
		// then opening
		ItemMenu page = named_pages.get ( page_name );
		
		// there could be named pages
		// stored in the indexed pages that
		// were skipped at the moment of building.
		if ( page == null ) {
			for ( GUIPage configuration : this.configuration.getPages ( ) ) {
				if ( configuration.isIndexable ( )
						&& Objects.equals ( page_name , configuration.getName ( ) ) ) {
					int      index    = configuration.getIndex ( );
					ItemMenu instance = indexed_pages.length > index ? indexed_pages[ index ] : null;
					
					if ( instance != null ) {
						page = instance;
						break;
					}
				}
			}
		}
		
		if ( page != null ) {
			page.open ( player );
		}
	}
	
	public synchronized void open ( org.bukkit.entity.Player player , int page_index ) {
		build ( Player.getPlayer ( player ) );
		
		// then opening
		if ( indexed_pages != null && indexed_pages.length > page_index ) {
			ItemMenu page = indexed_pages[ page_index ];
			
			if ( page != null ) {
				page.open ( player );
			}
		}
	}
	
	public synchronized boolean update ( org.bukkit.entity.Player player ) {
		if ( indexed_pages != null ) {
			for ( int i = 0 ; i < indexed_pages.length ; i++ ) {
				ItemMenu page = indexed_pages[ 0 ];
				
				if ( page.isMenuOpen ( player ) ) {
					page.update ( player );
					return true;
				}
			}
		}
		
		for ( ItemMenu page : named_pages.values ( ) ) {
			if ( page.isMenuOpen ( player ) ) {
				page.update ( player );
				return true;
			}
		}
		return false;
	}
	
	public synchronized void build ( Player player ) {
		// building indexed pages
		buildIndexedPages ( player );
		// building named pages
		buildNamedPages ( player );
	}
	
	public synchronized void build ( org.bukkit.entity.Player player ) {
		build ( Player.getPlayer ( player ) );
	}
	
	protected synchronized void buildIndexedPages ( Player player ) {
		// finding out the maximum index
		int max_index = 0;
		
		for ( GUIPage page : configuration.getPages ( ) ) {
			if ( page.isIndexable ( ) && page.getIndex ( ) > max_index ) {
				max_index = page.getIndex ( );
			}
		}
		
		// then building
		for ( int index = 0 ; index <= max_index ; index++ ) {
			GUIPage  page     = configuration.getPageByIndex ( index );
			ItemMenu instance = page != null ? buildPage ( page , player ) : null;
			
			// registering page
			if ( instance != null ) {
				ensureIndexedPagesSize ( index + 1 );
				
				// then registering
				indexed_pages[ index ] = instance;
				
				// registering listener. page was
				// probably recycled; in that case
				// listener is already registered.
				if ( !instance.isListenerRegistered ( ) ) {
					instance.registerListener ( BattleRoyale.getInstance ( ) );
				}
			}
		}
	}
	
	/**
	 * This method will ensure that the size of the {@link #indexed_pages}
	 * array is at least the provided <code>size</code>.
	 *
	 * @param size the required size.
	 */
	protected synchronized void ensureIndexedPagesSize ( int size ) {
		if ( indexed_pages == null ) {
			indexed_pages = new ItemMenu[ size ];
		} else if ( size >= indexed_pages.length ) { // re-dimensioning
			ItemMenu[] updated = new ItemMenu[ size ];
			System.arraycopy ( indexed_pages , 0 , updated , 0 , indexed_pages.length );
			
			this.indexed_pages = updated;
		}
	}
	
	protected synchronized void buildNamedPages ( Player player ) {
		for ( GUIPage page : configuration.getPages ( ) ) {
			// pages that are indexable will be skipped, as
			// there will already be instances for then created
			// at the moment of building the indexed pages.
			if ( page.isIndexable ( ) ) {
				continue;
			}
			
			ItemMenu instance = buildPage ( page , player );
			
			if ( instance != null ) {
				// then registering
				named_pages.put ( page.getName ( ) , instance );
				
				// registering listener. page was
				// probably recycled; in that case
				// listener is already registered.
				if ( !instance.isListenerRegistered ( ) ) {
					instance.registerListener ( BattleRoyale.getInstance ( ) );
				}
			}
		}
	}
	
	protected synchronized ItemMenu buildPage ( final GUIPage page , Player player ) {
		GUIPageModel model = getPageModel ( page );
		
		if ( model != null && model.isValid ( ) ) {
			ItemMenu instance = null;
			// parent will be the previous page
			// only if this page is indexable, and
			// if there is a previous page.
			ItemMenu parent = null;
			
			if ( page.isIndexable ( ) && indexed_pages != null ) {
				// recycling page from indexed pages
				if ( indexed_pages.length > page.getIndex ( ) ) {
					instance = indexed_pages[ page.getIndex ( ) ];
				}
				
				if ( page.getIndex ( ) > 0 && indexed_pages.length > page.getIndex ( ) - 1 ) {
					parent = indexed_pages[ page.getIndex ( ) - 1 ];
				}
			}
			
			if ( instance == null && named_pages.containsKey ( page.getName ( ) ) ) {
				// recycling page from named pages
				instance = named_pages.get ( page.getName ( ) );
			}
			
			// page to be recycled not found,
			// lets create a new instance.
			if ( instance == null ) {
				instance = buildPageInstance ( page , model , parent , player );
			}
			
			// making sure is clear
			instance.clear ( );
			
			// inserting slots
			populatePageContents ( page , model , instance , player );
			
			return instance;
		} else {
			return null;
		}
	}
	
	protected synchronized ItemMenu buildPageInstance ( GUIPage page , GUIPageModel model ,
			ItemMenu parent , Player player ) {
		return new ItemMenu (
				// title
				StringUtil.isBlank ( page.getTitle ( ) ) ? StringUtil.EMPTY : page.getTitle ( ) ,
				// size
				model.getSize ( ) ,
				// parent
				parent ) {
			
			@Override
			public boolean update ( org.bukkit.entity.Player player ) {
				Player br_player = Player.getPlayer ( player );
				
				if ( br_player != null && br_player.isOnline ( ) ) {
					ItemMenu new_instance = null;
					
					// the build method will probably dispose this instance,
					// so we need to open that new instance.
					super.clear ( );
					GUIInstance.this.build ( br_player );
					
					if ( page.isIndexable ( ) ) {
						int index = page.getIndex ( );
						
						if ( GUIInstance.this.indexed_pages != null
								&& GUIInstance.this.indexed_pages.length > index ) {
							new_instance = GUIInstance.this.indexed_pages[ index ];
						}
					} else {
						new_instance = GUIInstance.this.named_pages.get ( page.getName ( ) );
					}
					
					if ( Objects.equals ( new_instance , this ) ) {
						// a new instance was not created,
						// so we're clear to actually update.
						super.update ( player );
					} else if ( new_instance != null ) {
						new_instance.open ( player );
					}
					return true;
				} else {
					return false;
				}
			}
		};
	}
	
	protected synchronized GUIIconInstance buildSlot ( GUIPage page , GUIPageModel model ,
			ItemMenu page_instance , int index , Player player ) {
		GUIPageModelSlot slot = getSlotConfiguration ( model , index );
		GUIIcon          icon = slot != null ? configuration.getIconByName ( slot.getIconName ( ) ) : null;
		
		if ( slot != null && slot.isValid ( )
				&& icon != null && icon.isValid ( ) ) {
			return icon.createInstance ( this , player );
		} else {
			return null;
		}
	}
	
	protected synchronized void populatePageContents ( GUIPage page , GUIPageModel model ,
			ItemMenu page_instance , Player player ) {
		for ( int i = 0 ; i < page_instance.getSize ( ).getSize ( ) ; i++ ) {
			GUIIconInstance slot_instance = buildSlot ( page , model , page_instance , i , player );
			
			if ( slot_instance != null ) {
				page_instance.setItem ( i , slot_instance );
			}
		}
	}
	
	protected synchronized GUIPageModel getPageModel ( GUIPage page ) {
		GUIPageModel model = null;
		
		// specified model
		if ( StringUtil.isNotBlank ( page.getModelName ( ) ) ) {
			model = this.configuration.getPageModelByName ( page.getModelName ( ) );
		}
		
		// model not specified/invalid, using model base
		if ( model == null || model.isInvalid ( ) ) {
			model = this.configuration.getPageModelBase ( );
		}
		
		return model;
	}
	
	protected synchronized GUIPageModelSlot getSlotConfiguration ( GUIPageModel model , int index ) {
		GUIPageModelSlot slot = null;
		
		// specified slot
		slot = model.getContent ( ).getSlotByIndex ( index );
		
		// slot not specified or invalid, we will
		// insert the slot base if specified.
		if ( slot == null || slot.isInvalid ( ) ) {
			slot = model.getContent ( ).getSlotBase ( );
		}
		
		return slot;
	}
	
	public synchronized void dispose ( ) {
		named_pages.values ( ).forEach ( ItemMenu :: unregisterListener );
		named_pages.clear ( );
		
		if ( indexed_pages != null ) {
			for ( ItemMenu page_instance : indexed_pages ) {
				if ( page_instance != null ) {
					page_instance.unregisterListener ( );
				}
			}
			
			indexed_pages = null;
		}
	}
}