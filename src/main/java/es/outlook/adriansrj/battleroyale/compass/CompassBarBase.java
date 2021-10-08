package es.outlook.adriansrj.battleroyale.compass;

import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.math.DirectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.Objects;

/**
 * Base implementation of {@link CompassBar}.
 *
 * @author AdrianSR / 09/09/2021 / 04:47 p. m.
 */
public abstract class CompassBarBase extends CompassBar {
	
	protected BossBar handle;
	
	/** 3 times the model (model-model-model) */
	protected final String model_x3;
	
	protected CompassBarBase ( Player player ) {
		super ( player );
		
		// initializing handle
		this.handle = Bukkit.createBossBar ( StringUtil.EMPTY , getColor ( ) , getStyle ( ) );
		
		// calculating model x3
		String model = StringUtil.stripColors ( Objects.requireNonNull (
				getModel ( ) , "getMode() returned null" ) );
		
		this.model_x3 = model + model + model;
	}
	
	@Override
	public boolean isVisible ( ) {
		return handle != null && handle.isVisible ( );
	}
	
	protected abstract String getModel ( );
	
	protected abstract int getLength ( );
	
	protected abstract BarColor getColor ( );
	
	protected abstract String getTextColor ( );
	
	protected abstract BarStyle getStyle ( );
	
	@Override
	public void setVisible ( boolean visible ) {
		if ( handle != null ) {
			handle.setVisible ( visible );
		}
	}
	
	@Override
	public void update ( ) {
		if ( handle != null && isVisible ( ) ) {
			player.getBukkitPlayerOptional ( ).ifPresent ( bukkit -> {
				String base        = getModel ( );
				int    begin_index = base.length ( );
				int    angle       = ( int ) DirectionUtil.normalize ( bukkit.getLocation ( ).getYaw ( ) );
				int    base_length = base.length ( );
				float  factor      = angle / 360.0F;
				int    bar_length  = getLength ( );
				int    index       = ( begin_index - ( bar_length / 2 ) ) + ( int ) ( base_length * factor );
				
				handle.setTitle ( getTextColor ( ) + StringUtil.limit ( model_x3.substring ( index ) , bar_length ) );
			} );
		}
	}
	
	@Override
	public void destroy ( ) {
		if ( handle != null ) {
			this.handle.removeAll ( );
			this.handle = null;
		}
	}
	
	@Override
	protected void onPlayerReconnect ( ) {
		player.getBukkitPlayerOptional ( ).ifPresent ( bukkit -> {
			handle.removeAll ( );
			handle.addPlayer ( bukkit );
		} );
	}
}