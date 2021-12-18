package es.outlook.adriansrj.battleroyale.configuration.parachute;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.parachute.Parachute;
import es.outlook.adriansrj.battleroyale.parachute.ParachuteRegistry;
import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustom;
import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustomModel;
import es.outlook.adriansrj.battleroyale.parachute.custom.ParachuteCustomModelPartShape;
import es.outlook.adriansrj.battleroyale.util.Constants;
import es.outlook.adriansrj.core.util.console.ConsoleUtil;
import es.outlook.adriansrj.core.util.file.FilenameUtil;
import es.outlook.adriansrj.core.util.file.filter.YamlFileFilter;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author AdrianSR / 11/09/2021 / 11:50 a. m.
 */
public final class ParachutesConfigHandler extends ConfigurationHandler {
	
	private static final Map < String, Parachute > DEFAULT_PARACHUTES = new HashMap <> ( );
	
	static {
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
		
		ParachuteCustomModel big_model = new ParachuteCustomModel.Builder ( )
				// part 0
				.withPart ( 1.2F , 3.5F , 0.0F ,
							90.0F , 0.0F , 130.0F ,
							new ParachuteCustomModelPartShape (
									UniversalMaterial.WHITE_BANNER ,
									Parachute.Color.PLAYER
							) )
				// part 1
				.withPart ( 0.06F , 3.8F , 0.0F ,
							90.0F , 0.0F , 110.0F ,
							new ParachuteCustomModelPartShape (
									UniversalMaterial.WHITE_BANNER ,
									Parachute.Color.PLAYER
							) )
				// part 2
				.withPart ( -1.2F , 3.5F , 0.0F ,
							-90.0F , 0.0F , -130.0F ,
							new ParachuteCustomModelPartShape (
									UniversalMaterial.WHITE_BANNER ,
									Parachute.Color.PLAYER
							) )
				// part 3
				.withPart ( -0.06F , 3.8F , 0.0F ,
							-90.0F , 0.0F , -110.0F ,
							new ParachuteCustomModelPartShape (
									UniversalMaterial.WHITE_BANNER ,
									Parachute.Color.PLAYER
							) )
				// part 4
				.withPart ( 0.19F , 3.57F , 0.0F ,
							0.0F , 0.0F , 0.0F ,
							new ParachuteCustomModelPartShape (
									UniversalMaterial.OAK_PRESSURE_PLATE ,
									Parachute.Color.PLAYER
							) )
				// part 5
				.withPart ( -0.19F , 3.57F , 0.0F ,
							0.0F , 0.0F , 0.0F ,
							new ParachuteCustomModelPartShape (
									UniversalMaterial.OAK_PRESSURE_PLATE ,
									Parachute.Color.PLAYER
							) )
				.build ( );
		
		ParachuteCustomModel umbrella_model = new ParachuteCustomModel.Builder ( )
				// part 0
				.withPart ( 0.0F , 2.5F , 1.4F ,
							0.0F , -60.0F , 0.0F ,
							new ParachuteCustomModelPartShape (
									UniversalMaterial.WHITE_BANNER ,
									Parachute.Color.PLAYER
							) )
				// part 1
				.withPart ( 0.0F , 2.5F , -1.4F ,
							180.0F , -60.0F , 0.0F ,
							new ParachuteCustomModelPartShape (
									UniversalMaterial.WHITE_BANNER ,
									Parachute.Color.PLAYER
							) )
				// part 2
				.withPart ( 1.4F , 2.5F , 0.0F ,
							-90.0F , -60.0F , 0.0F ,
							new ParachuteCustomModelPartShape (
									UniversalMaterial.WHITE_BANNER ,
									Parachute.Color.PLAYER
							) )
				// part 3
				.withPart ( -1.4F , 2.5F , 0.0F ,
							90.0F , -60.0F , 0.0F ,
							new ParachuteCustomModelPartShape (
									UniversalMaterial.WHITE_BANNER ,
									Parachute.Color.PLAYER
							) )
				.build ( );
		
		// aladelta
		DEFAULT_PARACHUTES.put ( "alatelta" , new ParachuteCustom ( 1000 , new Permission (
				"br.parachute.aladelta" , PermissionDefault.OP ) , aladelta_model ) );
		
		// big
		DEFAULT_PARACHUTES.put ( "big" , new ParachuteCustom ( 1000 , new Permission (
				"br.parachute.big" , PermissionDefault.OP ) , big_model ) );
		
		// umbrella
		DEFAULT_PARACHUTES.put ( "umbrella" , new ParachuteCustom ( 1000 , new Permission (
				"br.parachute.umbrella" , PermissionDefault.OP ) , umbrella_model ) );
	}
	
	/**
	 * Constructs the configuration handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public ParachutesConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public void initialize ( ) {
		File    folder       = EnumDirectory.PARACHUTE_DIRECTORY.getDirectory ( );
		boolean defaults     = !folder.exists ( ) && folder.mkdirs ( );
		File    default_file = new File ( folder , Constants.DEFAULT_YAML_FILE_NAME );
		
		// saving default parachute
		if ( !default_file.exists ( ) ) {
			try {
				if ( default_file.createNewFile ( ) ) {
					saveParachute ( new ParachuteCustom (
							0 , null , ParachuteCustomModel.DEFAULT_MODEL ) , default_file );
				} else {
					throw new IllegalStateException ( "couldn't save default parachute file" );
				}
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
		
		// saving default parachutes
		if ( defaults ) {
			for ( Map.Entry < String, Parachute > entry : DEFAULT_PARACHUTES.entrySet ( ) ) {
				String    name      = entry.getKey ( );
				Parachute parachute = entry.getValue ( );
				File      file      = new File ( folder , name + "." + YamlFileFilter.YML_EXTENSION );
				
				saveParachute ( parachute , file );
			}
		}
		
		// then loading configuration
		loadConfiguration ( );
	}
	
	@Override
	public void loadConfiguration ( ) {
		File folder = EnumDirectory.PARACHUTE_DIRECTORY.getDirectoryMkdirs ( );
		
		for ( File file : Objects.requireNonNull ( folder.listFiles ( new YamlFileFilter ( ) ) ) ) {
			try {
				Parachute parachute = Parachute.of (
						YamlConfigurationComments.loadConfiguration ( file ) );
				
				if ( parachute != null && parachute.isValid ( ) ) {
					ParachuteRegistry.getInstance ( )
							.registerParachute ( FilenameUtil.getBaseName ( file ) , parachute );
				}
			} catch ( Exception ex ) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , "Couldn't load parachute '"
						+ FilenameUtil.getBaseName ( file ) + "'!" , BattleRoyale.getInstance ( ) );
				ex.printStackTrace ( );
			}
		}
	}
	
	@Override
	public void save ( ) {
		// nothing to do here
	}
	
	// ------- utils
	
	private void saveParachute ( Parachute parachute , File file ) {
		if ( !file.exists ( ) ) {
			file.getParentFile ( ).mkdirs ( );
			
			try {
				file.createNewFile ( );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
		
		YamlConfigurationComments yaml = YamlConfigurationComments.loadConfiguration ( file );
		
		if ( parachute.save ( yaml ) > 0 ) {
			try {
				yaml.save ( file );
			} catch ( IOException e ) {
				e.printStackTrace ( );
			}
		}
	}
}
