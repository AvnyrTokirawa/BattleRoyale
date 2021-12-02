package es.outlook.adriansrj.battleroyale.arena.listener;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.battlefield.bus.BusSpawn;
import es.outlook.adriansrj.battleroyale.bus.test.BusPetInstanceTest;
import es.outlook.adriansrj.battleroyale.bus.test.BusPetTest;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteRegistry;
import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustom;
import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustomModel;
import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustomModelPartShape;
import es.outlook.adriansrj.battleroyale.parachute.test.ParachuteTest;
import es.outlook.adriansrj.battleroyale.parachute.test.ParachuteTestInstance;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.math.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * @author AdrianSR / 28/11/2021 / 08:54 a. m.
 */
public final class TestCommandsListener extends BattleRoyaleArenaListener {
	
	ParachuteCustomModel aladelta_model = new ParachuteCustomModel.Builder ( )
			// part 0
			.withPart ( 0.96F , 3.15F , 0.3F ,
						230.0F , -90.0F , 0.0F ,
						new ParachuteCustomModelPartShape (
								UniversalMaterial.WHITE_BANNER ,
								Parachute.Color.PLAYER
						) )
			// part 1
			.withPart ( 1.8F , 3.15F , -0.4F ,
						230.0F , -90.0F , 0.0F ,
						new ParachuteCustomModelPartShape (
								UniversalMaterial.WHITE_BANNER ,
								Parachute.Color.PLAYER
						) )
			// part 2
			.withPart ( -0.96F , 3.15F , 0.3F ,
						-230.0F , -90.0F , 0.0F ,
						new ParachuteCustomModelPartShape (
								UniversalMaterial.WHITE_BANNER ,
								Parachute.Color.PLAYER
						) )
			// part 3
			.withPart ( -1.8F , 3.15F , -0.4F ,
						-230.0F , -90.0F , 0.0F ,
						new ParachuteCustomModelPartShape (
								UniversalMaterial.WHITE_BANNER ,
								Parachute.Color.PLAYER
						) )
			// part 4
			.withPart ( -1.45F , 3.15F , -0.82F ,
						-230.0F , -90.0F , 0.0F ,
						new ParachuteCustomModelPartShape (
								UniversalMaterial.WHITE_BANNER ,
								Parachute.Color.PLAYER
						) )
			// part 5
			.withPart ( 1.45F , 3.15F , -0.82F ,
						230.0F , -90.0F , 0.0F ,
						new ParachuteCustomModelPartShape (
								UniversalMaterial.WHITE_BANNER ,
								Parachute.Color.PLAYER
						) )
			.build ( );
	
	public TestCommandsListener ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@EventHandler
	public void parachute ( PlayerCommandPreprocessEvent event ) {
		if ( event.getMessage ( ).startsWith ( "/prt" ) ) {
			String[] args = event.getMessage ( ).split ( " " );
			
			if ( args.length > 1 ) {
				int count = Integer.parseInt ( args[ 1 ] );
				
				event.getPlayer ( ).sendMessage ( ChatColor.GOLD + "Spawning " + count + " parachutes" );
				
				// model
				ParachuteCustomModel model = null;
				
				for ( Parachute parachute : ParachuteRegistry.getInstance ( ).getRegisteredParachutes ( ) ) {
					if ( parachute instanceof ParachuteCustom
							&& ( model == null || Math.random ( ) > 0.5D ) ) {
						model = ( ( ParachuteCustom ) parachute ).getModel ( );
					}
				}
				
				if ( model == null ) {
					model = aladelta_model;
				}
				
				// spawning
				for ( int i = 0 ; i < count ; i++ ) {
					float    yaw   = ( float ) ( Math.random ( ) * 360.0F );
					Location spawn = event.getPlayer ( ).getLocation ( );
					
					spawn.setYaw ( yaw );
					
					ParachuteTest parachute = new ParachuteTest ( model );
					ParachuteTestInstance instance = parachute.createInstance (
							Player.getPlayer ( event.getPlayer ( ) ).getArena ( ) ,
							spawn );
					
					instance.open ( );
				}
			}
		}
	}
	
	@EventHandler
	public void bus ( PlayerCommandPreprocessEvent event ) {
		if ( event.getMessage ( ).startsWith ( "/brt" ) ) {
			String[] args = event.getMessage ( ).split ( " " );
			
			if ( args.length > 1 ) {
				int count = Integer.parseInt ( args[ 1 ] );
				
				event.getPlayer ( ).sendMessage ( ChatColor.GOLD + "Spawning " + count + " buses" );
				
				// model
				EntityType[] shapes = {
						EntityType.HORSE ,
						EntityType.SKELETON_HORSE ,
						EntityType.CHICKEN ,
						EntityType.PIG
				};
				
				// spawning
				for ( int i = 0 ; i < count ; i++ ) {
					BattleRoyaleArena arena = Player.getPlayer ( event.getPlayer ( ) ).getArena ( );
					Location          spawn = event.getPlayer ( ).getLocation ( );
					
					BusPetTest         pet      = new BusPetTest ( RandomUtil.getRandomElement ( shapes ) );
					BusPetInstanceTest instance = pet.createInstance ( arena , spawn );
					
					instance.start (
							Player.getPlayer ( event.getPlayer ( ) ).getArena ( ) ,
							new BusSpawn ( arena.getFullBounds ( ).unproject ( spawn.toVector ( ) ) ,
										   spawn.getYaw ( ) , 15 , 1.2D ) );
				}
			}
		}
	}
}
