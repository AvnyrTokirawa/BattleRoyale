package es.outlook.adriansrj.battleroyale.util.task;

import es.outlook.adriansrj.core.util.reflection.general.ClassReflection;
import es.outlook.adriansrj.core.util.reflection.general.FieldReflection;
import es.outlook.adriansrj.core.util.server.Version;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link BukkitRunnable} implementation that enables the
 * compatibility with legacy versions.
 *
 * @author AdrianSR / 31/10/2021 / 10:15 a. m.
 */
public abstract class BukkitRunnableWrapper extends BukkitRunnable {
	
	// it is not possible to override
	// as the isCancelled method was implemented
	// in 1_12_R1.
	public synchronized boolean isCancelled ( ) throws IllegalStateException {
		if ( Version.getServerVersion ( ).isNewerEquals ( Version.v1_12_R1 ) ) {
			return super.isCancelled ( );
		} else {
			BukkitScheduler scheduler = Bukkit.getScheduler ( );
			
			try {
				Class < ? > craft_scheduler_class = ClassReflection.getCraftClass (
						"CraftScheduler" , "scheduler" );
				Class < ? > craft_task_class = ClassReflection.getCraftClass (
						"CraftTask" , "scheduler" );
				Field period_field  = FieldReflection.getAccessible ( craft_task_class , "period" );
				Field runners_field = FieldReflection.getAccessible ( craft_scheduler_class , "runners" );
				
				// searching in runners
				for ( Object uncast_task : ( ( ConcurrentHashMap < ?, ? > ) runners_field.get ( scheduler ) ).values ( ) ) {
					if ( uncast_task != null && craft_task_class.isAssignableFrom ( uncast_task.getClass ( ) )
							&& ( ( BukkitTask ) uncast_task ).getTaskId ( ) == getTaskId ( ) ) {
						return period_field.getLong ( uncast_task ) == -2L;
					}
				}
				
				// searching in pending tasks
				for ( BukkitTask task : scheduler.getPendingTasks ( ) ) {
					if ( task.getTaskId ( ) == getTaskId ( ) && craft_task_class.isAssignableFrom ( task.getClass ( ) ) ) {
						return period_field.getLong ( task ) == -2L;
					}
				}
			} catch ( ClassNotFoundException | NoSuchFieldException | IllegalAccessException ex ) {
				throw new IllegalStateException ( ex );
			}
			
			return false;
		}
	}
}
