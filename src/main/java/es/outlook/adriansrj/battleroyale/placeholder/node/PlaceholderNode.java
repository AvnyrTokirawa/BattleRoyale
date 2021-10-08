package es.outlook.adriansrj.battleroyale.placeholder.node;

import org.bukkit.entity.Player;

/**
 * Plugin placeholder node.
 *
 * @author AdrianSR / 06/10/2021 / 10:53 a. m.
 */
public abstract class PlaceholderNode {
	
	/**
	 * Gets the identifier that goes immediately after the plugin
	 * placeholders identifier (<b>'br'</b>).
	 *
	 * @return the su-identifier.
	 */
	public abstract String getSubIdentifier ( );
	
	/**
	 * Called from {@link #onPlaceholderRequest(Player , String)} requesting a replacement
	 * of the placeholder from the provided parameters.
	 *
	 * @param player the player.
	 * @param params the parameters. (<b>the identifier and the sub-identifier are excluded from these parameters</b>).
	 * @return the resulting replacement.
	 */
	protected abstract String onRequest ( Player player , String params );
	
	/**
	 * Requests a replacement for the provided parameters.
	 *
	 * @param player the player.
	 * @param params the parameters, starting with the sub-identifier.
	 * @return the resulting replacement.
	 */
	public String onPlaceholderRequest ( Player player , String params ) {
		String identifier = getSubIdentifier ( ).toLowerCase ( );
		
		if ( params.toLowerCase ( ).startsWith ( identifier )
				|| params.toLowerCase ( ).startsWith ( identifier + '_' ) ) {
			params = params.toLowerCase ( ).replaceFirst ( identifier , "" );
			
			if ( params.length ( ) > 0 && params.charAt ( 0 ) == '_' ) {
				return onRequest ( player , params.replaceFirst ( "_" , "" ) );
			} else {
				return onRequest ( player , params );
			}
		} else {
			return null;
		}
	}
}
