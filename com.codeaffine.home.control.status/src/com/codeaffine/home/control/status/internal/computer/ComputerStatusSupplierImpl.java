package com.codeaffine.home.control.status.internal.computer;

import static com.codeaffine.home.control.status.internal.computer.Messages.INFO_COMPUTER_ACTIVITY_STATUS;
import static com.codeaffine.home.control.status.type.OnOff.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.event.Observe;
import com.codeaffine.home.control.event.UpdateEvent;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusSupplierCore;
import com.codeaffine.home.control.status.supplier.ComputerStatusSupplier;
import com.codeaffine.home.control.status.type.OnOff;
import com.codeaffine.home.control.type.DecimalType;

public class ComputerStatusSupplierImpl implements ComputerStatusSupplier {

  static final DecimalType MIN_IDLE_TIME_IN_SECONDS = new DecimalType( 60 );

  private final StatusSupplierCore<OnOff> core;

  public ComputerStatusSupplierImpl( EventBus eventBus, Logger logger ) {
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( logger, "logger" );

    this.core = new StatusSupplierCore<OnOff>( eventBus, OFF, this, logger );
  }

  @Observe( "computerIdleTime" )
  void onUpdate( UpdateEvent<NumberItem, DecimalType> event ) {
    core.updateStatus( () -> calculate( event ), INFO_COMPUTER_ACTIVITY_STATUS );
  }

  @Override
  public OnOff getStatus() {
    return core.getStatus();
  }

  private static OnOff calculate( UpdateEvent<NumberItem, DecimalType> event ) {
    DecimalType computerIdleTime = event.getUpdatedStatus().orElse( MIN_IDLE_TIME_IN_SECONDS );
    return computerIdleTime.compareTo( MIN_IDLE_TIME_IN_SECONDS ) < 0 ? ON : OFF;
  }
}