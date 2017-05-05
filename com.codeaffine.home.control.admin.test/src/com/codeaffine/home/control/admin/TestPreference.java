package com.codeaffine.home.control.admin;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

@Preference
interface TestPreference {

  static final Class<?> INT_ATTRIBUTE_TYPE = int[].class.getComponentType();
  static final String INT_ATTRIBUTE_NAME = "intValue";
  static final String INT_DEFAULT_VALUE = "2";
  static final Class<?> MAP_ATTRIBUTE_TYPE = Map.class;
  static final List<Class<?>> MAP_ATTRIBUTE_GENERIC_TYPE_PARAMETERS = asList( String.class, Integer.class );
  static final String MAP_ATTRIBUTE_NAME = "mapValue";
  static final String MAP_DEFAULT_VALUE = "{}";

  @DefaultValue( INT_DEFAULT_VALUE )
  int getIntValue();
  void setIntValue( int value );
  @DefaultValue( MAP_DEFAULT_VALUE )
  Map<String, Integer> getMapValue();
  void setMapValue( Map<String, Integer> mapValue );

  static TestPreference newInstance() {
    return new TestPreference() {

      private int intValue = Integer.valueOf( INT_DEFAULT_VALUE );
      private Map<String, Integer> mapValue = new HashMap<>();

      @Override
      public void setIntValue( int value ) {
        this.intValue = value;
      }

      @Override
      public int getIntValue() {
        return intValue;
      }

      @Override
      public Map<String, Integer> getMapValue() {
        return mapValue;
      }

      @Override
      public void setMapValue( Map<String, Integer> mapValue ) {
        this.mapValue = mapValue;
      }
    };
  }
}