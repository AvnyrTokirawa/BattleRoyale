package es.outlook.adriansrj.battleroyale.game.mode.complex;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A {@link URLClassLoader} implementation intended to
 * load the classes of a complex battle royale mode.
 * <p>
 * @author AdrianSR / Sunday 16 May, 2021 / 05:03 PM
 */
class ComplexBattleRoyaleModeClassLoader extends URLClassLoader {
	
	private final Map < String, Class < ? > > classes = new HashMap <> ( );
	
	private final ComplexBattleRoyaleModeLoader      loader;
	private final ComplexBattleRoyaleModeDescription description;
	
	ComplexBattleRoyaleModeClassLoader ( final ComplexBattleRoyaleModeLoader loader , final ClassLoader parent ,
			final ComplexBattleRoyaleModeDescription description , final File file ) throws MalformedURLException {
		super ( new URL[] { file.toURI ( ).toURL ( ) } , parent );
		
		this.loader      = loader;
		this.description = description;
	}
	
	ComplexBattleRoyaleMode load ( ) throws IllegalArgumentException , InvocationTargetException {
		Class < ? > uncast_main_class = null;
		
		try {
			uncast_main_class = Class.forName ( description.main , true , this );
		} catch ( ClassNotFoundException ex ) {
			throw new IllegalArgumentException ( "couldn't find main class: " + description.main , ex );
		}
		
		Class < ? extends ComplexBattleRoyaleMode > main_class = null;
		
		try {
			main_class = uncast_main_class.asSubclass ( ComplexBattleRoyaleMode.class );
		} catch ( ClassCastException ex ) {
			throw new IllegalArgumentException (
					"main class doesn't extend " + ComplexBattleRoyaleMode.class.getName ( ) , ex );
		}
		
		try {
			return main_class.getConstructor ( ).newInstance ( );
		} catch ( InstantiationException ex ) {
			throw new IllegalArgumentException ( "abnormal type" , ex );
		} catch ( IllegalAccessException | NoSuchMethodException ex_b ) {
			throw new IllegalArgumentException ( "a public constructor with no parameters couldn't be found!" , ex_b );
		}
	}
	
	@Override
	protected Class < ? > findClass ( String name ) throws ClassNotFoundException {
		return findClass ( name , true );
	}
	
	Class < ? > findClass ( String name , boolean checkGlobal ) throws ClassNotFoundException {
		Class < ? > result = classes.get ( name );
		
		if ( result == null ) {
			if ( checkGlobal ) {
				result = loader.getClassByName ( name );
			}
			
			if ( result == null ) {
				result = super.findClass ( name );
				
				if ( result != null ) {
					loader.setClass ( name , result );
				}
			}
			
			classes.put ( name , result );
		}
		
		return result;
	}
	
	Set < String > getClasses ( ) {
		return classes.keySet ( );
	}
}
