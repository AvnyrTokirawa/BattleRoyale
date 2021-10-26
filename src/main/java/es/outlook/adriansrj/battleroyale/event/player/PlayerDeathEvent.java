package es.outlook.adriansrj.battleroyale.event.player;

import es.outlook.adriansrj.battleroyale.game.player.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Thrown whenever a {@link Player} dies.
 *
 * @author AdrianSR / 15/09/2021 / 10:55 p. m.
 */
public class PlayerDeathEvent extends PlayerEventCancellable {
	
	/**
	 * Enumerates the different causes for which a player can die.
	 *
	 * @author AdrianSR / 15/09/2021 / 10:57 p. m.
	 */
	public enum Cause {
		
		/**
		 * Was out of bounds.
		 */
		OUT_OF_BOUNDS,
		
		/**
		 * Bleeding out (<b>was knocked</b>).
		 */
		BLEEDING_OUT,
		
		/**
		 * Damage caused when an entity contacts a block such as a Cactus.
		 * <p>
		 * Damage: 1 (Cactus)
		 */
		CONTACT,
		/**
		 * Damage caused when an entity attacks another entity.
		 * <p>
		 * Damage: variable
		 */
		ENTITY_ATTACK,
		/**
		 * Damage caused when an entity attacks another entity in a sweep attack.
		 * <p>
		 * Damage: variable
		 */
		ENTITY_SWEEP_ATTACK,
		/**
		 * Damage caused when attacked by a projectile.
		 * <p>
		 * Damage: variable
		 */
		PROJECTILE,
		/**
		 * Damage caused by being put in a block
		 * <p>
		 * Damage: 1
		 */
		SUFFOCATION,
		/**
		 * Damage caused when an entity falls a distance greater than 3 blocks
		 * <p>
		 * Damage: fall height - 3.0
		 */
		FALL,
		/**
		 * Damage caused by direct exposure to fire
		 * <p>
		 * Damage: 1
		 */
		FIRE,
		/**
		 * Damage caused due to burns caused by fire
		 * <p>
		 * Damage: 1
		 */
		FIRE_TICK,
		/**
		 * Damage caused due to a snowman melting
		 * <p>
		 * Damage: 1
		 */
		MELTING,
		/**
		 * Damage caused by direct exposure to lava
		 * <p>
		 * Damage: 4
		 */
		LAVA,
		/**
		 * Damage caused by running out of air while in water
		 * <p>
		 * Damage: 2
		 */
		DROWNING,
		/**
		 * Damage caused by being in the area when a block explodes.
		 * <p>
		 * Damage: variable
		 */
		BLOCK_EXPLOSION,
		/**
		 * Damage caused by being in the area when an entity, such as a
		 * Creeper, explodes.
		 * <p>
		 * Damage: variable
		 */
		ENTITY_EXPLOSION,
		/**
		 * Damage caused by falling into the void
		 * <p>
		 * Damage: 4 for players
		 */
		VOID,
		/**
		 * Damage caused by being struck by lightning
		 * <p>
		 * Damage: 5
		 */
		LIGHTNING,
		/**
		 * Damage caused by committing suicide using the command "/kill"
		 * <p>
		 * Damage: 1000
		 */
		SUICIDE,
		/**
		 * Damage caused by starving due to having an empty hunger bar
		 * <p>
		 * Damage: 1
		 */
		STARVATION,
		/**
		 * Damage caused due to an ongoing poison effect
		 * <p>
		 * Damage: 1
		 */
		POISON,
		/**
		 * Damage caused by being hit by a damage potion or spell
		 * <p>
		 * Damage: variable
		 */
		MAGIC,
		/**
		 * Damage caused by Wither potion effect
		 */
		WITHER,
		/**
		 * Damage caused by being hit by a falling block which deals damage
		 * <p>
		 * <b>Note:</b> Not every block deals damage
		 * <p>
		 * Damage: variable
		 */
		FALLING_BLOCK,
		/**
		 * Damage caused in retaliation to another attack by the Thorns
		 * enchantment.
		 * <p>
		 * Damage: 1-4 (Thorns)
		 */
		THORNS,
		/**
		 * Damage caused by a dragon breathing fire.
		 * <p>
		 * Damage: variable
		 */
		DRAGON_BREATH,
		/**
		 * Custom damage.
		 * <p>
		 * Damage: variable
		 */
		CUSTOM,
		/**
		 * Damage caused when an entity runs into a wall.
		 * <p>
		 * Damage: variable
		 */
		FLY_INTO_WALL,
		/**
		 * Damage caused when an entity steps on magma.
		 * <p>
		 * Damage: 1
		 */
		HOT_FLOOR,
		/**
		 * Damage caused when an entity is colliding with too many entities due
		 * to the maxEntityCramming game rule.
		 * <p>
		 * Damage: 6
		 */
		CRAMMING,
		/**
		 * Damage caused when an entity that should be in water is not.
		 * <p>
		 * Damage: 1
		 */
		DRYOUT,
		/**
		 * Damage caused from freezing.
		 * <p>
		 * Damage: 1 or 5
		 */
		FREEZE;
		
		public static Cause of ( EntityDamageEvent.DamageCause damage_cause ) {
			return valueOf ( damage_cause.name ( ) );
		}
	}
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}
	
	protected final Player  killer;
	protected final Cause   cause;
	protected final boolean headshot;
	protected final boolean cancellable;
	protected       boolean keep_level;
	protected       boolean keep_inventory;
	protected       String  death_message;
	
	public PlayerDeathEvent ( Player player , Player killer , Cause cause ,
			boolean headshot , String death_message , boolean cancellable ) {
		super ( player );
		
		this.killer        = killer;
		this.cause         = cause;
		this.headshot      = headshot;
		this.death_message = death_message;
		this.cancellable   = cancellable;
	}
	
	public PlayerDeathEvent ( Player player , Cause cause ,
			String death_message , boolean headshot , boolean cancellable ) {
		this ( player , null , cause , headshot , death_message , cancellable );
	}
	
	public Cause getCause ( ) {
		return cause;
	}
	
	public boolean isHeadshot ( ) {
		return headshot;
	}
	
	public boolean isKeepLevel ( ) {
		return keep_level;
	}
	
	public boolean isKeepInventory ( ) {
		return keep_inventory;
	}
	
	public String getDeathMessage ( ) {
		return death_message;
	}
	
	public Player getKiller ( ) {
		return killer;
	}
	
	/**
	 * Gets whether the player was killed.
	 * <br>
	 * This is the equivalent of using: <b><code>getKiller() != null</code></b>.
	 *
	 * @return whether the player was killed or not.
	 */
	public boolean wasKilled ( ) {
		return killer != null;
	}
	
	/**
	 * Gets whether this event can be cancelled or not.
	 *
	 * @return whether this event can be cancelled or not.
	 */
	public boolean isCancellable ( ) {
		return cancellable;
	}
	
	@Override
	public boolean isCancelled ( ) {
		return cancellable && super.isCancelled ( );
	}
	
	public void setDeathMessage ( String death_message ) {
		this.death_message = death_message;
	}
	
	public void setKeepLevel ( boolean keep_level ) {
		this.keep_level = keep_level;
	}
	
	public void setKeepInventory ( boolean keep_inventory ) {
		this.keep_inventory = keep_inventory;
	}
	
	@Override
	public void setCancelled ( boolean cancelled ) {
		if ( cancellable ) {
			super.setCancelled ( cancelled );
		}
	}
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
}