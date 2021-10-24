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
 * Compass configuration entries in an enumeration.
 *
 * @author AdrianSR / 09/09/2021 / 05:04 p. m.
 */
public enum EnumSettingsGUIsConfiguration implements ConfigurationEntry {
	
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
	
	// -------- main settings gui
	MAIN_GUI_TITLE ( "main.title" ,
					 "main settings gui title" ,
					 ChatColor.BLACK + "My Settings" ),
	
	MAIN_GUI_CLOSE_BUTTON_POSITION (
			"main.button.close.position" ,
			"position of the button to close the gui" ,
			26 ),
	
	MAIN_GUI_BUS_SETTINGS_BUTTON_POSITION (
			"main.button.bus-settings.position" ,
			"position of the button to open the bus settings gui" ,
			12 ),
	
	MAIN_GUI_BUS_SETTINGS_BUTTON_TEXT (
			"main.button.bus-settings.display-text" ,
			"display text of the button to open the bus settings gui" ,
			ChatColor.GOLD + "Bus Settings" ),
	
	MAIN_GUI_BUS_SETTINGS_BUTTON_MATERIAL (
			"main.button.bus-settings.material" ,
			"material of the button to open the bus settings gui" ,
			UniversalMaterial.MINECART ),
	
	MAIN_GUI_PARACHUTE_SETTINGS_BUTTON_POSITION (
			"main.button.parachute-settings.position" ,
			"open parachute settings button: position" ,
			14 ),
	
	MAIN_GUI_PARACHUTE_SETTINGS_BUTTON_TEXT (
			"main.button.parachute-settings.display-text" ,
			"open parachute settings button display text" ,
			ChatColor.GOLD + "Parachute Settings" ),
	
	MAIN_GUI_PARACHUTE_SETTINGS_BUTTON_MATERIAL (
			"main.button.parachute-settings.material" ,
			"open parachute settings button: material" ,
			UniversalMaterial.SADDLE ),
	
	// -------- bus settings gui
	BUS_GUI_TITLE ( "bus-settings.title" ,
					"bus settings gui title" ,
					ChatColor.BLACK + "Available Buses" ),
	
	BUS_GUI_SIZE ( "bus-settings.size" ,
				   "bus settings gui size" , ItemMenuSize.THREE_LINE ),
	
	BUS_GUI_ITEM_UNLOCKED_TEXT_FORMAT (
			"bus-settings.bus-item.unlocked-text-format" ,
			"bus settings gui: bus items display text format when unlocked" +
					"\n%s will be replaced by the name of the bus" ,
			ChatColor.GOLD + "%s" ),
	
	BUS_GUI_ITEM_UNLOCKED_DESCRIPTION_FORMAT (
			"bus-settings.bus-item.unlocked-description-format" ,
			"bus settings gui: bus items description format when unlocked" ,
			"" ,
			ChatColor.GREEN + "Unlocked" ,
			"" ,
			ChatColor.GREEN + "Click to use" ),
	
	BUS_GUI_ITEM_LOCKED_TEXT_FORMAT (
			"bus-settings.bus-item.unlocked-display-text-format" ,
			"bus settings gui: bus items display text format when locked" +
					"\n%s will be replaced by the name of the bus" ,
			ChatColor.DARK_GRAY + "%s" ),
	
	BUS_GUI_ITEM_LOCKED_DESCRIPTION_FORMAT (
			"bus-settings.bus-item.locked-description-format" ,
			"bus settings gui: bus items description format when unlocked" ,
			"" ,
			ChatColor.DARK_RED + "Locked" ,
			"" ,
			ChatColor.DARK_GRAY + "You can buy this bus" ,
			ChatColor.DARK_GRAY + "in the cosmetics shop" ),
	
	BUS_GUI_ITEM_SELECTED_MESSAGE (
			"bus-settings.bus-item.selected-message" ,
			"bus settings gui: message players will receive when" +
					"\nselecting to use a bus" ,
			ChatColor.GREEN + "Bus settings saved!" ),
	
	BUS_GUI_ITEM_LOCKED_MESSAGE (
			"bus-settings.bus-item.locked-message" ,
			"bus settings gui: message players will receive when" +
					"\ntrying to use a locked bus" ,
			ChatColor.DARK_RED + "You don't have permissions to use this bus!" ),
	
	// -------- parachute settings main gui
	PARACHUTE_MAIN_GUI_TITLE ( "parachute-settings-main.title" ,
							   "parachute settings gui title" ,
							   ChatColor.BLACK + "Parachute Settings" ),
	
	PARACHUTE_MAIN_GUI_SIZE ( "parachute-settings-main.size" ,
							  "parachute settings gui size" , ItemMenuSize.THREE_LINE ),
	
	PARACHUTE_MAIN_GUI_PARACHUTE_BUTTON_TEXT (
			"parachute-settings-main.button.display-text" ,
			"button to open parachute settings: display text" ,
			ChatColor.GOLD + "All Parachutes" ),
	
	PARACHUTE_MAIN_GUI_PARACHUTE_BUTTON_POSITION (
			"parachute-settings-main.button.position" ,
			"button to open parachute settings: position" ,
			12 ),
	
	PARACHUTE_MAIN_GUI_PARACHUTE_COLOR_BUTTON_TEXT (
			"parachute-settings-main.button.color.display-text" ,
			"button to open parachute color settings: display text" ,
			ChatColor.GOLD + "Parachute Colors" ),
	
	PARACHUTE_MAIN_GUI_PARACHUTE_COLOR_BUTTON_POSITION (
			"parachute-settings-main.button.color.position" ,
			"button to open parachute color settings: position" ,
			14 ),
	
	// -------- parachute settings gui
	PARACHUTE_GUI_TITLE ( "parachute-settings.title" ,
						  "parachute settings gui title" ,
						  ChatColor.BLACK + "Available Parachutes" ),
	
	PARACHUTE_GUI_SIZE ( "parachute-settings.size" ,
						 "parachute settings gui size" , ItemMenuSize.THREE_LINE ),
	
	PARACHUTE_GUI_ITEM_UNLOCKED_TEXT_FORMAT (
			"parachute-settings.parachute-item.unlocked-text-format" ,
			"parachute settings gui: parachute items display text format when unlocked" +
					"\nthe '%s' will be replaced by the name of the parachute" ,
			ChatColor.GOLD + "%s" ),
	
	PARACHUTE_GUI_ITEM_UNLOCKED_DESCRIPTION_FORMAT (
			"parachute-settings.parachute-item.unlocked-description-format" ,
			"parachute settings gui: parachute items description format when unlocked" ,
			"" ,
			ChatColor.GREEN + "Unlocked" ,
			"" ,
			ChatColor.GREEN + "Click to use" ),
	
	PARACHUTE_GUI_ITEM_LOCKED_TEXT_FORMAT (
			"parachute-settings.parachute-item.locked-display-text-format" ,
			"parachute settings gui: parachute items display text format when locked" +
					"\nthe '%s' will be replaced by the name of the parachute" ,
			ChatColor.DARK_GRAY + "%s" ),
	
	PARACHUTE_GUI_ITEM_LOCKED_DESCRIPTION_FORMAT (
			"parachute-settings.parachute-item.locked-description-format" ,
			"parachute settings gui: parachute items description format when locked" ,
			"" ,
			ChatColor.DARK_RED + "Locked" ,
			"" ,
			ChatColor.DARK_GRAY + "You can buy this parachute" ,
			ChatColor.DARK_GRAY + "in the cosmetics shop" ),
	
	PARACHUTE_GUI_ITEM_SELECTED_MESSAGE (
			"parachute-settings.parachute-item.selected-message" ,
			"parachute settings gui: message players will receive when" +
					"\nselecting a parachute" ,
			ChatColor.GREEN + "Parachute settings saved!" ),
	
	PARACHUTE_GUI_ITEM_LOCKED_MESSAGE (
			"parachute-settings.parachute-item.locked-message" ,
			"parachute settings gui: message players will receive when" +
					"\ntrying to use a locked parachute" ,
			ChatColor.DARK_RED + "You don't have permissions to use this parachute!" ),
	
	// -------- parachute color settings gui
	PARACHUTE_COLOR_GUI_TITLE ( "parachute-color-settings.title" ,
								"parachute color settings gui title" ,
								ChatColor.BLACK + "Available Parachute Colors" ),
	
	PARACHUTE_COLOR_GUI_SIZE ( "parachute-color-settings.size" ,
							   "parachute color settings gui size" , ItemMenuSize.THREE_LINE ),
	
	PARACHUTE_COLOR_GUI_ITEM_UNLOCKED_TEXT_FORMAT (
			"parachute-color-settings.color-item.unlocked-text-format" ,
			"parachute color settings gui: parachute color items display text format when unlocked" +
					"\nthe first '%s' will be replaced by the parachute color" +
					"\nthe second '%s' will be replaced by the name of the parachute color" ,
			"%s%s" ),
	
	PARACHUTE_COLOR_GUI_ITEM_UNLOCKED_DESCRIPTION_FORMAT (
			"parachute-color-settings.color-item.unlocked-description-format" ,
			"parachute color settings gui: parachute color items description format when unlocked" ,
			"" ,
			ChatColor.GREEN + "Unlocked" ,
			"" ,
			ChatColor.GREEN + "Click to use" ),
	
	PARACHUTE_COLOR_GUI_ITEM_LOCKED_TEXT_FORMAT (
			"parachute-color-settings.color-item.locked-display-text-format" ,
			"parachute color settings gui: parachute color items display text format when locked" +
					"\nthe first '%s' will be replaced by the parachute color" +
					"\nthe second '%s' will be replaced by the name of the parachute color" ,
			"%s%s" ),
	
	PARACHUTE_COLOR_GUI_ITEM_LOCKED_DESCRIPTION_FORMAT (
			"parachute-color-settings.color-item.locked-description-format" ,
			"parachute color settings gui: parachute color items description format when locked" ,
			"" ,
			ChatColor.DARK_RED + "Locked" ,
			"" ,
			ChatColor.DARK_GRAY + "You can buy this parachute" ,
			ChatColor.DARK_GRAY + "color in the cosmetics shop" ),
	
	PARACHUTE_COLOR_GUI_ITEM_SELECTED_MESSAGE (
			"parachute-color-settings.color-item.selected-message" ,
			"parachute color settings gui: message players will receive when" +
					"\nselecting a parachute color" ,
			ChatColor.GREEN + "Parachute settings saved!" ),
	
	PARACHUTE_COLOR_GUI_ITEM_LOCKED_MESSAGE (
			"parachute-color-settings.color-item.locked-message" ,
			"parachute color settings gui: message players will receive when" +
					"\ntrying to use a locked parachute color" ,
			ChatColor.DARK_RED + "You don't have permissions to use this color!" ),
	;
	
	private final String      key;
	private final String      comment;
	private final Object      default_value;
	private final Class < ? > type;
	private       Object      value;
	
	EnumSettingsGUIsConfiguration ( String key , String comment , Object default_value , Class < ? > type ) {
		this.key           = key;
		this.comment       = comment;
		this.default_value = default_value instanceof String ? StringEscapeUtils.escapeJava (
				StringUtil.untranslateAlternateColorCodes ( ( String ) default_value ) ) : default_value;
		this.value         = default_value;
		this.type          = type;
	}
	
	EnumSettingsGUIsConfiguration ( String key , String comment , Object default_value ) {
		this ( key , comment , default_value , default_value.getClass ( ) );
		
	}
	
	EnumSettingsGUIsConfiguration ( String key , String comment , Enum < ? > default_value ) {
		// must set value type to string, so we can
		// match the right value later on.
		this ( key , comment , default_value.name ( ) , String.class );
	}
	
	// multi-line string
	EnumSettingsGUIsConfiguration ( String key , String comment , String... default_value ) {
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