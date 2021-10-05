package es.outlook.adriansrj.battleroyale.gui.arena;

import es.outlook.adriansrj.battleroyale.gui.GUIIcon;
import es.outlook.adriansrj.battleroyale.gui.GUIIconType;
import es.outlook.adriansrj.battleroyale.gui.arena.icon.ArenaSelectorGUIButtonArena;
import es.outlook.adriansrj.battleroyale.gui.arena.icon.ArenaSelectorGUIButtonLeaveArena;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Arena selector defined {@link GUIIconType}s.
 *
 * @author AdrianSR / 03/10/2021 / 10:19 a. m.
 */
public enum ArenaSelectorGUIIconType implements GUIIconType {
	
	ARENA {
		@Override
		public GUIIcon load ( ConfigurationSection section ) {
			return ArenaSelectorGUIButtonArena.of ( section );
		}
	},
	
	LEAVE_ARENA {
		@Override
		public GUIIcon load ( ConfigurationSection section ) {
			return ArenaSelectorGUIButtonLeaveArena.of ( section );
		}
	};
	
	@Override
	public GUIIcon load ( ConfigurationSection section ) {
		throw new UnsupportedOperationException ( );
	}
}
