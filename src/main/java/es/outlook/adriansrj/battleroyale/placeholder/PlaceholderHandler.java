package es.outlook.adriansrj.battleroyale.placeholder;

import es.outlook.adriansrj.battleroyale.main.BattleRoyale;
import es.outlook.adriansrj.battleroyale.placeholder.processor.PlaceholderProcessor;
import es.outlook.adriansrj.battleroyale.placeholder.processor.internal.InternalPlaceholderProcessor;
import es.outlook.adriansrj.battleroyale.placeholder.processor.papi.PapiPlaceholderProcessor;
import es.outlook.adriansrj.battleroyale.util.PluginUtil;
import es.outlook.adriansrj.battleroyale.util.StringUtil;
import es.outlook.adriansrj.core.handler.PluginHandler;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.concurrent.Callable;

/**
 * Class responsible for handling the placeholders of the plugin.
 *
 * @author AdrianSR / 06/10/2021 / 09:45 a. m.
 */
public final class PlaceholderHandler extends PluginHandler {
	
	public static PlaceholderHandler getInstance ( ) {
		return getPluginHandler ( PlaceholderHandler.class );
	}
	
	/**
	 * Enumerates the supported processors.
	 *
	 * @author AdrianSR / 06/10/2021 / 10:12 a. m.
	 */
	private enum Processor {
		
		INTERNAL ( InternalPlaceholderProcessor.class ),
		PAPI_PLACEHOLDER_API ( PapiPlaceholderProcessor.class , PluginUtil :: isPlaceholderAPIEnabled ),
		//		MVDW_PLACEHOLDER_API ( MvwdPlaceholderProcessor.class , PluginUtil :: isMVdWPlaceholderAPIEnabled ),
		
		;
		
		private final Class < ? extends PlaceholderProcessor > clazz;
		private final Callable < Boolean >                     init_flag;
		
		Processor ( Class < ? extends PlaceholderProcessor > clazz , Callable < Boolean > init_flag ) {
			this.clazz     = clazz;
			this.init_flag = init_flag;
		}
		
		Processor ( Class < ? extends PlaceholderProcessor > clazz ) {
			this ( clazz , ( ) -> {
				return Boolean.TRUE; // no special checks are required by default.
			} );
		}
		
		boolean canInitialize ( ) {
			try {
				return init_flag.call ( );
			} catch ( Exception ex ) {
				ex.printStackTrace ( );
				return false;
			}
		}
	}
	
	private final EnumMap < Processor, PlaceholderProcessor > processors = new EnumMap <> ( Processor.class );
	
	/**
	 * Constructs the plugin handler.
	 *
	 * @param plugin the plugin to handle.
	 */
	public PlaceholderHandler ( BattleRoyale plugin ) {
		super ( plugin );
		
		// initializing processors
		for ( Processor processor : Processor.values ( ) ) {
			try {
				if ( processor.canInitialize ( ) ) {
					processors.put ( processor ,
									 processor.clazz.getConstructor ( ).newInstance ( ) );
				}
			} catch ( Exception ex ) {
				ex.printStackTrace ( );
			}
		}
	}
	
	public String setPlaceholders ( Player player , String contents ) {
		String result = contents;
		
		for ( Processor processor : Processor.values ( ) ) {
			if ( processor != Processor.INTERNAL ) {
				PlaceholderProcessor instance = processors.get ( processor );
				
				if ( instance != null ) {
					result = StringUtil.defaultString (
							instance.process ( player , result ) , result );
				}
			}
		}
		
		// there are probably no external-processors enabled, so
		// let's process it from the internal processor to ensure
		// the process is successful.
		return StringUtil.defaultString (
				processors.get ( Processor.INTERNAL ).process ( player , result ) , result );
	}
	
	public String setPlaceholders ( Object context , String contents ) {
		// process currently supported only by the internal processor.
		return StringUtil.defaultString (
				processors.get ( Processor.INTERNAL ).process ( context , contents ) , contents );
	}
	
	@Override
	protected boolean isAllowMultipleInstances ( ) {
		return false;
	}
}