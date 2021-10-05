package es.outlook.adriansrj.battleroyale.game.mode.simple;

import es.outlook.adriansrj.battleroyale.enums.BattleRoyaleModeDefaultPresentation;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleModeLoader;
import es.outlook.adriansrj.core.util.yaml.comment.YamlConfigurationComments;
import org.apache.commons.lang.Validate;

import java.io.File;

/**
 * Simple battle royale modes loader (<b>.yml</b>).
 * <p>
 * @author AdrianSR / Sunday 16 May, 2021 / 04:22 PM
 */
public class SimpleBattleRoyaleModeLoader extends BattleRoyaleModeLoader {
	
	@Override
	public BattleRoyaleModeDefaultPresentation getPresentation ( ) {
		return BattleRoyaleModeDefaultPresentation.YAML_FILE;
	}
	
	@Override
	public BattleRoyaleMode load ( File file ) {
		Validate.isTrue ( getPresentation ( ).getFileFilter ( ).accept ( file ) ,
						  "unsupported file extension: " + file.getName ( ) );
		
		return new SimpleBattleRoyaleMode ( ).load ( YamlConfigurationComments.loadConfiguration ( file ) );
	}
}