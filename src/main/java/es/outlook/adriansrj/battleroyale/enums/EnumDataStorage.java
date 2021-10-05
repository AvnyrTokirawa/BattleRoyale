package es.outlook.adriansrj.battleroyale.enums;

import es.outlook.adriansrj.battleroyale.data.DataStorage;
import es.outlook.adriansrj.battleroyale.data.implementation.DataStorageMongoDB;
import es.outlook.adriansrj.battleroyale.data.implementation.DataStorageMySQL;
import es.outlook.adriansrj.battleroyale.data.implementation.DataStorageSQLite;

/**
 * Enumerates the supported data storage systems.
 *
 * @author AdrianSR / 15/09/2021 / 06:18 p. m.
 */
public enum EnumDataStorage {
	
	MYSQL ( DataStorageMySQL.class ),
	SQLITE ( DataStorageSQLite.class ),
	MONGODB ( DataStorageMongoDB.class );
	
	private final Class < ? extends DataStorage > clazz;
	
	EnumDataStorage ( Class < ? extends DataStorage > clazz ) {
		this.clazz = clazz;
	}
	
	public Class < ? extends DataStorage > getImplementationClass ( ) {
		return clazz;
	}
}
