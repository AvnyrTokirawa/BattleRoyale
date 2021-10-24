package es.outlook.adriansrj.battleroyale.game.mode.complex;

import es.outlook.adriansrj.battleroyale.enums.BattleRoyaleModeDefaultPresentation;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleMode;
import es.outlook.adriansrj.battleroyale.game.mode.BattleRoyaleModeLoader;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * {@link ComplexBattleRoyaleMode} loader.
 *
 * @author AdrianSR / Sunday 16 May, 2021 / 04:21 PM
 */
public class ComplexBattleRoyaleModeLoader extends BattleRoyaleModeLoader {
	
	private final Map < String, Class < ? > >                 classes = new HashMap < String, Class < ? > > ( );
	private final List < ComplexBattleRoyaleModeClassLoader > loaders = new CopyOnWriteArrayList <> ( );
	
	@Override
	public BattleRoyaleModeDefaultPresentation getPresentation ( ) {
		return BattleRoyaleModeDefaultPresentation.JAR_FILE;
	}
	
	@SuppressWarnings ( "resource" )
	@Override
	public BattleRoyaleMode load ( File file ) throws IllegalArgumentException {
		Validate.isTrue ( getPresentation ( ).getFileFilter ( ).accept ( file ) ,
						  "unsupported file extension: " + file.getName ( ) );
		
		try {
			ComplexBattleRoyaleModeDescription description = ComplexBattleRoyaleModeDescription.of ( file );
			
			if ( description != null && description.isValid ( ) ) {
				ComplexBattleRoyaleModeClassLoader class_loader = new ComplexBattleRoyaleModeClassLoader (
						this , getClass ( ).getClassLoader ( ) , description , file );
				ComplexBattleRoyaleMode result = class_loader.load ( );
				
				if ( result != null ) {
					result.description = description;
					result.file        = file;
					
					loaders.add ( class_loader );
					return result;
				}
			} else {
				throw new IllegalArgumentException ( "invalid description: " + file.getName ( ) );
			}
		} catch ( MalformedURLException | InvocationTargetException ex ) {
			ex.printStackTrace ( );
		}
		return null;
	}
	
	Class < ? > getClassByName ( final String name ) {
		Class < ? > cachedClass = classes.get ( name );
		
		if ( cachedClass != null ) {
			return cachedClass;
		} else {
			for ( ComplexBattleRoyaleModeClassLoader loader : loaders ) {
				try {
					cachedClass = loader.findClass ( name , false );
				} catch ( ClassNotFoundException cnfe ) { }
				if ( cachedClass != null ) {
					return cachedClass;
				}
			}
		}
		return null;
	}
	
	void setClass ( final String name , final Class < ? > clazz ) {
		if ( !classes.containsKey ( name ) ) {
			classes.put ( name , clazz );
			
			if ( ConfigurationSerializable.class.isAssignableFrom ( clazz ) ) {
				Class < ? extends ConfigurationSerializable > serializable = clazz
						.asSubclass ( ConfigurationSerializable.class );
				ConfigurationSerialization.registerClass ( serializable );
			}
		}
	}
}