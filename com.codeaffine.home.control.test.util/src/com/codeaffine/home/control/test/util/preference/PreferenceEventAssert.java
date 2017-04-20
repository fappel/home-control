package com.codeaffine.home.control.test.util.preference;

import org.assertj.core.api.AbstractAssert;

import com.codeaffine.home.control.preference.PreferenceEvent;

public class PreferenceEventAssert extends AbstractAssert<PreferenceEventAssert, PreferenceEvent> {

  public PreferenceEventAssert( PreferenceEvent actual ) {
    super( actual, PreferenceEventAssert.class );
  }

  public static PreferenceEventAssert assertThat( PreferenceEvent actual ) {
    return new PreferenceEventAssert( actual );
  }

  public PreferenceEventAssert hasSource( Object expected ) {
    isNotNull();
    if( actual.getSource() != expected ) {
      failWithMessage( "Expected source to be <%s> but was <%s>", expected, actual.getSource() );
    }
    return this;
  }

  public PreferenceEventAssert hasAttributeName( String expected ) {
    isNotNull();
    if( !actual.getAttributeName().equals(  expected ) ) {
      failWithMessage( "Expected attributeName to be <%s> but was <%s>", expected, actual.getAttributeName() );
    }
    return this;
  }

  public PreferenceEventAssert hasOldValue( Object expected ) {
    isNotNull();
    if( hasValue( actual.getOldValue(), expected ) ) {
      failWithMessage( "Expected oldValue to be <%s> but was <%s>", expected, actual.getOldValue() );
    }
    return this;
  }

  public PreferenceEventAssert hasNewValue( Object expected ) {
    isNotNull();
    if( hasValue( actual.getNewValue(), expected ) ) {
      failWithMessage( "Expected newValue to be <%s> but was <%s>", expected, actual.getNewValue() );
    }
    return this;
  }

  private static boolean hasValue( Object actualValue, Object expected ) {
    return    actualValue == null && expected != null
           || actualValue != null && !actualValue.equals( expected );
  }
}