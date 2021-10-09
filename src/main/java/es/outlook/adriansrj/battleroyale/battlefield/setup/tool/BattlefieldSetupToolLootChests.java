package es.outlook.adriansrj.battleroyale.battlefield.setup.tool;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.item.ActionItem;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;

/**
 * @author AdrianSR / 13/09/2021 / 12:27 p. m.
 */
public class BattlefieldSetupToolLootChests extends BattlefieldSetupToolItem {
	
	protected BattlefieldSetupToolLootChests ( BattlefieldSetupSession session ,
			Player configurator ) {
		super ( session , configurator );
	}
	
	@Override
	public void initialize ( ) {
		super.initialize ( );
		
		// showing existing loot chests
		World world = session.getWorld ( );
		
		session.getResult ( ).getConfiguration ( ).getLootChests ( ).forEach (
				location -> world.getBlockAt ( session.getResult ( ).getBounds ( )
													   .project ( location ).toLocation ( world ) )
						.setType ( UniversalMaterial.CHEST.getMaterial ( ) ) );
	}
	
	@Override
	public boolean isModal ( ) {
		return false;
	}
	
	@Override
	public boolean isCancellable ( ) {
		return false;
	}
	
	@Override
	protected Material getItemMaterial ( ) {
		return UniversalMaterial.CHEST.getMaterial ( );
	}
	
	@Override
	protected String getItemDisplayName ( ) {
		return StringUtil.concatenate ( ChatColor.GOLD , ChatColor.BOLD ) + "Add/Remove loot chest";
	}
	
	@Override
	protected List < String > getItemDescription ( ) {
		return Arrays.asList (
				"" ,
				ChatColor.GRAY + "- Right click to add" ,
				ChatColor.GRAY + "  a loot chest." ,
				ChatColor.GRAY + "" ,
				ChatColor.GRAY + "- Left click on a loot" ,
				ChatColor.GRAY + "  chest to remove it." );
	}
	
	@Override
	protected void onActionPerform ( org.bukkit.entity.Player player ,
			ActionItem.EnumAction action , PlayerInteractEvent event ) {
		Block clicked = event.getClickedBlock ( );
		
		if ( clicked != null ) {
			if ( action.isRightClick ( ) ) {
				Block    placed   = clicked.getRelative ( event.getBlockFace ( ) );
				Location location = placed.getLocation ( );
				
				if ( session.addLootChest ( location.toVector ( ) ) ) {
					placed.setType ( UniversalMaterial.CHEST.getMaterial ( ) );
					placed.getState ( ).update ( );
					
					player.sendMessage ( ChatColor.GREEN + "Loot chest added at " + location.getBlockX ( )
												 + ", " + location.getBlockY ( ) + ", "
												 + location.getBlockZ ( ) + "." );
				}
			} else {
				if ( session.removeLootChest ( clicked.getLocation ( ).toVector ( ) ) ) {
					player.sendMessage ( ChatColor.GREEN + "Loot chest removed successfully!" );
				}
			}
		}
	}
}
