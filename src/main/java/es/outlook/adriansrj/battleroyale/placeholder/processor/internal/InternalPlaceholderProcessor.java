package es.outlook.adriansrj.battleroyale.placeholder.processor.internal;

import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNode;
import es.outlook.adriansrj.battleroyale.placeholder.processor.PlaceholderProcessor;
import es.outlook.adriansrj.battleroyale.util.Constants;
import org.bukkit.entity.Player;

/**
 * Process placeholder with no need for other plugins.
 *
 * @author AdrianSR / 06/10/2021 / 10:22 a. m.
 */
public class InternalPlaceholderProcessor implements PlaceholderProcessor {
	
	private static final char PLACEHOLDER_IDENTIFIER_CHAR = '%';
	
	public InternalPlaceholderProcessor ( ) {
		// java 16 and its constructor system!
	}
	
	@Override
	public String process ( Player player , String contents ) {
		if ( contents == null ) { return null; }
		
		int index = 0;
		
		while ( index < ( contents.length ( ) - 1 ) ) {
			String lower_case = contents.toLowerCase ( );
			int next_index = lower_case.indexOf (
					PLACEHOLDER_IDENTIFIER_CHAR + Constants.BATTLE_ROYALE_PLACEHOLDER_IDENTIFIER , index );
			int end_index = ( next_index != -1 && contents.length ( ) > 1 ) ?
					lower_case.indexOf ( PLACEHOLDER_IDENTIFIER_CHAR , next_index + 1 ) : -1;
			
			if ( next_index != -1 && end_index != -1 ) {
				String parameters = lower_case.substring ( next_index + 1 , end_index )
						.replaceFirst ( Constants.BATTLE_ROYALE_PLACEHOLDER_IDENTIFIER , "" );
				parameters = parameters.charAt ( 0 ) == '_' ? parameters.replaceFirst ( "_" , "" ) : parameters;
				String replacement = onPlaceholderRequest ( player , parameters );
				
				if ( replacement != null ) {
					String before_placeholder = contents.substring ( 0 , next_index );
					String after_placeholder =
							( end_index < contents.length ( ) - 1 ) ? contents.substring ( end_index + 1 ) : "";
					
					// the placeholder has been replaced successfully!
					contents = before_placeholder + replacement + after_placeholder;
					index    = before_placeholder.length ( ) + replacement.length ( );
				} else {
					// the placeholder was not replaced :(
					index = end_index + 1;
				}
			} else {
				break;
			}
		}
		return contents;
	}
	
	protected String onPlaceholderRequest ( Player player , String params ) {
		PlaceholderNode node = matchNode ( params );
		
		if ( node != null ) {
			return node.onPlaceholderRequest ( player , params.toLowerCase ( ).replaceFirst (
					Constants.BATTLE_ROYALE_PLACEHOLDER_IDENTIFIER + '_' , "" ) );
		} else {
			return null;
		}
	}
}
