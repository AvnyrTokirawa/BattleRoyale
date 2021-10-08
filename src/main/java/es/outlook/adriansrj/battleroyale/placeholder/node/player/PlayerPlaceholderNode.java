package es.outlook.adriansrj.battleroyale.placeholder.node.player;

import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNode;
import org.bukkit.entity.Player;

/**
 * <b>'br_player'</b> placeholder node.
 *
 * @author AdrianSR / 06/10/2021 / 11:00 a. m.
 */
public class PlayerPlaceholderNode extends PlaceholderNode {
	
	@Override
	public String getSubIdentifier ( ) {
		return "player";
	}
	
	@Override
	protected String onRequest ( Player player , String params ) {
		// TODO
		return null;
	}
}
