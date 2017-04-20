package com.codeaffine.home.control.preference;

import static com.codeaffine.home.control.internal.ArgumentVerification.*;
import static com.codeaffine.home.control.preference.Messages.ERROR_ARGUMENT_ATTRIBUTE_NAME_MUST_NOT_BE_EMPTY;

public class PreferenceEvent {

  private final String attributeName;
  private final Object oldValue;
  private final Object newValue;
  private final Object source;

  public PreferenceEvent( Object source, String attributeName, Object oldValue, Object newValue ) {
    verifyNotNull( source, "source" );
    verifyNotNull( attributeName, "attributeName" );
    verifyCondition( !attributeName.isEmpty(), ERROR_ARGUMENT_ATTRIBUTE_NAME_MUST_NOT_BE_EMPTY );

    this.attributeName = attributeName;
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.source = source;
  }

  public Object getNewValue() {
    return newValue;
  }

  public Object getOldValue() {
    return oldValue;
  }

  public Object getSource() {
    return source;
  }

  public String getAttributeName() {
    return attributeName;
  }
}