package com.codeaffine.home.control.engine.component.preference;

import static java.util.Arrays.asList;

import java.util.stream.Stream;

public class MyPreferenceValue {

  static final String VALUE_ONE_REPRESENTATION = "VALUE_ONE";
  static final String VALUE_TWO_REPRESENTATION = "VALUE_TWO";
  static final MyPreferenceValue VALUE_ONE = new MyPreferenceValue( VALUE_ONE_REPRESENTATION );
  static final MyPreferenceValue VALUE_TWO = new MyPreferenceValue( VALUE_TWO_REPRESENTATION );

  private final String representation;

  @Override
  public String toString() {
    return representation;
  }

  public static MyPreferenceValue valueOf( String representation ) {
    return streamOfValues().filter( value -> representation.equals( value.representation ) ).findFirst().get();
  }

  private MyPreferenceValue( String representation ) {
    this.representation = representation;
  }

  private static Stream<MyPreferenceValue> streamOfValues() {
    return asList( VALUE_ONE, VALUE_TWO ).stream();
  }
}