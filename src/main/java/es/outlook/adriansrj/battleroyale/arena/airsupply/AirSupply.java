package es.outlook.adriansrj.battleroyale.arena.airsupply;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.drop.ItemDrop;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumLootContainer;
import es.outlook.adriansrj.battleroyale.enums.EnumMainConfiguration;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfiguration;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfigurationContainer;
import es.outlook.adriansrj.battleroyale.game.loot.LootConfigurationEntry;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.packet.sender.PacketSenderService;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.battleroyale.util.math.Location2I;
import es.outlook.adriansrj.battleroyale.util.reflection.bukkit.EntityReflection;
import es.outlook.adriansrj.core.player.PlayerWrapper;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import es.outlook.adriansrj.core.util.math.RandomUtil;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author AdrianSR / 13/10/2021 / 09:31 a. m.
 */
public class AirSupply {
	
	// TODO: in case it causes a lot of problems, a flag can be implemented, so if the flag is enabled, the beacon and its base
	//  will not be placed, making it be less demanding in terms of the landing place.
	
	protected static final String AIR_SUPPLY_META_KEY          = "air-supply-object";
	protected static final String AIR_SUPPLY_INSTANCE_META_KEY = "air-supply-instance";
	
	protected static final int    PARACHUTE_PARTS = 4;
	// 9 iron blocks + 1 beacon + 1 chest.
	protected static final int    RESTORE_BLOCKS  = 9 + 1 + 1;
	// falling speed
	protected static final double FALLING_SPEED   = 0.30;
	
	/**
	 * @author AdrianSR / 13/10/2021 / 06:19 p. m.
	 */
	protected static class AirSupplyFallTask extends BukkitRunnable {
		
		protected final AirSupply airsupply;
		
		protected AirSupplyFallTask ( AirSupply airsupply ) {
			this.airsupply = airsupply;
		}
		
		@Override
		public void run ( ) {
			ArmorStand chest_holder = airsupply.chest_holder;
			Block      land         = airsupply.land;
			
			// the chest holder has died unexpectedly
			if ( chest_holder == null ) {
				airsupply.stop ( );
				airsupply.destroy ( );
				return;
			}
			
			// we are using the eye location to check if the air supply has landed as it represents
			// the location of the head of the armor stand, which holds the helmet, that is
			// the visible part of the armor stand for players.
			Location location = chest_holder.getEyeLocation ( );
			int      chunk_x  = location.getBlockX ( ) >> 4;
			int      chunk_z  = location.getBlockZ ( ) >> 4;
			
			if ( chest_holder.getWorld ( ).isChunkLoaded ( chunk_x , chunk_z ) ) {
				if ( chest_holder.getEyeLocation ( ).getBlock ( ).getType ( ).isSolid ( ) ) {
					// stopping and placing loot chest
					airsupply.stop ( );
					airsupply.destroyShape ( );
					airsupply.placeLootChest ( );
					
					// landing sound
					land.getWorld ( ).playSound ( land.getLocation ( ) , Sound.valueOf (
							Version.getServerVersion ( ).isOlder ( Version.v1_13_R1 )
									? "BLOCK_CLOTH_FALL"
									: "BLOCK_SNOW_FALL" ) , 4F , 1F );
					
					land.getWorld ( ).playSound ( land.getLocation ( ) , Sound.BLOCK_LADDER_PLACE , 4F , 1F );
				} else {
					location = chest_holder.getLocation ( ).subtract ( 0D , FALLING_SPEED , 0D );
					
					// updating chest holder location.
					// a teleport packet for the chest holder seems to be unnecessary,
					// but if it starts causing  problems, then we will have to send teleport packets.
					EntityReflection.setPositionDirty ( chest_holder , location.toVector ( ) );
					
					// updating parachute location
					if ( airsupply.parachute != null ) {
						for ( int i = 0 ; i < airsupply.parachute.length ; i++ ) {
							BlockFace face = i < DirectionUtil.FACES_90.length ? DirectionUtil.FACES_90[ i ]
									: DirectionUtil.FACES_90[ i % DirectionUtil.FACES_90.length ].getOppositeFace ( );
							
							Chicken part = airsupply.parachute[ i ];
							double  x    = location.getX ( ) + ( face.getModX ( ) * 1.9D );
							double  y    = location.getY ( ) + 5.0D;
							double  z    = location.getZ ( ) + ( face.getModZ ( ) * 1.9D );
							float   yaw  = DirectionUtil.getYaw ( face );
							
							// we will update the server-side location of the
							// part as we will use it later.
							EntityReflection.setPositionDirty ( part , new Vector ( x , y , z ) );
							EntityReflection.setYawDirty ( part , yaw );
						}
					}
					
					// packets
					airsupply.arena.getPlayers ( ).stream ( ).map ( PlayerWrapper :: getBukkitPlayer ).filter (
							Objects :: nonNull ).forEach ( player -> {
						// parachute teleport packet
						if ( airsupply.parachute != null ) {
							for ( Chicken part : airsupply.parachute ) {
								Vector part_location = Objects.requireNonNull ( EntityReflection.getPositionDirty ( part ) );
								
								PacketSenderService.getInstance ( ).sendEntityTeleportPacket (
										player , EntityReflection.getEntityID ( part ) , false ,
										part_location.getX ( ) , part_location.getY ( ) , part_location.getZ ( ) ,
										EntityReflection.getYawDirty ( part ) , 0.0F );
								
								// ensuring is leashed
								PacketSenderService.getInstance ( ).sendEntityAttachPacket ( player , part , chest_holder );
							}
						}
					} );
				}
			} else {
				// loading chunk synchronously
				Bukkit.getScheduler ( ).runTask (
						BattleRoyale.getInstance ( ) , ( ) ->
								chest_holder.getWorld ( ).loadChunk ( chunk_x , chunk_z ) );
			}
		}
	}
	
	/**
	 * @author AdrianSR / 14/10/2021 / 09:51 a. m.
	 */
	protected static class BlockInfo {
		
		/**
		 * @author AdrianSR / 14/10/2021 / 09:51 a. m.
		 */
		interface BlockInfoHandle {
			
			void restore ( );
		}
		
		/**
		 * @author AdrianSR / 14/10/2021 / 10:00 a. m.
		 */
		@SuppressWarnings ( "deprecation" )
		static class BlockInfoHandle_v1_12 implements BlockInfoHandle {
			
			final Block        block;
			final Material     type;
			final MaterialData data;
			
			public BlockInfoHandle_v1_12 ( Block block ) {
				this.block = block;
				this.type  = block.getType ( );
				this.data  = block.getState ( ).getData ( );
			}
			
			@Override
			public void restore ( ) {
				block.setType ( type );
				
				BlockState state = block.getState ( );
				
				state.setData ( data );
				state.update ( true , false );
			}
		}
		
		/**
		 * @author AdrianSR / 14/10/2021 / 10:00 a. m.
		 */
		static class BlockInfoHandle_v1_13 implements BlockInfoHandle {
			
			final Block    block;
			final Material type;
			final String   data;
			
			public BlockInfoHandle_v1_13 ( Block block ) {
				this.block = block;
				this.type  = block.getType ( );
				this.data  = block.getState ( ).getBlockData ( ).getAsString ( );
			}
			
			@Override
			public void restore ( ) {
				block.setType ( type );
				
				BlockState state = block.getState ( );
				
				state.setBlockData ( Bukkit.getServer ( ).createBlockData ( data ) );
				state.update ( true , false );
			}
		}
		
		protected final BlockInfoHandle handle;
		
		public BlockInfo ( Block block ) {
			if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 ) ) {
				this.handle = new BlockInfoHandle_v1_13 ( block );
			} else {
				this.handle = new BlockInfoHandle_v1_12 ( block );
			}
		}
		
		public void restore ( ) {
			handle.restore ( );
		}
	}
	
	protected final BattleRoyaleArena arena;
	protected final Location2I        location;
	protected final Block             land;
	protected       Block[]           place;
	protected       ArmorStand        chest_holder;
	protected       Chicken[]         parachute;
	
	// falling task
	protected AirSupplyFallTask fall_task;
	
	public AirSupply ( BattleRoyaleArena arena , Location2I location ) {
		this.arena    = arena;
		this.location = location;
		
		// calculating block to
		Block ground = null;
		
		for ( int y = arena.getWorld ( ).getMaxHeight ( ) - 1 ; y >= 0 ; y-- ) {
			Block block = arena.getWorld ( ).getBlockAt ( location.getX ( ) , y , location.getZ ( ) );
			
			if ( block.getType ( ).isSolid ( ) || ( !block.isLiquid ( ) && !block.isEmpty ( ) ) ) {
				ground = block;
				break;
			}
		}
		
		this.land = ground;
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	public Location2I getLocation ( ) {
		return location;
	}
	
	/**
	 * Gets whether this air supply will land on a valid place.
	 *
	 * @return whether this air supply will land on a valid place.
	 */
	public boolean isValidPlace ( ) {
		return placeCheck ( );
	}
	
	/**
	 * Gets whether this air supply has started.
	 *
	 * @return whether this air supply is started.
	 */
	public boolean isStarted ( ) {
		return fall_task != null;
	}
	
	/**
	 * Gets whether this air supply has landed.
	 *
	 * @return whether this air supply is finished.
	 */
	public boolean isFinished ( ) {
		return fall_task != null && !fall_task.isCancelled ( );
	}
	
	public void start ( ) {
		if ( fall_task != null ) {
			throw new IllegalStateException ( "already started" );
		} else if ( arena.getState ( ) != EnumArenaState.RUNNING ) {
			throw new IllegalStateException ( "cannot start until arena is started" );
		}
		
		if ( isValidPlace ( ) ) {
			if ( spawn ( ) ) {
				this.fall_task = new AirSupplyFallTask ( this );
				this.fall_task.runTaskTimerAsynchronously ( BattleRoyale.getInstance ( ) , 1 , 1 );
			}
		} else {
			throw new IllegalStateException ( "not valid place" );
		}
	}
	
	public void stop ( ) {
		if ( fall_task != null ) {
			fall_task.cancel ( );
		} else {
			throw new IllegalStateException ( "never started" );
		}
	}
	
	/**
	 * Causes this air supply to explode and drop its contents.
	 *
	 * @param player the player who opens the air supply, or <b>null</b> if not opened by a player.
	 */
	public void open ( org.bukkit.entity.Player player ) {
		Validate.isTrue ( Bukkit.isPrimaryThread ( ) , "must run on server thread" );
		
		// dropping contents
		Block chest_block = place[ 10 ];
		
		if ( chest_block != null && chest_block.getState ( ) instanceof Chest ) {
			Inventory inventory = ( ( Chest ) chest_block.getState ( ) ).getBlockInventory ( );
			LootConfiguration loot_configuration = arena.getBattlefield ( ).getConfiguration ( )
					.getLootConfiguration ( );
			LootConfigurationContainer container = loot_configuration != null
					? loot_configuration.getContainer ( EnumLootContainer.AIR_SUPPLY ) : null;
			
			if ( container != null ) {
				BattleRoyaleArena arena = Player.getPlayer ( player ).getArena ( );
				Set < LootConfigurationEntry > contents = container.getRandomEntries ( Math.max ( RandomUtil.nextInt (
						( container.getMaximum ( ) * ( inventory.getSize ( ) / 9 ) ) + 1 ) , 1 ) );
				
				// dropping
				contents.stream ( )
						.map ( entry -> player != null ? entry.toItemStack ( player ) : entry.toItemStack ( ) )
						.filter ( Objects :: nonNull )
						.forEach ( item -> {
							Item instance = chest_block.getWorld ( ).dropItem (
									chest_block.getLocation ( ).add ( 0.5 , 0.5 , 0.5 ) , item );
							
							// enhanced drop
							if ( EnumMainConfiguration.GAME_ENHANCED_DROPS_ENABLE.getAsBoolean ( )
									&& EnumMainConfiguration.GAME_ENHANCED_DROPS_LOOT_CONTAINER_ONLY.getAsBoolean ( )
									&& arena != null ) {
								arena.getDropManager ( ).register ( new ItemDrop ( instance , arena ) );
							}
						} );
			}
		}
		
		// destroying
		destroy ( );
		
		// explode effect
		ParticleEffect.LAVA.display (
				land.getLocation ( ).add ( 0.5D , 1.0D , 0.5D ) ,
				2F , 2F , 2F , 0.2F , 150 , null );
	}
	
	protected boolean spawn ( ) {
		return placeBeacon ( ) && spawnChestHolder ( ) && spawnParachute ( );
	}
	
	public void destroy ( ) {
		destroyShape ( );
		
		// restoring block original types
		for ( Block block : place ) {
			if ( block != null && block.hasMetadata ( AIR_SUPPLY_META_KEY ) ) {
				Object raw_info = block.getMetadata ( AIR_SUPPLY_META_KEY ).get ( 0 ).value ( );
				
				if ( raw_info instanceof BlockInfo ) {
					( ( BlockInfo ) raw_info ).restore ( );
				}
				
				// removing metadata
				block.removeMetadata ( AIR_SUPPLY_META_KEY , BattleRoyale.getInstance ( ) );
				block.removeMetadata ( AIR_SUPPLY_INSTANCE_META_KEY , BattleRoyale.getInstance ( ) );
			}
		}
	}
	
	protected void destroyShape ( ) {
		// removing chest holder
		if ( chest_holder != null ) {
			chest_holder.remove ( );
			chest_holder = null;
		}
		
		// removing parachute
		PacketSenderService packet_service = PacketSenderService.getInstance ( );
		
		if ( parachute != null ) {
			arena.getPlayers ( ).stream ( ).map ( PlayerWrapper :: getBukkitPlayerOptional ).filter (
					Optional :: isPresent ).map ( Optional :: get ).forEach ( player -> {
				for ( Chicken part : parachute ) {
					if ( part != null ) {
						packet_service.sendDestroyEntityPacket ( player , part.getEntityId ( ) );
						
						// we will display a cloud particle effect
						// to give the impression that it disappeared.
						ParticleEffect.CLOUD.display (
								Objects.requireNonNull ( EntityReflection.getPositionDirty ( part ) )
										.toLocation ( arena.getWorld ( ) ) ,
								0.3F , 0.3F , 0.3F , 0.2F , 10 , null );
					}
				}
			} );
			
			parachute = null;
		}
	}
	
	/**
	 * Request the setup fo the beacon.
	 *
	 * @return false if the beacon has already been placed.
	 */
	protected boolean placeBeacon ( ) {
		if ( placeCheck ( ) ) {
			for ( int i = 0 ; i < place.length - 1 ; i++ ) {
				// iron block
				if ( i < place.length - 2 ) {
					Block iron_block = place[ i ];
					
					iron_block.setMetadata ( AIR_SUPPLY_META_KEY , new FixedMetadataValue (
							BattleRoyale.getInstance ( ) , new BlockInfo ( iron_block ) ) );
					iron_block.setType ( UniversalMaterial.IRON_BLOCK.getMaterial ( ) );
					
					continue;
				}
				
				// beacon block
				this.land.setMetadata ( AIR_SUPPLY_META_KEY , new FixedMetadataValue (
						BattleRoyale.getInstance ( ) , new BlockInfo ( land ) ) );
				this.land.setType ( UniversalMaterial.BEACON.getMaterial ( ) );
				
				this.place[ i ] = land;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks whether the place where this air supply
	 * will land is valid.
	 *
	 * @return whether this air supply will land on a valid place.
	 */
	protected boolean placeCheck ( ) {
		if ( this.land != null ) {
			this.place = new Block[ RESTORE_BLOCKS ];
			
			// iron blocks
			this.place[ 0 ] = land.getRelative ( BlockFace.DOWN );
			this.place[ 1 ] = land.getRelative ( BlockFace.DOWN ).getRelative ( BlockFace.EAST );
			this.place[ 2 ] = land.getRelative ( BlockFace.DOWN ).getRelative ( BlockFace.WEST );
			this.place[ 3 ] = land.getRelative ( BlockFace.DOWN ).getRelative ( BlockFace.NORTH );
			this.place[ 4 ] = land.getRelative ( BlockFace.DOWN ).getRelative ( BlockFace.SOUTH );
			this.place[ 5 ] = land.getRelative (
					BlockFace.DOWN ).getRelative ( BlockFace.EAST ).getRelative ( BlockFace.NORTH );
			this.place[ 6 ] = land.getRelative (
					BlockFace.DOWN ).getRelative ( BlockFace.EAST ).getRelative ( BlockFace.SOUTH );
			this.place[ 7 ] = land.getRelative (
					BlockFace.DOWN ).getRelative ( BlockFace.WEST ).getRelative ( BlockFace.SOUTH );
			this.place[ 8 ] = land.getRelative (
					BlockFace.DOWN ).getRelative ( BlockFace.WEST ).getRelative ( BlockFace.NORTH );
			
			// beacon
			this.place[ 9 ] = land;
			
			// loot chest
			this.place[ 10 ] = land.getRelative ( BlockFace.UP );
			
			// checking
			for ( int i = 0 ; i < place.length ; i++ ) {
				Block block = this.place[ i ];
				
				if ( i < this.place.length - 2
						&& ( !block.getRelative ( BlockFace.UP ).getType ( ).isOccluding ( )
						|| !block.getRelative ( BlockFace.DOWN ).getType ( ).isOccluding ( )
						|| !block.getRelative ( BlockFace.NORTH ).getType ( ).isOccluding ( )
						|| !block.getRelative ( BlockFace.SOUTH ).getType ( ).isOccluding ( )
						|| !block.getRelative ( BlockFace.WEST ).getType ( ).isOccluding ( )
						|| !block.getRelative ( BlockFace.EAST ).getType ( ).isOccluding ( ) ) ) {
					// the iron block must be surrounded by
					// occluding blocks, so players will not see them.
					return false;
				}
				
				if ( block.getY ( ) < 0 ) {
					this.place = null;
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Request the chest holder to be spawned.
	 *
	 * @return false if the chest holder has already been spawned.
	 */
	protected boolean spawnChestHolder ( ) {
		if ( chest_holder == null ) {
			chest_holder = land.getWorld ( ).spawn ( calculateSpawnLocation ( ) , ArmorStand.class );
			
			chest_holder.setGravity ( false );
			chest_holder.setVisible ( false );
			chest_holder.getEquipment ( ).setHelmet ( UniversalMaterial.CHEST.getItemStack ( ) );
			chest_holder.setInvulnerable ( true );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Request the loot chest to be spawned.
	 */
	protected void placeLootChest ( ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			Block chest_block = place[ 10 ];
			
			if ( chest_block != null ) {
				chest_block.setMetadata ( AIR_SUPPLY_META_KEY , new FixedMetadataValue (
						BattleRoyale.getInstance ( ) , new BlockInfo ( chest_block ) ) );
				chest_block.setMetadata ( AIR_SUPPLY_INSTANCE_META_KEY , new FixedMetadataValue (
						BattleRoyale.getInstance ( ) , this ) );
				
				chest_block.setType ( UniversalMaterial.CHEST.getMaterial ( ) );
				chest_block.getState ( ).update ( true , false );
			}
		} else {
			// running on server primary thread.
			Bukkit.getScheduler ( ).runTask (
					BattleRoyale.getInstance ( ) , this :: placeLootChest );
		}
	}
	
	/**
	 * Request the parachute to be spawned.
	 *
	 * @return false if the parachute has already been spawned,
	 * the chest holder has never been spawned, or if the holder has died.
	 */
	protected boolean spawnParachute ( ) {
		if ( chest_holder != null && parachute == null ) {
			this.parachute = new Chicken[ PARACHUTE_PARTS ];
			
			PacketSenderService packet_service = PacketSenderService.getInstance ( );
			// we take the chest holder eye location at the spawn location as the
			// parachute might be spawned too late due to chunk usage.
			Location spawn_location = chest_holder.getEyeLocation ( );
			
			for ( int i = 0 ; i < parachute.length ; i++ ) {
				BlockFace face = i < DirectionUtil.FACES_90.length ? DirectionUtil.FACES_90[ i ]
						: DirectionUtil.FACES_90[ i % DirectionUtil.FACES_90.length ].getOppositeFace ( );
				
				double x   = spawn_location.getX ( ) + ( face.getModX ( ) * 1.9D );
				double y   = spawn_location.getY ( );
				double z   = spawn_location.getZ ( ) + ( face.getModZ ( ) * 1.9D );
				float  yaw = DirectionUtil.getYaw ( face );
				
				Chicken part = ( Chicken ) packet_service.spawnEntity (
						EntityType.CHICKEN , x , y , z , yaw , 0.0F , entity -> {
							EntityReflection.setSilent ( entity , true );
							EntityReflection.setInvulnerable ( entity , true );
							entity.setMetadata ( AIR_SUPPLY_INSTANCE_META_KEY , new FixedMetadataValue (
									BattleRoyale.getInstance ( ) , AirSupply.this ) );
						} );
				
				this.parachute[ i ] = part;
				
				// showing and leashing
				arena.getPlayers ( ).stream ( ).map ( PlayerWrapper :: getBukkitPlayerOptional )
						.filter ( Optional :: isPresent ).map ( Optional :: get )
						.forEach ( player -> SchedulerUtil.scheduleSyncDelayedTask ( ( ) -> {
							// showing/spawning
							packet_service.sendSpawnEntityPacket ( player , part );
							packet_service.sendEntityMetadataPacket ( player , part );
							// leashing
							packet_service.sendEntityAttachPacket ( player , part , chest_holder );
						} ) );
			}
			return true;
		} else {
			return false;
		}
	}
	
	protected Location calculateSpawnLocation ( ) {
		Location spawn = land.getLocation ( );
		
		spawn.setX ( spawn.getX ( ) + 0.5D ); // centering
		spawn.setY ( spawn.getWorld ( ).getMaxHeight ( ) - 1 );
		spawn.setZ ( spawn.getZ ( ) + 0.5D ); // centering
		
		return spawn;
	}
}