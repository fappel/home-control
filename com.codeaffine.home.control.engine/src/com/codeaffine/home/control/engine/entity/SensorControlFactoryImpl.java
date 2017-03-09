package com.codeaffine.home.control.engine.entity;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.AllocationTracker.SensorControl;
import com.codeaffine.home.control.entity.AllocationTracker.SensorControlFactory;

public class SensorControlFactoryImpl implements SensorControlFactory {

  private final AllocationTrackerImpl zoneProvider;

  public SensorControlFactoryImpl( AllocationTrackerImpl zoneProvider ) {
    this.zoneProvider = zoneProvider;
  }

  @Override
  public <E extends Entity<D>, D extends EntityDefinition<E>> SensorControl create( E zone ) {
    verifyNotNull( zone, "zone" );

    return new SensorControlImpl( zone, zoneProvider );
  }
}