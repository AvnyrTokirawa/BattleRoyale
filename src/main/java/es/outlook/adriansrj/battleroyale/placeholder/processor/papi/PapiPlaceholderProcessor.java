package es.outlook.adriansrj.battleroyale.placeholder.processor.papi;

import es.outlook.adriansrj.battleroyale.placeholder.processor.PlaceholderProcessor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 * Process placeholders from the PlaceholderAPI plugin.
 *
 * @author AdrianSR / 06/10/2021 / 10:10 a. m.
 */
public class PapiPlaceholderProcessor implements PlaceholderProcessor {
	
	private final PapiPlaceholderExpansion expansion;
	
	public PapiPlaceholderProcessor ( ) {
		this.expansion = new PapiPlaceholderExpansion ( this );
		this.expansion.register ( );
	}
	
	public PapiPlaceholderExpansion getExpansion ( ) {
		return expansion;
	}
	
	@Override
	public String process ( Player player , String contents ) {
		return PlaceholderAPI.setPlaceholders ( player , contents );
	}
}