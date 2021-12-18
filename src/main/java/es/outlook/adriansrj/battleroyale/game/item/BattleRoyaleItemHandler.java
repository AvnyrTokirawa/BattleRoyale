package es.outlook.adriansrj.battleroyale.game.item;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.enums.EnumItem;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.schedule.ScheduledExecutorPool;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.world.WorldUtil;
import es.outlook.adriansrj.battleroyale.util.task.BukkitRunnableWrapper;
import es.outlook.adriansrj.battleroyale.util.time.Delay;
import es.outlook.adriansrj.battleroyale.util.time.DelayPool;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.Duration;
import es.outlook.adriansrj.core.util.EventUtil;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import es.outlook.adriansrj.core.util.math.RandomUtil;
import es.outlook.adriansrj.core.util.math.collision.BoundingBox;
import es.outlook.adriansrj.core.util.server.Version;
import es.outlook.adriansrj.core.util.sound.UniversalSound;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * @author AdrianSR / 25/10/2021 / 05:15 p. m.
 */
public final class BattleRoyaleItemHandler extends PluginHandler {
	
	private static final String LAUNCH_PAD_METADATA_KEY = UUID.randomUUID ( ).toString ( );
	private static final String TNT_METADATA_KEY        = UUID.randomUUID ( ).toString ( );
	
	/**
	 * Map for storing a delay pool for each kit.
	 * <p>
	 * Intended for a easier delay handling.
	 */
	private static final EnumMap < EnumItem, DelayPool > DELAY_POOL_MAP = new EnumMap <> ( EnumItem.class );
	
	/**
	 * Gets the corresponding kit {@link DelayPool}.
	 * <p>
	 *
	 * @param type the kit class.
	 *
	 * @return the corresponding delay pool.
	 */
	private static DelayPool getDelayPool ( EnumItem type ) {
		return DELAY_POOL_MAP.computeIfAbsent ( type , k -> DelayPool.createDelayPool ( ) );
	}
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BattleRoyaleItemHandler ( BattleRoyale plugin ) {
		super ( plugin );
		
		register ( );
		loadConfiguration ( );
	}
	
	// this event handler is responsible for disallowing
	// players to drop battle royale items that cannot be dropped.
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onDrop ( PlayerDropItemEvent event ) {
		ItemStack drop = event.getItemDrop ( ).getItemStack ( );
		
		if ( Arrays.stream ( EnumItem.values ( ) ).anyMatch (
				item -> !item.isDroppable ( ) && item.isThis ( drop ) ) ) {
			event.setCancelled ( true );
		}
	}
	
	// ------- bridge egg
	
	/**
	 * @author AdrianSR / 01/11/2021 / 04:49 p. m.
	 */
	private static class BridgeEggTask extends BukkitRunnableWrapper {
		
		protected final Vector      spawn;
		protected final Vector      direction;
		protected final BlockFace   face;
		protected final BlockFace[] sides;
		protected final Egg         handle;
		
		private BridgeEggTask ( Vector spawn , Vector direction , Egg handle ) {
			this.spawn     = spawn;
			this.direction = direction;
			this.face      = DirectionUtil.getBlockFace45 ( DirectionUtil.getEulerAngles ( direction )[ 0 ] );
			this.sides     = new BlockFace[] {
					DirectionUtil.getLeftFace ( face ) ,
					DirectionUtil.getRightFace ( face ) ,
			};
			this.handle    = handle;
		}
		
		@Override
		public void run ( ) {
			if ( handle.isDead ( ) ) {
				cancel ( );
			} else {
				Vector location = handle.getLocation ( ).toVector ( );
				
				if ( location.distance ( spawn ) < EnumItem.BRIDGE_EGG.getExtraConfigurationEntry (
						Constants.LIMIT_KEY ).getValueAs ( Number.class ).intValue ( ) ) {
					Block block = handle.getWorld ( ).getBlockAt (
									location.getBlockX ( ) , location.getBlockY ( ) , location.getBlockZ ( ) )
							.getRelative ( BlockFace.DOWN , 2 );
					Block back = block.getRelative ( face.getOppositeFace ( ) );
					
					path ( block );
					path ( back );
					
					// side block
					Block side      = block.getRelative ( sides[ RandomUtil.nextBoolean ( ) ? 1 : 0 ] );
					Block side_back = side.getRelative ( face.getOppositeFace ( ) );
					
					path ( side );
					path ( side_back );
				} else {
					cancel ( );
					
					// disposing handle
					if ( !handle.isDead ( ) ) {
						handle.remove ( );
					}
				}
			}
		}
		
		protected void path ( Block block ) {
			// must set in the next tick, otherwise the egg
			// would get stuck in the block.
			if ( block.isEmpty ( ) ) {
				Bukkit.getScheduler ( ).scheduleSyncDelayedTask ( BattleRoyale.getInstance ( ) , ( ) -> {
					block.setType ( UniversalMaterial.SANDSTONE.getMaterial ( ) );
					block.getState ( ).update ( true );
					block.getWorld ( ).playSound ( block.getLocation ( ) ,
												   UniversalSound.CHICKEN_EGG_POP.asBukkit ( ) , 1.5F , 1.0F );
					
					WorldUtil.setPlayerBlock ( block , true );
				} , 1L );
			}
		}
	}
	
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onThrow ( PlayerEggThrowEvent event ) {
		org.bukkit.entity.Player player    = event.getPlayer ( );
		Player                   br_player = Player.getPlayer ( player );
		
		if ( EnumItem.BRIDGE_EGG.isThis ( br_player.getItemInHand ( ) ) ) {
			event.setHatching ( false );
		}
	}
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onThrowBridgeEgg ( PlayerInteractEvent event ) {
		org.bukkit.entity.Player player    = event.getPlayer ( );
		ItemStack                item      = event.getItem ( );
		Player                   br_player = Player.getPlayer ( player );
		BattleRoyaleArena        arena     = br_player.getArena ( );
		
		if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING && !arena.isOver ( )
				&& EventUtil.isRightClick ( event.getAction ( ) )
				&& !br_player.getBus ( ).isPassenger ( br_player )
				&& !br_player.getParachute ( ).isOpen ( )
				&& !br_player.isKnocked ( ) && EnumItem.BRIDGE_EGG.isThis ( item ) ) {
			event.setCancelled ( true );
			
			DelayPool delay_pool = getDelayPool ( EnumItem.BRIDGE_EGG );
			Delay     delay      = delay_pool.getOrCreateDelay ( player.getUniqueId ( ) );
			
			if ( !delay.isActive ( ) ) {
				delay.activate ( EnumItem.BRIDGE_EGG.getExtraConfigurationEntry (
						Constants.DELAY_KEY ).getValueAs ( Duration.class ) );
				
				// consuming
				if ( player.getGameMode ( ) != GameMode.CREATIVE ) {
					consume ( player.getInventory ( ) , EnumItem.BRIDGE_EGG :: isThis , 1 );
				}
				
				// then throwing
				Vector direction = player.getEyeLocation ( ).getDirection ( );
				
				new BridgeEggTask ( player.getLocation ( ).toVector ( ) , direction , player.launchProjectile (
						Egg.class , direction.multiply ( 2 ) ) )
						.runTaskTimer ( BattleRoyale.getInstance ( ) , 2L , 0L );
			}
			
			// updating player inventory
			player.updateInventory ( );
		}
	}
	
	// ------- launch pad
	
	@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onPlace ( BlockPlaceEvent event ) {
		Block             block     = event.getBlock ( );
		Player            br_player = Player.getPlayer ( event.getPlayer ( ) );
		BattleRoyaleArena arena     = br_player.getArena ( );
		
		if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING && !arena.isOver ( )
				&& !br_player.isKnocked ( )
				&& !br_player.getBus ( ).isPassenger ( br_player )
				&& !br_player.getParachute ( ).isOpen ( )
				&& EnumItem.LAUNCH_PAD.isThis ( br_player.getItemInHand ( ) ) ) {
			new LaunchPadTask ( block ).start ( );
		}
	}
	
	@EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onBreak ( BlockBreakEvent event ) {
		Block block = event.getBlock ( );
		
		if ( block.hasMetadata ( LAUNCH_PAD_METADATA_KEY ) ) {
			Object uncast = block.getMetadata ( LAUNCH_PAD_METADATA_KEY ).get ( 0 ).value ( );
			
			if ( uncast instanceof LaunchPadTask ) {
				( ( LaunchPadTask ) uncast ).destroy ( );
				
				// clearing drops
				if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_12_R1 ) ) {
					event.setDropItems ( false );
				}
			}
		}
	}
	
	/**
	 * @author AdrianSR / 02/11/2021 / 09:46 a. m.
	 */
	private static class LaunchPadTask extends BukkitRunnableWrapper {
		
		private final Block         block;
		private final long          duration;
		private final long          end_time;
		private final Set < Block > restore;
		private       BoundingBox   bounds;
		
		public LaunchPadTask ( Block block ) {
			this.block    = block;
			this.duration = EnumItem.LAUNCH_PAD.getExtraConfigurationEntry ( Constants.DURATION_KEY )
					.getValueAs ( Duration.class ).toMillis ( );
			this.end_time = System.currentTimeMillis ( ) + duration;
			this.restore  = new HashSet <> ( );
		}
		
		void start ( ) {
			int size = EnumItem.LAUNCH_PAD.getExtraConfigurationEntry ( Constants.SIZE_KEY )
					.getValueAsNumber ( ).intValue ( );
			
			if ( size > 0 ) {
				if ( size % 2 == 0 ) {
					// warning message
					ConsoleUtil.sendPluginMessage (
							ChatColor.RED , "Launch pad size must not be even" , BattleRoyale.getInstance ( ) );
				} else {
					// we're making it odd, so we
					// can use it in a for loop.
					size -= 1;
				}
				
				// placing
				int size_half = size >> 1;
				
				for ( int x = block.getX ( ) - size_half ; x <= block.getX ( ) + size_half ; x++ ) {
					for ( int z = block.getZ ( ) - size_half ; z <= block.getZ ( ) + size_half ; z++ ) {
						Block block = this.block.getWorld ( ).getBlockAt ( x , this.block.getY ( ) , z );
						
						if ( block.isEmpty ( ) || Objects.equals ( block , this.block ) ) {
							restore.add ( block );
							
							block.setType ( UniversalMaterial.SLIME_BLOCK.getMaterial ( ) );
							block.getState ( ).update ( true );
							block.setMetadata ( LAUNCH_PAD_METADATA_KEY , new FixedMetadataValue (
									BattleRoyale.getInstance ( ) , this ) );
							
							WorldUtil.setPlayerBlock ( block , true );
						}
					}
				}
				
				// calculating bounds
				int min_x = block.getX ( ) - size_half;
				int min_z = block.getZ ( ) - size_half;
				int max_x = block.getX ( ) + size_half;
				int max_z = block.getZ ( ) + size_half;
				
				this.bounds = new BoundingBox (
						min_x - 0.5D , block.getY ( ) + 1.0D , min_z - 0.5D ,
						max_x + 0.5D , block.getY ( ) + 1.5D , max_z + 0.5D );
				
				// scheduling
				runTaskTimerAsynchronously ( BattleRoyale.getInstance ( ) , 0L , 10L );
				// scheduling canceller. we will need another
				// task to cancel the particle displayer as
				// it runs each half of a second which is not accurate.
				ScheduledExecutorPool.getInstance ( ).getSingleThreadScheduledExecutor ( ).schedule (
						this :: destroy , duration , TimeUnit.MILLISECONDS );
			}
		}
		
		void destroy ( ) {
			if ( Bukkit.isPrimaryThread ( ) ) {
				// stopping task
				if ( !isCancelled ( ) ) {
					cancel ( );
				}
				
				// restoring blocks
				restore.stream ( ).filter ( block -> block.getType ( ) == UniversalMaterial.SLIME_BLOCK.getMaterial ( )
								&& WorldUtil.isPlayerBlock ( block ) && block.hasMetadata ( LAUNCH_PAD_METADATA_KEY ) )
						.forEach ( block -> {
							block.setType ( UniversalMaterial.AIR.getMaterial ( ) );
							block.getState ( ).update ( true );
							block.removeMetadata ( LAUNCH_PAD_METADATA_KEY , BattleRoyale.getInstance ( ) );
							
							WorldUtil.setPlayerBlock ( block , false );
						} );
				restore.clear ( );
			} else {
				Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , this :: destroy );
			}
		}
		
		@Override
		public void run ( ) {
			if ( System.currentTimeMillis ( ) < end_time ) {
				restore.forEach ( block -> ParticleEffect.VILLAGER_HAPPY.display (
						block.getLocation ( ).add ( 0.5D , 0.9D , 0.5D ) ,
						0.3F , 0F , 0.3F , 0.2F , 2 , null ) );
				
				// launching players above
				block.getWorld ( ).getPlayers ( ).stream ( ).filter (
						player -> bounds.contains ( player.getLocation ( ).toVector ( ) ) ).forEach ( player -> {
					Player br_player = Player.getPlayer ( player );
					
					if ( br_player.isPlaying ( ) && !br_player.isKnocked ( ) ) {
						br_player.setCanOpenParachute ( true );
						
						// launching towards sky
						player.setVelocity ( player.getLocation ( ).getDirection ( )
													 .multiply ( 0.0 ).setY ( 1 ).multiply ( 3.0D ) );
					}
				} );
			} else {
				destroy ( );
			}
		}
	}
	
	// ------- fireball
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onThrowFireball ( PlayerInteractEvent event ) {
		org.bukkit.entity.Player player    = event.getPlayer ( );
		ItemStack                item      = event.getItem ( );
		Player                   br_player = Player.getPlayer ( player );
		BattleRoyaleArena        arena     = br_player.getArena ( );
		
		if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING && !arena.isOver ( )
				&& EventUtil.isRightClick ( event.getAction ( ) )
				&& !br_player.getBus ( ).isPassenger ( br_player )
				&& !br_player.getParachute ( ).isOpen ( )
				&& !br_player.isKnocked ( ) && EnumItem.FIREBALL.isThis ( item ) ) {
			event.setCancelled ( true );
			
			DelayPool delay_pool = getDelayPool ( EnumItem.FIREBALL );
			Delay     delay      = delay_pool.getOrCreateDelay ( player.getUniqueId ( ) );
			
			if ( !delay.isActive ( ) ) {
				delay.activate ( EnumItem.FIREBALL.getExtraConfigurationEntry (
						Constants.DELAY_KEY ).getValueAs ( Duration.class ) );
				
				// consuming
				if ( player.getGameMode ( ) != GameMode.CREATIVE ) {
					consume ( player.getInventory ( ) , EnumItem.FIREBALL :: isThis , 1 );
				}
				
				// then throwing
				double strength = EnumItem.FIREBALL.getExtraConfigurationEntry (
						Constants.STRENGTH_KEY ).getValueAsNumber ( ).doubleValue ( );
				
				if ( strength > 0.0D ) {
					Fireball fireball = player.launchProjectile (
							Fireball.class , player.getEyeLocation ( ).getDirection ( ).multiply ( 1.1 ) );
					
					fireball.setIsIncendiary ( false );
					fireball.setYield ( ( float ) strength );
				}
			}
			
			// updating player inventory
			player.updateInventory ( );
		}
	}
	
	// ------- fireball
	
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onThrowTnt ( PlayerInteractEvent event ) {
		org.bukkit.entity.Player player    = event.getPlayer ( );
		ItemStack                item      = event.getItem ( );
		Player                   br_player = Player.getPlayer ( player );
		BattleRoyaleArena        arena     = br_player.getArena ( );
		
		if ( arena != null && arena.getState ( ) == EnumArenaState.RUNNING && !arena.isOver ( )
				&& EventUtil.isRightClick ( event.getAction ( ) )
				&& !br_player.getBus ( ).isPassenger ( br_player )
				&& !br_player.getParachute ( ).isOpen ( )
				&& !br_player.isKnocked ( ) && EnumItem.TNT.isThis ( item ) ) {
			event.setCancelled ( true );
			
			DelayPool delay_pool = getDelayPool ( EnumItem.TNT );
			Delay     delay      = delay_pool.getOrCreateDelay ( player.getUniqueId ( ) );
			
			if ( !delay.isActive ( ) ) {
				delay.activate ( EnumItem.TNT.getExtraConfigurationEntry (
						Constants.DELAY_KEY ).getValueAs ( Duration.class ) );
				
				// consuming
				if ( player.getGameMode ( ) != GameMode.CREATIVE ) {
					consume ( player.getInventory ( ) , EnumItem.TNT :: isThis , 1 );
				}
				
				// then throwing
				double strength = EnumItem.TNT.getExtraConfigurationEntry (
						Constants.STRENGTH_KEY ).getValueAsNumber ( ).doubleValue ( );
				
				if ( strength > 0.0D ) {
					TNTPrimed tnt = player.getWorld ( ).spawn ( player.getLocation ( ) , TNTPrimed.class );
					
					tnt.setVelocity ( player.getLocation ( ).getDirection ( ).clone ( ).multiply ( 1.1D ) );
					tnt.setFuseTicks ( 30 );
					tnt.setIsIncendiary ( false );
					tnt.setYield ( ( float ) strength );
					tnt.setMetadata ( TNT_METADATA_KEY ,
									  new FixedMetadataValue ( BattleRoyale.getInstance ( ) , br_player ) );
				}
			}
			
			// updating player inventory
			player.updateInventory ( );
		}
	}
	
	// this event will stop TNTs from damaging teammates.
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onExplode ( EntityDamageByEntityEvent event ) {
		Entity uncast_victim = event.getEntity ( );
		Entity damager       = event.getDamager ( );
		
		if ( uncast_victim instanceof org.bukkit.entity.Player
				&& damager instanceof TNTPrimed && damager.hasMetadata ( TNT_METADATA_KEY ) ) {
			org.bukkit.entity.Player victim    = ( org.bukkit.entity.Player ) uncast_victim;
			Player                   br_victim = Player.getPlayer ( victim );
			Object                   uncast    = damager.getMetadata ( TNT_METADATA_KEY ).get ( 0 ).value ( );
			
			if ( uncast instanceof Player ) {
				Player thrower = ( Player ) uncast;
				
				if ( !Objects.equals ( thrower , br_victim )
						&& Objects.equals ( thrower.getTeam ( ) , br_victim.getTeam ( ) ) ) {
					event.setCancelled ( true );
				}
			}
		}
	}
	
	// ------- utils
	
	private void loadConfiguration ( ) {
		File                      file = EnumFile.ITEM_CONFIGURATION.safeGetFile ( );
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( file );
		
		// this will save the defaults
		defaultConfigurationCheck ( file , yaml );
		
		// then loading
		for ( EnumItem item : EnumItem.values ( ) ) {
			if ( item.isConfigurable ( ) ) {
				ConfigurationSection section = yaml.getConfigurationSection ( formatName ( item ) );
				
				if ( section != null ) {
					item.load ( section );
				}
			}
		}
	}
	
	private void defaultConfigurationCheck ( File file , YamlConfigurationComments yaml ) {
		int save = 0;
		
		for ( EnumItem item : EnumItem.values ( ) ) {
			if ( item.isConfigurable ( ) ) {
				save += item.save ( YamlUtil.createNotExisting ( yaml , formatName ( item ) ) );
			}
		}
		
		if ( save > 0 ) {
			try {
				yaml.save ( file );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
	
	private String formatName ( EnumItem item ) {
		return item.name ( ).toLowerCase ( ).replace ( '_' , '-' );
	}
	
	private void consume ( PlayerInventory inventory , Predicate < ItemStack > filter , int to_consume ) {
		for ( int i = 0 ; i < inventory.getSize ( ) ; i++ ) {
			ItemStack item = inventory.getItem ( i );
			
			if ( item != null && filter.test ( item ) ) {
				int item_amount = item.getAmount ( );
				
				if ( item_amount > to_consume ) {
					item.setAmount ( item_amount - to_consume );
					break;
				} else if ( item_amount == to_consume ) {
					inventory.setItem ( i , null );
					break;
				} else {
					to_consume -= item_amount;
					
					inventory.setItem ( i , null );
				}
			}
		}
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}