package es.outlook.adriansrj.battleroyale.util;

/**
 * Useful class for dealing with strings.
 *
 * @author AdrianSR / 02/09/2021 / 06:35 p. m.
 */
public class StringUtil extends es.outlook.adriansrj.core.util.StringUtil {
	
	public static String replaceFileCharacters ( String string , String replacement ) {
		return string.replace ( "\\" , replacement ).replaceAll ( "[./]" , replacement );
	}
	
	public static String formatBattlefieldName ( String name ) {
		return replaceFileCharacters ( name , "-" );
	}
}