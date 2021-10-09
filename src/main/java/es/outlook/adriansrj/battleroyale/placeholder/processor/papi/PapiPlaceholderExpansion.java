package es.outlook.adriansrj.battleroyale.placeholder.processor.papi;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNode;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 *
 *
 * @author AdrianSR / 08/10/2021 / 05:44 p. m.
 */
class PapiPlaceholderExpansion extends PlaceholderExpansion {
	
	private final PapiPlaceholderProcessor processor;
	
	public PapiPlaceholderExpansion ( PapiPlaceholderProcessor processor ) {
		this.processor = processor;
	}
	
	@Override
	public @NotNull String getIdentifier ( ) {
		return "br";
	}
	
	@Override
	public @NotNull String getAuthor ( ) {
		return BattleRoyale.getInstance ( ).getDescription ( ).getAuthors ( ).toString ( );
	}
	
	@Override
	public @NotNull String getVersion ( ) {
		return "0.0.1";
	}
	
	@Override
	public String onPlaceholderRequest ( Player player , @NotNull String params ) {
		PlaceholderNode node = processor.matchNode ( params );
		
		if ( node != null ) {
			return node.onPlaceholderRequest (
					player , params.toLowerCase ( ).replaceFirst (
							getIdentifier ( ).toLowerCase ( ) + '_' , "" ) );
		} else {
			return null;
		}
	}
}