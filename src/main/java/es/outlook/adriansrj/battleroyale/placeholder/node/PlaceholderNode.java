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
		
		if ( params.toLowerCase ( ).startsWith ( identifier ) ) {
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
	
	/**
	 * Extracts the identifier of the provided placeholder.
	 * <br>
	 * Examples:
	 * <blockquote><pre>
	 * extractIdentifier( "br_team_id" ) returns "team_id" (<b>'br_'</b> extracted)
	 * extractIdentifier( "team_id" ) returns "id" (<b>'id_'</b> extracted)
	 * </pre></blockquote>
	 *
	 * @param placeholder the placeholder to extract from.
	 * @return the placeholder without the first identifier.
	 */
	protected String extractIdentifier ( String placeholder ) {
		int separator_index = placeholder.indexOf ( '_' );
		return separator_index != -1 && placeholder.length ( ) > 1
				? placeholder.substring ( separator_index + 1 ) : placeholder;
	}
}
