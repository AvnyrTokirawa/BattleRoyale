package es.outlook.adriansrj.battleroyale.placeholder.node.date;

import es.outlook.adriansrj.battleroyale.placeholder.node.PlaceholderNode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * <b>'br_date'</b> placeholder node.
 *
 * @author AdrianSR / 06/10/2021 / 12:26 p. m.
 */
public class DatePlaceholderNode extends PlaceholderNode {
	
	public static final String IDENTIFIER = "date";
	
	@Override
	public String getSubIdentifier ( ) {
		return IDENTIFIER;
	}
	
	@Override
	protected String onRequest ( org.bukkit.entity.Player player , String params ) {
		return DateTimeFormatter.ofPattern ( "dd/MM/yyyy" ).format ( LocalDate.now ( ) );
	}
}