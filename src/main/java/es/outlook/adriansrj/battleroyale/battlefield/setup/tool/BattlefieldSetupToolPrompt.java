package es.outlook.adriansrj.battleroyale.battlefield.setup.tool;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupTool;
import es.outlook.adriansrj.battleroyale.enums.EnumInternalLanguage;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ExactMatchConversationCanceller;
import org.bukkit.conversations.ValidatingPrompt;

import java.util.Objects;

/**
 * @author AdrianSR / 02/09/2021 / 01:06 p. m.
 */
public abstract class BattlefieldSetupToolPrompt extends BattlefieldSetupTool {
	
	protected static final ConversationFactory CONVERSATION_FACTOR = new ConversationFactory (
			BattleRoyale.getInstance ( ) );
	
	/**
	 * Cancel conversation keywords.
	 */
	public static final String[] CANCEL_KEYWORDS = {
			"exit" , "cancel" , "quit"
	};
	
	/**
	 * Go back conversation keywords.
	 */
	public static final String[] GO_BACK_KEYWORDS = {
			"back" , "previous"
	};
	
	protected BattlefieldSetupToolPrompt ( BattlefieldSetupSession session , Player configurator ) {
		super ( session , configurator );
	}
	
	protected abstract ValidatingPrompt getPrompt ( );
	
	protected abstract boolean isSuccessfully ( );
	
	protected boolean isStrict ( ) {
		return true;
	}
	
	@Override
	protected void initialize ( ) {
		ValidatingPrompt prompt = Objects.requireNonNull (
				getPrompt ( ) , "getPrompt() returned null" );
		
		configurator.getBukkitPlayerOptional ( ).ifPresent ( player -> {
			if ( isStrict ( ) && player.isConversing ( ) ) {
				player.sendMessage ( EnumInternalLanguage.TOOL_PROMPT_BUSY.toString ( ) );
				dispose ( );
			} else {
				ConversationFactory factory = new ConversationFactory ( BattleRoyale.getInstance ( ) );
				
				factory.withModality ( true ).withFirstPrompt ( prompt ).withLocalEcho ( true )
						.addConversationAbandonedListener ( event -> {
							dispose ( );
							
							if ( isSuccessfully ( ) || !isCancellable ( ) ) {
								event.getContext ( ).getForWhom ( ).sendRawMessage (
										EnumInternalLanguage.TOOL_FINISHED_MESSAGE.toString ( ) );
							} else {
								event.getContext ( ).getForWhom ( ).sendRawMessage (
										EnumInternalLanguage.TOOL_CANCELLED_MESSAGE.toString ( ) );
							}
						} );
				
				// cancellers
				for ( String keyword : CANCEL_KEYWORDS ) {
					factory.withConversationCanceller ( new ExactMatchConversationCanceller ( keyword ) {
						@Override
						public boolean cancelBasedOnInput ( ConversationContext context , String input ) {
							return keyword.equalsIgnoreCase ( input );
						}
					} );
				}
				
				// then begin
				factory.buildConversation ( player ).begin ( );
			}
		} );
	}
}