package es.outlook.adriansrj.battleroyale.compatibility.crackshot;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import es.outlook.adriansrj.battleroyale.compatibility.PluginCompatibilityHandler;
import es.outlook.adriansrj.battleroyale.game.player.Player;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.Constants;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

/**
 * Class responsible for the compatibility with the <b>CrackShot</b> plugin.
 *
 * @author AdrianSR / 23/12/2021 / 01:37 p. m.
 */
public final class CrackShotCompatibilityHandler extends PluginCompatibilityHandler {
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public CrackShotCompatibilityHandler ( BattleRoyale plugin ) {
		super ( plugin );
		register ( );
	}
	
	@EventHandler ( priority = EventPriority.LOWEST, ignoreCancelled = true )
	public void onShoot ( WeaponDamageEntityEvent event ) {
		org.bukkit.entity.Player player    = event.getPlayer ( );
		Player                   br_player = Player.getPlayer ( player );
		Entity                   entity    = event.getVictim ( );
		
		if ( entity instanceof org.bukkit.entity.Player ) {
			org.bukkit.entity.Player damaged    = ( org.bukkit.entity.Player ) entity;
			Player                   br_damaged = Player.getPlayer ( damaged );
			
			// stopping friendly-fire
			if ( Objects.equals ( br_player.getTeam ( ) , br_damaged.getTeam ( ) ) ) {
				event.setCancelled ( true );
			}
			
			// detecting headshots
			if ( event.isHeadshot ( ) ) {
				damaged.setMetadata (
						Constants.HEADSHOT_METADATA_KEY , new FixedMetadataValue (
								BattleRoyale.getInstance ( ) , true ) );
			}
		}
	}
}