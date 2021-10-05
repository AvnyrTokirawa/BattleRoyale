package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.core.menu.Item;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Objects;

/**
 * {@link Item} implementation for {@link GUIInstance}es.
 *
 * @author AdrianSR / 30/09/2021 / 12:16 p. m.
 */
public abstract class GUIIconInstance extends Item {
	
	protected final GUIInstance gui;
	
	protected GUIIconInstance ( GUIInstance gui , String name , ItemStack icon , Collection < String > lore ) {
		super ( name , icon , lore );
		this.gui = gui;
	}
	
	protected GUIIconInstance ( GUIInstance gui , String name , ItemStack icon , String... lore ) {
		super ( name , icon , lore );
		this.gui = gui;
	}
	
	protected GUIIconInstance ( GUIInstance gui , ItemStack icon ) {
		super ( icon );
		this.gui = gui;
	}
	
	public GUIInstance getGUI ( ) {
		return gui;
	}
	
	@Override
	public void onClick ( ItemClickAction action ) {
		// does nothing by default
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		if ( !( o instanceof GUIIconInstance ) ) { return false; }
		if ( !super.equals ( o ) ) { return false; }
		GUIIconInstance that = ( GUIIconInstance ) o;
		return Objects.equals ( gui , that.gui );
	}
	
	@Override
	public int hashCode ( ) {
		return Objects.hash ( super.hashCode ( ) , gui );
	}
}
