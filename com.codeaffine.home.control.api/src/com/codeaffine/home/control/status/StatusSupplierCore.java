package com.codeaffine.home.control.status;

import static com.codeaffine.home.control.internal.ArgumentVerification.verifyNotNull;

import java.util.function.Function;
import java.util.function.Supplier;

import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;

public class StatusSupplierCore<S> implements StatusSupplier<S> {

  private final StatusSupplier<S> statusSupplier;
  private final EventBus eventBus;
  private final Logger logger;

  private S status;

  public StatusSupplierCore( EventBus eventBus, S initialStatus, StatusSupplier<S> statusSupplier, Logger logger ) {
    verifyNotNull( statusSupplier, "statusSupplier" );
    verifyNotNull( initialStatus, "initialStatus" );
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( logger, "logger" );

    this.statusSupplier = statusSupplier;
    this.status = initialStatus;
    this.eventBus = eventBus;
    this.logger = logger;
  }

  public void updateStatus( Supplier<S> newStatusSupplier, String statusInfoPattern ) {
    updateStatus( newStatusSupplier, statusInfoPattern, status -> status );
  }

  public void updateStatus(
    Supplier<S> newStatusSupplier, String statusInfoPattern, Function<S, Object> statusInfoArgumentSupplier )
  {
    verifyNotNull( statusInfoArgumentSupplier, "statusInfoArgumentProvider" );
    verifyNotNull( newStatusSupplier, "newStatusSupplier" );
    verifyNotNull( statusInfoPattern, "statusInfoPattern" );

    S newStatus = newStatusSupplier.get();
    if( !newStatus.equals( status ) ) {
      status = newStatus;
      eventBus.post( new StatusEvent( statusSupplier ) );
      logger.info( statusInfoPattern, statusInfoArgumentSupplier.apply( newStatus ) );
    }
  }

  @Override
  public S getStatus() {
    return status;
  }
}