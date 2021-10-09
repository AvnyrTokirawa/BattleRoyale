package es.outlook.adriansrj.battleroyale.main;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArenaHandler;
import es.outlook.adriansrj.battleroyale.configuration.ConfigurationHandler;
import es.outlook.adriansrj.battleroyale.data.DataStorageHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.enums.EnumPluginHandler;
import es.outlook.adriansrj.battleroyale.gui.arena.ArenaSelectorGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.setting.SettingsGUIHandler;
import es.outlook.adriansrj.battleroyale.gui.team.TeamSelectorGUIHandler;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobby;
import es.outlook.adriansrj.battleroyale.lobby.BattleRoyaleLobbyHandler;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.dependency.MavenDependency;
import es.outlook.adriansrj.core.handler.PluginHandler;
import es.outlook.adriansrj.core.player.PlayerWrapper;
import es.outlook.adriansrj.core.plugin.Plugin;
import es.outlook.adriansrj.core.plugin.PluginAdapter;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.packet.PacketAdapter;
import es.outlook.adriansrj.core.util.packet.PacketChannelHandler;
import es.outlook.adriansrj.core.util.packet.PacketEvent;
import es.outlook.adriansrj.core.util.packet.PacketListener;
import es.outlook.adriansrj.core.util.reflection.bukkit.BukkitReflection;
import es.outlook.adriansrj.core.util.scheduler.SchedulerUtil;
import net.minecraft.server.v1_12_R1.PacketPlayOutOpenSignEditor;
import org.apache.commons.io.FileDeleteStrategy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;

/**
 * Battle royale plugin main class.
 *
 * @author AdrianSR / 22/08/2021 / Time: 05:53 p. m.
 */
public final class BattleRoyale extends PluginAdapter implements Listener {
	
	// TODO: debido a que no es precisamente optimo esperar a que todas la arenas en un mundo en especifico terminen
	// TODO: para volver a crearlo y empezarlas; creo que lo ideal seria que cada arena este en una region diferente.
	// TODO: Osea, cada arena tendra un archivo region.mca distinto, de modo que a la hora de crear una nueva
	//  instancia,
	// TODO: de arena, se crea un archivo region completamente nuevo, en coordendas no-ocupadas para asi evitar
	//  problemas
	// TODO: de concurrencia debido a que el servidor esta constantemente accediendo a los archivos region que
	//  almacenan
	// TODO: chunks cargados por el servidor.
	
	// TODO: world/schematics for setting up battlefields will be loaded from the directory battlefield/input.
	// TODO:
	//  -----------------------------------------------------------------------------------------------------------
	
	// TODO: the player will be able to setup a battlefield from a world/schematic. the resulting battlefield will be
	// TODO: saved into the directory battlefield (it's configuration will be saved in a .yml file with the same
	// TODO: name in the same directory, the same for it's minimap image).
	// TODO:
	//  -----------------------------------------------------------------------------------------------------------
	
	// TODO: there can be several loot configurations (in the directory loot).
	// TODO:
	//  -----------------------------------------------------------------------------------------------------------
	
	// TODO: by default, a given battlefield will take the first loot configuration that finds in the loot directory,
	// TODO: the player who configures the battlefield can choose what loot configuration the battlefield will use
	//  though.
	// TODO:
	//  -----------------------------------------------------------------------------------------------------------
	
	// TODO: there will be a section per loot container (initial, chest, air-suppl, etc...). each entry can have a
	//  name, material, data, etc... (just like any other item stack), but if the key "plugin-object" is specified,
	//  then the ordinary item stack values will be ignored, as the plugin will copy that object from another plugins,
	//  or from itself (bridge-eggs, medkits, etc..). the key "required" are the required loot entries that are to be
	//  added together with the entry. the "required" entry can be either a section or an string list. if it is a
	//  section, then the entries inside will be loaded just like any other loot entry. but if it is a string list,
	//  then the format will be the following:
	//  .
	//  required:
	//  - '[plugin-object or another entry]:[amount]'
	//  .
	//  example:
	//  .
	//  required:
	//  - '9mm:8'
	
	// TODO: to create an arena, the plugin will paste the schematic that correspond to the battlefield into the world
	// TODO: being loaded.
	// TODO:
	//  -----------------------------------------------------------------------------------------------------------
	
	// TODO: there can be several arena-worlds. the maximum number of arena-worlds that can be created will be
	//  configurable from the main configuration.
	// TODO:
	//  -----------------------------------------------------------------------------------------------------------
	
	// TODO: the cardinal bar will no longer be updated from a synchronous task, but instead, from a asynchronous task,
	// TODO: regardless of if it is required to use nms or not to achieve this.
	// TODO:
	//  -----------------------------------------------------------------------------------------------------------
	
	// TODO: try to render the minimap from a asynchronous task, regardless of if it is required to use nms or not to
	// TODO: achieve this.
	// TODO:
	//  -----------------------------------------------------------------------------------------------------------
	
	// TODO: the bus will now be a dragon or a ghast which will hold all the players in the arena.
	// TODO:
	//  -----------------------------------------------------------------------------------------------------------
	
	// TODO: the parachute will be the same as the one in the previous versions of battle royale.
	// TODO:
	//  -----------------------------------------------------------------------------------------------------------
	
	// TODO: loot will be found around the map in chests or on the ground (add support for good on-ground-items
	//  plugins)
	// TODO:
	//  -----------------------------------------------------------------------------------------------------------
	
	// TODO: since a world can only have one border, it will be necessary to update it through packets.
	// TODO: don't you lower the price of the plugin Adrian, this is so fucking complicated.
	// TODO:
	//  -----------------------------------------------------------------------------------------------------------
	
	public static BattleRoyale getInstance ( ) {
		return Plugin.getPlugin ( BattleRoyale.class );
	}
	
	@Override
	public MavenDependency[] getLibraries ( ) {
		return new MavenDependency[] {
				new MavenDependency ( "com.zaxxer:HikariCP:3.4.5" ) ,
				new MavenDependency ( "org.mongodb:mongodb-driver-sync:4.2.1" ) ,
				new MavenDependency ( "net.kyori:adventure-nbt:4.8.1" ) ,
				new MavenDependency ( "xyz.xenondevs:particle:1.6.4" ) ,
		};
	}
	
	static Thread SERVER_THREAD;
	
	@Override
	protected boolean setUp ( ) {
		Bukkit.getScheduler ( ).runTask ( this , ( ) -> { SERVER_THREAD = Thread.currentThread ( ); } );
		
		ConsoleUtil.sendPluginMessage ( ChatColor.GREEN ,
										"BattleRoyale enabled!" , this );
		
		// cleaning temp folder
		deleteTempFolder ( );
		
		//		final Map < UUID, BossBar > map = new HashMap <> ( );
		
		//		Bukkit.getScheduler ( ).runTaskTimerAsynchronously ( this , new Runnable ( ) {
		//			@Override
		//			public void run ( ) {
		//				for ( org.bukkit.entity.Player player : Bukkit.getOnlinePlayers ( ) ) {
		//					BossBar bar = map.get ( player.getUniqueId ( ) );
		//
		//					if ( bar == null ) {
		//						map.put ( player.getUniqueId ( ) , bar =
		//								Bukkit.createBossBar ( "hola" , BarColor.RED , BarStyle.SOLID ) );
		//						bar.addPlayer ( player );
		//					}
		//
		//					int           bar_length = 110;
		//					StringBuilder base       = new StringBuilder ( );
		//
		//					// between 0 and 359
		//					for ( int d = 0 ; d < 360 ; d++ ) {
		//						CompassCardinalPoint point = CompassCardinalPoint.ofValue ( d );
		//
		//						if ( point == null ) {
		//							base.append ( '.' ); // 100% customizable
		//						} else {
		//							if ( point.is90 ( ) ) {
		//								base.append ( point.getAbbreviationCharacter ( ) ); // 100% customizable
		//							} else {
		//								base.append ( point.getDefaultAbbreviation ( ) ); // 100% customizable
		//							}
		//						}
		//					}
		//
		//					StringBuilder builder     = new StringBuilder ( );
		//					int           begin_index = base.length ( );
		//
		//					builder.append ( base );
		//					builder.append ( base );
		//					builder.append ( base );
		//
		//					String full        = builder.toString ( );
		//					int    angle       = ( int ) DirectionUtil.normalize ( player.getLocation ( ).getYaw ( ) );
		//					int    base_length = base.length ( );
		//
		//					float factor_a = ( ( float ) angle ) / 360.0F;
		//					int   index    = ( begin_index - ( bar_length / 2 ) ) + ( int ) ( base_length * factor_a );
		//
		//					bar.setTitle ( ChatColor.GOLD + StringUtil.limit ( full.substring ( index ) , bar_length
		//					) );
		//				}
		//			}
		//		} , 0 , 0 );
		
		PacketChannelHandler.addPacketListener (
				"PacketPlayOutOpenSignEditor" , PacketListener.Priority.LOWEST , new PacketAdapter ( ) {
					@Override
					public void onSending ( PacketEvent event ) {
						System.out.println ( "onSending: " + event.getPacket ( ) );
						
						Thread[] f = new Thread[ Thread.currentThread ( ).getThreadGroup ( ).activeCount ( ) ];
						Thread.currentThread ( ).getThreadGroup ( ).enumerate ( f );
						
						for ( Thread thread : f ) {
							System.out.println ( );
							System.out.println ( );
							System.out.println ( );
							System.out.println ( );
							System.out.println ( "* " + thread.getName ( ) );
							
							for ( StackTraceElement e : thread.getStackTrace ( ) ) {
								System.out.println ( "   - " + e );
							}
						}
						
						//						System.out.println ( ">>>> channel thread stack trace: " );
						//						System.out.println ( );
						//						for ( StackTraceElement element : Thread.currentThread ( )
						//						.getStackTrace ( ) ) {
						//							System.out.println ( "- " + element );
						//							System.out.println ( );
						//						}
						//
						//						System.out.println ( );
						//
						//						System.out.println ( ">>>> server thread stack trace: " );
						//						System.out.println ( );
						//						for ( StackTraceElement element : SERVER_THREAD.getStackTrace ( ) ) {
						//							System.out.println ( "- " + element );
						//							System.out.println ( );
						//						}
						//
						//						System.out.println ( );
						//
						//						System.out.println ( "dumpStack::::: " );
						//						Thread.dumpStack ( );
						//
						//						System.out.println ( );
						//
						//						System.out.println ( "getThreadGroup list::::: " );
						//						Thread.currentThread ( ).getThreadGroup ( ).list ( );
						//
						//						System.out.println ( );
						//
						//						new Throwable ( ).printStackTrace ( );
						
						event.setCancelled ( true );
					}
					
					@Override
					public void onReceiving ( PacketEvent event ) {
						System.out.println ( "onReceiving: " + event.getPacket ( ) );
					}
				} );
		return true;
	}
	
	@Override
	protected boolean setUpHandlers ( ) {
		for ( EnumPluginHandler handler : EnumPluginHandler.values ( ) ) {
			if ( handler.canInitialize ( ) && !initialize ( handler ) ) {
				return false;
			}
		}
		
		Bukkit.getPluginManager ( ).registerEvents ( this , this );
		return true;
	}
	
	protected boolean initialize ( EnumPluginHandler handler ) {
		try {
			PluginHandler instance = handler.getHandlerClass ( )
					.getConstructor ( BattleRoyale.class ).newInstance ( this );
			
			// required by ConfigurationHandler handlers.
			if ( instance instanceof ConfigurationHandler ) {
				( ( ConfigurationHandler ) instance ).initialize ( );
			}
			
			return true;
		} catch ( Throwable ex ) {
			ex.printStackTrace ( );
			return false;
		}
	}
	
	@Override
	protected boolean setUpConfig ( ) {
		// making sure required directories exists
		for ( EnumDirectory folder : EnumDirectory.values ( ) ) {
			if ( folder.isRequired ( ) ) {
				folder.getDirectory ( ).mkdirs ( );
			}
		}
		return true;
	}
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator ( String world_name , String id ) {
		// TODO: return a EmptyChunkGenerator if it is a world generated from the arena world generator
		//		if ( Objects.equal ( Bukkit.getWorld ( world_name ).getWorldFolder ( ) ,
		//							 EnumDirectory.BATTLE_MAP_SETUP_WORLD_DIRECTORY.getDirectory ( ) ) ) {
		//			return new EmptyChunkGenerator ( );
		//		} else {
		//			return super.getDefaultWorldGenerator ( world_name , id );
		//		}
		return super.getDefaultWorldGenerator ( world_name , id );
	}
	
	@Override
	public void onDisable ( ) {
		// stopping arenas
		BattleRoyaleLobby lobby = BattleRoyaleLobbyHandler.getInstance ( ).getLobby ( );
		Location          spawn = lobby.getCustomSpawn ( );
		
		if ( spawn == null ) {
			spawn = lobby.getWorld ( ).getSpawnLocation ( );
		}
		
		for ( BattleRoyaleArena arena : BattleRoyaleArenaHandler.getInstance ( ).getArenas ( ) ) {
			for ( org.bukkit.entity.Player player : arena.getWorld ( ).getPlayers ( ) ) {
				// sending back to lobby
				player.teleport ( spawn );
			}
			
			Bukkit.unloadWorld ( arena.getWorld ( ) , false );
			arena.stop ( );
		}
		
		// closing database connection.
		try {
			DataStorageHandler.getInstance ( ).dispose ( );
		} catch ( Exception ex ) {
			ex.printStackTrace ( );
		}
		
		// cleaning temp folder
		deleteTempFolder ( );
	}
	
	protected void deleteTempFolder ( ) {
		File folder = EnumDirectory.BATTLEFIELD_TEMP_DIRECTORY.getDirectory ( );
		
		try {
			if ( !folder.delete ( ) ) {
				FileDeleteStrategy.FORCE.delete ( folder );
			}
		} catch ( Throwable ex ) {
			// ignored exception
		}
	}
	
	World world = null;
	
	@EventHandler
	public void onChat ( AsyncPlayerChatEvent event ) {
		
		//		new Thread ( new Runnable ( ) {
		//			@Override
		//			public void run ( ) {
		//				World     world = new World ( new File ( getInstance ( ).getDataFolder ( ) , "result_1_12" ) );
		//				WorldData data  = world.getWorldData ( );
		//
		//				data.setName ( "BattleRoyaleArenaWorld" );
		//				data.setGeneratorType ( EnumWorldGenerator.FLAT );
		//				data.setGeneratorOptions ( "2;0;1" ); // void
		//				data.setGenerateStructures ( false );
		//				data.setInitialized ( false );
		//				data.setSpawnX ( 0 );
		//				data.setSpawnY ( 0 );
		//				data.setSpawnZ ( 0 );
		//
		//				world.setBlockAt ( 0 , 0 , 0 , ( byte ) 2 );
		//				world.setBlockAt ( 0 , 1 , 0 , ( byte ) 2 );
		//				world.setBlockAt ( 0 , 2 , 0 , ( byte ) 2 );
		//
		//				// id over 255
		////				world.setBlockIDAt ( 0 , 3 , 0 , 324 );
		//
		//				// white wool
		////				world.setBlockIDAt ( 0 , 6 , 0 , 35 );
		//
		////				world.setBlockAt ( 0 , 4 , 0 , 35 , ( byte ) 4 );
		////				world.setBlockAt ( 0 , 5 , 0 , 35 , ( byte ) 11 );
		////				world.setBlockAt ( 0 , 6 , 0 , 35 , ( byte ) 14 );
		////
		////				world.setBlockAt ( 0 , 5 , 0 , 35 , ( byte ) 14 );
		////				world.setBlockAt ( 0 , 6 , 0 , 35 , ( byte ) 0 );
		////				world.setBlockAt ( 0 , 7 , 0 , 35 , ( byte ) 14 );
		////
		////				// test set to white, then to red, then to white
		////				world.setBlockAt ( 0 , 9 , 0 , 35 , ( byte ) 0 );
		////				world.setBlockAt ( 0 , 9 , 0 , 35 , ( byte ) 14 );
		////				world.setBlockAt ( 0 , 9 , 0 , 35 , ( byte ) 0 );
		//
		////				world.setMaterialAt ( 0 , 0 , 0 , new Material ( "minecraft:diamond_ore" ) );
		////				world.setMaterialAt ( 0 , 1 , 0 , new Material ( "minecraft:diamond_block" ) );
		////				world.setMaterialAt ( 0 , 2 , 0 , new Material ( "minecraft:gold_ore" ) );
		//
		////				Material a = new Material ( "minecraft:oak_stairs" );
		////				a.setProperty ( "facing" , "south" );
		////
		////				Material b = new Material ( "minecraft:oak_stairs" );
		////				b.setProperty ( "facing" , "north" );
		////
		////				Material c = new Material ( "minecraft:oak_stairs" );
		////				c.setProperty ( "facing" , "west" );
		////
		////				Material d = new Material ( "minecraft:oak_stairs" );
		////				d.setProperty ( "facing" , "east" );
		////
		////				world.setMaterialAt ( 0 , 0 , 0 , a );
		////				world.setMaterialAt ( 0 , 1 , 0 , b );
		////				world.setMaterialAt ( 0 , 2 , 0 , c );
		////				world.setMaterialAt ( 0 , 3 , 0 , d );
		//
		//				// schematic
		//				final long time = System.currentTimeMillis ();
		//
		//				File      schematic_folder = EnumDirectory.BATTLE_MAP_INPUT_DIRECTORY.getDirectory ( );
		//				File      schematic_file   = new File ( schematic_folder , "hg12.schematic" );
		//				Clipboard schematic        = null;
		//
		//				try {
		//					NBTInputStream input = new NBTInputStream ( new GZIPInputStream (
		//							new FileInputStream ( schematic_file ) ) );
		//
		//					Class < ? > legacy_schematic_reader_class = Class
		//							.forName ( "com.sk89q.worldedit.extent.clipboard.io.SchematicReader" );
		//					Class < ? > world_data_class = Class
		//							.forName ( "com.sk89q.worldedit.world.registry.WorldData" );
		//
		//					Method read_method = legacy_schematic_reader_class.getMethod ( "read" , world_data_class );
		//					Object reader = legacy_schematic_reader_class
		//							.getConstructor ( NBTInputStream.class ).newInstance ( input );
		//
		//					schematic = ( Clipboard ) read_method.invoke (
		//							reader ,
		//							world_data_class.cast ( null ) );
		//				} catch ( Exception e ) {
		//					e.printStackTrace ( );
		//				}
		//
		//				System.out.println ( "schematic = " + schematic );
		//
		//
		//				world.insert ( schematic , new Vector ( 2 , 2 , 2 ) , true );
		//				world.save ( );
		//				System.out.println ( "Done in " + ( System.currentTimeMillis () - time ) + "ms !!!!!!!" );
		//			}
		//		} ).run ( );
		
		if ( event.getMessage ( ).trim ( ).equals ( "go" ) ) {
			System.out.println ( ">>>> going...." );
			
			for ( BattleRoyaleArena arena : BattleRoyaleArenaHandler.getInstance ( ).getArenas ( ) ) {
				Bukkit.getScheduler ( ).runTask (
						this , ( ) -> {
							event.getPlayer ( ).teleport (
									new Location ( arena.getWorld ( ) , 0 , 0 , 0 ) );
							arena.introduce ( event.getPlayer ( ) , false );
						} );
				break;
			}
			//			if ( world != null ) {
			//				Bukkit.getScheduler ( ).runTask ( this ,
			//												  ( ) -> {
			//													  event.getPlayer ( ).teleport ( new Location ( world ,
			//																									0 ,
			//																									150 ,
			//																									0 ) );
			//												  } );
			//			}
			return;
		} else if ( event.getMessage ( ).trim ( ).equals ( "restart" ) ) {
			System.out.println ( ">>>> restarting...." );
			
			Player.getPlayer ( event.getPlayer ( ) ).getArena ( ).restart ( );
		} else if ( event.getMessage ( ).equals ( "set" ) ) {
			org.bukkit.entity.Player player = event.getPlayer ( );
			
			SchedulerUtil.runTask ( ( ) -> ArenaSelectorGUIHandler.getInstance ( ).open ( player ) );
		} else if ( event.getMessage ( ).equals ( "team" ) ) {
			org.bukkit.entity.Player player = event.getPlayer ( );
			
			//			SchedulerUtil.runTask ( ( ) -> TeamSelectorGUI.getInstance ( ).open ( player ) );
			SchedulerUtil.runTask ( ( ) -> TeamSelectorGUIHandler.getInstance ( ).open ( player ) );
			
		} else if ( event.getMessage ( ).equals ( "para" ) ) {
			Bukkit.getScheduler ( ).runTask ( this , ( ) -> {
				Player.getPlayer ( event.getPlayer ( ) ).getParachute ( ).open ( );
			} );
		} else if ( event.getMessage ( ).equals ( "sett" ) ) {
			Bukkit.getScheduler ( ).runTask ( this , ( ) -> {
				SettingsGUIHandler.getInstance ( ).open ( event.getPlayer ( ) );
			} );
		} else if ( event.getMessage ( ).equals ( "sp" ) ) {
			Bukkit.getScheduler ( ).runTask ( this , ( ) -> {
				Player.getPlayer ( event.getPlayer ( ) ).setSpectator (
						!Player.getPlayer ( event.getPlayer ( ) ).isSpectator ( ) );
			} );
		} else if ( event.getMessage ( ).equals ( "pa" ) ) {
			BukkitReflection.sendPacket ( event.getPlayer ( ) , new PacketPlayOutOpenSignEditor ( ) );
		} else if ( event.getMessage ( ).equals ( "pb" ) ) {
			Bukkit.getScheduler ( ).runTask ( this , ( ) -> {
				BukkitReflection.sendPacket ( event.getPlayer ( ) , new UIPacket ( ) );
			} );
		} else if ( event.getMessage ( ).equals ( "pc" ) ) {
			new Thread (
					( ) -> BukkitReflection.sendPacket ( event.getPlayer ( ) ,
														 new UIPacket ( ) ) ).run ( );
			
		} else if ( event.getMessage ( ).equals ( "wa" ) ) {
			Bukkit.getScheduler ( ).runTask ( this , ( ) -> {
				PlayerWrapper wrapper = PlayerWrapper.of ( event.getPlayer ( ) );
				
				System.out.println ( "try a" );
				System.out.println ( "returned: " + wrapper.getPlayerListFooter ( ) );
				//				wrapper.setPlayerListHeader ( "hola puto" );
				System.out.println ( "try a done" );
				
				System.out.println ( "try b" );
				wrapper.setVerbose ( true );
				System.out.println ( "returned: " + wrapper.getPlayerListFooter ( ) );
				//				wrapper.setPlayerListHeader ( "hola puto" );
				System.out.println ( "try b done" );
			} );
		}
		
	}
	
	static class UIPacket extends PacketPlayOutOpenSignEditor {
	
	}
}