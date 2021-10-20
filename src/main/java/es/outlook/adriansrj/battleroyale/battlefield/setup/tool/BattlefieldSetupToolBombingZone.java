package es.outlook.adriansrj.battleroyale.battlefield.setup.tool;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupResult;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.enums.EnumInternalLanguage;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.menu.Item;
import es.outlook.adriansrj.core.menu.ItemMenu;
import es.outlook.adriansrj.core.menu.action.ItemClickAction;
import es.outlook.adriansrj.core.menu.item.voidaction.VoidActionItem;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;

import java.util.Arrays;

/**
 * Tool useful to set the bombing zone generator configuration.
 *
 * @author AdrianSR / 19/10/2021 / 07:11 p. m.
 */
public class BattlefieldSetupToolBombingZone extends BattlefieldSetupToolGUI {
	
	protected int minimum;
	protected int maximum;
	
	protected BattlefieldSetupToolBombingZone ( BattlefieldSetupSession session , Player configurator ) {
		super ( session , configurator );
		
		// loading current values
		BattlefieldSetupResult result = session.getResult ( );
		
		if ( result != null ) {
			minimum = result.getConfiguration ( ).getBombingZoneMin ( );
			maximum = result.getConfiguration ( ).getBombingZoneMax ( );
		}
	}
	
	@Override
	public boolean isModal ( ) {
		return true;
	}
	
	@Override
	public boolean isCancellable ( ) {
		return true;
	}
	
	@Override
	protected ItemMenu build ( ) {
		final ItemMenu handle = new ItemMenu ( ChatColor.BLACK + "Bombing Zone" , ItemMenuSize.FOUR_LINE );
		
		// >>>>>>>>>>>>>>>> minimum
		handle.setItem ( 11 , new VoidActionItem (
				ChatColor.GOLD + "Minimum" , UniversalMaterial.TNT.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Current value: " + ChatColor.GOLD + minimum ,
				"" ,
				ChatColor.GRAY + "This value represents" ,
				ChatColor.GRAY + "the minimum number of bombing" ,
				ChatColor.GRAY + "zones that must be generated" ,
				ChatColor.GRAY + "each time the border pauses." ) );
		
		// minimum -
		handle.setItem ( 19 , new Item (
				ChatColor.GOLD + "-" , UniversalMaterial.STICK.getItemStack ( ) ) {
			
			@Override
			public void onClick ( ItemClickAction action ) {
				setMinimum ( minimum - 1 );
				
				// updating
				update ( handle , action.getPlayer ( ) );
			}
		} );
		
		// minimum +
		handle.setItem ( 21 , new Item (
				ChatColor.GOLD + "+" , UniversalMaterial.STICK.getItemStack ( ) ) {
			
			@Override
			public void onClick ( ItemClickAction action ) {
				setMinimum ( minimum + 1 );
				
				// updating
				update ( handle , action.getPlayer ( ) );
			}
		} );
		
		// >>>>>>>>>>>>>>>> maximum
		handle.setItem ( 15 , new VoidActionItem (
				ChatColor.GOLD + "Maximum" , UniversalMaterial.TNT.getItemStack ( ) ,
				"" ,
				ChatColor.GRAY + "Current value: " + ChatColor.GOLD + maximum ,
				"" ,
				ChatColor.GRAY + "This value represents" ,
				ChatColor.GRAY + "the maximum number of bombing" ,
				ChatColor.GRAY + "zones that can be generated" ,
				ChatColor.GRAY + "each time the border pauses." ) );
		
		// minimum -
		handle.setItem ( 23 , new Item (
				ChatColor.GOLD + "-" , UniversalMaterial.STICK.getItemStack ( ) ) {
			
			@Override
			public void onClick ( ItemClickAction action ) {
				setMaximum ( maximum - 1 );
				
				// updating
				update ( handle , action.getPlayer ( ) );
			}
		} );
		
		// minimum +
		handle.setItem ( 25 , new Item (
				ChatColor.GOLD + "+" , UniversalMaterial.STICK.getItemStack ( ) ) {
			
			@Override
			public void onClick ( ItemClickAction action ) {
				setMaximum ( maximum + 1 );
				
				// updating
				update ( handle , action.getPlayer ( ) );
			}
		} );
		
		// >>>>>>>>>>>>>>>> save
		handle.setItem ( 35 , new Item (
				ChatColor.GREEN + "Save" , UniversalMaterial.GREEN_WOOL.getItemStack ( ) ) {
			
			@Override
			public void onClick ( ItemClickAction action ) {
				dispose ( );
				
				// setting
				session.setBombingZoneMin ( minimum );
				session.setBombingZoneMax ( maximum );
				
				// letting player know
				action.getPlayer ( ).sendMessage (
						EnumInternalLanguage.TOOL_FINISHED_MESSAGE.toString ( ) );
			}
		} );
		
		return handle;
	}
	
	protected void setMinimum ( int minimum ) {
		this.minimum = Math.max ( minimum , 0 );
		
		if ( this.maximum < minimum ) {
			this.maximum = minimum;
		}
	}
	
	protected void setMaximum ( int maximum ) {
		this.maximum = Math.max ( maximum , 0 );
		
		if ( this.maximum < minimum ) {
			this.maximum = minimum;
		}
	}
	
	protected void update ( ItemMenu handle , org.bukkit.entity.Player player ) {
		// minimum
		handle.getItem ( 11 ).setLore ( Arrays.asList (
				"" ,
				ChatColor.GRAY + "Current value: " + ChatColor.GOLD + minimum ,
				"" ,
				ChatColor.GRAY + "This value represents" ,
				ChatColor.GRAY + "the minimum number of bombing" ,
				ChatColor.GRAY + "zones that must be generated" ,
				ChatColor.GRAY + "each time the border pauses."
		) );
		
		// maximum
		handle.getItem ( 15 ).setLore ( Arrays.asList (
				"" ,
				ChatColor.GRAY + "Current value: " + ChatColor.GOLD + maximum ,
				"" ,
				ChatColor.GRAY + "This value represents" ,
				ChatColor.GRAY + "the maximum number of bombing" ,
				ChatColor.GRAY + "zones that can be generated" ,
				ChatColor.GRAY + "each time the border pauses."
		) );
		
		// then updating
		handle.update ( player );
	}
}