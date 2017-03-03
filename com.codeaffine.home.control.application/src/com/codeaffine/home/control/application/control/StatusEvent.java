package com.codeaffine.home.control.application.control;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Optional.empty;

import java.util.Optional;

public class StatusEvent {

  private final StatusProvider<?> source;

  public StatusEvent( StatusProvider<?> source ) {
    verifyNotNull( source, "source" );

    this.source = source;
  }

  public <T extends StatusProvider<?>> Optional<T> getSource( Class<T> type ) {
    verifyNotNull( type, "type" );

    if( type.isInstance( source ) ) {
      return Optional.of( type.cast( source ) );
    }
    return empty();
  }
}
