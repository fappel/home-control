package com.codeaffine.home.control.admin.app.mock;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

@Preference interface MockPreference {

  static final String DEFAULT_STRING_VALUE = "String-Value";
  static final String DEFAULT_INT_VALUE = "12";
  static final String DEFAULT_ENUM_VALUE = "ELEMENT_1";
  static final String DEFAULT_BOOLEAN_VALUE = "true";
  static final String DEFAULT_MAP_VALUE = "{ELEMENT_1=4, ELEMENT_2=6}";
  static final String DEFAULT_LIST_VALUE = "{ELEMENT_2, ELEMENT_1}";
  static final String DEFAULT_SET_VALUE = "{ELEMENT_1, ELEMENT_2}";

  @DefaultValue( DEFAULT_BOOLEAN_VALUE )
  boolean isBooleanValue();
  void setBooleanValue( boolean booleanValue );
  @DefaultValue( DEFAULT_INT_VALUE )
  int getIntValue();
  void setIntValue( int value );
  @DefaultValue( DEFAULT_STRING_VALUE )
  String getStringValue();
  void setStringValue( String value );
  @DefaultValue( DEFAULT_ENUM_VALUE )
  EnumType getEnumValue();
  void setEnumValue( EnumType enumValue );
  @DefaultValue( DEFAULT_MAP_VALUE )
  Map<EnumType, Integer> getMapValue();
  void setMapValue( Map<EnumType, Integer> value );
  @DefaultValue( DEFAULT_LIST_VALUE )
  List<EnumType> getListValue();
  void setListValue( List<EnumType> value );
  @DefaultValue( DEFAULT_SET_VALUE )
  Set<EnumType> getSetValue();
  void setSetValue( Set<EnumType> value );
}