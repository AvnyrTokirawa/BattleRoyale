package es.outlook.adriansrj.battleroyale.arena;

import es.outlook.adriansrj.battleroyale.enums.EnumArenaStat;
import es.outlook.adriansrj.battleroyale.util.Validate;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * {@link BattleRoyaleArena} stats container.
 *
 * @author AdrianSR / 22/10/2021 / 12:46 p. m.
 */
public class BattleRoyaleArenaStats {
	
	protected final BattleRoyaleArena              arena;
	protected final Map < EnumArenaStat, Integer > stat_values = new EnumMap <> ( EnumArenaStat.class );
	
	public BattleRoyaleArenaStats ( BattleRoyaleArena arena ) {
		this.arena = arena;
		
		// mapping 0 as default stat values, so it
		// will never return null.
		for ( EnumArenaStat stat_type : EnumArenaStat.values ( ) ) {
			stat_values.put ( stat_type , 0 );
		}
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	public Map < EnumArenaStat, Integer > getAll ( ) {
		return Collections.unmodifiableMap ( stat_values );
	}
	
	public int get ( EnumArenaStat stat_type ) {
		return stat_values.get ( stat_type );
	}
	
	public void set ( EnumArenaStat stat_type , int value ) {
		Validate.notNull ( stat_type , "stat type cannot be null" );
		Validate.isTrue ( value >= 0 , "value must be >= 0" );
		
		this.stat_values.put ( stat_type , value );
	}
	
	public void increment ( EnumArenaStat stat_type , int amount ) {
		Validate.notNull ( stat_type , "stat type cannot be null" );
		
		set ( stat_type , Math.max ( get ( stat_type ) + amount , 0 ) );
	}
	
	public void decrement ( EnumArenaStat stat_type , int amount ) {
		Validate.notNull ( stat_type , "stat type cannot be null" );
		
		set ( stat_type , Math.max ( get ( stat_type ) - amount , 0 ) );
	}
	
	public void setAll ( Map < EnumArenaStat, Integer > stat_values ) {
		stat_values.forEach ( ( stat_type , value ) -> {
			if ( value >= 0 ) {
				this.set ( stat_type , value );
			}
		} );
	}
	
	public void reset ( ) {
		for ( EnumArenaStat stat_type : EnumArenaStat.values ( ) ) {
			stat_values.put ( stat_type , 0 );
		}
	}
	
	@Override
	public String toString ( ) {
		return "BattleRoyaleArenaStats{" + stat_values + '}';
	}
}
