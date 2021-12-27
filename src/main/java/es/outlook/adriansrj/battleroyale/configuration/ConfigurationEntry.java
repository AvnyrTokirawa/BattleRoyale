package es.outlook.adriansrj.battleroyale.configuration;

import es.outlook.adriansrj.battleroyale.util.reflection.ClassReflection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author AdrianSR / 22/08/2021 / Time: 09:19 p. m.
 */
public interface ConfigurationEntry {
	
	String getKey ( );
	
	String getComment ( );
	
	Object getDefaultValue ( );
	
	Object getValue ( );
	
	Class < ? > getValueType ( );
	
	default String getAsString ( ) {
		if ( String.class.isAssignableFrom ( getValueType ( ) ) ) {
			return getValue ( ) instanceof String ? ( ( String ) getValue ( ) ) : ( ( String ) getDefaultValue ( ) );
		} else {
			throw new UnsupportedOperationException ( "This configuration entry is not a String!" );
		}
	}
	
	default double getAsDouble ( ) {
		if ( Number.class.isAssignableFrom ( getValueType ( ) ) ) {
			return getValue ( ) instanceof Number ? ( ( Number ) getValue ( ) ).doubleValue ( )
					: ( ( Number ) getDefaultValue ( ) ).doubleValue ( );
		} else {
			throw new UnsupportedOperationException ( "This configuration entry is not a number!" );
		}
	}
	
	default int getAsInteger ( ) {
		if ( Number.class.isAssignableFrom ( getValueType ( ) ) ) {
			return getValue ( ) instanceof Number ? ( ( Number ) getValue ( ) ).intValue ( )
					: ( ( Number ) getDefaultValue ( ) ).intValue ( );
		} else {
			throw new UnsupportedOperationException ( "This configuration entry is not a number!" );
		}
	}
	
	default long getAsLong ( ) {
		if ( Number.class.isAssignableFrom ( getValueType ( ) ) ) {
			return getValue ( ) instanceof Number ? ( ( Number ) getValue ( ) ).longValue ( )
					: ( ( Number ) getDefaultValue ( ) ).longValue ( );
		} else {
			throw new UnsupportedOperationException ( "This configuration entry is not a number!" );
		}
	}
	
	default boolean getAsBoolean ( ) {
		if ( Boolean.class.isAssignableFrom ( this.getValueType ( ) ) ) {
			return getValue ( ) instanceof Boolean ? ( Boolean ) getValue ( ) : ( Boolean ) getDefaultValue ( );
		} else {
			throw new UnsupportedOperationException ( "This configuration entry is not a boolean!" );
		}
	}
	
	default < T extends Enum < T > > T getAsEnum ( Class < T > clazz ) {
		Object value = getValue ( );
		
		if ( value != null ) {
			if ( value.getClass ( ).isEnum ( ) ) {
				return Enum.valueOf ( clazz , ( ( Enum < ? > ) getValue ( ) ).name ( ) );
			} else {
				return Enum.valueOf ( clazz , value.toString ( ) );
			}
		} else {
			return null;
		}
	}
	
	//	public default < T extends Enum < T > > T getAsEnum ( Class < T > clazz ) {
	//		return Enum.valueOf ( clazz , ( ( Enum < ? > ) getValue ( ) ).name ( ) );
	//	}
	
	default List < ? > getAsList ( ) {
		if ( List.class.isAssignableFrom ( this.getValueType ( ) ) ) {
			return ( List < ? > ) getValue ( );
		} else {
			throw new UnsupportedOperationException ( "This configuration entry is not a list!" );
		}
	}
	
	default List < String > getAsStringList ( ) {
		List < ? >      raw    = getAsList ( );
		List < String > result = new ArrayList <> ( );
		
		for ( Object obj : raw ) {
			if ( obj instanceof String || ClassReflection.isPrimitiveWrapper ( obj ) ) {
				result.add ( String.valueOf ( obj ) );
			}
		}
		
		return result;
	}
	
	default List < Integer > getAsIntegerList ( ) {
		List < ? >       list   = getAsList ( );
		List < Integer > result = new ArrayList < Integer > ( );
		
		for ( Object object : list ) {
			if ( object instanceof Integer ) {
				result.add ( ( Integer ) object );
			} else if ( object instanceof String ) {
				try {
					result.add ( Integer.valueOf ( ( String ) object ) );
				} catch ( Exception ex ) {
				}
			} else if ( object instanceof Character ) {
				result.add ( ( int ) ( ( Character ) object ).charValue ( ) );
			} else if ( object instanceof Number ) {
				result.add ( ( ( Number ) object ).intValue ( ) );
			}
		}
		
		return result;
	}
	
	default List < Boolean > getAsBooleanList ( ) {
		List < ? >       list   = getAsList ( );
		List < Boolean > result = new ArrayList < Boolean > ( );
		
		for ( Object object : list ) {
			if ( object instanceof Boolean ) {
				result.add ( ( Boolean ) object );
			} else if ( object instanceof String ) {
				if ( Boolean.TRUE.toString ( ).equals ( object ) ) {
					result.add ( true );
				} else if ( Boolean.FALSE.toString ( ).equals ( object ) ) {
					result.add ( false );
				}
			}
		}
		
		return result;
	}
	
	default List < Double > getAasDoubleList ( ) {
		List < ? >      list   = getAsList ( );
		List < Double > result = new ArrayList < Double > ( );
		
		for ( Object object : list ) {
			if ( object instanceof Double ) {
				result.add ( ( Double ) object );
			} else if ( object instanceof String ) {
				try {
					result.add ( Double.valueOf ( ( String ) object ) );
				} catch ( Exception ex ) {
				}
			} else if ( object instanceof Character ) {
				result.add ( ( double ) ( ( Character ) object ).charValue ( ) );
			} else if ( object instanceof Number ) {
				result.add ( ( ( Number ) object ).doubleValue ( ) );
			}
		}
		
		return result;
	}
	
	default List < Float > getAsFloatList ( ) {
		List < ? >     list   = getAsList ( );
		List < Float > result = new ArrayList < Float > ( );
		
		for ( Object object : list ) {
			if ( object instanceof Float ) {
				result.add ( ( Float ) object );
			} else if ( object instanceof String ) {
				try {
					result.add ( Float.valueOf ( ( String ) object ) );
				} catch ( Exception ex ) {
				}
			} else if ( object instanceof Character ) {
				result.add ( ( float ) ( Character ) object );
			} else if ( object instanceof Number ) {
				result.add ( ( ( Number ) object ).floatValue ( ) );
			}
		}
		
		return result;
	}
	
	default List < Long > getAsLongList ( ) {
		List < ? >    list   = getAsList ( );
		List < Long > result = new ArrayList < Long > ( );
		
		for ( Object object : list ) {
			if ( object instanceof Long ) {
				result.add ( ( Long ) object );
			} else if ( object instanceof String ) {
				try {
					result.add ( Long.valueOf ( ( String ) object ) );
				} catch ( Exception ex ) {
				}
			} else if ( object instanceof Character ) {
				result.add ( ( long ) ( ( Character ) object ).charValue ( ) );
			} else if ( object instanceof Number ) {
				result.add ( ( ( Number ) object ).longValue ( ) );
			}
		}
		
		return result;
	}
	
	default List < Byte > getAsByteList ( ) {
		List < ? >    list   = getAsList ( );
		List < Byte > result = new ArrayList < Byte > ( );
		
		for ( Object object : list ) {
			if ( object instanceof Byte ) {
				result.add ( ( Byte ) object );
			} else if ( object instanceof String ) {
				try {
					result.add ( Byte.valueOf ( ( String ) object ) );
				} catch ( Exception ex ) {
				}
			} else if ( object instanceof Character ) {
				result.add ( ( byte ) ( ( Character ) object ).charValue ( ) );
			} else if ( object instanceof Number ) {
				result.add ( ( ( Number ) object ).byteValue ( ) );
			}
		}
		
		return result;
	}
	
	default List < Character > getAsCharacterList ( ) {
		List < ? >         list   = getAsList ( );
		List < Character > result = new ArrayList < Character > ( );
		
		for ( Object object : list ) {
			if ( object instanceof Character ) {
				result.add ( ( Character ) object );
			} else if ( object instanceof String ) {
				String str = ( String ) object;
				
				if ( str.length ( ) == 1 ) {
					result.add ( str.charAt ( 0 ) );
				}
			} else if ( object instanceof Number ) {
				result.add ( ( char ) ( ( Number ) object ).intValue ( ) );
			}
		}
		
		return result;
	}
	
	default List < Short > getAsShortList ( ) {
		List < ? >     list   = getAsList ( );
		List < Short > result = new ArrayList < Short > ( );
		
		for ( Object object : list ) {
			if ( object instanceof Short ) {
				result.add ( ( Short ) object );
			} else if ( object instanceof String ) {
				try {
					result.add ( Short.valueOf ( ( String ) object ) );
				} catch ( Exception ex ) {
				}
			} else if ( object instanceof Character ) {
				result.add ( ( short ) ( ( Character ) object ).charValue ( ) );
			} else if ( object instanceof Number ) {
				result.add ( ( ( Number ) object ).shortValue ( ) );
			}
		}
		
		return result;
	}
	
	default List < Map < ?, ? > > getAsMapList ( ) {
		List < ? >            list   = getAsList ( );
		List < Map < ?, ? > > result = new ArrayList < Map < ?, ? > > ( );
		
		if ( list == null ) {
			return result;
		}
		
		for ( Object object : list ) {
			if ( object instanceof Map ) {
				result.add ( ( Map < ?, ? > ) object );
			}
		}
		
		return result;
	}
	
	void load ( ConfigurationSection section );
}