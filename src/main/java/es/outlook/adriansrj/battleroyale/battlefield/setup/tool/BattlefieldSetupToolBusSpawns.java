package es.outlook.adriansrj.battleroyale.battlefield.setup.tool;

import es.outlook.adriansrj.battleroyale.battlefield.bus.BusSpawn;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.math.MathUtil;
import es.outlook.adriansrj.battleroyale.util.math.ZoneBounds;
import es.outlook.adriansrj.battleroyale.util.task.BukkitRunnableWrapper;
import es.outlook.adriansrj.core.item.ActionItem;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.entity.UUIDEntity;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import es.outlook.adriansrj.core.util.math.IntersectionUtil;
import es.outlook.adriansrj.core.util.math.Vector2D;
import es.outlook.adriansrj.core.util.math.collision.Ray;
import es.outlook.adriansrj.core.util.math.target.TargetUtil;
import es.outlook.adriansrj.core.util.reflection.bukkit.EntityReflection;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.RegularColor;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author AdrianSR / 07/09/2021 / 07:20 p. m.
 */
public class BattlefieldSetupToolBusSpawns extends BattlefieldSetupToolItem {
	
	/**
	 * @author AdrianSR / 07/09/2021 / 07:25 p. m.
	 */
	protected static class SpawnManipulator implements Runnable {
		
		protected static final String MANIPULATOR_START_METADATA_KEY = UUID.randomUUID ( ).toString ( );
		protected static final String MANIPULATOR_METADATA_KEY       = UUID.randomUUID ( ).toString ( );
		protected static final String MANIPULATOR_SPEED_METADATA_KEY = UUID.randomUUID ( ).toString ( );
		
		protected final BattlefieldSetupToolBusSpawns tool;
		protected final Vector                        start_location;
		protected final BukkitTask                    particle_displayer;
		protected final BukkitRunnable                speed_task;
		protected       float                         yaw;
		protected       double                        door_point_distance;
		protected       double                        speed;
		
		protected ArmorStand                        start_manipulator;
		protected Set < UUIDEntity < ArmorStand > > start_manipulator_description;
		protected ArmorStand                        door_manipulator;
		protected Set < UUIDEntity < ArmorStand > > door_manipulator_description;
		
		public SpawnManipulator ( BattlefieldSetupToolBusSpawns tool , BusSpawn spawn ) {
			this ( tool , spawn.getStartLocation ( ) , spawn.getYaw ( ) ,
				   spawn.getDoorPointDistance ( ) , spawn.getSpeed ( ) );
		}
		
		public SpawnManipulator ( BattlefieldSetupToolBusSpawns tool , Vector start_location ,
				float yaw , double door_point_distance , double speed ) {
			this.tool                = tool;
			this.start_location      = tool.session.getResult ( ).getBounds ( ).project ( start_location );
			this.yaw                 = yaw;
			this.door_point_distance = door_point_distance;
			this.speed               = speed;
			this.particle_displayer  = Bukkit.getScheduler ( ).runTaskTimerAsynchronously (
					BattleRoyale.getInstance ( ) , this , 12L , 12L );
			
			this.speed_task = new BukkitRunnableWrapper ( ) {
				@Override
				public void run ( ) {
					SpawnManipulator.this.run ( );
				}
			};
			this.speed_task.runTaskTimer ( BattleRoyale.getInstance ( ) , 0L , 0L );
		}
		
		public BusSpawn getResult ( ) {
			return new BusSpawn ( tool.session.getResult ( ).getBounds ( ).unproject ( start_location ) ,
								  yaw , door_point_distance , speed );
		}
		
		public void update ( ) {
			World world = tool.session.getWorld ( );
			
			// re-making models
			destroy ( false );
			
			// start manipulator
			start_manipulator = world.spawn ( start_location.toLocation ( world ).add ( 0 , -0.5D , 0 ) ,
											  ArmorStand.class );
			start_manipulator.setCustomNameVisible ( false );
			start_manipulator.setCustomName ( StringUtil.EMPTY );
			start_manipulator.getEquipment ( ).setHelmet ( UniversalMaterial.RED_WOOL.getItemStack ( ) );
			start_manipulator.setVisible ( false );
			start_manipulator.setGravity ( false );
			start_manipulator.setMetadata ( MANIPULATOR_START_METADATA_KEY ,
											new FixedMetadataValue ( BattleRoyale.getInstance ( ) , this ) );
			
			List < String > description = new ArrayList <> ( );
			
			description.add ( ChatColor.DARK_RED + "This is the point where the" );
			description.add ( ChatColor.DARK_RED + "travel of the bus starts" );
			
			start_manipulator_description = spawnManipulatorDescription (
					start_location.clone ( ).add ( new Vector ( 0 , 1.5 , 0 ) ) ,
					description , MANIPULATOR_START_METADATA_KEY );
			
			// door manipulator
			Vector door_point_location = calculateDoorPointLocation ( );
			
			door_manipulator = world.spawn ( door_point_location.toLocation ( world ) , ArmorStand.class );
			door_manipulator.setCustomNameVisible ( false );
			door_manipulator.setCustomName ( StringUtil.EMPTY );
			door_manipulator.getEquipment ( ).setHelmet ( UniversalMaterial.GREEN_WOOL.getItemStack ( ) );
			door_manipulator.setVisible ( false );
			door_manipulator.setGravity ( false );
			door_manipulator.setMetadata ( MANIPULATOR_METADATA_KEY ,
										   new FixedMetadataValue ( BattleRoyale.getInstance ( ) , this ) );
			
			description.clear ( );
			description.add ( ChatColor.DARK_GREEN + "This is the point where the" );
			description.add ( ChatColor.DARK_GREEN + "bus door opens, allowing the" );
			description.add ( ChatColor.DARK_GREEN + "players to jump" );
			
			door_manipulator_description = spawnManipulatorDescription (
					door_point_location.clone ( ).add ( new Vector ( 0 , 1.5 , 0 ) ) ,
					description , MANIPULATOR_METADATA_KEY );
		}
		
		protected Vector calculateDoorPointLocation ( ) {
			return start_location.clone ( ).add (
					DirectionUtil.getDirection ( yaw , 0.0F ).clone ( ).multiply ( door_point_distance ) );
		}
		
		protected Set < UUIDEntity < ArmorStand > > spawnManipulatorDescription ( Vector location ,
				List < String > description , String metadata_key ) {
			Set < UUIDEntity < ArmorStand > > result = new HashSet <> ( );
			
			for ( int i = 0 ; i < description.size ( ) ; i++ ) {
				String line = description.get ( i );
				ArmorStand handle = spawnArmorStand ( location.clone ( ).add (
						new Vector ( 0 , i * -0.27D , 0 ) ) );
				
				handle.setCustomName ( line );
				handle.setCustomNameVisible ( true );
				handle.setMetadata ( metadata_key ,
									 new FixedMetadataValue ( BattleRoyale.getInstance ( ) , this ) );
				
				result.add ( new UUIDEntity <> ( handle ) );
			}
			
			return result;
		}
		
		protected ArmorStand spawnArmorStand ( Vector location ) {
			ArmorStand result = tool.session.getWorld ( ).spawn (
					location.toLocation ( tool.session.getWorld ( ) ) , ArmorStand.class );
			
			result.setVisible ( false );
			result.setGravity ( false );
			
			return result;
		}
		
		public void destroy ( boolean dispose ) {
			if ( start_manipulator != null ) {
				start_manipulator.remove ( );
				start_manipulator = null;
			}
			
			if ( start_manipulator_description != null ) {
				start_manipulator_description.stream ( ).map ( UUIDEntity :: get ).forEach ( ArmorStand :: remove );
				start_manipulator_description.clear ( );
			}
			
			if ( door_manipulator != null ) {
				door_manipulator.remove ( );
				door_manipulator = null;
			}
			
			if ( door_manipulator_description != null ) {
				door_manipulator_description.stream ( ).map ( UUIDEntity :: get ).forEach ( ArmorStand :: remove );
				door_manipulator_description.clear ( );
			}
			
			if ( speed_display != null ) {
				speed_display.remove ( );
				speed_display = null;
			}
			
			if ( dispose ) {
				particle_displayer.cancel ( );
				speed_task.cancel ( );
			}
		}
		
		protected       ArmorStand    speed_display;
		protected       double        speed_display_x;
		protected       double        speed_display_z;
		protected       boolean       speed_display_entered_bounds;
		protected final DecimalFormat decimal_format = new DecimalFormat ( "#.##" );
		
		@Override
		public void run ( ) {
			ZoneBounds bounds = tool.session.getResult ( ).getBounds ( );
			
			if ( Bukkit.isPrimaryThread ( ) ) {
				// speed display task calls
				Vector direction = DirectionUtil.getDirection ( yaw , 0.0F );
				
				if ( !bounds.contains ( start_location ) && !IntersectionUtil.intersectRayBoundsFast (
						new Ray ( start_location , direction ) , bounds.toBoundingBox ( ) ) ) {
					// bus will never be within bounds
					return;
				}
				
				/* displacing displayer */
				if ( speed_display == null ) {
					speed_display_x              = start_location.getX ( );
					speed_display_z              = start_location.getZ ( );
					speed_display_entered_bounds =
							tool.session.getResult ( ).getBounds ( ).contains ( start_location );
					
					// spawning
					speed_display = tool.session.getWorld ( ).spawn (
							start_location.toLocation ( tool.session.getWorld ( ) ) , ArmorStand.class );
					speed_display.setGravity ( false );
					speed_display.setVisible ( false );
					speed_display.getEquipment ( ).setHelmet ( UniversalMaterial.RED_WOOL.getItemStack ( ) );
					speed_display.setCustomNameVisible ( true );
					speed_display.setMetadata ( MANIPULATOR_SPEED_METADATA_KEY ,
												new FixedMetadataValue ( BattleRoyale.getInstance ( ) ,
																		 SpawnManipulator.this ) );
				}
				
				speed_display_x += direction.getX ( ) * speed;
				speed_display_z += direction.getZ ( ) * speed;
				boolean out_bounds = !tool.session.getResult ( ).getBounds ( )
						.contains ( speed_display_x , speed_display_z );
				boolean door_open = new Vector2D ( speed_display_x , speed_display_z )
						.distance ( new Vector2D ( start_location.getX ( ) , start_location.getZ ( ) ) )
						>= door_point_distance;
				
				if ( out_bounds && speed_display_entered_bounds ) {
					speed_display_x              = start_location.getX ( );
					speed_display_z              = start_location.getZ ( );
					speed_display_entered_bounds = false;
				}
				
				if ( !speed_display_entered_bounds && !out_bounds ) {
					speed_display_entered_bounds = true;
				}
				
				// updating displaying
				speed_display.setCustomName (
						ChatColor.DARK_GREEN + "Displacement Speed: " +
								ChatColor.DARK_BLUE + decimal_format.format ( speed ) );
				
				ItemStack helmet = ( door_open ? UniversalMaterial.GREEN_WOOL :
						UniversalMaterial.RED_WOOL ).getItemStack ( );
				
				if ( !helmet.isSimilar ( speed_display.getEquipment ( ).getHelmet ( ) ) ) {
					speed_display.getEquipment ( ).setHelmet ( helmet );
				}
				
				// displacing
				EntityReflection.setLocation (
						speed_display , new Location (
								tool.session.getWorld ( ) ,
								speed_display_x , start_location.getY ( ) , speed_display_z ) );
			} else {
				// particles display task calls
				// start point <-> door open point
				Vector door_point = calculateDoorPointLocation ( );
				
				travel ( start_location.toLocation ( tool.session.getWorld ( ) ) ,
						 door_point.toLocation ( tool.session.getWorld ( ) ) , true );
				
				// door open point <-> end point
				Vector end_point = MathUtil.approximateEndPointLocation ( getResult ( ) , bounds );
				
				if ( end_point != null ) {
					travel ( door_point.toLocation ( tool.session.getWorld ( ) ) ,
							 end_point.toLocation ( tool.session.getWorld ( ) ) , false );
				}
			}
		}
		
		protected void travel ( Location from , Location to , boolean before_door ) {
			Vector direction = to.clone ( ).subtract ( from ).toVector ( ).normalize ( );
			double factor    = 0.0D;
			double increase  = 5.0D;
			double distance  = from.distance ( to );
			RegularColor color = before_door
					? new RegularColor ( 255 , 0 , 0 )
					: new RegularColor ( 0 , 255 , 0 );
			
			while ( factor + increase < distance ) {
				ParticleEffect.REDSTONE.display (
						from.clone ( ).add ( direction.clone ( ).multiply ( factor += increase ) ) ,
						0 , 0 , 0 , 0 , 1 , color );
			}
		}
		
		@Override
		public boolean equals ( Object o ) {
			if ( this == o ) { return true; }
			if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
			SpawnManipulator that = ( SpawnManipulator ) o;
			return Float.compare ( that.yaw , yaw ) == 0 && Objects.equals ( start_location ,
																			 that.start_location );
		}
		
		@Override
		public int hashCode ( ) {
			return Objects.hash ( start_location , yaw );
		}
	}
	
	protected final Set < SpawnManipulator > manipulators = new HashSet <> ( );
	
	protected BattlefieldSetupToolBusSpawns ( BattlefieldSetupSession session , Player configurator ) {
		super ( session , configurator );
	}
	
	@Override
	public void initialize ( ) {
		super.initialize ( );
		
		// loading existing spawns
		for ( BusSpawn spawn : session.getResult ( ).getConfiguration ( ).getBusSpawns ( ) ) {
			if ( spawn != null && spawn.isValid ( ) ) {
				SpawnManipulator manipulator = new SpawnManipulator ( this , spawn );
				manipulator.update ( );
				
				manipulators.add ( manipulator );
			}
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
		return UniversalMaterial.MINECART.getMaterial ( );
	}
	
	@Override
	protected String getItemDisplayName ( ) {
		return StringUtil.concatenate ( ChatColor.GOLD , ChatColor.BOLD ) + "Add bus spawn";
	}
	
	@Override
	protected List < String > getItemDescription ( ) {
		return Arrays.asList (
				"" ,
				"" ,
				"" );
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
			
			if ( stand.hasMetadata ( SpawnManipulator.MANIPULATOR_START_METADATA_KEY ) ) {
				manipulator = ( SpawnManipulator ) stand.getMetadata (
						SpawnManipulator.MANIPULATOR_START_METADATA_KEY ).get ( 0 ).value ( );
			} else if ( stand.hasMetadata ( SpawnManipulator.MANIPULATOR_METADATA_KEY ) ) {
				manipulator = ( SpawnManipulator ) stand.getMetadata (
						SpawnManipulator.MANIPULATOR_METADATA_KEY ).get ( 0 ).value ( );
			}
			
			// then removing
			if ( manipulator != null ) {
				manipulator.destroy ( true );
				manipulators.remove ( manipulator );
				
				save ( );
				
				player.sendMessage ( ChatColor.GREEN + "Bus spawn removed successfully!" );
			}
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
			boolean          speed       = false;
			
			if ( uncast.hasMetadata ( SpawnManipulator.MANIPULATOR_METADATA_KEY ) ) {
				manipulator = ( SpawnManipulator ) uncast.getMetadata (
						SpawnManipulator.MANIPULATOR_METADATA_KEY ).get ( 0 ).value ( );
			} else if ( uncast.hasMetadata ( SpawnManipulator.MANIPULATOR_SPEED_METADATA_KEY ) ) {
				manipulator = ( SpawnManipulator ) uncast.getMetadata (
						SpawnManipulator.MANIPULATOR_SPEED_METADATA_KEY ).get ( 0 ).value ( );
				speed       = true;
			} else if ( uncast.hasMetadata ( SpawnManipulator.MANIPULATOR_START_METADATA_KEY ) ) {
				event.setCancelled ( true );
				return;
			}
			
			// right click will displace the door manipulator/change speed
			if ( manipulator != null ) {
				float player_yaw = DirectionUtil.normalize ( player.getLocation ( ).getYaw ( ) );
				float spawn_yaw  = DirectionUtil.normalize ( manipulator.yaw );
				float distance = DirectionUtil.normalize (
						Math.max ( player_yaw , spawn_yaw ) - Math.min ( player_yaw , spawn_yaw ) );
				
				if ( distance < 90.0F ) {
					if ( speed ) {
						// towards (increasing speed)
						manipulator.speed += 0.25D;
						manipulator.speed = Math.min ( 3.0D , manipulator.speed );
					} else {
						// towards (increasing distance)
						manipulator.door_point_distance += 1.0D;
					}
					
				} else {
					if ( speed ) {
						// backwards (decreasing speed)
						manipulator.speed -= 0.25D;
						manipulator.speed = Math.max ( 0.0D , manipulator.speed );
					} else {
						// backwards (decreasing distance)
						manipulator.door_point_distance -= 1.0D;
					}
				}
				
				manipulator.update ( );
				save ( );
				
				event.setCancelled ( true );
			}
		}
	}
	
	// for adding new bus spawns
	@Override
	protected void onActionPerform ( org.bukkit.entity.Player player ,
			ActionItem.EnumAction action , PlayerInteractEvent event ) {
		if ( !action.isRightClick ( )
				|| TargetUtil.getTarget ( player , 8.0D , ArmorStand.class ) != null ) {
			// making sure is right click.
			// making sure is not clicking a manipulator
			return;
		}
		
		Location player_location     = player.getLocation ( );
		Vector   start_location      = player_location.toVector ( );
		float    yaw                 = player_location.getYaw ( );
		double   door_point_distance = calculateDoorPointDistance ( start_location , yaw );
		double   speed               = BusSpawn.DEFAULT_BUS_SPEED;
		
		// manipulator
		SpawnManipulator manipulator = new SpawnManipulator (
				this , session.getResult ( ).getBounds ( ).unproject ( start_location ) ,
				yaw , door_point_distance , speed );
		
		if ( session.addBusSpawn ( start_location , yaw , door_point_distance , speed , true ) ) {
			manipulator.update ( );
			manipulators.add ( manipulator );
			
			player.sendMessage ( ChatColor.GREEN + "Bus spawn added successfully!" );
		}
	}
	
	protected double calculateDoorPointDistance ( Vector start_location , float yaw ) {
		ZoneBounds bounds = session.getResult ( ).getBounds ( );
		int        size   = bounds.getSize ( );
		World      world  = session.getWorld ( );
		
		Block highest = world.getHighestBlockAt (
				start_location.getBlockX ( ) , start_location.getBlockZ ( ) );
		
		if ( highest.getY ( ) >= 0 && highest.getType ( ).isSolid ( ) ) {
			// already over solid ground, so it is not actually required
			// to be too far.
			return 2.0D;
		} else {
			Vector  direction = DirectionUtil.getDirection ( yaw , 0.0F );
			Vector  point     = null;
			double  factor    = 0.0D;
			boolean found     = false;
			
			do {
				factor += 1.0D;
				point   = start_location.clone ( ).add ( direction.clone ( ).multiply ( factor ) );
				highest = world.getHighestBlockAt ( point.getBlockX ( ) , point.getBlockZ ( ) );
				
				if ( highest.getY ( ) >= 0 && !highest.isEmpty ( ) ) {
					found = true;
					break;
				}
			} while ( point.distance ( start_location ) < size );
			
			if ( found ) {
				return point.distance ( start_location );
			} else {
				return 2.0D;
			}
		}
	}
	
	@Override
	protected void dispose ( ) {
		super.dispose ( );
		
		// disposing manipulators
		this.manipulators.forEach ( manipulators -> manipulators.destroy ( true ) );
		this.manipulators.clear ( );
	}
	
	protected void save ( ) {
		this.session.clearBusSpawns ( );
		this.manipulators.stream ( ).map ( SpawnManipulator :: getResult )
				.forEach ( this.session :: addBusSpawn );
	}
}
