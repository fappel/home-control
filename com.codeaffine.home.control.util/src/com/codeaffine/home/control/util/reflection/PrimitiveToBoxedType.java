package com.codeaffine.home.control.util.reflection;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveToBoxedType {

  private static final Map<String, Class<?>> PRIMITIVE_TYPE_TO_BOX_TYPE_MAP = createPrimitiveTypeToBoxTypeMap();

  public static Class<?> replacePrimitiveTypeByBoxedType( Class<?> type ) {
    if( PRIMITIVE_TYPE_TO_BOX_TYPE_MAP.containsKey( type.getName() ) ) {
      return PRIMITIVE_TYPE_TO_BOX_TYPE_MAP.get( type.getName() );
    }
    return type;
  }

  private static Map<String, Class<?>> createPrimitiveTypeToBoxTypeMap() {
    Map<String, Class<?>> result = new HashMap<>();
    result.put( "int", Integer.class );
    result.put( "long", Long.class );
    result.put( "double", Double.class );
    result.put( "float", Float.class );
    result.put( "boolean", Boolean.class );
    result.put( "byte", Byte.class );
    result.put( "short", Short.class );
    return result;
  }
}