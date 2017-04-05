package com.codeaffine.home.control.status;

import static com.codeaffine.home.control.internal.ArgumentVerification.verifyNotNull;
import static java.util.Optional.empty;

import java.util.Optional;

public class StatusEvent {

  private final StatusSupplier<?> source;

  public StatusEvent( StatusSupplier<?> source ) {
    verifyNotNull( source, "source" );

    this.source = source;
  }

  public <T extends StatusSupplier<?>> Optional<T> getSource( Class<T> type ) {
    verifyNotNull( type, "type" );

    if( type.isInstance( source ) ) {
      return Optional.of( type.cast( source ) );
    }
    return empty();
  }
}
