package es.outlook.adriansrj.battleroyale.battlefield.minimap;

import es.outlook.adriansrj.battleroyale.player.Player;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import java.util.UUID;

/**
 * Minimap settings holder.
 *
 * @author AdrianSR / 02/09/2021 / 11:35 a. m.
 */
public class MinimapSettings {
	
	protected final UUID        id;
	protected       MinimapZoom zoom;
	
	protected MinimapSettings ( UUID id ) {
		this.id   = id;
		this.zoom = MinimapZoom.NORMAL;
	}
	
	public MinimapSettings ( org.bukkit.entity.Player player ) {
		this ( player.getUniqueId ( ) );
	}
	
	public MinimapSettings ( Player player ) {
		this ( player.getUniqueId ( ) );
	}
	
	public Player getPlayer ( ) {
		return Player.getPlayer ( id );
	}
	
	public MinimapZoom getZoom ( ) {
		return zoom;
	}
	
	public void setZoom ( MinimapZoom zoom ) {
		Validate.notNull ( zoom , "zoom cannot be null!" );
		
		this.zoom = zoom;
	}
	
	public void toggleZoom ( ) {
		int index = ArrayUtils.indexOf ( MinimapZoom.values ( ) , zoom );
		
		if ( ( index + 1 ) < MinimapZoom.values ( ).length ) {
			zoom = MinimapZoom.values ( )[ index + 1 ];
		} else {
			zoom = MinimapZoom.values ( )[ 0 ];
		}
	}
}
