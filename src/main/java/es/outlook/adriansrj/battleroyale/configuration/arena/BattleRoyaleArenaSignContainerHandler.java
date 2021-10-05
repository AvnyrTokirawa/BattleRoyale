package es.outlook.adriansrj.battleroyale.configuration.arena;

import es.outlook.adriansrj.battleroyale.arena.sign.BattleRoyaleArenaSign;
import es.outlook.adriansrj.battleroyale.arena.sign.BattleRoyaleArenaSignHandler;
import es.outlook.adriansrj.battleroyale.configuration.ScalableConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

/**
 * @author AdrianSR / 03/09/2021 / 02:30 p. m.
 */
public final class BattleRoyaleArenaSignContainerHandler extends ScalableConfigurationHandler {
	
	private static final String SIGNS_KEY = "signs";
	
	public static BattleRoyaleArenaSignContainerHandler getInstance ( ) {
		return getPluginHandler ( BattleRoyaleArenaSignContainerHandler.class );
	}
	
	/**
	 * Constructs the container handler.
	 *
	 * @param plugin the battle royale plugin instance.
	 */
	public BattleRoyaleArenaSignContainerHandler ( BattleRoyale plugin ) {
		super ( plugin );
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.ARENA_SIGNS_CONTAINER.getFile ( );
	}
	
	@Override
	protected void loadConfiguration ( YamlConfigurationComments yaml ) {
		ConfigurationSection section = yaml.getConfigurationSection ( SIGNS_KEY );
		
		if ( section != null ) {
			for ( String key : section.getKeys ( false ) ) {
				if ( section.isConfigurationSection ( key ) ) {
					BattleRoyaleArenaSign sign = BattleRoyaleArenaSign.of ( section.getConfigurationSection ( key ) );
					
					if ( sign.isValid ( ) ) {
						BattleRoyaleArenaSignHandler.getInstance ( ).registerSign ( sign );
					}
				}
			}
		}
	}
	
	@Override
	protected int save ( YamlConfigurationComments yaml ) {
		ConfigurationSection section = yaml.createSection ( SIGNS_KEY );
		int                  count   = 0;
		int                  save    = 0;
		
		for ( BattleRoyaleArenaSign sign : BattleRoyaleArenaSignHandler.getInstance ( ).getSigns ( ) ) {
			if ( sign.isValid ( ) ) {
				save += sign.save ( section.createSection ( "sign-" + ( count++ ) ) );
			}
		}
		return save;
	}
	
	@Override
	protected int saveDefaultConfiguration ( YamlConfigurationComments yaml ) {
		return 0;
	}
}