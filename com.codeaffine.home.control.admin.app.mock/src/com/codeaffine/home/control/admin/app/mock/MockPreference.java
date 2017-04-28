package com.codeaffine.home.control.admin.app.mock;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

@Preference interface MockPreference {

  static final String DEFAULT_STRING_VALUE = "String-Value";
  static final String DEFAULT_INT_VALUE = "12";
  static final String DEFAULT_ENUM_VALUE = "ELEMENT_1";
  static final String DEFAULT_BOOLEAN_VALUE = "true";

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
}