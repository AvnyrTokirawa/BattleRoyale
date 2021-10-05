package es.outlook.adriansrj.battleroyale.gui;

import es.outlook.adriansrj.battleroyale.gui.team.TeamGUIHandler;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Enumerates the default types of icons for a {@link TeamGUIHandler}.
 *
 * @author AdrianSR / 09/08/2021 / Time: 12:08 p. m.
 */
public enum GUIIconTypeDefault implements GUIIconType {
	
	/** @see GUIIconEmpty */
	EMPTY {
		@Override
		public GUIIconEmpty load ( ConfigurationSection section ) {
			return GUIIconEmpty.of ( section );
		}
	},
	
	/** @see GUIButtonNextPage */
	NEXT_BUTTON {
		@Override
		public GUIButtonNextPage load ( ConfigurationSection section ) {
			return GUIButtonNextPage.of ( section );
		}
	},
	
	/** @see GUIButtonBackPage */
	BACK_BUTTON {
		@Override
		public GUIButtonBackPage load ( ConfigurationSection section ) {
			return GUIButtonBackPage.of ( section );
		}
	},
	
	/** @see GUIButtonClose */
	CLOSE_BUTTON {
		@Override
		public GUIButtonClose load ( ConfigurationSection section ) {
			return GUIButtonClose.of ( section );
		}
	},
	
	/** @see GUIButtonGoToPage */
	GO_TO_PAGE_BUTTON {
		@Override
		public GUIButtonGoToPage load ( ConfigurationSection section ) {
			return GUIButtonGoToPage.of ( section );
		}
	},
	
	/** @see GUIIconCustom */
	CUSTOM {
		@Override
		public GUIIconCustom load ( ConfigurationSection section ) {
			return GUIIconCustom.of ( section );
		}
	};
}
