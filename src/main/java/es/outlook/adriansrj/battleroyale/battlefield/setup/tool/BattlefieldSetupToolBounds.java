package es.outlook.adriansrj.battleroyale.battlefield.setup.tool;

import es.outlook.adriansrj.battleroyale.battlefield.minimap.Minimap;
import es.outlook.adriansrj.battleroyale.battlefield.minimap.renderer.MinimapRendererSetupSession;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.enums.EnumInternalLanguage;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.util.MiniMapUtil;
import es.outlook.adriansrj.battleroyale.util.itemstack.ItemStackUtil;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.core.item.ActionItem;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author AdrianSR / 28/08/2021 / 10:50 a. m.
 */
public class BattlefieldSetupToolBounds extends BattlefieldSetupToolItem {
	
	protected Location2I corner_a;
	protected Location2I corner_b;
	
	protected BattlefieldSetupToolBounds ( BattlefieldSetupSession session , Player configurator ) {
		super ( session , configurator );
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
	protected Material getItemMaterial ( ) {
		return UniversalMaterial.BLAZE_ROD.getMaterial ( );
	}
	
	@Override
	protected String getItemDisplayName ( ) {
		return StringUtil.concatenate ( ChatColor.GOLD , ChatColor.BOLD ) + "Bounds Selector";
	}
	
	@Override
	protected List < String > getItemDescription ( ) {
		return Arrays.asList (
				"" ,
				"" ,
				"" );
	}
	
	@Override
	protected void onActionPerform ( org.bukkit.entity.Player player ,
			ActionItem.EnumAction action , PlayerInteractEvent event ) {
		Block block = event.getClickedBlock ( );
		Vector corner = block != null ? block.getLocation ( ).toVector ( )
				: player.getLocation ( ).toVector ( );
		
		if ( action.isLeftClick ( ) ) {
			this.corner_a = new Location2I ( corner.getBlockX ( ) , corner.getBlockZ ( ) );
		} else {
			this.corner_b = new Location2I ( corner.getBlockX ( ) , corner.getBlockZ ( ) );
		}
		
		player.sendMessage ( ChatColor.GOLD + ( action.isLeftClick ( ) ? "First" : "Second" ) + " corner set." );
		
		if ( corner_a != null && corner_b != null ) {
			player.sendMessage ( ChatColor.GOLD + "Bounds set successfully! Calculating stuff..." );
			
			Consumer < Minimap > minimap_callback = minimap -> {
				// we will give the minimap item to any
				// player in the session once the minimap is
				// successfully generated.
				if ( minimap != null ) {
					giveMinimap ( session.getOwner ( ) );
					
					for ( Player invited : session.getGuestList ( ) ) {
						giveMinimap ( invited );
					}
				}
			};
			
			Consumer < Boolean > schematic_callback = schematic -> {
				// this will let the player know that schematic
				// was successfully exported.
				if ( schematic != null && schematic ) {
					player.sendMessage ( EnumInternalLanguage.TOOL_BOUNDS_SCHEMATIC_SET.toString ( ) );
				}
			};
			
			getSession ( ).setBounds ( new ZoneBounds ( corner_a , corner_b ) ,
									   minimap_callback , schematic_callback );
			dispose ( );
		}
	}
	
	protected void giveMinimap ( Player br_player ) {
		br_player.getBukkitPlayerOptional ( ).ifPresent ( player -> {
			player.getInventory ( ).addItem (
					ItemStackUtil.createViewItemStack ( MiniMapUtil.createView (
							new MinimapRendererSetupSession ( session ) , session.getWorld ( ) ) ) );
			player.updateInventory ( );
			
			// letting know
			player.sendMessage ( EnumInternalLanguage.TOOL_BOUNDS_MINIMAP_SET.toString ( ) );
		} );
	}
}