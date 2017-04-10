package com.codeaffine.home.control.status.supplier;

import static com.codeaffine.home.control.status.supplier.Messages.STATUS_INFO_HEARTBEAT;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;

import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusSupplier;
import com.codeaffine.home.control.status.StatusSupplierCore;

public class HeartBeatSupplier implements StatusSupplier<LocalDateTime> {

  private final StatusSupplierCore<LocalDateTime> statusSupplierCore;

  HeartBeatSupplier( EventBus eventBus, Logger logger ) {
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( logger, "logger" );

    statusSupplierCore = new StatusSupplierCore<>( eventBus, now(), this, logger );
  }

  @Override
  public LocalDateTime getStatus() {
    pulse();
    return statusSupplierCore.getStatus();
  }

  @Schedule( period = 5L )
  void pulse() {
    statusSupplierCore.updateStatus( () -> now(), STATUS_INFO_HEARTBEAT );
  }
}
