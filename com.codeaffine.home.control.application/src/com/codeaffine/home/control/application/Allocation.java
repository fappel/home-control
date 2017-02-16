package com.codeaffine.home.control.application;

import org.slf4j.LoggerFactory;

import com.codeaffine.home.control.entity.AllocationEvent;
import com.codeaffine.home.control.event.Subscribe;

public class Allocation {

  @Subscribe
  public void allocationChanged( AllocationEvent event ) {
    LoggerFactory.getLogger( Allocation.class ).info( event.getAllocations().toString() );
  }
}