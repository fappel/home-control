package com.codeaffine.home.control.application.control;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.function.Function;
import java.util.function.Supplier;

import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;

public class StatusProviderCore<S> implements StatusProvider<S> {

  private final StatusProvider<S> statusProvider;
  private final EventBus eventBus;
  private final Logger logger;

  private S status;

  public StatusProviderCore( EventBus eventBus, S initialStatus, StatusProvider<S> statusProvider, Logger logger ) {
    verifyNotNull( statusProvider, "statusProvider" );
    verifyNotNull( initialStatus, "initialStatus" );
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( logger, "logger" );

    this.statusProvider = statusProvider;
    this.status = initialStatus;
    this.eventBus = eventBus;
    this.logger = logger;
  }

  public void updateStatus( Supplier<S> newStatusSupplier, String statusInfoPattern ) {
    updateStatus( newStatusSupplier, statusInfoPattern, status -> status );
  }

  public void updateStatus(
    Supplier<S> newStatusSupplier, String statusInfoPattern, Function<S, Object> statusInfoArgumentProvider )
  {
    verifyNotNull( statusInfoArgumentProvider, "statusInfoArgumentProvider" );
    verifyNotNull( newStatusSupplier, "newStatusSupplier" );
    verifyNotNull( statusInfoPattern, "statusInfoPattern" );

    S newStatus = newStatusSupplier.get();
    if( !newStatus.equals( status ) ) {
      status = newStatus;
      eventBus.post( new StatusEvent( statusProvider ) );
      logger.info( statusInfoPattern, statusInfoArgumentProvider.apply( newStatus ) );
    }
  }

  @Override
  public S getStatus() {
    return status;
  }
}