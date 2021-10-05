package es.outlook.adriansrj.battleroyale.game.mode.complex;

import es.outlook.adriansrj.battleroyale.util.StringUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Sunday 16 May, 2021 / 05:05 PM
 */
public final class ComplexBattleRoyaleModeDescription {
	
	protected static final Pattern NAME_PATTERN          = Pattern.compile ( "^[A-Za-z0-9 _.-]+$" );
	protected static final String  DESCRIPTION_FILE_NAME = "mode.yml";
	protected static final String  MAIN_KEY              = "main";
	protected static final String  NAME_KEY              = "name";
	protected static final String  AUTHORS_KEY           = "authors";
	protected static final String  AUTHOR_KEY            = "author";
	
	/**
	 * <br>
	 * @param file the file of the complex battle mode that is to be loaded.
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected static ComplexBattleRoyaleModeDescription of ( File file ) throws IllegalArgumentException {
		ComplexBattleRoyaleModeDescription result = null;
		JarFile                            jar    = null;
		
		try {
			jar = new JarFile ( file );
			JarEntry description_entry = jar.getJarEntry ( DESCRIPTION_FILE_NAME );
			
			if ( description_entry == null ) {
				throw new IllegalArgumentException ( "Description file (" + DESCRIPTION_FILE_NAME
															 + ") is missing from '" + file.getName ( ) + "'!" );
			}
			
			InputStream       input  = jar.getInputStream ( description_entry );
			BufferedReader    reader = new BufferedReader ( new InputStreamReader ( input ) );
			YamlConfiguration yaml   = YamlConfiguration.loadConfiguration ( reader );
			
			result = new ComplexBattleRoyaleModeDescription ( yaml );
		} catch ( IOException ex ) {
			ex.printStackTrace ( );
		} finally {
			try {
				jar.close ( );
			} catch ( IOException ex ) {
				ex.printStackTrace ( );
			}
		}
		
		return result;
	}
	
	protected String          main;
	protected String          name;
	protected List < String > authors;
	
	private ComplexBattleRoyaleModeDescription ( YamlConfiguration yaml ) {
		this.main    = yaml.getString ( MAIN_KEY , StringUtil.EMPTY ).trim ( );
		this.name    = yaml.getString ( NAME_KEY , StringUtil.EMPTY ).trim ( );
		this.authors = yaml.getStringList ( AUTHORS_KEY );
		
		if ( authors.isEmpty ( ) && yaml.isString ( AUTHOR_KEY ) ) {
			authors.add ( yaml.getString ( AUTHOR_KEY ).trim ( ) );
		}
	}
	
	public String getMain ( ) {
		return main;
	}
	
	public String getName ( ) {
		return name;
	}
	
	public List < String > getAuthors ( ) {
		return Collections.unmodifiableList ( authors );
	}
	
	public boolean isValid ( ) {
		return StringUtil.isNotBlank ( name ) && NAME_PATTERN.matcher ( name ).matches ( )
				&& StringUtil.isNotBlank ( main );
	}
}