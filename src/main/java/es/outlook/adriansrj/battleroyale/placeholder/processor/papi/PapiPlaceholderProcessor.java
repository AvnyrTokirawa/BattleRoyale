package es.outlook.adriansrj.battleroyale.placeholder.processor.papi;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNode;
import es.outlook.adriansrj.battleroyale.placeholder.processor.PlaceholderProcessor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Process placeholders from the PlaceholderAPI plugin.
 *
 * @author AdrianSR / 06/10/2021 / 10:10 a. m.
 */
public class PapiPlaceholderProcessor extends PlaceholderExpansion implements PlaceholderProcessor {
	
	public PapiPlaceholderProcessor ( ) {
		this.register ( );
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
	public String process ( Player player , String contents ) {
		return PlaceholderAPI.setPlaceholders ( player , contents );
	}
	
	@Override
	public String onPlaceholderRequest ( Player player , @NotNull String params ) {
		PlaceholderNode node = matchNode ( params );
		
		if ( node != null ) {
			return node.onPlaceholderRequest (
					player , params.toLowerCase ( ).replaceFirst (
							getIdentifier ( ).toLowerCase ( ) + '_' , "" ) );
		} else {
			return null;
		}
	}
}