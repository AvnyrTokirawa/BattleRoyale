package es.outlook.adriansrj.battleroyale.arena.drop;

import es.outlook.adriansrj.battleroyale.arena.BattleRoyaleArena;
import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.util.task.BukkitRunnableWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Class responsible for keeping track of the {@link ItemDrop}
 * in a certain arena.
 *
 * @author AdrianSR / 15/10/2021 / 07:44 p. m.
 */
public class ItemDropManager {
	
	/**
	 * Task responsible for ticking drops registered
	 * in a given {@link ItemDropManager}.
	 *
	 * @author AdrianSR / 15/10/2021 / 08:18 p. m.
	 */
	protected static class DropTickTask extends BukkitRunnableWrapper {
		
		protected final ItemDropManager manager;
		
		public DropTickTask ( ItemDropManager manager ) {
			this.manager = manager;
		}
		
		@Override
		public void run ( ) {
			synchronized ( manager.synchronized_view ) {
				Iterator < ItemDrop > iterator = manager.synchronized_view.iterator ( );
				
				while ( iterator.hasNext ( ) ) {
					ItemDrop drop   = iterator.next ( );
					boolean  remove = false;
					
					try {
						if ( drop.exists ( ) ) {
							drop.tick ( );
						} else {
							remove = true;
							drop.remove ( );
						}
					} catch ( Exception ex ) {
						ex.printStackTrace ( );
					}
					
					if ( remove ) {
						iterator.remove ( );
					}
				}
			}
		}
	}
	
	protected final ArrayList < ItemDrop > drops             = new ArrayList <> ( );
	protected final List < ItemDrop >      unmodifiable_view = Collections.unmodifiableList ( drops );
	protected final List < ItemDrop >      synchronized_view = Collections.synchronizedList ( drops );
	
	protected final BattleRoyaleArena arena;
	protected final DropTickTask      tick_task;
	
	public ItemDropManager ( BattleRoyaleArena arena ) {
		this.arena     = arena;
		this.tick_task = new DropTickTask ( this );
		
		// scheduling tick task
		this.tick_task.runTaskTimerAsynchronously (
				BattleRoyale.getInstance ( ) , 0L , 0L );
	}
	
	public BattleRoyaleArena getArena ( ) {
		return arena;
	}
	
	public List < ItemDrop > getDrops ( ) {
		return unmodifiable_view;
	}
	
	public boolean register ( ItemDrop drop ) {
		if ( drop != null && drop.exists ( ) ) {
			// making sure is initialized.
			drop.initialize ( );
			
			return synchronized_view.add ( drop );
		} else {
			return false;
		}
	}
	
	public boolean unregister ( ItemDrop drop ) {
		return synchronized_view.remove ( drop );
	}
}