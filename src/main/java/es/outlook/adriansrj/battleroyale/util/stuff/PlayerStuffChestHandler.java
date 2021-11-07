package es.outlook.adriansrj.battleroyale.util.stuff;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.EventUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import es.outlook.adriansrj.core.util.math.RandomUtil;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.*;

/**
 * Class that manages the stuff-chests of the players how died.
 *
 * @author AdrianSR / 23/10/2021 / 07:58 p. m.
 */
public final class PlayerStuffChestHandler extends PluginHandler {
	
	private static final String FALLING_BLOCK_METADATA_KEY = UUID.randomUUID ( ).toString ( );
	private static final String STUFF_BLOCK_METADATA_KEY   = UUID.randomUUID ( ).toString ( );
	
	public static PlayerStuffChestHandler getInstance ( ) {
		return getPluginHandler ( PlayerStuffChestHandler.class );
	}
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public PlayerStuffChestHandler ( BattleRoyale plugin ) {
		super ( plugin ); register ( );
	}
	
	@SuppressWarnings ( "deprecation" )
	public void spawnStuffChest ( Collection < ItemStack > stuff , Location location ) {
		Block block = location.getBlock ( );
		
		if ( block.getType ( ).isSolid ( )
				|| block.getRelative ( BlockFace.DOWN ).getType ( ).isSolid ( ) ) {
			if ( !block.getType ( ).isSolid ( ) ) {
				block = block.getRelative ( BlockFace.DOWN );
			}
			
			spawnStuffChest ( block , stuff );
		} else {
			// ground is not close; let's spawn
			// using a falling block.
			FallingBlock falling_block;
			
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 ) ) {
				falling_block = location.getWorld ( ).spawnFallingBlock (
						location , UniversalMaterial.CHEST.getMaterial ( ).createBlockData ( ) );
			} else {
				// legacy versions
				if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_12_R1 ) ) {
					falling_block = location.getWorld ( ).spawnFallingBlock ( location , new org.bukkit.material.Chest (
							RandomUtil.getRandomElement ( DirectionUtil.FACES_90 ) ) );
				} else {
					falling_block = location.getWorld ( ).spawnFallingBlock (
							location , UniversalMaterial.CHEST.getMaterial ( ) , ( byte ) 0 );
				}
			}
			
			falling_block.setDropItem ( false );
			falling_block.setHurtEntities ( false );
			falling_block.setMetadata ( FALLING_BLOCK_METADATA_KEY ,
										new FixedMetadataValue ( BattleRoyale.getInstance ( ) , stuff ) );
		}
	}
	
	public void spawnStuffChest ( org.bukkit.entity.Player player , Location location ) {
		// calculating stuff
		List < ItemStack > stuff = new ArrayList <> ( );
		player.getInventory ( ).forEach ( stuff :: add );
		
		// then spawning
		spawnStuffChest ( stuff , location );
	}
	
	public void spawnStuffChest ( Player player , Location location ) {
		player.getBukkitPlayerOptional ( ).ifPresent (
				bukkit -> spawnStuffChest ( bukkit , location ) );
	}
	
	// event handler responsible for spawning the stuff
	// chest once the corresponding falling block lands.
	@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onLand ( EntityChangeBlockEvent event ) {
		Entity entity = event.getEntity ( );
		Block  block  = event.getBlock ( );
		
		if ( entity instanceof FallingBlock
				&& entity.hasMetadata ( FALLING_BLOCK_METADATA_KEY ) ) {
			List < MetadataValue > values = entity.getMetadata ( FALLING_BLOCK_METADATA_KEY );
			MetadataValue          value  = values.size ( ) > 0 ? values.get ( 0 ) : null;
			Object                 uncast = value != null ? value.value ( ) : null;
			
			if ( uncast instanceof List ) {
				List < ItemStack > stuff = new ArrayList <> ( );
				
				for ( Object uncast_item : ( List < ? > ) uncast ) {
					if ( uncast_item instanceof ItemStack ) {
						stuff.add ( ( ItemStack ) uncast_item );
					}
				}
				
				// then spawning
				spawnStuffChest ( block , stuff );
			}
		}
	}
	
	// event handler responsible for redirecting players
	// that clicks a stuff chest to the right inventory.
	@EventHandler ( priority = EventPriority.HIGHEST, ignoreCancelled = true )
	public void onOpenStuffChest ( PlayerInteractEvent event ) {
		org.bukkit.entity.Player player    = event.getPlayer ( );
		Player                   br_player = Player.getPlayer ( player );
		Block                    block     = event.getClickedBlock ( );
		
		if ( block != null && block.hasMetadata ( STUFF_BLOCK_METADATA_KEY )
				&& EventUtil.isRightClick ( event.getAction ( ) ) && br_player.isPlaying ( ) ) {
			// must cancel interaction
			event.setCancelled ( true );
			
			// then opening
			List < MetadataValue > metadata = block.getMetadata ( STUFF_BLOCK_METADATA_KEY );
			MetadataValue          value    = metadata.size ( ) > 0 ? metadata.get ( 0 ) : null;
			Object                 uncast   = value != null ? value.value ( ) : null;
			
			if ( uncast instanceof Inventory ) {
				player.openInventory ( ( Inventory ) uncast );
			}
		}
	}
	
	// ------- utils
	
	private void spawnStuffChest ( Block block , Collection < ItemStack > stuff ) {
		if ( stuff.size ( ) > 0 ) {
			if ( !( block.getState ( ) instanceof Chest ) ) {
				block.setType ( UniversalMaterial.CHEST.getMaterial ( ) );
				block.getState ( ).update ( true , true );
			}
			
			block.setMetadata ( STUFF_BLOCK_METADATA_KEY , new FixedMetadataValue (
					BattleRoyale.getInstance ( ) , buildStuffInventory ( stuff ) ) );
		}
	}
	
	private Inventory buildStuffInventory ( Collection < ItemStack > stuff ) {
		ItemMenuSize size      = ItemMenuSize.fitOf ( stuff.size ( ) );
		Inventory    inventory = Bukkit.createInventory ( null , size.getSize ( ) );
		
		stuff.stream ( ).filter ( Objects :: nonNull )
				.forEach ( inventory :: addItem );
		
		return inventory;
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}
