package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.configuration.ConfigurationEntry;
import es.outlook.adriansrj.battleroyale.placeholder.PlaceholderHandler;
import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import es.outlook.adriansrj.core.menu.size.ItemMenuSize;
import es.outlook.adriansrj.core.util.StringUtil;
import es.outlook.adriansrj.core.util.material.UniversalMaterial;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Shop GUIs configuration entries in an enum.
 *
 * @author AdrianSR / 07/11/2021 / 11:47 a. m.
 */
public enum EnumShopGUIsConfiguration implements ConfigurationEntry {
	
	// -------- commons
	COMMON_BUTTON_BACK_MATERIAL (
			"common.button.back.material" ,
			"back button material" ,
			UniversalMaterial.ARROW ),
	
	COMMON_BUTTON_NEXT_MATERIAL (
			"common.button.next.material" ,
			"next button material" ,
			UniversalMaterial.ARROW ),
	
	COMMON_BUTTON_CLOSE_MATERIAL (
			"common.button.close.material" ,
			"close button material" ,
			UniversalMaterial.BARRIER ),
	
	// -------- main shop gui
	MAIN_GUI_TITLE ( "main.title" ,
					 "main shop gui title" ,
					 ChatColor.BLACK + "Shop" ),
	
	MAIN_GUI_BUS_SHOP_BUTTON_POSITION (
			"main.button.bus-shop.position" ,
			"position of the button to open the bus shop gui" ,
			12 ),
	
	MAIN_GUI_BUS_SHOP_BUTTON_TEXT (
			"main.button.bus-shop.display-text" ,
			"display text of the button to open the bus shop gui" ,
			ChatColor.GOLD + "Bus Shop" ),
	
	MAIN_GUI_BUS_SHOP_BUTTON_MATERIAL (
			"main.button.bus-shop.material" ,
			"material of the button to open the bus shop gui" ,
			UniversalMaterial.MINECART ),
	
	MAIN_GUI_PARACHUTE_SHOP_BUTTON_POSITION (
			"main.button.parachute-shop.position" ,
			"position of the button to open the parachute shop gui" ,
			14 ),
	
	MAIN_GUI_PARACHUTE_SHOP_BUTTON_TEXT (
			"main.button.parachute-shop.display-text" ,
			"display text of the button to open the parachute shop gui" ,
			ChatColor.GOLD + "Parachute Shop" ),
	
	MAIN_GUI_PARACHUTE_SHOP_BUTTON_MATERIAL (
			"main.button.parachute-shop.material" ,
			"material of the button to open the parachute shop gui" ,
			UniversalMaterial.SADDLE ),
	
	MAIN_GUI_PARACHUTE_COLOR_SHOP_BUTTON_POSITION (
			"main.button.parachute-color-shop.position" ,
			"position of the button to open the parachute color shop gui" ,
			22 ),
	
	MAIN_GUI_PARACHUTE_COLOR_SHOP_BUTTON_TEXT (
			"main.button.parachute-color-shop.display-text" ,
			"display text of the button to open the parachute color shop gui" ,
			ChatColor.GOLD + "Parachute Color Shop" ),
	
	MAIN_GUI_PARACHUTE_COLOR_SHOP_BUTTON_MATERIAL (
			"main.button.parachute-color-shop.material" ,
			"material of the button to open the parachute color shop gui" ,
			UniversalMaterial.WHITE_WOOL ),
	
	// -------- confirmation gui
	CONFIRMATION_GUI_TITLE ( "confirmation.title" ,
							 "purchase confirmation gui title" ,
							 ChatColor.BLACK + "Confirmation" ),
	
	CONFIRMATION_GUI_SIZE ( "confirmation.size" ,
							"purchase confirmation gui size" , ItemMenuSize.THREE_LINE ),
	
	CONFIRMATION_GUI_ITEM_CONFIRM_TEXT ( "confirmation.item.confirm.text" ,
										 "confirm purchase item" ,
										 ChatColor.GREEN + "Yes, proceed with the purchase" ),
	
	CONFIRMATION_GUI_ITEM_CONFIRM_POSITION ( "confirmation.item.confirm.position" ,
											 "confirm purchase item position" ,
											 14 ),
	
	CONFIRMATION_GUI_ITEM_CANCEL_TEXT ( "confirmation.item.cancel.text" ,
										"cancel purchase item text" ,
										ChatColor.RED + "No, cancel the purchase" ),
	
	CONFIRMATION_GUI_ITEM_CANCEL_POSITION ( "confirmation.item.cancel.position" ,
											"cancel purchase item position" ,
											12 ),
	
	// -------- bus shop gui
	BUS_GUI_TITLE ( "bus-shop.title" ,
					"bus shop gui title" ,
					ChatColor.BLACK + "Available Buses" ),
	
	BUS_GUI_SIZE ( "bus-shop.size" ,
				   "bus shop gui size" , ItemMenuSize.THREE_LINE ),
	
	BUS_GUI_ITEM_TEXT_FORMAT (
			"bus-shop.bus-item.text-format" ,
			"bus shop gui: bus items display text format" ,
			ChatColor.GOLD + "%s" ),
	
	BUS_GUI_ITEM_DESCRIPTION_FORMAT (
			"bus-shop.bus-item.description-format" ,
			"bus shop gui: bus items description format" ,
			"" ,
			ChatColor.GOLD + "Price: " + ChatColor.GREEN + "%d" ,
			"" ,
			ChatColor.GREEN + "Click to purchase" ),
	
	BUS_GUI_ITEM_PURCHASED_MESSAGE (
			"bus-shop.bus-item.purchased-message" ,
			"bus shop gui: message players will receive when" +
					"\npurchasing a bus" ,
			ChatColor.GREEN + "Congratulations. This bus is now yours!" ),
	
	BUS_GUI_ITEM_NOT_PURCHASED_MESSAGE (
			"bus-shop.bus-item.not-purchased-message" ,
			"bus shop gui: message players will receive when" +
					"\ntrying a purchase a bus with insufficient balance." ,
			ChatColor.DARK_RED + "Not enough balance to purchase this bus!" ),
	
	// -------- parachute shop gui
	PARACHUTE_GUI_TITLE ( "parachute-shop.title" ,
						  "parachute shop gui title" ,
						  ChatColor.BLACK + "Available Parachutes" ),
	
	PARACHUTE_GUI_SIZE ( "parachute-shop.size" ,
						 "parachute shop gui size" , ItemMenuSize.THREE_LINE ),
	
	PARACHUTE_GUI_ITEM_TEXT_FORMAT (
			"parachute-shop.parachute-item.text-format" ,
			"parachute shop gui: parachute items display text format" ,
			ChatColor.GOLD + "%s" ),
	
	PARACHUTE_GUI_ITEM_DESCRIPTION_FORMAT (
			"parachute-shop.parachute-item.description-format" ,
			"parachute shop gui: parachute items description format" ,
			"" ,
			ChatColor.GOLD + "Price: " + ChatColor.GREEN + "%d" ,
			"" ,
			ChatColor.GREEN + "Click to purchase" ),
	
	PARACHUTE_GUI_ITEM_PURCHASED_MESSAGE (
			"parachute-shop.parachute-item.purchased-message" ,
			"parachute shop gui: message players will receive when" +
					"\npurchasing a parachute" ,
			ChatColor.GREEN + "Congratulations. This parachute is now yours!" ),
	
	PARACHUTE_GUI_ITEM_NOT_PURCHASED_MESSAGE (
			"parachute-shop.parachute-item.not-purchased-message" ,
			"parachute shop gui: message players will receive when" +
					"\ntrying a purchase a parachute with insufficient balance." ,
			ChatColor.DARK_RED + "Not enough balance to purchase this parachute!" ),
	
	// -------- parachute color shop gui
	PARACHUTE_COLOR_GUI_TITLE ( "parachute-color-shop.title" ,
								"parachute color shop gui title" ,
								ChatColor.BLACK + "Available Colors" ),
	
	PARACHUTE_COLOR_GUI_SIZE ( "parachute-color-shop.size" ,
							   "parachute color shop gui size" , ItemMenuSize.THREE_LINE ),
	
	PARACHUTE_COLOR_GUI_ITEM_TEXT_FORMAT (
			"parachute-color-shop.parachute-color-item.text-format" ,
			"parachute color shop gui: parachute color items display text format" ,
			ChatColor.GOLD + "%s" ),
	
	PARACHUTE_COLOR_GUI_ITEM_DESCRIPTION_FORMAT (
			"parachute-color-shop.parachute-color-item.description-format" ,
			"parachute color shop gui: parachute color items description format" ,
			"" ,
			ChatColor.GOLD + "Price: " + ChatColor.GREEN + "%d" ,
			"" ,
			ChatColor.GREEN + "Click to purchase" ),
	
	PARACHUTE_COLOR_GUI_ITEM_PURCHASED_MESSAGE (
			"parachute-color-shop.parachute-color-item.purchased-message" ,
			"parachute color shop gui: message players will receive when" +
					"\npurchasing a parachute color" ,
			ChatColor.GREEN + "Congratulations. This color is now yours!" ),
	
	PARACHUTE_COLOR_GUI_ITEM_NOT_PURCHASED_MESSAGE (
			"parachute-color-shop.parachute-color-item.not-purchased-message" ,
			"parachute color shop gui: message players will receive when" +
					"\ntrying a purchase a parachute color with insufficient balance." ,
			ChatColor.DARK_RED + "Not enough balance to purchase this parachute color!" ),
	;
	
	private final String      key;
	private final String      comment;
	private final Object      default_value;
	private final Class < ? > type;
	private       Object      value;
	
	EnumShopGUIsConfiguration ( String key , String comment , Object default_value , Class < ? > type ) {
		this.key           = key;
		this.comment       = comment;
		this.default_value = default_value instanceof String ? StringEscapeUtils.escapeJava (
				StringUtil.untranslateAlternateColorCodes ( ( String ) default_value ) ) : default_value;
		this.value         = default_value;
		this.type          = type;
	}
	
	EnumShopGUIsConfiguration ( String key , String comment , Object default_value ) {
		this ( key , comment , default_value , default_value.getClass ( ) );
		
	}
	
	EnumShopGUIsConfiguration ( String key , String comment , Enum < ? > default_value ) {
		// must set value type to string, so we can
		// match the right value later on.
		this ( key , comment , default_value.name ( ) , String.class );
	}
	
	// multi-line string
	EnumShopGUIsConfiguration ( String key , String comment , String... default_value ) {
		this ( key , comment , StringUtil.join ( default_value , "\n" ) );
	}
	
	@Override
	public String getKey ( ) {
		return key;
	}
	
	@Override
	public String getComment ( ) {
		return comment;
	}
	
	@Override
	public Object getDefaultValue ( ) {
		return default_value;
	}
	
	@Override
	public Object getValue ( ) {
		return value;
	}
	
	@Override
	public Class < ? > getValueType ( ) {
		return type;
	}
	
	@Override
	public String getAsString ( ) {
		return getAsString ( null );
	}
	
	public String getAsString ( Player player ) {
		return PlaceholderHandler.getInstance ( ).setPlaceholders ( player , StringEscapeUtils.unescapeJava (
				StringUtil.translateAlternateColorCodes ( ConfigurationEntry.super.getAsString ( ) ) ) );
	}
	
	@Override
	public List < String > getAsStringList ( ) {
		return Arrays.asList ( getAsString ( ).split ( "\n" ) );
	}
	
	public Material getAsMaterial ( ) {
		UniversalMaterial wrapper = getAsUniversalMaterial ( );
		
		return wrapper != null ? wrapper.getMaterial ( ) : null;
	}
	
	public UniversalMaterial getAsUniversalMaterial ( ) {
		Object value = getValue ( );
		
		if ( value != null ) {
			return UniversalMaterial.match ( value.toString ( ) );
		} else {
			return UniversalMaterial.match ( default_value.toString ( ) );
		}
	}
	
	public ItemStack getAsItemStack ( ) {
		UniversalMaterial wrapper = getAsUniversalMaterial ( );
		
		return wrapper != null ? wrapper.getItemStack ( ) : null;
	}
	
	@Override
	public void load ( ConfigurationSection section ) {
		Object raw = section.get ( getKey ( ) );
		
		if ( raw != null && ClassReflection.compatibleTypes ( this.type , raw ) ) {
			this.value = raw;
		}
	}
}