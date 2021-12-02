package es.outlook.adriansrj.battleroyale.configuration.stat;

import es.outlook.adriansrj.battleroyale.configuration.ScalableConfigurationHandler;
import es.outlook.adriansrj.battleroyale.enums.EnumFile;
import es.outlook.adriansrj.battleroyale.enums.EnumStat;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.core.util.yaml.YamlUtil;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author AdrianSR / 29/11/2021 / 02:43 p. m.
 */
public final class StatRewardConfigHandler extends ScalableConfigurationHandler {
	
	public static StatRewardConfigHandler getInstance ( ) {
		return getPluginHandler ( StatRewardConfigHandler.class );
	}
	
	private final Map < EnumStat, Integer > reward_map = new EnumMap <> ( EnumStat.class );
	
	public StatRewardConfigHandler ( BattleRoyale plugin ) {
		super ( plugin );
		
		//
		for ( EnumStat stat : EnumStat.values ( ) ) {
			reward_map.put ( stat , 0 );
		}
	}
	
	public int getReward ( EnumStat stat ) {
		return reward_map.get ( stat );
	}
	
	@Override
	public File getFile ( ) {
		return EnumFile.START_REWARD_CONFIGURATION.getFile ( );
	}
	
	@Override
	protected void loadConfiguration ( YamlConfigurationComments yaml ) {
		for ( EnumStat stat : EnumStat.values ( ) ) {
			reward_map.put ( stat , yaml.getInt ( stat.name ( ).toLowerCase ( ) ) );
		}
	}
	
	@Override
	protected int saveDefaultConfiguration ( YamlConfigurationComments yaml ) {
		int save = 0;
		
		for ( EnumStat stat : EnumStat.values ( ) ) {
			save += YamlUtil.setNotSet (
					yaml , stat.name ( ).toLowerCase ( ) , stat.getDefaultReward ( ) ) ? 1 : 0;
		}
		
		return save;
	}
	
	@Override
	protected int save ( YamlConfigurationComments yaml ) {
		// nothing to save
		return 0;
	}
}
