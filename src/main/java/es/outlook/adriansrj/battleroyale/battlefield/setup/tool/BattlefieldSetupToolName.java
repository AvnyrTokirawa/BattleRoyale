package es.outlook.adriansrj.battleroyale.battlefield.setup.tool;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.enums.EnumInternalLanguage;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.util.StringUtil;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

/**
 * Tool useful to set the name of the battlefield.
 *
 * @author AdrianSR / 02/09/2021 / 01:48 p. m.
 */
public class BattlefieldSetupToolName extends BattlefieldSetupToolPrompt {
	
	/**
	 * @author AdrianSR / 02/09/2021 / 01:49 p. m.
	 */
	protected static class BattlefieldNamePrompt extends ValidatingPrompt {
		
		protected final BattlefieldSetupToolName tool;
		
		public BattlefieldNamePrompt ( BattlefieldSetupToolName tool ) {
			this.tool = tool;
		}
		
		@Override
		protected boolean isInputValid ( ConversationContext conversationContext , String input ) {
			if ( StringUtil.isNotBlank ( input ) ) {
				try {
					Paths.get ( input.trim ( ) );
					return true;
				} catch ( InvalidPathException ex ) {
					return false;
				}
			} else {
				return false;
			}
		}
		
		@Override
		protected Prompt acceptValidatedInput ( ConversationContext context , String input ) {
			input = input.trim ( );
			input = input.replace ( "/" , "" );
			input = input.replace ( "\\" , "" );
			
			if ( input.indexOf ( '.' ) != -1 ) {
				input = input.substring ( 0 , input.indexOf ( '.' ) );
			}
			
			// checking name is not already used
			if ( new File ( EnumDirectory.BATTLEFIELD_DIRECTORY.getDirectory ( ) , input ).exists ( ) ) {
				context.getForWhom ( ).sendRawMessage (
						EnumInternalLanguage.TOOL_PROMPT_BATTLEFIELD_NAME_USED.toString ( ) );
				return this;
			}
			
			// then setting name
			tool.session.setName ( input );
			tool.successful = true;
			
			context.getForWhom ( ).sendRawMessage ( String.format (
					EnumInternalLanguage.TOOL_PROMPT_BATTLEFIELD_NAME_SET.toString ( ) , input ) );
			
			/*if ( !ArrayUtils.contains ( CANCEL_KEYWORDS , input.toLowerCase ( ) ) ) {
			
			}*/ /* else {
				context.getForWhom ( ).sendRawMessage ( EnumInternalLanguage.TOOL_CANCELLED_MESSAGE.toString ( ) );
			}*/
			
			return Prompt.END_OF_CONVERSATION;
		}
		
		@Override
		public String getPromptText ( ConversationContext conversationContext ) {
			return EnumInternalLanguage.TOOL_PROMPT_BATTLEFIELD_NAME.toString ( ) + '\n'
					+ EnumInternalLanguage.TOOL_PROMPT_CANCEL.toString ( );
		}
		
		@Override
		protected String getFailedValidationText ( ConversationContext context , String invalid_input ) {
			return EnumInternalLanguage.TOOL_PROMPT_INVALID_INPUT.toString ( );
		}
	}
	
	protected final BattlefieldNamePrompt prompt;
	protected       boolean               successful;
	
	protected BattlefieldSetupToolName ( BattlefieldSetupSession session , Player configurator ) {
		super ( session , configurator );
		
		this.prompt     = new BattlefieldNamePrompt ( this );
		this.successful = false;
	}
	
	@Override
	public boolean isModal ( ) {
		return true;
	}
	
	@Override
	public boolean isCancellable ( ) {
		return true;
	}
	
	@Override
	protected ValidatingPrompt getPrompt ( ) {
		return prompt;
	}
	
	@Override
	protected boolean isSuccessfully ( ) {
		return successful;
	}
}
