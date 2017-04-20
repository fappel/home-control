package com.codeaffine.home.control.preference;

import static com.codeaffine.home.control.test.util.preference.PreferenceEventAssert.assertThat;

import org.junit.Test;

public class PreferenceEventTest {

  private static final String ATTRIBUTE_NAME = "attributeName";
  private static final Object NEW_VALUE = new Object();
  private static final Object OLD_VALUE = new Object();
  private static final Object SOURCE = new Object();

  @Test
  public void construct() {
    PreferenceEvent actual = new PreferenceEvent( SOURCE, ATTRIBUTE_NAME, OLD_VALUE, NEW_VALUE );

    assertThat( actual )
      .hasAttributeName( ATTRIBUTE_NAME )
      .hasSource( SOURCE )
      .hasNewValue( NEW_VALUE )
      .hasOldValue( OLD_VALUE );
  }

  @Test
  public void constructWithNullValues() {
    PreferenceEvent actual = new PreferenceEvent( SOURCE, ATTRIBUTE_NAME, null, null );

    assertThat( actual )
      .hasAttributeName( ATTRIBUTE_NAME )
      .hasSource( SOURCE )
      .hasNewValue( null )
      .hasOldValue( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSourceArgument() {
    new PreferenceEvent( null, ATTRIBUTE_NAME, null, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsAttributeNameArgument() {
    new PreferenceEvent( SOURCE, null, null, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithEmptyStrinAsAttributeNameArgument() {
    new PreferenceEvent( SOURCE, "", null, null );
  }
}