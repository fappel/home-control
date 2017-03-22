package com.codeaffine.home.control.application;

import com.codeaffine.home.control.event.Observe;
import com.codeaffine.home.control.event.UpdateEvent;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.type.DecimalType;

class ComputerIdleTime {

  private final Logger logger;

  ComputerIdleTime( Logger logger ) {
    this.logger = logger;
  }

  @Observe( "computerIdleTime" )
  void onUpdate( UpdateEvent<NumberItem, DecimalType> event ) {
    logger.info( "computerIdleTime: %s", event.getUpdatedStatus() );
  }
}
