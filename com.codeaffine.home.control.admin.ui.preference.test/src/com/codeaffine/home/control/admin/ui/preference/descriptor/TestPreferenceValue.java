package com.codeaffine.home.control.admin.ui.preference.descriptor;

import static java.util.Arrays.asList;

import com.codeaffine.home.control.preference.PreferenceValue;

class TestPreferenceValue implements PreferenceValue<TestPreferenceValue> {

  static final String ONE_REPRESENTATION = "ONE";
  static final String TWO_REPRESENTATION = "TWO";
  static final TestPreferenceValue ONE = new TestPreferenceValue( ONE_REPRESENTATION );
  static final TestPreferenceValue TWO = new TestPreferenceValue( TWO_REPRESENTATION );

  private final String representation;

  public static TestPreferenceValue valueOf( String representation ) {
    return new TestPreferenceValue( representation );
  }

  public static TestPreferenceValue[] values() {
    return asList( ONE, TWO ).stream().sorted().toArray( TestPreferenceValue[]::new );
  }

  @Override
  public int compareTo( TestPreferenceValue other ) {
    return representation.compareTo( other.representation );
  }

  @Override
  public String toString() {
    return representation;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( representation == null ) ? 0 : representation.hashCode() );
    return result;
  }

  @Override
  public boolean equals( Object obj ) {
    if( this == obj )
      return true;
    if( obj == null )
      return false;
    if( getClass() != obj.getClass() )
      return false;
    TestPreferenceValue other = ( TestPreferenceValue )obj;
    if( representation == null ) {
      if( other.representation != null )
        return false;
    } else if( !representation.equals( other.representation ) )
      return false;
    return true;
  }

  private TestPreferenceValue( String representation ) {
    this.representation = representation;
  }
}