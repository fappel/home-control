package com.codeaffine.home.control.application.control;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Optional.empty;

import java.util.Optional;

public class Event {

  private final Object source;

  public Event( Object source ) {
    verifyNotNull( source, "source" );

    this.source = source;
  }

  public <T> Optional<T> getSource( Class<T> type ) {
    verifyNotNull( type, "type" );

    if( type.isInstance( source ) ) {
      return Optional.of( type.cast( source ) );
    }
    return empty();
  }
}
