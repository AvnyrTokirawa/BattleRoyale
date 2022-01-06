package es.outlook.adriansrj.battleroyale.arena.sign;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.configuration.arena.BattleRoyaleArenaSignContainerHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumArenaState;
import es.outlook.adriansrj.battleroyale.enums.EnumLanguage;
import es.outlook.adriansrj.battleroyale.enums.EnumSignConfiguration;
import es.outlook.adriansrj.battleroyale.event.arena.ArenaStateChangeEvent;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobbyHandler;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.battleroyale.util.Validate;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.util.EventUtil;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.math.Vector3I;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author AdrianSR / 04/09/2021 / 12:25 p. m.
 */
public final class BattleRoyaleArenaSignHandler extends PluginHandler {
	
	public static BattleRoyaleArenaSignHandler getInstance ( ) {
		return getPluginHandler ( BattleRoyaleArenaSignHandler.class );
	}
	
	private final Map < Vector3I, BattleRoyaleArenaSign > sign_map = new HashMap <> ( );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public BattleRoyaleArenaSignHandler ( BattleRoyale plugin ) {
		super ( plugin );
		register ( );
	}
	
	public Collection < BattleRoyaleArenaSign > getSigns ( ) {
		return Collections.unmodifiableCollection ( sign_map.values ( ) );
	}
	
	public Stream < BattleRoyaleArenaSign > getSignsByArena ( BattleRoyaleArena arena ) {
		return sign_map.values ( ).stream ( ).filter ( sign -> Objects.equals ( arena , sign.getArena ( ) ) );
	}
	
	public Optional < BattleRoyaleArenaSign > getSign ( Vector3I location ) {
		return Optional.ofNullable ( sign_map.get ( location ) );
	}
	
	public Optional < BattleRoyaleArenaSign > getSign ( Vector location ) {
		return getSign ( new Vector3I ( location.getBlockX ( ) , location.getBlockY ( ) , location.getBlockZ ( ) ) );
	}
	
	public BattleRoyaleArenaSign createSign ( Vector3I location , BlockFace direction , BattleRoyaleArena arena ) {
		return registerSign ( new BattleRoyaleArenaSign ( location , direction , arena ) );
	}
	
	public BattleRoyaleArenaSign createSign ( Vector location , BlockFace direction , BattleRoyaleArena arena ) {
		return createSign ( new Vector3I (
				location.getBlockX ( ) , location.getBlockY ( ) , location.getBlockZ ( ) ) , direction , arena );
	}
	
	public BattleRoyaleArenaSign registerSign ( BattleRoyaleArenaSign sign ) {
		Validate.notNull ( sign , "sign cannot be null" );
		Validate.isTrue ( sign.isValid ( ) , "sign must be valid" );
		
		// placing
		placeSign ( sign );
		
		// then registering
		sign_map.put ( sign.getLocation ( ) , sign );
		return sign;
	}
	
	public BattleRoyaleArenaSign unregisterSign ( BattleRoyaleArenaSign sign ) {
		sign_map.remove ( sign.getLocation ( ) );
		return sign;
	}
	
	public BattleRoyaleArenaSign unregisterSign ( Vector3I location ) {
		return sign_map.remove ( location );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
	
	// handler responsible for handling the interaction
	// of player with battle royale signs.
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onSignClick ( PlayerInteractEvent event ) {
		Block      block  = event.getClickedBlock ( );
		BlockState state  = block != null ? block.getState ( ) : null;
		Player     player = event.getPlayer ( );
		
		if ( state instanceof Sign ) {
			BattleRoyaleArenaSign sign = getSign ( block.getLocation ( ).toVector ( ) ).orElse ( null );
			
			if ( sign != null ) {
				if ( EventUtil.isLeftClick ( event.getAction ( ) )
						&& player.getGameMode ( ) == GameMode.CREATIVE ) {
					event.setCancelled ( true );
					
					// the sign is being removed
					block.setType ( UniversalMaterial.AIR.getMaterial ( ) );
					
					unregisterSign ( sign );
					// saving
					BattleRoyaleArenaSignContainerHandler.getInstance ( ).save ( );
				} else {
					BattleRoyaleArenaHandler.getInstance ( ).joinArena ( player , sign.getArena ( ) );
				}
			}
		}
	}
	
	// handler responsible updating the signs the
	// arena tha changes its state.
	@EventHandler ( priority = EventPriority.MONITOR )
	public void onUpdate ( ArenaStateChangeEvent event ) {
		Bukkit.getScheduler ( ).runTask ( BattleRoyale.getInstance ( ) , ( ) -> getSignsByArena (
				event.getArena ( ) ).forEach ( sign -> updateSign ( sign , event.getState ( ) ) ) );
	}
	
	// handler responsible for the creation of new
	// battle royale signs.
	@EventHandler ( priority = EventPriority.HIGHEST )
	public void onSetupSign ( SignChangeEvent event ) {
		Block             block   = event.getBlock ( );
		BattleRoyaleArena arena   = null;
		boolean           br_sign = false;
		
		// finding out arena
		for ( String line : event.getLines ( ) ) {
			if ( line != null && ( line = line.trim ( ) ).toLowerCase ( )
					.startsWith ( Constants.ARENA_SIGN_KEYWORD ) ) {
				br_sign = true;
				arena   = BattleRoyaleArenaHandler.getInstance ( )
						.getArena ( line.replaceFirst ( Constants.ARENA_SIGN_KEYWORD , "" ).trim ( ) )
						.orElse ( null );
				break;
			}
		}
		
		if ( br_sign ) {
			if ( arena != null ) {
				// facing direction
				BlockFace facing;
				
				if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 ) ) {
					facing = ( ( Directional ) block.getBlockData ( ) ).getFacing ( );
				} else {
					facing = ( ( org.bukkit.material.Sign ) block.getState ( ).getData ( ) ).getFacing ( );
				}
				
				// the creating.
				// sign must be registered the next tick,
				// otherwise, the SignChangeEvent will override
				// the changes.
				BattleRoyaleArena final_arena = arena;
				
				Bukkit.getScheduler ( ).scheduleSyncDelayedTask ( BattleRoyale.getInstance ( ) , ( ) -> {
					createSign ( block.getLocation ( ).toVector ( ) , facing , final_arena );
					
					// saving
					BattleRoyaleArenaSignContainerHandler.getInstance ( ).save ( );
				} );
			} else {
				// unknown arena
				Arrays.fill ( event.getLines ( ) , StringUtil.EMPTY );
				
				event.setLine ( 0 , ChatColor.DARK_RED + "Unknown Arena" );
				event.setLine ( 2 , ChatColor.DARK_RED + "Self Destroying" );
				event.setLine ( 3 , ChatColor.DARK_RED + "......" );
				
				Bukkit.getScheduler ( ).scheduleSyncDelayedTask ( plugin , ( ) -> {
					if ( block.getState ( ) instanceof Sign ) {
						block.getWorld ( ).playEffect ( block.getLocation ( ) ,
														Effect.STEP_SOUND , block.getType ( ) );
						
						block.setType ( UniversalMaterial.AIR.getMaterial ( ) );
						block.getState ( ).update ( );
					}
				} , 20L );
			}
		}
	}
	
	// ---- utils
	
	@SuppressWarnings ( "deprecation" )
	private void placeSign ( BattleRoyaleArenaSign sign ) {
		// signs are placed only in the lobby at the moment, and unless
		// people ask for a change in this system, the lobby will be
		// the only place for the signs.
		Block     block     = getSignBlock ( sign );
		BlockFace direction = sign.getFacingDirection ( );
		boolean   wall      = block.getRelative ( direction.getOppositeFace ( ) ).getType ( ).isBlock ( );
		
		// we will have to make direct reference to
		// WALL_SIGN/SIGN materials as the UniversalMaterial
		// returns a LEGACY material which doesn't actually work,
		// as the backwards compatibility system of Spigot is a disaster
		Material sign_material = null;
		
		for ( Material other : Material.values ( ) ) {
			if ( !other.name ( ).contains ( "LEGACY" )
					&& other.name ( ).endsWith ( wall ? "WALL_SIGN" : "SIGN" ) ) {
				sign_material = other;
				break;
			}
		}
		
		if ( sign_material != null && wall ) {
			block.setType ( sign_material );
		} else if ( sign_material != null && block.getRelative ( BlockFace.DOWN ).getType ( ).isBlock ( ) ) {
			block.setType ( sign_material );
		} else {
			// cannot place as there is not a wall behind,
			// and there is not a solid ground below.
			return;
		}
		
		Sign handle = ( Sign ) block.getState ( );
		
		// facing direction
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_13_R1 ) ) {
			org.bukkit.block.data.BlockData data = block.getBlockData ( );
			
			if ( data instanceof org.bukkit.block.data.type.WallSign ) {
				( ( org.bukkit.block.data.type.WallSign ) data ).setFacing ( direction );
			} else if ( data instanceof org.bukkit.block.data.type.Sign ) {
				( ( org.bukkit.block.data.type.Sign ) data ).setRotation ( direction );
			}
		} else {
			// legacy versions
			org.bukkit.material.Sign material = new org.bukkit.material.Sign ( block.getType ( ) );
			
			material.setFacingDirection ( sign.getFacingDirection ( ) );
			handle.setData ( material );
			handle.update ( true );
		}
		
		// display text
		setFormattedText ( handle , sign.getArenaName ( ) , getSignStateText ( sign.getArena ( ).getState ( ) ) );
	}
	
	private void updateSign ( BattleRoyaleArenaSign sign , EnumArenaState state ) {
		Block block = getSignBlock ( sign );
		
		if ( block.getState ( ) instanceof Sign ) {
			setFormattedText ( ( Sign ) block.getState ( ) , sign.getArenaName ( ) , getSignStateText ( state ) );
		} else {
			placeSign ( sign );
		}
		
		if ( EnumSignConfiguration.STATUS_BLOCK_ENABLE.getAsBoolean ( )
				&& !sign.isDisableStatusBlock ( ) ) {
			updateStatusBlock ( sign , state );
		}
	}
	
	private void updateStatusBlock ( BattleRoyaleArenaSign sign , EnumArenaState state ) {
		Block block  = getSignBlock ( sign );
		Block status = block.getRelative ( sign.getFacingDirection ( ).getOppositeFace ( ) );
		
		if ( !( block.getState ( ) instanceof Sign ) ) {
			placeSign ( sign );
		}
		
		status.setType ( getSignStateMaterial ( state ).getMaterial ( ) );
		
		if ( Version.getServerVersion ( ).isOlder ( Version.v1_13_R1 ) ) {
			try {
				Block.class.getMethod ( "setData" , byte.class )
						.invoke ( status , ( byte ) getSignStateData ( state ) );
			} catch ( NoSuchMethodException | InvocationTargetException | IllegalAccessException e ) {
				e.printStackTrace ( );
			}
		}
		
		status.getState ( ).update ( );
	}
	
	private Block getSignBlock ( BattleRoyaleArenaSign sign ) {
		Validate.isTrue ( Bukkit.isPrimaryThread ( ) , "must run on server thread" );
		
		Vector3I location = sign.getLocation ( );
		
		return BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( ).getWorld ( )
				.getBlockAt ( location.getX ( ) , location.getY ( ) , location.getZ ( ) );
	}
	
	private void setFormattedText ( Sign handle , String arena_name , EnumLanguage text ) {
		String[] text_lines = text.getAsString ( ).split ( "\n" );
		
		for ( int i = 0 ; i < text_lines.length ; i++ ) {
			String line = text_lines[ i ];
			
			if ( line != null ) {
				text_lines[ i ] = String.format ( line , arena_name );
			}
		}
		
		setText ( handle , text_lines );
	}
	
	private void setText ( Sign handle , String[] text ) {
		for ( int i = 0 ; i < handle.getLines ( ).length ; i++ ) {
			if ( i < text.length ) {
				handle.setLine ( i , text[ i ] );
			} else {
				handle.setLine ( i , StringUtil.EMPTY );
			}
		}
		
		handle.update ( true );
	}
	
	private EnumLanguage getSignStateText ( EnumArenaState state ) {
		switch ( state ) {
			case WAITING:
				return EnumLanguage.ARENA_SIGN_WAITING_TEXT;
			case RUNNING:
				return EnumLanguage.ARENA_SIGN_RUNNING_TEXT;
			case RESTARTING:
				return EnumLanguage.ARENA_SIGN_RESTARTING_TEXT;
			case STOPPED:
				return EnumLanguage.ARENA_STATE_STOPPED_WORD;
		}
		
		throw new IllegalStateException ( );
	}
	
	private UniversalMaterial getSignStateMaterial ( EnumArenaState state ) {
		String material_name;
		
		switch ( state ) {
			case WAITING:
				material_name = EnumSignConfiguration.STATUS_BLOCK_WAITING_MATERIAL.getAsString ( );
				break;
			case RUNNING:
				material_name = EnumSignConfiguration.STATUS_BLOCK_RUNNING_MATERIAL.getAsString ( );
				break;
			case RESTARTING:
				material_name = EnumSignConfiguration.STATUS_BLOCK_RESTARTING_MATERIAL.getAsString ( );
				break;
			case STOPPED:
				material_name = EnumSignConfiguration.STATUS_BLOCK_STOPPED_MATERIAL.getAsString ( );
				break;
			default:
				throw new IllegalStateException ( );
		}
		
		return StringUtil.isBlank ( material_name ) ? null : UniversalMaterial.match ( material_name );
	}
	
	private int getSignStateData ( EnumArenaState state ) {
		switch ( state ) {
			case WAITING:
				return EnumSignConfiguration.STATUS_BLOCK_WAITING_DATA.getAsInteger ( );
			case RUNNING:
				return EnumSignConfiguration.STATUS_BLOCK_RUNNING_DATA.getAsInteger ( );
			case RESTARTING:
				return EnumSignConfiguration.STATUS_BLOCK_RESTARTING_DATA.getAsInteger ( );
			case STOPPED:
				return EnumSignConfiguration.STATUS_BLOCK_STOPPED_DATA.getAsInteger ( );
		}
		
		throw new IllegalStateException ( );
	}
}