package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupSession;
import es.outlook.adriansrj.battleroyale.battlefield.setup.BattlefieldSetupTool;
import es.outlook.adriansrj.battleroyale.battlefield.setup.tool.*;
import es.outlook.adriansrj.battleroyale.player.Player;
import es.outlook.adriansrj.core.util.reflection.general.ConstructorReflection;

import java.lang.reflect.InvocationTargetException;

/**
 * @author AdrianSR / 29/08/2021 / 11:19 a. m.
 */
public enum EnumBattleMapSetupTool {
	
	SET_BUS_SPAWNS ( BattlefieldSetupToolBusSpawns.class ),
	SET_PLAYER_SPAWNS ( BattlefieldSetupToolPlayerSpawns.class ),
	SET_VEHICLE_SPAWNS ( BattlefieldSetupToolVehicleSpawns.class ),
	SET_NAME ( BattlefieldSetupToolName.class ),
	SET_BOUNDS ( BattlefieldSetupToolBounds.class ),
	SET_LOOT_CHESTS ( BattlefieldSetupToolLootChests.class ),
	
	;
	
	protected final Class < ? extends BattlefieldSetupTool > clazz;
	
	EnumBattleMapSetupTool ( Class < ? extends BattlefieldSetupTool > clazz ) {
		this.clazz = clazz;
	}
	
	public BattlefieldSetupTool getNewInstance ( BattlefieldSetupSession session , Player configurator ) {
		try {
			return ConstructorReflection.newInstance (
					clazz , new Class < ? >[] { BattlefieldSetupSession.class , Player.class } ,
					session , configurator );
		} catch ( InstantiationException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException ex ) {
			ex.printStackTrace ( );
		}
		return null;
	}
}