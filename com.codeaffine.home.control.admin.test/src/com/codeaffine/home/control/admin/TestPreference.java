package com.codeaffine.home.control.admin;

import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

@Preference
interface TestPreference {

  static final Class<?> ATTRIBUTE_TYPE = int[].class.getComponentType();
  static final String ATTRIBUTE_NAME = "value";
  static final String DEFAULT_VALUE = "2";

  @DefaultValue( DEFAULT_VALUE )
  int getValue();
  void setValue( int value );

  static TestPreference newInstance() {
    return new TestPreference() {

      private int value = Integer.valueOf( DEFAULT_VALUE );

      @Override
      public void setValue( int value ) {
        this.value = value;
      }

      @Override
      public int getValue() {
        return value;
      }
    };
  }
}