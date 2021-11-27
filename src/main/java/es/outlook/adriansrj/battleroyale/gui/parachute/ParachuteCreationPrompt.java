package es.outlook.adriansrj.battleroyale.gui.parachute;

import es.outlook.adriansrj.battleroyale.enums.EnumDirectory;
import es.outlook.adriansrj.battleroyale.enums.EnumInternalLanguage;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.file.filter.YamlFileFilter;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.function.BiConsumer;

/**
 * Prompt that accepts the name for the new parachute
 * that is to be created.
 *
 * @author AdrianSR / 23/11/2021 / 11:01 a. m.
 */
public class ParachuteCreationPrompt extends ValidatingPrompt {
	
	private final BiConsumer < Player, String > callback;
	
	public ParachuteCreationPrompt ( BiConsumer < Player, String > callback ) {
		this.callback = callback;
	}
	
	@Override
	protected boolean isInputValid ( @NotNull ConversationContext context , @NotNull String input ) {
		if ( StringUtil.isNotBlank ( input ) ) {
			try {
				return !Paths.get ( yamlExtensionCheck ( input ).trim ( ) ).toFile ( ).exists ( );
			} catch ( InvalidPathException ex ) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Nullable
	@Override
	protected Prompt acceptValidatedInput ( @NotNull ConversationContext context , @NotNull String input ) {
		input = input.trim ( );
		input = input.replace ( "/" , "" );
		input = input.replace ( "\\" , "" );
		input = yamlExtensionCheck ( input );
		
		// checking name is not already used
		if ( new File ( EnumDirectory.PARACHUTE_DIRECTORY.getDirectory ( ) , input ).exists ( ) ) {
			context.getForWhom ( ).sendRawMessage (
					EnumInternalLanguage.PARACHUTE_CREATOR_PROMPT_NAME_USED.toString ( ) );
			return this;
		}
		
		Conversable who = context.getForWhom ( );
		
		if ( who instanceof org.bukkit.entity.Player ) {
			callback.accept ( Player.getPlayer ( ( org.bukkit.entity.Player ) who ) , input );
		} else {
			who.sendRawMessage (
					EnumInternalLanguage.PARACHUTE_CREATOR_PROMPT_MUST_BE_PLAYER.toString ( ) );
		}
		
		return Prompt.END_OF_CONVERSATION;
	}
	
	@NotNull
	@Override
	public String getPromptText ( @NotNull ConversationContext context ) {
		return EnumInternalLanguage.PARACHUTE_CREATOR_PROMPT.toString ( ) + '\n'
				+ EnumInternalLanguage.TOOL_PROMPT_CANCEL;
	}
	
	// ------ utils
	
	private String yamlExtensionCheck ( String filename ) {
		if ( !filename.toLowerCase ( ).endsWith ( "." + YamlFileFilter.YML_EXTENSION ) ) {
			filename += "." + YamlFileFilter.YML_EXTENSION;
		}
		return filename;
	}
}
