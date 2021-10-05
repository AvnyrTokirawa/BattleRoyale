package es.outlook.adriansrj.battleroyale.battlefield.setup.tool;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.item.ActionItem;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * @author AdrianSR / 05/09/2021 / 12:11 a. m.
 */
public class BattlefieldSetupToolPlayerSpawns extends BattlefieldSetupToolItem {
	
	/**
	 * @author AdrianSR / 14/09/2021 / 05:04 p. m.
	 */
	protected static class SpawnManipulator {
		
		protected static final String MANIPULATOR_METADATA_KEY = UUID.randomUUID ( ).toString ( );
		
		protected final BattlefieldSetupToolPlayerSpawns tool;
		protected       Vector                           location;
		protected       ArmorStand                       handle;
		
		public SpawnManipulator ( Vector location , BattlefieldSetupToolPlayerSpawns tool ) {
			this.location = location;
			this.tool     = tool;
		}
		
		protected void update ( ) {
			World world = tool.session.getWorld ( );
			
			if ( handle == null ) {
				handle = world.spawn ( location.toLocation ( world ) , ArmorStand.class );
				handle.setVisible ( false );
				handle.setGravity ( false );
				handle.setSmall ( false );
				handle.setBasePlate ( false );
				handle.setMetadata ( MANIPULATOR_METADATA_KEY ,
									 new FixedMetadataValue ( BattleRoyale.getInstance ( ) , this ) );
				handle.setHelmet ( UniversalMaterial.WHITE_BED.getItemStack ( ) );
				handle.setCustomNameVisible ( true );
				handle.setCustomName ( ChatColor.DARK_GREEN + "Player Spawn" );
			}
			
			handle.teleport ( location.toLocation ( world ) );
		}
		
		public void destroy ( ) {
			if ( handle != null ) {
				handle.remove ( );
				handle = null;
			}
		}
	}
	
	protected final Set < SpawnManipulator > manipulators = new HashSet <> ( );
	
	protected BattlefieldSetupToolPlayerSpawns ( BattlefieldSetupSession session ,
			Player configurator ) {
		super ( session , configurator );
		
		// loading existing spawns
		for ( Vector location : session.getResult ( ).getConfiguration ( ).getPlayerSpawns ( ) ) {
			SpawnManipulator manipulator = new SpawnManipulator (
					session.getResult ( ).getBounds ( ).project ( location ) , this );
			manipulator.update ( );
			
			manipulators.add ( manipulator );
		}
	}
	
	@Override
	public boolean isModal ( ) {
		return true;
	}
	
	@Override
	public boolean isCancellable ( ) {
		return false;
	}
	
	@Override
	protected Material getItemMaterial ( ) {
		return UniversalMaterial.WHITE_BED.getMaterial ( );
	}
	
	@Override
	protected String getItemDisplayName ( ) {
		return StringUtil.concatenate ( ChatColor.GOLD , ChatColor.BOLD ) + "Add player spawn";
	}
	
	@Override
	protected List < String > getItemDescription ( ) {
		return Arrays.asList (
				"" ,
				"" ,
				"" );
	}
	
	// for adding new spawns
	@Override
	protected void onActionPerform ( org.bukkit.entity.Player player ,
			ActionItem.EnumAction action , PlayerInteractEvent event ) {
		Vector location = player.getLocation ( ).toVector ( );
		
		if ( voidCheck ( location ) ) {
			SpawnManipulator manipulator = new SpawnManipulator ( location , this );
			
			if ( session.addPlayerSpawn ( manipulator.location ) ) {
				manipulator.update ( );
				manipulators.add ( manipulator );
				
				player.sendMessage ( ChatColor.GREEN + "Player spawn added successfully!" );
			}
		} else {
			player.sendMessage ( ChatColor.RED + "Cannot add spawns over the void!" );
		}
	}
	
	// for manipulating already existing spawns
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onManipulate ( PlayerInteractAtEntityEvent event ) {
		org.bukkit.entity.Player player = event.getPlayer ( );
		Entity                   uncast = event.getRightClicked ( );
		
		if ( uncast instanceof ArmorStand && Objects.equals (
				player.getUniqueId ( ) , session.getOwner ( ).getUniqueId ( ) ) ) {
			// finding out the clicked manipulator
			SpawnManipulator manipulator = null;
			
			if ( uncast.hasMetadata ( SpawnManipulator.MANIPULATOR_METADATA_KEY ) ) {
				manipulator = ( SpawnManipulator ) uncast.getMetadata (
						SpawnManipulator.MANIPULATOR_METADATA_KEY ).get ( 0 ).value ( );
			}
			
			// right click will displace the spawn to the
			// direction the player is facing
			if ( manipulator != null ) {
				Vector direction = player.getLocation ( ).getDirection ( );
				Vector location  = manipulator.location.clone ( ).add ( direction.multiply ( 0.5D ) );
				
				if ( voidCheck ( location ) ) {
					manipulator.location = location;
					manipulator.update ( );
					
					save ( );
				} else {
					player.sendMessage ( ChatColor.RED + "The spawn cannot be over the void!" );
				}
				
				event.setCancelled ( true );
			}
		}
	}
	
	// for removing spawns
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onRemove ( EntityDamageByEntityEvent event ) {
		if ( event.getDamager ( ) instanceof org.bukkit.entity.Player
				&& event.getEntity ( ) instanceof ArmorStand ) {
			org.bukkit.entity.Player player = ( org.bukkit.entity.Player ) event.getDamager ( );
			ArmorStand               stand  = ( ArmorStand ) event.getEntity ( );
			
			// finding out the clicked manipulator
			SpawnManipulator manipulator = null;
			
			if ( stand.hasMetadata ( SpawnManipulator.MANIPULATOR_METADATA_KEY ) ) {
				manipulator = ( SpawnManipulator ) stand.getMetadata (
						SpawnManipulator.MANIPULATOR_METADATA_KEY ).get ( 0 ).value ( );
			}
			
			// then removing
			if ( manipulator != null ) {
				manipulator.destroy ( );
				manipulators.remove ( manipulator );
				
				save ( );
				
				player.sendMessage ( ChatColor.GREEN + "Player spawn removed successfully!" );
			}
		}
	}
	
	protected boolean voidCheck ( Vector vector ) {
		World world = session.getWorld ( );
		Block floor = world.getBlockAt ( vector.toLocation ( world ) );
		
		while ( !floor.getType ( ).isSolid ( ) ) {
			floor = floor.getRelative ( BlockFace.DOWN );
			
			if ( floor.getY ( ) < 0 ) {
				floor = null;
				break;
			}
		}
		
		return floor != null && floor.getType ( ).isSolid ( );
	}
	
	@Override
	protected void dispose ( ) {
		super.dispose ( );
		
		// disposing manipulators
		this.manipulators.forEach ( SpawnManipulator :: destroy );
		this.manipulators.clear ( );
	}
	
	protected void save ( ) {
		this.session.clearPlayerSpawns ( );
		this.manipulators.stream ( ).map ( manipulator -> manipulator.location )
				.forEach ( this.session :: addPlayerSpawn );
	}
}
