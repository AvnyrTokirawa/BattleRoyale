package es.outlook.adriansrj.battleroyale.placeholder.processor;

import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNode;
import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNodeRegistry;
import org.bukkit.entity.Player;

/**
 * Class responsible for processing placeholders.
 *
 * @author AdrianSR / 06/10/2021 / 09:47 a. m.
 */
public interface PlaceholderProcessor {
	
	String process ( Player player , String contents );
	
	default PlaceholderNode matchNode ( String params ) {
		if ( params != null ) {
			for ( PlaceholderNode node : PlaceholderNodeRegistry.getInstance ( ).getRegisteredNodes ( ) ) {
				String identifier = node.getSubIdentifier ( ).toLowerCase ( ).trim ( );
				
				if ( params.toLowerCase ( ).startsWith ( identifier )
						|| params.toLowerCase ( ).startsWith ( identifier + '_' ) ) {
					return node;
				}
			}
		}
		return null;
	}
}